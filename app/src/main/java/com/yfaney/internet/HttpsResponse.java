package com.yfaney.internet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.security.cert.Certificate;

import javax.net.ssl.HttpsURLConnection;

/**
 * <p>Handles HTTPS response at ease.</p>
 * <p>Created by Younghwan on 8/4/2014.</p>
 * @version 2014.08.11 - NULL response
 */
public class HttpsResponse {
    /**
     * The code when the server returns success.
     */
    public final static int CODE_OK = 200;
    /**
     * The code when the url has an incorrect request.
     */
    public final static int CODE_BADREQUEST = 400;
    /**
     * The code when the request does not have the authorization.
     */
    public final static int CODE_UNAUTHORIZED = 401;
    /**
     * The code when the request is denied.
     */
    public final static int CODE_FORBIDDEN = 403;
    /**
     * The code when the the server cannot find the request.<br/>
     * Rarely gets this message. Generally gets FileIOException rather than this.
     */
    public final static int CODE_NOTFOUND = 404;
    /**
     * The code when the server gets an error during processing the request.
     */
    public final static int CODE_INTERVAL_SERVER_ERROR = 500;
    /**
     * The internal code of the null pointer.
     */
    public final static int CODE_NULLPOINTER = 0;

    /**
     * Saves the response code.
     */
    private int mResponseCode;
    /**
     * Saves the response string.
     */
    private String mResultString;
    /**
     * Saves the certificates.
     */
    private Certificate[] mCertificates;

    /**
     * Constructor. Makes a NULL HttpsResponse
     */
    public HttpsResponse(){
        mResponseCode = CODE_NULLPOINTER;
        mResultString = null;
    }
    /**
     * Constructor. Makes a NULL HttpsResponse
     * @param httpsUrlConnection HttpsURLConnection which will be got the response
     * @see HttpsURLConnection
     */
    public HttpsResponse(HttpsURLConnection httpsUrlConnection){
        try {
            this.mResponseCode = httpsUrlConnection.getResponseCode();
            StringBuffer retVal = new StringBuffer();
            BufferedReader br =
                    new BufferedReader(
                            new InputStreamReader(httpsUrlConnection.getInputStream()));

            String input;
            while ((input = br.readLine()) != null){
                retVal.append(input);
                //System.out.println(input);
            }
            br.close();
            this.mResultString = retVal.toString();
            this.mCertificates = httpsUrlConnection.getServerCertificates();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the response code of http.
     * @return The response code of http
     */
    public int getResponseCode() {
        return mResponseCode;
    }

    /**
     * Retrieves the response string.
     * @return The response string
     */
    public String getResultString() {
        if(mResultString != null){
            return mResultString;
        }else{
            return "HTTPS_RESPONSE_ERROR_NULL_STRING";
        }
    }

    /**
     * Retrieves the certificates of the HTTPS connection.
     * @return The array of the certificte
     * @see Certificate
     */
    public Certificate[] getCertificates() {
        return mCertificates;
    }
}
