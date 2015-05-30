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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


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
        mEditUrl.setText("192.168.0.6");
        mOnOff = false;
    }

    @Override
    protected void onResume(){
        super.onResume();
        new GetPinStatusTask().execute();
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
            new OnOffCallTask().execute(false);
        }else{
            new OnOffCallTask().execute(true);
        }
    }

    private boolean callHttpGet(String url, String jsonKey){
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        HttpResponse response;
        try {
            response = client.execute(request);
            String respString = getStringFromInputStream(response.getEntity().getContent());
            Log.d("Response of GET request", respString);
            JSONObject result = new JSONObject(respString);
            return result.getBoolean(jsonKey);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    private void changeButtonImage(boolean on){
        if(on){
            mButton.setBackgroundResource(R.drawable.poweron);
        }else{
            mButton.setBackgroundResource(R.drawable.poweroff);
        }
    }

    class GetPinStatusTask extends AsyncTask<Void, Process, Boolean>{
        @Override
        protected Boolean doInBackground(Void... params) {
            return callHttpGet("http://" + mEditUrl.getText().toString() + "/api/get/4", "output");
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
                return callHttpGet("http://" + mEditUrl.getText().toString() + "/api/4/on", "result");
            }else{
                return callHttpGet("http://" + mEditUrl.getText().toString() + "/api/4/off", "result");
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
