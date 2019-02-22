package com.example.user1.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

import static com.example.user1.myapplication.MyDBHandler.TABLE_NAME;

public class MainActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    // Product ID counter.
    int id_counter;
    // budgets and daily spent
    Double dailyBudget,monthlyBudget,dailySpent;
    // Button to save the made purchase into database.
    Button saveBtn;
    // Dropdown menu containing different categories of purchases.
    Spinner dropdown;
    // User input field of the purchase cost.
    EditText priceInput;
    // Shows the amount of spent on current day.
    TextView dailyBudgetTxt;
    //Defines current currency.
    String currency = "€";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing views
        Toolbar toolbar = findViewById(R.id.toolbar);
        saveBtn = findViewById(R.id.save_btn);
        priceInput = findViewById(R.id.price_input);
        dropdown = findViewById(R.id.category_list);
        dailyBudgetTxt = findViewById(R.id.dailybudget_text);
        setSupportActionBar(toolbar);


        // Getting budgets and product id counter from sharedPreferences
        sharedPreferences = getApplicationContext().getSharedPreferences
                ("MyPreferences", Context.MODE_PRIVATE);
        try{
            dailyBudget = Double.longBitsToDouble(sharedPreferences.getLong("dailyBudget",0));
            monthlyBudget = Double.longBitsToDouble(sharedPreferences.getLong("monthlyBudget",1));
            id_counter = sharedPreferences.getInt("counterVal", 0);
        } catch (Exception e){
            System.out.println("Error in fetching from saved preferences.");
        }

        // Initializing the dropdown list.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);

        calculateBudget();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    // Create a new Product object with the info passed from text input field and dropdown.
                    Product pr = new Product(Double.parseDouble(priceInput.getText().toString()),
                            dropdown.getSelectedItem().toString(), id_counter);
                    id_counter++;
                    addProduct(pr);
                    // Update shared prefs with the current id_counter value.
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("counterVal", id_counter);
                    editor.commit();
                    priceInput.setText("");
                    calculateBudget();
                    Toasty.success(getApplicationContext(),"Purchase succesfully added!",Toast.LENGTH_SHORT,true).show();
                } catch (NumberFormatException e) {
                    Toasty.error(getApplicationContext(),"Error in price input.",Toast.LENGTH_SHORT,true).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        calculateBudget();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Function for listening clicks on menu items.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_purchases) {
            Intent intent = new Intent(this, QueryActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    // Method for adding products into database.
    public void addProduct(Product pr) {
        MyDBHandler handler = new MyDBHandler(getApplicationContext(), null, null, 1);
        handler.addHandler(pr);
    }

    // Calculates budget and displays it.
    private void calculateBudget(){
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String query = "SELECT price FROM " + TABLE_NAME + " WHERE purchase_date = '" + currentDate+ "'";
        MyDBHandler handler = new MyDBHandler(getApplicationContext(),null,null,1);
        dailySpent = handler.loadBugdetHandler(query);
        dailyBudgetTxt.setText(dailySpent + currency + " / "+ String.valueOf(dailyBudget)+currency);
        if(dailySpent <= dailyBudget){
             dailyBudgetTxt.setTextColor(getResources().getColor(R.color.colorPositive));
        } else{
            dailyBudgetTxt.setTextColor(getResources().getColor(R.color.colorNegative));
        }
    }
}

