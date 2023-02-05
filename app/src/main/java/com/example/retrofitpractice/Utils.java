package com.example.retrofitpractice;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ProgressBar;

import androidx.core.app.ActivityCompat;

import com.example.retrofitpractice.R;
import com.example.retrofitpractice.WebResponseWeather;
import com.example.retrofitpractice.WebService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utils {

    static ProgressDialog pd;

    public static WebService getAPI(Context context){
        String uriAddress = context.getResources().getString(R.string.api_url);
        Gson gson = new GsonBuilder().setLenient().create();

        return (WebService) new Retrofit.Builder()
                .baseUrl(uriAddress)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(new OkHttpClient()
                        .newBuilder()
                        .readTimeout(20, TimeUnit.SECONDS)
                        .connectTimeout(20, TimeUnit.SECONDS)
                        .build())
                .build().create(WebService.class);
    }

    public static boolean isConnectedToInternet(Context _context, boolean ShowConnectionDialog){
        Boolean isInternetPresent = Boolean.FALSE;
        if(_context != null){
            ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivity != null){
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if(info != null){
                    isInternetPresent = Boolean.TRUE;
                }
            }
        }
        //if internet is not present and the passed context is an instance of specified class (Activity) and no Dialog is showing
        if((_context instanceof Activity) && !isInternetPresent && ShowConnectionDialog){
            ShowAlertDialog(_context, "No Internet Connection", "You don't have Internet Connection");
        }
        //id internet is present then it will return true
        return isInternetPresent;
    }

    private static void ShowAlertDialog(Context _context, String title, String message) {
        if((_context instanceof Activity)){
            AlertDialog alertDialog = new AlertDialog.Builder(_context).create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            if(!((Activity) _context).isFinishing()){
                alertDialog.show();
            }
        }
    }
    public static void ShowDialog(Context context){
        pd=new ProgressDialog(context);
        pd.setMessage("Wait a moment");
        pd.setCancelable(false);
        pd.show();
    }
    public static void hideDialog(){
        pd.dismiss();
    }
}
