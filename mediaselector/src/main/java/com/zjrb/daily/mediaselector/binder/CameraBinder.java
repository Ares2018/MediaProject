package com.zjrb.daily.mediaselector.binder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zjrb.daily.mediaselector.R;
import com.zjrb.daily.mediaselector.entity.MediaEntity;
import com.zjrb.daily.mediaselector.listener.OnItemClickListener;

import me.drakeet.multitype.ItemViewBinder;

public class CameraBinder extends ItemViewBinder<MediaEntity, CameraBinder.CameraHolder> {

    OnItemClickListener onItemClickListener;

    class CameraHolder extends RecyclerView.ViewHolder implements View.OnClickListener{



        public CameraHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view == itemView){
                if(onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, getAdapterPosition());
                }
            }
        }
    }

    public CameraBinder(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    protected CameraHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View root = inflater.inflate(R.layout.item_camera, parent, false);
        return new CameraHolder(root);
    }

    @Override
    protected void onBindViewHolder(@NonNull CameraHolder holder, @NonNull MediaEntity item) {

    }

}
