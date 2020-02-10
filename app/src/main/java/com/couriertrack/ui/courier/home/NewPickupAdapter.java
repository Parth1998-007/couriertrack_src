package com.couriertrack.ui.courier.home;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.couriertrack.R;
import com.couriertrack.api_model.AcceptedCourierModel;
import com.couriertrack.api_model.CourierOrderList;
import com.couriertrack.api_model.NewPickupOrderListModel;
import com.couriertrack.databinding.ItemNewpickupBinding;
import com.couriertrack.ui.Base;
import com.couriertrack.ui.BaseFragment;
import com.couriertrack.ui.courier.myorder.MyOrderCourierDetail;
import com.couriertrack.utils.AppLog;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class NewPickupAdapter extends RecyclerView.Adapter<NewPickupAdapter.MyDashboard> {
    public String TAG = "NewPickupAdapter";
    private Context context;
    private ArrayList<NewPickupOrderListModel.NewPickupOrderListDetail> pickupOrders = new ArrayList<>();
    private ClickAcceptPickup viewAcceptItemClick;
    private ArrayList<CourierOrderList.OrderListDetail> myorderList;
    private String fragmentName = "NewPickupFragment";

    @NonNull
    @Override
    public NewPickupAdapter.MyDashboard onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        ItemNewpickupBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_newpickup, viewGroup, false);
        return new MyDashboard(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NewPickupAdapter.MyDashboard viewHolder, final int i) {
        dataDisplay(viewHolder, i);
    }

    void dataDisplay(NewPickupAdapter.MyDashboard viewHolder, final int i) {
        if (this.fragmentName.equals("NewPickupFragment")) {
            final int order_id = pickupOrders.get(i).getOrder_id();
            final String order_status = pickupOrders.get(i).getOrder_status();
            viewHolder.binding.tvStatusCost.setText("To Earn");
            viewHolder.binding.tvOrderid.setText("" + pickupOrders.get(i).getOrder_id());
            viewHolder.binding.tvStatus.setText("" + pickupOrders.get(i).getCost());
            viewHolder.binding.tvPickup.setText("" + pickupOrders.get(i).getPickup_address());
            viewHolder.binding.tvDrop.setText("" + pickupOrders.get(i).getDrop_address());
            viewHolder.binding.tvAcceptPickup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                NewPickupOrderListModel.User user = pickupOrders.get(i).getUser();
                    viewAcceptItemClick.onClickAccepyPickup(pickupOrders.get(i), i);

                }
            });
            viewHolder.binding.tvViewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Base.activity_name = "NewPickupFragment";
                    Intent intent = new Intent(context, MyOrderCourierDetail.class);
                    intent.putExtra("pickup_order_id", order_id + "");
                    intent.putExtra("pickup_status", order_status);
                    context.startActivity(intent);
//                Toast.makeText(context,"View Detail Click Pos : "+i,Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            viewHolder.binding.tvAcceptPickup.setVisibility(View.GONE);
            final int order_id = myorderList.get(i).getOrder_id();
            final String order_status = myorderList.get(i).getOrder_status();
            viewHolder.binding.tvStatusCost.setText("STATUS");
            viewHolder.binding.tvOrderid.setText("" + myorderList.get(i).getOrder_id());
            viewHolder.binding.tvStatus.setText("" + myorderList.get(i).getOrder_status());
            viewHolder.binding.tvPickup.setText("" + myorderList.get(i).getPickup_address());
            viewHolder.binding.tvDrop.setText("" + myorderList.get(i).getDrop_address());
            viewHolder.binding.llBtnView.setGravity(Gravity.CENTER);
            viewHolder.binding.tvViewDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Base.activity_name = "MyOrderCourierFragment";
                    Intent intent = new Intent(context, MyOrderCourierDetail.class);
                    intent.putExtra("pickup_order_id", order_id + "");
                    intent.putExtra("pickup_status", order_status);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (this.fragmentName.equals("NewPickupFragment")) {
            return pickupOrders != null ?
                    pickupOrders.size() : 0;
        } else {
            return myorderList != null ?
                    myorderList.size() : 0;
        }
    }


    public void addData(ArrayList<NewPickupOrderListModel.NewPickupOrderListDetail> pickupOrders, String fragment_name) {
        this.fragmentName = fragment_name;
        this.pickupOrders.addAll(pickupOrders);
        notifyDataSetChanged();
    }

    public void clear() {
        if (this.fragmentName.equals("NewPickupFragment")) {
            this.pickupOrders.clear();
        } else {
            this.myorderList.clear();
        }
        notifyDataSetChanged();
    }

    public void remove(int position) {
        this.pickupOrders.remove(position);
        notifyDataSetChanged();
    }

    public void addMyOrderData(ArrayList<CourierOrderList.OrderListDetail> myorderList, String fragment_name) {
        this.fragmentName = fragment_name;
        this.myorderList = myorderList;

    }


    public class MyDashboard extends RecyclerView.ViewHolder {
        ItemNewpickupBinding binding;

        public MyDashboard(@NonNull ItemNewpickupBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface ClickAcceptPickup {
        public void onClickAccepyPickup(NewPickupOrderListModel.NewPickupOrderListDetail order, int pos);
    }

    public void setViewAcceptItemClick(ClickAcceptPickup viewAcceptItemClick) {
        this.viewAcceptItemClick = viewAcceptItemClick;
    }

}
