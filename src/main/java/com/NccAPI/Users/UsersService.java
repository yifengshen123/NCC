package com.NccAPI.Users;

import com.NccUsers.NccUserData;

import java.util.ArrayList;

/**
 * Created by seko on 18.01.2016.
 *
 */
public interface UsersService {
    NccUserData getUser(String login);
    ArrayList<NccUserData> getUsers();
    ArrayList<Integer> createUser(
            String userLogin,
            String userPassword,
            Integer userStatus,
            Integer userAccount,
            Long userIP);


}

