package com.demo.furnitureapp.screens;

import static com.demo.furnitureapp.utility.Helper.disableClick;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.demo.furnitureapp.R;
import com.demo.furnitureapp.databinding.ActivityPaymentBinding;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setToolbar();
        setListener();
    }

    private void setToolbar() {
        mBinding.toolbarMain.tvToolbarTitle.setText(R.string.make_payment);
        mBinding.toolbarMain.iVBackArrow.setVisibility(View.VISIBLE);
    }

    private void setListener() {
        mBinding.toolbarMain.iVBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (disableClick()) {
                    finish();
                }
            }
        });
    }
}
