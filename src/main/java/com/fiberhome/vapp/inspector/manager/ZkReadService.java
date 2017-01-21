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
package com.fiberhome.vapp.inspector.manager;

import java.util.List;
import java.util.Map;

/**
 * @author CGSmithe
 * 
 */
public interface ZkReadService {

    /**
     * @param nodePath
     * @return the data for the node
     */
    String getData(String nodePath);

    /**
     * @param nodePath
     * @return the metaData for the node
     */
    Map<String, String> getNodeMeta(String nodePath);

    /**
     * @param nodePath
     * @return the ACLs set on the node
     */
    List<Map<String, String>> getACLs(String nodePath);

    /**
     * @return the metaData for the current session
     */
    Map<String, String> getSessionMeta();

    /**
     * @param nodePath
     * @return true if the node has children
     */
    boolean hasChildren(String nodePath);

    /**
     * @param nodePath
     * @return the index of the node within its siblings
     */
    int getNodeIndex(String nodePath);

    /**
     * @param nodePath
     * @return the number of children of the node
     */
    int getNumChildren(String nodePath);

    /**
     * @param nodePath
     * @param childIndex
     * @return the path to the node for the child of the nodePath at childIndex
     */
    String getNodeChild(String nodePath, int childIndex);

    /**
     * @param nodePath
     * @return true if the node allows children nodes
     */
    boolean isAllowsChildren(String nodePath);

    /**
     * @param nodePath
     * @return a {@link List} of the children of the node
     */
    List<String> getChildren(String nodePath);

}