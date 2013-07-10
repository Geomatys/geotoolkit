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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
import org.apache.sis.util.iso.ResourceInternationalString;
import org.opengis.feature.Property;
import org.opengis.feature.type.PropertyType;
import org.opengis.util.InternationalString;

/**
 * Edit a single property. 
 * When value in property editor change, JAttributeEditor will fire a 
 * property change event with property name <code>#VALUE_CHANGE_EVENT</code>
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys)
 */
public class JAttributeEditor extends JPanel implements PropertyChangeListener, FocusListener {

    //Event name fired in PropertyChange
    public static final String VALUE_CHANGE_EVENT = "value";
    
    /*
     * Not supported text gived to notSupported TextField.
     */
    private static final String BUNDLE_PATH = "org/geotoolkit/gui/swing/resource/Bundle";
    private static final String NOT_SUPPORTED_KEY = "notSupported";
    private static final InternationalString NOT_SUPPORTED = new ResourceInternationalString(BUNDLE_PATH, NOT_SUPPORTED_KEY);
    
    private final JTextField notSupportedTF = new JTextField(NOT_SUPPORTED.toString());
    private final List<PropertyValueEditor> editors = new CopyOnWriteArrayList<PropertyValueEditor>();
    private PropertyValueEditor editor;
    private Property property = null;
    private boolean useProvidedEditor;

    public JAttributeEditor(){
      this(null);
    }

    /**
     * Constructor with a provided editor in parameter.
     * If provided editor is not null, JAttributeEditor will by-pass 
     * editor search from available editors and use it.
     * If provided editor is null JAttributeEditor will have a standard behavior.
     * 
     * @param providedEditor editor to use regardless of property set. Can be null.
     */
    public JAttributeEditor(final PropertyValueEditor providedEditor){
        setLayout(new BorderLayout());
        if (providedEditor == null) {
            editors.addAll(createDefaultEditorList());
            notSupportedTF.setEnabled(false);
            useProvidedEditor = false;
        } else {
            editor = providedEditor;
            useProvidedEditor = true;
        }
    }
    
    public Property getProperty() {
        if(editor != null ){
            property.setValue(editor.getValue());
        }
        return property;
    }

    public void setProperty(Property property) {
        
        this.property = property;
        if (editor != null) {
            editor.removePropertyChangeListener(this);
        }
        removeAll();
            
        if (useProvidedEditor) {
            
            if (this.property != null) {
                editor.setValue(property.getType(), property.getValue());
            }
            editor.addPropertyChangeListener(this);
            editor.addFocusListener(this);
            add(BorderLayout.CENTER,editor);
            
        } else {
            
            if (this.property != null) {
                editor = getEditor(editors,this.property.getType());
                if(editor != null){
                    editor.setValue(property.getType(), property.getValue());
                    editor.addPropertyChangeListener(this);
                    editor.addFocusListener(this);
                    add(BorderLayout.CENTER,editor);
                } else {
                    add(BorderLayout.CENTER, notSupportedTF);
                }
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PropertyValueEditor.PROP_VALUE.equals(evt.getPropertyName())) {
            firePropertyChange(VALUE_CHANGE_EVENT, evt.getOldValue(), evt.getNewValue());
        }
    }

    /**
     * Transfer enable request to editor.
     * @param enabled 
     */
    @Override
    public void setEnabled(boolean enabled) {
        if (editor != null) {
            editor.setEnabled(enabled);
        }
    }
    
    @Override
    public boolean isEnabled() {
        if (editor != null) {
           return editor.isEnabled();
        } 
        return false;
    }
    
    /**
     * @return true if an editor is found, or false otherwise.
     */
    public boolean isEditorFound() {
        return editor != null;
    }

    /**
     * Forward focus gained event.
     * @param e 
     */
    @Override
    public void focusGained(FocusEvent e) {
        for (FocusListener listeners : getFocusListeners()) {
            listeners.focusGained(e);
        }
    }

    /**
     * Forward focus lost event.
     * @param e 
     */
    @Override
    public void focusLost(FocusEvent e) {
        for (FocusListener listeners : getFocusListeners()) {
            listeners.focusLost(e);
        }
    }
}
