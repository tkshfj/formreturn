package com.ebstrada.formreturn.manager.gef.util;

import java.awt.Rectangle;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;

/**
 * Predicate that returns true for Fig's that intersect the rectangle given in
 * the constructor. Useful for making a EnumerationPredicate that finds Figs
 * intersecting a given Rectangle.
 */
public class PredFigInRect implements Predicate {

    // //////////////////////////////////////////////////////////////
    // instance variables

    Rectangle _r;

    // //////////////////////////////////////////////////////////////
    // constructor

    public PredFigInRect(Rectangle r) {
        _r = r;
    }

    // //////////////////////////////////////////////////////////////
    // Predicate API

    public boolean predicate(Object o) {
        if (!(o instanceof Fig fig)) {
            return false;
        }
        return fig.intersects(_r);
    }
}
