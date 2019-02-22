package com.example.user1.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class SettingsActivity extends AppCompatActivity {
    Button saveBtn;
    RadioGroup radioGroup;
    RadioButton euro;
    String chosenCurrency = "€";
    TextInputLayout daily_input;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_main);
        saveBtn = findViewById(R.id.save_btn);
        daily_input = findViewById(R.id.daily_budget_input);
        radioGroup = findViewById(R.id.radio_group);
        euro = findViewById(R.id.euro_radio_btn);
        // Different currencies could be added here.
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.euro_radio_btn){
                    chosenCurrency = "€";
                }
            }
        });
        // Saves the new budget into the sharedPreferences.
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Double daily = Double.parseDouble(daily_input.getEditText().getText().toString());
                    //Long daily = Long.parseLong(daily_input.getEditText().getText().toString());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences
                            ("MyPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    //editor.putLong("dailyBudget", daily);
                    editor.putLong("dailyBudget",Double.doubleToRawLongBits(daily));
                    editor.apply();
                    startActivity(intent);
                } catch (NumberFormatException ex){
                    Toasty.error(getApplicationContext(),"Input cannot be empty.",Toast.LENGTH_SHORT,true).show();
                }
            }
        });

    }


}
