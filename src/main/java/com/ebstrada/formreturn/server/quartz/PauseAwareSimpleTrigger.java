package com.ebstrada.formreturn.server.quartz;

import java.util.Date;

import org.quartz.TriggerKey;
import org.quartz.impl.triggers.SimpleTriggerImpl;

public class PauseAwareSimpleTrigger extends SimpleTriggerImpl {

    private static final long serialVersionUID = 1L;

    public PauseAwareSimpleTrigger(String name, String group) {
        super();
        setKey(new TriggerKey(name, group));
    }

    @Override public Date getNextFireTime() {
        Date nextFireTime = super.getNextFireTime();
        if (nextFireTime.getTime() < System.currentTimeMillis()) {
            // next fire time after now
            nextFireTime = super.getFireTimeAfter(null);
            super.setNextFireTime(nextFireTime);
        }
        return nextFireTime;
    }

}
