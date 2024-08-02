package com.szantog.filechooserdialog;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<File> fileItems;

    public ListViewAdapter(Context context, ArrayList<File> fileItems) {
        this.context = context;
        this.fileItems = fileItems;
    }

    @Override
    public int getCount() {
        if (fileItems.size() == 0) {
            return 1;
        } else {
            return fileItems.size();
        }
    }

    @Override
    public Object getItem(int i) {
        return fileItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listitem_layout, null);
        }
        ImageView imageView = view.findViewById(R.id.listitem_img);
        TextView nameTextView = view.findViewById(R.id.listitem_name);
        TextView sizeTextView = view.findViewById(R.id.listitem_size);
        if (fileItems.size() == 0) {
            imageView.setImageDrawable(null);
            nameTextView.setText("(Empty directory)");
            sizeTextView.setText("");
        } else {
            File actualFile = fileItems.get(pos);
            if (actualFile.isDirectory()) {
                imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.folder128x128, null));
                nameTextView.setText(actualFile.getName());
                sizeTextView.setText("DIR");
            } else if (actualFile.getName().contains(".mp3")) {
                imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.musicnote64x64, null));
                nameTextView.setText(actualFile.getName());
                sizeTextView.setText(String.valueOf(actualFile.length()));
            } else {
                imageView.setImageDrawable(null);
                nameTextView.setText(actualFile.getName());
                sizeTextView.setText(String.valueOf(actualFile.length()));
            }
        }
        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        if (fileItems.size() == 0) {
            return false;
        } else {
            return true;
        }
    }
}
