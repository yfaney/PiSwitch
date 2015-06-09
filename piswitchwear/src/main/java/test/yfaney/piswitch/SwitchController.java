package test.yfaney.piswitch;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Calls the PiSwitch server
 * Created by Younghwan on 6/7/2015.
 */
public class SwitchController {

    /**
     * Singleton instance
     */
    private volatile static SwitchController mSwitchController;
    private HttpClient mClient;

    private SwitchController(){
        mClient = new DefaultHttpClient();
    }

    public static SwitchController getInstance(){
        if(mSwitchController == null){
            synchronized (SwitchController.class){
                if(mSwitchController == null){
                    mSwitchController = new SwitchController();
                }
            }
        }
        return mSwitchController;
    }

    public boolean callHttp(String url, String jsonKey){
        HttpGet request = new HttpGet(url);
        HttpResponse response;
        try {
            response = mClient.execute(request);
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
}
