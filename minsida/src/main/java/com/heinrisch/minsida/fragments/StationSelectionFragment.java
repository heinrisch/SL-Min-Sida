package com.heinrisch.minsida.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.heinrisch.minsida.R;
import com.heinrisch.minsida.RESTHandler;
import com.heinrisch.minsida.models.Sites;
import retrofit.http.Callback;
import retrofit.http.RetrofitError;

/**
 * User: henrik
 * Date: 3/9/13
 * Copyright (c) 2013 SBLA.
 */
public class StationSelectionFragment extends ListFragment{
  private static final String ARG_SEARCH_TERM = "search-term";

  private String searchTerm;
  private OnStationSelection listener;

  public static StationSelectionFragment newInstance(String searchTerm){
    Bundle args = new Bundle();

    args.putString(ARG_SEARCH_TERM, searchTerm);

    StationSelectionFragment fragment = new StationSelectionFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle args = getArguments();
    searchTerm = args.getString(ARG_SEARCH_TERM);

    listener = (OnStationSelection) getActivity();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.station_selection_fragment, container, false);



    return view;
  }

  @Override
  public void onStart() {
    super.onStart();
    RESTHandler.getSites(searchTerm, new Callback<Sites>() {
      @Override
      public void success(Sites sites) {
        listener.onSelectionLoaded();
        if (sites.Hafas.Sites != null) {
          onSitesReceived(sites.Hafas.Sites);
        } else {
          Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
        }
      }

      @Override
      public void failure(RetrofitError retrofitError) {
        listener.onSelectionLoaded();
        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
        retrofitError.getException().printStackTrace();
        Log.e("Failed", retrofitError.getException().toString());
      }
    });
  }

  private void onSitesReceived(Sites.HafasObject.SitesObject sites) {
    setListAdapter(new StationSelectionAdapter(getActivity(), sites));
  }

  public interface OnStationSelection{
    public void onSelectionLoaded();
    public void onSiteSelected(String name, int siteId);
  }


  public class StationSelectionAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final Sites.HafasObject.SitesObject sites;

    public StationSelectionAdapter(Context context, final Sites.HafasObject.SitesObject sites) {
      super(context, R.layout.main, sites.toStringArray());
      this.context = context;
      this.sites = sites;

      getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          listener.onSiteSelected(sites.Site[position].Name ,sites.Site[position].Number);
        }
      });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

      View rowView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

      TextView textView = (TextView) rowView.findViewById(android.R.id.text1);
      textView.setText(sites.Site[position].Name);
      textView.setTextColor(Color.BLACK);


      return rowView;
    }

  }

}
