package com.NccAccounts;

import com.NccSystem.SQL.NccQuery;
import com.NccSystem.SQL.NccQueryException;
import com.sun.rowset.CachedRowSetImpl;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class NccAccounts {

    private NccQuery query;
    private static Logger logger = Logger.getLogger(NccAccounts.class);
    private final String accQueryFieldset = "id, accDeposit, accCredit, accPerson, accAddressCity, accAddressStreet, accAddressBuild, accAddressApt, accRegDate, accPersonPassport, accPersonPhone, accPersonEmail, accComments";

    public NccAccounts() {
        try {
            query = new NccQuery();
        } catch (NccQueryException e) {
            e.printStackTrace();
        }
    }

    public AccountData getAccount(Integer id) {
        return new AccountData().getData("SELECT * FROM nccUserAccounts WHERE id=" + id);
    }

    public AccountData getAccount(String login) {
        return new AccountData().getData("SELECT * FROM nccUserAccounts WHERE accLogin='" + login + "'");
    }

    public ArrayList<AccountData> getAccount() {
        return new AccountData().getDataList("SELECT * FROM nccUserAccounts");
    }

    public ArrayList<AccountData> getAdministrators() {
        return new AccountData().getDataList("SELECT * FROM nccAccountsPermissions a " +
                "LEFT JOIN nccPermissions p ON a.permId=p.id " +
                "LEFT JOIN nccUserAccounts c ON c.id=a.accountId " +
                "WHERE p.permName='LoginAsAdministrator'");
    }

    public boolean checkAccountPermission(AccountData accountData, String permission) {

        logger.info("Checking permission '" + permission + "' for account '" + accountData.accLogin + "'");

        if (accountData != null) {

            try {
                CachedRowSetImpl rs = query.selectQuery("SELECT a.accountId FROM nccAccountsPermissions a " +
                        "LEFT JOIN nccPermissions p ON a.permId=p.id " +
                        "WHERE p.permName='" + permission + "' AND " +
                        "a.accountId=" + accountData.id);

                if (rs != null) {
                    if (rs.size() > 0) {
                        logger.info("Success");
                        return true;
                    }
                }
            } catch (NccQueryException e) {
                e.printStackTrace();
            }
        }

        logger.info("Fail");

        return false;
    }

    public ArrayList<Integer> createAccount(AccountData accountData) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String insertQuery = "INSERT INTO nccUserAccounts (" +
                "accDeposit, " +
                "accCredit, " +
                "accPerson, " +
                "accAddressCity, " +
                "accAddressStreet, " +
                "accAddressBuild, " +
                "accAddressApt, " +
                "accRegDate, " +
                "accPersonPassport, " +
                "accPersonPhone, " +
                "accPersonEmail, " +
                "accComments, " +
                "accLogin, " +
                "accPassword) VALUES (" +
                accountData.accDeposit + ", " +
                accountData.accCredit + ", " +
                "'" + accountData.accPerson + "', " +
                "'" + accountData.accAddressCity + "', " +
                "'" + accountData.accAddressStreet + "', " +
                "'" + accountData.accAddressBuild + "', " +
                "'" + accountData.accAddressApt + "', " +
                "'" + dateFormat.format(accountData.accRegDate) + "', " +
                "'" + accountData.accPersonPassport + "', " +
                "'" + accountData.accPersonPhone + "', " +
                "'" + accountData.accPersonEmail + "', " +
                "'" + accountData.accComments + "', " +
                "'" + accountData.accLogin + "', " +
                "'" + accountData.accPassword + "')";

        try {
            return query.updateQuery(insertQuery);

        } catch (NccQueryException e) {
            e.printStackTrace();
        }

        return null;
    }
}
