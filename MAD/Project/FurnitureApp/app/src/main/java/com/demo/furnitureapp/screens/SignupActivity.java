package com.demo.furnitureapp.screens;

import static com.demo.furnitureapp.utility.Helper.disableClick;
import static com.demo.furnitureapp.utility.Helper.getOutputFileDirectory;
import static com.demo.furnitureapp.utility.Helper.showLoadingDialog;
import static com.demo.furnitureapp.utility.Helper.showToast;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.demo.furnitureapp.R;
import com.demo.furnitureapp.databinding.ActivitySignupBinding;
import com.demo.furnitureapp.dialog.LoadingDialog;
import com.demo.furnitureapp.model.User;
import com.demo.furnitureapp.utility.FirestoreHelper;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private Uri mProfileUri;
    private ActivitySignupBinding mBinding;
    private String TAG = getClass().getCanonicalName();
    private boolean hasAlreadyExist = false;
    private boolean isPassword = false;
    private StorageReference mStorageRef;
    private ArrayList<User> userArrayList = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCollection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        mStorageRef = FirebaseStorage.getInstance().getReference();

        FirestoreHelper.fetchUserList(db, userArrayList -> {
            this.userArrayList = userArrayList;
        });

        setListener();
    }

    private void setListener() {
        mBinding.IvProfileImage.setOnClickListener(v -> {
            if (disableClick()) {
                ImagePicker.with(SignupActivity.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .saveDir(getOutputFileDirectory(this, getResources().getString(R.string.app_name)))
                        .createIntent(intent -> {
                            imageLauncher.launch(intent);
                            return null; // Return null as a placeholder for Unit
                        });
            }
        });

        mBinding.tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });

        mBinding.btnSignup.setOnClickListener(v -> {
            if (disableClick()) {
                if (validate()) {
                    if (mProfileUri == null) {
                        setData("");
                    } else {
                        LoadingDialog dialog = showLoadingDialog(this, getSupportFragmentManager(), getString(R.string.signing));
                        uploadData(mProfileUri, new UploadCallback() {
                            @Override
                            public void onUploadComplete(boolean success) {

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (dialog.isVisible()) {
                                            dialog.dismiss();
                                        }
                                        if (success) {
//                                            showToast(SignupActivity.this, "Success");
                                        } else {
                                            showToast(SignupActivity.this, "Something Went Wrong");
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });

        mBinding.IvPasswordTooggle.setOnClickListener(v -> {
            if (!isPassword) {
                mBinding.IvPasswordTooggle.setImageDrawable(ContextCompat.getDrawable(SignupActivity.this, R.drawable.hide));
                mBinding.edtUserLoginPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                isPassword = true;
                mBinding.edtUserLoginPassword.setSelection(mBinding.edtUserLoginPassword.length());
            } else {
                mBinding.IvPasswordTooggle.setImageDrawable(ContextCompat.getDrawable(SignupActivity.this, R.drawable.show));
                mBinding.edtUserLoginPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                isPassword = false;
                mBinding.edtUserLoginPassword.setSelection(mBinding.edtUserLoginPassword.length());
            }
        });

    }


    private boolean validate() {
        String emailAddress = mBinding.edtUsernameOrEmail.getText().toString().trim();
        String password = mBinding.edtUserLoginPassword.getText().toString().trim();
        String firstName = mBinding.edtUserFirstName.getText().toString().trim();
        String lastName = mBinding.edtUserLastName.getText().toString().trim();

        if (TextUtils.isEmpty(emailAddress)
                && TextUtils.isEmpty(password)
                && TextUtils.isEmpty(firstName)
                && TextUtils.isEmpty(lastName)) {
            mBinding.edtUsernameOrEmail.setError("Please enter email address.");
            mBinding.edtUserLoginPassword.setError("Please enter password.");
            mBinding.edtUserFirstName.setError("Please enter first name.");
            mBinding.edtUserLastName.setError("Please enter last name.");
            return false;
        }

        if (TextUtils.isEmpty(emailAddress)) {
            mBinding.edtUsernameOrEmail.requestFocus(R.id.edtUsernameOrEmail);
            mBinding.edtUsernameOrEmail.setError("Please enter email address.");
            return false;
        }

        String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!emailAddress.matches(EMAIL_PATTERN)) {
            mBinding.edtUsernameOrEmail.requestFocus(R.id.edtUsernameOrEmail);
            showToast(this, "Please enter valid email address.");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            mBinding.edtUserLoginPassword.setError("Please enter password.");
            mBinding.edtUserLoginPassword.requestFocus(R.id.edtUserLoginPassword);
            return false;
        } /*else if (password.length() < 8) {
            showToast(this, "Password length should be between 8 and 10 characters.");
            mBinding.edtUserLoginPassword.requestFocus(R.id.edtUserLoginPassword);
            return false;
        }*/ else {
            mBinding.edtUserLoginPassword.setError(null);
        }

        if (TextUtils.isEmpty(firstName)) {
            showToast(this, "Please enter first name.");
            mBinding.edtUserFirstName.requestFocus(R.id.edtUserFirstName);
            return false;
        }

        if (TextUtils.isEmpty(lastName)) {
            showToast(this, "Please enter last name.");
            mBinding.edtUserLastName.requestFocus(R.id.edtUserLastName);
            return false;
        }

        for (User user : userArrayList) {
            if (user.getEmail().equals(emailAddress) && user.getPassword().equals(password)) {
                hasAlreadyExist = true;
                break;
            }
        }

        if (hasAlreadyExist) {
            showToast(this, "User Already exist");
            return false;
        }

        return true;
    }


    private void setData(String profileImageUrl) {
        userCollection = db.collection("User");
        mBinding.getRoot().post(() -> {
            Map<String, Object> data = new HashMap<>();
            data.put("email", mBinding.edtUsernameOrEmail.getText().toString().trim());
            data.put("password", mBinding.edtUserLoginPassword.getText().toString().trim());
            data.put("firstname", mBinding.edtUserFirstName.getText().toString().trim());
            data.put("lastname", mBinding.edtUserLastName.getText().toString().trim());
            data.put("profileImageUrl", profileImageUrl);

            userCollection.add(data)
                    .addOnSuccessListener(documentReference -> {
                        showToast(this, "User Created Successfully");
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "setData: ", e));
        });
    }

    public interface UploadCallback {
        void onUploadComplete(boolean success);
    }

    private void uploadData(Uri uri, UploadCallback hasUploaded) {
        try {
            StorageReference fileReference = mStorageRef.child(Objects.requireNonNull(uri.getLastPathSegment()));
            fileReference.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null) {
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnSuccessListener(downloadUrl -> {
                                        setData(downloadUrl.toString());
                                        hasUploaded.onUploadComplete(true);
                                    })
                                    .addOnFailureListener(e -> {
                                        hasUploaded.onUploadComplete(false);
                                        Log.e(TAG, "downloadUrl: ", e);
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "uploadData: ", e);
                        hasUploaded.onUploadComplete(false);
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                            mProfileUri = fileUri;
                            Glide.with(this)
                                    .load(fileUri)
                                    .circleCrop()
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                                    .skipMemoryCache(true)
                                    .into(mBinding.IvProfileImage);
                        }
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    showToast(SignupActivity.this, "Task Cancelled");
                } else {
                    showToast(SignupActivity.this, "Image Picker Error");
                }
            }
    );
}