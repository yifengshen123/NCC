package com.NccMap;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by root on 19.08.16.
 */
public class MapLineData extends NccAbstractData<MapLineData> {
    public Integer id;
    public ArrayList<MapVertexData> vertex;

    @Override
    public MapLineData fillData() {
        MapLineData mapLineData = new MapLineData();

        try {
            mapLineData.id = rs.getInt("id");
            mapLineData.vertex = new NccMap().getLineVertex(mapLineData.id);

            return mapLineData;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mapLineData;
    }
}
