package com.fiberhome.vapp.inspector.gui;

import com.fiberhome.vapp.inspector.common.Toaster;
import com.fiberhome.vapp.inspector.manager.NodeListener;
import com.fiberhome.vapp.inspector.manager.ZkService;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class InspectorTreeViewer extends JPanel implements NodeListener {
    private final ZkService zkService;

    private final JTree tree;

    private final Toaster toasterManager;

    /**
     * @param zkService
     * @param listener
     */
    public InspectorTreeViewer(final ZkService zkService, TreeSelectionListener listener) {
        this.zkService = zkService;
        this.setLayout(new BorderLayout());

        //添加监听
        final JPopupMenu popupMenu = new JPopupMenu();
        final JMenuItem addNotify = new JMenuItem("Add Change Notification");
        this.toasterManager = new Toaster();
        this.toasterManager.setBorderColor(Color.BLACK);
        this.toasterManager.setMessageColor(Color.BLACK);
        this.toasterManager.setToasterColor(Color.WHITE);
        addNotify.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<String> selectedNodes = getSelectedNodes();
                zkService.addWatchers(selectedNodes, InspectorTreeViewer.this);
            }
        });
        final JMenuItem removeNotify = new JMenuItem("Remove Change Notification");
        removeNotify.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List<String> selectedNodes = getSelectedNodes();
                zkService.removeWatchers(selectedNodes);
            }
        });

        // 构造树
        tree = new JTree(new DefaultMutableTreeNode());
        tree.setCellRenderer(new ZooInspectorTreeCellRenderer());
        tree.setEditable(false);
        tree.getSelectionModel().addTreeSelectionListener(listener);
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.isPopupTrigger() || e.getButton() == MouseEvent.BUTTON3) {
                    // TODO only show add if a selected node isn't being
                    // watched, and only show remove if a selected node is being
                    // watched
                    popupMenu.removeAll();
                    popupMenu.add(addNotify);
                    popupMenu.add(removeNotify);
                    popupMenu.show(InspectorTreeViewer.this, e.getX(), e.getY());
                }
            }
        });
        this.add(tree, BorderLayout.CENTER);
    }

    /**
     * 
     */
    public void refreshView() {
        final Set<TreePath> expandedNodes = new LinkedHashSet<TreePath>();
        int rowCount = tree.getRowCount();
        for (int i = 0; i < rowCount; i++) {
            TreePath path = tree.getPathForRow(i);
            if (tree.isExpanded(path)) {
                expandedNodes.add(path);
            }
        }
        final TreePath[] selectedNodes = tree.getSelectionPaths();
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() throws Exception {
                tree.setModel(new DefaultTreeModel(new ZooInspectorTreeNode("/", null)));
                return true;
            }

            @Override
            protected void done() {
                for (TreePath path : expandedNodes) {
                    tree.expandPath(path);
                }
                tree.getSelectionModel().setSelectionPaths(selectedNodes);
            }
        };
        worker.execute();
    }

    /**
     * 
     */
    public void clearView() {
        tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode()));
    }

    /**
     * @author Colin
     * 
     */
    private class ZooInspectorTreeCellRenderer extends DefaultTreeCellRenderer {
        public ZooInspectorTreeCellRenderer() {
            setLeafIcon(IconResources.getTreeLeafIcon());
            setOpenIcon(IconResources.getTreeOpenIcon());
            setClosedIcon(IconResources.getTreeClosedIcon());
        }
    }

    /**
     * @author Colin
     * 
     */
    private class ZooInspectorTreeNode implements TreeNode {
        private final String nodePath;

        private final String nodeName;

        private final ZooInspectorTreeNode parent;

        public ZooInspectorTreeNode(String nodePath, ZooInspectorTreeNode parent) {
            this.parent = parent;
            this.nodePath = nodePath;
            int index = nodePath.lastIndexOf("/");
            if (index == -1) {
                throw new IllegalArgumentException("Invalid node path" + nodePath);
            }
            this.nodeName = nodePath.substring(index + 1);
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.tree.TreeNode#children()
         */
        public Enumeration<TreeNode> children() {
            List<String> children = zkService.getChildren(this.nodePath);
            Collections.sort(children);
            List<TreeNode> returnChildren = new ArrayList<TreeNode>();
            for (String child : children) {
                returnChildren.add(
                        new ZooInspectorTreeNode((this.nodePath.equals("/") ? "" : this.nodePath) + "/" + child, this));
            }
            return Collections.enumeration(returnChildren);
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.tree.TreeNode#getAllowsChildren()
         */
        public boolean getAllowsChildren() {
            return zkService.isAllowsChildren(this.nodePath);
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.tree.TreeNode#getChildAt(int)
         */
        public TreeNode getChildAt(int childIndex) {
            String child = zkService.getNodeChild(this.nodePath, childIndex);
            if (child != null) {
                return new ZooInspectorTreeNode((this.nodePath.equals("/") ? "" : this.nodePath) + "/" + child, this);
            }
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.tree.TreeNode#getChildCount()
         */
        public int getChildCount() {
            return zkService.getNumChildren(this.nodePath);
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
         */
        public int getIndex(TreeNode node) {
            return zkService.getNodeIndex(this.nodePath);
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.tree.TreeNode#getParent()
         */
        public TreeNode getParent() {
            return this.parent;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.tree.TreeNode#isLeaf()
         */
        public boolean isLeaf() {
            return !zkService.hasChildren(this.nodePath);
        }

        @Override
        public String toString() {
            return this.nodeName;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((nodePath == null) ? 0 : nodePath.hashCode());
            result = prime * result + ((parent == null) ? 0 : parent.hashCode());
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
            ZooInspectorTreeNode other = (ZooInspectorTreeNode) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (nodePath == null) {
                if (other.nodePath != null)
                    return false;
            } else if (!nodePath.equals(other.nodePath))
                return false;
            if (parent == null) {
                if (other.parent != null)
                    return false;
            } else if (!parent.equals(other.parent))
                return false;
            return true;
        }

        private InspectorTreeViewer getOuterType() {
            return InspectorTreeViewer.this;
        }

    }

    /**
     * @return {@link List} of the currently selected nodes
     */
    public List<String> getSelectedNodes() {
        TreePath[] paths = tree.getSelectionPaths();
        List<String> selectedNodes = new ArrayList<String>();
        if (paths != null) {
            for (TreePath path : paths) {
                StringBuilder sb = new StringBuilder();
                Object[] pathArray = path.getPath();
                for (Object o : pathArray) {
                    String nodeName = o.toString();
                    if (nodeName.length() > 0) {
                        sb.append("/");
                        sb.append(o.toString());
                    }
                }
                selectedNodes.add(sb.toString());
            }
        }
        return selectedNodes;
    }

    public void processEvent(String nodePath, String eventType, Map<String, String> eventInfo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Node: ");
        sb.append(nodePath);
        sb.append("\nEvent: ");
        sb.append(eventType);
        if (eventInfo != null) {
            for (Map.Entry<String, String> entry : eventInfo.entrySet()) {
                sb.append("\n");
                sb.append(entry.getKey());
                sb.append(": ");
                sb.append(entry.getValue());
            }
        }
        this.toasterManager.showToaster(IconResources.getInformationIcon(), sb.toString());
    }
}
