package com.szantog.filechooserdialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FileChooserDialogBuilder extends AlertDialog.Builder implements View.OnClickListener, AdapterView.OnItemClickListener {

    private AlertDialog mainDialog;

    private String actualDir;
    private ArrayList<File> container = new ArrayList<>();
    private File actualSelection;
    private ArrayList<String> filters = new ArrayList<>();

    private TextView storage_btn;
    private TextView sdcard_btn;
    private TextView currentFolder_textView;
    private ListView dialog_listView;
    private ListViewAdapter adapter;

    private int chooserType;
    private TextView selectedFileTextView;
    private EditText selectedFileEditText;
    private ImageView okbtn;

    private OnFileSelectedListener listener;

    private static final String PREF_DIR = "com.szantog.filechooserdialog.prefdir";

    public static final int OPEN = 100;
    public static final int SAVE = 101;


    public interface OnFileSelectedListener {
        void OnFileSelected(File file);
    }

    public FileChooserDialogBuilder(Context context, int chooserType, String... args) {
        super(context);
        this.chooserType = chooserType;
        if (args.length > 0) {
            filters.addAll(Arrays.asList(args));
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_mainlayout, null);
        setView(view);

        RelativeLayout openFileFooter = view.findViewById(R.id.dialog_footer_openfile);
        RelativeLayout saveFileFooter = view.findViewById(R.id.dialog_footer_savefile);

        if (chooserType == OPEN) {
            saveFileFooter.setVisibility(View.GONE);
            okbtn = view.findViewById(R.id.dialog_okbtn_openfile);
            selectedFileTextView = view.findViewById(R.id.dialog_selectedfile_openfile);
            okbtn.setOnClickListener(this);
        } else if (chooserType == SAVE) {
            openFileFooter.setVisibility(View.GONE);
            okbtn = view.findViewById(R.id.dialog_okbtn_savefile);
            okbtn.setOnClickListener(this);
            selectedFileEditText = view.findViewById(R.id.dialog_selectedfile_savefile);
        }

        mainDialog = this.create();
        mainDialog.show();
        actualDir = "/";
        currentFolder_textView = (TextView) view.findViewById(R.id.dialog_currentfolder);

        storage_btn = view.findViewById(R.id.dialog_storage);
        storage_btn.setOnClickListener(this);
        sdcard_btn = view.findViewById(R.id.dialog_sdcard);
        sdcard_btn.setOnClickListener(this);
        loadActualDirFiles(false);
        dialog_listView = view.findViewById(R.id.dialog_listview);
        adapter = new ListViewAdapter(context, container);
        dialog_listView.setAdapter(adapter);

        dialog_listView.setOnItemClickListener(this);

        ImageView dialog_folderup = view.findViewById(R.id.dialog_folderup);
        dialog_folderup.setOnClickListener(this);
    }

    private void loadActualDirFiles(Boolean update) {
        File dir = new File(actualDir);
        File[] files = dir.listFiles();
        currentFolder_textView.setText(dir.getAbsolutePath());
        container.clear();
        ArrayList<File> folders = new ArrayList<>();
        ArrayList<File> folderFiles = new ArrayList<>();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    folders.add(f);
                } else if (f.isFile()) {
                    if (filters != null && filters.size() > 0 && chooserType == OPEN) {
                        for (String str : filters) {
                            if (f.getName().contains(str)) {
                                folderFiles.add(f);
                                break;
                            }
                        }
                    } else {
                        folderFiles.add(f);
                    }
                }
            }
            Collections.sort(folders);
            Collections.sort(folderFiles);
            folders.addAll(folderFiles);
            container.addAll(folders);
        }
        if (update) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.dialog_folderup) {
            int index = actualDir.substring(0, actualDir.length() - 1).lastIndexOf("/");
            if (index < 2) {
                actualDir = "/";
            } else {
                actualDir = actualDir.substring(0, index) + "/";
            }
            loadActualDirFiles(true);
        } else if (view.getId() == R.id.dialog_storage) {
            actualDir = Environment.getExternalStorageDirectory().getPath();
            loadActualDirFiles(true);
        } else if (view.getId() == R.id.dialog_sdcard) {
            if (new File("/storage/extSdCard/").exists()) {
                actualDir = "/storage/extSdCard/";
                loadActualDirFiles(true);
            }
        } else if (view.getId() == R.id.dialog_okbtn_openfile) {
            if (listener != null) {
                listener.OnFileSelected(actualSelection);
            }
            container.clear();
            adapter.notifyDataSetChanged();
            mainDialog.dismiss();
        } else if (view.getId() == R.id.dialog_okbtn_savefile) {
            if (listener != null) {
                listener.OnFileSelected(new File(actualDir + selectedFileEditText.getText().toString()));
            }
            container.clear();
            adapter.notifyDataSetChanged();
            mainDialog.dismiss();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
        actualSelection = (File) adapterView.getItemAtPosition(pos);
        if (chooserType == SAVE) {
            if (actualSelection.isDirectory()) {
                actualDir = actualSelection.getAbsolutePath() + "/";
                loadActualDirFiles(true);
            } else if (actualSelection.isFile()) {
                selectedFileEditText.setText(actualSelection.getName());
            }
        } else if (chooserType == OPEN) {
            if (actualSelection.isDirectory()) {
                selectedFileTextView.setVisibility(View.GONE);
                okbtn.setVisibility(View.GONE);
                actualDir = actualSelection.getAbsolutePath() + "/";
                loadActualDirFiles(true);
            } else if (actualSelection.isFile()) {
                selectedFileTextView.setVisibility(View.VISIBLE);
                okbtn.setVisibility(View.VISIBLE);
                selectedFileTextView.setText(actualSelection.getName());
            }
        }
    }

    public void setOnFileSelectedListener(OnFileSelectedListener listener) {
        this.listener = listener;
    }
}
