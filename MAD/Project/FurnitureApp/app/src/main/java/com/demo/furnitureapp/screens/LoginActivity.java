package com.demo.furnitureapp.screens;

import static com.demo.furnitureapp.utility.FirestoreHelper.hasAdminExist;
import static com.demo.furnitureapp.utility.FirestoreHelper.hasEmailPassword;
import static com.demo.furnitureapp.utility.PreferencesUtil.setLoginAsAdmin;
import static com.demo.furnitureapp.utility.PreferencesUtil.setStayLogin;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.demo.furnitureapp.R;
import com.demo.furnitureapp.databinding.ActivityLoginBinding;
import com.demo.furnitureapp.model.Admin;
import com.demo.furnitureapp.model.User;
import com.demo.furnitureapp.utility.FirestoreHelper;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding mBinding;
    private ArrayList<User> userArrayList;
    private Admin adminObj;
    private boolean isPassword;
    private boolean hasStayLogin = false;
    private boolean loginAsAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirestoreHelper.fetchUserList(db, userArrayList -> {
            this.userArrayList = userArrayList;
        });
        FirestoreHelper.fetchAdminCredential(db, adminObj -> {
            this.adminObj = adminObj;
        });

        setListener();

    }

    private void setListener() {
        mBinding.tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
            }
        });

        mBinding.IvPasswordTooggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePasswordVisibility();
            }
        });

        mBinding.IvCheckStayLoggedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleStayLoggedIn();
            }
        });

        mBinding.cbAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                loginAsAdmin = isChecked;
                if (isChecked) {
                    int color = ContextCompat.getColor(LoginActivity.this, R.color.colorPrimary);
                    setCheckBoxColor(mBinding.cbAdmin, color);
                    mBinding.cLDontAccount.setVisibility(View.INVISIBLE);
                } else {
                    int color = ContextCompat.getColor(LoginActivity.this, R.color.colorPrimaryGrey);
                    setCheckBoxColor(mBinding.cbAdmin, color);
                    mBinding.cLDontAccount.setVisibility(View.VISIBLE);
                }
            }
        });

        mBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });
    }

    private void togglePasswordVisibility() {
        if (!isPassword) {
            mBinding.IvPasswordTooggle.setImageDrawable(
                    ContextCompat.getDrawable(LoginActivity.this, R.drawable.hide));
            mBinding.edtUserLoginPassword.setTransformationMethod(
                    HideReturnsTransformationMethod.getInstance());
            isPassword = true;
            mBinding.edtUserLoginPassword.setSelection(mBinding.edtUserLoginPassword.length());
        } else {
            mBinding.IvPasswordTooggle.setImageDrawable(
                    ContextCompat.getDrawable(LoginActivity.this, R.drawable.show));
            mBinding.edtUserLoginPassword.setTransformationMethod(
                    PasswordTransformationMethod.getInstance());
            isPassword = false;
            mBinding.edtUserLoginPassword.setSelection(mBinding.edtUserLoginPassword.length());
        }
    }

    private void toggleStayLoggedIn() {
        if (!hasStayLogin) {
            mBinding.IvCheckStayLoggedIn.setImageDrawable(
                    ContextCompat.getDrawable(LoginActivity.this, R.drawable.checked_icon));
            hasStayLogin = true;
        } else {
            mBinding.IvCheckStayLoggedIn.setImageDrawable(
                    ContextCompat.getDrawable(LoginActivity.this, R.drawable.unchecked_icon));
            hasStayLogin = false;
        }
    }

    private void validate() {
        String emailAddress = mBinding.edtUsernameOrEmail.getText().toString().trim();
        String password = mBinding.edtUserLoginPassword.getText().toString().trim();

        if (TextUtils.isEmpty(emailAddress) && TextUtils.isEmpty(password)) {
            mBinding.edtUsernameOrEmail.setError("Please enter email address.");
            mBinding.edtUserLoginPassword.setError("Please enter password.");
            return;
        }
        if (TextUtils.isEmpty(emailAddress)) {
            mBinding.edtUsernameOrEmail.requestFocus();
            mBinding.edtUsernameOrEmail.setError("Please enter email address.");
            return;
        }
        String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (!emailAddress.matches(EMAIL_PATTERN)) {
            mBinding.edtUsernameOrEmail.requestFocus();
            showErrorToast("Please enter valid email address.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            mBinding.edtUserLoginPassword.setError("Please enter password.");
            mBinding.edtUserLoginPassword.requestFocus();
            return;
        } /*else if (password.length() < 8 || password.length() > 10) {
            showErrorToast("Password length should be between 8 and 10 characters.");
            mBinding.edtUserLoginPassword.requestFocus();
            return;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            showErrorToast("Your password should contain special characters e.g @#$%!");
            mBinding.edtUserLoginPassword.requestFocus();
            return;
        } */ else {
            mBinding.edtUserLoginPassword.setError(null);
        }

        boolean isSuccess;
        if (loginAsAdmin) {
            if (hasAdminExist(this, adminObj, emailAddress, password)) {
                isSuccess = true;
                showSuccessToast("Admin Login Successfully");
                setStayLogin(this, hasStayLogin);
                setLoginAsAdmin(this, loginAsAdmin);
                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                finish();
            } else {
                isSuccess = false;
            }

        } else {
            if (hasEmailPassword(this, userArrayList, emailAddress, password)) {
                isSuccess = true;
                showSuccessToast("User Login Successfully");
                setStayLogin(this, hasStayLogin);
                setLoginAsAdmin(this, loginAsAdmin);
                startActivity(new Intent(LoginActivity.this, UserActivity.class));
                finish();
            } else {
                isSuccess = false;
            }

        }
        if (!isSuccess) {
            showErrorToast("Invalid Credential");
        }
    }

    private void showErrorToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccessToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setCheckBoxColor(CheckBox checkBox, int color) {
        checkBox.setButtonTintList(ColorStateList.valueOf(color));
    }
}