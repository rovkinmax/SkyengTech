package ru.rovkinmax.skyengtech.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import ru.rovkinmax.skyengtech.R;


public class UnexpectedErrorDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog)
                .setMessage(R.string.error_unexpected)
                .setPositiveButton(R.string.button_ok, null)
                .create();
    }

}
