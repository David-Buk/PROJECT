package com.example.project;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LinkAdapter extends RecyclerView.Adapter<LinkAdapter.LinkViewHolder> {
    private List<LinkItem> linkList;

    public LinkAdapter(List<LinkItem> linkList) {
        this.linkList = linkList;
    }

    @NonNull
    @Override
    public LinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_link, parent, false);
        return new LinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LinkViewHolder holder, int position) {
        LinkItem currentItem = linkList.get(position);
        holder.linkTextView.setText(currentItem.getLink());
    }

    @Override
    public int getItemCount() {
        return linkList.size();
    }

    public static class LinkViewHolder extends RecyclerView.ViewHolder {
        public TextView linkTextView;

        public LinkViewHolder(@NonNull View itemView) {
            super(itemView);
            linkTextView = itemView.findViewById(R.id.linkTextView);
        }
    }
}

