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

import java.awt.Component;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.processing.chain.model.Constant;
import org.geotoolkit.processing.chain.model.ElementCondition;
import org.geotoolkit.processing.chain.model.ElementManual;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JOtherTreeCellRenderer extends DefaultTreeCellRenderer{

    private static final ImageIcon ICON_MANUAL_INTERVENTION = IconBundle.getIcon("16_conditional");
    private static final ImageIcon ICON_CONDITIONAL_ELEMENT = IconBundle.getIcon("16_conditional");

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        final JLabel lbl = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        DefaultMutableTreeNode parentNode = null;
        if(value instanceof DefaultMutableTreeNode){
            parentNode = (DefaultMutableTreeNode) ((DefaultMutableTreeNode)value).getParent();
            value = ((DefaultMutableTreeNode)value).getUserObject();
        }

        setIcon(null);
        setToolTipText(null);

        if(value instanceof ElementManual){
            lbl.setText(MessageBundle.format("manualInt"));
            lbl.setIcon(ICON_MANUAL_INTERVENTION);
        } else if(value instanceof ElementCondition) {
            lbl.setText(MessageBundle.format("conditionalTitle"));
            lbl.setIcon(ICON_CONDITIONAL_ELEMENT);
        }else if(value instanceof Constant) {
            lbl.setText(MessageBundle.format("constantTitle"));
            lbl.setIcon(ICON_CONDITIONAL_ELEMENT);
        } else if(value instanceof String) {
            lbl.setText((String) value);
            lbl.setIcon(null);
        }
        return lbl;
    }

}
