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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;

import com.fiberhome.vapp.inspector.gui.IconResources;
import com.fiberhome.vapp.inspector.manager.ZkNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CGSmithe
 * 
 */
public class NodeViewerData extends AbstractNodeViewer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeViewerData.class);

    private ZkNodeService zkNodeService;

    private final JTextPane dataArea;

    private final JToolBar toolbar;

    private String selectedNode;

    /**
     * 
     */
    public NodeViewerData() {
        this.setLayout(new BorderLayout());
        this.dataArea = new JTextPane();
        this.toolbar = new JToolBar();
        this.toolbar.setFloatable(false);
        JScrollPane scroller = new JScrollPane(this.dataArea);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scroller, BorderLayout.CENTER);
        this.add(this.toolbar, BorderLayout.NORTH);
        JButton saveButton = new JButton(IconResources.getSaveIcon());
        saveButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (selectedNode != null) {
                    if (JOptionPane.showConfirmDialog(NodeViewerData.this,
                            "Are you sure you want to save this node?" + " (this action cannot be reverted)",
                            "Confirm Save", JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                        zkNodeService.setData(selectedNode, dataArea.getText());
                    }
                }
            }
        });
        this.toolbar.add(saveButton);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.zookeeper.inspector.gui.nodeviewer.ZooInspectorNodeViewer#
     * getTitle()
     */
    @Override
    public String getTitle() {
        return "Node Data";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.zookeeper.inspector.gui.nodeviewer.ZooInspectorNodeViewer#
     * nodeSelectionChanged(java.util.Set)
     */
    @Override
    public void nodeSelectionChanged(List<String> selectedNodes) {
        if (selectedNodes.size() > 0) {
            this.selectedNode = selectedNodes.get(0);
            SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {

                @Override
                protected String doInBackground() throws Exception {
                    return NodeViewerData.this.zkNodeService.getData(NodeViewerData.this.selectedNode);
                }

                @Override
                protected void done() {
                    String data = "";
                    try {
                        data = get();
                    } catch (InterruptedException e) {
                        LOGGER.error("Error retrieving data for node: " + NodeViewerData.this.selectedNode, e);
                    } catch (ExecutionException e) {
                        LOGGER.error("Error retrieving data for node: " + NodeViewerData.this.selectedNode, e);
                    }
                    NodeViewerData.this.dataArea.setText(data);
                }
            };
            worker.execute();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.zookeeper.inspector.gui.nodeviewer.ZooInspectorNodeViewer#
     * setZkNodeService
     * (org.apache.zookeeper.inspector.manager.ZooInspectorNodeManager)
     */
    @Override
    public void setZkNodeService(ZkNodeService zooInspectorManager) {
        this.zkNodeService = zooInspectorManager;
    }

}
