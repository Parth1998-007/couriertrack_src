package com.couriertrack.ui.login;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.bumptech.glide.Glide;
import com.couriertrack.R;
import com.couriertrack.api_model.SignUpModel;
import com.couriertrack.databinding.FragmentSignUpBinding;
import com.couriertrack.image_utils.FragmentSelectImageDialog;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.ui.webview.Webview;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;

import static com.couriertrack.image_utils.ImageUtils.getOutputMediaFile;

/**
 * A simple {@link Fragment} subclass.
 */

public class SignUpFragment extends BaseFragment implements View.OnClickListener, FragmentSelectImageDialog.ImageSelectListener {

    private static final String TAG = "SignUpFragment";
    FragmentSignUpBinding binding;
    private FragmentSelectImageDialog selectImageDialog;
    private String selectedImagePath = "";
    public static final int CompressSize = 800;
    private SignUpModel.SignUpReq signUpReq;

    public static SignUpFragment newInstance(Bundle bundle) {
        SignUpFragment fragment = new SignUpFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false);
        //return inflater.inflate(R.layout.fragment_sign_up, container, false);

        init();
        return binding.getRoot();
    }

    private void init() {
        signUpReq = new SignUpModel.SignUpReq();
        selectImageDialog = FragmentSelectImageDialog.newInstance();
        selectImageDialog.setListener(this);
        if (appPref.getString(AppPref.USER_TYPE).equals("courier"))
            binding.llProfilePic.setVisibility(View.VISIBLE);
        binding.btnregister.setOnClickListener(this);
        binding.llProfilePic.setOnClickListener(this);
        AppLog.e(TAG, "FCM TOKEN :" + appPref.getString(AppPref.FCM_TOKEN));

        binding.tvTerms.setOnClickListener(this);
        binding.tvPrivacy.setOnClickListener(this);

        String privacy = "<a href='"+getActivity().getResources().getString(R.string.privacy_policy)+"'>Privacy Policy</a>";//+getResources().getString(R.string.privacy_policy)+
        String terms = "<a href='"+getActivity().getResources().getString(R.string.terms_condition)+"'>Terms&Conditions</a>";
        binding.tvTermspolicy.setMovementMethod(LinkMovementMethod.getInstance());
        binding.tvTermspolicy.setText("I agree to "+Html.fromHtml(terms));//"I agree to "+Html.fromHtml(terms)+" and "+Html.fromHtml(privacy)
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnregister: {
                if (isValid()) {
                    setsignupdata();
                }
                break;
            }

            case R.id.ll_profile_pic:
            {
                selectImageDialog.show(getChildFragmentManager(), "dialog");
                break;
            }
            case R.id.tv_terms:{
               /* Uri uri = Uri.parse(""+getActivity().getResources().getString(R.string.terms_condition)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);*/
                Bundle b = new Bundle();
                b.putString("url",getActivity().getResources().getString(R.string.terms_condition));
                b.putString("title","Terms and Condition");
                gotoActivity(Webview.class,b,false);
                break;
            }
            case R.id.tv_privacy:{
               /* Uri uri = Uri.parse(""+getActivity().getResources().getString(R.string.privacy_policy)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);*/
                Bundle b = new Bundle();
                b.putString("url",getActivity().getResources().getString(R.string.privacy_policy));
                b.putString("title","Privacy Policy");
                gotoActivity(Webview.class,b,false);
                break;
            }
        }
    }

    private boolean isValid() {

        if (appPref.getString(AppPref.USER_TYPE).equals("customer")) {
            if (isEmpty(binding.etFirstname, R.string.hint_first_name)) {
                return false;
            } else if (isEmpty(binding.etLastname, R.string.hint_last_name)) {
                return false;
            } else if (isEmpty(binding.etemail, R.string.hint_email)) {
                return false;
            } else if (isEmpty(binding.etPassword, R.string.hint_password)) {
                return false;
            } else if (isEmpty(binding.etconfirmPassword, R.string.hint_pass_again)) {
                return false;
            } else if (!binding.etPassword.getText().toString().equals(binding.etconfirmPassword.getText().toString())) {
                showToast("Both password must be same");
                return false;
            } else if (ischecked()) {
                return false;
            }else if(!binding.cbtermsofuse.isChecked()){
                showToast("Please Agree Terms&Conditions and Privacy Policy");
                return false;
            }
        } else {
            if (TextUtils.isEmpty(selectedImagePath)) {
                showToast("Select Profile Pic");
                return false;
            } else if (isEmpty(binding.etFirstname, R.string.hint_first_name)) {
                return false;
            } else if (isEmpty(binding.etLastname, R.string.hint_last_name)) {
                return false;
            } else if (isEmpty(binding.etemail, R.string.hint_email)) {
                return false;
            } else if (isEmpty(binding.etPassword, R.string.hint_password)) {
                return false;
            } else if (isEmpty(binding.etconfirmPassword, R.string.hint_pass_again)) {
                return false;
            } else if (!binding.etPassword.getText().toString().equals(binding.etconfirmPassword.getText().toString())) {
                showToast("Both password must be same");
                return false;
            } else if (ischecked()) {
                return false;
            }else if(!binding.cbtermsofuse.isChecked()){
                showToast("Please Agree Terms&Conditions and Privacy Policy");
                return false;
            }
        }
        return true;
    }

    private boolean ischecked() {
        if (!binding.rbfemale.isChecked() && !binding.rbmale.isChecked()) {
            binding.rbmale.requestFocus();
            binding.rbfemale.requestFocus();
            showToast("Select gender");
            return true;
        }
        return false;
    }


    private void setsignupdata()
    {
        signUpReq.setDevice_type("android");
        if (appPref.getString(AppPref.USER_TYPE).equals("customer"))
            signUpReq.setUser_type(1);
        else
            signUpReq.setUser_type(2);
        signUpReq.setToken(appPref.getString(AppPref.FCM_TOKEN));
        signUpReq.setFirst_name(binding.etFirstname.getText().toString());
        signUpReq.setLast_name(binding.etLastname.getText().toString());
        signUpReq.setEmail(binding.etemail.getText().toString());
        signUpReq.setPassword(binding.etPassword.getText().toString());

        if (binding.rbmale.isChecked())
        {
            signUpReq.setGender(1);
        }
        else if (binding.rbfemale.isChecked())
        {
            signUpReq.setGender(2);
        }

        Bundle b = new Bundle();
        b.putString("signupreq", new Gson().toJson(signUpReq));
        ((SignUp) getActivity()).changeFrag(VerifyFragment.newInstance(b), true, false);

    }

    @Override
    public void onImageSelected(String path)
    {
        selectImageDialog.dismiss();
        try
        {
            File newFile = new Compressor(getActivity()).setMaxWidth(CompressSize).compressToFile(new File(path));
            selectedImagePath = newFile.getAbsolutePath();

            // bitmap= ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(selectedImagePath),binding.imageView.getWidth(),binding.imageView.getHeight());//getBitmapFromImage(newFile);//
            if (signUpReq != null) {
                signUpReq.setProfile_pic(selectedImagePath);
            }
            Glide.with(getActivity().getApplicationContext()).load(selectedImagePath).into(binding.ivProfilepic);

            // callupdateProfile();
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
