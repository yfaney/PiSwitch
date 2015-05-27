package com.yfaney.internet;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.cert.Certificate;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * <p>Makes an HTTPS connection. Also can handle bearer token authentication.</p>
 * <p>Please make sure if you are using "https", not "http",
 * it will occur Class Cast Exception otherwise.</p>
 * <p>Edited and Developed by Faney</p>
 * <p>First Developed by MKyong</p>
 * <p>Reference:
 * <a href="http://www.mkyong.com/java/java-https-client-httpsurlconnection-example/">
 *     http://www.mkyong.com/java/java-https-client-httpsurlconnection-example/
 * </a>
 * </p>
 * @version 2014.08.14 - Added Class Cast Exception(for avoid mistaking with 'http'.
 * @author Faney
 */
// version 2014.08.13 - Reorganizing POST and GET method(With Authentication and Without Auth)
// version 2014.08.11 - Null Response Handling
// version 2014.08.04 - Return Code Handling
// version 2014.07.28 - First Version

public class HttpsClient{
    /**
     * HTTPS URL Connection variable
     */
    private HttpsURLConnection mConnection;
    /**
     * Checks if the HTTPS connection is available or not
     */
    private boolean isAvailable = false;

    /**
     * Constructor
     */
    public HttpsClient(){
        // Null Host Name Verifier
        mConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
        // Create a trust manager that does not validate certificate chains
        // TODO Delete this after getting Proper Certificate
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            mConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            isAvailable = true;
        } catch (Exception e) {
        }
    }

    /**
     * Posts the request
     * @param https_url The URL which will be requested
     * @return The response of the URL
     * @see com.yfaney.internet.HttpsResponse
     */
    public HttpsResponse post(String https_url){
        if(isAvailable){
            URL url;
            try {
                url = new URL(https_url);
                // Open the connection
                mConnection = (HttpsURLConnection)url.openConnection();
                // Content Type as JSON String
                mConnection.setRequestProperty("Content-Type", "application/json");
                mConnection.setRequestProperty("charset", "utf-8");
                mConnection.setUseCaches (false);
                // Request method as POST
                mConnection.setDoOutput(true);
                mConnection.setChunkedStreamingMode(0);
                return new HttpsResponse(mConnection);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (ClassCastException e){
                e.printStackTrace();
                System.out.println("Class Cast Exception : " + e.getMessage());
                System.out.println("Please make sure if you are using http\"s\" correctly or not.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("HttpsClient-Post : " + e.getMessage());
                // Return Null HttpsResponse
                return new HttpsResponse();
            }
        }
        return null;
    }

    /**
     * Posts the request
     * @param https_url The URL which will be requested
     * @param body      The body which will be posted. Generally parameters.
     * @return The response of the URL
     * @see com.yfaney.internet.HttpsResponse
     */
    public HttpsResponse post(String https_url, String body){
        if(isAvailable){
            URL url;
            try {
                url = new URL(https_url);
                // Open the connection
                mConnection = (HttpsURLConnection)url.openConnection();
                // Content Type as JSON String
                mConnection.setRequestProperty("Content-Type", "application/json");
                mConnection.setRequestProperty("charset", "utf-8");
                mConnection.setRequestProperty("Content-Length", Integer.toString(body.getBytes().length));
                mConnection.setUseCaches (false);
                // Request method as POST
                mConnection.setDoOutput(true);
                mConnection.setChunkedStreamingMode(0);
                // Send the body content into the server
                DataOutputStream wr = new DataOutputStream(mConnection.getOutputStream());
                wr.writeBytes(body);
                wr.flush();
                wr.close();
                return new HttpsResponse(mConnection);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (ClassCastException e){
                e.printStackTrace();
                System.out.println("Class Cast Exception : " + e.getMessage());
                System.out.println("Please make sure if you are using http\"s\" correctly or not.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("HttpsClient-Post : " + e.getMessage());
                // Return Null HttpsResponse
                return new HttpsResponse();
            }
        }
        return null;
    }

    /**
     * Posts the request with an authentication
     * @param https_url The URL which will be requested
     * @param token     The security token which is given by the server
     * @return The response of the URL
     * @see com.yfaney.internet.HttpsResponse
     */
    public HttpsResponse post_auth(String https_url, String token){
        if(isAvailable){
            URL url;
            try {
                url = new URL(https_url);
                // Open the connection
                mConnection = (HttpsURLConnection)url.openConnection();
                // Set Authentication with the given token
                mConnection.setRequestProperty("Authorization", "Bearer " + token);
                // Content Type as JSON String
                mConnection.setRequestProperty("Content-Type", "application/json");
                mConnection.setRequestProperty("charset", "utf-8");
                mConnection.setUseCaches (false);
                // Request method as POST
                mConnection.setDoOutput(true);
                mConnection.setChunkedStreamingMode(0);
                return new HttpsResponse(mConnection);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (ClassCastException e){
                e.printStackTrace();
                System.out.println("Class Cast Exception : " + e.getMessage());
                System.out.println("Please make sure if you are using http\"s\" correctly or not.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("HttpsClient-Post : " + e.getMessage());
                // Return Null HttpsResponse
                return new HttpsResponse();
            }
        }
        return null;
    }

    /**
     * Posts the request with an authentication
     * @param https_url The URL which will be requested
     * @param body      The body which will be posted. Generally parameters.
     * @param token     The security token which is given by the server
     * @return The response of the URL
     * @see com.yfaney.internet.HttpsResponse
     */
    public HttpsResponse post_auth(String https_url, String body, String token){
        if(isAvailable){
            URL url;
            try {
                url = new URL(https_url);
                // Open the connection
                mConnection = (HttpsURLConnection)url.openConnection();
                // Set Authentication with the given token
                mConnection.setRequestProperty("Authorization", "Bearer " + token);
                // Content Type as JSON String
                mConnection.setRequestProperty("Content-Type", "text/json");
                mConnection.setRequestProperty("charset", "utf-8");
                mConnection.setRequestProperty("Content-Length", Integer.toString(body.getBytes().length));
                mConnection.setUseCaches (false);
                // Request method as POST
                mConnection.setDoOutput(true);
                //mConnection.setChunkedStreamingMode(0);
                // Send the body content into the server
                DataOutputStream wr = new DataOutputStream(mConnection.getOutputStream());
                wr.writeBytes(body);
                wr.flush();
                wr.close();
                return new HttpsResponse(mConnection);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (ClassCastException e){
                e.printStackTrace();
                System.out.println("Class Cast Exception : " + e.getMessage());
                System.out.println("Please make sure if you are using http\"s\" correctly or not.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("HttpsClient-Post : " + e.getMessage());
                // Return Null HttpsResponse
                return new HttpsResponse();
            }
        }
        return null;
    }

    /**
     * Sends the request using GET method
     * @param https_url The URL which will be requested
     * @return The response of the URL
     * @see com.yfaney.internet.HttpsResponse
     */
    public HttpsResponse get(String https_url){
        if(isAvailable){
            URL url;
            try {
                url = new URL(https_url);
                // Open the connection
                mConnection = (HttpsURLConnection)url.openConnection();
                // Request method as GET
                mConnection.setRequestMethod("GET");
                return new HttpsResponse(mConnection);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (ClassCastException e){
                e.printStackTrace();
                System.out.println("Class Cast Exception : " + e.getMessage());
                System.out.println("Please make sure if you are using http\"s\" correctly or not.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("HttpsClient-Get : " + e.getMessage());
                // Return Null HttpsResponse
                return new HttpsResponse();
            }
        }
        return null;
    }

    /**
     * Sends the request with an authentication using GET method
     * @param https_url The URL which will be requested
     * @param token     The security token which is given by the server
     * @return The response of the URL
     * @see com.yfaney.internet.HttpsResponse
     */
    public HttpsResponse get_auth(String https_url, String token){
        if(isAvailable){
            URL url;
            try {
                url = new URL(https_url);
                // Open the connection
                mConnection = (HttpsURLConnection)url.openConnection();
                // Request method as GET
                mConnection.setRequestMethod("GET");
                // Set Authentication with the given token
                mConnection.addRequestProperty("Authorization", "Bearer " + token);
                return new HttpsResponse(mConnection);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (ClassCastException e){
                e.printStackTrace();
                System.out.println("Class Cast Exception : " + e.getMessage());
                System.out.println("Please make sure if you are using http\"s\" correctly or not.");
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("HttpsClient-Get : " + e.getMessage());
                // Return Null HttpsResponse
                return new HttpsResponse();
            }
        }
        return null;
    }
    private void print_https_cert(HttpsURLConnection con){

        if(con!=null){

            try {

                System.out.println("Response Code : " + con.getResponseCode());
                System.out.println("Cipher Suite : " + con.getCipherSuite());
                System.out.println("\n");

                Certificate[] certs = con.getServerCertificates();
                for(Certificate cert : certs){
                    System.out.println("Cert Type : " + cert.getType());
                    System.out.println("Cert Hash Code : " + cert.hashCode());
                    System.out.println("Cert Public Key Algorithm : "
                            + cert.getPublicKey().getAlgorithm());
                    System.out.println("Cert Public Key Format : "
                            + cert.getPublicKey().getFormat());
                    System.out.println("\n");
                }

            } catch (SSLPeerUnverifiedException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }

        }

    }

    private static String getContent(HttpsURLConnection con){
        if(con!=null){
            StringBuffer retVal = new StringBuffer();

            try {
                BufferedReader br =
                        new BufferedReader(
                                new InputStreamReader(con.getInputStream()));

                String input;
                while ((input = br.readLine()) != null){
                    retVal.append(input);
                    //System.out.println(input);
                }
                br.close();
                return retVal.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void print_content(HttpsURLConnection con){
        if(con!=null){

            try {

                System.out.println("****** Content of the URL ********");
                BufferedReader br =
                        new BufferedReader(
                                new InputStreamReader(con.getInputStream()));

                String input;

                while ((input = br.readLine()) != null){
                    System.out.println(input);
                }
                br.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Splits and stores the set of parameters and values from the URL query.
     * Generally used to get values from the URL which has some worth data from server.
     * @param url The URL to be splited
     * @return The Map of the sets or an empty Map if there is no query
     */
    public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }

    /**
     * Splits and stores the set of parameters and values from the URL query.
     * Generally used to get values from the URL which has some worth data from server.
     * @param url The URL to be splited as String
     * @return The Map of the sets or an empty Map if there is no query
     */
    public static Map<String, String> splitQuery(String url) throws UnsupportedEncodingException, MalformedURLException {
        URL tobeParsed = new URL(url);
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = tobeParsed.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }
    /**
     * Splits and stores the set of parameters and values from the hashed(after #) URL query.
     * Generally used to get values from the URL which has some worth data from server.
     * @param url The URL to be splited as String
     * @return The Map of the sets or an empty Map if there is no hashed query
     */
    public static Map<String, String> splitHash(String url) throws UnsupportedEncodingException, MalformedURLException {
        if(url.contains("#")){
            int afterHash = url.indexOf("#");
            Map<String, String> query_pairs = new LinkedHashMap<String, String>();
            String hash = url.substring(afterHash + 1);
            String[] pairs = hash.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
            return query_pairs;
        }else{
            return null;
        }
    }

    /**
     * Used to skip validating certificates because we are currently using Self-Signed-Certificate
     * on our server.
     * Need to be deleted after getting CA certificate.
     */
    public class NullHostNameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            System.out.println("RestUtilImpl - Approving certificate for " + hostname);
            return true;
        }
    }
}
