package com.farukydnn.lastearthquakes.adapters;


import com.farukydnn.lastearthquakes.interfaces.EarthquakeService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitAdapter {

    private static final String BASE_URL = "https://tercihapp.herokuapp.com/";
    private static Retrofit retrofit = null;


    private static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    public static EarthquakeService getEarthquakeService() {
        return getRetrofitInstance().create(EarthquakeService.class);
    }

}