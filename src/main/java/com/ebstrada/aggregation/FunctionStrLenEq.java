package com.ebstrada.aggregation;

import com.ebstrada.aggregation.exception.InvalidRulePartException;

public class FunctionStrLenEq extends AbstractFunction {
    
    private int stringLength;

    @Override
    public boolean match(Selection selectionValues) {
	if ( selectionValues.size() != 1 ) {
	    return negated ? true: false;
	}
	if (stringLength == selectionValues.get(0).length()) {
	    return negated ? false : true;
	} else {
	    return negated ? true: false;
	}
    }

    @Override
    public void parse(String conditionPartStr) throws InvalidRulePartException {
	conditionPartStr = conditionPartStr.strip();
	this.stringLength = parseIntFunctionParameter(conditionPartStr);
    }

}
