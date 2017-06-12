package com.NccUsers;

import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import com.sun.rowset.CachedRowSetImpl;
import org.apache.commons.lang.StringEscapeUtils;

import java.sql.SQLException;
import java.util.ArrayList;

public class NccUsers {

    private NccQuery query;
    private final String usersQueryFieldset = "id, userLogin, userPassword, userStatus, accountId, userIP";

    private static String DB_DECODE_KEY = "sab1093582";

    public NccUsers() throws NccUsersException {
        try {
            query = new NccQuery();
        } catch (NccQueryException e) {
            e.printStackTrace();
            throw new NccUsersException("CachedQuery SQL error");
        }
    }

    private NccUserData fillUserData(CachedRowSetImpl rs) throws NccUsersException {
        if (rs != null) {
            try {
                if (rs.next()) {
                    NccUserData userData = new NccUserData();

                    userData.userLogin = rs.getString("userLogin");
                    userData.id = rs.getInt("userId");
                    userData.userPassword = rs.getString("userPassword");
                    userData.userStatus = rs.getBoolean("userStatus") ? 0 : 1;
                    userData.userIP = rs.getLong("userIP");
                    userData.userTariff = rs.getInt("userTariff");
                    userData.userCredit = rs.getFloat("userCredit");
                    userData.userDeposit = rs.getFloat("userDeposit");

                    return userData;
                } else {
                    throw new NccUsersException("User not found");
                }
            } catch (SQLException se) {
                throw new NccUsersException("SQL error: " + se.getMessage());
            }
        } else {
            throw new NccUsersException("User not found");
        }
    }

    public NccUserData getUserByIP(Long ip) throws NccUsersException {
        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT " +
                    "u.id AS userLogin, " +
                    "0 AS accountId, " +
                    "DECODE(u.password, '" + DB_DECODE_KEY + "') AS userPassword, " +
                    "u.credit AS userCredit, " +
                    "u.disable AS userStatus, " +
                    "u.bill_id AS billId, " +
                    "u.uid AS userId, " +
                    "u.gid AS groupId, " +
                    "d.ip AS userIP, " +
                    "d.tp_id AS userTariff, " +
                    "d.cid AS userMAC, " +
                    "b.deposit AS userDeposit " +
                    "FROM users u " +
                    "LEFT JOIN bills b ON b.id=u.bill_id " +
                    "LEFT JOIN dv_main d ON d.uid=u.uid " +
                    "WHERE d.ip=" + ip);
        } catch (NccQueryException e) {
            throw new NccUsersException("SQL error: " + e.getMessage());
        }

        return fillUserData(rs);
    }

    public NccUserData getUser(Integer uid) throws NccUsersException {

        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT " +
                    "u.id AS userLogin, " +
                    "0 AS accountId, " +
                    "DECODE(u.password, '" + DB_DECODE_KEY + "') AS userPassword, " +
                    "u.credit AS userCredit, " +
                    "u.disable AS userStatus, " +
                    "u.bill_id AS billId, " +
                    "u.uid AS userId, " +
                    "u.gid AS groupId, " +
                    "d.ip AS userIP, " +
                    "d.tp_id AS userTariff, " +
                    "d.cid AS userMAC, " +
                    "b.deposit AS userDeposit " +
                    "FROM users u " +
                    "LEFT JOIN bills b ON b.id=u.bill_id " +
                    "LEFT JOIN dv_main d ON d.uid=u.uid " +
                    "WHERE u.uid=" + uid);
        } catch (NccQueryException e) {
            throw new NccUsersException("SQL error: " + e.getMessage());
        }

        return fillUserData(rs);
    }

    public NccUserData getUser(String login) throws NccUsersException {

        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT * FROM nccUsers WHERE userLogin='" + StringEscapeUtils.escapeSql(login) + "'");
        } catch (NccQueryException e) {
            throw new NccUsersException("SQL error: " + e.getMessage());
        }

        if (rs != null) {
            try {
                if (rs.next()) {
                    NccUserData userData = new NccUserData();

                    userData.id = rs.getInt("id");
                    userData.userLogin = login;
                    userData.userPassword = rs.getString("userPassword");
                    userData.userStatus = rs.getInt("userStatus");
                    userData.accountId = rs.getInt("accountId");
                    userData.userIP = rs.getLong("userIP");
                    userData.userTariff = rs.getInt("userTariff");

                    return userData;
                } else {
                    throw new NccUsersException("User not found");
                }
            } catch (SQLException se) {
                throw new NccUsersException("SQL error: " + se.getMessage());
            }
        } else {
            throw new NccUsersException("User not found");
        }
    }

    public ArrayList<NccUserData> getUsers() throws NccUsersException {

        ArrayList<NccUserData> users = new ArrayList<>();

        CachedRowSetImpl rs;

        try {
            rs = query.selectQuery("SELECT " + usersQueryFieldset + " FROM nccUsers LIMIT 10");

            try {
                while (rs.next()) {
                    NccUserData userData = new NccUserData();

                    userData.id = rs.getInt("id");
                    userData.userLogin = rs.getString("userLogin");
                    userData.userPassword = rs.getString("userPassword");
                    userData.userStatus = rs.getInt("userStatus");
                    userData.accountId = rs.getInt("accountId");
                    userData.userIP = rs.getLong("userIP");

                    users.add(userData);
                }

                return users;

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (NccQueryException e) {
            e.printStackTrace();
            throw new NccUsersException("CachedQuery error: " + e.getMessage());
        }

        return null;
    }

    public ArrayList<Integer> createUser(NccUserData userData) throws NccUsersException {

        try {
            if (getUser(userData.userLogin) != null) {
                System.out.println("User '" + userData.userLogin + "' exists");
                throw new NccUsersException("User exists");
            }

            String insertQuery = "INSERT INTO nccUsers (" +
                    "userLogin, " +
                    "userPassword, " +
                    "userStatus, " +
                    "accountId, " +
                    "userIP) VALUES (" +
                    "'" + StringEscapeUtils.escapeSql(userData.userLogin) + "', " +
                    "'" + StringEscapeUtils.escapeSql(userData.userPassword) + "', " +
                    userData.userStatus + ", " +
                    userData.accountId + ", " +
                    userData.userIP + ")";

            try {
                NccQuery query = new NccQuery();

                return query.updateQuery(insertQuery);

            } catch (NccQueryException e) {
                e.printStackTrace();
            }

        } catch (NccUsersException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
