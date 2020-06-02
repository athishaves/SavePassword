package com.athishWorks.savepasswords.adapters;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.athishWorks.savepasswords.PasswordGenerator;
import com.athishWorks.savepasswords.R;
import com.athishWorks.savepasswords.pojoModels.PasswordsData;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class PasswordAdapter extends RecyclerView.Adapter <PasswordAdapter.ViewHolder> {

    private ArrayList<PasswordsData> listdata;

    private OnItenClickListener listener;
    public interface OnItenClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }
    public void setListener(OnItenClickListener listener) {
        this.listener = listener;
    }

    public PasswordAdapter(ArrayList<PasswordsData> listdata) {
        this.listdata = listdata;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.passwords_list, parent, false);
        return (new ViewHolder(listItem, listener));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final PasswordsData myListData = listdata.get(position);

        holder.emailTextView.setText(myListData.getmEmail());
        holder.websiteTextView.setText(myListData.getmWebsite());
        holder.passwordTextView.setText(myListData.getmPassword());
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView websiteTextView, passwordTextView, emailTextView;
        Context mContext;
        ImageView editOption, deleteOption;

        public ViewHolder(View itemView, final OnItenClickListener listener) {
            super(itemView);

            mContext = itemView.getContext();

            this.editOption = itemView.findViewById(R.id.edit_password_image);
            editOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null) {
                        int position = getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION) {
                            listener.onEditClick(position);
                        }
                    }
                }
            });
            this.deleteOption = itemView.findViewById(R.id.delete_password_image);
            deleteOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null) {
                        int position = getAdapterPosition();
                        if (position!=RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });

            this.websiteTextView = itemView.findViewById(R.id.websiteTextView);
            this.passwordTextView = itemView.findViewById(R.id.passwordTextView);
            passwordTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    copyToClipBoard();
                }
            });
            this.emailTextView = itemView.findViewById(R.id.emailTextView);
        }

        private void copyToClipBoard() {
            ClipboardManager clipboardManager = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Password", passwordTextView.getText());
            clipboardManager.setPrimaryClip(clip);
            callASnackBar("Password copied to clipboard");
        }

        private void callASnackBar(String a) {
            Snackbar.make(itemView, a, Snackbar.LENGTH_SHORT).show();
        }
    }

}