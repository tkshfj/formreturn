package com.ebstrada.formreturn.manager.gef.properties;

import java.beans.PropertyDescriptor;
import java.util.HashMap;

public class PropCategoryManager {

    protected static HashMap<String, HashMap<String, String>> _categories = new HashMap<String, HashMap<String, String>>();

    public static void categorizeProperty(String catName, String propName) {
        HashMap<String, String> cat = _categories.get(catName);
        if (cat == null) {
            cat = new HashMap<String, String>();
        }
        cat.put(propName, propName);
        _categories.put(catName, cat);
    }

    public static void categorizeProperty(String catName, PropertyDescriptor pd) {
        categorizeProperty(catName, pd.getName());
    }

    public static boolean inCategory(String catName, PropertyDescriptor pd) {
        return inCategory(catName, pd.getName());
    }

    public static boolean inCategory(String catName, String propName) {
        if ("All".equals(catName)) {
            return true;
        }
        HashMap<String, String> cat = _categories.get(catName);
        if (cat == null) {
            return false;
        }
        return cat.containsKey(propName);
    }

}
