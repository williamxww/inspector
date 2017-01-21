package com.fiberhome.vapp.inspector.gui.nodeviewer;

import com.fiberhome.vapp.inspector.manager.ZkNodeService;
import info.clearthought.layout.TableLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

/**
 * 将URL decode后显示出来
 * 
 * @author vv
 * @since 2017/1/21.
 */
public class NodeViewerContent extends AbstractNodeViewer {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeViewerContent.class);

    private JTextArea textArea;

    private String selectedNode;

    public NodeViewerContent() {
        this.setLayout(new TableLayout(new double[] { TableLayout.FILL },
                new double[] { 150, TableLayout.PREFERRED, TableLayout.PREFERRED }));
        this.textArea = new JTextArea();
        this.textArea.setLineWrap(true);
        JScrollPane scroller = new JScrollPane(textArea);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scroller, "0,0");
    }

    /**
     * @param nodeService
     */
    @Override
    public void setZkNodeService(ZkNodeService nodeService) {

    }

    /**
     * Called whenever the selected nodes in the tree view changes.
     *
     * @param selectedNodes
     *            - the nodes currently selected in the tree view
     */
    @Override
    public void nodeSelectionChanged(List<String> selectedNodes) {
        if (selectedNodes.size() > 0) {
            this.selectedNode = selectedNodes.get(0);
            String plainText = null;
            try {
                plainText = URLDecoder.decode(selectedNode, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            textArea.setText(plainText);
        }

    }

    /**
     * @return the title of the node viewer. this will be shown on the tab for
     *         this node viewer.
     */
    @Override
    public String getTitle() {
        return "content";
    }
}
