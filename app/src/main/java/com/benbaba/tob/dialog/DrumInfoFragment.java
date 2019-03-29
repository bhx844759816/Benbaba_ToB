package com.benbaba.tob.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.benbaba.tob.Constants;
import com.benbaba.tob.R;
import com.benbaba.tob.bean.StudentDrum;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DrumInfoFragment extends DialogFragment {

    Unbinder unbinder;
    @BindView(R.id.tvTone)
    TextView tvTone;
    @BindView(R.id.tvMode)
    TextView tvMode;
    @BindView(R.id.tvAccuracy)
    TextView tvAccuracy;
    @BindView(R.id.tvElectricity)
    TextView tvElectricity;
    private OnFragmentDrumListener mListener;
    private StudentDrum studentDrum;

    public DrumInfoFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_drum_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {
        tvTone.setText("音色:"+studentDrum.getTone());
    }

    public void setDrumInfo(StudentDrum studentDrum) {
        this.studentDrum = studentDrum;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentDrumListener) {
            mListener = (OnFragmentDrumListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tone, R.id.mode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tone:
                showDialog();
                break;
            case R.id.mode:
                showModeDialog();
                break;
        }
    }

    public interface OnFragmentDrumListener {
        void onFragmentInteraction(String message);
    }

    public void showModeDialog() {
        new AlertDialog.Builder(getContext())
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

    public void showAdminDialog(final View view) {
        new AlertDialog.Builder(getContext())
                .setTitle("请选择")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(new String[]{"从鼓", "主鼓"}, 0,
                        (dialog, which) -> {
                            Log.i("which0", "which" + which);
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
        new AlertDialog.Builder(getContext())
                .setTitle("请选择")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setSingleChoiceItems(Constants.tones, 0,
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
}
