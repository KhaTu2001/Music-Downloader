package com.ddona.music_download_ms3_tunk.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;



public abstract class BaseRecyclerViewAdapter<D, VH extends RecyclerView.ViewHolder> extends
        RecyclerView.Adapter<VH> {

    protected final Context context;
    protected OnItemClickListener onItemClickListener;
    private List<D> data = new ArrayList<>();

    public BaseRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public D getData(int position) {
        return data.get(position);
    }

    public void appendData(List<D> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void clearData() {
        this.data.clear();
        notifyDataSetChanged();
    }

    /**
     * @param onItemClickListener
     */
    public void setOnItemClickListener(
            OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<D> getData() {
        return data;
    }

    public void setData(List<D> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * Item click listener.
     */
    public interface OnItemClickListener {

        void onItemClick(int position);
    }


}
