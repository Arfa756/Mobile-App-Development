package com.demo.furnitureapp.screens;

import static com.demo.furnitureapp.utility.Helper.FURNITURE_OBJECT;
import static com.demo.furnitureapp.utility.Helper.disableClick;
import static com.demo.furnitureapp.utility.Helper.showConfirmationDialog;
import static com.demo.furnitureapp.utility.PreferencesUtil.signOut;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.furnitureapp.R;
import com.demo.furnitureapp.adapter.FurnitureAdapter;
import com.demo.furnitureapp.databinding.ActivityUserBinding;
import com.demo.furnitureapp.model.Furniture;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class UserActivity extends AppCompatActivity {

    private ActivityUserBinding mBinding;
    private String TAG = getClass().getCanonicalName();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference furnitureCollection;
    private boolean isScreenOpened = false;
    private ArrayList<Furniture> furnitureArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isScreenOpened = true;
        mBinding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        furnitureCollection = db.collection("Furniture");
        setToolbar();
        setListener();

        addRealTimeUpdate();
    }

    private void setToolbar() {
        mBinding.toolbarMain.tvToolbarTitle.setText(R.string.user_screen_title);
        mBinding.toolbarMain.tvLogout.setText(R.string.logout);
    }

    private void setListener() {
        mBinding.toolbarMain.tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (disableClick()) {
                    showConfirmationDialog(
                            getSupportFragmentManager(),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    signOut(UserActivity.this);
                                    startActivity(new Intent(UserActivity.this, LoginActivity.class));
                                    finish();
                                }
                            }
                    );
                }
            }
        });

    }

    private void addRealTimeUpdate() {
        furnitureCollection.orderBy("time", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("Error", Objects.requireNonNull(error.getMessage()));
                    return;
                }
                if (isScreenOpened) {
                    furnitureArrayList = new ArrayList<>();

                    if (value != null && !value.isEmpty()) {
                        Log.d("Documents", value.getDocuments().toString());

                        for (QueryDocumentSnapshot doc : value) {
                            isScreenOpened = false;
                            if (doc.exists()) {
                                Furniture furniture = doc.toObject(Furniture.class);
                                furnitureArrayList.add(furniture);
                            }
                        }
                        setFurnitureAdapter(furnitureArrayList);
                    }
                } else {
                    Furniture furniture;

                    if (value != null) {
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                furniture = dc.getDocument().toObject(Furniture.class);
                                furnitureArrayList.add(furniture);
                            }
                        }
                    }

                    setFurnitureAdapter(furnitureArrayList);
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setFurnitureAdapter(ArrayList<Furniture> furnitureArrayList) {
        if (furnitureArrayList.size() > 0) {
            mBinding.recyclerViewFurniture.setVisibility(View.VISIBLE);
            mBinding.recyclerViewFurniture.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
            mBinding.recyclerViewFurniture.setItemAnimator(new DefaultItemAnimator());
            FurnitureAdapter blogAdapter = new FurnitureAdapter(furnitureArrayList, new FurnitureAdapter.OnItemClick() {
                @Override
                public void onClick(Furniture furnitureItem) {
                    handleBlogItemClick(furnitureItem);
                }
            });
            mBinding.recyclerViewFurniture.setAdapter(blogAdapter);
            blogAdapter.notifyDataSetChanged();
        }
    }

    private void handleBlogItemClick(Furniture clickItem) {

        Log.d(TAG, "handleBlogItemClick: " + clickItem);
        Intent intent = new Intent(this, FurnitureDetailActivity.class);
        intent.putExtra(FURNITURE_OBJECT, clickItem);
        startActivity(intent);
    }

}