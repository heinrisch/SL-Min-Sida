package com.heinrisch.minsida.models;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * User: henrik
 * Date: 3/10/13
 * Copyright (c) 2013 SBLA.
 */
public class SummaryObject {
  public static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy'T'HH:mm");
  public TextObject Origin;
  public TextObject Destination;
  public String DepartureDate;
  public String ArrivalDate;
  public TextObject DepartureTime;
  public TextObject ArrivalTime;
  public String Changes;
  public String Duration;
  public TransportObject Transport;
  public RTUMessagesObject RTUMessages;

  public long getArrivalTime(){
    return getLongFromString(ArrivalDate + "T" + ArrivalTime.text);
  }

  public long getDepatureTime(){
    return getLongFromString(DepartureDate + "T" + DepartureTime.text);
  }

  private long getLongFromString(String arrival) {
    try {
      return dateFormat.parse(arrival).getTime();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return 0;
  }

  public class TransportObject{
    public String Type;
    public String Name;
    public int Line;
    public String Towards;
  }

  public class TextObject {
    @SerializedName("#text")
    public String text;
  }

  public class RTUMessagesObject{
    public String RTUMessage;
  }
}