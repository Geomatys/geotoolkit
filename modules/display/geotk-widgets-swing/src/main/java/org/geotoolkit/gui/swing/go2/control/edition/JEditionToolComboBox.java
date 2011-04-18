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

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class JEditionToolComboBox extends JComboBox {

    private Object edited = null;

    public JEditionToolComboBox() {
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

}
