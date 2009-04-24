/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.gui.swing.contexttree;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 * Selection manager for treetable
 * 
 * @author Johann Sorel
 */
class TreeSelectionManager implements javax.swing.event.TreeSelectionListener{
    
    private final JContextTree tree;
    private final EventListenerList listeners = new EventListenerList();    
    
    
    TreeSelectionManager(JContextTree tree){
        this.tree = tree;
    }

    
    /**
     * add treeListener to Model
     * @param ker the new listener
     */
    void addTreeSelectionListener(TreeSelectionListener ker) {
        listeners.add(TreeSelectionListener.class, ker);
    }

    /**
     * remove treeListener from Model
     * @param ker the listner to remove
     */
    void removeTreeSelectionListener(TreeSelectionListener ker) {
        listeners.remove(TreeSelectionListener.class, ker);
    }

    /**
     * get treeListeners list
     * @return the listener's table
     */
    TreeSelectionListener[] getTreeSelectionListeners() {
        return listeners.getListeners(TreeSelectionListener.class);
    }
    
    public void valueChanged(TreeSelectionEvent e) {
        fireEvent( (TreeSelectionEvent)e.cloneWithSource(tree) );
    }
    
    private void fireEvent(TreeSelectionEvent tse){
        TreeSelectionListener[] list = getTreeSelectionListeners();
        for (TreeSelectionListener tsl : list) {
            tsl.valueChanged(tse);
        }
    }
    
    
    
    
}
