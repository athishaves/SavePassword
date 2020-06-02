package com.athishWorks.savepasswords.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.athishWorks.savepasswords.PasswordGenerator;
import com.athishWorks.savepasswords.R;
import com.google.android.material.snackbar.Snackbar;

public class SettingsActivity extends AppCompatActivity {

    EditText passLength, specialLength, numberLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        passLength = findViewById(R.id.passLength);
        specialLength = findViewById(R.id.specialLength);
        numberLength = findViewById(R.id.numberLength);

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREFERENCE, Context.MODE_PRIVATE);

        int defaultPassLength = sharedPreferences.getInt(PasswordGenerator.PASSWORD_LENGTH, 10);
        int defaultSpecialLength = sharedPreferences.getInt(PasswordGenerator.SPECIAL_LENGTH, 2);
        int defaultNumberLength = sharedPreferences.getInt(PasswordGenerator.NUMBER_LENGTH, 2);

        passLength.setText(String.valueOf(defaultPassLength));
        specialLength.setText(String.valueOf(defaultSpecialLength));
        numberLength.setText(String.valueOf(defaultNumberLength));

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passName = passLength.getText().toString().trim();
                String specialName = specialLength.getText().toString().trim();
                String numberName = numberLength.getText().toString().trim();

                if (passName.equals("") || specialName.equals("") || numberName.equals("")) {
                    callASnackBar("Fields cannot be empty");
                    return;
                }
                if (passName.length()>2 || specialName.length()>2 || numberName.length()>2) {
                    callASnackBar("Keep the password length short");
                    return;
                }

                int passNameInt = Integer.parseInt(passName);
                if (passNameInt==0) {
                    callASnackBar("Length of the password cannot be 0");
                    return;
                }
                int numberNameInt = Integer.parseInt(numberName);
                int specialNameInt = Integer.parseInt(specialName);

                if (numberNameInt + specialNameInt > passNameInt) {
                    callASnackBar("Sum of number and special characters cannot exceed Password length");
                    return;
                }

                SharedPreferences.Editor editor = getSharedPreferences(MainActivity.SHARED_PREFERENCE, Context.MODE_PRIVATE).edit();
                editor.putInt(PasswordGenerator.PASSWORD_LENGTH, passNameInt);
                editor.putInt(PasswordGenerator.NUMBER_LENGTH, numberNameInt);
                editor.putInt(PasswordGenerator.SPECIAL_LENGTH, specialNameInt);
                if (editor.commit()) {
                    callASnackBar("Settings saved successfully");
                }
            }
        });
    }

    private void callASnackBar(String a) {
        Snackbar.make(findViewById(R.id.settings_view), a, Snackbar.LENGTH_SHORT).show();
    }

}
