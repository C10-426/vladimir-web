package net;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leo
 * @date 2018/6/25
 */

public class Request implements IRequest {

    private boolean autoClose = true;
    private int priority;
    private int connectionTimeout;
    private int socketTimeout;
    private String url;
    private Map<String, String> header = new HashMap<>();
    private Map<String, Object> query = new HashMap<>();
    private Map<String, String> unmodifiableHeader;
    private Map<String, Object> unmodifiableQuery;
    private Method method;

    public Request(String url) {
        connectionTimeout = 5000;
        socketTimeout = 10000;
        this.unmodifiableHeader = Collections.unmodifiableMap(this.header);
        this.unmodifiableQuery = Collections.unmodifiableMap(this.query);
        this.url = url;
        this.method = Method.POST;
    }

    @Override
    public Method getMethod() {
        return this.method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public Map<String, String> getHeaders() {
        return this.unmodifiableHeader;
    }


    @Override
    public Map<String, Object> getQueries() {
        return this.unmodifiableQuery;
    }

    @Override
    public void addHeader(String key, String value) {
        if(!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)) {
            this.header.put(key, value);
        }
    }

    @Override
    public void addQuery(String key, JSONObject value) {
        this.query.put(key, value);
    }

    @Override
    public void addQuery(String key, JSONArray value) {
        this.query.put(key, value);
    }

    @Override
    public void addQuery(String key, String value) {
        this.query.put(key, value);
    }

    @Override
    public String getUrlWithParams() {
        if(this.getQueries().isEmpty()) {
            return this.getUrl();
        } else {
            return getUrl();
//            Uri.Builder builder = Uri.parse(this.getUrl()).buildUpon();
//            for(Map.Entry<String, Object> param : this.getQueries().entrySet()) {
//                if (param.getValue() instanceof String) {
//                    builder.appendQueryParameter(param.getKey(), (String)param.getValue());
//                }
//            }
//            return builder.build().toString();
        }
    }

    @Override
    public int getConnectionTimeOut() {
        return this.connectionTimeout;
    }

    @Override
    public void setConnectionTimeOut(int timeout) {
        this.connectionTimeout = timeout;
    }

    @Override
    public int getSocketTimeOut() {
        return this.socketTimeout;
    }

    @Override
    public void setSocketTimeOut(int timeout) {
        this.socketTimeout = timeout;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }


    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public boolean isAutoClose() {
        return this.autoClose;
    }

    public void setAutoClose(boolean autoClose) {
        this.autoClose = autoClose;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public byte[] buildBody() {
        return new byte[0];
    }
}
