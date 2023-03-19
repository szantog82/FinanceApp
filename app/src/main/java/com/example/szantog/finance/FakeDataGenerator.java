package com.example.szantog.finance;

import com.example.szantog.finance.Models.EntryItem;
import com.example.szantog.finance.Models.RepetitiveItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class FakeDataGenerator {

    private static String[] fakeExpCategories = new String[]{"shopping", "restaurant", "education", "household", "children",
            "travelling", "workout", "car", "hobby", "savings"};

    private static String[] fakeIncCategories = new String[]{"salary", "benefits", "grant"};

    private Calendar calendar;

    private long initialBalance;
    private ArrayList<EntryItem> fakeItems = new ArrayList<>();
    private ArrayList<RepetitiveItem> fakeRepetitiveItems = new ArrayList<>();

    public FakeDataGenerator() {
        Random random = new Random(System.currentTimeMillis());
        calendar = Calendar.getInstance();
        calendar.setTime(new Date(System.currentTimeMillis()));
        initialBalance = (long) random.nextInt(100000);

        for (int i = 0; i < 100; i++) {
            long time = random.nextLong();
            int year = random.nextInt(2) + calendar.get(Calendar.YEAR) - 2;
            int month = random.nextInt(12);
            int day = random.nextInt(29);
            long sum = 0;
            String category = "";
            if (i % 15 == 0) {
                sum = (long) random.nextInt(200000) + 300000;
                category = fakeIncCategories[random.nextInt(fakeIncCategories.length)];
            } else {
                sum = (long) random.nextInt(60000) * (-1);
                category = fakeExpCategories[random.nextInt(fakeExpCategories.length)];
            }
            String subCategory = "subcat" + String.valueOf(random.nextInt(20));
            fakeItems.add(new EntryItem(time, sum, year, month, day, category, subCategory));
        }
        for (int i = 0; i < (calendar.get(Calendar.MONTH) + 1) * 15; i++) {
            long time = random.nextLong();
            int year = calendar.get(Calendar.YEAR);
            int month = random.nextInt(calendar.get(Calendar.MONTH) + 1);
            int day = random.nextInt(29);
            long sum = 0;
            String category = "";
            if (i % 15 == 0) {
                sum = (long) random.nextInt(200000) + 300000;
                category = fakeIncCategories[random.nextInt(fakeIncCategories.length)];
            } else {
                sum = (long) random.nextInt(60000) * (-1);
                category = fakeExpCategories[random.nextInt(fakeExpCategories.length)];
            }
            String subCategory = "subcat" + String.valueOf(random.nextInt(20));
            fakeItems.add(new EntryItem(time, sum, year, month, day, category, subCategory));
        }
    }

    public long getInitialBalance() {
        return initialBalance;
    }

    public ArrayList<EntryItem> getFakeItems() {
        return fakeItems;
    }

    public long getTotalBalance() {
        long sum = 0;
        for (int i = 0; i < fakeItems.size(); i++) {
            sum += fakeItems.get(i).getSum();
        }
        return sum;
    }

    public ArrayList<RepetitiveItem> getFakeRepetitiveItems() {
        return fakeRepetitiveItems;
    }

    public void insertEntry(EntryItem item) {
        fakeItems.add(item);
    }

    public void updateEntry(EntryItem item) {
        int i = 0;
        while (i < fakeItems.size() && item.getTime() != fakeItems.get(i).getTime()) {
            i++;
        }
        if (i < fakeItems.size()) {
            fakeItems.remove(i);
            fakeItems.add(item);
        }
    }

    public void deleteEntry(EntryItem item) {
        fakeItems.remove(item);
    }

    public String getAllRepetitiveCollections() {
        return "";
    }


    public ArrayList<EntryItem> getCertainDailyData(int year, int month, int day) {
        ArrayList<EntryItem> items = new ArrayList<>();
        for (int i = 0; i < fakeItems.size(); i++) {
            if (fakeItems.get(i).getYear() == year && fakeItems.get(i).getMonth() == month && fakeItems.get(i).getDay() == day) {
                items.add(fakeItems.get(i));
            }
        }
        return items;
    }

    public ArrayList<EntryItem> getCertainMonthlyData(int year, int month) {
        ArrayList<EntryItem> items = new ArrayList<>();
        for (int i = 0; i < fakeItems.size(); i++) {
            if (fakeItems.get(i).getYear() == year && fakeItems.get(i).getMonth() == month) {
                items.add(fakeItems.get(i));
            }
        }
        return items;
    }

    public ArrayList<EntryItem> getCertainYearlyData(int year) {
        ArrayList<EntryItem> items = new ArrayList<>();
        for (int i = 0; i < fakeItems.size(); i++) {
            if (fakeItems.get(i).getYear() == year) {
                items.add(fakeItems.get(i));
            }
        }
        return items;
    }

    public ArrayList<EntryItem> getCertainMonthlyDataCategoryDefined(int year, int month, String category) {
        ArrayList<EntryItem> items = new ArrayList<>();
        for (int i = 0; i < fakeItems.size(); i++) {
            if (fakeItems.get(i).getYear() == year && fakeItems.get(i).getMonth() == month && fakeItems.get(i).getCategory().equals(category)) {
                items.add(fakeItems.get(i));
            }
        }
        return items;
    }

    public ArrayList<EntryItem> getCertainYearlyDataCategoryDefined(int year, String category) {
        ArrayList<EntryItem> items = new ArrayList<>();
        for (int i = 0; i < fakeItems.size(); i++) {
            if (fakeItems.get(i).getYear() == year && fakeItems.get(i).getCategory().equals(category)) {
                items.add(fakeItems.get(i));
            }
        }
        return items;
    }

    public ArrayList<EntryItem> customQuery(String sum, char sumOperator, long timeFrom, long timeTo, String category, String categoryOperator,
                                            String subCategory, String subCategoryOperator) {
        ArrayList<EntryItem> items = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < fakeItems.size(); i++) {
            calendar.set(fakeItems.get(i).getYear(), fakeItems.get(i).getMonth(), fakeItems.get(i).getDay());
            Boolean isTimeFrom_Matched = false;
            Boolean isTimeTo_Matched = false;
            Boolean isCategory_Matched = false;

            if (timeFrom != 0 && timeFrom < calendar.getTimeInMillis()) {
                isTimeFrom_Matched = true;
            } else if (timeFrom == 0) {
                isTimeFrom_Matched = true;
            }
            if (timeTo != 0 && timeTo > calendar.getTimeInMillis()) {
                isTimeTo_Matched = true;
            } else if (timeTo == 0) {
                isTimeTo_Matched = true;
            }
            if (!category.equals("") && category.equals(fakeItems.get(i).getCategory())) {
                isCategory_Matched = true;
            } else if (category.equals("")) {
                isCategory_Matched = true;
            }
            if (isCategory_Matched && isTimeFrom_Matched && isTimeTo_Matched) {
                items.add(fakeItems.get(i));
            }
        }
        return items;
    }
}
