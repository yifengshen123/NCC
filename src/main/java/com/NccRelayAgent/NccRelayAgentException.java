package com.NccRelayAgent;

public class NccRelayAgentException extends Exception {
    private String message;

    public NccRelayAgentException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
