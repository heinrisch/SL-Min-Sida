package com.heinrisch.minsida.fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.heinrisch.minsida.MainActivity;
import com.heinrisch.minsida.R;

import java.util.UUID;

/**
 * User: henrik
 * Date: 3/9/13
 * Copyright (c) 2013 SBLA
 */
public class NewReseplanerareComponent extends Activity implements StationInputFragment.OnSearchListener, StationSelectionFragment.OnStationSelection {
  private final static String TAG_ACTIVE_FRAGMENT = "active-fragment";

  private ProgressBar progressBar;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.new_component_fragment);

    FrameLayout container = (FrameLayout) findViewById(R.id.mainContainer);
    progressBar = (ProgressBar) findViewById(R.id.progressBar);

    if (getFragmentManager().findFragmentByTag(TAG_ACTIVE_FRAGMENT) == null) {
      FragmentTransaction transaction = getFragmentManager().beginTransaction();
      transaction.add(R.id.mainContainer, new StationInputFragment(), TAG_ACTIVE_FRAGMENT);
      transaction.commit();

      stopTask();
    }
  }

  private void stopTask() {
    progressBar.setVisibility(View.GONE);
  }

  private void startTask() {
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override
  public void onSearch(String term) {
    startTask();
    FragmentTransaction transaction = getFragmentManager().beginTransaction();
    StationSelectionFragment fragment = StationSelectionFragment.newInstance(term);
    transaction.replace(R.id.mainContainer, fragment, TAG_ACTIVE_FRAGMENT);
    transaction.commit();
  }

  @Override
  public void onSelectionLoaded() {
    stopTask();
  }

  private String header = null;
  private int startSite;

  @Override
  public void onSiteSelected(String name, int siteId) {
    if (header == null) {
      header = name;
      startSite = siteId;

      FragmentTransaction transaction = getFragmentManager().beginTransaction();
      transaction.replace(R.id.mainContainer, new StationInputFragment(), TAG_ACTIVE_FRAGMENT);
      transaction.commit();

    } else {
      SharedPreferences settings = getSharedPreferences(MainActivity.SETTINGS, 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putString(UUID.randomUUID().toString(), MainActivity.TYPE_RESEPLANERARE + "," + header + "," + startSite + "," + siteId);
      editor.commit();
      finish();
    }
  }
}
