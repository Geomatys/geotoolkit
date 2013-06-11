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
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessingRegistry;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class JProcessTreeCellRenderer extends DefaultTreeCellRenderer{

    private static final ImageIcon ICON_TOOLS = IconBundle.getIcon("16_tools");
    private static final ImageIcon ICON_TOOL = IconBundle.getIcon("16_tool");

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
        
        if(value instanceof ProcessingRegistry){
            final ProcessingRegistry registry = (ProcessingRegistry) value;
            final String name = registry.getIdentification().getCitation().getTitle().toString();
            lbl.setText(name);
            lbl.setIcon(ICON_TOOLS);

        }else if(value instanceof ProcessDescriptor){
            final ProcessDescriptor desc = (ProcessDescriptor) value;
            final String fullProcessName = desc.getDisplayName() != null ? desc.getDisplayName().toString() : desc.getIdentifier().getCode();
            //get the process name from last dot
            String processName = fullProcessName;
            if (fullProcessName.contains(".")) {
                processName = fullProcessName.substring(fullProcessName.lastIndexOf(".")+1, fullProcessName.length());
            }

            lbl.setText(processName);
            lbl.setToolTipText(String.valueOf(desc.getProcedureDescription().toString()));
            final String registryName = desc.getIdentifier().getAuthority().getTitle().toString();
            lbl.setIcon(ICON_TOOL);

        }else if(value instanceof String){
            lbl.setText((String) value);
            lbl.setIcon(ICON_TOOLS);
        }


        return lbl;
    }

}
