package com.demo.furnitureapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.furnitureapp.R;
import com.demo.furnitureapp.databinding.ItemFurnitureListBinding;
import com.demo.furnitureapp.model.Furniture;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FurnitureAdapter extends RecyclerView.Adapter<FurnitureAdapter.ItemBlogViewHolder> {

    private ArrayList<Furniture> furnitureArrayList;
    private final OnItemClick clickItem;

    public interface OnItemClick {
        void onClick(Furniture blog);
    }

    public FurnitureAdapter(ArrayList<Furniture> blogArrayList, OnItemClick clickItem) {
        this.furnitureArrayList = blogArrayList;
        this.clickItem = clickItem;
    }

    @NonNull
    @Override
    public ItemBlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFurnitureListBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_furniture_list,
                parent,
                false
        );
        return new ItemBlogViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemBlogViewHolder holder, int position) {
        Furniture furniture = furnitureArrayList.get(position);
        holder.bindItems(furniture);
    }

    @Override
    public int getItemCount() {
        return furnitureArrayList.size();
    }

    public void submitData(ArrayList<Furniture> furnitureArrayList) {
        this.furnitureArrayList = furnitureArrayList;
        notifyDataSetChanged();
    }

    public class ItemBlogViewHolder extends RecyclerView.ViewHolder {
        private final ItemFurnitureListBinding binding;

        public ItemBlogViewHolder(ItemFurnitureListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindItems(Furniture furnitureObj) {
            Picasso.get().load(furnitureObj.getUrl())
                    .placeholder(R.drawable.image_default_bg)
                    .error(R.drawable.image_default_bg)
                    .into(binding.iVImage);

            binding.tvTitle.setText(furnitureObj.getTitle());
            binding.tvDescription.setText(furnitureObj.getDescription());

            binding.iVImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickItem.onClick(furnitureObj);
                }
            });
        }
    }
}
