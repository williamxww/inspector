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

import info.clearthought.layout.TableLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;

/**
 * @author CGSmithe
 * 
 */
public class AboutDialog extends JDialog {
    private static final Logger LOGGER = LoggerFactory.getLogger(AboutDialog.class);

    /**
     * @param frame
     * 
     */
    public AboutDialog(Frame frame) {
        super(frame);
        this.setLayout(new BorderLayout());
        this.setIconImage(IconResources.getInformationIcon().getImage());
        this.setTitle("About VappInspector");
        this.setModal(true);
        this.setAlwaysOnTop(true);
        this.setResizable(false);
        JPanel panel = new JPanel();
        panel.setLayout(new TableLayout(new double[] { 5, 800, 5 }, new double[] { 5, 170, 5 }));
        JEditorPane aboutPane = new JEditorPane();
        aboutPane.setEditable(false);
        aboutPane.setOpaque(false);
        java.net.URL aboutURL = AboutDialog.class.getResource("/about.html");
        try {
            aboutPane.setPage(aboutURL);
        } catch (IOException e) {
            LOGGER.error("Error loading about.html, file may be corrupt", e);
        }
        panel.add(aboutPane, "1,1");
        JPanel buttonsPanel = new JPanel();
        buttonsPanel
                .setLayout(new TableLayout(new double[] { TableLayout.FILL, TableLayout.PREFERRED, TableLayout.FILL },
                        new double[] { TableLayout.PREFERRED }));
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AboutDialog.this.dispose();
            }
        });
        buttonsPanel.add(okButton, "1,0");
        this.add(panel, BorderLayout.CENTER);
        this.add(buttonsPanel, BorderLayout.SOUTH);
        this.pack();
    }
}
