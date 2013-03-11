package com.heinrisch.minsida.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.heinrisch.minsida.R;
import com.heinrisch.minsida.Tools;
import com.heinrisch.minsida.models.DPSDepartures;
import com.heinrisch.minsida.models.Reseplanerare;
import com.heinrisch.minsida.models.SummaryObject;
import com.heinrisch.minsida.models.Time;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: henrik
 * Date: 3/10/13
 * Copyright (c) 2013 SBLA
 */
public class TripView extends LinearLayout {

  public TripView(Context context) {
    super(context);
  }

  public TripView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public TripView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  private TextView arrivalTime;
  private Reseplanerare.HafasResponseObject.TripObject trip;
  private List<Time> coolDowns = new ArrayList<Time>();

  public void setTrip(Reseplanerare.HafasResponseObject.TripObject trip, DPSDepartures departures) {
    this.trip = trip;
    LayoutInflater inflater = LayoutInflater.from(getContext());
    inflater.inflate(R.layout.trip_object_view, this);

    SummaryObject firstSubTrip = trip.SubTrip[0];
    int lineNumber = firstSubTrip.Transport.Line;

    if(firstSubTrip.RTUMessages != null){
      TextView rtum = (TextView) findViewById(R.id.rtum);
      rtum.setText(firstSubTrip.RTUMessages.RTUMessage);
      rtum.setVisibility(VISIBLE);
    }

    ((TextView) findViewById(R.id.lineNumber)).setText(String.valueOf(lineNumber));
    ((TextView) findViewById(R.id.destination)).setText(firstSubTrip.Transport.Towards);

    arrivalTime = (TextView) findViewById(R.id.arrival);
    setArrivalString();

    if (departures != null) {
      long plannedTime = firstSubTrip.getDepatureTime();
      for (DPSDepartures.DPSObject.BusesObject.DpsBusObject bus : departures.DPS.Buses.DpsBus) {
        if (bus.LineNumber == lineNumber && bus.getTimeTableDate() == plannedTime) {
          TextView text = (TextView) findViewById(R.id.realtid);
          text.setText(Tools.getTimeToDepatureString(new Date().getTime(), bus.getExpectedDate()));
          coolDowns.add(new Time(text, bus.getExpectedDate()));
          text.setVisibility(View.VISIBLE);
        }
      }
    }

    //addSubtrips(trip, inflater);
  }

  private void addSubtrips(Reseplanerare.HafasResponseObject.TripObject trip, LayoutInflater inflater) {
    LinearLayout subTrips = (LinearLayout) findViewById(R.id.subTripContainer);
    for (SummaryObject subtrip : trip.SubTrip) {
      View view = inflater.inflate(R.layout.trip_object_view_subitem, null);

      ((TextView) view.findViewById(R.id.destination)).setText(subtrip.Transport.Towards);
      ((TextView) view.findViewById(R.id.lineNumber)).setText(String.valueOf(subtrip.Transport.Line));

      ((TextView) view.findViewById(R.id.displayTime)).setText(subtrip.DepartureTime.text + " - " + subtrip.ArrivalTime.text);

      subTrips.addView(view);
    }
  }

  private void setArrivalString() {
    long now = new Date().getTime();
    long depature = trip.Summary.getDepatureTime();
    String depatureString = Tools.getTimeToDepatureString(now, depature);
    arrivalTime.setText(depatureString);

    coolDowns.add(new Time(arrivalTime, depature));
  }

  public List<Time> getTimes() {
    return coolDowns;
  }
}
