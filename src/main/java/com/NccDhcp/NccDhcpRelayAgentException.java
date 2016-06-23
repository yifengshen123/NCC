package com.NccDhcp;

public class NccDhcpRelayAgentException extends Exception {
    private String message;

    public NccDhcpRelayAgentException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
