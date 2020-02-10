package com.couriertrack.ui.courier.wallet;


import android.databinding.DataBindingUtil;
import android.os.Bundle;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couriertrack.R;
import com.couriertrack.api_model.TransactionModel;
import com.couriertrack.databinding.FragmentWalletBinding;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.utils.AppLog;


/**
 * A simple {@link Fragment} subclass.
 */
public class WalletFragment extends BaseFragment {


    private static final String TAG = "HelpSupportFragment";
    WalletAdapter walletAdapter;
    FragmentWalletBinding binding;
    public static WalletFragment newInstance(Bundle bundle) {

        WalletFragment fragment = new WalletFragment();
        fragment.setArguments(bundle);
        return fragment;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_wallet, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wallet,container,false);
        init();
        setwalletTransaction();
        return binding.getRoot();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        callTransactionAPI();
    }

    private void callTransactionAPI()
    {
        TransactionModel.TransactionReq transactionReq = new TransactionModel.TransactionReq();
        transactionReq.setUserId(appPref.getUserId());

        AppLog.e(TAG,"transactionReq: "+transactionReq);

        apiService.getTrasactionList(transactionReq)//loginReq
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<TransactionModel.TransactionRes>>() {
                    @Override
                    public void onSubscribe(Disposable d)
                    {
                        subscribe(d);
                    }

                    @Override
                    public void onNext(Response<TransactionModel.TransactionRes> transactionRes)
                    {
                        AppLog.e(TAG, "transactionRes :" + transactionRes + transactionRes.code());
                        if (isSuccess(transactionRes, transactionRes.body()))
                        {
                            ontransactionRes(transactionRes.body());
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

    private void ontransactionRes(TransactionModel.TransactionRes transactionRes) {
        if(transactionRes.isStatus()){
            binding.tvBalance.setText("₹ "+transactionRes.getWallet());

            walletAdapter.clear();
            walletAdapter.addTransactionList(transactionRes.getTransactionslist());
        }
    }

    private void setwalletTransaction() {
        binding.rvtransaction.setAdapter(walletAdapter);
    }

    private void init() {
        walletAdapter = new WalletAdapter();
    }

}
