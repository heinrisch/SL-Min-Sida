package com.heinrisch.minsida;

import com.google.gson.*;
import com.heinrisch.minsida.models.Reseplanerare;
import com.heinrisch.minsida.models.SummaryObject;
import retrofit.http.*;

import java.lang.reflect.Type;

/**
 * User: henrik
 * Date: 3/9/13
 * Copyright (c) SBLA
 */
public class ReseplanerareFetcher {
  public static final String API_URL = "https://api.trafiklab.se/sl";

  private static AsyncService service;

  private static AsyncService getHanlder() {
    if (service == null) {
      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.registerTypeAdapter(SummaryObject[].class, new SummaryObjectFixer());
      //gsonBuilder.registerTypeAdapter(Reseplanerare.HafasResponseObject.TripObject[].class, new TripObjectFixer());

      GsonConverter converter = new GsonConverter(gsonBuilder.create());
      RestAdapter restAdapter = new RestAdapter.Builder()
              .setServer(API_URL)
              .setConverter(converter)
              .build();

      service = restAdapter.create(AsyncService.class);
    }

    return service;
  }

  public static void getReseplanerare(int startSiteId, int endSiteId, String time, Callback<Reseplanerare> callback) {
    getHanlder().getReseplanerare(startSiteId, endSiteId, time, APIKEY.API_KEY_RESEPLANERARE, callback);
  }

  public interface AsyncService {
    @GET("/reseplanerare.json")
    void getReseplanerare(@Name("S") int startSiteId, @Name("Z") int endSiteId, @Name("Time") String time, @Name("key") String key, Callback<Reseplanerare> callback);
  }

  static class TripObjectFixer implements JsonDeserializer<Reseplanerare.HafasResponseObject.TripObject[]> {
    @Override
    public Reseplanerare.HafasResponseObject.TripObject[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if (json instanceof JsonArray) {
        return new Gson().fromJson(json, Reseplanerare.HafasResponseObject.TripObject[].class);
      }

      if (json == null) {
        return new Reseplanerare.HafasResponseObject.TripObject[]{};
      }
      Reseplanerare.HafasResponseObject.TripObject child = context.deserialize(json, Reseplanerare.HafasResponseObject.TripObject.class);
      return new Reseplanerare.HafasResponseObject.TripObject[]{child};
    }
  }

  static class SummaryObjectFixer implements JsonDeserializer<SummaryObject[]> {
    @Override
    public SummaryObject[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if (json instanceof JsonArray) {
        return new Gson().fromJson(json, SummaryObject[].class);
      }

      if (json == null) {
        return new SummaryObject[]{};
      }
      SummaryObject child = context.deserialize(json, SummaryObject.class);
      return new SummaryObject[]{child};
    }
  }

}

