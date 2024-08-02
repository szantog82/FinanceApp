package com.example.szantog.finance.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.szantog.finance.Adapters.RepetitiveListViewAdapter;
import com.example.szantog.finance.Database.FinanceDatabaseHandler;
import com.example.szantog.finance.Database.RepetitiveDatabaseHandler;
import com.example.szantog.finance.Models.EntryItem;
import com.example.szantog.finance.Models.RepetitiveItem;
import com.example.szantog.finance.NewEditAlertDialog;
import com.example.szantog.finance.R;

import java.util.ArrayList;

/**
 * Created by szantog on 2018.03.29..
 */

public class RepetitiveEditActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener, NewEditAlertDialog.newEditAlertDialogListener {

    private NewEditAlertDialog mDialog;

    private ArrayList<RepetitiveItem> repetitiveItems = new ArrayList<>();
    private RepetitiveListViewAdapter adapter;
    private RepetitiveDatabaseHandler repetitiveDb;

    private ImageView editButton;
    private ImageView deleteButton;

    private ArrayList<Integer> selectedEntryPosition = new ArrayList<Integer>(); //0 or positive integer, if any of them is selected (only one at a time)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.repetitive_activity_layout);

        mDialog = new NewEditAlertDialog(this, true);
        mDialog.setOnOKButtonListener(this);
        ImageView newButton = findViewById(R.id.repetitiveactivity_newentry);
        editButton = findViewById(R.id.repetitiveactivity_editentry);
        deleteButton = findViewById(R.id.repetitiveactivity_deleteentry);
        newButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        repetitiveDb = new RepetitiveDatabaseHandler(this);
        repetitiveItems = repetitiveDb.getAllData();
        ListView listView = findViewById(R.id.repetitiveactivity_listview);
        listView.setOnItemClickListener(this);
        adapter = new RepetitiveListViewAdapter(this, repetitiveItems, selectedEntryPosition);
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(this, MainActivity.class);
        backIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(backIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void deleteSelectedEntry() {
        final RepetitiveItem item = repetitiveItems.get(selectedEntryPosition.get(0));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String previousOccurrences = item.getCollection();
        builder.setTitle("Megerősítés");
        builder.setMessage(String.format("Visszamenőleg töröljünk, vagy csak a jövőbeli előfordulásokat? (%s/%s)", item.getCategory(), item.getSubCategory()));
        builder.setPositiveButton("Jövőbeni", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                repetitiveDb.deleteData(repetitiveItems.get(selectedEntryPosition.get(0)).getTime());
                repetitiveItems.remove(item);
                Toast.makeText(RepetitiveEditActivity.this, "Törölve", Toast.LENGTH_SHORT).show();
                selectedEntryPosition.clear();
                adapter.notifyDataSetChanged();
            }
        });
        builder.setNeutralButton("Mégse", null);
        builder.setNegativeButton("Visszamenőleg", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String[] collection = previousOccurrences.split(RepetitiveDatabaseHandler.DIVIDER);
                FinanceDatabaseHandler financeDb = new FinanceDatabaseHandler(RepetitiveEditActivity.this);
                for (String timeStr : collection) {
                    try {
                        financeDb.deleteEntryByTime(Long.parseLong(timeStr));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                repetitiveDb.deleteData(repetitiveItems.get(selectedEntryPosition.get(0)).getTime());
                repetitiveItems.remove(item);
                Toast.makeText(RepetitiveEditActivity.this, "Összes korábbi törölve", Toast.LENGTH_SHORT).show();
                selectedEntryPosition.clear();
                adapter.notifyDataSetChanged();
            }
        });
        builder.show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.repetitiveactivity_newentry) {
            mDialog = null;
            mDialog = new NewEditAlertDialog(this, true);
            mDialog.setOnOKButtonListener(this);
            mDialog.showDialog(true, null, null, null);
        } else if (view.getId() == R.id.repetitiveactivity_deleteentry) {
            deleteSelectedEntry();
        } else if (view.getId() == R.id.repetitiveactivity_editentry) {
            mDialog.showDialog(false, null, null, repetitiveItems.get(selectedEntryPosition.get(0)));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (selectedEntryPosition.size() > 0 && position == selectedEntryPosition.get(0)) {
            selectedEntryPosition.clear();
            editButton.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
        } else {
            selectedEntryPosition.clear();
            selectedEntryPosition.add(position);
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onOKButtonClicked(EntryItem item, Boolean isNewEntry) {

    }

    @Override
    public void onOKButtonClickedRepetitive(RepetitiveItem item, Boolean isNewEntry) {
        selectedEntryPosition.clear();
        if (isNewEntry) {
            repetitiveDb.insertData(item);
            ArrayList<EntryItem> newItems = repetitiveDb.checkData();
            if (newItems.size() > 0) {
                FinanceDatabaseHandler db = new FinanceDatabaseHandler(this);
                for (EntryItem newItem : newItems) {
                    db.insertEntry(newItem);
                }
                db.close();
            }
            repetitiveItems.add(item);
        } else {
            repetitiveDb.updateData(item);
            repetitiveItems.clear();
            repetitiveItems.addAll(repetitiveDb.getAllData());
        }
        adapter.notifyDataSetChanged();
    }
}
