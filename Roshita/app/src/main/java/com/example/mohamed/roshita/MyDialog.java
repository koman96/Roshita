package com.example.mohamed.roshita;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class MyDialog extends Dialog {
    private Context context;
    public Button yes ,no;
    public TextView dialogText;

    public MyDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.my_dialog);

        yes = (Button) findViewById(R.id.yesDialog);
        no = (Button) findViewById(R.id.noDialog);
        dialogText = (TextView) findViewById(R.id.dialogText);
    }
}