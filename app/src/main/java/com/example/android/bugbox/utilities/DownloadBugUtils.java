package com.example.android.bugbox.utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.UserManager;
import android.util.Log;
import android.widget.Toast;

import com.example.android.bugbox.BugsActivity;
import com.example.android.bugbox.BuildConfig;
import com.example.android.bugbox.MyBugsFragment;
import com.example.android.bugbox.R;
import com.example.android.bugbox.background.BugDownloadedBroadcastReceiver;
import com.example.android.bugbox.model.Bug;
import com.example.android.bugbox.model.Bug3D;
import com.example.android.bugbox.network.GetDataService;
import com.example.android.bugbox.network.RetrofitClientInstance;
import com.google.android.gms.common.Feature;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadBugUtils {

    private static final String POLY_API_KEY = BuildConfig.POLY_API_KEY;

    //download bug and insert into db
    public static void downloadBug(final Context context, String polyAssetId) {
        Log.d("SERVICE", "downloadBug called");

        //Create handle for the RetrofitInstance interface
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Bug3D> call = service.getBug3D(polyAssetId, POLY_API_KEY);
        call.enqueue(new Callback<Bug3D>() {
            @Override
            public void onResponse(Call<Bug3D> call, Response<Bug3D> response) {
                //save downloaded bug to db
                Bug3D bug = response.body();
                if (bug != null) {
                    ContentProviderUtils.insertBug(bug, context);//asynctask?
                }else{
                    Toast.makeText(context, R.string.bug_null_text, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Bug3D> call, Throwable t) {
                Toast.makeText(context, R.string.bug_download_error, Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }

        });
    }

}
