package com.NccAPI.UserAccounts;

import com.NccAccounts.AccountData;

import java.util.ArrayList;
import java.util.Date;

public interface AccountsService {
    AccountData getAccount(String login, String key, Integer id);
    ApiAccountData getAccounts(String login, String key);
    ApiAccountData getAdministrators(String login, String key);
    ArrayList<Integer> createAccount(String login, String key,
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
                                     String accComments);
}
