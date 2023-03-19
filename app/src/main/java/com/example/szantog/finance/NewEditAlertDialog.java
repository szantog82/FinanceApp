package com.example.szantog.finance;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szantog.finance.Adapters.NewEditEntryCategoryAdapter;
import com.example.szantog.finance.Adapters.SubCategoryListAdapter;
import com.example.szantog.finance.Database.FinanceDatabaseHandler;
import com.example.szantog.finance.Models.EntryItem;
import com.example.szantog.finance.Models.RepetitiveItem;
import com.example.szantog.finance.Models.SubCategoryListItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class NewEditAlertDialog extends AlertDialog implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {


    public interface newEditAlertDialogListener {
        public void onOKButtonClicked(EntryItem item, Boolean isNewEntry);

        public void onOKButtonClickedRepetitive(RepetitiveItem item, Boolean isNewEntry);
    }

    private Context context;
    private DisplayMetrics metrics;
    private Boolean isRepetitive;
    private TextView currentDateTextView;
    private TextView startDateTextView;
    private TextView endDateTextView;
    private TextView endDateTextViewSuppl;
    private Spinner turnoverSpinner;
    private long itemCurrentTime;
    private long itemStartTime;
    private long itemEndTime;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy. MMMM dd.", new Locale("HU"));
    private TextView sumTextView;
    private CheckBox subcatCheckbox;
    private CheckBox repetitiveCheckbox;
    private Spinner subcat;
    private Button new_subcat_btn;
    private ArrayList<SubCategoryListItem> subCategoryItems = new ArrayList<>();
    private SubCategoryListAdapter subCategoryAdapter;

    private FinanceDatabaseHandler db;

    private SharedPreferences sharedPrefs;
    private newEditAlertDialogListener listener;

    private GridView categoryGridView;
    private LinearLayout.LayoutParams gridParams;
    private NewEditEntryCategoryAdapter categoryAdapter;
    private int[] selectedCategory = {-1};
    private Boolean[] isIncome = {false};
    private RadioGroup incExpRadioGroup;

    private Boolean isNewEntry;
    private long itemTimePrimaryKey;
    private ArrayList<String> categoryList;

    public NewEditAlertDialog(final Context context, final Boolean isRepetitive) {
        super(context, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        this.context = context;
        this.isRepetitive = isRepetitive;

        metrics = new DisplayMetrics();
        this.getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.new_edit_entry_dialog2, null);

        setView(view);

        TextView currencyText = view.findViewById(R.id.new_edit_dialog_currency);
        FinanceDatabaseHandler financeDb = new FinanceDatabaseHandler(context);
        currencyText.setText(financeDb.getCurrentPocketCurrency());

        Button btn_0 = view.findViewById(R.id.new_edit_entry_include).findViewById(R.id.btn_0);
        Button btn_1 = view.findViewById(R.id.new_edit_entry_include).findViewById(R.id.btn_1);
        Button btn_2 = view.findViewById(R.id.new_edit_entry_include).findViewById(R.id.btn_2);
        Button btn_3 = view.findViewById(R.id.new_edit_entry_include).findViewById(R.id.btn_3);
        Button btn_4 = view.findViewById(R.id.new_edit_entry_include).findViewById(R.id.btn_4);
        Button btn_5 = view.findViewById(R.id.new_edit_entry_include).findViewById(R.id.btn_5);
        Button btn_6 = view.findViewById(R.id.new_edit_entry_include).findViewById(R.id.btn_6);
        Button btn_7 = view.findViewById(R.id.new_edit_entry_include).findViewById(R.id.btn_7);
        Button btn_8 = view.findViewById(R.id.new_edit_entry_include).findViewById(R.id.btn_8);
        Button btn_9 = view.findViewById(R.id.new_edit_entry_include).findViewById(R.id.btn_9);
        Button btn_c = view.findViewById(R.id.new_edit_entry_include).findViewById(R.id.btn_C);
        btn_0.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_6.setOnClickListener(this);
        btn_7.setOnClickListener(this);
        btn_8.setOnClickListener(this);
        btn_9.setOnClickListener(this);
        btn_c.setOnClickListener(this);

        db = new FinanceDatabaseHandler(context);
        categoryList = new ArrayList<>();
        categoryGridView = view.findViewById(R.id.new_edit_entry_gridview);
        gridParams = (LinearLayout.LayoutParams) categoryGridView.getLayoutParams();
        categoryAdapter = new NewEditEntryCategoryAdapter(getContext(), categoryList, selectedCategory, isIncome);
        categoryGridView.setAdapter(categoryAdapter);

        sharedPrefs = context.getSharedPreferences(context.getString(R.string.SHAREDPREF_MAINKEY), 0);

        final ScrollView scrollView = view.findViewById(R.id.new_edit_entry_scrollview);
        currentDateTextView = view.findViewById(R.id.new_edit_entry_datetext);
        currentDateTextView.setOnClickListener(this);
        sumTextView = view.findViewById(R.id.sum);
        subcat = view.findViewById(R.id.subcat);
        new_subcat_btn = view.findViewById(R.id.new_edit_entry_new_subcat_btn);
        new_subcat_btn.setOnClickListener(this);
        new_subcat_btn.setEnabled(false);
        subcatCheckbox = view.findViewById(R.id.new_edit_entry_subcat_checkbox);
        subcatCheckbox.setChecked(false);
        subcatCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    subcat.setVisibility(View.VISIBLE);
                    new_subcat_btn.setEnabled(true);
                    updateSubCategories();
                } else {
                    subcat.setVisibility(View.INVISIBLE);
                    new_subcat_btn.setEnabled(false);
                }
            }
        });

        subCategoryAdapter = new SubCategoryListAdapter(context, subCategoryItems);
        subcat.setAdapter(subCategoryAdapter);

        incExpRadioGroup = view.findViewById(R.id.new_edit_entry_radiogroup);
        incExpRadioGroup.setOnCheckedChangeListener(this);

        categoryGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (selectedCategory[0] == i) {
                    selectedCategory[0] = -1;
                } else {
                    selectedCategory[0] = i;
                }
                categoryAdapter.notifyDataSetChanged();
                if (subcatCheckbox.isChecked()) {
                    updateSubCategories();
                }
                int[] location = {0, 0};
                sumTextView.getLocationOnScreen(location);
                scrollView.smoothScrollTo(0, scrollView.getScrollY() + location[1] - sumTextView.getHeight());
            }
        });

        updateCategories();

        Button ok_btn = view.findViewById(R.id.newentrydialog_ok_btn);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedCategory[0] < 0 || sumTextView.getText().toString().length() < 1) {
                    Toast.makeText(getContext(), "Nincs minden mező kitöltve", Toast.LENGTH_SHORT).show();
                    return;
                } else if (listener != null) {
                    long time;
                    if (isNewEntry) {
                        time = System.currentTimeMillis();
                    } else {
                        time = itemTimePrimaryKey;
                    }
                    long sumMoney = Long.parseLong(sumTextView.getText().toString());
                    if (incExpRadioGroup.getCheckedRadioButtonId() == R.id.new_edit_entry_radiobutton_expenditure) {
                        sumMoney = 0 - Math.abs(sumMoney);
                    } else {
                        sumMoney = Math.abs(sumMoney);
                    }
                    String subCategory;
                    if (subcatCheckbox.isChecked()) {
                        subCategory = (String) subcat.getSelectedItem();
                    } else {
                        subCategory = "";
                    }
                    String category = categoryList.get(selectedCategory[0]);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date(itemCurrentTime));
                    if (isRepetitive) {
                        listener.onOKButtonClickedRepetitive(new RepetitiveItem(time, sumMoney, itemStartTime, itemEndTime, 0, turnoverSpinner.getSelectedItemPosition() + 1, "", category, subCategory), isNewEntry);
                    } else {
                        listener.onOKButtonClicked(new EntryItem(time, sumMoney, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH), category, subCategory), isNewEntry);
                    }
                }
                sumTextView.setText("");
                subcat.setSelection(0);
                subcat.setVisibility(View.INVISIBLE);
                new_subcat_btn.setEnabled(false);
                subcatCheckbox.setSelected(false);
                dismiss();
            }
        });
        Button cancel_btn = view.findViewById(R.id.newentrydialog_cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sumTextView.setText("");
                subcat.setSelection(0);
                subcat.setVisibility(View.INVISIBLE);
                new_subcat_btn.setEnabled(false);
                subcatCheckbox.setSelected(false);
                dismiss();
            }
        });

        if (isRepetitive) {
            currentDateTextView.setVisibility(View.GONE);
            LinearLayout repetitiveSection = view.findViewById(R.id.new_edit_entry_repetitive_section);
            repetitiveSection.setVisibility(View.VISIBLE);
            startDateTextView = view.findViewById(R.id.new_edit_entry_startdate);
            startDateTextView.setOnClickListener(this);
            endDateTextView = view.findViewById(R.id.new_edit_entry_enddate);
            endDateTextView.setOnClickListener(this);
            endDateTextViewSuppl = view.findViewById(R.id.new_edit_entry_enddate_suppl);
            turnoverSpinner = view.findViewById(R.id.new_edit_entry_turnover_spinner);
            String[] nums = {"1 havonta", "2 havonta", "3 havonta", "4 havonta", "5 havonta",
                    "6 havonta", "7 havonta", "8 havonta", "9 havonta", "10 havonta",
                    "11 havonta", "12 havonta"};
            ArrayAdapter<String> turnoverAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, nums);
            turnoverSpinner.setAdapter(turnoverAdapter);
            turnoverSpinner.setSelection(0);
            repetitiveCheckbox = view.findViewById(R.id.new_edit_entry_repetitive_checkbox);
            repetitiveCheckbox.setChecked(true);
            repetitiveCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        endDateTextView.setVisibility(View.INVISIBLE);
                        endDateTextViewSuppl.setVisibility(View.INVISIBLE);
                    } else {
                        endDateTextView.setVisibility(View.VISIBLE);
                        endDateTextViewSuppl.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

    }

    private void updateCategories() {
        Set<String> categorySet = null;
        if (incExpRadioGroup.getCheckedRadioButtonId() == R.id.new_edit_entry_radiobutton_expenditure) {
            categorySet = sharedPrefs.getStringSet(context.getString(R.string.expenditurecategorylist_key), null);
            isIncome[0] = false;
        } else {
            categorySet = sharedPrefs.getStringSet(context.getString(R.string.incomecategorylist_key), null);
            isIncome[0] = true;
        }
        categoryList.clear();
        if (categorySet != null) {
            categoryList.addAll(categorySet);
        }
        categoryAdapter.notifyDataSetChanged();
        //int rowCount = (int) Math.ceil((float) categoryList.size() / (float) categoryGridView.getNumColumns());
        int rowCount = (int) Math.ceil((float) categoryList.size() / 2f);
        gridParams.height = metrics.heightPixels / 10 * rowCount;
        categoryGridView.setLayoutParams(gridParams);
    }

    private void updateSubCategories() {
        if (selectedCategory[0] >= 0) {
            String category = categoryList.get(selectedCategory[0]);
            subCategoryItems.clear();
            subCategoryItems.addAll(db.getSubCategoriesforCategory(category));
            subCategoryAdapter.notifyDataSetChanged();
        }
    }

    public void showDialog(Boolean isNewEntry, Calendar calendar, EntryItem entryItemToEdit, RepetitiveItem repetitiveItemToEdit) {
        this.isNewEntry = isNewEntry;
        if (isNewEntry) {
            sumTextView.setText("");
            subcat.setSelection(0);
            if (calendar != null) {
                itemCurrentTime = calendar.getTimeInMillis();
                currentDateTextView.setText(simpleDateFormat.format(calendar.getTimeInMillis()));
            }
            if (isRepetitive) {
                itemStartTime = System.currentTimeMillis();
                startDateTextView.setText(simpleDateFormat.format(itemStartTime));
            }
        } else {
            if (entryItemToEdit != null && repetitiveItemToEdit == null && !isRepetitive) {
                if (entryItemToEdit.getSum() > 0) {
                    RadioButton radioButton = (RadioButton) incExpRadioGroup.getChildAt(0);
                    radioButton.setChecked(true);
                } else {
                    RadioButton radioButton = (RadioButton) incExpRadioGroup.getChildAt(1);
                    radioButton.setChecked(true);
                }
                for (int i = 0; i < categoryList.size(); i++) {
                    if (entryItemToEdit.getCategory().equals(categoryList.get(i))) {
                        selectedCategory[0] = i;
                        categoryAdapter.notifyDataSetChanged();
                    }
                }
                sumTextView.setText(String.valueOf(Math.abs(entryItemToEdit.getSum())));
                if (entryItemToEdit.getSubCategory().length() > 0) {
                    subcatCheckbox.setChecked(true);
                    subcat.setVisibility(View.VISIBLE);
                    new_subcat_btn.setEnabled(true);
                    for (int i = 0; i < subCategoryItems.size(); i++) {
                        if (entryItemToEdit.getSubCategory().equals(subCategoryItems.get(i).getName())) {
                            subcat.setSelection(i);
                        }
                    }
                } else {
                    subcatCheckbox.setChecked(false);
                }
                itemTimePrimaryKey = entryItemToEdit.getTime();
                if (calendar != null) {
                    itemCurrentTime = calendar.getTimeInMillis();
                } else {
                    itemCurrentTime = System.currentTimeMillis();
                }
                currentDateTextView.setText(simpleDateFormat.format(new Date(itemCurrentTime)));
            } else if (entryItemToEdit == null && repetitiveItemToEdit != null && isRepetitive) {
                itemTimePrimaryKey = repetitiveItemToEdit.getTime();
                sumTextView.setText(String.valueOf(Math.abs(repetitiveItemToEdit.getSum())));
                for (int i = 0; i < categoryList.size(); i++) {
                    if (repetitiveItemToEdit.getCategory().equals(categoryList.get(i))) {
                        selectedCategory[0] = i;
                        categoryAdapter.notifyDataSetChanged();
                    }
                }
                if (repetitiveItemToEdit.getSubCategory().length() > 0) {
                    subcatCheckbox.setChecked(true);
                    subcat.setVisibility(View.VISIBLE);
                    new_subcat_btn.setEnabled(true);
                    for (int i = 0; i < subCategoryItems.size(); i++) {
                        if (repetitiveItemToEdit.getSubCategory().equals(subCategoryItems.get(i).getName())) {
                            subcat.setSelection(i);
                        }
                    }
                } else {
                    subcatCheckbox.setChecked(false);
                }
                startDateTextView.setText(simpleDateFormat.format(repetitiveItemToEdit.getStartTime()));
                startDateTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "Kezdődátum nem változtatható!", Toast.LENGTH_SHORT).show();
                    }
                });
                if (repetitiveItemToEdit.getEndTime() == 0) {
                    repetitiveCheckbox.setChecked(true);
                    endDateTextView.setVisibility(View.INVISIBLE);
                    endDateTextViewSuppl.setVisibility(View.VISIBLE);
                } else {
                    repetitiveCheckbox.setChecked(false);
                    endDateTextView.setVisibility(View.VISIBLE);
                    endDateTextViewSuppl.setVisibility(View.INVISIBLE);
                    endDateTextView.setText(simpleDateFormat.format(repetitiveItemToEdit.getEndTime()));
                }
                turnoverSpinner.setSelection(repetitiveItemToEdit.getTurnoverMonth() - 1);
            }
        }

        show();

    }

    public void setOnOKButtonListener(newEditAlertDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.new_edit_entry_new_subcat_btn) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            final EditText editText = new EditText(context);
            dialog.setView(editText);
            dialog.setTitle("Új alkategória");
            dialog.setPositiveButton("OK", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    subCategoryItems.add(new SubCategoryListItem(editText.getText().toString(), 0));
                    subCategoryAdapter.notifyDataSetChanged();
                    subcat.setSelection(subCategoryItems.size() - 1);
                }
            });
            dialog.setNegativeButton("Mégse", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.show();
        } else if (view.getId() == R.id.new_edit_entry_datetext) {
            final Calendar calendar = Calendar.getInstance();
            if (itemCurrentTime == 0) {
                itemCurrentTime = System.currentTimeMillis();
            }
            calendar.setTime(new Date(itemCurrentTime));
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(year, month, dayOfMonth);
                    itemCurrentTime = calendar.getTimeInMillis();
                    currentDateTextView.setText(simpleDateFormat.format(new Date(itemCurrentTime)));
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        } else if (view.getId() == R.id.new_edit_entry_startdate) {
            final Calendar calendar = Calendar.getInstance();
            if (itemStartTime == 0) {
                itemStartTime = System.currentTimeMillis();
            }
            calendar.setTime(new Date(itemStartTime));
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(year, month, dayOfMonth);
                    itemStartTime = calendar.getTimeInMillis();
                    startDateTextView.setText(simpleDateFormat.format(new Date(itemStartTime)));
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        } else if (view.getId() == R.id.new_edit_entry_enddate) {
            final Calendar calendar = Calendar.getInstance();
            if (itemEndTime == 0) {
                itemEndTime = System.currentTimeMillis();
            }
            calendar.setTime(new Date(itemEndTime));
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(year, month, dayOfMonth);
                    if (calendar.getTimeInMillis() < itemStartTime) {
                        Toast.makeText(context, "Végső időpont nem lehet kisebb a kezdetinél!", Toast.LENGTH_SHORT).show();
                    } else {
                        itemEndTime = calendar.getTimeInMillis();
                        endDateTextView.setText(simpleDateFormat.format(new Date(itemEndTime)));
                    }
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        } else if (view instanceof Button) {
            Button btn = (Button) view;
            try {
                int num = Integer.parseInt(btn.getText().toString());
                sumTextView.setText(sumTextView.getText().toString() + String.valueOf(num));
            } catch (NumberFormatException e) {
                if (sumTextView.getText().toString().length() > 0) {
                    sumTextView.setText(sumTextView.getText().toString().substring(0, sumTextView.getText().toString().length() - 1));
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (radioGroup.getId() == R.id.new_edit_entry_radiogroup) {
            selectedCategory[0] = -1;
            updateCategories();
        }
    }
}
