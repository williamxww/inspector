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

import info.clearthought.layout.TableLayout;
import com.fiberhome.vapp.inspector.manager.ZkNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

/**
 * @author CGSmithe
 * 
 */
public class NodeViewerACL extends AbstractNodeViewer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeViewerACL.class);

    private ZkNodeService zkNodeService;

    private final JPanel aclDataPanel;

    private String selectedNode;

    /**
     * 
     */
    public NodeViewerACL() {
        this.setLayout(new BorderLayout());
        this.aclDataPanel = new JPanel();
        this.aclDataPanel.setBackground(Color.WHITE);
        JScrollPane scroller = new JScrollPane(this.aclDataPanel);
        this.add(scroller, BorderLayout.CENTER);
    }

    @Override
    public String getTitle() {
        return "Node ACLs";
    }

    @Override
    public void nodeSelectionChanged(List<String> selectedNodes) {
        this.aclDataPanel.removeAll();
        if (selectedNodes.size() > 0) {
            this.selectedNode = selectedNodes.get(0);
            SwingWorker<List<Map<String, String>>, Void> worker = new SwingWorker<List<Map<String, String>>, Void>() {

                @Override
                protected List<Map<String, String>> doInBackground() throws Exception {
                    return zkNodeService.getACLs(selectedNode);
                }

                @Override
                protected void done() {
                    List<Map<String, String>> acls = null;
                    try {
                        acls = get();
                    } catch (InterruptedException e) {
                        acls = new ArrayList();
                        LOGGER.error("Error retrieving ACL Information for node: " + selectedNode,
                                e);
                    } catch (ExecutionException e) {
                        acls = new ArrayList();
                        LOGGER.error("Error retrieving ACL Information for node: " + selectedNode,
                                e);
                    }
                    int numRows = acls.size() * 2 + 1;
                    double[] rows = new double[numRows];
                    for (int i = 0; i < numRows; i++) {
                        if (i % 2 == 0) {
                            rows[i] = 5;
                        } else {
                            rows[i] = TableLayout.PREFERRED;
                        }
                    }
                    aclDataPanel.setLayout(new TableLayout(new double[] { 10, TableLayout.PREFERRED, 10 }, rows));
                    int j = 0;
                    for (Map<String, String> data : acls) {
                        int rowPos = 2 * j + 1;
                        JPanel aclPanel = new JPanel();
                        aclPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        aclPanel.setBackground(Color.WHITE);
                        int numRowsACL = data.size() * 2 + 1;
                        double[] rowsACL = new double[numRowsACL];
                        for (int i = 0; i < numRowsACL; i++) {
                            if (i % 2 == 0) {
                                rowsACL[i] = 5;
                            } else {
                                rowsACL[i] = TableLayout.PREFERRED;
                            }
                        }
                        aclPanel.setLayout(new TableLayout(
                                new double[] { 10, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 10 }, rowsACL));
                        int i = 0;
                        for (Map.Entry<String, String> entry : data.entrySet()) {
                            int rowPosACL = 2 * i + 1;
                            JLabel label = new JLabel(entry.getKey());
                            JTextField text = new JTextField(entry.getValue());
                            text.setEditable(false);
                            aclPanel.add(label, "1," + rowPosACL);
                            aclPanel.add(text, "3," + rowPosACL);
                            i++;
                        }
                        aclDataPanel.add(aclPanel, "1," + rowPos);
                    }
                    aclDataPanel.revalidate();
                    aclDataPanel.repaint();
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
