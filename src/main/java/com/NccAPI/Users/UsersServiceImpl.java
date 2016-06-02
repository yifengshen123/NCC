package com.NccAPI.Users;

import com.NccUsers.NccUsers;
import com.NccUsers.NccUsersException;
import com.NccUsers.NccUserData;

import java.util.ArrayList;

public class UsersServiceImpl implements UsersService {

    public NccUserData getUser(String login) {
        System.out.println("getUser: '" + login + "'");

        try {
            return new NccUsers().getUser(login);
        } catch (NccUsersException e) {
            System.out.println("API: user not found: '" + login + "'");
        }

        return null;
    }

    public ArrayList<NccUserData> getUsers() {
        try {
            return new NccUsers().getUsers();
        } catch (NccUsersException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Integer> createUser(
            String userLogin,
            String userPassword,
            Integer userStatus,
            Integer userAccount,
            Integer userIP) {

        System.out.println("API: createUser: " + userLogin);

        NccUserData userData = new NccUserData();
        userData.userLogin = userLogin;
        userData.userPassword = userPassword;
        userData.userStatus = userStatus;
        userData.accountId = userAccount;
        userData.userIP = userIP;

        try {
            return new NccUsers().createUser(userData);
        } catch (NccUsersException e) {
            e.printStackTrace();
        }
        return null;
    }
}
