package com.ebstrada.aggregation;

import com.ebstrada.aggregation.exception.InvalidRulePartException;

public class FunctionFactory {
    
    private static final String BLANK_PREFIX = "blank";
    
    private static final String STRING_LENGTH_EQUALS_PREFIX = "strleneq";
    
    private static final String NUMERIC_RANGE_BETWEEN_PREFIX = "range";
    
    public static AbstractFunction getFunction(String conditionPartStr) throws InvalidRulePartException {
	conditionPartStr = conditionPartStr.strip();
	boolean negated = false;
	int beginOffset = 2;
	if (conditionPartStr.strip().startsWith("!!!")) {
	    negated = true;
	    beginOffset = 3;
	}
	String call = conditionPartStr.substring(beginOffset, conditionPartStr.length() - 2);
	AbstractFunction function;
	if (call.toLowerCase().startsWith(BLANK_PREFIX)) {
	    function = new FunctionBlank();
	    function.parse(call);
	} else if (call.toLowerCase().startsWith(STRING_LENGTH_EQUALS_PREFIX)) {
	    function = new FunctionStrLenEq();
	    function.parse(call);
	} else if (call.toLowerCase().startsWith(NUMERIC_RANGE_BETWEEN_PREFIX)) {
	    function = new FunctionRangeBetween();
	    function.parse(call);
	} else {
	    throw new InvalidRulePartException();
	}
	if (negated) {
	    function.setNegated(true);
	}
	return function;
    }

}
