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
package org.geotoolkit.coverage.decorator;

import java.util.Collection;
import org.apache.sis.util.collection.TreeTable;
import org.geotoolkit.storage.DataNode;
import org.geotoolkit.storage.DefaultDataNode;
import org.geotoolkit.storage.StorageEvent;
import org.geotoolkit.storage.StorageListener;
import org.geotoolkit.storage.coverage.CoverageReference;
import org.geotoolkit.storage.coverage.PyramidalCoverageReference;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
final class DecoratorDataNode extends DefaultDataNode {
    private final DataNode base;
    private final DecoratorCoverageStore store;

    private StorageListener subListener = new StorageListener(){
        @Override
        public void structureChanged(StorageEvent event) {
            rebuildNodes();
            sendStructureEvent(event.copy(DecoratorDataNode.this));
        }
        @Override
        public void contentChanged(StorageEvent event) {
            sendStructureEvent(event.copy(DecoratorDataNode.this));
        }
    };

    DecoratorDataNode(DataNode node, final DecoratorCoverageStore store) {
        this.store = store;
        this.base = node;
        node.addStorageListener(new StorageListener.Weak(store, subListener));
        rebuildNodes();
    }

    private void rebuildNodes(){
        getChildren().clear();
        final Collection<TreeTable.Node> children = base.getChildren();
        for(TreeTable.Node n : children){
            if(n instanceof PyramidalCoverageReference){
                getChildren().add(new DecoratorPyramidalCoverageReference((CoverageReference)n, store));
            }else if(n instanceof CoverageReference){
                getChildren().add(new DecoratorCoverageReference((CoverageReference)n, store));
            }else if(n instanceof DataNode){
                getChildren().add(new DecoratorDataNode((DataNode)n, store));
            }
        }
    }

}
