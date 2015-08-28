package se.kmdev.tvepg.EPGApiCaller;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import se.kmdev.tvepg.tag.TAG;

/**
 * Created by admin on 27-Aug-15.
 */
public class EPGCaller {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.removeAllHeaders();
        client.setTimeout(3000);
        client.get(url, params, responseHandler);
    }

    public static void get(String url, RequestParams params, String Header, AsyncHttpResponseHandler responseHandler) {
        client.removeAllHeaders();
        client.setTimeout(3000);
        client.addHeader(TAG.HEADER_TAG, Header);
        client.get(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.removeAllHeaders();
        client.setTimeout(3000);
        client.post(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, String Header, AsyncHttpResponseHandler responseHandler) {
        client.removeAllHeaders();
        client.setTimeout(3000);
        client.addHeader(TAG.HEADER_TAG, Header);
        client.post(url, params, responseHandler);
    }

    public static void cancelRequest(Context context) {
        client.cancelRequests(context, false);
    }
}
