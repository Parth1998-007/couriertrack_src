package com.couriertrack.ui.courier.wallet;

import android.content.Context;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.couriertrack.R;
import com.couriertrack.api_model.TransactionModel;
import com.couriertrack.databinding.ItemWalletBinding;
import com.couriertrack.utils.DateTimeHelper;

import java.util.ArrayList;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.ViewHolder> {
    Context context;
    ArrayList<TransactionModel.Transactions> transactionsList = new ArrayList<>();
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
       /* View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_wallet,viewGroup,false);
        return new ViewHolder(view);*/
        ItemWalletBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_wallet, viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvText.setText(""+transactionsList.get(i).getText());
        viewHolder.binding.tvAmount.setText("₹ "+transactionsList.get(i).getAmount());
        viewHolder.binding.tvTransactiontime.setText(""+ DateTimeHelper.convertFormat(transactionsList.get(i).getCreated_date(),"yyyy-MM-dd HH:mm:ss","dd MMM yyyy, HH:mm a"));
    }

    @Override
    public int getItemCount() {
        return transactionsList != null?transactionsList.size():0;
    }

    public void clear() {
        transactionsList.clear();
    }

    public void addTransactionList(ArrayList<TransactionModel.Transactions> transactionslist) {
        this.transactionsList.addAll(transactionslist);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemWalletBinding binding;
        public ViewHolder(ItemWalletBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
