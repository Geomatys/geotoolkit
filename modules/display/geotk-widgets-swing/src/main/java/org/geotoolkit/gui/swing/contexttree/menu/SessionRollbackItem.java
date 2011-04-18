/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2011, Johann Sorel
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
package org.geotoolkit.gui.swing.contexttree.menu;

import java.awt.Component;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.geotoolkit.gui.swing.contexttree.AbstractTreePopupItem;
import org.geotoolkit.gui.swing.go2.control.edition.SessionRollbackAction;
import org.geotoolkit.map.FeatureMapLayer;

/**
 * session rollback item for JContextTree
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class SessionRollbackItem extends AbstractTreePopupItem{

    private final SessionRollbackAction action;

    /**
     * Session rollback for jcontexttree
     */
    public SessionRollbackItem(){
        super(new SessionRollbackAction());
        action = (SessionRollbackAction) getAction();
    }

    @Override
    public boolean isValid(final TreePath[] selection) {
        return uniqueAndType(selection,FeatureMapLayer.class);
    }

    @Override
    public Component getComponent(final TreePath[] selection) {
        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) selection[0].getLastPathComponent();
        final FeatureMapLayer layer = (FeatureMapLayer) node.getUserObject();
        action.setLayer(layer);
        return this;
    }

}
