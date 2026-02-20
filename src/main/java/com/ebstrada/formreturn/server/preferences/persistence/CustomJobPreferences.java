package com.ebstrada.formreturn.server.preferences.persistence;

import java.util.HashMap;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("customJobPreferences") public class CustomJobPreferences
    extends TaskSchedulerJobPreferences {

    private HashMap<String, ?> customPreferences;

    public void setCustomPreferences(HashMap<String, ?> customPreferences) {
        this.customPreferences = customPreferences;
    }

    public HashMap<String, ?> getCustomPreferences() {
        return customPreferences;
    }

}
