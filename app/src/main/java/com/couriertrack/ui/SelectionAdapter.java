package com.couriertrack.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.couriertrack.R;
import com.couriertrack.utils.AppLog;

import java.util.ArrayList;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.ViewHolder> {

    private static final String TAG = "SelectionAdapter";
    Context context;
    ArrayList<String> selectionList = new ArrayList<>();
    int selectedpos ;
    Selectitemlistener selectitemlistener;
    String tag;
    public SelectionAdapter(String tag) {
        this.tag = tag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context=viewGroup.getContext();
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_select,viewGroup,false);
        return new ViewHolder(view);
    }

    public void SetSelectItemListener(Selectitemlistener selectitemlistener){
        this.selectitemlistener = selectitemlistener;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

            viewHolder.radioButton.setText(selectionList.get(i));
            AppLog.e(TAG,"bindview "+selectedpos+" : "+i);
            if(selectedpos == i){
                viewHolder.radioButton.setChecked(true);
            }else {
                viewHolder.radioButton.setChecked(false);
            }

    }

    @Override
    public int getItemCount() {
        return selectionList!=null?selectionList.size():0;
    }

    public void clear() {
        selectionList.clear();
    }

    public void additem(ArrayList<String> selectionitem) {
        this.selectionList.addAll(selectionitem);
        notifyDataSetChanged();
    }

    public void additemprimary(int selectedpos,ArrayList<String> selectionitem) {
        this.selectedpos = selectedpos;
        this.selectionList.addAll(selectionitem);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.cbselect);

            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedpos = getAdapterPosition();
                    selectitemlistener.selectitem(tag,selectionList.get(getAdapterPosition()),getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void getselecteditem() {

    }

    public interface Selectitemlistener{
        public void selectitem(String tag, String item, int adapterPosition);

    }
}
