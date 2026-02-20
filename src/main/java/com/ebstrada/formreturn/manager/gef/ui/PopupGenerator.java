package com.ebstrada.formreturn.manager.gef.ui;

import java.awt.event.MouseEvent;
import java.util.List;

public interface PopupGenerator {
    public List<Object> getPopUpActions(MouseEvent me);
}
