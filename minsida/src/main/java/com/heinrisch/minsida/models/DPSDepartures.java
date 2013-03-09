package com.heinrisch.minsida.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: henrik
 * Date: 3/9/13
 * Copyright (c) 2013 SBLA
 */
public class DPSDepartures {
  public static final DateFormat SLDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  public DPSObject DPS;

  public class DPSObject {
    public BusesObject Buses;

    public class BusesObject {
      public DpsBusObject[] DpsBus;

      public class DpsBusObject implements Comparable<DpsBusObject> {
        public int SiteId;
        public int StopAreaNumber;
        public String TransportMode;
        public String StopAreaName;
        public int LineNumber;
        public String Destination;
        public String TimeTabledDateTime;
        public String ExpectedDateTime;
        public String DisplayTime;

        public Date expectedDate;

        public long getExpectedDate() {
          if (expectedDate == null) {
            try {
              expectedDate = SLDateFormat.parse(ExpectedDateTime);
            } catch (ParseException e) {
              e.printStackTrace();
            }
          }
          return expectedDate.getTime();
        }

        @Override
        public int compareTo(DpsBusObject dpsBusObject) {
          if (dpsBusObject.getExpectedDate() > getExpectedDate()) {
            return -1;
          } else if (dpsBusObject.getExpectedDate() < getExpectedDate()) {
            return 1;
          }
          return 0;
        }
      }
    }
  }
}
