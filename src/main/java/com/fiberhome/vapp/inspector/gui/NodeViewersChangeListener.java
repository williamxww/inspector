package com.fiberhome.vapp.inspector.gui;

import com.fiberhome.vapp.inspector.gui.nodeviewer.AbstractNodeViewer;

import java.util.List;

public interface NodeViewersChangeListener {
    /**
     * @param newViewers newViewers
     */
    void nodeViewersChanged(List<AbstractNodeViewer> newViewers);
}
