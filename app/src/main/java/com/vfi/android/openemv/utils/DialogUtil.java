package com.vfi.android.openemv.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public class DialogUtil {
    public static synchronized void showInputDialog(Context context, String title, final InputDialogListener listener) {

        final EditText mEditText = new EditText(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setView(mEditText);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                dialog.dismiss();
                //数据获取
                if (listener != null)
                    listener.onInput(mEditText.getText().toString(), true);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (listener != null)
                    listener.onInput("", false);
            }
        });
        showDialog(builder);
    }

    private static void showDialog(final AlertDialog.Builder builder) {
        final AlertDialog dialog = builder.create();
        //dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    public interface InputDialogListener {
        public void onInput(String inputStr, boolean isConfirm);
    }
}
