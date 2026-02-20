/*
 * Created on Jun 23, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.ebstrada.formreturn.manager.ui.tab;

import java.awt.event.MouseEvent;
import java.util.EventObject;

/**
 * @author David_211245
 * <p>
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class CloseTabbedPaneEvent extends EventObject {

    private String description;
    private MouseEvent e;
    private int overTabIndex;


    public CloseTabbedPaneEvent(MouseEvent e, String description, int overTabIndex) {
        super(e != null ? e.getSource() : new Object());
        this.e = e;
        this.description = description;
        this.overTabIndex = overTabIndex;
    }

    public String getDescription() {
        return description;
    }

    public MouseEvent getMouseEvent() {
        return e;
    }

    public int getOverTabIndex() {
        return overTabIndex;
    }
}
