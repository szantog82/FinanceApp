package com.example.szantog.finance.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.szantog.finance.Adapters.HistoryListViewAdapter;
import com.example.szantog.finance.Database.FinanceDatabaseHandler;
import com.example.szantog.finance.Models.EntryItem;
import com.example.szantog.finance.R;
import com.example.szantog.finance.Tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by szantog on 2018.04.13..
 */

public class SQLQueryActivity extends Activity implements View.OnClickListener {

    private static final Character[] NUM_OPERATORS = {'=', '<', '>'};
    private static final String[] CAT_OPERATORS = {"=", "LIKE"};

    private EditText sumEditText;
    private EditText categoryEditText;
    private EditText subcategoryEditText;
    private TextView timeFromTextView;
    private TextView timeToTextView;
    private Button delTimeButton;
    private Spinner sumSpinner;
    private Spinner catSpinner;
    private Spinner subcatSpinner;

    private long timeFromSet = 0;
    private long timeToSet = 0;

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(this, MainActivity.class);
        backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(backIntent);
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sqlquery_activity_layout);

        sumEditText = findViewById(R.id.custom_sqlquery_sum);
        categoryEditText = findViewById(R.id.custom_sqlquery_category);
        subcategoryEditText = findViewById(R.id.custom_sqlquery_subcategory);
        timeFromTextView = findViewById(R.id.custom_sqlquery_timefrom);
        timeFromTextView.setOnClickListener(this);
        timeToTextView = findViewById(R.id.custom_sqlquery_timeto);
        timeToTextView.setOnClickListener(this);
        delTimeButton = findViewById(R.id.custom_sqlquery_deltime);
        delTimeButton.setOnClickListener(this);

        ArrayAdapter<Character> numAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, NUM_OPERATORS);
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, CAT_OPERATORS);

        sumSpinner = findViewById(R.id.custom_sqlquery_sumspinner);
        sumSpinner.setAdapter(numAdapter);
        catSpinner = findViewById(R.id.custom_sqlquery_categoryspinner);
        catSpinner.setAdapter(catAdapter);
        subcatSpinner = findViewById(R.id.custom_sqlquery_subcategoryspinner);
        subcatSpinner.setAdapter(catAdapter);
    }

    public void submit(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView sumText = new TextView(this);
        sumText.setTextSize(16);
        sumText.setTextColor(Color.BLUE);
        ListView listView = new ListView(this);
        listView.setPadding(5, 5, 5, 5);
        linearLayout.addView(sumText);
        linearLayout.addView(listView);
        FinanceDatabaseHandler db = new FinanceDatabaseHandler(this);
        ArrayList<EntryItem> itemsFound = db.customQuery(sumEditText.getText().toString(), (Character) sumSpinner.getSelectedItem(), timeFromSet,
                timeToSet, categoryEditText.getText().toString(), (String) catSpinner.getSelectedItem(),
                subcategoryEditText.getText().toString(), (String) subcatSpinner.getSelectedItem());
        Collections.sort(itemsFound);
        HistoryListViewAdapter historyListViewAdapter = new HistoryListViewAdapter(this, itemsFound);
        listView.setAdapter(historyListViewAdapter);
        builder.setView(linearLayout);
        builder.setTitle("Találatok (" + String.valueOf(itemsFound.size()) + "db)");
        long sum = 0;
        for (EntryItem i : itemsFound) {
            sum += i.getSum();
        }
        sumText.setText(Html.fromHtml("<b>Összeg: " + Tools.formatNumber(sum) + "</b>"));
        builder.show();
    }


    @Override
    public void onClick(final View view) {
        if (view.getId() == R.id.custom_sqlquery_deltime) {
            timeFromTextView.setText("");
            timeToTextView.setText("");
            timeFromSet = 0;
            timeToSet = 0;
        } else if (view instanceof TextView) {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePickerView, int year, int month, int dayOfMonth) {
                    if (view.getId() == R.id.custom_sqlquery_timefrom) {
                        timeFromTextView.setText(String.valueOf(year) + ". " + String.valueOf(month + 1) + ". " + String.valueOf(dayOfMonth));
                        calendar.set(year, month, dayOfMonth);
                        timeFromSet = calendar.getTimeInMillis();
                    } else if (view.getId() == R.id.custom_sqlquery_timeto) {
                        timeToTextView.setText(String.valueOf(year) + ". " + String.valueOf(month + 1) + ". " + String.valueOf(dayOfMonth));
                        calendar.set(year, month, dayOfMonth);
                        timeToSet = calendar.getTimeInMillis();
                    }
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        }
    }
}
