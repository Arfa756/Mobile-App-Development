package com.demo.furnitureapp.dialog;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.demo.furnitureapp.R;
import com.demo.furnitureapp.databinding.DialogConfirmationBinding;

import java.util.Objects;

public class ConfirmationDialog extends DialogFragment {

    private final View.OnClickListener clickAction;
    private DialogConfirmationBinding mBinding;


    public ConfirmationDialog(View.OnClickListener clickAction) {
        this.clickAction = clickAction;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DialogConfirmationBinding.inflate(inflater, container, false);
        Dialog dialog = getDialog();
        if (dialog != null) {
            Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.card_bg);
            dialog.setCancelable(false);
        }
        return mBinding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.tvCancel.setOnClickListener(v -> dismiss());

        mBinding.tvLogout.setOnClickListener(v -> {
            clickAction.onClick(v);
            dismiss();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = requireDialog().getWindow();
        if (window != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}