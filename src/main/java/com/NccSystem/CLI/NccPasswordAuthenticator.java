package com.NccSystem.CLI;

import com.NccUsers.NccUserData;
import com.NccUsers.NccUsers;
import com.NccUsers.NccUsersException;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

public class NccPasswordAuthenticator implements PasswordAuthenticator {
    @Override
    public boolean authenticate(String username, String password, ServerSession session) {

        return !username.equalsIgnoreCase("") && username.equalsIgnoreCase(password);
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

