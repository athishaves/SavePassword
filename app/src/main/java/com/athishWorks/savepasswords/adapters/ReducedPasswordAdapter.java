package com.athishWorks.savepasswords.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.athishWorks.savepasswords.R;
import com.athishWorks.savepasswords.pojoModels.PasswordsData;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class ReducedPasswordAdapter extends RecyclerView.Adapter <ReducedPasswordAdapter.ViewHolder> {

    private ArrayList<PasswordsData> listdata;
    private int row_item = -1;

    public ReducedPasswordAdapter(ArrayList<PasswordsData> listdata) {
        this.listdata = listdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.reduced_passwords_list, parent, false);
        return (new ViewHolder(listItem));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final PasswordsData myListData = listdata.get(position);

        holder.reducedEmailTextView.setText(myListData.getmEmail());
        holder.reducedWebsiteTextView.setText(myListData.getmWebsite());
        holder.reducedPasswordTextView.setText(myListData.getmPassword());


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_item = position;
                notifyDataSetChanged();
                ClipboardManager clipboardManager = (ClipboardManager) holder.mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Password", holder.reducedPasswordTextView.getText());
                clipboardManager.setPrimaryClip(clip);
            }
        });

        if (row_item==position) {
            holder.reducedEmailTextView.setTextColor(Color.RED);
            holder.reducedWebsiteTextView.setTextColor(Color.RED);
        } else {
            holder.reducedEmailTextView.setTextColor(Color.BLACK);
            holder.reducedWebsiteTextView.setTextColor(Color.BLACK);
        }

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView reducedWebsiteTextView, reducedEmailTextView, reducedPasswordTextView;
        Context mContext;
        RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            mContext = itemView.getContext();

            this.reducedPasswordTextView = itemView.findViewById(R.id.reducedPasswordTextView);
            this.reducedEmailTextView = itemView.findViewById(R.id.reducedEmailTextView);
            this.reducedWebsiteTextView = itemView.findViewById(R.id.reducedWebsiteTextView);
            this.relativeLayout = itemView.findViewById(R.id.password_relative_layout);
        }

        private void copyToClipBoard() {
            ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Password", reducedPasswordTextView.getText());
            clipboardManager.setPrimaryClip(clip);
        }

    }

}