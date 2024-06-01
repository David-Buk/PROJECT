package com.example.project;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;

public class SextortionDialogBuilder {
    private AlertDialog.Builder builder;
    private Context context;
    private StorageReference imageRef;

    public SextortionDialogBuilder(Context context) {
        this.context = context;
        this.imageRef = imageRef;
        builder = new AlertDialog.Builder(context);
    }

    public SextortionDialogBuilder setMessage(String message) {
        builder.setMessage(message);
        return this;
    }

    public SextortionDialogBuilder setPositiveButton(String text) {
        builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, InfoActivity.class);
                context.startActivity(intent);
                // Delete the image from Firebase Storage
                imageRef.delete().addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Image deleted", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to delete image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

        });
        return this;
    }

    public SextortionDialogBuilder setNegativeButton(String text) {
        builder.setNegativeButton(text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Redirect to InfoActivity
                Intent intent = new Intent(context, ImageListActivity.class);
                context.startActivity(intent);
            }
        });
        return this;
    }


    public void show() {
        builder.create().show();
    }

}
