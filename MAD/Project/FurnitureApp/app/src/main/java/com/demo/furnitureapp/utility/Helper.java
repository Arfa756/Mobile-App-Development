package com.demo.furnitureapp.utility;

import android.content.Context;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.demo.furnitureapp.R;
import com.demo.furnitureapp.dialog.ConfirmationDialog;
import com.demo.furnitureapp.dialog.LoadingDialog;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Helper {


    public static boolean disableClick() {
        long mLastClickTime = 0;
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return true;
    }

    public static LoadingDialog showLoadingDialog(Context context, FragmentManager parentFragmentManager, String title) {
        LoadingDialog loadingDialog = new LoadingDialog();
        loadingDialog.setTitle(title);
        loadingDialog.setCancelable(false);
        loadingDialog.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomBottomSheetDialogTheme);
        if (!loadingDialog.isAdded()) {
            loadingDialog.show(parentFragmentManager, LoadingDialog.TAG);
        }
        return loadingDialog;
    }

    public static void showConfirmationDialog(FragmentManager parentFragmentManager,
                                              View.OnClickListener onDialogAction) {
        ConfirmationDialog confirmationDialog = new ConfirmationDialog(onDialogAction);
        confirmationDialog.setCancelable(false);
        confirmationDialog.show(parentFragmentManager, "ConfirmationDialog");
    }

    public static String getDateFormat() {
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss", Locale.getDefault());
        String currentDate = formatter.format(new Date());
        try {
            date = formatter.parse(currentDate);
            assert date != null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date != null ? formatter.format(date) : null;
    }



    public static File getOutputFileDirectory(Context context, String dir) {
        Context appContext = context.getApplicationContext();
        File mediaDir = context.getExternalFilesDir(null);
        if (mediaDir != null) {
            mediaDir = new File(mediaDir, dir);
            mediaDir.mkdirs();
        }
        return mediaDir != null && mediaDir.exists() ? mediaDir : appContext.getFilesDir();
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static final String FURNITURE_OBJECT = "FURNITURE_OBJECT";


}
