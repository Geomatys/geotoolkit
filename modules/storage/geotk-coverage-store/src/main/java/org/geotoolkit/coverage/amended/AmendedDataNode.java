/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.coverage.amended;

import java.util.Collection;
import org.apache.sis.util.collection.TreeTable;
import org.geotoolkit.storage.DataNode;
import org.geotoolkit.storage.DefaultDataNode;
import org.geotoolkit.storage.StorageEvent;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.storage.coverage.PyramidalCoverageReference;

/**
 * Wrap a DataNode and it's children.
 * 
 * @author Johann Sorel (Geomatys)
 */
final class AmendedDataNode extends DefaultDataNode {
    private final DataNode base;
    private final AmendedCoverageStore store;

    /**
     * Listen to the real node events and propage them.
     */
    private final StorageListener subListener = new StorageListener(){
        @Override
        public void structureChanged(StorageEvent event) {
            rebuildNodes();
            sendStructureEvent(event.copy(AmendedDataNode.this));
        }
        @Override
        public void contentChanged(StorageEvent event) {
            sendStructureEvent(event.copy(AmendedDataNode.this));
        }
    };

    AmendedDataNode(DataNode node, final AmendedCoverageStore store) {
        this.store = store;
        this.base = node;
        node.addStorageListener(new StorageListener.Weak(store, subListener));
        rebuildNodes();
    }

    /**
     * Wrap node children.
     */
    private void rebuildNodes(){
        if(!getChildren().isEmpty()) getChildren().clear();
        final Collection<TreeTable.Node> children = base.getChildren();
        for(TreeTable.Node n : children){
            if(n instanceof PyramidalCoverageReference){
                //TODO : create an amended reference which declares itself as a pyramid.
                getChildren().add(new AmendedCoverageReference((CoverageReference)n, store));
            }else if(n instanceof CoverageReference){
                getChildren().add(new AmendedCoverageReference((CoverageReference)n, store));
            }else if(n instanceof DataNode){
                getChildren().add(new AmendedDataNode((DataNode)n, store));
            }
        }
    }

}
