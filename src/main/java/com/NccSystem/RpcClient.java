package com.NccSystem;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import org.apache.commons.lang.ArrayUtils;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by root on 05.10.16.
 */
public class RpcClient<T> {

    private String server;
    private String login;
    private String key;
    private Object[] auth;
    private JsonRpcHttpClient client;

    public RpcClient(String server, String login, String key) {
        try {
            this.server = server;
            this.login = login;
            this.key = key;
            auth = new Object[]{login, key};
            client = new JsonRpcHttpClient(new URL(server));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public <T> T get(Class<T> c, String request, Object[] params) {
        try {
            Object[] p;
            if (params != null) {
                p = (Object[]) ArrayUtils.addAll(auth, params);
            } else {
                p = auth;
            }

            return client.invoke(request, p, c);

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    public <T> T get(Class<T> c, String url, String request, Object[] params) {
        try {
            Object[] p;
            if (params != null) {
                p = (Object[]) ArrayUtils.addAll(auth, params);
            } else {
                p = auth;
            }

            client.setServiceUrl(new URL(url));
            return client.invoke(request, p, c);

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
