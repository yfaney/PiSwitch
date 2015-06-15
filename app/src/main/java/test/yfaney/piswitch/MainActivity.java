package test.yfaney.piswitch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity {
    final static String TAG = "PiSwitchMain";
    final static String PREFERENCES_APPLICATION = "APPLICATION_PREFERENCES";
    final static String PREF_KEY_SERVER_ADDRESS = "PREF_KEY_SERVER_ADDRESS";
    final static String INIT_SERVER_ADDRESS = "192.168.0.15";

    SharedPreferences pref;
    ImageButton mButton;
    boolean mOnOff;
//    EditText mEditUrl;
    String mAddress;
    SwitchController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (ImageButton)findViewById(R.id.imgBtnOnOff);
        mButton.setBackgroundColor(0x00FFFFFF);
//        mEditUrl = (EditText)findViewById(R.id.editTextUrl);
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        mAddress = pref.getString(PREF_KEY_SERVER_ADDRESS, INIT_SERVER_ADDRESS);
        mController = SwitchController.getInstance();
//        if ("".equals(address)) {
//            mEditUrl.setText(INIT_SERVER_ADDRESS);
//            SharedPreferences.Editor editor = pref.edit();
//            editor.putString(PREF_KEY_SERVER_ADDRESS, INIT_SERVER_ADDRESS);
//            editor.apply();
//        }else{
//            mEditUrl.setText(address);
//        }
        mOnOff = false;
    }

    @Override
    protected void onResume(){
        super.onResume();
        new GetPinStatusTask().execute();
    }

//    @Override
//    protected void onPause(){
//        super.onPause();
//    }
//
//    @Override
//    protected void onDestroy(){
//        super.onDestroy();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onoffClicked(View view){
        if(mOnOff){
            new OnOffCallTask().execute(false);
        }else{
            new OnOffCallTask().execute(true);
        }
    }

    private void changeButtonImage(boolean on){
        if(on){
            mButton.setImageResource(R.drawable.poweron);
        }else{
            mButton.setImageResource(R.drawable.poweroff);
        }
    }

    class GetPinStatusTask extends AsyncTask<Void, Process, Boolean>{
        @Override
        protected Boolean doInBackground(Void... params) {
//            String address = mEditUrl.getText().toString();
            return mController.callHttp("http://" + mAddress + "/api/get/4", "output");
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
//            String address = mEditUrl.getText().toString();
            if(params[0]){
                return mController.callHttp("http://" + mAddress + "/api/4/on", "result");
            }else{
                return mController.callHttp("http://" + mAddress + "/api/4/off", "result");
            }
        }

        @Override
        public void onPostExecute(Boolean success){
            if(success){
                mOnOff = !mOnOff;
                changeButtonImage(mOnOff);
            }
        }
    }
}
