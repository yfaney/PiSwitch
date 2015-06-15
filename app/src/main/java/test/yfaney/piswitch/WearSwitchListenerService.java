package test.yfaney.piswitch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class WearSwitchListenerService extends WearableListenerService {
    private final static String TAG = "WearSwitchEvent";
    final static String WEAR_EVENT_KEY = "com.yfaney.wear.event";
    final static String PREFERENCES_APPLICATION = "APPLICATION_PREFERENCES";
    final static String PREF_KEY_SERVER_ADDRESS = "PREF_KEY_SERVER_ADDRESS";

    SwitchController mController;
    private String mAddress;

    @Override
    public void onCreate(){
        super.onCreate();
        mController = SwitchController.getInstance();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        mAddress = pref.getString(PREF_KEY_SERVER_ADDRESS, "");
        Log.d(TAG,"=================Service Started=================");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onDataChanged: " + dataEvents + " for " + getPackageName());
        }
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if(item.getUri().getPath().equals("/switch")){
                    DataMap map = DataMapItem.fromDataItem(item).getDataMap();
                    String msg = map.getString(WEAR_EVENT_KEY);
                    if(msg.equals("SWITCH_ON")){
                        new OnOffCallTask().execute(true);
                    }else if(msg.equals("SWITCH_OFF")){
                        new OnOffCallTask().execute(false);
                    }
                }
            }
        }
        dataEvents.close();
    }
    class OnOffCallTask extends AsyncTask<Boolean, Process, Boolean> {
        @Override
        protected Boolean doInBackground(Boolean... params) {
            String address = mAddress;
            if(params[0]){
                return mController.callHttp("http://" + address + "/api/4/on", "result");
            }else{
                return mController.callHttp("http://" + address + "/api/4/off", "result");
            }
        }

        @Override
        public void onPostExecute(Boolean success){
            if(success){
            }
        }
    }

}
