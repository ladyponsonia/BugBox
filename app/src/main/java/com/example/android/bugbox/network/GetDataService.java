package com.example.android.bugbox.network;

import com.example.android.bugbox.model.Bug3D;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GetDataService {
    //https://poly.googleapis.com/v1/assets/ASSET_ID_HERE?key=YOUR_API_KEY_HERE
    @GET("/v1/assets/{polyAssetID}")
    Call<Bug3D> getBug3D(@Path ("polyAssetID") String polyAssetId, @Query("key") String polyKey);
}
