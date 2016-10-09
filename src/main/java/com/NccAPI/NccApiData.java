package com.NccAPI;

import java.util.ArrayList;

/**
 * Created by root on 09.10.16.
 */
public class NccApiData<T> {
    public ArrayList<T> data;
    public Integer status;
    public String message;

    public NccApiData(){
        data = new ArrayList<T>();
        status = 1;
        message = "error";
    }
}
