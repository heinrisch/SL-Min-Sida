package com.heinrisch.minsida.models;

/**
 * User: henrik
 * Date: 3/9/13
 * Copyright (c) 2013 SBLA
 */
public class Sites {
  public HafasObject Hafas;

  public class HafasObject {
    public SitesObject Sites;

    public class SitesObject {
      public SiteObject[] Site;

      public class SiteObject {
        public int Number;
        public String Name;
      }

     public String[] toStringArray(){
       String[] array = new String[Site.length];
       for(int i = 0; i < Site.length; i++){
         array[i] = Site[i].Name;
       }
       return array;
     }
    }
  }
}
