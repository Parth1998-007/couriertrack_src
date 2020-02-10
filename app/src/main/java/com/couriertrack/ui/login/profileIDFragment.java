package com.couriertrack.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.couriertrack.R;
import com.couriertrack.api_model.SignUpModel;
import com.couriertrack.api_model.UpdateIDModel;
import com.couriertrack.databinding.FragmentIdBinding;
import com.couriertrack.image_utils.FragmentSelectImageDialog;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.ui.courier.home.HomeCourier;
import com.couriertrack.ui.home.Home;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

import static com.couriertrack.image_utils.ImageUtils.getOutputMediaFile;

/**
 * A simple {@link Fragment} subclass.
 */

public class profileIDFragment extends BaseFragment implements View.OnClickListener, FragmentSelectImageDialog.ImageSelectListener {

    private static final int IMAGE_CAPTURE = 1;
    private static final String TAG = "ProfileIDFragment";
    FragmentIdBinding binding;
    public static final int CompressSize = 800;
    FragmentSelectImageDialog selectImageDialog;
    private String selectedImagePath = "";
    boolean isfront, isback;
    UpdateIDModel.ProfileIDReq profileIDReq;

    boolean isBackImageUpdated;
    boolean isFrontImageUpdated;

    private File mediaFile;


    public static profileIDFragment newInstance(Bundle bundle)
    {
        profileIDFragment fragment = new profileIDFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_id, container, false);

        init();
        setspinner();

        return binding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    private void setspinner()
    {

        String[] shifttype = {"Select Type of ID", "AADHAR", "VOTER ID", "DRIVING LICENCE", "PAN CARD", "PASSPORT"};

        // Creating adapter for spinner
        ArrayAdapter<String> spinshiftAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spin_item, shifttype);

        // Drop down layout style - list view with radio button
        spinshiftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        binding.spinidtype.setAdapter(spinshiftAdapter);
        int position =0;
        for(String s : shifttype)
        {
           if( s.equalsIgnoreCase(appPref.getString(AppPref.DOCTYPE)))
           {
               break;
           }
           position++;
        }

        if(position >= shifttype.length)
            position = 0;

        binding.spinidtype.setSelection(position);

        profileIDReq.setDoc_type(spinshiftAdapter.getItem(position));

        binding.llAction.setVisibility(View.GONE);


    }

    private void init()
    {

        selectImageDialog = FragmentSelectImageDialog.newInstance();
        selectImageDialog.setListener(this);
        binding.btnverifyid.setOnClickListener(this);
        binding.ivBackpic.setOnClickListener(this);
        binding.ivForntpic.setOnClickListener(this);
        binding.etidnumber.setText(appPref.getString(AppPref.DOCNUMBER));

        isBackImageUpdated = false;
        isFrontImageUpdated = false;

        profileIDReq = new UpdateIDModel.ProfileIDReq();
        profileIDReq.setFront_img(appPref.getString(AppPref.FRONTIMAGE));
        profileIDReq.setBack_img(appPref.getString(AppPref.BACKIMAGE));
        profileIDReq.setDoc_number(appPref.getString(AppPref.DOCNUMBER));

        Glide.with(getActivity().getApplicationContext()).load(appPref.getString(AppPref.BACKIMAGE)).into(binding.ivBackpic);
        Glide.with(getActivity().getApplicationContext()).load(appPref.getString(AppPref.FRONTIMAGE)).into(binding.ivForntpic);

    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnverifyid:
                {
                if (isValid())
                {
                    setsignupdata();
                }
                break;
            }
            case R.id.iv_forntpic:
                {
                isfront = true;
                isback = false;
                cameraIntent();
                //selectImageDialog.show(getChildFragmentManager(), "dialog");
                break;
            }
            case R.id.iv_backpic: {
                isfront = false;
                isback = true;
                cameraIntent();
                //selectImageDialog.show(getChildFragmentManager(), "dialog");
                break;
            }
        }
    }

    private boolean isValid()
    {
        if (isEmpty(binding.etidnumber, R.string.hint_id_number)) {
            return false;
        } else if (binding.spinidtype.getSelectedItem().toString().equalsIgnoreCase("Select Type of ID")) {
            showToast("Select Type of ID");
            return false;
        } else if (profileIDReq != null && TextUtils.isEmpty(profileIDReq.getFront_img())) {
            showToast("Add Front Image");
            return false;
        } else if (profileIDReq != null && TextUtils.isEmpty(profileIDReq.getBack_img())) {
            showToast("Add Back Image");
            return false;
        }

        return true;
    }

    private void setsignupdata()
    {
        if (profileIDReq != null)
        {
            profileIDReq.setDoc_type(binding.spinidtype.getSelectedItem().toString());
            profileIDReq.setDoc_number(binding.etidnumber.getText().toString());

            callIDUpdateReq(profileIDReq);
        }

    }

    private void callIDUpdateReq(UpdateIDModel.ProfileIDReq profileIDReq)
    {

        AppLog.e(TAG, "profileIDReq : " + profileIDReq);
        showLoading();

        RequestBody userType =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(profileIDReq.getUser_type()));


        MultipartBody.Part frontimg = null;
        if(isFrontImageUpdated)
        {
            File filefront = new File(profileIDReq.getFront_img());
            RequestBody fileBodyfront = RequestBody.create(MediaType.parse("multipart/form-data"), filefront);
            frontimg = MultipartBody.Part.createFormData("front_img", filefront.getName(), fileBodyfront);
        }
        else
            frontimg = null;

        MultipartBody.Part backimg = null;
        if(isBackImageUpdated)
        {
            File fileback = new File(profileIDReq.getBack_img());
            RequestBody fileBodyback = RequestBody.create(MediaType.parse("multipart/form-data"), fileback);
            backimg = MultipartBody.Part.createFormData("back_img", fileback.getName(), fileBodyback);
        }


        RequestBody doctype =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(profileIDReq.getDoc_type()));
        RequestBody docnumber =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(profileIDReq.getDoc_number()));

        int utype ;
        if (appPref.getString(AppPref.USER_TYPE).equals("customer"))
            utype = 1;
        else
            utype = 2;

        RequestBody user_type = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(utype));
        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(appPref.getString(AppPref.USER_ID)));
        RequestBody token = RequestBody.create(MediaType.parse("text/plain"), appPref.getString(AppPref.FCM_TOKEN));
        RequestBody device_type = RequestBody.create(MediaType.parse("text/plain"), "android");


        apiService.updateID(user_id , device_type , user_type , token , frontimg , backimg , doctype , docnumber)//loginReq
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<UpdateIDModel.UpdateProfileIDRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<UpdateIDModel.UpdateProfileIDRes> updateProfileRes) {
                        AppLog.e(TAG, "updateProfileRes :" + updateProfileRes);
                        if (isSuccess(updateProfileRes, updateProfileRes.body())) {
                            onUpdateIDRes(updateProfileRes.body());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        onFailure(e);
                        AppLog.e(TAG, "updateProfileRes ERROR :" + e.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete()
                    {
                        onDone();
                        AppLog.e(TAG, "updateProfileRes Completed");
                    }
                });


    }

    private void onUpdateIDRes(UpdateIDModel.UpdateProfileIDRes updateRes)
    {
        if (updateRes.isStatus())
        {
            appPref.set(AppPref.DOCTYPE, updateRes.getDoc_type());
            appPref.set(AppPref.DOCNUMBER, updateRes.getDoc_number());
            appPref.set(AppPref.FRONTIMAGE, updateRes.getFront_img());
            appPref.set(AppPref.BACKIMAGE, updateRes.getBack_img());
            appPref.set(AppPref.USERSTATUS, updateRes.getUser_status());

                if (appPref.getString(AppPref.USER_TYPE).equals("customer"))
                    gotoActivity(Home.class, null, true);
                else
                    gotoActivity(HomeCourier.class, null, true);

        }
    }

    @Override
    public void onImageSelected(String path)
    {

        try
        {
            File newFile = new Compressor(getActivity()).setMaxWidth(CompressSize).compressToFile(new File(path));
            selectedImagePath = newFile.getAbsolutePath();

            // bitmap= ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(selectedImagePath),binding.imageView.getWidth(),binding.imageView.getHeight());//getBitmapFromImage(newFile);//
            if (isfront)
            {
                isFrontImageUpdated = true;
                if (profileIDReq != null)
                {
                    profileIDReq.setFront_img(selectedImagePath);
                }
                Glide.with(getActivity().getApplicationContext()).load(selectedImagePath).into(binding.ivForntpic);
            }

            if (isback)
            {
                isBackImageUpdated = true;
                if (profileIDReq != null)
                {
                    profileIDReq.setBack_img(selectedImagePath);
                }
                Glide.with(getActivity().getApplicationContext()).load(selectedImagePath).into(binding.ivBackpic);
            }

            // callupdateProfile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onImageSelected(ArrayList<String> listImages) {
        selectImageDialog.dismiss();
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

    private void cameraIntent()
    {
        if (hasPermission(context, new String[]{android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE}))
        {
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
        }
        else
        {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        String imagePath;
        if (requestCode == IMAGE_CAPTURE && resultCode == Activity.RESULT_OK)
        {
            imagePath = mediaFile.getAbsolutePath();
            if (!TextUtils.isEmpty(imagePath)) {
                onImageSelected(imagePath);
            } else {
                Toast.makeText(context, "Can't get image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
