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

/**
 * @author CGSmithe
 * 
 */
public class Inspector {

    private static final Logger LOGGER = LoggerFactory.getLogger(Inspector.class);
    /**
     * @param args
     */
    public static void main(String[] args) {
        LOGGER.debug("start inspector...");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            JFrame frame = new JFrame("VappInspector");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
