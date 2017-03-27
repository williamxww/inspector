/*
 * ZooInspector
 * 
 * Copyright 2010 Colin Goodheart-Smithe

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.fiberhome.vapp.inspector.gui.nodeviewer;

import com.fiberhome.vapp.inspector.manager.ZkNodeService;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import javax.swing.JPanel;

/**
 * 
 * @author CGSmithe
 * 
 */
public abstract class AbstractNodeViewer extends JPanel implements Transferable {
    /**
     * The {@link DataFlavor} used for DnD in the node viewer configuration
     * dialog
     */
    public static final DataFlavor nodeViewerDataFlavor = new DataFlavor(AbstractNodeViewer.class, "nodeviewer");

    /**
     * @param nodeService
     */
    public abstract void setZkNodeService(ZkNodeService nodeService);

    /**
     * Called whenever the selected nodes in the tree view changes.
     * 
     * @param selectedNodes - the nodes currently selected in the tree view
     * 
     */
    public abstract void nodeSelectionChanged(List<String> selectedNodes);

    /**
     * @return the title of the node viewer. this will be shown on the tab for
     *         this node viewer.
     */
    public abstract String getTitle();

    /**
     * (non-Javadoc)
     * 
     * @see java.awt.datatransfer.Transferable#getTransferData(DataFlavor)
     */
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(nodeViewerDataFlavor)) {
            return this.getClass().getCanonicalName();
        } else {
            return null;
        }
    }

    /**
     * 
     * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
     */
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { nodeViewerDataFlavor };
    }

    /**
     *
     * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(DataFlavor)
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(nodeViewerDataFlavor);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractNodeViewer other = (AbstractNodeViewer) obj;
        if (getClass().getCanonicalName() != other.getClass().getCanonicalName()) {
            return false;
        }
        if (getTitle() == null) {
            if (other.getTitle() != null)
                return false;
        } else if (!getTitle().equals(other.getTitle()))
            return false;
        return true;
    }
}
