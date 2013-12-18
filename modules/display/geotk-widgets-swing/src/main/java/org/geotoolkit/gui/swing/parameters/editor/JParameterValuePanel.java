/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013 Geomatys
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
package  org.geotoolkit.gui.swing.parameters.editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.gui.swing.propertyedit.JAttributeEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.PropertyValueEditor;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.FontIconJButton;
import org.geotoolkit.gui.swing.resource.IconBuilder;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.util.SwingUtilities;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterValue;
import org.openide.util.NbBundle;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public final class JParameterValuePanel extends GeneralParameterValuePanel implements FocusListener {

    private static final ImageIcon ACTIVATE_ICON = IconBuilder.createIcon(FontAwesomeIcons.ICON_UNLOCK, 18, Color.BLACK);
    private static final ImageIcon UNACTIVATE_ICON = IconBuilder.createIcon(FontAwesomeIcons.ICON_LOCK, 18, Color.BLACK);
    
    /*
     * Simple parameter properties. (mandatory, type and defaultValue)
     */
    private final boolean mandatory;
    private final Class type;
    private final Object defaultValue;
    
    /*
     * Enable state of the parameter editor.
     */
    private boolean activated;
    
    /*
     * Label with the parameter name.
     */
    private JLabel guiParameterNameLbl;
    
    /*
     * Button to toggle activate/unactivate state of the editor.
     */
    private JButton guitoggleParamBtn;
    
    /*
     * Parameter editor.
     */
    private JAttributeEditor guiEditor;
    
    /**
     * Keep default label color in order to restore if a 
     * validation error occurs and turn it in red.
     */
    private Color defaultLabelColor;
    
    /**
     * Create new JParameterValuePanel
     * @param paramValue ParameterDescritor to edit.
     * @param parent parent JParameterValueGroupPanel.
     */
    public JParameterValuePanel(final ParameterValue paramValue, final JParameterValueGroupPanel parent, 
            final List<PropertyValueEditor> availableEditors, final CustomParameterEditor customEditor) {
        super(paramValue.getDescriptor(), parent);
        
        this.mandatory      = paramDesc.getMinimumOccurs() > 0;
        this.type           = ((ParameterDescriptor)paramDesc).getValueClass();
        this.defaultValue   = ((ParameterDescriptor)paramDesc).getDefaultValue();
        
        initComponents();
        
        //Parameter label
        guiParameterNameLbl = new JLabel(code);
        guiParameterNameLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        guiParameterNameLbl.addMouseListener(this);
        guiParameterNameLbl.setOpaque(false);
        if (mandatory) {
            guiParameterNameLbl.setText(guiParameterNameLbl.getText()+"*");
        }
        SwingUtilities.bold(guiParameterNameLbl);
        defaultLabelColor = guiParameterNameLbl.getForeground();
        
        //Parameter editor. use custom editor if not null. Otherwise, use all 
        //found editors
        if (customEditor != null && customEditor.getCustomEditor(paramValue) != null) {
            guiEditor = new JAttributeEditor(customEditor.getCustomEditor(paramValue));
        } else {
            guiEditor = new JAttributeEditor();
        }
        //use custom editors
        if (availableEditors != null && !availableEditors.isEmpty()) {
            guiEditor.getEditors().clear();
            guiEditor.getEditors().addAll(availableEditors);
        }
        guiEditor.setProperty(FeatureUtilities.toProperty(paramValue));
        guiEditor.addPropertyChangeListener(this);
        guiEditor.addFocusListener(this);
        guiEditor.setOpaque(false);
        
        //Parameter toggle button
        guitoggleParamBtn = new FontIconJButton(FontAwesomeIcons.ICON_CIRCLE, 18, Color.BLACK);
        guitoggleParamBtn.setOpaque(false);
        //enable only if an editor is found and is not mandatory parameter
        guitoggleParamBtn.setEnabled(guiEditor.isEditorFound() && !mandatory); 
        guitoggleParamBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setActivated(!activated);
            }
        });
        
        
        //parameter is activated if he is mandatory or if parameter default value or parameter value is not null.
        boolean active = mandatory;
        if (!mandatory) {
            if (paramValue.getValue() != null || defaultValue != null) {
                active = true;
            } else {
                active = false;
            }
        }
        
        setActivated(active);
        addToContainer(this, 0); // default use with current panel
    }

    @Override
    public void updateContent() {
        guiParameterNameLbl.setText(code);
        this.revalidate();
    }
    
    @Override
    public void setBackgroundColor(final Color color) {
        guiParameterNameLbl.setOpaque(true);
        guiEditor.setOpaque(true);
        
        guiParameterNameLbl.setBackground(color);
        guiEditor.setBackground(color);
        guitoggleParamBtn.setBackground(color);
    }
    
    public ParameterValue getParameterValue() {
        
        if (mandatory) {
            final ParameterValue paramValue = ((ParameterDescriptor)paramDesc).createValue();
            paramValue.setValue(getValue());
            return paramValue;
            
        } else {
            //optional parameter
            if (activated) {
                final ParameterValue paramValue = ((ParameterDescriptor)paramDesc).createValue();
                paramValue.setValue(getValue());
                return paramValue;
                
            } else {
                return null;
            }
        }
    }
    
    /**
     * Check if Parameter value is valid (not null or empty if mandatory)
     * @return true if value is valid, false otherwise.
     */
    public boolean validateValue() {
        final ParameterDescriptor desc = (ParameterDescriptor) paramDesc;
        final ParameterValue paramValue = getParameterValue();
        
        if (mandatory) {
            if (paramValue.getValue() == null) {
                validationError = MessageBundle.getString("parameters.errorNullEmptyParameterValue", paramDesc.getName().getCode());
                guiParameterNameLbl.setForeground(Color.red);
                return false;
            } else {
                //rise an error if empty String for mandatory parameters
                if (desc.getValueClass().isAssignableFrom(String.class)) {
                    if (((String)paramValue.getValue()).isEmpty()) {
                        validationError = MessageBundle.getString("parameters.errorNullEmptyParameterValue", paramDesc.getName().getCode());
                        guiParameterNameLbl.setForeground(Color.red);
                        return false;
                    }
                }
                //rise an error if empty Array for mandatory parameters
//                if (desc.getValueClass().isArray()) {
//                    if (Arrays.asList(paramValue.getValue()).isEmpty()) {
//                        validationError = "Empty value";
//                        guiParameterNameLbl.setForeground(Color.red);
//                        return false;
//                    }
//                }
            }
        }
        
        validationError = null;
        guiParameterNameLbl.setForeground(defaultLabelColor);
        return true;
    }
    
    /**
     * Add components to a container. The container MUST use a GridBagLayout.
     * @param container GridBag container
     * @param gridY GridBag row index.
     */
    public void addToContainer(JPanel container, int gridY) {
        //first remove component from current panel
        this.removeAll();
        
        GridBagConstraints constraint;

        //label
        constraint = new GridBagConstraints();
        constraint.gridx = 0;
        constraint.gridy = gridY;
        constraint.weightx = 0.0;
        constraint.weighty = 0.0;
        constraint.fill = GridBagConstraints.BOTH;
        guiParameterNameLbl.setBorder(new EmptyBorder(0, 10, 0, 10));
        container.add(guiParameterNameLbl, constraint);
        
        //editor
        constraint = new GridBagConstraints();
        constraint.gridx = 1;
        constraint.gridy = gridY;
        constraint.weightx = 1.0;
        constraint.weighty = 0.0;
        constraint.fill = GridBagConstraints.HORIZONTAL;
        container.add(guiEditor, constraint);
        
        //toggle
        constraint = new GridBagConstraints();
        constraint.gridx = 2;
        constraint.gridy = gridY;
        constraint.weightx = 0.0;
        constraint.weighty = 0.0;
        constraint.fill = GridBagConstraints.HORIZONTAL;
        guitoggleParamBtn.setBorder(new EmptyBorder(0, 5, 0, 5));
        container.add(guitoggleParamBtn, constraint);
        
        container.revalidate();
    }
    
    public Object getValue() {
        return guiEditor.getProperty().getValue();
    }
    
    public boolean isMandatory() {
        return mandatory;
    }

    public Class getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
    
    private void setActivated(boolean activated) {
        this.activated = activated;
        if (activated) {
            guitoggleParamBtn.setIcon(ACTIVATE_ICON);
            guitoggleParamBtn.setToolTipText(MessageBundle.getString("parameters.unactivateParam"));
            guiEditor.setEnabled(true);
        } else {
            guitoggleParamBtn.setIcon(UNACTIVATE_ICON);
            guitoggleParamBtn.setToolTipText(MessageBundle.getString("parameters.activateParam"));
            guiEditor.setEnabled(false);
        }
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.GridBagLayout());
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (JAttributeEditor.VALUE_CHANGE_EVENT.equals(evt.getPropertyName())) {
            //value change
            firePropertyChange(JAttributeEditor.VALUE_CHANGE_EVENT, evt.getOldValue(), evt.getNewValue());
            validateValue();
        }
    }
    
    @Override
    public void focusGained(FocusEvent e) {
        setSelected(true);
    }

    @Override
    public void focusLost(FocusEvent e) {
        setSelected(false);
    }

}
