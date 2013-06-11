/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.gui.swing.etl;

import java.util.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessingRegistry;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class JProcessTreeModel extends DefaultTreeModel {

    public JProcessTreeModel() {
        super(new DefaultMutableTreeNode());
    }

    @Override
    public DefaultMutableTreeNode getRoot() {
        return (DefaultMutableTreeNode) super.getRoot();
    }

    public List<ProcessingRegistry> getRegistries(){
        final List<ProcessingRegistry> registries = new ArrayList<ProcessingRegistry>();

        final DefaultMutableTreeNode root = getRoot();
        for(int i=0;i<root.getChildCount();i++){
            final DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
            registries.add((ProcessingRegistry)child.getUserObject());
        }

        return registries;

    }

    public void addRegistry(final ProcessingRegistry register){
        final List<ProcessingRegistry> registries = getRegistries();
        if(registries.contains(register)){
            return;
        }

        int insertIndex = Collections.binarySearch(registries, register, new Comparator<ProcessingRegistry>(){
            @Override
            public int compare(ProcessingRegistry o1, ProcessingRegistry o2) {
                final String name1 = String.valueOf(o1.getIdentification().getCitation().getTitle());
                final String name2 = String.valueOf(o2.getIdentification().getCitation().getTitle());
                if("chains".equals(name1)){
                    //chains always in first place.
                    return -1;
                }
                return name1.compareToIgnoreCase(name2);
            }

        });

        if(insertIndex < 0) insertIndex = 0;


        final DefaultMutableTreeNode root = getRoot();
        final DefaultMutableTreeNode candidate = new DefaultMutableTreeNode(register);
        root.insert(candidate, insertIndex);

        final List<ProcessDescriptor> descriptors = register.getDescriptors();
        Collections.sort(descriptors, new DescriptorComparator());
        final Map<String, ProcessDescriptor> candidateNode = new LinkedHashMap<String, ProcessDescriptor>();

        for(ProcessDescriptor desc : descriptors) {
            final String processName = desc.getIdentifier().getCode();
            findLeaf(candidateNode, desc, processName);
        }
        createHierarchy(candidate, candidateNode);

    }

    private void findLeaf(final Map<String, ProcessDescriptor> candidateNode, final ProcessDescriptor desc, final String processName) {

        final String[] processNameSplit = processName.split("\\.");
        String current = null;

        for (int i = 0; i < processNameSplit.length; i++) {

            if (current == null) {
                current = processNameSplit[i];
            } else {
                current = current + "." + processNameSplit[i];
            }

            if (!candidateNode.containsKey(current)) {

                if (i == processNameSplit.length-1) {
                    candidateNode.put(current, desc);
                } else {
                    candidateNode.put(current, null);
                }

            }
        }
    }

    private MutableTreeNode createHierarchy (final MutableTreeNode root, final Map<String, ProcessDescriptor> candidateNode) {

        final Map<String, MutableTreeNode> folders = new HashMap<String, MutableTreeNode>();
        for (Map.Entry<String, ProcessDescriptor> entry : candidateNode.entrySet()) {

            final String path = entry.getKey();
            boolean isLeaf = entry.getValue() != null;
            if (isLeaf) {
                if (path.contains(".")) {

                    final String parent = path.substring(0, path.lastIndexOf("."));
                    if (folders.containsKey(parent)) {

                        final MutableTreeNode node = new DefaultMutableTreeNode(entry.getValue());
                        final MutableTreeNode parentNode = folders.get(parent);
                        parentNode.insert(node, parentNode.getChildCount());
                    }

                } else {
                    root.insert(new DefaultMutableTreeNode(entry.getValue()), root.getChildCount());
                }

            } else {
                if (path.contains(".")) {
                    final String parent = path.substring(0, path.lastIndexOf("."));
                    if (folders.containsKey(parent)) {

                        final String folderName = path.substring(path.lastIndexOf(".")+1, path.length());
                        final MutableTreeNode node = new DefaultMutableTreeNode(folderName);
                        final MutableTreeNode parentNode = folders.get(parent);
                        parentNode.insert(node, parentNode.getChildCount());

                        //add to map
                        folders.put(path, node);
                    }
                } else {
                    final MutableTreeNode node = new DefaultMutableTreeNode(path);
                    folders.put(path, node);
                    root.insert(node, root.getChildCount());
                }
            }
        }

        return  root;
    }

    class DescriptorComparator implements Comparator<ProcessDescriptor> {

        @Override
        public int compare(final ProcessDescriptor o1, final ProcessDescriptor o2) {
            final String name1 = String.valueOf(o1.getIdentifier().getCode());
            final String name2 = String.valueOf(o2.getIdentifier().getCode());
            return name1.compareToIgnoreCase(name2);
        }

    }

}
