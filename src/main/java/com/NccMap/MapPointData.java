package com.NccMap;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

/**
 * Created by root on 29.07.16.
 */
public class MapPointData extends NccAbstractData<MapPointData> {
    public Integer id;
    public float lat;
    public float lng;
    public Integer radius;
    public Integer state;

    public MapPointData(){

    }

    public MapPointData(float lat, float lng){
        this.lat = lat;
        this.lng = lng;
    }

    public MapPointData(float lat, float lng, int radius){
        this.lat = lat;
        this.lng = lng;
        this.radius = radius;
    }

    @Override
    public MapPointData fillData(){
        MapPointData mapPointData = new MapPointData();

        try {
            mapPointData.id = rs.getInt("id");
            mapPointData.lat = rs.getFloat("lat");
            mapPointData.lng = rs.getFloat("lng");
            mapPointData.radius = rs.getInt("radius");
            mapPointData.state = rs.getInt("active");

            return mapPointData;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return mapPointData;
    }
}
