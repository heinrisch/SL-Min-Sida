package com.heinrisch.minsida.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.heinrisch.minsida.R;

/**
 * User: henrik
 * Date: 3/9/13
 * Copyright (c) 2013 SBLA.
 */
public class StationInputFragment extends Fragment{

  private EditText searchField;
  private Button searchButton;

  private OnSearchListener listener;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    listener = (OnSearchListener) getActivity();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.station_input_fragment, container, false);

    searchField = (EditText) view.findViewById(R.id.searchField);
    searchButton = (Button) view.findViewById(R.id.searchButton);

    searchButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        listener.onSearch(String.valueOf(searchField.getText()));
      }
    });

    return view;
  }

  public interface OnSearchListener {
    public void onSearch(String term);
  }

  @Override
  public void onStart() {
    super.onStart();
    searchField.requestFocus();
  }
}
