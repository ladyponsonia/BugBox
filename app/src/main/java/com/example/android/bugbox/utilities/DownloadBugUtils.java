package com.example.android.bugbox.utilities;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.android.bugbox.BugsActivity;
import com.example.android.bugbox.BuildConfig;
import com.example.android.bugbox.MyBugsFragment;
import com.example.android.bugbox.model.Bug3D;
import com.example.android.bugbox.network.GetDataService;
import com.example.android.bugbox.network.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadBugUtils {

    private static final String POLY_API_KEY = BuildConfig.POLY_API_KEY;

    //download bug
    public static void downloadBug(final Context context){
        Log.d("SERVICE", "downloadBug called");

        //Create handle for the RetrofitInstance interface
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Bug3D> call = service.getBug3D("4K7V5f9ntfu", POLY_API_KEY);

        call.enqueue(new Callback<Bug3D>() {
            @Override
            public void onResponse(Call<Bug3D> call, Response<Bug3D> response) {
                //save downloaded bug to db
                Bug3D bug = response.body();
                ContentProviderUtils.insertBug(bug, context);

                //download and save 3D asset

                //save file locations to db

                //refresh myBugs recycler view
               BugsActivity.refreshMyBugs();

            }

            @Override
            public void onFailure(Call<Bug3D> call, Throwable t) {
                Toast.makeText(context, "Connection error", Toast.LENGTH_LONG).show();
                t.printStackTrace ();
            }

        });

    }
}
