package com.benbaba.tob.adapter;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.TextView;

import com.benbaba.tob.R;
import com.benbaba.tob.bean.StudentDrum;
import com.bhx.common.adapter.rv.CommonAdapter;
import com.bhx.common.adapter.rv.ViewHolder;

import java.util.List;

public class GroupDrumItemAdapter extends CommonAdapter<StudentDrum> {

    private OnCBClickListener onCBClickListener;

    public GroupDrumItemAdapter(Context context, List<StudentDrum> datas,OnCBClickListener onCBClickListener) {
        super(context, datas);
        this.onCBClickListener = onCBClickListener;
    }

    @Override
    protected int itemLayoutId() {
        return R.layout.group_drum_item;
    }

    @Override
    protected void convert(ViewHolder holder, StudentDrum studentDrum, int position) {
        TextView tvName = holder.getView(R.id.name);
        CheckBox checkBox = holder.getView(R.id.cbCheck);
        tvName.setText(studentDrum.getName());
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (onCBClickListener!=null){
                onCBClickListener.onCBClickListener(studentDrum,position,isChecked);
            }
        });
    }

    public void setOnItemClickListener(OnCBClickListener onCBClickListener){
        this.onCBClickListener = onCBClickListener;
    }

    public interface OnCBClickListener{
        void onCBClickListener(StudentDrum studentDrum,int position,Boolean isChecked);
    }
}
