package com.ebstrada.formreturn.server.quartz.job;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ebstrada.formreturn.server.preferences.persistence.CustomJobPreferences;

public class CustomJob extends TaskSchedulerJob {

    // DO NOT REMOVE THE DEFAULT CONSTRUCTOR - IT IS REQUIRED FOR QUARTZ!
    public CustomJob() {
        super();
    }

    public CustomJob(CustomJobPreferences jobPreferences) {
        super(jobPreferences);
    }

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        System.out.println(jobExecutionContext.getJobDetail().getKey().toString());
    }

}
