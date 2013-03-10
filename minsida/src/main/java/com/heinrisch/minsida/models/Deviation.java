package com.heinrisch.minsida.models;

import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * User: henrik
 * Date: 3/10/13
 * Copyright (c) 2013 SBLA.
 */
public class Deviation {
  public static final DateFormat SLDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  public DeviationObject GetDeviationsResponse;

  public class DeviationObject{
    public DeviationResultObject GetDeviationsResult;

    public class DeviationResultObject{
      public AWCFDeviationObject aWCFDeviation;

      public class AWCFDeviationObject{
        public String aHeader;
        public String aDetails;
      }

    }

  }
}
