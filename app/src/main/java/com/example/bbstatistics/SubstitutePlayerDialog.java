package com.example.bbstatistics;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by zdenko on 2015-04-05.
 */
public class SubstitutePlayerDialog extends Dialog {
    private  boolean mIsOkPressed;

    public SubstitutePlayerDialog(Context context) {
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.dialog_substitute);
        Button btnOk = (Button) findViewById(R.id.btnOK);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsOkPressed = true;
                dismiss();
            }
        });
        Button btnDismiss = (Button) findViewById(R.id.btnDismiss);
        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsOkPressed = false;
                dismiss();
            }
        });
    }

    public boolean isIsOkPressed() {
        Log.v(Consts.TAG, "SubstitutePlayerDialog#isIsOkPressed() returning " + mIsOkPressed);
        return mIsOkPressed;
    }
}
