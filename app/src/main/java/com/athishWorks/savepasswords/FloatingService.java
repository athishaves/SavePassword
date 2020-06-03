package com.athishWorks.savepasswords;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.athishWorks.savepasswords.adapters.PasswordAdapter;
import com.athishWorks.savepasswords.adapters.ReducedPasswordAdapter;
import com.athishWorks.savepasswords.pojoModels.PasswordsData;

import java.util.ArrayList;

public class FloatingService extends Service {

    private WindowManager mWindowManager;
    private View mFloatingView, collapsedView, expandedView;

    ReducedPasswordAdapter adapter;
    ArrayList<PasswordsData> passwordsDataArrayList;
    DatabaseHelper databaseHelper;
    RecyclerView recyclerView;

    public FloatingService() {
        // Required
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        mFloatingView = LayoutInflater.from(this).inflate(R.layout.floating_widget, (ViewGroup) null);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        DisplayMetrics displayMetrics = new  DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(displayMetrics);
        final int width = displayMetrics.widthPixels;
        Log.i("Service", "Screen width " + width);

        databaseHelper = new DatabaseHelper(this);
        passwordsDataArrayList = new ArrayList<>();
        adapter = new ReducedPasswordAdapter(passwordsDataArrayList);

        recyclerView = mFloatingView.findViewById(R.id.reduced_passwords_list);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        populateDB();

        collapsedView = mFloatingView.findViewById(R.id.collapsed_layout);
        expandedView = mFloatingView.findViewById(R.id.expanded_layout);

        mFloatingView.findViewById(R.id.parent_floating_widget).setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (Math.abs(params.x - initialX)<1 && Math.abs(params.y - initialY)<1) {
                            collapsedView.setVisibility(View.GONE);
                            expandedView.setVisibility(View.VISIBLE);
                        }
                        if (params.x<0) {
                            params.x = -width/2;
                            mFloatingView.findViewById(R.id.button_close_expanded_left).setVisibility(View.VISIBLE);
                            mFloatingView.findViewById(R.id.button_close_expanded_right).setVisibility(View.GONE);
                        } else {
                            params.x = width/2;
                            mFloatingView.findViewById(R.id.button_close_expanded_left).setVisibility(View.GONE);
                            mFloatingView.findViewById(R.id.button_close_expanded_right).setVisibility(View.VISIBLE);
                        }
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        if (params.x<0) {
                            mFloatingView.findViewById(R.id.button_close_expanded_left).setVisibility(View.VISIBLE);
                            mFloatingView.findViewById(R.id.button_close_expanded_right).setVisibility(View.GONE);
                        } else {
                            mFloatingView.findViewById(R.id.button_close_expanded_left).setVisibility(View.GONE);
                            mFloatingView.findViewById(R.id.button_close_expanded_right).setVisibility(View.VISIBLE);
                        }
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });

        mFloatingView.findViewById(R.id.button_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });

        mFloatingView.findViewById(R.id.button_close_expanded_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseFloatingWindow();
            }
        });

        mFloatingView.findViewById(R.id.button_close_expanded_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseFloatingWindow();
            }
        });
    }

    private void collapseFloatingWindow() {
        collapsedView.setVisibility(View.VISIBLE);
        expandedView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView!=null) mWindowManager.removeView(mFloatingView);
    }

    private void populateDB() {
        Cursor data = databaseHelper.getData();
        passwordsDataArrayList.clear();
        while (data.moveToNext()) {
            Log.i("Pass", "Id = " + data.getString(0));
            PasswordsData pass = new PasswordsData();
            pass.setmID(data.getString(0));
            pass.setmWebsite(data.getString(1));
            pass.setmEmail(data.getString(2));
            pass.setmPassword(data.getString(3));
            passwordsDataArrayList.add(pass);
        }
        Log.i("Service", "Arraylist size " + passwordsDataArrayList.size());
        adapter.notifyDataSetChanged();
    }

}
