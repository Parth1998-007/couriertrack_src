package com.couriertrack.ui.home.support;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.couriertrack.R;
import com.couriertrack.api_model.TransactionModel;
import com.couriertrack.databinding.FragmentHelpsupportBinding;
import com.couriertrack.databinding.FragmentWalletBinding;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.ui.courier.wallet.WalletAdapter;
import com.couriertrack.utils.AppLog;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */

public class HelpSupportFragment extends BaseFragment {

    private static final String TAG = "HelpSupportFragment";
    WalletAdapter walletAdapter;
    FragmentHelpsupportBinding binding;

    public static HelpSupportFragment newInstance(Bundle bundle)
    {
        HelpSupportFragment fragment = new HelpSupportFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_wallet, container, false);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_helpsupport,container,false);
        init();
        return binding.getRoot();
    }

    @Override
    public void onStart()
    {
        super.onStart();

    }

    private void init()
    {
        binding.tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:help.passonn@gmail.com"));//passonnservices@gmail.com"

                try {
                    startActivity(emailIntent);
                } catch (ActivityNotFoundException e) {
                    //TODO: Handle case where no email app is available
                    ((HelpSupportActivity)getActivity()).showToast("Unable To Open Email Application");
                }

            }
        });
    }

}
