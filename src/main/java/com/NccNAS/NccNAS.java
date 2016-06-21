package com.NccNAS;

import com.NccSystem.NccUtils;
import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import com.sun.rowset.CachedRowSetImpl;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

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

    private NccNasData fillNasData(CachedRowSetImpl rs) {

        NccNasData nasData = new NccNasData();

        try {
            nasData.id = rs.getInt("id");
            nasData.nasName = rs.getString("nasName");
            nasData.nasIP = rs.getLong("nasIP");
            nasData.nasType = rs.getInt("nasType");
            nasData.nasStatus = rs.getInt("nasStatus");
            nasData.nasSecret = rs.getString("nasSecret");
            nasData.nasInterimInterval = rs.getInt("nasInterimInterval");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return nasData;
    }

    private NccNasType fillNasType(CachedRowSetImpl rs) {
        NccNasType nasType = new NccNasType();

        try {
            nasType.id = rs.getInt("id");
            nasType.typeName = rs.getString("typeName");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return nasType;
    }

    public NccNasData getNAS(Integer id) throws NccNasException {
        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT * FROM nccNAS WHERE id=" + id);
        } catch (NccQueryException e) {
            e.printStackTrace();
            throw new NccNasException("getNAS: SQL error: " + e.getMessage());
        }

        if (rs != null) {
            try {
                if (rs.next()) {
                    return fillNasData(rs);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new NccNasException("getNAS: SQL error: " + e.getMessage());
            }
        } else {
            throw new NccNasException("getNAS: NAS not found");
        }

        throw new NccNasException("getNAS: NAS not found");
    }

    public ArrayList<NccNasData> getNAS() throws NccNasException {
        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT * FROM nccNAS");
        } catch (NccQueryException e) {
            e.printStackTrace();
            throw new NccNasException("getNAS: SQL error: " + e.getMessage());
        }

        if (rs != null) {
            ArrayList<NccNasData> nas = new ArrayList<>();

            try {
                NccNasData nasData;

                while (rs.next()) {
                    nasData = fillNasData(rs);
                    if (nasData != null) {
                        nas.add(nasData);
                    }
                }

                return nas;
            } catch (SQLException e) {
                e.printStackTrace();
                throw new NccNasException("getNAS: SQL error: " + e.getMessage());
            }
        } else {
            throw new NccNasException("getNAS: NAS not found");
        }
    }

    public ArrayList<NccNasType> getNASTypes() throws NccNasException {
        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT * FROM nccNASTypes");

            if (rs != null) {
                ArrayList<NccNasType> types = new ArrayList<>();
                NccNasType nasType;

                try {
                    while (rs.next()) {
                        nasType = fillNasType(rs);
                        if (nasType != null) {
                            types.add(nasType);
                        }
                    }

                    return types;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }

    public NccNasData getNasByIP(Long nasIP) throws NccNasException {
        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT * FROM nccNAS WHERE nasIP=" + nasIP);
        } catch (NccQueryException e) {
            e.printStackTrace();
            throw new NccNasException("getNasByIP: SQL error: " + e.getMessage());
        }

        if (rs != null) {
            try {
                if (rs.next()) {
                    return fillNasData(rs);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new NccNasException("getNasByIP: SQL error: " + e.getMessage());
            }
        } else {
            throw new NccNasException("getNasByIP: NAS not found");
        }

        throw new NccNasException("getNasByIP: NAS not found");
    }

    public String getNasSecretByIP(Long nasIP) {
        try {
            NccNAS nas = new NccNAS();
            NccNasData nasData = nas.getNasByIP(nasIP);

            if (nasData != null) {
                return nasData.nasSecret;
            }
        } catch (NccNasException e) {
            try {
                logger.error("NAS error for '" + NccUtils.long2ip(nasIP) + "': " + e.getMessage());
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            }
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
