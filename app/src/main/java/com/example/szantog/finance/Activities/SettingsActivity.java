package com.example.szantog.finance.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szantog.finance.Adapters.IconGridAdapter;
import com.example.szantog.finance.Adapters.PreferencesListViewAdapter;
import com.example.szantog.finance.Database.FinanceDatabaseHandler;
import com.example.szantog.finance.Database.RepetitiveDatabaseHandler;
import com.example.szantog.finance.ProgressDialog;
import com.example.szantog.finance.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by szantog on 2018.03.18..
 */

public class SettingsActivity extends PreferenceActivity {

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

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends android.support.v14.preference.PreferenceFragment implements Preference.OnPreferenceClickListener {

        private AlertDialog.Builder dialogBuilder;
        private SharedPreferences sharedPrefs;
        private SharedPreferences.Editor sharedEditor;

        private View newCategoryView;
        private ListView newCategoryListView;
        private PreferencesListViewAdapter preferencesListViewAdapter;
        private ArrayList<String> categoriesArr;
        private final Boolean[] isIncomeCategory = {true};
        private RadioGroup radioGroup;
        private JSONObject categoryIconObject;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            getPreferenceManager().setSharedPreferencesName(getString(R.string.SHAREDPREF_MAINKEY));
            getPreferenceManager().setSharedPreferencesMode(0);

            addPreferencesFromResource(R.xml.app_preferences);

            dialogBuilder = new AlertDialog.Builder(getActivity());
            sharedPrefs = getActivity().getSharedPreferences(getString(R.string.SHAREDPREF_MAINKEY), 0);
            sharedEditor = sharedPrefs.edit();
            Preference categories = getPreferenceScreen().findPreference(getString(R.string.categories));
            categories.setOnPreferenceClickListener(this);
            Preference passwordSettings = getPreferenceScreen().findPreference(getString(R.string.password_settings));
            passwordSettings.setOnPreferenceClickListener(this);
            Preference exportData = getPreferenceScreen().findPreference(getString(R.string.export_data));
            exportData.setOnPreferenceClickListener(this);
            Preference exportDataLocal = getPreferenceScreen().findPreference(getString(R.string.export_data_local));
            exportDataLocal.setOnPreferenceClickListener(this);
            Preference importDataLocal = getPreferenceScreen().findPreference(getString(R.string.import_data_local));
            importDataLocal.setOnPreferenceClickListener(this);
        }

        private void setCategoryPreference() {
            categoriesArr = new ArrayList<>();
            newCategoryListView = newCategoryView.findViewById(R.id.preferences_categories_listview);
            if (sharedPrefs.getString(getString(R.string.category_iconassociations_key), null) == null) {
                categoryIconObject = new JSONObject();
                JSONObject incomeObj = new JSONObject();
                JSONObject expOjb = new JSONObject();
                Set<String> incCategorySet = sharedPrefs.getStringSet(getString(R.string.incomecategorylist_key), null);
                if (incCategorySet != null) {
                    for (String category : incCategorySet) {
                        try {
                            incomeObj.put(category, -1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Set<String> expCategorySet = sharedPrefs.getStringSet(getString(R.string.expenditurecategorylist_key), null);
                if (expCategorySet != null) {
                    for (String category : expCategorySet) {
                        try {
                            expOjb.put(category, -1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        categoryIconObject.put(getString(R.string.income), incomeObj);
                        categoryIconObject.put(getString(R.string.expenditure), expOjb);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sharedEditor.putString(getString(R.string.category_iconassociations_key), categoryIconObject.toString());
                    sharedEditor.apply();
                }
            } else {
                try {
                    categoryIconObject = new JSONObject(sharedPrefs.getString(getString(R.string.category_iconassociations_key), null));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            preferencesListViewAdapter = new PreferencesListViewAdapter(getActivity(), categoriesArr, categoryIconObject, isIncomeCategory);
            newCategoryListView.setAdapter(preferencesListViewAdapter);
            preferencesListViewAdapter.setOnDeleteListener(new PreferencesListViewAdapter.PreferencesDeleteItemListener() {
                @Override
                public void onDelClicked(final String selectedCategory) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Megerősítés");
                    builder.setMessage("Biztos, hogy töröljük a kategóriát?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            categoriesArr.remove(selectedCategory);
                            Toast.makeText(getActivity(), "Kategória eltávolítva", Toast.LENGTH_SHORT).show();
                            Set<String> categoriesSet = new HashSet<>(categoriesArr);
                            if (radioGroup.getCheckedRadioButtonId() == R.id.preferences_categories_radiobutton_income) {
                                try {
                                    categoryIconObject.getJSONObject(getString(R.string.income)).remove(selectedCategory);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                sharedEditor.putStringSet(getString(R.string.incomecategorylist_key), categoriesSet);
                                sharedEditor.apply();
                            } else if (radioGroup.getCheckedRadioButtonId() == R.id.preferences_categories_radiobutton_expenditure) {
                                try {
                                    categoryIconObject.getJSONObject(getString(R.string.expenditure)).remove(selectedCategory);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                sharedEditor.putStringSet(getString(R.string.expenditurecategorylist_key), categoriesSet);
                                sharedEditor.apply();
                            }
                            preferencesListViewAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("Mégse", null);
                    builder.show();
                }

                @Override
                public void onIconButtonClicked(final String selectedCategory) {
                    final int[] selectedIconPosition = {-1};
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    LinearLayout iconChooserLayout = new LinearLayout(getActivity());
                    iconChooserLayout.setOrientation(LinearLayout.VERTICAL);

                    TextView titleText = new TextView(getActivity());
                    titleText.setText("Ikonválasztó");
                    titleText.setTextSize(20);
                    titleText.setTextColor(Color.BLACK);
                    params.setMargins(5, 5, 5, 10);
                    titleText.setLayoutParams(params);

                    View lineView = new View(getActivity());
                    lineView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
                    lineView.setBackgroundColor(Color.GREEN);

                    LinearLayout messageLayout = new LinearLayout(getActivity());
                    messageLayout.setOrientation(LinearLayout.HORIZONTAL);
                    TextView messageText = new TextView(getActivity());
                    messageText.setText(String.format("Válassz ikont ehhez: '%s'", selectedCategory));
                    messageText.setTextSize(18);
                    messageText.setTextColor(Color.BLACK);
                    messageText.setLayoutParams(params);
                    final ImageView messageImage = new ImageView(getActivity());
                    messageImage.setVisibility(View.INVISIBLE);
                    messageImage.setLayoutParams(params);
                    messageLayout.addView(messageText);
                    messageLayout.addView(messageImage);

                    GridView iconGrids = new GridView(getActivity());
                    iconGrids.setNumColumns(3);
                    iconGrids.setClipToPadding(false);
                    iconGrids.setHorizontalSpacing(16);
                    iconGrids.setVerticalSpacing(16);
                    IconGridAdapter iconGridAdapter = new IconGridAdapter(getActivity());
                    iconGrids.setAdapter(iconGridAdapter);
                    iconGrids.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            selectedIconPosition[0] = i;
                            messageImage.setImageResource(IconGridAdapter.icons[i]);
                            messageImage.setVisibility(View.VISIBLE);
                        }
                    });

                    iconChooserLayout.addView(titleText);
                    iconChooserLayout.addView(lineView);
                    iconChooserLayout.addView(messageLayout);
                    iconChooserLayout.addView(iconGrids);

                    AlertDialog.Builder iconDialogBuilder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                    iconDialogBuilder.setView(iconChooserLayout);
                    iconDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (radioGroup.getCheckedRadioButtonId() == R.id.preferences_categories_radiobutton_income) {
                                try {
                                    categoryIconObject.getJSONObject(getString(R.string.income)).put(selectedCategory, selectedIconPosition[0]);
                                    sharedEditor.putString(getString(R.string.category_iconassociations_key), categoryIconObject.toString());
                                    sharedEditor.apply();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (radioGroup.getCheckedRadioButtonId() == R.id.preferences_categories_radiobutton_expenditure) {
                                try {
                                    categoryIconObject.getJSONObject(getString(R.string.expenditure)).put(selectedCategory, selectedIconPosition[0]);
                                    sharedEditor.putString(getString(R.string.category_iconassociations_key), categoryIconObject.toString());
                                    sharedEditor.apply();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            preferencesListViewAdapter.notifyDataSetChanged();
                        }
                    });
                    iconDialogBuilder.setNeutralButton("Mégse", null);
                    iconDialogBuilder.setNegativeButton("Ikon törlése", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (radioGroup.getCheckedRadioButtonId() == R.id.preferences_categories_radiobutton_income) {
                                try {
                                    categoryIconObject.getJSONObject(getString(R.string.income)).put(selectedCategory, -1);
                                    sharedEditor.putString(getString(R.string.category_iconassociations_key), categoryIconObject.toString());
                                    sharedEditor.apply();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else if (radioGroup.getCheckedRadioButtonId() == R.id.preferences_categories_radiobutton_expenditure) {
                                try {
                                    categoryIconObject.getJSONObject(getString(R.string.expenditure)).put(selectedCategory, -1);
                                    sharedEditor.putString(getString(R.string.category_iconassociations_key), categoryIconObject.toString());
                                    sharedEditor.apply();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            preferencesListViewAdapter.notifyDataSetChanged();
                        }
                    });
                    iconDialogBuilder.show();
                }
            });
            updateCategoryPreference();
        }

        private void updateCategoryPreference() {
            final Set<String> categoriesSet = new HashSet<>();
            if (radioGroup.getCheckedRadioButtonId() == R.id.preferences_categories_radiobutton_income) {
                isIncomeCategory[0] = true;
                if (sharedPrefs.getStringSet(getString(R.string.incomecategorylist_key), null) != null) {
                    categoriesSet.addAll(sharedPrefs.getStringSet(getString(R.string.incomecategorylist_key), null));
                }
            } else if (radioGroup.getCheckedRadioButtonId() == R.id.preferences_categories_radiobutton_expenditure) {
                isIncomeCategory[0] = false;
                if (sharedPrefs.getStringSet(getString(R.string.expenditurecategorylist_key), null) != null) {
                    categoriesSet.addAll(sharedPrefs.getStringSet(getString(R.string.expenditurecategorylist_key), null));
                }
            }
            categoriesArr.clear();
            categoriesArr.addAll(categoriesSet);
            preferencesListViewAdapter.notifyDataSetChanged();
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            if (preference.getKey().equals(getString(R.string.categories))) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                newCategoryView = inflater.inflate(R.layout.preferences_set_categories_layout, null);
                dialogBuilder.setView(newCategoryView);
                dialogBuilder.show();
                radioGroup = newCategoryView.findViewById(R.id.preferences_categories_radiogroup);
                setCategoryPreference();
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        updateCategoryPreference();
                    }
                });
                ImageButton addButton = newCategoryView.findViewById(R.id.preferences_categories_addnew);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder newCategoryDialog = new AlertDialog.Builder(getActivity());
                        final EditText editText = new EditText(getActivity());
                        newCategoryDialog.setView(editText);
                        newCategoryDialog.setTitle("Új kategória hozzáadása");
                        newCategoryDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                categoriesArr.add(editText.getText().toString());
                                Toast.makeText(getActivity(), "Kategória hozzáadva", Toast.LENGTH_SHORT).show();
                                Set<String> categoriesSet = new HashSet<>(categoriesArr);
                                if (radioGroup.getCheckedRadioButtonId() == R.id.preferences_categories_radiobutton_income) {
                                    if (categoryIconObject != null) {
                                        try {
                                            categoryIconObject.getJSONObject(getString(R.string.income)).put(editText.getText().toString(), -1);
                                            sharedEditor.putString(getString(R.string.category_iconassociations_key), categoryIconObject.toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    sharedEditor.putStringSet(getString(R.string.incomecategorylist_key), categoriesSet);
                                    sharedEditor.apply();
                                } else if (radioGroup.getCheckedRadioButtonId() == R.id.preferences_categories_radiobutton_expenditure) {
                                    if (categoryIconObject != null) {
                                        try {
                                            categoryIconObject.getJSONObject(getString(R.string.expenditure)).put(editText.getText().toString(), -1);
                                            sharedEditor.putString(getString(R.string.category_iconassociations_key), categoryIconObject.toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    sharedEditor.putStringSet(getString(R.string.expenditurecategorylist_key), categoriesSet);
                                    sharedEditor.apply();
                                }
                                editText.setText("");
                                preferencesListViewAdapter.notifyDataSetChanged();
                            }
                        });
                        newCategoryDialog.setNegativeButton("Mégse", null);
                        newCategoryDialog.show();
                    }
                });
            } else if (preference.getKey().equals(getString(R.string.export_data))) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                final FinanceDatabaseHandler db = new FinanceDatabaseHandler(getActivity());

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.preferences_backup_layout, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setTitle("Adatok az exportáláshoz");
                final EditText usernameText = dialogView.findViewById(R.id.preferences_backup_username);
                final EditText pwdText = dialogView.findViewById(R.id.preferences_backup_password);
                dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Gson gson = new GsonBuilder().create();
                        String balanceData = gson.toJson(db.getAllData());
                        RepetitiveDatabaseHandler repetitivedb = new RepetitiveDatabaseHandler(getActivity());
                        String repetitiveData = gson.toJson(repetitivedb.getAllData());
                        JSONArray categoryList = new JSONArray(db.getDistinctCategories());
                        // strings[0]: username
                        // strings[1]: password
                        // strings[2]: initialBalance
                        // strings[3]: balanceData
                        // strings[4]: categoryList
                        // strings[5]: repetitiveData
                        new UploadBackup().execute(
                                usernameText.getText().toString(),
                                pwdText.getText().toString(),
                                sharedPrefs.getString(getString(R.string.initialbalance_key), "0"),
                                balanceData,
                                categoryList.toString(),
                                repetitiveData
                        );
                    }
                });
                dialogBuilder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dialogBuilder.show();
            } else if (preference.getKey().equals(getString(R.string.export_data_local))) {
                Gson gson = new GsonBuilder().create();
                FinanceDatabaseHandler db = new FinanceDatabaseHandler(getActivity());
                RepetitiveDatabaseHandler repetitivedb = new RepetitiveDatabaseHandler(getActivity());
                JSONArray categoryList = new JSONArray(db.getDistinctCategories());
                JsonObject json = new JsonObject();
                json.addProperty("username", "localuser");
                json.addProperty("password", "password");
                json.addProperty("initialbalance", sharedPrefs.getString(getString(R.string.initialbalance_key), "0"));
                json.addProperty("balance", gson.toJson(db.getAllData()));
                json.addProperty("categorylist", categoryList.toString());
                json.addProperty("repetitivedata", gson.toJson(repetitivedb.getAllData()));

            } else if (preference.getKey().equals(getString(R.string.import_data_local))) {

            } else if (preference.getKey().equals(getString(R.string.password_settings))) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                LinearLayout linlay = new LinearLayout(getActivity());
                linlay.setOrientation(LinearLayout.VERTICAL);
                final EditText pwdText = new EditText(getActivity());
                pwdText.setInputType(InputType.TYPE_CLASS_NUMBER);
                InputFilter[] filterArray = new InputFilter[1];
                filterArray[0] = new InputFilter.LengthFilter(4);
                pwdText.setFilters(filterArray);
                Button delPwd = new Button(getActivity());
                delPwd.setText("Törlés");
                linlay.addView(pwdText);
                linlay.addView(delPwd);
                dialogBuilder.setTitle("App jelszó megváltoztatása...");
                dialogBuilder.setView(linlay);
                delPwd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sharedEditor.putString(getString(R.string.password_key), null);
                        sharedEditor.apply();
                        pwdText.setText("");
                        Toast.makeText(getActivity(), "Appvédelem törölve", Toast.LENGTH_SHORT).show();
                    }
                });
                dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (pwdText.getText().toString().length() == 4) {
                            sharedEditor.putString(getString(R.string.password_key), pwdText.getText().toString());
                            sharedEditor.apply();
                        } else {
                            Toast.makeText(getActivity(), "Túl rövid jelszó", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialogBuilder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dialogBuilder.show();

            }
            return true;
        }

        class UploadBackup extends AsyncTask<String, String, String> {

            private ProgressDialog progressDialog;

            private Boolean networkSuccess = false;
            private Boolean badPassword = false;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {
                // strings[0]: username
                // strings[1]: password
                // strings[2]: initialBalance
                // strings[3]: balanceData
                // strings[4]: categoryList
                // strings[5]: repetitiveData
                String SERVERURL = "https://recipes-szg.herokuapp.com";
                URL url = null;
                try {
                    JsonObject json = new JsonObject();
                    json.addProperty("username", strings[0]);
                    json.addProperty("password", strings[1]);
                    json.addProperty("initialbalance", strings[2]);
                    json.addProperty("balance", strings[3]);
                    json.addProperty("categorylist", strings[4]);
                    json.addProperty("repetitivedata", strings[5]);
                    url = new URL(SERVERURL + "/financebackup");
                    HttpsURLConnection connectionUpload = (HttpsURLConnection) url.openConnection();
                    connectionUpload.setRequestMethod("POST");
                    connectionUpload.setDoOutput(true);
                    OutputStream out = connectionUpload.getOutputStream();
                    out.write(json.toString().getBytes("UTF-8"));
                    out.flush();
                    out.close();
                    BufferedReader br = new BufferedReader(new InputStreamReader(connectionUpload.getInputStream()));
                    String response = br.readLine().toString();
                    if (response.equals("success")) {
                        networkSuccess = true;
                    } else {
                        badPassword = true;
                    }
                    br.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                progressDialog.dismiss();
                if (badPassword) {
                    Toast.makeText(getActivity(), "Rossz jelszó!", Toast.LENGTH_SHORT).show();
                } else if (networkSuccess) {
                    Toast.makeText(getActivity(), "Feltöltés sikeres", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Feltöltés sikertelen", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }
}