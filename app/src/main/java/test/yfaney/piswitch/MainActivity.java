package test.yfaney.piswitch;

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
        mOnOff = false;
    }

    @Override
    protected void onResume(){
        super.onResume();
        mOnOff = false;
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

    }

    public boolean getPinStatus(){
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://" + mEditUrl.getText().toString() + "/api/get/4");
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
}
