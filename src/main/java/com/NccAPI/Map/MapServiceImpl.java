package com.NccAPI.Map;

import com.NccAPI.NccAPI;
import com.NccMap.MapLineData;
import com.NccMap.MapPointData;
import com.NccMap.NccMap;

import java.util.ArrayList;

/**
 * Created by root on 29.07.16.
 */
public class MapServiceImpl implements MapService {
    public ApiMapPointData getMapPoints(String login, String key){
        if (!new NccAPI().checkPermission(login, key, "GetMapPoints")) return null;

        ArrayList<MapPointData> mapPointData = new NccMap().getPoints();
        ApiMapPointData result = new ApiMapPointData();
        result.data = mapPointData;
        return result;

    }

    public ApiMapLineData getMapLines(String login, String key){
        if (!new NccAPI().checkPermission(login, key, "GetMapLines")) return null;

        ArrayList<MapLineData> mapLineData = new NccMap().getLines();
        ApiMapLineData result = new ApiMapLineData();
        result.data = mapLineData;
        return result;

    }
}
