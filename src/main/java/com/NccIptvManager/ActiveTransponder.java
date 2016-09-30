package com.NccIptvManager;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by root on 27.09.16.
 */
public class ActiveTransponder {
    public Integer id;
    public Process process;
    public File tmpFile;
    public BufferedReader reader;
    public ArrayList<ActiveChannel> channels;
    public Timer timer;
    public TimerTask timerTask;
    public Integer signal;
    public Integer snr;
    public Integer ber;
    public Integer unc;
}
