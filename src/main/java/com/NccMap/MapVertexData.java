package com.NccMap;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

/**
 * Created by root on 19.08.16.
 */
public class MapVertexData extends NccAbstractData<MapVertexData> {
    public Double lat;
    public Double lng;

    @Override
    public MapVertexData fillData() {
        MapVertexData mapVertexData = new MapVertexData();

        try {
            mapVertexData.lat = rs.getDouble("lat");
            mapVertexData.lng = rs.getDouble("lng");

            return mapVertexData;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mapVertexData;
    }
}
