package com.ebstrada.formreturn.manager.logic.export.filter;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("excludeEmptyRecordsFilter") public class ExcludeEmptyRecordsFilter extends Filter {

    public ExcludeEmptyRecordsFilter() {
        super.setWhere("FORM_PAGE.PROCESSED_TIME IS NOT NULL");
    }

}
