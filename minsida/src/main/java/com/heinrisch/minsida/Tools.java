package com.heinrisch.minsida;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * User: henrik
 * Date: 3/10/13
 * Copyright (c) 2013 SBLA.
 */
public class Tools {

  public static final DateFormat DateFormatterHourMinutes = new SimpleDateFormat("HH:mm");

  public static String getTimeToDepatureString(long now, long depatureTime) {
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
}
