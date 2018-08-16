package net;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public interface IRequest {

    Method getMethod();

    int getPriority();

    void setConnectionTimeOut(int timeout);

    int getConnectionTimeOut();

    int getSocketTimeOut();

    void setSocketTimeOut(int timeout);

    void setUrl(String url);

    String getUrl();

    String getUrlWithParams();

    void addHeader(String key, String value);

    Map<String, String> getHeaders();

    void addQuery(String key, String value);

    void addQuery(String key, JSONObject value);

    void addQuery(String key, JSONArray value);

    Map<String, Object> getQueries();

    boolean isAutoClose();

    byte[] buildBody();

    String toString();

    public static enum Method {
        GET,
        POST;

        private Method() {
        }
    }
}
