package com.NccSystem.SQL;

import com.Ncc;
import com.NccSystem.NccLogger;
import com.sun.rowset.CachedRowSetImpl;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

public class NccQuery {

    private Connection sql;
    private static NccLogger nccLogger = new NccLogger("SQLLogger");
    private static Logger logger = nccLogger.setFilename(Ncc.SQLLogfile);

    public NccQuery() throws NccQueryException {
    }

    public CachedRowSetImpl selectQuery(String query) throws NccQueryException {

        if (Ncc.logQuery) logger.info("SQL: '" + query + "'");

        try {

            try {
                sql = Ncc.sqlPool.getConnection();
            } catch (SQLException se) {
                se.printStackTrace();
                throw new NccQueryException("SQL error");
            }

            CachedRowSetImpl crs = new CachedRowSetImpl();

            Statement stmt = sql.createStatement();

            ResultSet rs = stmt.executeQuery(query);

            if (rs != null) {
                crs.populate(rs);
                sql.close();
                if (Ncc.logQuery) logger.info("Query complete");
                return crs;
            } else {
                sql.close();
                throw new NccQueryException("null result set");
            }

        } catch (SQLException se) {
            se.printStackTrace();
            throw new NccQueryException("SQL error");
        }
    }

    public ArrayList<Integer> updateQuery(String query) throws NccQueryException {

        if (Ncc.logQuery) logger.info("SQL: '" + query + "'");

        try {

            try {
                sql = Ncc.sqlPool.getConnection();
            } catch (SQLException se) {
                se.printStackTrace();
                throw new NccQueryException("SQL error");
            }

            Statement stmt = sql.createStatement();
            Integer updateRows = stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

            ArrayList<Integer> ids = new ArrayList<>();
            if ((updateRows > 0) && !stmt.isClosed()) {
                ResultSet rs = stmt.getGeneratedKeys();

                CachedRowSetImpl crs = new CachedRowSetImpl();

                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }

                sql.close();
            } else {
                sql.close();
            }
            if (Ncc.logQuery) logger.info("Query complete");
            return ids;

        } catch (SQLException se) {
            se.printStackTrace();
            throw new NccQueryException("SQL error");
        }
    }
}

