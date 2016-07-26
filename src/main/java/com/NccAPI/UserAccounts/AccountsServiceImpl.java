package com.NccAPI.UserAccounts;

import com.NccAccounts.AccountData;
import com.NccAccounts.NccAccounts;

import java.util.ArrayList;
import java.util.Date;

public class AccountsServiceImpl implements AccountsService {


    public AccountData getAccount(Integer id) {
        return new NccAccounts().getAccount(id);
    }

    public ApiAccountData getUserAccount() {
        ArrayList<AccountData> accountData = new NccAccounts().getAccount();
        ApiAccountData result = new ApiAccountData();
        result.data = accountData;
        return result;
    }

    public ArrayList<Integer> createAccount(Double accDeposit,
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
        AccountData accountData = new AccountData();

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

        return new NccAccounts().createAccount(accountData);
    }
}
