package com.NccMap;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

/**
 * Created by root on 29.07.16.
 */
public class MapPointData extends NccAbstractData<MapPointData> {
    public Integer id;
    public Double lat;
    public Double lng;
    public Integer radius;
    public Integer state;

    @Override
    public MapPointData fillData(){
        MapPointData mapPointData = new MapPointData();

        try {
            mapPointData.id = rs.getInt("id");
            mapPointData.lat = rs.getDouble("lat");
            mapPointData.lng = rs.getDouble("lng");
            mapPointData.radius = rs.getInt("radius");
            mapPointData.state = rs.getInt("active");

            return mapPointData;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mapPointData;
    }
}
