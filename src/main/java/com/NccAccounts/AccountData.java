package com.NccAccounts;

import com.NccNAS.NccNasData;
import com.NccSystem.NccAbstractData;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class AccountData extends NccAbstractData<AccountData> {

    public Integer id;
    public Double accDeposit;
    public Double accCredit;
    public String accPerson;
    public String accAddressCity;
    public String accAddressStreet;
    public String accAddressBuild;
    public String accAddressApt;
    public Date accRegDate;
    public String accPersonPassport;
    public String accPersonPhone;
    public String accPersonEmail;
    public String accComments;
    public String accLogin;
    public String accPassword;

    @Override
    public AccountData fillData(){
        AccountData accountData = new AccountData();

        try {
            accountData.id = rs.getInt("id");
            accountData.accDeposit = rs.getDouble("accDeposit");
            accountData.accCredit = rs.getDouble("accCredit");
            accountData.accPerson = rs.getString("accPerson");
            accountData.accAddressCity = rs.getString("accAddressCity");
            accountData.accAddressBuild = rs.getString("accAddressBuild");
            accountData.accAddressApt = rs.getString("accAddressApt");
            accountData.accRegDate = rs.getDate("accRegDate");
            accountData.accPersonPassport = rs.getString("accPersonPassport");
            accountData.accPersonPhone = rs.getString("accPersonPhone");
            accountData.accPersonEmail = rs.getString("accPersonEmail");
            accountData.accComments = rs.getString("accComments");
            accountData.accLogin = rs.getString("accLogin");
            accountData.accPassword = rs.getString("accPassword");

            return accountData;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accountData;
    }
}
