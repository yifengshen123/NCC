package com.NccNetworkMonitor.API;

public class Event {

    private Trigger eventTrigger;

    public Event() {
        eventTrigger = new Trigger();
    }

    public Event(Trigger trigger) {
        this.eventTrigger = trigger;
    }

    public void fire() {
        System.out.println("Event fired on trigger '" + eventTrigger.getData().triggerName + "'");
    }
}
