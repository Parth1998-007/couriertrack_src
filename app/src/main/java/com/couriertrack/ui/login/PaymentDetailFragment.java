package com.couriertrack.ui.login;

import android.databinding.DataBindingUtil;
import android.os.Bundle;


import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.couriertrack.R;
import com.couriertrack.api_model.BankDetailModel;
import com.couriertrack.databinding.FragmentPaymentDetailBinding;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.utils.AppLog;
import com.couriertrack.utils.AppPref;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentDetailFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "PaymentDetailFragment";
    FragmentPaymentDetailBinding binding;
    public static PaymentDetailFragment newInstance(Bundle bundle) {
        PaymentDetailFragment fragment = new PaymentDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_payment_detail, container, false);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment_detail,container,false);

        init();

        return binding.getRoot();
    }

    private void init() {
        //binding.tvEditaccountdetail.setOnClickListener(this);
        binding.btnupdate.setOnClickListener(this);

        getBankDetails();

        if(!TextUtils.isEmpty(appPref.getString(AppPref.ACCOUNTNAME)) && !TextUtils.isEmpty(appPref.getString(AppPref.ACCOUNTNUMBER)) && !TextUtils.isEmpty(appPref.getString(AppPref.IFSCCODE))){
            binding.btnupdate.setText("UPDATE");
        }
        else
        {
            binding.btnupdate.setText("ADD");
        }

        binding.tvAccountname.setEnabled(true);
        binding.tvaccountnumber.setEnabled(true);
        binding.tvIfsccode.setEnabled(true);
        binding.tvAccountholdername.setEnabled(true);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnupdate:
            {


                if(isEmpty(binding.tvAccountname , R.string.lbl_fill_details) || isEmpty(binding.tvaccountnumber  , R.string.lbl_fill_details)|| isEmpty(binding.tvIfsccode , R.string.lbl_fill_details) || isEmpty(binding.tvAccountname , R.string.lbl_fill_details))
                {

                }
                else
                {
                    callUpdateBankDetailAPI();
                }
                break;
            }

        }
    }


    private void getBankDetails()
    {
        BankDetailModel.BankDetailReq bankDetailReq = new BankDetailModel.BankDetailReq();
        bankDetailReq.setUser_id(appPref.getString(AppPref.USER_ID));

        AppLog.e(TAG , "BankDetailReq : "+bankDetailReq.toString());

        apiService.getBankDetail(bankDetailReq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<BankDetailModel.BankDetailRes>>()
                {
                    @Override
                    public void onSubscribe(Disposable d)
                    {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<BankDetailModel.BankDetailRes> bankDetailRes)
                    {
                        AppLog.e(TAG, "bankDetailRes :" + bankDetailRes.body());
                        if(isSuccess(bankDetailRes,bankDetailRes.body()))
                        {
                            gotBankDetailRes(bankDetailRes.body());
                        }
                    }

                    @Override
                    public void onError(Throwable e)
                    {
                        onFailure(e);
                    }

                    @Override
                    public void onComplete()
                    {
                        onDone();
                    }
                });

    }

    private void callUpdateBankDetailAPI()
    {
        BankDetailModel.BankDetailReq bankDetailReq = new BankDetailModel.BankDetailReq();
        bankDetailReq.setAccount_num(binding.tvaccountnumber.getText().toString());
        bankDetailReq.setBank_name(binding.tvAccountname.getText().toString());
        bankDetailReq.setIfsc_code(binding.tvIfsccode.getText().toString());
        bankDetailReq.setAccount_name(binding.tvAccountholdername.getText().toString());
        bankDetailReq.setUser_id(appPref.getString(AppPref.USER_ID));

        AppLog.e(TAG , "BankDetailReq : "+bankDetailReq.toString());

        apiService.bankDetail(bankDetailReq)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<BankDetailModel.BankDetailRes>>()
                {
                    @Override
                    public void onSubscribe(Disposable d) {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<BankDetailModel.BankDetailRes> bankDetailRes)
                    {
                        AppLog.e(TAG, "bankDetailRes :" + bankDetailRes.body());
                        if(isSuccess(bankDetailRes,bankDetailRes.body()))
                        {
                            onbankDetailRes(bankDetailRes.body());
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

    private void onbankDetailRes(BankDetailModel.BankDetailRes bankDetailRes) {
        if(bankDetailRes.isStatus()){
            showToast(bankDetailRes.getMsg());
            appPref.set(AppPref.ACCOUNTNAME,binding.tvAccountname.getText().toString());
            appPref.set(AppPref.ACCOUNTNUMBER,binding.tvaccountnumber.getText().toString());
            appPref.set(AppPref.IFSCCODE,binding.tvIfsccode.getText().toString());
            appPref.set(AppPref.ACCOUNTHOLDERNAME , binding.tvAccountholdername.getText().toString());
        }

    }

    private void gotBankDetailRes(BankDetailModel.BankDetailRes bankDetailRes) {
        if(bankDetailRes.isStatus())
        {
            binding.tvAccountname.setText(bankDetailRes.getAccount_num());
            binding.tvAccountholdername.setText(bankDetailRes.getAccount_name());
            binding.tvaccountnumber.setText(bankDetailRes.getAccount_num());
            binding.tvIfsccode.setText(bankDetailRes.getIfsc_code());
        }

    }
}
