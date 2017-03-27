package com.fiberhome.vapp.inspector.gui.nodeviewer;

import info.clearthought.layout.TableLayout;
import com.fiberhome.vapp.inspector.manager.ZkNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;


public class NodeViewerMetaData extends AbstractNodeViewer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeViewerMetaData.class);

    private ZkNodeService zooInspectorManager;

    private final JPanel metaDataPanel;

    private String selectedNode;

    public NodeViewerMetaData() {
        this.setLayout(new BorderLayout());
        this.metaDataPanel = new JPanel();
        this.metaDataPanel.setBackground(Color.WHITE);
        JScrollPane scroller = new JScrollPane(this.metaDataPanel);
        this.add(scroller, BorderLayout.CENTER);
    }

    @Override
    public String getTitle() {
        return "Node Metadata";
    }

    @Override
    public void nodeSelectionChanged(List<String> selectedNodes) {
        this.metaDataPanel.removeAll();
        if (selectedNodes.size() > 0) {
            this.selectedNode = selectedNodes.get(0);
            SwingWorker<Map<String, String>, Void> worker = new SwingWorker<Map<String, String>, Void>() {

                /**
                 * doInBackground方法作为任务线程的一部分执行，它负责完成线程的基本任务，并以返回值来作为线程的执行结果
                 * @return 任务线程的执行结果
                 * @throws Exception
                 */
                @Override
                protected Map<String, String> doInBackground() throws Exception {
                    return NodeViewerMetaData.this.zooInspectorManager
                            .getNodeMeta(NodeViewerMetaData.this.selectedNode);
                }

                /**
                 * 在doInBackground方法完成之后，SwingWorker调用done方法
                 */
                @Override
                protected void done() {
                    Map<String, String> data;
                    try {
                        //阻塞直到任务线程有结果，将其放在done()里肯定不会阻塞
                        data = get();
                    } catch (InterruptedException e) {
                        data = new HashMap();
                        LOGGER.error("Error retrieving meta data for node: " + NodeViewerMetaData.this.selectedNode, e);
                    } catch (ExecutionException e) {
                        data = new HashMap();
                        LOGGER.error("Error retrieving meta data for node: " + NodeViewerMetaData.this.selectedNode, e);
                    }
                    int numRows = data.size() * 2 + 1;
                    double[] rows = new double[numRows];
                    for (int i = 0; i < numRows; i++) {
                        if (i % 2 == 0) {
                            rows[i] = 5;
                        } else {
                            rows[i] = TableLayout.PREFERRED;
                        }
                    }
                    NodeViewerMetaData.this.metaDataPanel.setLayout(new TableLayout(
                            new double[] { 10, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 10 }, rows));
                    int i = 0;
                    for (Map.Entry<String, String> entry : data.entrySet()) {
                        int rowPos = 2 * i + 1;
                        JLabel label = new JLabel(entry.getKey());
                        JTextField text = new JTextField(entry.getValue());
                        text.setEditable(false);
                        NodeViewerMetaData.this.metaDataPanel.add(label, "1," + rowPos);
                        NodeViewerMetaData.this.metaDataPanel.add(text, "3," + rowPos);
                        i++;
                    }
                    NodeViewerMetaData.this.metaDataPanel.revalidate();
                    NodeViewerMetaData.this.metaDataPanel.repaint();
                }
            };
            worker.execute();
        }
    }

    @Override
    public void setZkNodeService(ZkNodeService zooInspectorManager) {
        this.zooInspectorManager = zooInspectorManager;
    }

}
