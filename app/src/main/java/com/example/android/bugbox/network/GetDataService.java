package com.example.android.bugbox.network;

import com.example.android.bugbox.model.Bug3D;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDataService {
    //https://poly.googleapis.com/v1/assets/ASSET_ID_HERE?key=YOUR_API_KEY_HERE
    @GET("/v1/assets/{polyAssetID}")
    Call<Bug3D> getBug3D(@Path ("polyAssetID") String polyAssetId, @Query("key") String polyKey);

    //https://poly.googleapis.com/downloads/4yufxgZ1QQ2/bwAW7sQLjH3/Mesh_Beetle.obj
    @GET("/downloads/{assetPath}")
    Call<String> getBugObj(@Path ("assetPath") String fileUrl);

    @GET("/downloads/{assetPath}")
    Call<String> getBugMtl(@Path ("assetPath") String fileUrl);

    @GET("/downloads/{assetPath}")
    Call<String> getBugPng(@Path ("assetPath") String fileUrl);
}
