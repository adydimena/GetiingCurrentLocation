package com.example.ady.getiingcurrentlocation;


import com.example.ady.getiingcurrentlocation.locationpojo.Locationgetter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Ady on 11/18/2017.
 */


public class RetrofitHelper {

    public static final String BASE_URL = "https://maps.googleapis.com/";

    //    build the retrofit object to be used
    public static Retrofit create() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;

    }

    //call the interface to get the response
    public static Call<Locationgetter> getmyzipLocation(String zip){
        Retrofit retrofit = create();
        RetrofitService service = retrofit.create(RetrofitService.class);
        return service.getLocation(zip);
    }


    //    create an interface for http verbs
    interface RetrofitService {

        @GET("maps/api/geocode/json")
        Call<Locationgetter> getLocation(@Query("address")String zip);
        //Call<Locationgetter> getLocation();
    }


}
