package com.heinrisch.minsida.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.heinrisch.minsida.R;
import com.heinrisch.minsida.Tools;
import com.heinrisch.minsida.models.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: henrik
 * Date: 3/10/13
 * Copyright (c) 2013 SBLA
 */
public class DeviationView extends LinearLayout {

  public DeviationView(Context context) {
    super(context);
  }

  public DeviationView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public DeviationView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  private String info;

  public void setDeviation(Deviation deviation, final Activity activity) {
    LayoutInflater inflater = LayoutInflater.from(getContext());
    inflater.inflate(R.layout.deviation_view, this);

    TextView header = (TextView) findViewById(R.id.header);
    header.setText(deviation.GetDeviationsResponse.GetDeviationsResult.aWCFDeviation.aHeader);
    info = deviation.GetDeviationsResponse.GetDeviationsResult.aWCFDeviation.aDetails;

    header.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        new MessageDialogFragment().show(activity.getFragmentManager(), null);
      }
    });

    header.setVisibility(View.VISIBLE);


  }

  public class MessageDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      builder.setMessage(info)
              .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                  dismiss();
                }
              });
      return builder.create();
    }
  }

}
