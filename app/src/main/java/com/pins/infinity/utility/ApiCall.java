package com.pins.infinity.utility;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.pins.infinity.application.MyApplication;
import com.pins.infinity.interfaces.ApiResponseListener;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shri.kant on 18-11-2016.
 */
public class ApiCall {
    private static ApiCall mApiCall;
    public ApiCall()
    {

    }

    public static ApiCall getInstance(){
        if(mApiCall==null){
            mApiCall = new ApiCall();
        }
        return mApiCall;
    }

    public void makeGetRequest(final Context context, String url, HashMap<String, String> params,
                               final ApiResponseListener apiResponseListener,final int requestCode) {
        url = ApiConstants.urlBuilder(url, params);
        Log.e("URL ", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    apiResponseListener.onResponse(response,requestCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null && error.networkResponse != null) {
                    apiResponseListener.onError(error.toString(), error.networkResponse.statusCode, requestCode);
                } else {
                    apiResponseListener.onError(error.toString(), 0, requestCode);
                }
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void makeGetRequest(final Context context, String url, final ApiResponseListener apiResponseListener, String tag,final int requestCode) {
        //Log.e("Url =", url);
        Log.e("URL ", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    apiResponseListener.onResponse(response,requestCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null && error.networkResponse != null) {
                    apiResponseListener.onError(error.toString(), error.networkResponse.statusCode, requestCode);
                } else {
                    apiResponseListener.onError(error.toString(), 0, requestCode);
                }
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    public void makeGetRequest(final Context context, String url, HashMap<String, String> params,final Map<String, String> header, final ApiResponseListener apiResponseListener,final int requestCode) {
//        params.put("isPurchased","1");//shri
        url = ApiConstants.urlBuilder(url, params);

        Log.e("URL ",url);
        //Log.e("Url =", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    apiResponseListener.onResponse(response,requestCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null && error.networkResponse != null) {
                    apiResponseListener.onError(error.toString(), error.networkResponse.statusCode, requestCode);
                } else {
                    apiResponseListener.onError(error.toString(), 0, requestCode);
                }
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               /* Map<String, String> header = new HashMap<>();
                header.put(ApiConstants.AUTH_HEADER_KEY,ApiConstants.AUTH_HEADER);*/

                return header;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(stringRequest, "PIN");
    }
    public void makeGetRequestWithUrl(final Context context, String url, HashMap<String, String> params,final Map<String, String> header, final ApiResponseListener apiResponseListener,final int requestCode) {

        Log.e("URL ",url +"      "+ params+"   "+header);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    apiResponseListener.onResponse(response,requestCode);
                    Log.d("response", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null && error.networkResponse != null) {
                    apiResponseListener.onError(error.toString(), error.networkResponse.statusCode, requestCode);
                } else {
                    apiResponseListener.onError(error.toString(), 0, requestCode);
                }
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               /* Map<String, String> header = new HashMap<>();
                header.put(ApiConstants.AUTH_HEADER_KEY,ApiConstants.AUTH_HEADER);*/

                return header;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(stringRequest, "PINS");

    }
    public void makePutRequestWithUrl(final Context context, String url, HashMap<String, String> params,final Map<String, String> header, final ApiResponseListener apiResponseListener,final int requestCode, String userID) {
//        params.put("isPurchased","1");//shri
        if(userID!=null)
        url = ApiConstants.urlBuilderID(url, params, userID);
        else
        url = ApiConstants.urlBuilder(url, params);

        Log.e("URL ",url);
        //Log.e("Url =", url);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    apiResponseListener.onResponse(response,requestCode);
                    Log.d("response", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null && error.networkResponse != null) {
                    apiResponseListener.onError(error.toString(), error.networkResponse.statusCode, requestCode);
                } else {
                    apiResponseListener.onError(error.toString(), 0, requestCode);
                }
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               /* Map<String, String> header = new HashMap<>();
                header.put(ApiConstants.AUTH_HEADER_KEY,ApiConstants.AUTH_HEADER);*/

                return header;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(stringRequest, "PIN");
    }

    public void makeDeleteRequest(final Context context, String url, HashMap<String, String> params,final Map<String, String> header, final ApiResponseListener apiResponseListener,final int requestCode, String userID) {
//        params.put("isPurchased","1");//shri
        url = ApiConstants.urlBuilderID(url, params, userID);

        Log.e("URL ",url);
        //Log.e("Url =", url);
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    apiResponseListener.onResponse(response,requestCode);
                    Log.d("response", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null && error.networkResponse != null) {
                    apiResponseListener.onError(error.toString(), error.networkResponse.statusCode, requestCode);
                } else {
                    apiResponseListener.onError(error.toString(), 0, requestCode);
                }
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               /* Map<String, String> header = new HashMap<>();
                header.put(ApiConstants.AUTH_HEADER_KEY,ApiConstants.AUTH_HEADER);*/

                return header;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(stringRequest, "PIN");
    }

    public void makeDeletePictureRequest(String url, HashMap<String, String> params,final Map<String, String> header, final ApiResponseListener apiResponseListener,final int requestCode) {
//        params.put("isPurchased","1");//shri

        Log.e("URL ",url);
        //Log.e("Url =", url);
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    apiResponseListener.onResponse(response,requestCode);
                    Log.d("response", response);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null && error.networkResponse != null) {
                    apiResponseListener.onError(error.toString(), error.networkResponse.statusCode, requestCode);
                } else {
                    apiResponseListener.onError(error.toString(), 0, requestCode);
                }
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               /* Map<String, String> header = new HashMap<>();
                header.put(ApiConstants.AUTH_HEADER_KEY,ApiConstants.AUTH_HEADER);*/

                return header;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(stringRequest, "PIN");
    }

    public void makePostRequest(String url, HashMap<String, String> params,final Map<String, String> header, final ApiResponseListener apiResponseListener,final int requestCode, String userID) {
//        params.put("isPurchased","1");//shri
        url = ApiConstants.urlBuilderID(url, params, userID);

        Log.e("URL ",url);
        //Log.e("Url =", url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("response ",response);
                    apiResponseListener.onResponse(response,requestCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null && error.networkResponse != null) {
                    apiResponseListener.onError(error.toString(), error.networkResponse.statusCode, requestCode);
                } else {
                    apiResponseListener.onError(error.toString(), 0, requestCode);
                }
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               /* Map<String, String> header = new HashMap<>();
                header.put(ApiConstants.AUTH_HEADER_KEY,ApiConstants.AUTH_HEADER);*/

                return header;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(stringRequest, "PIN");
    }

    public void makePutRequest(String url, HashMap<String, String> params,final Map<String, String> header, final ApiResponseListener apiResponseListener,final int requestCode, String userID) {
        url = ApiConstants.urlBuilderID(url, params, userID);

        if(!url.contains("location")){
            Log.e("URL ",url);
        }
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    apiResponseListener.onResponse(response,requestCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null && error.networkResponse != null) {
                    apiResponseListener.onError(error.toString(), error.networkResponse.statusCode, requestCode);
                } else {
                    apiResponseListener.onError(error.toString(), 0, requestCode);
                }
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               /* Map<String, String> header = new HashMap<>();
                header.put(ApiConstants.AUTH_HEADER_KEY,ApiConstants.AUTH_HEADER);*/

                return header;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(stringRequest, "PIN");
    }

    public void makePostRequest(final Context context, String url, final HashMap<String, String> params, final Map<String, String> header, final ApiResponseListener apiResponseListener, final int requestCode) {

        url = ApiConstants.urlBuilder(url, null);
        Log.e("URL ",url+"  "+params+"   "+header);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("response ",response);
                    apiResponseListener.onResponse(response,requestCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("error   "+error.getLocalizedMessage());
                if(error != null && error.networkResponse != null) {
                    apiResponseListener.onError(error.toString(), error.networkResponse.statusCode, requestCode);
                } else {
                    apiResponseListener.onError(error.toString(), 0, requestCode);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               /* Map<String, String> header = new HashMap<>();
                header.put(ApiConstants.AUTH_HEADER_KEY,ApiConstants.AUTH_HEADER);*/

                return header;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(stringRequest, "PINS");

    }


    public void makeMultiPartRequest(String url,File file,final HashMap<String, String> params,
                                     final Map<String, String> header,final ApiResponseListener apiResponseListener,
                                     final int requestCode, boolean media) {
        url = ApiConstants.urlBuilder(url, null);
        MultipartRequest multipartRequest = new MultipartRequest(url,new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    Log.d("response.....", response);
                    apiResponseListener.onResponse(response,requestCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(error != null && error.networkResponse != null) {
                    apiResponseListener.onError(error.toString(), error.networkResponse.statusCode, requestCode);
                } else {
                    apiResponseListener.onError(error.toString(), 0, requestCode);
                }
            }

        }, file, params,header, media)
        {
            @Override
            public String getBodyContentType() {
                return "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'";
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(150000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(multipartRequest);
    }
//    final HashMap<String, String>
    public void makePostRequestContact(final Context context, String url, final String params, final Map<String, String> header, final ApiResponseListener apiResponseListener, final int requestCode, String userId, String imei) {



//        url = ApiConstants.urlBuilderCotactsWithTwoPara(url, null, userId, "1231234abdz");
        url = ApiConstants.urlBuilderCotactsWithTwoPara(url, null, userId, imei);
        Log.e("URL ",url+"   "+userId+"   "+imei+"   "+header+"   "+params);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("response ",response);
                    apiResponseListener.onResponse(response,requestCode);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error ",error.toString());
                if(error != null && error.networkResponse != null) {
                    apiResponseListener.onError(error.toString(), error.networkResponse.statusCode, requestCode);
                } else {
                    apiResponseListener.onError(error.toString(), 0, requestCode);
                }
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return params == null ? null : params.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", params, "utf-8");
                    return null;
                }
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {

                    responseString = String.valueOf(response.statusCode);
                    // can get more details such as response.headers
                }
                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
               /* Map<String, String> header = new HashMap<>();
                header.put(ApiConstants.AUTH_HEADER_KEY,ApiConstants.AUTH_HEADER);*/

                return header;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyApplication.getInstance().addToRequestQueue(stringRequest, "PINS");

    }
}

