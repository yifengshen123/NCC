package com.NccIptvManager;

import java.io.BufferedReader;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by root on 27.09.16.
 */
public class ActiveChannel {
    public Integer id;
    public Integer transponderId;
    public ChannelData channelData;
    public Process process;
    public BufferedReader reader;
    public Integer bitrate;
    public Timer timer;
    public TimerTask timerTask;
    public Integer scrambledCount;
    public Integer ccCount;
}
