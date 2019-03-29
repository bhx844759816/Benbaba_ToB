package com.benbaba.tob.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.benbaba.tob.Constants;
import com.benbaba.tob.R;
import com.benbaba.tob.adapter.DeviceAdapter;
import com.benbaba.tob.model.DeviceModel;
import com.benbaba.tob.model.DrumBean;
import com.benbaba.tob.utils.DeviceManager;
import com.bhx.common.utils.LogUtils;
import com.bhx.common.utils.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 搜索附近得设备 并配置家庭网络
 */
public class SearchDeviceActivity extends AppCompatActivity {

    @BindView(R.id.id_search_recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.id_search_device)
    Button mSearchDevice;
    @BindView(R.id.id_search_wifiName)
    TextView mSearchWifiName;
    @BindView(R.id.id_search_wifiPsd)
    TextView mSearchWifiPsd;
    private String mWifiName;
    private String mWifiPsd;
    private DeviceManager mDeviceManager;
    private State mCurrentState = State.DEFAULT;
    private List<DeviceModel> mDeviceList;
    private DeviceAdapter mAdapter;
    private int mSetUpIndex;
    private EditText mWifiNameEditText;
    private EditText mWifiPsdEditText;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private UIHandler mHandler = new UIHandler(SearchDeviceActivity.this);

    private static class UIHandler extends Handler {
        private WeakReference<SearchDeviceActivity> weakReference;

        UIHandler(SearchDeviceActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SearchDeviceActivity activity = weakReference.get();
            if (activity == null) {
                return;
            }
            ScanResult result;
            switch (msg.what) {
                case 0x01://配置失败
                    result = (ScanResult) msg.obj;
                    activity.settingResult(result, 1);
                    activity.settingDevice();
                    break;
                case 0x02://配置成功
                    result = (ScanResult) msg.obj;
                    activity.settingResult(result, 0);
                    activity.settingDevice();
                    break;

            }
            super.handleMessage(msg);
        }
    }

    public void settingResult(ScanResult result, int settingReult) {
        for (DeviceModel model : mDeviceList) {
            if (model.getResult().equals(result)) {
                LogUtils.i("settingResult model=="+model.getSettingResult());
                model.setSettingResult(settingReult);
                break;
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_device);
        ButterKnife.bind(this);
        requestWifiPermission();
        //检查wifi所需要得权限
        initView();
    }

    private void initView() {
        mDeviceList = new ArrayList<>();
        mDeviceManager = new DeviceManager(this);
        mAdapter = new DeviceAdapter(this, mDeviceList);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void requestWifiPermission() {
        Disposable disposable = new RxPermissions(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            if (locManager != null && !locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                Intent intent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(intent, 0x01); // 设置完成后返回到原来的界面
                            }
                        }
                    }
                });
        compositeDisposable.add(disposable);
    }

    @OnClick(R.id.id_search_device)
    public void onViewClicked() {
        switch (mCurrentState) {
            case DEFAULT: // 默认状态
                mSearchDevice.setText("选择设备");
                mCurrentState = State.SELECT_STATE;
                startSearchDevice();
                break;
            case SETUP_STATE:
                List<DeviceModel> list = getSelectDeviceList();
                if (list.isEmpty()) {
                    ToastUtils.toastShort("请至少选择一个设备进行配置");
                    return;
                }
                if (TextUtils.isEmpty(mWifiName) && TextUtils.isEmpty(mWifiPsd)) {
                    showInputWifiDialog();
                    ToastUtils.toastShort("请选择配置得家庭WIFI");
                    return;
                }
                mDeviceList.clear();
                mDeviceList.addAll(list);
                mAdapter.notifyDataSetChanged();
                settingDevice();

                break;
            case SELECT_STATE:
                showStopSearchDialog();
                break;
        }
    }

    /**
     * 设置玩具鼓wifi
     */
    public void settingDevice() {
        LogUtils.i("settingDevice:" + mSetUpIndex);
        if (mSetUpIndex >= mDeviceList.size()) {
            ToastUtils.toastShort("配置完成");
            //切换到选择得网络
            mSearchDevice.setText("开始搜索");
            mCurrentState = State.DEFAULT;
            int success = getSettingSuccessNums();
            showSettingFinishDialog(success, mDeviceList.size() - success);
            return;
        }
        DeviceModel model = mDeviceList.get(mSetUpIndex);
        //判断是正在配置得哪个
        model.setStartSetting(true);
        mAdapter.notifyDataSetChanged();
        mSetUpIndex++;
        mDeviceManager.setUpScanResult(model.getResult(), Constants.DEVICE_WIFI_PSD,
                new DeviceManager.OnSendWifiToDeviceCallBack() {
                    @Override
                    public void connectResult(ScanResult result, boolean isConnectSuccess) {
                        if (!isConnectSuccess) {
                            LogUtils.i("settingDevice isConnectSuccess: error");
                            //连接失败
                            sendMsg(0x01, result);
                        }
                    }

                    @Override
                    public void settingResult(ScanResult result, DrumBean bean) {
                        if (bean == null) {
                            sendMsg(0x01, result);
                        } else {
                            sendMsg(0x02, result);
                        }
                    }
                }
        );
    }

    /**
     * 获取配置成功的个数
     *
     * @return
     */
    private int getSettingSuccessNums() {
        int nums = 0;
        for (DeviceModel model : mDeviceList) {
            if (model.getSettingResult() == 0) {
                nums++;
            }
        }
        return nums;
    }

    /**
     * 弹出配置完成的Dialog
     **/
    private void showSettingFinishDialog(int sizeNum, int errorNum) {
        new AlertDialog.Builder(this)
                .setTitle("配置完成")
                .setMessage("成功配置玩具鼓" + sizeNum + "个,失败" + errorNum + "个")
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                    if (errorNum > 0) {
                        //弹出是否需要重新搜索配置
                        ToastUtils.toastShort("请点击开始搜索重新配置");
                    }
                })
                .setPositiveButton("确定", (dialog, which) -> {
                    //跳转到具体的鼓列表界面
                    dialog.dismiss();
                })
                .create()
                .show();
    }

    /**
     * 发送消息到Handler
     *
     * @param what
     * @param obj
     */
    private void sendMsg(int what, Object obj) {
        LogUtils.i("sendMsg");
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        mHandler.sendMessage(msg);
    }

    /**
     * 获取所有选择得玩具鼓
     *
     * @return
     */
    private List<DeviceModel> getSelectDeviceList() {
        List<DeviceModel> list = new ArrayList<>();
        for (DeviceModel model : mDeviceList) {
            if (model.isSelect()) {
                model.setVisibility(false);
                list.add(model);
            }
        }
        return list;
    }

    /**
     * 展示输入家庭wifi得对话框
     */
    private void showInputWifiDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.view_input_wifi, null, false);
        mWifiNameEditText = view.findViewById(R.id.id_input_wifi_name);
        mWifiPsdEditText = view.findViewById(R.id.id_input_wifi_psd);
        new AlertDialog.Builder(this)
                .setTitle("选择家庭wifi")
                .setView(view)
                .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("确定", (dialog, which) -> {
                    mWifiName = mWifiNameEditText.getText().toString().trim();
                    mWifiPsd = mWifiPsdEditText.getText().toString().trim();
                    mSearchWifiName.setText(mWifiName);
                    mSearchWifiPsd.setText(mWifiPsd);
                }).show();
    }

    /**
     * 是否停止搜索
     */
    private void showStopSearchDialog() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("是否停止搜索设备")
                .setNegativeButton("取消", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("确定", (dialog, which) -> {
                    mDeviceManager.stopSearchWifi();
                    mSearchDevice.setText("设置设备");
                    mCurrentState = State.SETUP_STATE;
                    //将所有得设备都设置显示出选择框
                    for (DeviceModel model : mDeviceList) {
                        model.setVisibility(true);
                    }
                    mAdapter.notifyDataSetChanged();
                })
                .create()
                .show();
    }

    /**
     * 开始搜索设备
     */
    private void startSearchDevice() {
        mDeviceManager.startSearch(results -> {
            mDeviceList.clear();
            for (ScanResult result : results) {
                if (result.SSID.replace("\"", "").equalsIgnoreCase(Constants.DEVICE_WIFI_SSID)) {
                    DeviceModel model = new DeviceModel();
                    model.setResult(result);
                    mDeviceList.add(model);
                }
            }
            mAdapter.notifyDataSetChanged();
        });
    }

    enum State {
        DEFAULT, SELECT_STATE, SETUP_STATE
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
}
