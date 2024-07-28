package com.demo.furnitureapp.screens;

import static com.demo.furnitureapp.utility.Helper.disableClick;
import static com.demo.furnitureapp.utility.Helper.getDateFormat;
import static com.demo.furnitureapp.utility.Helper.getOutputFileDirectory;
import static com.demo.furnitureapp.utility.Helper.showConfirmationDialog;
import static com.demo.furnitureapp.utility.Helper.showLoadingDialog;
import static com.demo.furnitureapp.utility.Helper.showToast;
import static com.demo.furnitureapp.utility.PreferencesUtil.getLoggedEmail;
import static com.demo.furnitureapp.utility.PreferencesUtil.signOut;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.demo.furnitureapp.R;
import com.demo.furnitureapp.databinding.ActivityAdminBinding;
import com.demo.furnitureapp.dialog.LoadingDialog;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding mBinding;
    private Uri furnitureUri;
    private String TAG = getClass().getCanonicalName();
    private StorageReference mStorageRef;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference furnitureCollection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        furnitureCollection = db.collection("Furniture");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        setToolbar();
        setListener();
    }

    private void setToolbar() {
        mBinding.toolbarMain.tvToolbarTitle.setText(R.string.admin_screen_title);
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
                                    signOut(AdminActivity.this);
                                    startActivity(new Intent(AdminActivity.this, LoginActivity.class));
                                    finish();
                                }
                            }
                    );
                }
            }
        });
        mBinding.IvAddFurnitureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (disableClick()) {
                    ImagePicker.with(AdminActivity.this)
                            .crop()
                            .compress(1024)
                            .maxResultSize(1080, 1080)
                            .saveDir(getOutputFileDirectory(AdminActivity.this, getResources().getString(R.string.app_name)))
                            .createIntent(intent -> {
                                imageLauncher.launch(intent);
                                return null; // Return null as a placeholder for Unit
                            });
                }
            }
        });
        mBinding.btnAddFurniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (disableClick()) {
                    if (validate() && furnitureUri != null) {
                        LoadingDialog dialog = showLoadingDialog(AdminActivity.this, getSupportFragmentManager(), getString(R.string.uploading));
                        uploadData(furnitureUri, new HasUploadedCallback() {
                            @Override
                            public void onUploaded(boolean success) {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (dialog.isVisible()) {
                                            dialog.dismiss();
                                        }
                                        if (success) {
                                            showToast(AdminActivity.this, "Added Successfully");
                                            clearValues();
                                        } else {
                                            showToast(AdminActivity.this, "Something Went Wrong");
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }

    private void uploadData(Uri uri, HasUploadedCallback hasUploaded) {
        try {
            String fileReferenceUriString = uri.getLastPathSegment();
            StorageReference fileReference = fileReferenceUriString != null ? mStorageRef.child(fileReferenceUriString) : null;

            if (fileReference != null) {
                fileReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                    if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null) {
                        Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUrl.addOnSuccessListener(uri1 -> {
                            String imageUrl = uri.toString();
                            String getCurrentTime = getDateFormat();

                            Map<String, Object> data = new HashMap<>();
                            data.put("url", uri1.toString());
                            data.put("title", mBinding.edtFurnitureTitle.getText().toString().trim());
                            data.put("description", mBinding.edtFurnitureDescription.getText().toString().trim());
                            data.put("time", getCurrentTime);
                            data.put("timestamp", FieldValue.serverTimestamp());
                            data.put("email", getLoggedEmail(AdminActivity.this));
                            data.put("furnitureId", furnitureCollection.document().getId());

                            furnitureCollection.add(data);
                            hasUploaded.onUploaded(true);
                        }).addOnFailureListener(exception -> {
                            hasUploaded.onUploaded(false);
                            Log.e(TAG, "downloadUrl: ", exception);
                        });
                    }
                }).addOnFailureListener(exception -> {
                    Log.e(TAG, "uploadData: ");
                    hasUploaded.onUploaded(false);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface HasUploadedCallback {
        void onUploaded(boolean success);
    }

    private final ActivityResultLauncher<Intent> imageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Image Uri will not be null for RESULT_OK
                    Intent data = result.getData();
                    if (data != null) {
                        Uri fileUri = data.getData();
                        if (fileUri != null) {
                            furnitureUri = fileUri;
                            Glide.with(this)
                                    .load(fileUri)
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(mBinding.iVFurnitureImage);
                        }
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    showToast(AdminActivity.this, "Task Cancelled");
                } else {
                    showToast(AdminActivity.this, "Image Picker Error");
                }
            }
    );

    private boolean validate() {
        String edtTitle = mBinding.edtFurnitureTitle.getText().toString().trim();
        String edtDescription = mBinding.edtFurnitureDescription.getText().toString().trim();

        if (furnitureUri == null) {
            showToast(AdminActivity.this, "Please Add Image.");
            return false;
        }
        if (TextUtils.isEmpty(edtTitle)) {
            mBinding.edtFurnitureTitle.setError("Please enter title.");
            mBinding.edtFurnitureTitle.requestFocus(R.id.edtFurnitureTitle);
            return false;
        }
        if (TextUtils.isEmpty(edtDescription)) {
            mBinding.edtFurnitureDescription.setError("Please enter description.");
            mBinding.edtFurnitureDescription.requestFocus(R.id.edtFurnitureDescription);
            return false;
        }
        return true;
    }

    private void clearValues() {
        furnitureUri = null;

        // Using Glide to load a default image into an ImageView
        Glide.with(AdminActivity.this)
                .load(R.drawable.image_default_bg)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(mBinding.iVFurnitureImage);

        // Clear the text in EditText fields
        mBinding.edtFurnitureTitle.getText().clear();
        mBinding.edtFurnitureDescription.getText().clear();
    }

}