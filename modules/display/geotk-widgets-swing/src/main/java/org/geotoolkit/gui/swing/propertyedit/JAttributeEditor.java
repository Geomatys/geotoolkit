/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012 - 2014, Geomatys
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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.type.PropertyType;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.*;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.opengis.util.InternationalString;

/**
 * Edit a single property. 
 * When value in property editor change, JAttributeEditor will fire a 
 * property change event with property name <code>#VALUE_CHANGE_EVENT</code>
 *
 * @author Johann Sorel (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @author Cédric Briançon (Geomatys)
 */
public class JAttributeEditor extends JPanel implements PropertyChangeListener, FocusListener {

    //Event name fired in PropertyChange
    public static final String VALUE_CHANGE_EVENT = "value";
    
    /*
     * Not supported text gived to notSupported TextField.
     */
    private static final InternationalString NOT_SUPPORTED = MessageBundle.formatInternational(MessageBundle.Keys.notSupported);

    /**
     * Unmodifiable list of default editors.
     */
    private static final List<PropertyValueEditor> DEFAULT_EDITORS = UnmodifiableArrayList.wrap(
        new PropertyValueEditor[]{
            new ChoiceEditor(), new BooleanEditor(), new CRSEditor(), new CharsetEditor(), new CharacterEditor(),
            new NumberEditor(), new StringEditor(), new InternationalStringEditor(), new URLEditor(), new URIEditor(),
            new FileEditor(), new UnitEditor(), new EnumEditor(), new ArrayEditor(), new StyleEditor(),
            new FilterEditor(), new DateEditor(), new TimeStampEditor()});

    private final JTextField notSupportedTF = new JTextField(NOT_SUPPORTED.toString());
    private final List<PropertyValueEditor> editors = new CopyOnWriteArrayList<>();
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
                editor.setValue(property.getType(), property.getValue(), property.getUserData());
            }
            editor.addPropertyChangeListener(this);
            editor.addFocusListener(this);
            add(BorderLayout.CENTER,editor);
            
        } else {
            
            if (this.property != null) {
                editor = getEditor(editors,this.property.getType());
                if(editor != null){
                    editor.setValue(property.getType(), property.getValue(), property.getUserData());
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

    /**
     * @return an {@linkplain UnmodifiableArrayList unmodifiable list} of default editors
     */
    public static List<PropertyValueEditor> createDefaultEditorList(){
        return DEFAULT_EDITORS;
    }


    public static PropertyValueEditor getEditor(final Collection<? extends PropertyValueEditor> editors, PropertyType type){
        if(type != null){
            for(PropertyValueEditor edit : editors){
                final Class clazz = type.getBinding();
                if(edit instanceof ArrayEditor && clazz.isArray()){
                    final PropertyValueEditor cp = edit.copy();
                    ((ArrayEditor)cp).setEditors(editors);
                    if(cp.canHandle(type)){
                        return cp;
                    }
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
