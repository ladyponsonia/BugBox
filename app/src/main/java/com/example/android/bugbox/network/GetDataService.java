package com.example.android.bugbox.network;

import com.example.android.bugbox.model.Bug3D;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataService {
    //https://poly.googleapis.com/v1/assets/ASSET_ID_HERE?key=YOUR_API_KEY_HERE
    @GET("/v1/assets/4yufxgZ1QQ2?***REMOVED***")
    Call<Bug3D> getBug3D();
}
