package com.NccNAS;

import com.NccSystem.NccUtils;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;
import java.util.ArrayList;

public class NccNAS {

    private static Logger logger = Logger.getLogger(NccNAS.class);
    private NccQuery query;

    public NccNAS() throws NccNasException {
        try {
            query = new NccQuery();
        } catch (NccQueryException e) {
            e.printStackTrace();
            throw new NccNasException("SQL error: " + e.getMessage());
        }
    }

    public NccNasData getNAS(Integer id) throws NccNasException {

        return new NccNasData().getData("SELECT * FROM nccNAS WHERE id=" + id);
    }

    public ArrayList<NccNasData> getNAS() {

        return new NccNasData().getDataList("SELECT * FROM nccNAS");
    }

    public ArrayList<NccNasType> getNASTypes() throws NccNasException {

        return new NccNasType().getDataList("SELECT * FROM nccNASTypes");
    }

    public NccNasData getNasByIP(Long nasIP) throws NccNasException {

        return new NccNasData().getData("SELECT * FROM nccNAS WHERE nasIP=" + nasIP);
    }

    public String getNasSecretByIP(Long nasIP) {
        try {
            NccNAS nas = new NccNAS();
            NccNasData nasData = nas.getNasByIP(nasIP);

            if (nasData != null) {
                return nasData.nasSecret;
            }
        } catch (NccNasException e) {
            logger.error("NAS error for '" + NccUtils.long2ip(nasIP) + "': " + e.getMessage());
        }

        return null;
    }

    public Integer createNas(NccNasData nasData) throws NccNasException {

        try {
            ArrayList<Integer> ids = query.updateQuery("INSERT INTO nccNAS (" +
                    "nasName, " +
                    "nasType, " +
                    "nasIP, " +
                    "nasStatus, " +
                    "nasSecret, " +
                    "nasInterimInterval) VALUES(" +
                    "'" + nasData.nasName + "', " +
                    nasData.nasType + ", " +
                    nasData.nasIP + ", " +
                    "1, " +
                    "'" + nasData.nasSecret + "', " +
                    nasData.nasInterimInterval + ")");

            if (ids != null && ids.size() > 0) {
                return ids.get(0);
            }

        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Integer updateNas(NccNasData nasData) throws NccNasException {

        try {
            ArrayList<Integer> ids = query.updateQuery("UPDATE nccNAS SET " +
                    "nasName='" + nasData.nasName + "', " +
                    "nasType=" + nasData.nasType + ", " +
                    "nasIP=" + nasData.nasIP + ", " +
                    "nasSecret='" + nasData.nasSecret + "', " +
                    "nasInterimInterval=" + nasData.nasInterimInterval + " " +
                    "WHERE id=" + nasData.id);

            if (ids != null && ids.size() > 0) {
                return ids.get(0);
            }

        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Integer deleteNas(Integer id) throws NccNasException {
        try {
            ArrayList<Integer> ids = query.updateQuery("DELETE FROM nccNAS WHERE id=" + id);

            if (ids != null && ids.size() > 0) {
                return ids.get(0);
            }
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }
}
