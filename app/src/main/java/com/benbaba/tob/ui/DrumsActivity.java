package com.benbaba.tob.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.benbaba.tob.Constants;
import com.benbaba.tob.R;
import com.benbaba.tob.adapter.DrumItemAdapter;
import com.benbaba.tob.adapter.GroupDrumItemAdapter;
import com.benbaba.tob.bean.StudentDrum;
import com.benbaba.tob.dialog.DrumInfoFragment;
import com.benbaba.tob.service.SocketService;
import com.bhx.common.base.BaseActivity;
import com.bhx.common.utils.LogUtils;
import com.bhx.common.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.benbaba.tob.Constants.tones;

public class DrumsActivity extends BaseActivity implements DrumItemAdapter.OnDrumClickListener, DrumInfoFragment.OnFragmentDrumListener, GroupDrumItemAdapter.OnCBClickListener {

    @BindView(R.id.rvDrums)
    RecyclerView rvDrums;
    @BindView(R.id.llGroup)
    LinearLayout llGroup;
    @BindView(R.id.mask)
    View mask;
    @BindView(R.id.rvGroupDrum)
    RecyclerView rvGroupDrum;

    private List<StudentDrum> studentDrumList = new ArrayList<>();
    private List<StudentDrum> clickDrumList = new ArrayList<>();
    private ServiceConnection connection;
    private Boolean isBind = false;
    private SocketService service;
    private int clickIndex = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_drums;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drums);
        ButterKnife.bind(this);
        initView();
        initData();

        initService();
        bind();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        //接管点击事件，防止点击穿透
        mask.setOnTouchListener((v, event) -> {
            if (llGroup.getVisibility() == View.VISIBLE&&clickIndex==0){
                groupOutAnim();
                clickIndex++;
            }
            return true;
        });
        llGroup.setOnTouchListener((v, event) -> true);
    }

    private void initService() {
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                isBind = true;
                SocketService.SocketBinder socketBinder = (SocketService.SocketBinder) binder;
                service = socketBinder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBind = false;
            }
        };
    }

    private void initData() {
        for (int i = 0; i < 20; i++) {
            StudentDrum studentDrum = new StudentDrum();
            studentDrum.setName("name" + i);
            studentDrum.setElectricity(50);
            studentDrum.setMissNum(0);
            studentDrum.setSuccessNum(1);
            if (i % 2 == 1) {
                studentDrum.setOnLine(true);
            } else {
                studentDrum.setOnLine(false);
            }
            studentDrum.setVolume(10);
            studentDrumList.add(studentDrum);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 6);
        DrumItemAdapter adapter = new DrumItemAdapter(this, studentDrumList, this);
        rvDrums.setLayoutManager(gridLayoutManager);
        rvDrums.setAdapter(adapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        GroupDrumItemAdapter groupDrumItemAdapter = new GroupDrumItemAdapter(this, studentDrumList, this);
        rvGroupDrum.setLayoutManager(linearLayoutManager);
        rvGroupDrum.setAdapter(groupDrumItemAdapter);
    }

    @Override
    public void setOnItemClickListener(StudentDrum studentDrum, int position) {
        DrumInfoFragment drumInfoFragment = new DrumInfoFragment();
        drumInfoFragment.show(getSupportFragmentManager(), "info");
    }

    @Override
    public void onFragmentInteraction(String message) {

    }

    public void groupOutAnim() {
        Animation animation = AnimationUtils.makeOutAnimation(this, true);
        animation.setDuration(2000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llGroup.setVisibility(View.GONE);
                mask.setVisibility(View.GONE);
                clickIndex = 0;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        llGroup.startAnimation(animation);
    }

    private void groupInAnim() {
        llGroup.setVisibility(View.VISIBLE);
        mask.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.makeInAnimation(this, false);
        animation.setDuration(2000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                clickIndex = 0;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        llGroup.startAnimation(animation);
    }

    /**
     * 绑定service
     */
    public void bind() {
        Intent intent = new Intent(this, SocketService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    /**
     * 解除绑定service
     */
    public void unBind() {
        if (isBind) {
            unbindService(connection);
        }
    }

    @Override
    public void onCBClickListener(StudentDrum studentDrum, int position, Boolean isChecked) {
        LogUtils.e(studentDrum.getName() + ",isChecked:" + isChecked);
        if (isChecked) {
            clickDrumList.add(studentDrum);
        } else {
            if (clickDrumList.contains(studentDrum)) {
                clickDrumList.remove(studentDrum);
            }
        }
    }

    @OnClick({R.id.group, R.id.btnTone, R.id.btnMode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.group:
                groupInAnim();
                break;
            case R.id.btnTone:
                if (clickDrumList.size() > 0) {
                    showDialog();
                } else {
                    ToastUtils.toastShort("最少选择一个。。");
                }
                break;
            case R.id.btnMode:
                if (clickDrumList.size() > 0) {
                    showModeDialog();
                } else {
                    ToastUtils.toastShort("最少选择一个。。");
                }
                break;
        }
    }

    /**
     * 0：底鼓 ；
     * 1：军鼓；
     * 2：嗵鼓；
     * 3：嗵鼓；
     * 4：踩镲；
     * 5：吊镲；
     * 6：do；
     * 7：re；
     * 8：mi；
     * 9：fa；
     * 10：so；
     * 11：la；
     * 12：si；
     * 13：do+;
     */
    public void showDialog() {
        new AlertDialog.Builder(this)
                .setTitle("请选择")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(tones, 0,
                        (dialog, which) -> {
                            Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
                                emitter.onNext(true);

                                emitter.onComplete();
                            }).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe();
                            dialog.dismiss();
                        }
                )
                .setNegativeButton("取消", null)
                .show();
    }

    public void showModeDialog() {
        new AlertDialog.Builder(this)
                .setTitle("请选择")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(Constants.mode, 0,
                        (dialog, which) -> {
                            Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
                                emitter.onNext(true);
                                emitter.onComplete();
                            }).subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe();
                            dialog.dismiss();
                        }
                )
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBind();
    }
}
