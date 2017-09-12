package com.farukydnn.lastearthquakes.interfaces;


import com.farukydnn.lastearthquakes.model.Earthquake;
import retrofit2.Call;
import retrofit2.http.GET;

public interface EarthquakeService {

    @GET("depremler")
    Call<Earthquake> getLastEarthquakes();

    @GET("onemliDepremler")
    Call<Earthquake> getImportantEarthquakes();
}
