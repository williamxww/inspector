package com.fiberhome.vapp.inspector;

import com.fiberhome.vapp.inspector.gui.InspectorPanel;
import com.fiberhome.vapp.inspector.manager.ZkServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Inspector {

    private static final Logger LOGGER = LoggerFactory.getLogger(Inspector.class);

    /**
     * start app
     * @param args args
     */
    public static void main(String[] args) {
        LOGGER.debug("start inspector...");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame frame = new JFrame("VappInspector");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //总面板
            final InspectorPanel inspectorPanel = new InspectorPanel(new ZkServiceImpl());
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    super.windowClosed(e);
                    inspectorPanel.disconnect(true);
                }
            });

            frame.setContentPane(inspectorPanel);
            frame.setSize(900, 700);
            frame.setVisible(true);
        } catch (Exception e) {
            LOGGER.error("Error occurred loading Inspector", e);
            JOptionPane.showMessageDialog(null, "Inspector failed to start: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
