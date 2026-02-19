package com.ebstrada.formreturn.manager.gef.base;

import java.util.Map;

/**
 * This is the base interface for all modes in gef. A Mode is responsible for
 * handling most of the events that come to the Editor. A Mode defines a context
 * for interperting those events. Systems using GEF can define their own Modes
 * by subclassing from FigModifyingMode.
 *
 * @see ModeImpl
 * @see Cmd
 * @see FigModifyingMode
 */

public interface Mode {
    public void start();

    public void done();

    public void setArgs(Map args);

    public void setArg(String key, Object value);

    public Map getArgs();

    public Object getArg(String key);

    public boolean canExit();

    public void init(Map parameters);

} /* end interface Mode */
