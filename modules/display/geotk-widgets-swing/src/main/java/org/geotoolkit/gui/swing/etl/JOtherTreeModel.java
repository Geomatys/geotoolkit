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

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.geotoolkit.process.chain.model.Constant;
import org.geotoolkit.process.chain.model.ElementCondition;
import org.geotoolkit.process.chain.model.ElementManual;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class JOtherTreeModel extends DefaultTreeModel {

    public JOtherTreeModel() {
        super(new DefaultMutableTreeNode());

        final DefaultMutableTreeNode root = getRoot();
        final DefaultMutableTreeNode constantNode = new DefaultMutableTreeNode(new Constant(-1, String.class, ""));
        final DefaultMutableTreeNode conditionNode = new DefaultMutableTreeNode(new ElementCondition());
        final DefaultMutableTreeNode manualNode = new DefaultMutableTreeNode(new ElementManual());
        root.add(constantNode);
        root.add(conditionNode);
        root.add(manualNode);
    }

    @Override
    public final DefaultMutableTreeNode getRoot() {
        return (DefaultMutableTreeNode) super.getRoot();
    }
}
