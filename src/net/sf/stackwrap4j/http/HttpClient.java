/**
 * StackWrap4J - A Java wrapper for the Stack Exchange API.
 * 
 * Copyright (c) 2010 Bill Cruise and Justin Nelson.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.sf.stackwrap4j.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import android.util.Log;

/**
 * A tiny HTTP client that does only what we need to interact with the Stack
 * Exchange API.
 * 
 * @author Justin Nelson
 * @author Bill Cruise
 */
public class HttpClient {

    private static final String LOG_TAG = HttpClient.class.getSimpleName();

    /**
     * The proxy to use for this session. Defaults to no proxy
     */
    public static Proxy proxyServer = null;

    /**
     * The throttling method used for this session.
     */
    public static Throttle throttle = Throttle.NON_THREADED;

    /*
     * Timestamp when the last request was made.
     */
    private long lastRequest = 0;

    private int requests = 0;
    private int throttledRequests = 0;

    private static final long WAIT_TIME = 170;

    /*
     * The minimum amount of time to delay between requests.
     */
    private static int timeout = 0;

    private ApiRequestQueue queue = null; // new ApiRequestQueue();

    protected static final String API_URL = "http://api.stackexchange.com";

    public HttpClient() {
        // queue = new ApiRequestQueue();
    }

    /**
     * Sets the read timeout on the connection.
     * 
     * A timeout of zero means infinite (that's the default). If a request times
     * out, it will throw a java.net.SocketTimeoutTxeption
     * 
     * @param timeout
     *            the time in milliseconds
     */
    public static void setTimeout(int timeout) {
        if (timeout < 0)
            throw new IllegalArgumentException(
                    "The timeout value must be positive.");
        HttpClient.timeout = timeout;
    }

    /**
     * Makes a HTTP request to a server.
     * 
     * @param site
     *            - The URL of the server (Example:
     *            "http://api.stackoverflow.com/")
     * @param versionString
     *            - The current version of the API (Example: "0.8/")
     * @param extendedURL
     *            - The (not sure what to call it) rest of the URL (Example:
     *            "users/{id}/favorites/recent")
     * @param apiKey
     *            - The API key for your application (Example: "knockknock")
     * @return - A JSON string containing the data that was requested
     * @throws IOException
     *             - If the given URL doesn't exist
     */
    public String sendGetRequest(String site, String versionString,
            String extendedURL, String apiKey) throws IOException {
        return sendGetRequest(site, versionString, extendedURL, apiKey, null);
    }

    /**
     * 
     * @param site
     *            - The URL of the server (Example:
     *            "http://api.stackoverflow.com/")
     * @param versionString
     *            - The current version of the API (Example: "0.8/")
     * @param extendedURL
     *            - The (not sure what to call it) rest of the URL (Example:
     *            "users/{id}/favorites/recent")
     * @param apiKey
     *            - The API key for your application (Example: "knockknock")
     * @param requestParams
     *            - The parameters for this request (Example:
     *            "param1=val1&param2=val2")
     * @return - A JSON string containing the data that was requested
     * @throws IOException
     *             - If the given URL doesn't exist
     */
    public String sendGetRequest(String site, String versionString,
            String extendedURL, String apiKey, String requestParams)
            throws IOException {

        String response = null;
        final long now = System.currentTimeMillis();

        switch (throttle) {
        case NON_THREADED:
            // delay before making the request in the next case.
            long elapsed = now - lastRequest;
            if (elapsed < WAIT_TIME) {
                throttledRequests++;
                try {
                    Thread.sleep(WAIT_TIME - elapsed);
                } catch (InterruptedException ie) {
                } // We don't really mind if this gets interrupted.
            }
        case NONE:
            ApiRequest req = new ApiRequest(site, versionString, extendedURL,
                    apiKey, requestParams);
            response = req.makeRequest();
            break;
        case THREADED:
            response = sendGetRequestThrottled(site, versionString,
                    extendedURL, apiKey, requestParams);
        }
        // moved counter after request has been processed - guarantees that our
        // time lags behind
        // theirs instead of getting out in front by not accounting for the time
        // it takes for
        // for them to process the request
        final long later = System.currentTimeMillis();
        lastRequest = later;
        requests++;

        return response;
    }

    public String sendGetRequestThrottled(String site, String versionString,
            String extendedURL, String apiKey, String requestParams)
            throws IOException {
        ApiRequest req = new ApiRequest(site, versionString, extendedURL,
                apiKey, requestParams);
        ApiRequestQueue.Future result = queue.offer(req);
        String resultS = result.get();
        if (result.getException() != null)
            throw result.getException();
        return resultS;
    }

    private static String combineUrlParts(String first, String second) {
        String ret = first;
        if (!ret.endsWith("/")) {
            ret = ret + "/";
        }
        if (second.startsWith("/"))
            return ret + second.substring(1);
        else
            return ret + second;
    }

    public static class ApiRequest {
        private HttpURLConnection conn;
        private boolean used;

        public ApiRequest(String site, String versionString,
                String extendedURL, String apiKey, String requestParams)
                throws IOException {
            this.used = false;

            // Begin building up the request URL
            String urlStr = HttpClient.API_URL;

            urlStr = combineUrlParts(urlStr, versionString);
            urlStr = combineUrlParts(urlStr, extendedURL);
            // if no key, start with ? otherwise &
            urlStr += (apiKey == null) ? "?" : "?key=" + apiKey + "&";
            if (requestParams != null && requestParams.length() > 0) {
                if (requestParams.startsWith("&")) {
                    // if the &amp; was on the beginning of the string, remove
                    // it
                    requestParams = requestParams.substring(1);
                }

                urlStr += requestParams;
            }
            if (site != null) {
                if (urlStr.endsWith("&")) {
                    urlStr = urlStr.substring(0, urlStr.length() - 1);
                }
                urlStr += "&site=" + site;
            }
            // Done building the string, now we create a request

            URL url = new URL(urlStr);
            Log.i(LOG_TAG, urlStr);
            HttpURLConnection conn;
            if (proxyServer == null)
                conn = (HttpURLConnection) url.openConnection();
            else
                conn = (HttpURLConnection) url.openConnection(proxyServer);
            conn.setInstanceFollowRedirects(true);

            conn.setReadTimeout(timeout);
            conn.setConnectTimeout(timeout);

            // allow both GZip and Deflate (ZLib) encodings
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("User-agent", "StackWrap4J-1.0/" + apiKey);

            this.conn = conn;
        }

        public String makeRequest() throws IOException {
            if (used)
                throw new IOException("Content already read from this request.");
            conn.connect();

            // Request built, lets get a response
            String encoding = conn.getContentEncoding();
            InputStream inStr = null;

            // create the appropriate stream wrapper based on
            // the encoding type
            if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
                inStr = new GZIPInputStream(conn.getInputStream());
            } else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
                inStr = new InflaterInputStream(conn.getInputStream(),
                        new Inflater(true));
            } else {
                inStr = conn.getInputStream();
            }

            // we have a response, lets extract a JSON string
            String result = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inStr));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            result = sb.toString();
            used = true;
            return result;
        }
    }

    public int getRequests() {
        return requests;
    }

    public int getThrottledRequests() {
        return throttledRequests;
    }
}
