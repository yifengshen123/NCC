package com.NccDhcp;

public class NccDhcpReceiveThread extends Thread {
    public NccDhcpReceiveThread(Runnable processor){
        super(processor);
        this.setName("NccDhcpReceiver");
    }
}
