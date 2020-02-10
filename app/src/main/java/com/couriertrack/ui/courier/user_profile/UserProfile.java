package com.couriertrack.ui.courier.user_profile;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.couriertrack.R;
import com.couriertrack.api_model.BaseRes;
import com.couriertrack.api_model.PasswordChangeModel;
import com.couriertrack.api_model.SendOtpModel;
import com.couriertrack.api_model.UpdateProfileModel;
import com.couriertrack.databinding.ActivityUserProfileBinding;
import com.couriertrack.image_utils.FragmentSelectImageDialog;
import com.couriertrack.ui.Base;
import com.couriertrack.ui.courier.home.HomeCourier;
import com.couriertrack.ui.home.Home;
import com.couriertrack.ui.login.BankDetails;
import com.couriertrack.ui.login.ProfileID;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;

import java.io.File;
import java.io.IOException;
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

public class UserProfile extends Base implements View.OnClickListener, FragmentSelectImageDialog.ImageSelectListener {
    ActivityUserProfileBinding binding;
    public static String TAG = "UserProfile";


    FragmentSelectImageDialog selectImageDialog;
    private String selectedImagePath = "";

    public static final int CompressSize = 800;
    boolean ifProfileImageChanged;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile);
        init();
        setToolbar();
        setTitle("My Profile");
        enableBack(true);
    }

    void init()
    {
        binding.etUserName.setText(appPref.getString(AppPref.NAME));
        binding.etEmail.setText(appPref.getString(AppPref.EMAIL));
        binding.tvNumber.setText(appPref.getString(AppPref.MOBILE));
        binding.btnResetPassword.setOnClickListener(this);
        binding.tvNumber.setOnClickListener(this);

        binding.btnUpdateProfile.setOnClickListener(this);
        binding.btnEditID.setOnClickListener(this);
        binding.ivUserPick.setOnClickListener(this);
        binding.btnBankAccount.setOnClickListener(this);

        selectImageDialog = FragmentSelectImageDialog.newInstance();
        selectImageDialog.setListener(this);

        ifProfileImageChanged = false;

        if(appPref.getString(AppPref.USER_TYPE).equalsIgnoreCase("courier"))
        {
            binding.ivUserPick.setVisibility(View.VISIBLE);

            RequestOptions cropOptions = new RequestOptions().placeholder(R.drawable.icon_driver);

                Glide.with(UserProfile.this)
                        .load(appPref.getString(AppPref.USER_PROFILE))
                        .apply(cropOptions)
                        .into(binding.ivUserPick);
        }
        else
        {
            binding.ivUserPick.setVisibility(View.GONE);
            binding.btnBankAccount.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed()
    {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            super.onBackPressed();
        } else {
           // gotoActivity(HomeCourier.class, null, true);
            finish();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_resetPassword:
            {
                showChangePasswordDialog("Change Password", "Cancel", "Update");
                break;
            }

            case R.id.btn_editID:
            {
                gotoActivity(ProfileID.class,null,false);
                break;
            }

            case R.id.tv_number:
            {

            }
            break;

            case R.id.btn_bankAccount:
            {
                gotoActivity(BankDetails.class,null,false);
            }
            break;

            case R.id.btn_updateProfile :
            {
                callUpdateProfileAPI();
            }
                break;

            case R.id.iv_user_pick:

                selectImageDialog.show(getSupportFragmentManager(), "dialog");

                break;
        }
    }

    private void callUpdateProfileAPI()
    {
        RequestBody userId =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(appPref.getString(AppPref.USER_ID)));

        RequestBody userType =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(appPref.getString(AppPref.USER_TYPE).equalsIgnoreCase("customer")?1: 2));
        RequestBody email =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(binding.etEmail.getText()));
        RequestBody firstname =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(binding.etUserName.getText()));
        RequestBody lastname =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(""));

        RequestBody mobile =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(binding.tvNumber.getText().toString()));
        RequestBody gender =
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(appPref.getInt(AppPref.GENDER)));

        MultipartBody.Part profileimg = null;
        if(ifProfileImageChanged)
        {
            File filefront = new File(selectedImagePath);
            RequestBody fileBodyfront = RequestBody.create(MediaType.parse("multipart/form-data"), filefront);
            profileimg = MultipartBody.Part.createFormData("profile_image", filefront.getName(), fileBodyfront);
        }

        showLoading();
        apiService.updateProfile(userId , userType , email , firstname , lastname , mobile , profileimg , gender )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<UpdateProfileModel.UpdateProfileRes>>() {
                    @Override
                    public void onSubscribe(Disposable d)
                    {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<UpdateProfileModel.UpdateProfileRes> updateProfileRes)
                    {
                        hideLoading();
                        if (isSuccess(updateProfileRes, updateProfileRes.body()))
                        {
                            onProfileUpdated(updateProfileRes.body());
                            showToast("Profile Updated Successfully !");
                        }
                        else
                        {
                            showToast(""+updateProfileRes.message());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }

                });

    }

    private void onProfileUpdated(UpdateProfileModel.UpdateProfileRes updateProfileRes)
    {

        appPref.set(AppPref.NAME, updateProfileRes.getFirst_name());
        appPref.set(AppPref.EMAIL, updateProfileRes.getEmail());

        appPref.set(AppPref.MOBILE, updateProfileRes.getMobile());
        appPref.set(AppPref.USER_PROFILE , updateProfileRes.getProfile_image());
        appPref.set(AppPref.USERSTATUS, updateProfileRes.getUser_status());

        binding.tvNumber.setText(updateProfileRes.getMobile());
        binding.etEmail.setText(updateProfileRes.getEmail());
        binding.etUserName.setText(updateProfileRes.getFirst_name());

        RequestOptions cropOptions = new RequestOptions().placeholder(R.drawable.icon_driver);
        if(appPref.getString(AppPref.USER_TYPE).equalsIgnoreCase("courier"))
        Glide.with(UserProfile.this)
        .load(appPref.getString(AppPref.USER_PROFILE))
        .apply(cropOptions)
        .into(binding.ivUserPick);


    }

    private void showChangePasswordDialog(final String msg, String btn_cancel, String btn_okay)
    {

        final Dialog dialog_con = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        dialog_con.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_con.setContentView(R.layout.dialog_password);

        dialog_con.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView con_msg = dialog_con.findViewById(R.id.msg_con);
        con_msg.setText(msg);
        Button pauseOrderOkay = dialog_con.findViewById(R.id.btPauseOrderOkay);
        pauseOrderOkay.setText(btn_okay);

        final EditText etNewPassword = dialog_con.findViewById(R.id.etPassword);
        final EditText etRePassword = dialog_con.findViewById(R.id.etRePassword);

        pauseOrderOkay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(!etNewPassword.getText().toString().equalsIgnoreCase(""))
                    if(etNewPassword.getText().toString().equals(etRePassword.getText().toString()))
                    callChangePasswordAPI(etNewPassword.getText().toString() , dialog_con);
                    else
                    showToast("Password doesn't match , Please correct it.");
            }
        });
        Button pauseOrderCancel = dialog_con.findViewById(R.id.btPauseOrderCancel);
        pauseOrderCancel.setText(btn_cancel);
        pauseOrderCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog_con.dismiss();
            }
        });

        dialog_con.show();
    }

    private void callChangePasswordAPI(String password , final Dialog dialog)
    {
        PasswordChangeModel.ChangePassword changeModel = new PasswordChangeModel.ChangePassword();
        changeModel.setNewPassword(password);
        changeModel.setMobile(appPref.getString(AppPref.MOBILE));

        if(appPref.getString(AppPref.USER_TYPE).equalsIgnoreCase("customer"))
            changeModel.setUserType(1);
        else
            changeModel.setUserType(2);


        showLoading();
        apiService.changePassword(changeModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<BaseRes>>() {
                    @Override
                    public void onSubscribe(Disposable d)
                    {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<BaseRes> changePassRes) {
                        AppLog.e(TAG, "changepassword :" + changePassRes.body());
                        dialog.dismiss();
                        hideLoading();
                        if (isSuccess(changePassRes, changePassRes.body()))
                        {
                            showToast("Password Changed Successfully !");
                        }
                        else
                        {
                            showToast(""+changePassRes.message());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }

                });


    }

    private void sendOTP(SendOtpModel.SendOtpreq sendOtpreq) {

        AppLog.e(TAG, "sendOtpreq : " + sendOtpreq);
        showLoading();
        apiService.sendOtpreq(sendOtpreq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<SendOtpModel.SendOtpRes>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<SendOtpModel.SendOtpRes> sendOtpreqRes) {
                        AppLog.e(TAG, "sendOTPRes :" + sendOtpreqRes.body());
                        if (isSuccess(sendOtpreqRes, sendOtpreqRes.body()))
                        {
                            OnsendOtpRes(sendOtpreqRes.body());
                        }
                    }

                    @Override
                    public void onError(Throwable e)
                    {

                    }

                    @Override
                    public void onComplete()
                    {

                    }
                });


    }

    private void OnsendOtpRes(SendOtpModel.SendOtpRes sendOtpRes)
    {
        if (sendOtpRes.isStatus())
        {
            showToast(sendOtpRes.getMsg());
            appPref.set(AppPref.OTP, sendOtpRes.getOTP());
        }
    }

    @Override
    public void onImageSelected(String path)
    {
        selectImageDialog.dismiss();
        try
        {
            File newFile = new Compressor(UserProfile.this).setMaxWidth(CompressSize).compressToFile(new File(path));
            selectedImagePath = newFile.getAbsolutePath();
            ifProfileImageChanged = true;

            Glide.with(UserProfile.this.getApplicationContext()).load(selectedImagePath).into(binding.ivUserPick);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onImageSelected(ArrayList<String> listImages) {

    }
}
