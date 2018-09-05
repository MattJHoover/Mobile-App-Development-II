package com.hoover.matthew.giantbombgames;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class ErrorDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String title = getActivity().getTitle().toString();
        builder.setMessage(title + " was already added to favorites");
        builder.setTitle(title);
        builder.setCancelable(false);
        return builder.create();
    }
}