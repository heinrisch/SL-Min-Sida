package com.heinrisch.minsida.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        public Date timeTableDate;

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

        public long getTimeTableDate() {
          if (timeTableDate == null) {
            try {
              timeTableDate = SLDateFormat.parse(TimeTabledDateTime);
              //Skip seconds becuase reseplanerare does not consider them
              Calendar c = Calendar.getInstance();
              c.setTime(timeTableDate);
              //However, they to round their numbers
              if(c.get(Calendar.SECOND) > 30){
                c.add(Calendar.MINUTE, 1);
              }
              c.set(Calendar.SECOND, 0);
              timeTableDate = c.getTime();
            } catch (ParseException e) {
              e.printStackTrace();
            }
          }
          return timeTableDate.getTime();
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
