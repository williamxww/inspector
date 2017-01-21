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

import javax.swing.ImageIcon;
import java.net.URL;

/**
 * @author CGSmithe
 * 
 */
public class IconResources {

    private static URL getResource(String fileName) {
        return ClassLoader.getSystemResource("icons/" + fileName);
    }

    /**
     * @return file icon
     */
    public static ImageIcon getTreeLeafIcon() {
        return new ImageIcon(getResource("file_obj.gif"));
    }

    /**
     * @return folder open icon
     */
    public static ImageIcon getTreeOpenIcon() {
        return new ImageIcon(getResource("fldr_obj.gif"));
    }

    /**
     * @return folder closed icon
     */
    public static ImageIcon getTreeClosedIcon() {
        return new ImageIcon(getResource("fldr_obj.gif"));
    }

    /**
     * @return connect icon
     */
    public static ImageIcon getConnectIcon() {
        return new ImageIcon(getResource("launch_run.gif"));
    }

    /**
     * @return disconnect icon
     */
    public static ImageIcon getDisconnectIcon() {
        return new ImageIcon(getResource("launch_stop.gif"));
    }

    /**
     * @return save icon
     */
    public static ImageIcon getSaveIcon() {
        return new ImageIcon(getResource("save_edit.gif"));
    }

    /**
     * @return add icon
     */
    public static ImageIcon getAddNodeIcon() {
        return new ImageIcon(getResource("new_con.gif"));
    }

    /**
     * @return delete icon
     */
    public static ImageIcon getDeleteNodeIcon() {
        return new ImageIcon(getResource("trash.gif"));
    }

    /**
     * @return refresh icon
     */
    public static ImageIcon getRefreshIcon() {
        return new ImageIcon(getResource("refresh.gif"));
    }

    /**
     * @return information icon
     */
    public static ImageIcon getInformationIcon() {
        return new ImageIcon(getResource("info_obj.gif"));
    }

    /**
     * @return node viewers icon
     */
    public static ImageIcon getChangeNodeViewersIcon() {
        return new ImageIcon(getResource("edtsrclkup_co.gif"));
    }

    /**
     * @return up icon
     */
    public static ImageIcon getUpIcon() {
        return new ImageIcon(getResource("search_prev.gif"));
    }

    /**
     * @return down icon
     */
    public static ImageIcon getDownIcon() {
        return new ImageIcon(getResource("search_next.gif"));
    }
}
