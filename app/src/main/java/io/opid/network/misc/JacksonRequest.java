package io.opid.network.misc;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class JacksonRequest<T> extends Request<T> {
    private final Class<T> clazz;
    private final Response.Listener<T> listener;
    private final Map<String, String> params;
    private Map<String, String> headers;

    public JacksonRequest(int method, String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(method, url, null, clazz, new HashMap<String, String>(), listener, errorListener);
    }

    public JacksonRequest(int method, String url, Map<String, String> params, Class<T> clazz, Map<String, String> headers, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.clazz = clazz;
        this.listener = listener;
        this.params = params;
        this.headers = headers;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String data = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new ObjectMapper().readValue(data, clazz), HttpHeaderParser.parseCacheHeaders(response));
        } catch (IOException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public String getBodyContentType() {
        return "application/json";
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        return new JSONObject(params).toString().getBytes(Charset.forName("UTF-8"));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.putAll(this.headers);
        return headers;
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }
}
