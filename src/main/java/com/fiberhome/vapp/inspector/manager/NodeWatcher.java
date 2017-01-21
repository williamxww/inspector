package com.fiberhome.vapp.inspector.manager;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author vv
 * @since 2017/1/21.
 */
public class NodeWatcher implements Watcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeWatcher.class);

    private final String nodePath;

    private final NodeListener nodeListener;

    private final ZooKeeper zookeeper;

    private boolean closed = false;

    /**
     * @param nodePath
     * @param nodeListener
     * @param zookeeper
     * @throws InterruptedException
     * @throws KeeperException
     */
    public NodeWatcher(String nodePath, NodeListener nodeListener, ZooKeeper zookeeper)
            throws KeeperException, InterruptedException {
        this.nodePath = nodePath;
        this.nodeListener = nodeListener;
        this.zookeeper = zookeeper;
        Stat s = zookeeper.exists(nodePath, this);
        if (s != null) {
            zookeeper.getChildren(nodePath, this);
        }
    }

    public void process(WatchedEvent event) {
        if (!closed) {
            try {
                if (event.getType() != Watcher.Event.EventType.NodeDeleted) {
                    Stat s = zookeeper.exists(nodePath, this);
                    if (s != null) {
                        zookeeper.getChildren(nodePath, this);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error occured re-adding node watcherfor node " + nodePath, e);
            }
            nodeListener.processEvent(event.getPath(), event.getType().name(), null);
        }
    }

    /**
     *
     */
    public void stop() {
        this.closed = true;
    }

}
