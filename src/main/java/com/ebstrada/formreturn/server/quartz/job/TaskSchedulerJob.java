package com.ebstrada.formreturn.server.quartz.job;

import java.text.ParseException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.triggers.CronTriggerImpl;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.util.RandomGUID;
import com.ebstrada.formreturn.server.preferences.persistence.TaskSchedulerJobPreferences;
import com.ebstrada.formreturn.server.quartz.ITriggerTypes;
import com.ebstrada.formreturn.server.quartz.PauseAwareSimpleTrigger;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public abstract class TaskSchedulerJob implements Job {

    protected JobDetail job;

    protected TriggerState state;

    protected PauseAwareSimpleTrigger simpleTrigger;

    protected Trigger cronTrigger;

    protected TaskSchedulerJobPreferences preferences;

    protected final String GUID = (new RandomGUID()).toString();

    protected Class<?> clazz;

    private static final Logger logger = Logger.getLogger(TaskSchedulerJob.class);

    public TaskSchedulerJob() {
    }

    public TaskSchedulerJob(TaskSchedulerJobPreferences jobPreferences) {
        this.preferences = jobPreferences;
    }

    public int getTriggerType() {
        return preferences.getTriggerType();
    }

    public JobDetail getJob() {
        return job;
    }

    public Trigger getTrigger() {
        switch (getTriggerType()) {
            case ITriggerTypes.CRON_TRIGGER:
                return cronTrigger;
            case ITriggerTypes.SIMPLE_TRIGGER:
            default:
                return simpleTrigger;
        }
    }

    public void createJob(Class<?> clazz) {

        this.clazz = clazz;

        if (job == null) {
            job = JobBuilder.newJob(clazz)
                .withIdentity(GUID, GUID + "Group")
                .withDescription(preferences.getDescription())
                .build();
        }
        switch (getTriggerType()) {

            case ITriggerTypes.SIMPLE_TRIGGER:
                if (simpleTrigger == null) {
                    simpleTrigger =
                        new PauseAwareSimpleTrigger(GUID + "Trigger", GUID + "TriggerGroup");
                    long ctime = System.currentTimeMillis();
                    simpleTrigger.setJobName(GUID);
                    simpleTrigger.setJobGroup(GUID + "Group");
                    simpleTrigger.setStartTime(new Date(ctime));
                    simpleTrigger.setRepeatInterval(preferences.getInterval());
                    simpleTrigger.setRepeatCount(PauseAwareSimpleTrigger.REPEAT_INDEFINITELY);
                    simpleTrigger.setPriority(Trigger.DEFAULT_PRIORITY);
                }
                break;

            case ITriggerTypes.CRON_TRIGGER:
                if (cronTrigger == null) {
                    try {
                        cronTrigger = TriggerBuilder.newTrigger()
                            .withIdentity(GUID + "Trigger", GUID + "TriggerGroup")
                            .startAt(new Date(System.currentTimeMillis()))
                            .withPriority(Trigger.DEFAULT_PRIORITY)
                            .withSchedule(CronScheduleBuilder.cronSchedule(preferences.getCronExpression()))
                            .build();
                    } catch (Exception e) {
                        logger.warn(e.getLocalizedMessage(), e);
                    }
                }
                break;
        }
    }

    public Trigger rescheduleJob(TaskSchedulerJobPreferences jobPreferences) throws ParseException {
        this.preferences = jobPreferences;
        long ctime = System.currentTimeMillis();
        switch (getTriggerType()) {
            case ITriggerTypes.CRON_TRIGGER:
                Trigger ct = TriggerBuilder.newTrigger()
                    .withIdentity(GUID + "Trigger", GUID + "TriggerGroup")
                    .startAt(new Date(ctime))
                    .withPriority(Trigger.DEFAULT_PRIORITY)
                    .withSchedule(CronScheduleBuilder.cronSchedule(this.preferences.getCronExpression()))
                    .build();
                return ct;
            default:
            case ITriggerTypes.SIMPLE_TRIGGER:
                PauseAwareSimpleTrigger nt =
                    new PauseAwareSimpleTrigger(GUID + "Trigger", GUID + "TriggerGroup");
                nt.setJobName(GUID);
                nt.setJobGroup(GUID + "Group");
                nt.setStartTime(new Date(ctime));
                nt.setRepeatInterval(preferences.getInterval());
                nt.setRepeatCount(PauseAwareSimpleTrigger.REPEAT_INDEFINITELY);
                nt.setPriority(Trigger.DEFAULT_PRIORITY);
                return nt;
        }
    }

    public void setInterval(int repeatInterval) {
        preferences.setInterval(repeatInterval);
    }

    public int getInterval() {
        return preferences.getInterval();
    }

    public TaskSchedulerJobPreferences getPreferences() {
        return this.preferences;
    }

    public void setTrigger(Trigger trigger) {
        if (trigger instanceof PauseAwareSimpleTrigger) {
            this.simpleTrigger = (PauseAwareSimpleTrigger) trigger;
        } else {
            this.cronTrigger = trigger;
        }
    }

    public TriggerState getState() {
        return state;
    }

    public void setState(TriggerState state) {
        this.state = state;
    }

    public String getStateString(TriggerState state) {

        if (state == null) {
            return Localizer.localize("Server", "StateNotRunningText");
        }

        switch (state) {
            case BLOCKED:
            case NORMAL:
                return Localizer.localize("Server", "StateNormalText");

            case PAUSED:
                return Localizer.localize("Server", "StatePausedText");

            case ERROR:
                return Localizer.localize("Server", "StateErrorText");

            case COMPLETE:
                return Localizer.localize("Server", "StateCompleteText");
            case NONE:
                return Localizer.localize("Server", "StateNotRunningText");
            default:
                return Localizer.localize("Server", "StateNotRunningText");
        }

    }

    public String toString() {
        return "\"" + getJob().getDescription() + "\" - " + getStateString(this.state);
    }

    public String getGUID() {
        return GUID;
    }

}
