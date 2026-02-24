package com.ebstrada.aggregation;

import java.util.ArrayList;
import java.util.List;

public class Selection extends ArrayList<String> {

    public Selection(String[] selection) {
	if ( selection != null ) {
	    addAll(List.of(selection));
	}
    }

}
