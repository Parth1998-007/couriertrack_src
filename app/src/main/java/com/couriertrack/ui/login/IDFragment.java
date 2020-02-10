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
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.couriertrack.R;
import com.couriertrack.api_model.SignUpModel;
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

public class IDFragment extends BaseFragment implements View.OnClickListener, FragmentSelectImageDialog.ImageSelectListener {


    private static final int IMAGE_CAPTURE = 1;
    private File mediaFile;

    private static final String TAG = "IDFragment";
    FragmentIdBinding binding;
    public static final int CompressSize = 800;
    FragmentSelectImageDialog selectImageDialog;
    private String selectedImagePath = "";
    boolean isfront, isback;
    SignUpModel.SignUpReq signUpReq;
    private RequestBody password;

    public static IDFragment newInstance(Bundle bundle) {
        IDFragment fragment = new IDFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_id, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_id, container, false);

        init();
        setspinner();

        return binding.getRoot();
    }

    private void setspinner() {

        String[] shifttype = {"Select Type of ID", "AADHAR", "VOTER ID", "DRIVING LICENSE", "PAN CARD", "PASSPORT"};

        // Creating adapter for spinner
        ArrayAdapter<String> spinshiftAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spin_item, shifttype);

        // Drop down layout style - list view with radio button
        spinshiftAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        binding.spinidtype.setAdapter(spinshiftAdapter);
    }

    private void init() {
        if (getArguments() != null) {
            Type type = new TypeToken<SignUpModel.SignUpReq>() {
            }.getType();
            signUpReq = new Gson().fromJson(getArguments().getString("signupreq"), type);
        }

        selectImageDialog = FragmentSelectImageDialog.newInstance();
        selectImageDialog.setListener(this);
        binding.btnverifyid.setOnClickListener(this);
        binding.ivBackpic.setOnClickListener(this);
        binding.ivForntpic.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnverifyid: {
                if (isValid()) {
                    setsignupdata();
                }
                break;
            }
            case R.id.iv_forntpic: {
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

    private boolean isValid() {
        if (isEmpty(binding.etidnumber, R.string.hint_id_number)) {
            return false;
        } else if (binding.spinidtype.getSelectedItem().toString().equalsIgnoreCase("Select Type of ID")) {
            showToast("Select Type of ID");
            return false;
        } else if (signUpReq != null && TextUtils.isEmpty(signUpReq.getFront_img())) {
            showToast("Add Front Image");
            return false;
        } else if (signUpReq != null && TextUtils.isEmpty(signUpReq.getBack_img())) {
            showToast("Add Back Image");
            return false;
        }

        return true;
    }

    private void setsignupdata() {
        if (signUpReq != null) {
            signUpReq.setDoc_type(binding.spinidtype.getSelectedItem().toString());
            signUpReq.setDoc_number(binding.etidnumber.getText().toString());
            signUpReq.setToken(appPref.getString(AppPref.FCM_TOKEN));

            callSignUpReq(signUpReq);

        }


    }

    private void callSignUpReq(SignUpModel.SignUpReq signupReq) {

        AppLog.e(TAG, "signupReq : " + signupReq);
        showLoading();

        RequestBody devicetype =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(signupReq.getDevice_type()));
        RequestBody userType =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(signupReq.getUser_type()));
        RequestBody email =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(signupReq.getEmail()));
        RequestBody firstname =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(signupReq.getFirst_name()));
        RequestBody lastname =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(signupReq.getLast_name()));
         password =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(signupReq.getPassword()));
        RequestBody mobile =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(signupReq.getMobile()));
        RequestBody token =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(signupReq.getToken()));

        MultipartBody.Part profilePic = null;
        if(signupReq.getUser_type() == 2)
        {
            File filepropic = new File(signupReq.getProfile_pic());
            RequestBody fileBodyProPic = RequestBody.create(MediaType.parse("multipart/form-data"), filepropic);
            profilePic = MultipartBody.Part.createFormData("profile", filepropic.getName(), fileBodyProPic);
        }

        File filefront = new File(signupReq.getFront_img());
        RequestBody fileBodyfront = RequestBody.create(MediaType.parse("multipart/form-data"), filefront);
        MultipartBody.Part frontimg = MultipartBody.Part.createFormData("front_img", filefront.getName(), fileBodyfront);

        File fileback = new File(signupReq.getBack_img());
        RequestBody fileBodyback = RequestBody.create(MediaType.parse("multipart/form-data"), fileback);
        MultipartBody.Part backimg = MultipartBody.Part.createFormData("back_img", fileback.getName(), fileBodyback);


        RequestBody doctype =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(signupReq.getDoc_type()));
        RequestBody docnumber =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(signupReq.getDoc_number()));
        RequestBody gender =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(signupReq.getGender()));

        apiService.signup(devicetype, userType, email, firstname, lastname, password, mobile, token, frontimg, backimg, doctype, docnumber, gender , profilePic)//loginReq
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<SignUpModel.SignUpRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<SignUpModel.SignUpRes> signupRes) {
                        AppLog.e(TAG, "signUpRes :" + signupRes);
                        if (isSuccess(signupRes, signupRes.body())) {
                            onSignUpRes(signupRes.body());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        onFailure(e);
                    }

                    @Override
                    public void onComplete() {
                        onDone();
                    }
                });


    }

    private void onSignUpRes(SignUpModel.SignUpRes signUpRes) {
        if (signUpRes.isStatus()) {
            appPref.set(AppPref.USER_ID, signUpRes.getUser_id());
            appPref.set(AppPref.API_KEY, signUpRes.getApi_key());
            appPref.set(AppPref.NAME, signUpRes.getFirst_name());
            appPref.set(AppPref.EMAIL, signUpRes.getEmail());
            appPref.set(AppPref.GENDER, signUpRes.getGender());
            appPref.set(AppPref.MOBILE, signUpRes.getMobile());
            appPref.set(AppPref.DOCTYPE, signUpRes.getDoc_type());
            appPref.set(AppPref.DOCNUMBER, signUpRes.getDoc_number());
            appPref.set(AppPref.FRONTIMAGE, signUpRes.getFront_img());
            appPref.set(AppPref.BACKIMAGE, signUpRes.getBack_img());
            appPref.set(AppPref.USERSTATUS, signUpRes.getUser_status());
            appPref.set(AppPref.USER_PROFILE , signUpRes.getProfile_image());
            appPref.set(AppPref.IS_LOGIN, true);
            if (signUpRes.getUser_status().equalsIgnoreCase("not_verified")) {
                ((SignUp) getActivity()).changeFrag(SuccessRegFragment.newInstance(null), true, false);
            } else {
                if (appPref.getString(AppPref.USER_TYPE).equals("customer"))
                    gotoActivity(Home.class, null, true);
                else
                    gotoActivity(HomeCourier.class, null, true);

            }

        }
    }

    @Override
    public void onImageSelected(String path) {

        try {
            File newFile = new Compressor(getActivity()).setMaxWidth(CompressSize).compressToFile(new File(path));
            selectedImagePath = newFile.getAbsolutePath();

            // bitmap= ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(selectedImagePath),binding.imageView.getWidth(),binding.imageView.getHeight());//getBitmapFromImage(newFile);//
            if (isfront) {
                if (signUpReq != null) {
                    signUpReq.setFront_img(selectedImagePath);
                }
                Glide.with(getActivity().getApplicationContext()).load(selectedImagePath).into(binding.ivForntpic);
            }

            if (isback) {
                if (signUpReq != null) {
                    signUpReq.setBack_img(selectedImagePath);
                }
                Glide.with(getActivity().getApplicationContext()).load(selectedImagePath).into(binding.ivBackpic);
            }

            // callupdateProfile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onImageSelected(ArrayList<String> listImages)
    {
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
