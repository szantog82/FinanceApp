package com.example.szantog.finance.Activities;

import android.accounts.AccountManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szantog.finance.Database.FinanceDatabaseHandler;
import com.example.szantog.finance.Database.RepetitiveDatabaseHandler;
import com.example.szantog.finance.Models.EntryItem;
import com.example.szantog.finance.R;

import java.util.ArrayList;

/**
 * Created by szantog on 2018.03.18..
 */

public class InitActivity extends AppCompatActivity {

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;
    private TextView pwdTextView;
    private String enteredPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //commevnt


        setContentView(R.layout.init_layout);
        sharedPrefs = getSharedPreferences(getString(R.string.SHAREDPREF_MAINKEY), 0);
        editor = sharedPrefs.edit();

        //checking if there is new repetitive balance which needs to be added

        RepetitiveDatabaseHandler repDb = new RepetitiveDatabaseHandler(this);

        //correction
        /*ArrayList<RepetitiveItem> reps = repDb.getAllData();
        FinanceDatabaseHandler dba = new FinanceDatabaseHandler(this);
        for (RepetitiveItem rep : reps) {
            long sum = rep.getSum();
            String category = rep.getCategory();
            String subCategory = rep.getSubCategory();
            String newCollection = dba.getPreviousInstances(sum, category, subCategory);
            repDb.updateCollection(category, subCategory, sum, newCollection);
        }*/


        // repDb.insertData(new RepetitiveItem(1497225600000l, -3459,1497225600000l,0,1,"","Egyetem","miskolc"));
        // repDb.insertData(new RepetitiveItem(1514937600000l, -13459,1514937600000l,0,1,"","Csekkek","közüzem"));
        // repDb.insertData(new RepetitiveItem(1470960000000l, 435435,1470960000000l,0,1,"","Fizetés","készpénz"));

        ArrayList<EntryItem> newItems = new ArrayList<>();
        try {
            newItems = repDb.checkData();
        } catch (SQLiteException e) {
            repDb.createTable();
            newItems = repDb.checkData();
        } finally {
            if (newItems.size() > 0) {
                FinanceDatabaseHandler db = new FinanceDatabaseHandler(this);
                for (EntryItem item : newItems) {
                    db.insertEntryAfterCheck(item);
                }
            }
        }

        //starting the app


       /* Intent intent = AccountManager.newChooseAccountIntent(null, null,
                new String[]{"com.google", "com.google.android.legacyimap"},
                false, null, null, null, null);
        startActivityForResult(intent, 123);*/

        if (sharedPrefs.getString(getString(R.string.password_key), null) == null) {
            editor.putBoolean(getString(R.string.fake_data), false);
            editor.apply();
            Intent startIntent = new Intent(InitActivity.this, MainActivity.class);
            startActivity(startIntent);
            finish();
        }

        pwdTextView = findViewById(R.id.init_textview_pwd);
        enteredPassword = "";
    }

    private void checkPwd() {
        if (enteredPassword.equals(sharedPrefs.getString(getString(R.string.password_key), null))) {
            editor.putBoolean(getString(R.string.fake_data), false);
            editor.apply();
            Intent startIntent = new Intent(InitActivity.this, MainActivity.class);
            startActivity(startIntent);
            finish();
        } else if (enteredPassword.equals("1234")) {
            Toast.makeText(this, "Entering fake mode", Toast.LENGTH_LONG).show();
            editor.putBoolean(getString(R.string.fake_data), true);
            editor.apply();
            Intent startIntent = new Intent(InitActivity.this, MainActivity.class);
            startActivity(startIntent);
            finish();
        } else {
            pwdTextView.setText("");
            enteredPassword = "";
            Toast.makeText(InitActivity.this, "Rossz jelszó", Toast.LENGTH_SHORT).show();
        }
    }

    public void numPressed(View view) {
        Button btn = (Button) view;
        enteredPassword += btn.getText().toString();
        pwdTextView.setText("");
        for (int i = 0; i < enteredPassword.length(); i++) {
            pwdTextView.append("*");
        }
        if (enteredPassword.length() == 4) {
            checkPwd();
        }
    }

    public void clearPressed(View view) {
        if (pwdTextView.getText().toString().length() > 0) {
            String prevText = pwdTextView.getText().toString();
            pwdTextView.setText(prevText.substring(0, prevText.length() - 1));
            enteredPassword = enteredPassword.substring(0, enteredPassword.length() - 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (resultCode == RESULT_OK) {
                Log.e("name", data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME));
                Log.e("type", data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
                Log.e("password", "aa" + data.getStringExtra(AccountManager.KEY_ACCOUNTS));
                Log.e("password", "aa" + data.getStringExtra(AccountManager.KEY_ACCOUNT_STATUS_TOKEN));
            }
        }
    }
}