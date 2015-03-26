package android.iiitb.org.usermod;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;

/**
 * Created by SatishPK on 3/25/2015.
 */
public class DriverLocation  extends Service {


    public static final String BROADCAST_ACTION  = "MY_ACTION";
    Location location; // location
    private double latitude; // latitude
    private double longitude; // longitude
    Intent intent;
    private final Handler handler = new Handler();
    String response = "";

    public DriverLocation() {



    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public Location getLocation(){

        /*ParseQuery<ParseObject> query = ParseQuery.getQuery("vehiclelocation");
        List<ParseObject> vehicleloc;
        ParseObject loc = new ParseObject("vehiclelocation");

        query.whereEqualTo("vehicleid",1);
        try {
            vehicleloc = query.find();
            loc = vehicleloc.get(0);
            latitude= loc.getDouble("latitude");
            longitude = loc.getDouble("longitude");



        } catch (ParseException e) {
            e.printStackTrace();
        }
        */

        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("vehicleid",1);

        ParseCloud.callFunctionInBackground("getDriverLocation",params,new FunctionCallback<String>() {

            public void done(String value, ParseException e) {
                if (e == null) {
                    response = value;
                    Log.d("MyApp",response);
                    String[] loc = response.split(":");
                    latitude = Double.parseDouble(loc[1]);
                    longitude = Double.parseDouble(loc[2]);
                    //Log.d("MyApp",loc[0]);
                    //Log.d("MyApp",loc[1]);
                    //Log.d("MyApp",loc[2]);
                } else {
                    Log.d("MyApp","Unable Fetch Driverloc details");
                    response = "Error : "+e.getMessage().toString();

                }
            }
        });

        return location;


    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyApp", "In service oncreate");
        intent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        getLocation();
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 10000); // 10 second
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            Log.d("MyApp","In service run");

            Bundle loc = new Bundle();
            loc.putDouble("LATITUDE",latitude);
            loc.putDouble("LONGITUDE",longitude);
            intent.setAction(BROADCAST_ACTION);

            intent.putExtra("LOC",loc);

            sendBroadcast(intent);

            //Call getLocation again to fetch next location update, thus, repeatedly calling getLocation
            getLocation();
            handler.postDelayed(this, 10000); // 10 seconds
        }
    };
}
