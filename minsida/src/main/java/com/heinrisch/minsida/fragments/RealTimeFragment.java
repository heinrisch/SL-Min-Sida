package com.heinrisch.minsida.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.heinrisch.minsida.MainActivity;
import com.heinrisch.minsida.R;
import com.heinrisch.minsida.RESTHandler;
import com.heinrisch.minsida.models.DPSDepartures;
import retrofit.http.Callback;
import retrofit.http.RetrofitError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * User: henrik
 * Date: 3/9/13
 * Copyright (c) 2013 SBLA
 */
public class RealTimeFragment extends Fragment implements View.OnLongClickListener, View.OnClickListener {
  public static final String ARG_HEADER = "header";
  public static final String ARG_SITE = "site";
  public static final String ARG_TIME_WINDOW = "time-window";
  public static final String ARG_ID = "id";

  private LinearLayout rootViewGroup;
  private LinearLayout depaturesList;
  private ProgressBar progressBar;

  private String header;
  private int siteId;
  private int timeWindow;
  private String id;
  private DPSDepartures dpsDepartures;
  private List<Time> countDowns;
  private Handler handler;

  public static RealTimeFragment newInstance(String header, int siteId, int timeWindow, String Id) {
    Bundle args = new Bundle();
    args.putString(ARG_HEADER, header);
    args.putInt(ARG_SITE, siteId);
    args.putInt(ARG_TIME_WINDOW, timeWindow);
    args.putString(ARG_ID, Id);

    RealTimeFragment fragment = new RealTimeFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle args = getArguments();
    header = args.getString(ARG_HEADER);
    siteId = args.getInt(ARG_SITE);
    timeWindow = args.getInt(ARG_TIME_WINDOW);
    id = args.getString(ARG_ID);

    handler = new Handler();

    setRetainInstance(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.real_time_fragment, container, false);

    rootViewGroup = (LinearLayout) view.findViewById(R.id.root);
    depaturesList = (LinearLayout) view.findViewById(R.id.departuresList);
    progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

    TextView headerView = (TextView) view.findViewById(R.id.header);
    headerView.setText(header);

    view.setOnLongClickListener(this);
    view.setOnClickListener(this);

    return view;
  }

  @Override
  public void onStart() {
    super.onStart();
    refresh();
  }

  private void refresh() {
    if (dpsDepartures == null) {
      startTask();
      RESTHandler.getDPSDepatures(siteId, timeWindow, new Callback<DPSDepartures>() {
        @Override
        public void success(DPSDepartures response) {
          stopTask();
          dpsDepartures = response;
          if (dpsDepartures.DPS.Buses != null) {
            Arrays.sort(dpsDepartures.DPS.Buses.DpsBus);
          }
          onDepaturesRecieved();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
          stopTask();
          Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
          retrofitError.getException().printStackTrace();
          Log.e("Failed", retrofitError.getException().toString());
          Log.e("Url:", retrofitError.getUrl());
        }
      });
    } else {
      onDepaturesRecieved();
      stopTask();
    }
  }

  private void onDepaturesRecieved() {
    depaturesList.removeAllViews();
    if (dpsDepartures.DPS.Buses == null) {
      return;
    }
    countDowns = new ArrayList<Time>();
    handler.removeCallbacks(countdown);
    long now = new Date().getTime();
    for (DPSDepartures.DPSObject.BusesObject.DpsBusObject bus : dpsDepartures.DPS.Buses.DpsBus) {
      View view = getActivity().getLayoutInflater().inflate(R.layout.depature_list_item, null);

      ((TextView) view.findViewById(R.id.destination)).setText(bus.Destination);
      ((TextView) view.findViewById(R.id.lineNumber)).setText(String.valueOf(bus.LineNumber));

      TextView displayTime = (TextView) view.findViewById(R.id.displayTime);
      displayTime.setText(getTimeToDepatureString(now, bus.getExpectedDate()));
      countDowns.add(new Time(displayTime, bus.getExpectedDate()));

      depaturesList.addView(view);
    }

    handler.postDelayed(countdown, 1000);
  }

  public String getTimeToDepatureString(long now, long depatureTime) {
    long seconds = ((depatureTime - now) / (1000));

    long minutes = seconds / 60;

    if (seconds >= 60) {
      return Math.abs(minutes) + " min";
    } else if (seconds >= 0) {
      return "Nu";
    } else {
      return "Avgått";
    }
  }

  private void stopTask() {
    progressBar.setVisibility(View.GONE);
    depaturesList.setVisibility(View.VISIBLE);
  }

  private void startTask() {
    progressBar.setVisibility(View.VISIBLE);
    depaturesList.setVisibility(View.GONE);
  }

  private final Runnable countdown = new Runnable() {
    @Override
    public void run() {
      long now = new Date().getTime();
      for (Time t : countDowns) {
        t.view.setText(getTimeToDepatureString(now, t.time));
      }
      handler.postDelayed(this, 1000);
    }
  };

  @Override
  public boolean onLongClick(View v) {
    SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.SETTINGS, 0);
    SharedPreferences.Editor editor = settings.edit();
    editor.remove(id);
    editor.commit();

    FragmentTransaction transaction = getActivity().getFragmentManager().beginTransaction();
    transaction.remove(this);
    transaction.commit();

    return true;
  }

  @Override
  public void onClick(View v) {
    reload();
  }

  private void reload() {
    handler.removeCallbacks(countdown);
    dpsDepartures = null;
    depaturesList.removeAllViews();
    refresh();
  }

  @Override
  public void onStop() {
    super.onStop();
    handler.removeCallbacks(countdown);
  }

  class Time {

    public Time(TextView view, long time) {
      this.view = view;
      this.time = time;
    }

    public TextView view;
    public long time;
  }
}
