package com.NccNetworkMonitor.API;

import com.NccIptvManager.ChannelData;
import com.NccIptvManager.NccIptvManager;

/**
 * Created by root on 28.11.16.
 */
public class IPTV {

    public Integer getBitrate(Integer channelId){
        ChannelData channelData = new NccIptvManager().getChannelById(channelId);

        return channelData.currentBitrate;
    }
}
