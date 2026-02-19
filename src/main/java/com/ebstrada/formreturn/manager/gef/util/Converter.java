package com.ebstrada.formreturn.manager.gef.util;

/**
 * This Class is a utility to convert java.util.*-classes to java.util.*-classes
 */

public class Converter {

    public static java.util.HashMap<Object, Object> convert(java.util.HashMap<Object, Object> oldOne) {
        if (oldOne == null) {
            return null;
        }
        java.util.HashMap<Object, Object> newOne = new java.util.HashMap<Object, Object>();
        for (Object key : oldOne.keySet()) {
            newOne.put(key, oldOne.get(key));
        }
        return newOne;
    }

    public static java.util.Vector convert(java.util.Vector oldOne) {
        if (oldOne == null) {
            return null;
        }
        java.util.Vector newOne = new java.util.Vector();
        for (int i = 0; i < oldOne.size(); i++) {
            newOne.addElement(oldOne.elementAt(i));
        }
        return newOne;
    }

    public static java.util.Vector convertCollection(java.util.Collection oldCol) {
        if (oldCol == null) {
            return null;
        }

        java.util.Vector newVec = new java.util.Vector();
        java.util.Iterator iter = oldCol.iterator();
        while (iter.hasNext()) {
            newVec.addElement(iter.next());
        }
        return newVec;
    }
}
/* end class Converter */
