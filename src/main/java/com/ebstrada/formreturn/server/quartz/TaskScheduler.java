package com.ebstrada.formreturn.server.quartz;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.JobExecutionContext;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ebstrada.formreturn.server.preferences.ServerPreferencesManager;
import com.ebstrada.formreturn.server.preferences.persistence.CustomJobPreferences;
import com.ebstrada.formreturn.server.preferences.persistence.ExportJobPreferences;
import com.ebstrada.formreturn.server.preferences.persistence.ImageFolderMonitorJobPreferences;
import com.ebstrada.formreturn.server.preferences.persistence.SourceDataFolderMonitorJobPreferences;
import com.ebstrada.formreturn.server.preferences.persistence.TaskSchedulerJobPreferences;
import com.ebstrada.formreturn.server.preferences.persistence.TaskSchedulerPreferences;
import com.ebstrada.formreturn.server.preferences.persistence.VacuumJobPreferences;
import com.ebstrada.formreturn.server.quartz.job.CustomJob;
import com.ebstrada.formreturn.server.quartz.job.ExportJob;
import com.ebstrada.formreturn.server.quartz.job.ImageFolderMonitorJob;
import com.ebstrada.formreturn.server.quartz.job.SourceDataFolderMonitorJob;
import com.ebstrada.formreturn.server.quartz.job.TaskSchedulerJob;
import com.ebstrada.formreturn.server.quartz.job.VacuumJob;

public class TaskScheduler {

    private Scheduler scheduler;

    private TaskSchedulerPreferences preferences;

    private List<TaskSchedulerJob> jobList = new ArrayList<>();

    public TaskScheduler() throws SchedulerException {
        scheduler =
            new StdSchedulerFactory("com/ebstrada/formreturn/server/quartz/quartz.properties")
                .getScheduler();
        scheduler.startDelayed(5);
        loadPreferences();
    }

    private void loadPreferences() {
        preferences = ServerPreferencesManager.getTaskSchedulerPreferences();
    }

    public void restoreJobsFromPreferences() throws SchedulerException {
        if (this.preferences == null || this.preferences.getJobPreferences() == null) {
            return;
        }
        for (TaskSchedulerJobPreferences jobPreferences : preferences.getJobPreferences()) {
            TaskSchedulerJob job = createJob(jobPreferences);
            loadJob(job);
            if (jobPreferences.isAutoStart()) {
                startJob(job);
            }
        }
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void start() throws SchedulerException {
        scheduler.start();
    }

    public void stop() throws SchedulerException {
        scheduler.shutdown(true);
    }

    public void addJobToPreferences(TaskSchedulerJobPreferences taskSchedulerJobPreferences) {
        if (preferences.getJobPreferences().contains(taskSchedulerJobPreferences)) {
            return;
        }
        preferences.getJobPreferences().add(taskSchedulerJobPreferences);
    }

    public void replaceJobPreferences(TaskSchedulerJobPreferences oldprefs,
        TaskSchedulerJobPreferences newprefs) {
        if (!(preferences.getJobPreferences().contains(oldprefs))) {
            addJobToPreferences(newprefs);
        } else {
            ArrayList<TaskSchedulerJobPreferences> prefs = preferences.getJobPreferences();
            int index = prefs.indexOf(oldprefs);
            prefs.set(index, newprefs);
        }
    }

    public void removeJobFromPreferences(TaskSchedulerJobPreferences taskSchedulerJobPreferences) {
        if (!(preferences.getJobPreferences().contains(taskSchedulerJobPreferences))) {
            return;
        }
        preferences.getJobPreferences().remove(taskSchedulerJobPreferences);
    }

    public void stopJob(TaskSchedulerJob job) throws SchedulerException {
        TriggerKey triggerKey = TriggerKey.triggerKey(
            job.getJob().getKey().getName(), job.getJob().getKey().getGroup());
        JobKey jobKey = job.getJob().getKey();
        scheduler.unscheduleJob(triggerKey);
        scheduler.deleteJob(jobKey);
    }

    public void removeJob(TaskSchedulerJob job) throws SchedulerException {
        boolean currentlyRunning = false;
        try {
            List<JobExecutionContext> currentJobs = scheduler.getCurrentlyExecutingJobs();
            for (JobExecutionContext jec : currentJobs) {
                if (jec.getJobDetail().getKey().equals(job.getJob().getKey())) {
                    currentlyRunning = true;
                    break;
                }
            }
        } catch (SchedulerException e) { // NOSONAR - ignore if scheduler can't report running jobs
        }
        if (!currentlyRunning) {
            stopJob(job);
            this.jobList.remove(job);
            removeJobFromPreferences(job.getPreferences());
        } else {
            throw new SchedulerException("Cannot remove job that is currently running.");
        }
    }

    public TaskSchedulerJob createJob(TaskSchedulerJobPreferences jobPreferences) {
        TaskSchedulerJob job = null;
        if (jobPreferences instanceof ImageFolderMonitorJobPreferences) {
            job = new ImageFolderMonitorJob((ImageFolderMonitorJobPreferences) jobPreferences);
        } else if (jobPreferences instanceof SourceDataFolderMonitorJobPreferences) {
            job = new SourceDataFolderMonitorJob(
                (SourceDataFolderMonitorJobPreferences) jobPreferences);
        } else if (jobPreferences instanceof VacuumJobPreferences) {
            job = new VacuumJob((VacuumJobPreferences) jobPreferences);
        } else if (jobPreferences instanceof CustomJobPreferences) {
            job = new CustomJob((CustomJobPreferences) jobPreferences);
        } else if (jobPreferences instanceof ExportJobPreferences) {
            job = new ExportJob((ExportJobPreferences) jobPreferences);
        }
        return job;
    }

    public void loadJob(TaskSchedulerJob job) {
        if (job instanceof ImageFolderMonitorJob) {
            job.createJob(ImageFolderMonitorJob.class);
        } else if (job instanceof SourceDataFolderMonitorJob) {
            job.createJob(SourceDataFolderMonitorJob.class);
        } else if (job instanceof VacuumJob) {
            job.createJob(VacuumJob.class);
        } else if (job instanceof CustomJob) {
            job.createJob(CustomJob.class);
        } else if (job instanceof ExportJob) {
            job.createJob(ExportJob.class);
        }
        this.jobList.add(job);
    }

    public void startJob(TaskSchedulerJob job) throws SchedulerException {
        Set<JobKey> jobKeys = scheduler.getJobKeys(
            GroupMatcher.jobGroupEquals(job.getGUID() + "Group"));
        if (jobKeys != null && !jobKeys.isEmpty()) {
            scheduler.deleteJob(JobKey.jobKey(job.getGUID(), job.getGUID() + "Group"));
        }
        scheduler.scheduleJob(job.getJob(), job.getTrigger());
    }

    public List<TaskSchedulerJob> getJobList() {
        return jobList;
    }

    public void rescheduleJob(TaskSchedulerJob job, TaskSchedulerJobPreferences jobPreferences)
        throws SchedulerException, ParseException {
        TaskSchedulerJobPreferences oldprefs = job.getPreferences();
        Trigger nt = job.rescheduleJob(jobPreferences);
        scheduler.rescheduleJob(
            TriggerKey.triggerKey(job.getGUID() + "Trigger", job.getGUID() + "TriggerGroup"), nt);
        job.setTrigger(nt);
        replaceJobPreferences(oldprefs, jobPreferences);
    }

    public boolean isRunning() throws SchedulerException {
        return scheduler.isStarted();
    }

}
