package com.athishWorks.savepasswords.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.athishWorks.savepasswords.DatabaseHelper;
import com.athishWorks.savepasswords.FloatingService;
import com.athishWorks.savepasswords.PasswordGenerator;
import com.athishWorks.savepasswords.R;
import com.athishWorks.savepasswords.adapters.PasswordAdapter;
import com.athishWorks.savepasswords.pojoModels.PasswordsData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final static public String SHARED_PREFERENCE = "Data";
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 100;

    RecyclerView recyclerView;

    ArrayList <PasswordsData> passwordsDataArrayList;
    PasswordAdapter adapter;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        passwordsDataArrayList = new ArrayList<>();

        adapter = new PasswordAdapter(passwordsDataArrayList);

        recyclerView = findViewById(R.id.passwordsRecyclerView);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        populateDB();

        adapter.setListener(new PasswordAdapter.OnItenClickListener() {
            @Override
            public void onEditClick(int position) {
                PasswordsData p = passwordsDataArrayList.get(position);
                Log.i("Pass", "Tapped Position " + position);
                addData(p, 1, false, true);
            }

            @Override
            public void onDeleteClick(int position) {
                callForDelete(position);
            }
        });

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData(new PasswordsData(), 1, true, false);
            }
        });

    }

    private void callForDelete(final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Sure you want to delete Password??");
        alert.setMessage("Once deleted, your password cannot be revived");
        alert.setCancelable(true);
        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            }
        });
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (databaseHelper.deleteRow(passwordsDataArrayList.get(position).getmID())) {
                    callASnackBar("Password deleted successfully");
                    populateDB();
                }
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.password_settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_icon:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        adapter.notifyDataSetChanged();
    }

    private void addData(PasswordsData localPasswordsData, int priority, boolean toggleState, final boolean isAnUpdate)  {
        View alertLayout = getLayoutInflater().inflate(R.layout.custom_alert_dialog, (ViewGroup) null);
        final TextInputEditText etWebsite = alertLayout.findViewById(R.id.websiteET);
        etWebsite.setText(localPasswordsData.getmWebsite());
        final TextInputEditText etEmail = alertLayout.findViewById(R.id.emailET);
        etEmail.setText(localPasswordsData.getmEmail());
        final TextInputEditText etPassword = alertLayout.findViewById(R.id.passwordET);
        etPassword.setText(localPasswordsData.getmPassword());
        final String localID = localPasswordsData.getmID();
        switch (priority) {
            case 0:
                etWebsite.requestFocus();
                break;
            case 1:
                etEmail.requestFocus();
                break;
            case 2:
                etPassword.requestFocus();
                break;
        }
        final Switch togglePassword = alertLayout.findViewById(R.id.toggle_button);
        togglePassword.setChecked(toggleState);
        final TextInputLayout textInputLayout = alertLayout.findViewById(R.id.textInputLayout3);
        if (toggleState) {
            textInputLayout.setVisibility(View.GONE);
        } else {
            textInputLayout.setVisibility(View.VISIBLE);
        }
        togglePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textInputLayout.setVisibility(View.GONE);
                } else {
                    textInputLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        if (!isAnUpdate) {
            alert.setTitle("Add Password");
        } else {
            alert.setTitle("Update Password");
        }
        alert.setView(alertLayout);
        alert.setCancelable(false);

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Negative Response
            }
        });

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String web = etWebsite.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();
                String mail = etEmail.getText().toString().trim();

                if (web.equals("") || mail.equals("")) {
                    callASnackBar("Fields cannot be empty");
                    if (mail.equals("")) {
                        addData(new PasswordsData(mail, web, pass), 1, togglePassword.isChecked(), isAnUpdate);
                    } else {
                        addData(new PasswordsData(mail, web, pass), 0, togglePassword.isChecked(), isAnUpdate);
                    }
                    return;
                }

                if (!togglePassword.isChecked()) {
                    pass = etPassword.getText().toString().trim();
                    if (pass.equals("")) {
                        callASnackBar("Fields cannot be empty");
                        addData(new PasswordsData(mail, web, pass), 2, togglePassword.isChecked(), isAnUpdate);
                        return;
                    }
                } else {
                    pass = generatePassword();
                }
                PasswordsData passwordsData = new PasswordsData(localID, mail, web, pass);
                if (!isAnUpdate) {
                    boolean insertToDB = databaseHelper.addData(passwordsData);
                    if (!insertToDB) {
                        callASnackBar("Couldnt add to db");
                    }
                } else {
                    if (!databaseHelper.updateData(passwordsData)) {
                        callASnackBar("Db cannot be updated");
                    } else {
                        callASnackBar("Db updated successfully");
                    }
                }
                populateDB();
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    private String generatePassword() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCE, Context.MODE_PRIVATE);
        int passLength = sharedPreferences.getInt(PasswordGenerator.PASSWORD_LENGTH, 10);
        int specialLength = sharedPreferences.getInt(PasswordGenerator.SPECIAL_LENGTH, 2);
        int numberLength = sharedPreferences.getInt(PasswordGenerator.NUMBER_LENGTH, 2);

        PasswordGenerator passwordGenerator = new PasswordGenerator(passLength);
        passwordGenerator.setSpecialCharacters(specialLength);
        passwordGenerator.setNumericCharacters(numberLength);
        return passwordGenerator.createPassword();
    }

    private void callASnackBar(String a) {
        Snackbar.make(findViewById(R.id.main_view), a, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                askPermissions();
            } else {
                startService(new Intent(MainActivity.this, FloatingService.class));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopService(new Intent(MainActivity.this, FloatingService.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askPermissions() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }

}
