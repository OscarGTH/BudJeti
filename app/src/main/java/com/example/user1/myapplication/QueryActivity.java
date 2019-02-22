package com.example.user1.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.example.user1.myapplication.MyDBHandler.TABLE_NAME;

public class QueryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    ListView listView;
    Button dateBtn, resetBtn;
    Spinner dropdown;
    boolean datePicked = false;
    String currencyType = "€";
    TextView totalSpent;
    String date = "";
    Toolbar toolbar;
    double totalBalance = 0.0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.query_activity);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Initializing views
        listView = findViewById(R.id.query_list);
        dropdown = findViewById(R.id.category_searchlist);
        dateBtn = findViewById(R.id.date_button);
        resetBtn = findViewById(R.id.reset_button);
        totalSpent = findViewById(R.id.money_spent_txt);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.search_array, android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        loadFromDB("SELECT * FROM " + TABLE_NAME);

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateBtn.setText("All time");
                datePicked = false;
                dropdown.setSelection(0);
                searchWithSelection();
            }
        });
        // Purchase category dropdown list listener.
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                searchWithSelection();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_purchtable, menu);
        return true;
    }

    // Sends a query to database with the selected options.
    private void searchWithSelection() {
        String query = "SELECT * FROM " + TABLE_NAME;
        String selectedItem = dropdown.getSelectedItem().toString();
        if (!(selectedItem.equals("ALL"))) {
            query += " WHERE category = ";
            switch (selectedItem) {
                case "FOOD":
                    query += "'FOOD'";
                    break;
                case "BILL":
                    query += "'BILL'";
                    break;
                case "TRANSPORTATION":
                    query += "'TRANSPORTATION'";
                    break;
                case "SNACK":
                    query += "'SNACK'";
                    break;
                case "DRINK":
                    query += "'DRINK'";
                    break;
                case "OTHER":
                    query += "'OTHER'";
                    break;
                case "ENTERTAINMENT":
                    query += "'ENTERTAINMENT'";
                    break;
            }
        }
        if(datePicked && selectedItem.equals("ALL")){
            query += " WHERE purchase_date = " + "'" + date + "'";
        } else if(datePicked){
            query += " AND purchase_date = " + "'" + date + "'";
        }
        loadFromDB(query);
        Log.i("Query:",query);
    }

    // Loads the items from database and displays it in list format
    public void loadFromDB(String query) {
        MyDBHandler handler = new MyDBHandler(getApplicationContext(), null, null, 1);
        List<String> loadedData = handler.loadHandler(query);
        // Get total balance from loadedData arraylist and save it.
        totalBalance = Double.parseDouble(loadedData.get(loadedData.size()-1));

        // Remove the balance from the arraylist.
        loadedData.remove(loadedData.size() - 1);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                loadedData){
            @Override
            public View getView(int position,View convertView,  ViewGroup parent) {
                View view =  super.getView(position, convertView, parent);
                TextView text = view.findViewById(android.R.id.text1);
                text.setTextColor(getResources().getColor(R.color.white));
                return view;
            }
        };
        listView.setAdapter(adapter);
        totalSpent.setText(totalBalance + currencyType);
    }

    // Item selection listener for menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_id) {
         showDeleteItemDialog(this);
        }
        if(id == R.id.action_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == android.R.id.home){
            finish();
        }
        if(id == R.id.action_delete_all){
            showDeleteAllDialog(this);
        }

        return super.onOptionsItemSelected(item);
    }

    // Displays dialog to remove item by ID.
    private void showDeleteItemDialog(Context c) {
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Delete purchase by ID")
                .setMessage("Input item ID")
                .setView(taskEditText)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        deleteFromDB(Integer.parseInt(task));
                        searchWithSelection();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }

    // Displays dialog to remove all items.
    private void showDeleteAllDialog(Context c) {
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Confirmation of deletion")
                .setMessage("Are you sure you want to delete all purchases?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFromDB();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }


    // Deletes item by its ID.
    public void deleteFromDB(int id) {
        MyDBHandler handler = new MyDBHandler(getApplicationContext(), null, null, 1);
        if (handler.dropSelectedIDHandler(id)) {
            Toasty.success(getApplicationContext(),"Purchase successfully deleted!",Toast.LENGTH_SHORT,true).show();
        } else{
            Toasty.error(getApplicationContext(),"Incorrect ID.",Toast.LENGTH_SHORT,true).show();
        }
    }
    // Deletes all data from Db
    public void deleteFromDB() {
        MyDBHandler handler = new MyDBHandler(getApplicationContext(), null, null, 1);
        if (handler.deleteAllHandler()) {
            Toasty.success(getApplicationContext(), "Purchases successfully deleted!", Toast.LENGTH_SHORT, true).show();
            searchWithSelection();
        }else {
                Toasty.error(getApplicationContext(), "Error in deleting data.", Toast.LENGTH_SHORT, true).show();
        }
    }




    // Listens for date change and querys the database again with a new date.
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        datePicked = true;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DATE, day);
        date = day + "-" + (month+1) + "-" + year;
        dateBtn.setText(DateFormat.getDateInstance().format(c.getTime()));
        searchWithSelection();
    }
}
