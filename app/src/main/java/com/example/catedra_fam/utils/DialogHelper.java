package com.example.catedra_fam.utils;

import android.app.AlertDialog;
import android.content.Context;

public class DialogHelper {
    public static void showInfoDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    public static void showErrorDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("Cerrar", null)
                .show();
    }

    public static void showSuccessDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Éxito")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}

