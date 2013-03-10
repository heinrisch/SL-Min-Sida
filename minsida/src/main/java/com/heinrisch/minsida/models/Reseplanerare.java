package com.heinrisch.minsida.models;

/**
 * User: henrik
 * Date: 3/9/13
 * Copyright (c) 2013 SBLA
 */
public class Reseplanerare {
  public HafasResponseObject HafasResponse;

  public class HafasResponseObject {
    public TripObject[] Trip;

    public class TripObject {
      public SummaryObject Summary;
      public SummaryObject[] SubTrip;
    }
  }
}


