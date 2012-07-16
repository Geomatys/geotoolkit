/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.gui.swing.propertyedit;

import java.awt.BorderLayout;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.TableCellEditorRenderer;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.VersatileEditor;
import org.opengis.feature.Property;
import org.opengis.feature.type.PropertyType;

/**
 * Edit a single property.
 *
 * @author Johann Sorel (Geomatys)
 */
public class JAttributeEditor extends JPanel{

    private final List<JFeatureOutLine.PropertyEditor> editors = new CopyOnWriteArrayList<JFeatureOutLine.PropertyEditor>();
    private VersatileEditor editor;
    private TableCellEditorRenderer cellEditor;
    private Property property = null;

    public JAttributeEditor(){
        setLayout(new BorderLayout());
        editors.addAll(JFeatureOutLine.createDefaultEditorList());
    }

    public Property getProperty() {
        if(editor != null && cellEditor != null){
            property.setValue(cellEditor.getCellEditorValue());
        }

        return property;
    }

    public void setProperty(Property property) {
        this.property = property;

        removeAll();

        if(this.property != null){
            editor = (VersatileEditor) getEditor(this.property.getType());
            if(editor != null){
                cellEditor = editor.getWritingRenderer();
                cellEditor.setPropertyType(property.getType());
                cellEditor.setCellEditorValue(property.getValue());
                add(BorderLayout.CENTER,cellEditor.getComponent(null, true, 0, 0));
            }
        }

    }

    /**
     * @return live list of property editors.
     */
    public List<JFeatureOutLine.PropertyEditor> getEditors() {
        return editors;
    }

    private JFeatureOutLine.PropertyEditor getEditor(PropertyType type){
        if(type != null){
            for(JFeatureOutLine.PropertyEditor edit : editors){
                if(edit.canHandle(type)){
                    return edit;
                }
            }
        }
        return null;
    }

}
