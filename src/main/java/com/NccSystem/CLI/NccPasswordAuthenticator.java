package com.NccSystem.CLI;

import com.NccUsers.NccUserData;
import com.NccUsers.NccUsers;
import com.NccUsers.NccUsersException;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

public class NccPasswordAuthenticator implements PasswordAuthenticator {
    @Override
    public boolean authenticate(String username, String password, ServerSession session) {

        // TODO: 26.09.16 Implement DB authentication
        if(username.equals("seko") && password.equals("CtrhtnysqRjl")) return true;

        return false;

        //return !username.equalsIgnoreCase("") && username.equalsIgnoreCase(password);
//        if(username.equals("")) return false;
//
//        try {
//            NccUserData userData = new NccUsers().getUser(username);
//
//            if(userData!=null){
//                if(userData.userPassword.equals(password)) return true;
//            }
//
//        } catch (NccUsersException e) {
//            e.printStackTrace();
//        }
//
//        return false;
    }
}

