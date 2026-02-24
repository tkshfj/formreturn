package com.ebstrada.aggregation;

import com.ebstrada.aggregation.exception.InvalidRulePartException;

import java.util.ArrayList;

public abstract class AbstractFunction implements IConditionPart {
    
    protected boolean negated = false;
    
    @Override
    public boolean isNegated() {
	return this.negated;
    }
    
    public int parseIntFunctionParameter(String conditionName) throws InvalidRulePartException {
	String intStr = conditionName.substring(conditionName.indexOf('(') + 1, conditionName.length() - 1);
	try {
	    return Integer.parseInt(intStr);
	} catch (Exception ex) {
	    throw new InvalidRulePartException(ex);
	}
    }
    
    public ArrayList<Double> parseRangeFunctionParameter(String rangeString) throws InvalidRulePartException {

	String rangeStr = rangeString.substring(rangeString.indexOf('(') + 1, rangeString.length() - 1);
	String[] rangeParts = rangeStr.split("\\.\\.");

	if ( rangeParts == null || rangeParts.length != 2 ) {
	    throw new InvalidRulePartException();
	}

	try {
	    double low;
	    if ( rangeParts[0].contains("/") ) {
		String[] divisionStr = rangeParts[0].split("/");
		double dividend = Double.parseDouble(divisionStr[0].strip());
		double divisor = Double.parseDouble(divisionStr[1].strip());
		low = dividend / divisor;
	    } else {
		low = Double.parseDouble(rangeParts[0].strip());
	    }
	    double high;
	    if ( rangeParts[1].contains("/") ) {
		String[] divisionStr = rangeParts[1].split("/");
		double dividend = Double.parseDouble(divisionStr[0].strip());
		double divisor = Double.parseDouble(divisionStr[1].strip());
		high = dividend / divisor;
	    } else {
		high = Double.parseDouble(rangeParts[1].strip());
	    }
	    ArrayList<Double> range = new ArrayList<Double>();
	    range.add(low);
	    range.add(high);
	    return range;
	} catch (Exception ex) {
	    throw new InvalidRulePartException(ex);
	}

    }
    
    @Override
    public void setNegated(boolean negated) {
	this.negated = negated;
    }
    
}
