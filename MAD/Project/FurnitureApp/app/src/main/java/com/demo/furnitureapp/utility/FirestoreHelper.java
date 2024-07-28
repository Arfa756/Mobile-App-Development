package com.demo.furnitureapp.utility;

import static com.demo.furnitureapp.utility.PreferencesUtil.setFirstName;
import static com.demo.furnitureapp.utility.PreferencesUtil.setLastName;
import static com.demo.furnitureapp.utility.PreferencesUtil.setLoggedEmail;
import static com.demo.furnitureapp.utility.PreferencesUtil.setProfileImage;

import android.content.Context;
import android.util.Log;

import com.demo.furnitureapp.model.Admin;
import com.demo.furnitureapp.model.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class FirestoreHelper {

    public static void fetchUserList(FirebaseFirestore db, UserListListener listener) {
        final ArrayList<User> userArrayList = new ArrayList<>();

        db.collection("User").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.d("Error", Objects.requireNonNull(error.getMessage()));
                return;
            }
            if (value != null && !value.isEmpty()) {
                for (QueryDocumentSnapshot document : value) {
                    if (document.get("email") != null) {
                        String email = Objects.requireNonNull(document.get("email")).toString();
                        String password = Objects.requireNonNull(document.get("password")).toString();
                        String firstName = Objects.requireNonNull(document.get("firstname")).toString();
                        String lastName = Objects.requireNonNull(document.get("lastname")).toString();
                        String profileImageUrl = Objects.requireNonNull(document.get("profileImageUrl")).toString();

                        userArrayList.add(new User(email, password, firstName, lastName, profileImageUrl));
                    }
                }
                listener.onUserListFetched(userArrayList);
            }
        });
    }

    public interface UserListListener {
        void onUserListFetched(ArrayList<User> userList);
    }

    public static void fetchAdminCredential(FirebaseFirestore db, AdminFetchListener listener) {
        final Admin adminObj = new Admin();

        db.collection("Admin").addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.d("Error", Objects.requireNonNull(error.getMessage()));
                return;
            }
            if (value != null && !value.isEmpty()) {
                for (QueryDocumentSnapshot document : value) {
                    if (document.get("email") != null) {
                        String email = Objects.requireNonNull(document.get("email")).toString();
                        String password = Objects.requireNonNull(document.get("password")).toString();

                        adminObj.setEmail(email);
                        adminObj.setPassword(password);

                    }
                }
                listener.onAdminFetched(adminObj);
            }
        });
    }

    public interface AdminFetchListener {
        void onAdminFetched(Admin adminObj);
    }

    public static boolean hasEmailPassword(Context context, ArrayList<User> userArrayList, String emailAddress, String password) {
        for (User user : userArrayList) {
            if (user.getEmail().equals(emailAddress) && user.getPassword().equals(password)) {
                setLoggedEmail(context, user.getEmail());
                setFirstName(context, user.getFirstName());
                setLastName(context, user.getLastName());
                setProfileImage(context, user.getProfileImageUrl());
                return true;
            }
        }
        return false;
    }

    public static boolean hasAdminExist(Context context, Admin adminObj, String emailAddress, String password) {

            if (adminObj.getEmail().equals(emailAddress) && adminObj.getPassword().equals(password)) {
                setLoggedEmail(context, adminObj.getEmail());
                return true;
            }
        return false;
    }
}
