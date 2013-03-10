package com.heinrisch.minsida;

import com.google.gson.*;
import com.heinrisch.minsida.models.DPSDepartures;
import com.heinrisch.minsida.models.Sites;
import retrofit.http.*;

import java.lang.reflect.Type;

/**
 * User: henrik
 * Date: 3/9/13
 * Copyright (c) SBLA
 */
public class RESTHandler {
  public static final String API_URL = "https://api.trafiklab.se/sl/realtid";

  private static AsyncService service;

  private static AsyncService getHanlder() {
    if (service == null) {
      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.registerTypeAdapter(DPSDepartures.DPSObject.BusesObject.DpsBusObject[].class, new BusesObjectFixer());
      gsonBuilder.registerTypeAdapter(Sites.HafasObject.SitesObject.SiteObject[].class, new SitesObjectFixer());

      GsonConverter converter = new GsonConverter(gsonBuilder.create());
      RestAdapter restAdapter = new RestAdapter.Builder()
              .setServer(API_URL)
              .setConverter(converter)
              .build();

      service = restAdapter.create(AsyncService.class);
    }

    return service;
  }

  public static void getDPSDepatures(int siteId, int timeWindow, Callback<DPSDepartures> callback) {
    getHanlder().getDpsDepatures(siteId, timeWindow, APIKEY.API_KEY_REAL_TIME, callback);
  }

  public static void getSites(String searchTerm, Callback<Sites> callback) {
    getHanlder().getSite(searchTerm, APIKEY.API_KEY_REAL_TIME, callback);
  }

  public interface AsyncService {
    @GET("/GetSite.json")
    void getSite(@Name("stationSearch") String search, @Name("key") String key, Callback<Sites> callback);

    @GET("/GetDpsDepartures.json")
    void getDpsDepatures(@Name("siteId") int siteId, @Name("timeWindow") int timeWindow, @Name("key") String key, Callback<DPSDepartures> callback);
  }

  static class BusesObjectFixer implements JsonDeserializer<DPSDepartures.DPSObject.BusesObject.DpsBusObject[]> {
    @Override
    public DPSDepartures.DPSObject.BusesObject.DpsBusObject[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if (json instanceof JsonArray) {
        return new Gson().fromJson(json, DPSDepartures.DPSObject.BusesObject.DpsBusObject[].class);
      }

      if (json == null) {
        return new DPSDepartures.DPSObject.BusesObject.DpsBusObject[]{};
      }
      DPSDepartures.DPSObject.BusesObject.DpsBusObject child = context.deserialize(json, DPSDepartures.DPSObject.BusesObject.DpsBusObject.class);
      return new DPSDepartures.DPSObject.BusesObject.DpsBusObject[]{child};
    }
  }

  static class SitesObjectFixer implements JsonDeserializer<Sites.HafasObject.SitesObject.SiteObject[]> {
    @Override
    public Sites.HafasObject.SitesObject.SiteObject[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      if (json instanceof JsonArray) {
        return new Gson().fromJson(json, Sites.HafasObject.SitesObject.SiteObject[].class);
      }

      if (json == null) {
        return new Sites.HafasObject.SitesObject.SiteObject[]{};
      }

      Sites.HafasObject.SitesObject.SiteObject child = context.deserialize(json, Sites.HafasObject.SitesObject.SiteObject.class);
      return new Sites.HafasObject.SitesObject.SiteObject[]{child};
    }
  }
}

