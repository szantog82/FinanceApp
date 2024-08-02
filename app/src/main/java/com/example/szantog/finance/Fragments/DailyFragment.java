package com.example.szantog.finance.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szantog.finance.Activities.MainActivity;
import com.example.szantog.finance.Activities.RepetitiveEditActivity;
import com.example.szantog.finance.Activities.SettingsActivity;
import com.example.szantog.finance.Adapters.DailyListViewAdapter;
import com.example.szantog.finance.Database.FinanceDatabaseHandler;
import com.example.szantog.finance.Database.RepetitiveDatabaseHandler;
import com.example.szantog.finance.Models.EntryItem;
import com.example.szantog.finance.Models.PocketItem;
import com.example.szantog.finance.Models.RepetitiveItem;
import com.example.szantog.finance.NewEditAlertDialog;
import com.example.szantog.finance.R;
import com.example.szantog.finance.Tools;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, NewEditAlertDialog.newEditAlertDialogListener, DailyListViewAdapter.DailyAdapterListener {

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor sharedEditor;

    private FloatingActionButton addButton;
    private ImageButton prevDayButton;
    private ImageButton nextDayButton;

    private TextView currentDayText;
    private TextView dailyBalanceText;
    private TextView cumulatedBalanceText;

    private ListView entriesListView;
    private DailyListViewAdapter dailyListViewAdapter;

    private ArrayList<EntryItem> currentEntryItems = new ArrayList<>();

    private FinanceDatabaseHandler financeDb;
    private RepetitiveDatabaseHandler repetitiveDb;
    private String repetitiveCollections;

    private NewEditAlertDialog newEditAlertDialog;

    private ArrayList<Integer> selectedEntryPosition = new ArrayList<Integer>(); //0 or positive integer, if any of them is selected (only one at a time)
    private ArrayList<Boolean> isRepetitives = new ArrayList<>();
    private int currentVisibleDay = 0; //0 if today (can never be positive)
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy. MMMM dd.", new Locale("HU"));

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.daily_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.daily_menu_preferences) {
            Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (item.getItemId() == R.id.daily_menu_repetitive) {
            Intent repetitiveIntent = new Intent(getActivity(), RepetitiveEditActivity.class);
            startActivity(repetitiveIntent);
        } else if (item.getItemId() == R.id.menu_quit) {
            getActivity().finish();
        } else if (item.getItemId() == R.id.daily_menu_change_pocket) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.pocket_change_layout, null);
            final TextView currencyText = view.findViewById(R.id.pocket_dialog_currency_text);
            final EditText newPocketNameEditText = view.findViewById(R.id.pocket_dialog_new_pocket_name_edittext);
            final EditText newPocketCurrencyEditText = view.findViewById(R.id.pocket_dialog_new_pocket_currency_edittext);
            Spinner spinner = view.findViewById(R.id.pocket_dialog_spinner);
            ArrayList<PocketItem> pockets = financeDb.getPockets();
            final List<Integer> pocketIds = new ArrayList<>();
            final List<String> pocketNames = new ArrayList<>();
            final List<String> pocketCurrencies = new ArrayList<>();
            for (PocketItem pocketItem : pockets) {
                pocketIds.add(pocketItem.getId());
                pocketNames.add(pocketItem.getName());
                pocketCurrencies.add(pocketItem.getCurrency());
            }
            ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, pocketNames);
            spinner.setAdapter(arrayAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    currencyText.setText(pocketCurrencies.get(position));
                    Toast.makeText(getActivity(), "Az alábbi zseb kiválasztva: " + pocketNames.get(position), Toast.LENGTH_LONG).show();
                    sharedEditor.putInt(getActivity().getString(R.string.pocket_sharedpref_key), pocketIds.get(position));
                    sharedEditor.apply();
                    updateFragment();
                    updateAdapter();
                    ((MainActivity) getActivity()).getSupportActionBar().setTitle(pocketNames.get(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            builder.setView(view);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String newPocketName = newPocketNameEditText.getText().toString();
                    String newPocketCurrency = newPocketCurrencyEditText.getText().toString();
                    if (newPocketName.length() > 1 && newPocketCurrency.length() > 1) {
                        long newId = financeDb.addNewPocket(newPocketName, newPocketCurrency);
                        if (newId == -1) {
                            Toast.makeText(getActivity(), "Nem sikerült hozzáadni...", Toast.LENGTH_LONG).show();
                        } else {
                            sharedEditor.putInt(getActivity().getString(R.string.pocket_sharedpref_key), (int) newId);
                            sharedEditor.apply();
                            Toast.makeText(getActivity(), "Zseb hozzáadva", Toast.LENGTH_LONG).show();
                            ((MainActivity) getActivity()).getSupportActionBar().setTitle(newPocketName);
                            updateFragment();
                            updateAdapter();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Túl rövid adatok!", Toast.LENGTH_LONG).show();
                    }
                }
            });
            builder.setNegativeButton("Mégse", null);
            builder.show();
        }
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.daily_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPrefs = getContext().getSharedPreferences(getString(R.string.SHAREDPREF_MAINKEY), 0);
        sharedEditor = sharedPrefs.edit();

        financeDb = new FinanceDatabaseHandler(getContext());
        repetitiveDb = new RepetitiveDatabaseHandler(getContext());
        repetitiveCollections = repetitiveDb.getAllCollections();

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(financeDb.getPocketNameById(sharedPrefs.getInt(getActivity().getString(R.string.pocket_sharedpref_key), 1)));

        addButton = view.findViewById(R.id.daily_FAB);
        prevDayButton = view.findViewById(R.id.dailyfragment_prevday);
        nextDayButton = view.findViewById(R.id.dailyfragment_nextday);
        currentDayText = view.findViewById(R.id.dailyfragment_currentdaytext);
        currentDayText.setOnClickListener(this);
        dailyBalanceText = view.findViewById(R.id.dailyfragment_dailybalancetext);
        cumulatedBalanceText = view.findViewById(R.id.dailyfragment_cumulatedbalancetext);
        entriesListView = view.findViewById(R.id.dailyfragment_listview);

        dailyListViewAdapter = new DailyListViewAdapter(getContext(), currentEntryItems, selectedEntryPosition, isRepetitives);
        dailyListViewAdapter.setListener(this);

        entriesListView.setAdapter(dailyListViewAdapter);
        entriesListView.setOnItemClickListener(this);

        newEditAlertDialog = new NewEditAlertDialog(getContext(), false);
        newEditAlertDialog.setOnOKButtonListener(this);

        addButton.setOnClickListener(this);
        prevDayButton.setOnClickListener(this);
        nextDayButton.setOnClickListener(this);

        updateFragment();
    }

    private void updateFragment() {
        cumulatedBalanceText.setText(Tools.formatNumber(financeDb.getCurrentInitialValue() + financeDb.getTotalBalance(), financeDb.getCurrentPocketCurrency()));
        String currentDate;
        Date dateSet = new Date(System.currentTimeMillis() + Long.valueOf(currentVisibleDay) * 1000 * 3600 * 24);
        switch (currentVisibleDay) {
            case 0:
                currentDate = "Ma";
                break;
            case -1:
                currentDate = "Tegnap";
                break;
            case -2:
                currentDate = "Tegnapelőtt";
                break;
            default:
                //currentVisibleDay is a negative integer!!!
                currentDate = simpleDateFormat.format(dateSet);
                break;
        }
        currentDayText.setText(currentDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateSet);
        isRepetitives.clear();
        currentEntryItems.clear();
        currentEntryItems.addAll(financeDb.getCertainDailyData(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
        for (EntryItem item : currentEntryItems) {
            if (repetitiveCollections.contains(String.valueOf(item.getTime()))) {
                isRepetitives.add(true);
            } else {
                isRepetitives.add(false);
            }
        }
        dailyListViewAdapter.notifyDataSetChanged();

        dailyBalanceText.setText(Tools.calculateBalanceFromList(currentEntryItems, financeDb.getCurrentPocketCurrency()));
    }

    private void updateAdapter() {
        currentEntryItems.clear();
        currentEntryItems.addAll(financeDb.getCertainDailyData(setCalendar().get(Calendar.YEAR), setCalendar().get(Calendar.MONTH), setCalendar().get(Calendar.DAY_OF_MONTH)));
        dailyListViewAdapter.notifyDataSetChanged();
    }

    private Calendar setCalendar() {
        Date dateSet = new Date(System.currentTimeMillis() + Long.valueOf(currentVisibleDay) * 1000 * 3600 * 24);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateSet);
        return calendar;
    }

    private void deleteSelectedEntry() {
        financeDb.deleteEntry(currentEntryItems.get(selectedEntryPosition.get(0)));
        Toast.makeText(getContext(), "Törölve", Toast.LENGTH_SHORT).show();
        selectedEntryPosition.clear();
        updateAdapter();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dailyfragment_prevday:
                currentVisibleDay--;
                updateFragment();
                break;
            case R.id.dailyfragment_nextday:
                if (currentVisibleDay < 0) {
                    currentVisibleDay++;
                    updateFragment();
                }
                break;
            case R.id.daily_FAB:
                newEditAlertDialog = null;
                newEditAlertDialog = new NewEditAlertDialog(getContext(), false);
                newEditAlertDialog.setOnOKButtonListener(this);
                newEditAlertDialog.showDialog(true, setCalendar(), null, null);
                break;
            case R.id.dailyfragment_currentdaytext:
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                            currentVisibleDay = (int) ((calendar.getTimeInMillis() - System.currentTimeMillis()) / (1000 * 3600 * 24));
                            updateFragment();
                        }
                    }
                },
                        setCalendar().get(Calendar.YEAR), setCalendar().get(Calendar.MONTH), setCalendar().get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
            default:
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (selectedEntryPosition.size() > 0 && i == selectedEntryPosition.get(0)) {
            selectedEntryPosition.clear();
        } else {
            selectedEntryPosition.clear();
            selectedEntryPosition.add(i);
        }
        dailyListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onOKButtonClicked(EntryItem item, Boolean isNewEntry) {
        if (isNewEntry) {
            financeDb.insertEntry(item);
            updateAdapter();
            selectedEntryPosition.clear();
            Toast.makeText(getContext(), "Hozzáadva", Toast.LENGTH_SHORT).show();
        } else {
            financeDb.updateEntry(item);
            updateAdapter();
            selectedEntryPosition.clear();
            Toast.makeText(getContext(), "Javítva", Toast.LENGTH_SHORT).show();
        }
        updateFragment();
    }

    @Override
    public void onOKButtonClickedRepetitive(RepetitiveItem item, Boolean isNewEntry) {

    }

    @Override
    public void onEditClicked(EntryItem item) {
        newEditAlertDialog.showDialog(false, setCalendar(), currentEntryItems.get(selectedEntryPosition.get(0)), null);
    }

    @Override
    public void onDeleteClicked(EntryItem item) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Megerősítés");
        dialog.setMessage("Biztos, hogy töröljük?");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteSelectedEntry();
            }
        });
        dialog.setNegativeButton("Mégse", null);
        dialog.show();
    }
}
