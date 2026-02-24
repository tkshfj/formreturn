package com.ebstrada.formreturn.manager.logic.publish;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

public class FormPublisherException extends Exception {

    private static final long serialVersionUID = 1L;

    private int error;

    private String overwriteFilename;

    private String unfoundFilename;

    public static final int INTERRUPTED = 0;
    public static final int NO_ENTITY_MANAGER = 1;
    public static final int NO_SOURCE_DATA_RECORDS_TO_PUBLISH = 2;
    public static final int CANNOT_OVERWITE_FILE = 3;
    public static final int CANNOT_FIND_FILE = 4;

    public FormPublisherException(int error) {
        this.error = error;
    }

    public String getMessage() {
        return getErrorTitle();
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getErrorTitle() {
        return switch (error) {
            case INTERRUPTED -> Localizer.localize("UI", "TransactionAbortedMessage");
            case NO_ENTITY_MANAGER -> Localizer.localize("UI", "NoEntityManagerMessage");
            case NO_SOURCE_DATA_RECORDS_TO_PUBLISH -> Localizer.localize("UI", "NoSourceDataRecordsToPublishMessage");
            case CANNOT_OVERWITE_FILE -> String.format(Localizer.localize("UI", "CannotOverwriteFileMessage"),
                overwriteFilename);
            case CANNOT_FIND_FILE -> String.format(Localizer.localize("UI", "CannotFindFileMessage"), unfoundFilename);
            default -> "";
        };
    }

    public void setOverwriteFilename(String overwriteFilename) {
        this.overwriteFilename = overwriteFilename;
    }

    public void setUnfoundFilename(String unfoundFilename) {
        this.unfoundFilename = unfoundFilename;
    }

}
