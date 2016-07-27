package com.NccAPI.UserAccounts;

import com.NccAPI.NccAPI;
import com.NccAccounts.AccountData;
import com.NccAccounts.NccAccounts;

import java.util.ArrayList;
import java.util.Date;

public class AccountsServiceImpl implements AccountsService {


    public AccountData getAccount(String login, String key, Integer id) {

        if (!new NccAPI().checkPermission(login, key, "GetAccount")) return null;

        return new NccAccounts().getAccount(id);
    }

    public ApiAccountData getAccounts(String login, String key) {

        if (!new NccAPI().checkPermission(login, key, "GetAccounts")) return null;

        ArrayList<AccountData> accountData = new NccAccounts().getAccount();
        ApiAccountData result = new ApiAccountData();
        result.data = accountData;
        return result;
    }

    public ApiAccountData getAdministrators(String login, String key) {

        if (!new NccAPI().checkPermission(login, key, "GetAdministrators")) return null;

        ArrayList<AccountData> accountData = new NccAccounts().getAdministrators();
        ApiAccountData result = new ApiAccountData();
        result.data = accountData;
        return result;
    }

    public ApiAccountData createAccount(String login, String key,
                                        String accLogin,
                                        String accPassword,
                                        Double accDeposit,
                                        Double accCredit,
                                        String accPerson,
                                        String accAddressCity,
                                        String accAddressStreet,
                                        String accAddressBuild,
                                        String accAddressApt,
                                        Date accRegDate,
                                        String accPersonPassport,
                                        String accPersonPhone,
                                        String accPersonEmail,
                                        String accComments) {

        if (!new NccAPI().checkPermission(login, key, "CreateAccount")) return null;

        AccountData accountData = new AccountData();

        accountData.accLogin = accLogin;
        accountData.accPassword = accPassword;
        accountData.accDeposit = accDeposit;
        accountData.accCredit = accCredit;
        accountData.accPerson = accPerson;
        accountData.accAddressCity = accAddressCity;
        accountData.accAddressStreet = accAddressStreet;
        accountData.accAddressBuild = accAddressBuild;
        accountData.accAddressApt = accAddressApt;
        accountData.accRegDate = accRegDate;
        accountData.accPersonPassport = accPersonPassport;
        accountData.accPersonPhone = accPersonPhone;
        accountData.accPersonEmail = accPersonEmail;
        accountData.accComments = accComments;

        ArrayList<Integer> result = new NccAccounts().createAccount(accountData);
        ApiAccountData apiAccountData = new ApiAccountData();

        if (result.size() > 0) {
            apiAccountData.data = new ArrayList<>();
            apiAccountData.data.add(accountData);
            apiAccountData.status = 1;
            apiAccountData.message = "Account created";

            return apiAccountData;
        }

        apiAccountData.data = new ArrayList<>();
        apiAccountData.status = 0;
        apiAccountData.message = "Error creating account";

        return apiAccountData;
    }
}
