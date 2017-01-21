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
package com.fiberhome.vapp.inspector.gui;

import com.fiberhome.vapp.inspector.gui.nodeviewer.AbstractNodeViewer;
import com.fiberhome.vapp.inspector.manager.ZkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;

/**
 * @author CGSmithe
 * 
 */
public class InspectorPanel extends JPanel implements NodeViewersChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(InspectorPanel.class);

    private final JButton refreshButton;

    private final JButton disconnectButton;

    private final JButton connectButton;

    private final NodeViewersPanel nodeViewersPanel;

    private final InspectorTreeViewer treeViewer;

    private final ZkService zooInspectorManager;

    private final JButton addNodeButton;

    private final JButton deleteNodeButton;

    private final JButton nodeViewersButton;

    private final JButton aboutButton;

    private final List<NodeViewersChangeListener> listeners = new ArrayList<NodeViewersChangeListener>();

    {
        listeners.add(this);
    }

    /**
     * @param zkService
     */
    public InspectorPanel(final ZkService zkService) {
        this.zooInspectorManager = zkService;
        final ArrayList<AbstractNodeViewer> nodeViewers = new ArrayList<AbstractNodeViewer>();
        try {
            List<String> defaultNodeViewersClassNames = this.zooInspectorManager.getDefaultNodeViewerConfiguration();
            for (String className : defaultNodeViewersClassNames) {
                nodeViewers.add((AbstractNodeViewer) Class.forName(className).newInstance());
            }
        } catch (Exception ex) {
            LOGGER.error("Error loading default node viewers.", ex);
            JOptionPane.showMessageDialog(InspectorPanel.this,
                    "Error loading default node viewers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        nodeViewersPanel = new NodeViewersPanel(zkService, nodeViewers);
        treeViewer = new InspectorTreeViewer(zkService, nodeViewersPanel);
        this.setLayout(new BorderLayout());
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        connectButton = new JButton(IconResources.getConnectIcon());
        disconnectButton = new JButton(IconResources.getDisconnectIcon());
        refreshButton = new JButton(IconResources.getRefreshIcon());
        addNodeButton = new JButton(IconResources.getAddNodeIcon());
        deleteNodeButton = new JButton(IconResources.getDeleteNodeIcon());
        nodeViewersButton = new JButton(IconResources.getChangeNodeViewersIcon());
        aboutButton = new JButton(IconResources.getInformationIcon());
        toolbar.add(connectButton);
        toolbar.add(disconnectButton);
        toolbar.add(refreshButton);
        toolbar.add(addNodeButton);
        toolbar.add(deleteNodeButton);
        toolbar.add(nodeViewersButton);
        toolbar.add(aboutButton);
        aboutButton.setEnabled(true);
        connectButton.setEnabled(true);
        disconnectButton.setEnabled(false);
        refreshButton.setEnabled(false);
        addNodeButton.setEnabled(false);
        deleteNodeButton.setEnabled(false);
        nodeViewersButton.setEnabled(true);
        nodeViewersButton.setToolTipText("Change Node Viewers");
        aboutButton.setToolTipText("About ZooInspector");
        connectButton.setToolTipText("Connect");
        disconnectButton.setToolTipText("Disconnect");
        refreshButton.setToolTipText("Refresh");
        addNodeButton.setToolTipText("Add Node");
        deleteNodeButton.setToolTipText("Delete Node");
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ConnectionPropertiesDialog zicpd = new ConnectionPropertiesDialog(
                        zkService.getConnectionPropertiesTemplate(), InspectorPanel.this);
                zicpd.setVisible(true);
            }
        });
        disconnectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                disconnect();
            }
        });
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                treeViewer.refreshView();
            }
        });
        addNodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final List<String> selectedNodes = treeViewer.getSelectedNodes();
                if (selectedNodes.size() == 1) {
                    final String nodeName = JOptionPane.showInputDialog(InspectorPanel.this,
                            "Please Enter a name for the new node", "Create Node", JOptionPane.INFORMATION_MESSAGE);
                    if (nodeName != null && nodeName.length() > 0) {
                        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

                            @Override
                            protected Boolean doInBackground() throws Exception {
                                return InspectorPanel.this.zooInspectorManager.createNode(selectedNodes.get(0),
                                        nodeName);
                            }

                            @Override
                            protected void done() {
                                treeViewer.refreshView();
                            }
                        };
                        worker.execute();
                    }
                } else {
                    JOptionPane.showMessageDialog(InspectorPanel.this,
                            "Please select 1 parent node for the new node.");
                }
            }
        });
        deleteNodeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final List<String> selectedNodes = treeViewer.getSelectedNodes();
                if (selectedNodes.size() == 0) {
                    JOptionPane.showMessageDialog(InspectorPanel.this,
                            "Please select at least 1 node to be deleted");
                } else {
                    int answer = JOptionPane.showConfirmDialog(InspectorPanel.this,
                            "Are you sure you want to delete the selected nodes?" + "(This action cannot be reverted)",
                            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (answer == JOptionPane.YES_OPTION) {
                        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

                            @Override
                            protected Boolean doInBackground() throws Exception {
                                for (String nodePath : selectedNodes) {
                                    InspectorPanel.this.zooInspectorManager.deleteNode(nodePath);
                                }
                                return true;
                            }

                            @Override
                            protected void done() {
                                treeViewer.refreshView();
                            }
                        };
                        worker.execute();
                    }
                }
            }
        });
        nodeViewersButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                NodeViewersDialog nvd = new NodeViewersDialog(JOptionPane.getRootFrame(),
                        nodeViewers, listeners, zkService);
                nvd.setVisible(true);
            }
        });
        aboutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AboutDialog zicpd = new AboutDialog(JOptionPane.getRootFrame());
                zicpd.setVisible(true);
            }
        });
        JScrollPane treeScroller = new JScrollPane(treeViewer);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroller, nodeViewersPanel);
        splitPane.setResizeWeight(0.25);
        this.add(splitPane, BorderLayout.CENTER);
        this.add(toolbar, BorderLayout.NORTH);
    }

    /**
     * @param connectionProps
     */
    public void connect(final Properties connectionProps) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() throws Exception {
                return zooInspectorManager.connect(connectionProps);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        treeViewer.refreshView();
                        connectButton.setEnabled(false);
                        disconnectButton.setEnabled(true);
                        refreshButton.setEnabled(true);
                        addNodeButton.setEnabled(true);
                        deleteNodeButton.setEnabled(true);
                    } else {
                        JOptionPane.showMessageDialog(InspectorPanel.this, "Unable to connect to zookeeper", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (InterruptedException e) {
                    LOGGER.error("Error occurred while connecting to ZooKeeper server", e);
                } catch (ExecutionException e) {
                    LOGGER.error("Error occurred while connecting to ZooKeeper server", e);
                }
            }

        };
        worker.execute();
    }

    /**
     * 
     */
    public void disconnect() {
        disconnect(false);
    }

    /**
     * @param wait
     */
    public void disconnect(boolean wait) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() throws Exception {
                return zooInspectorManager.disconnect();
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        treeViewer.clearView();
                        connectButton.setEnabled(true);
                        disconnectButton.setEnabled(false);
                        refreshButton.setEnabled(false);
                        addNodeButton.setEnabled(false);
                        deleteNodeButton.setEnabled(false);
                    }
                } catch (InterruptedException e) {
                    LOGGER.error("Error occurred while disconnecting from ZooKeeper server", e);
                } catch (ExecutionException e) {
                    LOGGER.error("Error occurred while disconnecting from ZooKeeper server", e);
                }
            }

        };
        worker.execute();
        if (wait) {
            while (!worker.isDone()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    LOGGER.error("Error occurred while disconnecting from ZooKeeper server", e);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.apache.zookeeper.inspector.gui.NodeViewersChangeListener#
     * nodeViewersChanged(java.util.List)
     */
    public void nodeViewersChanged(List<AbstractNodeViewer> newViewers) {
        this.nodeViewersPanel.setNodeViewers(newViewers);
    }
}
