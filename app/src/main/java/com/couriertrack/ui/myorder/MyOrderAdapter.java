package com.couriertrack.ui.myorder;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.couriertrack.R;
import com.couriertrack.api_model.OrderListModel;
import com.couriertrack.databinding.ItemMyorderBinding;
import com.couriertrack.ui.home.SubscriptionAdapter;
import java.util.ArrayList;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.ViewHolder> {
    Context context;
    ArrayList<OrderListModel.Order> orderlist = new ArrayList<>();
    OrderItemClicked orderItemClicked;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        ItemMyorderBinding binding= DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_myorder, viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.binding.tvOrderid.setText(""+orderlist.get(i).getOrder_id());
        viewHolder.binding.tvOrderstatus.setText(""+orderlist.get(i).getOrder_status());
        viewHolder.binding.tvPickup.setText(""+orderlist.get(i).getPickup_address());
        viewHolder.binding.tvDrop.setText(""+orderlist.get(i).getDrop_address());
    }

    public void setOrderItemClicked(OrderItemClicked orderItemClicked) {
        this.orderItemClicked = orderItemClicked;
    }

    @Override
    public int getItemCount() {
        return orderlist!=null?orderlist.size():0;
    }

    public void addOrders(ArrayList<OrderListModel.Order> orders) {
        this.orderlist.addAll(orders);
        notifyDataSetChanged();
    }

    public void clear() {
        orderlist.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemMyorderBinding binding;
        public ViewHolder(ItemMyorderBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

            binding.llmyOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(orderItemClicked!=null){
                        orderItemClicked.OrderClicked(orderlist.get(getAdapterPosition()).getOrder_id());
                    }
                }
            });

        }
    }

    public interface OrderItemClicked{
        public void OrderClicked(int orderid);
    }
}
