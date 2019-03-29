package com.benbaba.tob.adapter;

import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.benbaba.tob.R;
import com.benbaba.tob.bean.StudentDrum;
import com.bhx.common.ui.widget.DrumView;
import com.bhx.common.adapter.rv.CommonAdapter;
import com.bhx.common.adapter.rv.ViewHolder;

import java.util.List;

/**
 * 鼓列表
 */
public class DrumItemAdapter extends CommonAdapter<StudentDrum> {

    private OnDrumClickListener onDrumClickListener;

    public DrumItemAdapter(Context context, List<StudentDrum> datas,OnDrumClickListener onDrumClickListener) {
        super(context, datas);
        this.onDrumClickListener = onDrumClickListener;
    }

    @Override
    protected int itemLayoutId() {
        return R.layout.drum_item;
    }

    @Override
    protected void convert(ViewHolder holder, StudentDrum studentDrum, int position) {
        TextView name = holder.getView(R.id.name);
        DrumView icon = holder.getView(R.id.drum);
        TextView onLine = holder.getView(R.id.tvOnLine);
        ProgressBar progressBar = holder.getView(R.id.progressBar);
        name.setText(studentDrum.getName());
        progressBar.setProgress(studentDrum.getElectricity());
        if (studentDrum.isOnLine()){
            onLine.setText("在线");
        }else {
            onLine.setText("离线");
        }
        icon.setOnDrumClickListener(v -> {
            if (onDrumClickListener!=null)
                onDrumClickListener.setOnItemClickListener(studentDrum,position);
        });
    }

    public interface OnDrumClickListener{
        void setOnItemClickListener(StudentDrum studentDrum,int position);
    }
}
