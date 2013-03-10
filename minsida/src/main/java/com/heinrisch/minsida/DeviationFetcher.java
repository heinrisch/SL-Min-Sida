package com.heinrisch.minsida;

import com.google.gson.*;
import com.heinrisch.minsida.models.Deviation;
import com.heinrisch.minsida.models.Reseplanerare;
import com.heinrisch.minsida.models.SummaryObject;
import retrofit.http.*;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: henrik
 * Date: 3/9/13
 * Copyright (c) SBLA
 */
public class DeviationFetcher {
  public static final String API_URL = "https://api.trafiklab.se/sl/storningsinfo";
  public static final DateFormat SLDateFormat = new SimpleDateFormat("yyyy-MM-dd");

  private static AsyncService service;

  private static AsyncService getHanlder() {
    if (service == null) {
      GsonBuilder gsonBuilder = new GsonBuilder();
      GsonConverter converter = new GsonConverter(gsonBuilder.create());
      RestAdapter restAdapter = new RestAdapter.Builder()
              .setServer(API_URL)
              .setConverter(converter)
              .build();

      service = restAdapter.create(AsyncService.class);
    }

    return service;
  }

  public static void getDeviations(int siteId, Callback<Deviation> callback) {
    String today = SLDateFormat.format(new Date());
    getHanlder().getReseplanerare(siteId, today, today, APIKEY.API_KEY_DEVIATIONS, callback);
  }

  public interface AsyncService {
    @GET("/GetDeviations.json")
    void getReseplanerare(@Name("stopArea") int siteId, @Name("fromDate") String from, @Name("toDate") String to, @Name("key") String key, Callback<Deviation> callback);
  }

}

