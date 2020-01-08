package com.example.szantog.finance;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by szantog on 2018.04.04..
 */

public class ProgressDialog extends Dialog {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public ProgressDialog(@NonNull Context context) {
        super(context);

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(10, 10, 10, 10);
        TextView textView = new TextView(context);
        textView.setText("Töltés folyamatban...");
        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setIndeterminate(true);
        linearLayout.addView(textView);
        linearLayout.addView(progressBar);

        setContentView(linearLayout);

    }
}
