package test.yfaney.piswitch;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    final static String TAG = "PiSwitchWearMain";

    final static String WEAR_ADDRESS_KEY = "com.yfaney.wear.address";
    final static String WEAR_EVENT_KEY = "com.yfaney.wear.event";

    private ImageButton mButton;
    private String mAddress = "192.168.0.15";

    boolean mOnOff = false;
    boolean mSynced = false;
    boolean mWiFiAvailable = false;

    SwitchController mController;


    // For accessing Android Wear
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mButton = (ImageButton) stub.findViewById(R.id.imageButton);
                mButton.setBackgroundColor(0x00FFFFFF);
                changeButtonImage(mOnOff);
            }
        });

        // Check if Wi-Fi is available
        WifiManager manager = (WifiManager)getSystemService(Context.WIFI_SERVICE);

        if(manager == null){
            mWiFiAvailable = false;
        }else{
            mWiFiAvailable = manager.isWifiEnabled();
            if(mWiFiAvailable){
                mController = SwitchController.getInstance();
            }
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                        // Request access only to the Wearable API
                .addApi(Wearable.API)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(this, "WiFi:" + mWiFiAvailable, Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
        if(mSynced && mWiFiAvailable){
            new GetPinStatusTask().execute();
        }else{
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    public void onSwitchClicked(View v){
        if(mOnOff){
            // On -> Off
            new OnOffCallTask().execute(false);
        }else{
            // Off -> On
            new OnOffCallTask().execute(true);
        }

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/address") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    mAddress = dataMap.getString(WEAR_ADDRESS_KEY);
                    if(!mSynced){
                        new GetPinStatusTask().execute();
                        mSynced = true;
                    }
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Wear API Connected", Toast.LENGTH_SHORT).show();
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private PendingResult<DataApi.DataItemResult> reqData(String key, String value){
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/switch");
        putDataMapReq.getDataMap().putString(key, value);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        return Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);
    }

    class GetPinStatusTask extends AsyncTask<Void, Process, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            if(mController != null){
                return mController.callHttp("http://" + mAddress + "/api/get/4", "output");
            }else{
                return false;
            }
        }

        @Override
        public void onPostExecute(Boolean result){
            mOnOff = result;
            changeButtonImage(mOnOff);
        }
    }

    class OnOffCallTask extends AsyncTask<Boolean, Process, Boolean>{
        @Override
        protected Boolean doInBackground(Boolean... params) {
            if(params[0]){
                if(mWiFiAvailable){
                    if(mController != null){
                        return mController.callHttp("http://" + mAddress + "/api/4/on", "result");
                    }else{
                        return false;
                    }
                }else{
                    PendingResult<DataApi.DataItemResult> pendingResult =
                            reqData(WEAR_EVENT_KEY, "SWITCH_ON");
                    DataApi.DataItemResult result = pendingResult.await();
                    if(result != null){
                        return true;
                    }else{
                        return false;
                    }
                }
            }else{
                if(mWiFiAvailable){
                    if(mController != null){
                        return mController.callHttp("http://" + mAddress + "/api/4/off", "result");
                    }else{
                        return false;
                    }
                }else{
                    PendingResult<DataApi.DataItemResult> pendingResult =
                            reqData(WEAR_EVENT_KEY, "SWITCH_OFF");
                    DataApi.DataItemResult result = pendingResult.await();
                    if(result != null){
                        return true;
                    }else{
                        return false;
                    }
                }
            }
        }

        @Override
        public void onPostExecute(Boolean success){
            if(success){
                //Toast.makeText(getBaseContext(), "Success!", Toast.LENGTH_SHORT).show();
                mOnOff = !mOnOff;
                changeButtonImage(mOnOff);
            }else{
                //Toast.makeText(getBaseContext(), "Failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void changeButtonImage(boolean on){
        if(on){
            mButton.setImageResource(R.drawable.poweron);
        }else{
            mButton.setImageResource(R.drawable.poweroff);
        }
    }
}
