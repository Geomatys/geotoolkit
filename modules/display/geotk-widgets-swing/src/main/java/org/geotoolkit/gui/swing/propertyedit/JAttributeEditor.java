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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.ArrayEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.BooleanEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.CRSEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.CharsetEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.ChoiceEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.DateEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.EnumEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.FileEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.FilterEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.InternationalStringEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.NumberEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.PropertyValueEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.StringEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.StyleEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.TimeStampEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.URLEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.UnitEditor;
import org.opengis.feature.Property;
import org.opengis.feature.type.PropertyType;

/**
 * Edit a single property.
 *
 * @author Johann Sorel (Geomatys)
 */
public class JAttributeEditor extends JPanel{

    private final List<PropertyValueEditor> editors = new CopyOnWriteArrayList<PropertyValueEditor>();
    private PropertyValueEditor editor;
    private Property property = null;

    public JAttributeEditor(){
        setLayout(new BorderLayout());
        editors.addAll(createDefaultEditorList());
    }

    public Property getProperty() {
        if(editor != null ){
            property.setValue(editor.getValue());
        }
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;

        removeAll();

        if(this.property != null){
            editor = getEditor(editors,this.property.getType());
            if(editor != null){
                editor.setValue(property.getType(), property.getValue());
                add(BorderLayout.CENTER,editor);
            }
        }

        revalidate();
        repaint();
    }

    /**
     * @return live list of property editors.
     */
    public List<PropertyValueEditor> getEditors() {
        return editors;
    }

    public static List<PropertyValueEditor> createDefaultEditorList(){
        final List<PropertyValueEditor> lst = new ArrayList<PropertyValueEditor>();
        lst.add(new ChoiceEditor());
        lst.add(new BooleanEditor());
        lst.add(new CRSEditor());
        lst.add(new CharsetEditor());
        lst.add(new NumberEditor());
        lst.add(new StringEditor());
        lst.add(new InternationalStringEditor());
        lst.add(new URLEditor());
        lst.add(new FileEditor());
        lst.add(new UnitEditor());
        lst.add(new EnumEditor());
        lst.add(new ArrayEditor());
        lst.add(new StyleEditor());
        lst.add(new FilterEditor());
        lst.add(new DateEditor());
        lst.add(new TimeStampEditor());
        return lst;
    }


    public static PropertyValueEditor getEditor(final Collection<? extends PropertyValueEditor> editors, PropertyType type){
        if(type != null){
            for(PropertyValueEditor edit : editors){
                if(edit instanceof ArrayEditor){
                    ((ArrayEditor)edit).setEditors(editors);
                }

                if(edit.canHandle(type)){
                    PropertyValueEditor newInst = edit.copy();

                    if(edit instanceof ArrayEditor){
                        ((ArrayEditor)edit).setEditors(editors);
                    }
                    return newInst;
                }
            }
        }
        return null;
    }

}
