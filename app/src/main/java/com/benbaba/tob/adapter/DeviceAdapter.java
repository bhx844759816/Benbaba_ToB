package com.benbaba.tob.adapter;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.benbaba.tob.R;
import com.benbaba.tob.model.DeviceModel;
import com.benbaba.tob.widget.DrumView;
import com.bhx.common.ui.recyclerview.CommonAdapter;
import com.bhx.common.ui.recyclerview.ViewHolder;

import java.util.List;

/**
 * 设备列表适配器
 */
public class DeviceAdapter extends CommonAdapter<DeviceModel> {

    public DeviceAdapter(Context context, List<DeviceModel> datas) {
        super(context, datas);
    }

    @Override
    public int getLayoutId() {
        return R.layout.adapter_device_list;
    }

    public void notifyData(List<DeviceModel> data) {
        this.mDatas = data;
        notifyDataSetChanged();
    }

    @Override
    public void convert(ViewHolder holder, DeviceModel item, int position) {
        CheckBox cb = holder.getView(R.id.id_device_list_cb);
        cb.setOnCheckedChangeListener((buttonView, isChecked) -> item.setSelect(isChecked));
        cb.setVisibility(item.isVisibility() ? View.VISIBLE : View.GONE);
        if (item.isSelect()) {
            cb.setChecked(true);
        } else {
            cb.setChecked(false);
        }
        DrumView view = holder.getView(R.id.id_device_list_drumView);
        view.setCurSetting(item.isStartSetting());
        view.setIsSetResult(item.getSettingResult());
    }
}
