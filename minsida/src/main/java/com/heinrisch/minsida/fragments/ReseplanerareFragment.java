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
import com.heinrisch.minsida.*;
import com.heinrisch.minsida.models.DPSDepartures;
import com.heinrisch.minsida.models.Reseplanerare;
import com.heinrisch.minsida.models.Time;
import com.heinrisch.minsida.views.TripView;
import retrofit.http.Callback;
import retrofit.http.RetrofitError;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: henrik
 * Date: 3/9/13
 * Copyright (c) 2013 SBLA
 */
public class ReseplanerareFragment extends Fragment implements View.OnLongClickListener, View.OnClickListener {
  public static final String ARG_HEADER = "header";
  public static final String ARG_START_SITE = "startSite";
  public static final String ARG_END_SITE = "endSite";
  public static final String ARG_ID = "id";

  private LinearLayout rootViewGroup;
  private LinearLayout depaturesList;
  private ProgressBar progressBar;

  private String header;
  private int startSiteId;
  private int endSiteId;
  private String id;
  private Reseplanerare reseplanerare;
  private DPSDepartures dpsDepartures;
  private List<Time> countDowns;
  private Handler handler;

  public static ReseplanerareFragment newInstance(String header, int startSiteId, int endSiteId, String Id) {
    Bundle args = new Bundle();
    args.putString(ARG_HEADER, header);
    args.putInt(ARG_START_SITE, startSiteId);
    args.putInt(ARG_END_SITE, endSiteId);
    args.putString(ARG_ID, Id);

    ReseplanerareFragment fragment = new ReseplanerareFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle args = getArguments();
    header = args.getString(ARG_HEADER);
    startSiteId = args.getInt(ARG_START_SITE);
    endSiteId = args.getInt(ARG_END_SITE);
    id = args.getString(ARG_ID);

    handler = new Handler();

    setRetainInstance(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.reseplanerare_fragment, container, false);


    ((TextView) view.findViewById(R.id.header)).setText(header);
    rootViewGroup = (LinearLayout) view.findViewById(R.id.root);
    depaturesList = (LinearLayout) view.findViewById(R.id.departuresList);
    progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

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
    if (reseplanerare == null) {
      dpsDepartures = null;
      startTask();
      Date oneMinuteAgo = new Date(new Date().getTime() - 60 * 1000);
      ReseplanerareFetcher.getReseplanerare(startSiteId, endSiteId, Tools.DateFormatterHourMinutes.format(oneMinuteAgo), new Callback<Reseplanerare>() {
        @Override
        public void success(Reseplanerare result) {
          stopTask();
          reseplanerare = result;
          onReseplanerareReceived();
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

      DPSDeparturesFetcher.getDPSDepatures(startSiteId, 60, new Callback<DPSDepartures>() {
        @Override
        public void success(DPSDepartures result) {
          dpsDepartures = result;
          onDPSDepaturesReceived();
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
      onReseplanerareReceived();
      stopTask();
    }
  }

  private void onDPSDepaturesReceived() {
    if (reseplanerare != null) {
      onReseplanerareReceived();
    }
  }

  private void onReseplanerareReceived() {
    depaturesList.removeAllViews();
    if (reseplanerare.HafasResponse.Trip == null) {
      return;
    }
    countDowns = new ArrayList<Time>();
    handler.removeCallbacks(countdown);
    long now = new Date().getTime();
    for (Reseplanerare.HafasResponseObject.TripObject trip : reseplanerare.HafasResponse.Trip) {
      TripView view = new TripView(getActivity());
      view.setTrip(trip, dpsDepartures);

      countDowns.addAll(view.getTimes());
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
    reseplanerare = null;
    depaturesList.removeAllViews();
    refresh();
  }

  @Override
  public void onStop() {
    super.onStop();
    handler.removeCallbacks(countdown);
  }
}
