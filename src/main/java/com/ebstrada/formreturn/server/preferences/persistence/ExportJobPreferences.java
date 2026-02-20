package com.ebstrada.formreturn.server.preferences.persistence;

import com.ebstrada.formreturn.manager.logic.export.ExportOptions;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("exportJobPreferences") public class ExportJobPreferences
    extends TaskSchedulerJobPreferences {

    private ExportOptions exportOptions;

    public ExportOptions getExportOptions() {
        return exportOptions;
    }

    public void setExportOptions(ExportOptions exportOptions) {
        this.exportOptions = exportOptions;
    }

}
