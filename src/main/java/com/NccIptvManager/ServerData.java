package com.NccIptvManager;

import com.NccSystem.NccAbstractData;

import java.sql.SQLException;

public class ServerData extends NccAbstractData<ServerData> {
    public Integer id;
    public Long serverIP;
    public String serverSecret;
    public Long serverLocalAddress;
    public String serverComment;
    public String serverName;

    @Override
    public ServerData fillData(){
        ServerData serverData = new ServerData();

        try {
            serverData.id = rs.getInt("id");
            serverData.serverIP = rs.getLong("serverIP");
            serverData.serverSecret = rs.getString("serverSecret");
            serverData.serverLocalAddress = rs.getLong("serverLocalAddress");
            serverData.serverComment = rs.getString("serverComment");
            serverData.serverName = rs.getString("serverName");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return serverData;
    }
}
