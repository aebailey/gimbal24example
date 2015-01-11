package baileyae.gimbal24example;

import android.app.Application;
import android.location.Location;


public class MyApp extends Application {

    private Location mLastLocation;

    public Location getmLastLocation(){
        return mLastLocation;
    }
    public void setState(Location l){
        mLastLocation = l;
    }

}
