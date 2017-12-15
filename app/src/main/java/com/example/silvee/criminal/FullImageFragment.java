package com.example.silvee.criminal;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * Created by silvee on 30.11.2017.
 */

// Fragment with dialog to choose date of a crime
public class FullImageFragment extends DialogFragment {
    public static final String ARG_PHOTO_FILE = "photo_file";

    private ImageView fullPhotoImage;
    private Button cancelButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        File photoFile = (File) getArguments().getSerializable(ARG_PHOTO_FILE);
        View v = inflater.inflate(R.layout.dialog_fullimage, container, false);

        fullPhotoImage = v.findViewById(R.id.dialog_full_image);
        if (photoFile == null || !photoFile.exists()) {
            fullPhotoImage.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());
            fullPhotoImage.setImageBitmap(bitmap);
        }

        cancelButton =  v.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;
    }

    public static FullImageFragment newInstance(File file) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO_FILE, file);

        FullImageFragment fullImageFragment = new FullImageFragment();
        fullImageFragment.setArguments(args);
        return fullImageFragment;
    }
}
