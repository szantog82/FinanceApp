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
    private TextView pwdTextView;
    private String enteredPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //comment


        setContentView(R.layout.init_layout);
        sharedPrefs = getSharedPreferences(getString(R.string.SHAREDPREF_MAINKEY), 0);

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
                    db.insertEntry(item);
                }
            }
        }

        //starting the app


       /* String proba = "{\n" +
                "    \"_id\": {\n" +
                "        \"$oid\": \"5ac32dfa268b8e0014d06494\"\n" +
                "    },\n" +
                "    \"username\": \"Gábor\",\n" +
                "    \"initialbalance\": \"410000\",\n" +
                "    \"balance\": \"[{\\\"category\\\":\\\"Szórakozás\\\",\\\"subCategory\\\":\\\"uszoda\\\",\\\"time\\\":1521225305319,\\\"sum\\\":-5000,\\\"day\\\":16,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Eating-out\\\",\\\"subCategory\\\":\\\"street food\\\",\\\"time\\\":1521225318119,\\\"sum\\\":-10000,\\\"day\\\":16,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Bevásárlás\\\",\\\"subCategory\\\":\\\"piac\\\",\\\"time\\\":1521274836075,\\\"sum\\\":-4200,\\\"day\\\":17,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Autó\\\",\\\"subCategory\\\":\\\"benzin\\\",\\\"time\\\":1521275474808,\\\"sum\\\":-8000,\\\"day\\\":17,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Bevásárlás\\\",\\\"subCategory\\\":\\\"aldi\\\",\\\"time\\\":1521278016288,\\\"sum\\\":-13705,\\\"day\\\":17,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Egyéb\\\",\\\"subCategory\\\":\\\"ajándék\\\",\\\"time\\\":1521530791604,\\\"sum\\\":-650,\\\"day\\\":20,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Bevásárlás\\\",\\\"subCategory\\\":\\\"olívaolaj\\\",\\\"time\\\":1521547306922,\\\"sum\\\":-10300,\\\"day\\\":20,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Étel\\\",\\\"subCategory\\\":\\\"\\\",\\\"time\\\":1521558284765,\\\"sum\\\":-310,\\\"day\\\":20,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Telefon\\\",\\\"subCategory\\\":\\\"\\\",\\\"time\\\":1521633544387,\\\"sum\\\":-7000,\\\"day\\\":21,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Gyerek\\\",\\\"subCategory\\\":\\\"bicikli\\\",\\\"time\\\":1521656427972,\\\"sum\\\":-12850,\\\"day\\\":21,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"e-bay\\\",\\\"subCategory\\\":\\\"\\\",\\\"time\\\":1521794334843,\\\"sum\\\":-5000,\\\"day\\\":23,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Háztartás\\\",\\\"subCategory\\\":\\\"főgáz\\\",\\\"time\\\":1521813647897,\\\"sum\\\":-1033,\\\"day\\\":23,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Háztartás\\\",\\\"subCategory\\\":\\\"műszaki\\\",\\\"time\\\":1521820320308,\\\"sum\\\":-10940,\\\"day\\\":23,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Eating-out\\\",\\\"subCategory\\\":\\\"street food\\\",\\\"time\\\":1521891786486,\\\"sum\\\":-1800,\\\"day\\\":24,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Bevásárlás\\\",\\\"subCategory\\\":\\\"aldi\\\",\\\"time\\\":1521906665900,\\\"sum\\\":-17620,\\\"day\\\":24,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Önismeret\\\",\\\"subCategory\\\":\\\"\\\",\\\"time\\\":1521977787816,\\\"sum\\\":-30000,\\\"day\\\":25,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Eating-out\\\",\\\"subCategory\\\":\\\"street food\\\",\\\"time\\\":1521982039572,\\\"sum\\\":-5900,\\\"day\\\":25,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Étel\\\",\\\"subCategory\\\":\\\"\\\",\\\"time\\\":1522040949391,\\\"sum\\\":-420,\\\"day\\\":26,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Autó\\\",\\\"subCategory\\\":\\\"benzin\\\",\\\"time\\\":1522082705255,\\\"sum\\\":-10100,\\\"day\\\":26,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Önismeret\\\",\\\"subCategory\\\":\\\"\\\",\\\"time\\\":1522159676019,\\\"sum\\\":-20000,\\\"day\\\":27,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Egészség\\\",\\\"subCategory\\\":\\\"fodrász\\\",\\\"time\\\":1522328679679,\\\"sum\\\":-2700,\\\"day\\\":29,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Gyerek\\\",\\\"subCategory\\\":\\\"játék\\\",\\\"time\\\":1522329566478,\\\"sum\\\":-2990,\\\"day\\\":29,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Gyerek\\\",\\\"subCategory\\\":\\\"játék\\\",\\\"time\\\":1522352037597,\\\"sum\\\":-400,\\\"day\\\":29,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Gyerek\\\",\\\"subCategory\\\":\\\"babysitter\\\",\\\"time\\\":1522430940050,\\\"sum\\\":-40000,\\\"day\\\":30,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Háztartás\\\",\\\"subCategory\\\":\\\"kertészkedés\\\",\\\"time\\\":1522482722740,\\\"sum\\\":-7850,\\\"day\\\":31,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Bevásárlás\\\",\\\"subCategory\\\":\\\"biobolt\\\",\\\"time\\\":1522508490442,\\\"sum\\\":-470,\\\"day\\\":31,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Gyerek\\\",\\\"subCategory\\\":\\\"játszóház\\\",\\\"time\\\":1522508511975,\\\"sum\\\":-975,\\\"day\\\":31,\\\"month\\\":2,\\\"year\\\":2018},{\\\"category\\\":\\\"Egyéb\\\",\\\"subCategory\\\":\\\"banki költségek\\\",\\\"time\\\":1522598488374,\\\"sum\\\":-400,\\\"day\\\":1,\\\"month\\\":3,\\\"year\\\":2018},{\\\"category\\\":\\\"Gyerek\\\",\\\"subCategory\\\":\\\"játék\\\",\\\"time\\\":1522739585103,\\\"sum\\\":-2395,\\\"day\\\":3,\\\"month\\\":3,\\\"year\\\":2018}]\",\n" +
                "    \"categorylist\": \"[\\\"Szórakozás\\\",\\\"Eating-out\\\",\\\"Bevásárlás\\\",\\\"Autó\\\",\\\"Egyéb\\\",\\\"Étel\\\",\\\"Telefon\\\",\\\"Gyerek\\\",\\\"e-bay\\\",\\\"Háztartás\\\",\\\"Önismeret\\\",\\\"Egészség\\\"]\",\n" +
                "    \"repetitivedata\": \"[{\\\"category\\\":\\\"Egyéb\\\",\\\"collection\\\":\\\"1522598488374__div__\\\",\\\"subCategory\\\":\\\"banki költségek\\\",\\\"turnoverMonth\\\":1,\\\"endTime\\\":0,\\\"sum\\\":-400,\\\"time\\\":1522598488327,\\\"startTime\\\":1522598464039}]\"\n" +
                "}";
        try {
            JSONObject obj = new JSONObject(proba);
            String arrStr = obj.getString("balance");
            JSONArray arr = new JSONArray(arrStr);
            ArrayList<EntryItem> items = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
               JSONObject obj2 = new JSONObject(arr.getString(i));
               Log.e("cate", obj2.getString("category"));
               // items.add((EntryItem) obj2);
            }
            for (EntryItem item : items) {
                Log.e("cat", item.getCategory());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

       /* Intent intent = AccountManager.newChooseAccountIntent(null, null,
                new String[]{"com.google", "com.google.android.legacyimap"},
                false, null, null, null, null);
        startActivityForResult(intent, 123);*/

        if (sharedPrefs.getString(getString(R.string.password_key), null) == null) {
            Intent startIntent = new Intent(InitActivity.this, MainActivity.class);
            startActivity(startIntent);
            finish();
        }

        pwdTextView = findViewById(R.id.init_textview_pwd);
        enteredPassword = "";
    }

    private void checkPwd() {
        if (enteredPassword.equals(sharedPrefs.getString(getString(R.string.password_key), null))) {
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