package com.couriertrack.image_utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.couriertrack.R;

import java.io.File;
import java.util.ArrayList;

import static com.couriertrack.image_utils.ImageUtils.getOutputMediaFile;
import static com.couriertrack.image_utils.ImageUtils.getPath;

public class FragmentSelectImageDialog extends DialogFragment implements View.OnClickListener {

    private static final int IMAGE_CAPTURE = 1;
    private static final int IMAGE_GALLERY = 2;
    private static final String TAG = "SelectImageDialog";

    LinearLayout llGallery,llCamera;
    Context context;
    private File mediaFile;
    ImageSelectListener listener;
    public static FragmentSelectImageDialog newInstance()
    {
        return new FragmentSelectImageDialog();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_dialog, container, false);
        llCamera = view.findViewById(R.id.llGallery);
        llCamera.setOnClickListener(this);
        llGallery= view.findViewById(R.id.llCamera);
        llGallery.setOnClickListener(this);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        return view;
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.llCamera:
                cameraIntent();
                break;
            case R.id.llGallery:
                galleryIntent();
                break;
        }
    }
    public boolean hasPermission(Context context, String[] permissions)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null)
        {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
        }
        return true;
    }

    private void cameraIntent() {
        if (hasPermission(context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri uri;
            mediaFile=getOutputMediaFile();
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                uri = FileProvider.getUriForFile(context, context.getPackageName()+".provider", mediaFile);
            } else {
                uri = Uri.fromFile(mediaFile);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, IMAGE_CAPTURE);
        } else {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void galleryIntent() {
        if (hasPermission(context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }*/
            startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), IMAGE_GALLERY);
        }
        else {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if(hasPermission(context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}))
                    cameraIntent();
                break;
            case 2:
                if(hasPermission(context, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}))
                    galleryIntent();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String imagePath;
        if (requestCode == IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            imagePath = mediaFile.getAbsolutePath();
            if(!TextUtils.isEmpty(imagePath))
            {
                if(listener!=null)
                    listener.onImageSelected(imagePath);
            }
            else
            {
                Toast.makeText(context, "Can't get image", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == IMAGE_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            ClipData clipData = data.getClipData();

            if(clipData==null)
            {
                Log.e(TAG, "uri from gallery -> " + uri.toString());
                imagePath = getPath(context, uri);
                Log.e(TAG, "imagePath -> " + imagePath);
                if(!TextUtils.isEmpty(imagePath))
                {
                    if(listener!=null)
                        listener.onImageSelected(imagePath);
                }
                else
                {
                    Toast.makeText(context, "Can't get image", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                ArrayList<String> multipleImages=new ArrayList<>();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    final Uri uri1 = item.getUri();
                    multipleImages.add(getPath(context, uri1));
                }
                if(listener!=null)
                    listener.onImageSelected(multipleImages);
            }
        }

    }

    public void setListener(ImageSelectListener listener) {
        this.listener=listener;
    }

    public interface ImageSelectListener
    {
        public void onImageSelected(String path);
        public void onImageSelected(ArrayList<String> listImages);
    }
}
