package com.example.karina.firebaseapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle

  fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(it)

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            builder.setView(layoutInflater.inflate(R.layout.activity_uploading, null))
                // Add action buttons
                .setPositiveButton(R.string.uploading,
                    DialogInterface.OnClickListener { dialog, id ->
                      R.id.UploadImage
                    })
                .setNegativeButton(R.string.back,
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog().goBack()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }