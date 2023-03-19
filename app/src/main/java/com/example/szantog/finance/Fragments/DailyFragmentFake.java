package com.example.szantog.finance.Fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szantog.finance.Adapters.DailyListViewAdapter;
import com.example.szantog.finance.FakeDataGenerator;
import com.example.szantog.finance.Models.EntryItem;
import com.example.szantog.finance.Models.RepetitiveItem;
import com.example.szantog.finance.NewEditAlertDialog;
import com.example.szantog.finance.R;
import com.example.szantog.finance.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DailyFragmentFake extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, NewEditAlertDialog.newEditAlertDialogListener, DailyListViewAdapter.DailyAdapterListener {

    private FakeDataGenerator fakeDataGenerator;

    private FloatingActionButton addButton;
    private ImageButton prevDayButton;
    private ImageButton nextDayButton;

    private TextView currentDayText;
    private TextView dailyBalanceText;
    private TextView cumulatedBalanceText;

    private ListView entriesListView;
    private DailyListViewAdapter dailyListViewAdapter;

    private ArrayList<EntryItem> currentEntryItems = new ArrayList<>();

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
            //  Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
            //  startActivity(settingsIntent);
        } else if (item.getItemId() == R.id.daily_menu_repetitive) {
            //  Intent repetitiveIntent = new Intent(getActivity(), RepetitiveEditActivity.class);
            //  startActivity(repetitiveIntent);
        } else if (item.getItemId() == R.id.menu_quit) {
            getActivity().finish();
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

        fakeDataGenerator = new FakeDataGenerator();

        repetitiveCollections = fakeDataGenerator.getAllRepetitiveCollections();

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
        cumulatedBalanceText.setText(Tools.formatNumber(fakeDataGenerator.getInitialBalance() + fakeDataGenerator.getTotalBalance()));
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
        currentEntryItems.addAll(fakeDataGenerator.getCertainDailyData(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)));
        for (EntryItem item : currentEntryItems) {
            if (repetitiveCollections.contains(String.valueOf(item.getTime()))) {
                isRepetitives.add(true);
            } else {
                isRepetitives.add(false);
            }
        }
        dailyListViewAdapter.notifyDataSetChanged();

        dailyBalanceText.setText(Tools.calculateBalanceFromList(currentEntryItems));
    }

    private void updateAdapter() {
        currentEntryItems.clear();
        currentEntryItems.addAll(fakeDataGenerator.getCertainDailyData(setCalendar().get(Calendar.YEAR), setCalendar().get(Calendar.MONTH), setCalendar().get(Calendar.DAY_OF_MONTH)));
        dailyListViewAdapter.notifyDataSetChanged();
    }

    private Calendar setCalendar() {
        Date dateSet = new Date(System.currentTimeMillis() + Long.valueOf(currentVisibleDay) * 1000 * 3600 * 24);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateSet);
        return calendar;
    }

    private void deleteSelectedEntry() {
        fakeDataGenerator.deleteEntry(currentEntryItems.get(selectedEntryPosition.get(0)));
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
            fakeDataGenerator.insertEntry(item);
            updateAdapter();
            selectedEntryPosition.clear();
            Toast.makeText(getContext(), "Hozzáadva", Toast.LENGTH_SHORT).show();
        } else {
            fakeDataGenerator.updateEntry(item);
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
