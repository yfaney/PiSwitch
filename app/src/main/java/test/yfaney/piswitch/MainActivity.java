package test.yfaney.piswitch;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    ImageButton mButton;
    boolean mOnOff;
    EditText mEditUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (ImageButton)findViewById(R.id.imgBtnOnOff);
        mEditUrl = (EditText)findViewById(R.id.editTextUrl);
        if(mEditUrl.equals("")){
            mEditUrl.setText("192.168.0.6");
        }
        mOnOff = false;
    }

    @Override
    protected void onResume(){
        super.onResume();
        new AsyncTask<String, Process, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                return callHttpGet(params[0]);
            }

            @Override
            public void onPostExecute(Boolean result){
                mOnOff = result;
                changeButtonImage(mOnOff);
            }
        }.execute("http://" + mEditUrl.getText().toString() + "/api/get/4");
    }

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onoffClicked(View view){
        if(mOnOff){
            new AsyncTask<String, Process, Boolean>() {
                @Override
                protected Boolean doInBackground(String... params) {
                    return callHttpGet(params[0]);
                }

                @Override
                public void onPostExecute(Boolean success){
                    if(success){
                        mOnOff = false;
                        changeButtonImage(false);
                    }
                }
            }.execute("http://" + mEditUrl.getText().toString() + "/api/4/off");
        }else{
            new AsyncTask<String, Process, Boolean>() {
                @Override
                protected Boolean doInBackground(String... params) {
                    return callHttpGet(params[0]);
                }

                @Override
                public void onPostExecute(Boolean success){
                    if(success){
                        mOnOff = true;
                        changeButtonImage(true);
                    }
                }
            }.execute("http://" + mEditUrl.getText().toString() + "/api/4/on");
        }
    }

    public boolean getPinStatus(){
        return callHttpGet("http://" + mEditUrl.getText().toString() + "/api/get/4");
    }

    private boolean callHttpGet(String url){
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response;
        try {
            response = client.execute(request);
            Log.d("Response of GET request", response.toString());
            JSONObject result = new JSONObject(response.toString());
            return result.getBoolean("result");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void changeButtonImage(boolean on){
        if(on){
            mButton.setBackgroundResource(R.drawable.poweron);
        }else{
            mButton.setBackgroundResource(R.drawable.poweroff);
        }
    }
}
