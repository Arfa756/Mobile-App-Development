package com.demo.furnitureapp.screens;

import static com.demo.furnitureapp.utility.Helper.FURNITURE_OBJECT;
import static com.demo.furnitureapp.utility.Helper.disableClick;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.demo.furnitureapp.R;
import com.demo.furnitureapp.databinding.ActivityFurnitureDetailBinding;
import com.demo.furnitureapp.model.Furniture;
import com.squareup.picasso.Picasso;

public class FurnitureDetailActivity extends AppCompatActivity {

    private ActivityFurnitureDetailBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityFurnitureDetailBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setToolbar();
        setListener();
        setData(getFurnitureObj());
    }

    private void setToolbar() {
        mBinding.toolbarMain.tvToolbarTitle.setText(R.string.furniture_detail);
        mBinding.toolbarMain.tvLogout.setVisibility(View.INVISIBLE);
        mBinding.toolbarMain.iVBackArrow.setVisibility(View.VISIBLE);
    }

    private void setListener() {
        mBinding.toolbarMain.iVBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (disableClick()) {
                    finish();
                }
            }
        });
        mBinding.btnBuyFurniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (disableClick()) {
                    Intent intent = new Intent(FurnitureDetailActivity.this, PaymentActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private Furniture getFurnitureObj() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null && extras.containsKey(FURNITURE_OBJECT)) {
            return extras.getParcelable(FURNITURE_OBJECT);
        } else {
            return null;
        }
    }

    private void setData(Furniture furnitureObj) {
        if (furnitureObj != null) {
            if (!TextUtils.isEmpty(furnitureObj.getUrl())) {
                Picasso.get().load(furnitureObj.getUrl())
                        .placeholder(R.drawable.image_default_bg)
                        .error(R.drawable.image_default_bg)
                        .into(mBinding.iVFurnitureImage);
            } else {
                Picasso.get().load(R.drawable.image_default_bg)
                        .placeholder(R.drawable.image_default_bg)
                        .error(R.drawable.image_default_bg)
                        .into(mBinding.iVFurnitureImage);
            }

            mBinding.edtFurnitureTitle.setText(furnitureObj.getTitle());
            mBinding.edtFurnitureDescription.setText(furnitureObj.getDescription());
        }
    }


}