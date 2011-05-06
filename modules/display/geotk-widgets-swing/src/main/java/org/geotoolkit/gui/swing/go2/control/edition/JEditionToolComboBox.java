/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

package org.geotoolkit.gui.swing.go2.control.edition;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JEditionToolComboBox extends JComboBox {

    private Object edited = null;

    public JEditionToolComboBox() {
        setRenderer(new ToolRenderer());
    }

    public Object getEdited() {
        return edited;
    }

    public void setEdited(Object edited) {
        this.edited = edited;
        reloadModel();
    }

    @Override
    public EditionTool getSelectedItem() {
        return (EditionTool) super.getSelectedItem();
    }

    private void reloadModel(){
        final List<EditionTool> tools = EditionTools.getTools();
        final List<EditionTool> validTools = new ArrayList<EditionTool>();

        for(final EditionTool candidate : tools){
            if(candidate.canHandle(edited)){
                validTools.add(candidate);
            }
        }

        setModel(new ListComboBoxModel(validTools));
    }

    public class ToolRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
            final JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            lbl.setIcon(null);
            
            if(value instanceof EditionTool){
                final EditionTool tool = (EditionTool) value;
                lbl.setText(tool.getTitle().toString());
                lbl.setToolTipText(tool.getAbstract().toString());
                lbl.setIcon(tool.getIcon());
            }

            return lbl;
        }

    }

}
