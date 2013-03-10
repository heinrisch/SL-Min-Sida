package com.heinrisch.minsida.models;

import android.widget.TextView;

/**
 * User: henrik
 * Date: 3/10/13
 * Copyright (c) 2013 SBLA.
 */
public class Time {

  public Time(TextView view, long time) {
    this.view = view;
    this.time = time;
  }

  public TextView view;
  public long time;
}
