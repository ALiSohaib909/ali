package com.pins.infinity.utility;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;


import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class MultipartRequest extends Request<String> {

    MultipartEntityBuilder entity = MultipartEntityBuilder.create();
    HttpEntity httpentity;
    private static final String FILE_PART_NAME = "image";
    private static final String MEDIA_PART_NAME = "media";
    private final Response.Listener<String> mListener;
    private final File mFilePart;
    boolean isMedia;
    private final Map<String, String> mStringPart;
    private Map<String, String> header;
    public MultipartRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener,
            File file, Map<String, String> mStringPart, Map<String, String> header, boolean media) {
        super(Method.POST, url, errorListener);
        mListener = listener;
        this.isMedia = media;
        mFilePart = file;
        this.mStringPart = mStringPart;
        this.header = header;
        buildMultipartEntity();
    }

    public void addStringBody(String param, String value) {
        mStringPart.put(param, value);
    }

    private void buildMultipartEntity() {
        if(mFilePart!=null) {
            if(isMedia) {
                entity.addPart(MEDIA_PART_NAME, new FileBody(mFilePart));
                System.out.println("filename is    "+ mFilePart.getName());
            } else {
                entity.addPart(FILE_PART_NAME, new FileBody(mFilePart));
            }

        }

        for (Map.Entry<String, String> entry : mStringPart.entrySet()) {
            try {
                entity.addTextBody(entry.getKey(), entry.getValue());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBodyContentType() {
        return httpentity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            httpentity = entity.build();
            httpentity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return header;
    }
}