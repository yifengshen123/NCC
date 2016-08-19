package com.NccMap;

import com.NccAccounts.NccAccounts;
import com.NccSystem.SQL.NccQuery;
import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by root on 29.07.16.
 */
public class NccMap {
    private NccQuery query;
    private static Logger logger = Logger.getLogger(NccAccounts.class);

    public NccMap() {

    }

    public ArrayList<MapPointData> getPoints() {
        return new MapPointData().getDataList("SELECT * FROM nccMapPoints");
    }

    public ArrayList<MapLineData> getLines() {
        return new MapLineData().getDataList("SELECT * FROM nccMapLines");
    }

    public ArrayList<MapVertexData> getLineVertex(Integer id) {
        return new MapVertexData().getDataList("SELECT * FROM nccMapLineVertex WHERE lineId=" + id);
    }
}
