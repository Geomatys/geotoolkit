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
package  org.geotoolkit.gui.swing.parameters.creator;

import org.geotoolkit.gui.swing.etl.JClassCellRenderer;
import  org.geotoolkit.gui.swing.parameters.ParameterType;
import java.awt.BorderLayout;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.gui.swing.propertyedit.JAttributeEditor;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.PropertyValueEditor;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.processing.chain.model.ChainDataTypes;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.opengis.parameter.ParameterDescriptor;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public class JParameterDescriptorEditorPanel extends javax.swing.JPanel implements DocumentListener, ChangeListener, PropertyChangeListener {

    private GeneralParameterDescriptorPanel parameterPanel;
    private ParameterType type;
    // Last value for class
    private Class oldType = null;
    private boolean editable = true;
    
    public JParameterDescriptorEditorPanel(final List<PropertyValueEditor> availableEditors) {
        initComponents();
        
        guiGroupRemarkTA.setLineWrap(true);
        guiRemarkTA.setLineWrap(true);
        
        guiTypeCB.setModel(new ListComboBoxModel(ChainDataTypes.VALID_TYPES));
        guiTypeCB.setSelectedItem(String.class);
        guiTypeCB.setRenderer(new JClassCellRenderer());
        
        //if avaibleEditors is defined and not empty, use them.
        if (availableEditors != null && !availableEditors.isEmpty()) {
            defaultValueEditor.getEditors().clear();
            defaultValueEditor.getEditors().addAll(availableEditors);
        }
        
        guiMinOccursSp.setModel(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));
        guiMaxOccursSp.setModel(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
        
        setEditableParameter(editable);
        addListeners();
    }

    /**
     * Enable/disable fields.
     * 
     * @param editable 
     */
    private final void setEditableParameter(boolean editable) {
        this.editable = editable;
        guiCodeTF.setEnabled(editable);
        guiRemarkTA.setEnabled(editable);
        guiMandatory.setEnabled(editable);
        guiTypeCB.setEnabled(editable);
        defaultValueEditor.setEnabled(editable);
        guiGroupCodeTF.setEnabled(editable);
        guiGroupRemarkTA.setEnabled(editable);
        guiMaxOccursSp.setEnabled(editable);
        guiMinOccursSp.setEnabled(editable);
    }
    
    /**
     * Remove change listeners from all fields
     */
    private void removeListeners() {
        guiCodeTF.getDocument().removeDocumentListener(this);
        guiRemarkTA.getDocument().removeDocumentListener(this);
        guiMandatory.removeChangeListener(this);
        defaultValueEditor.removePropertyChangeListener(this);
        
        guiGroupCodeTF.getDocument().removeDocumentListener(this);
        guiGroupRemarkTA.getDocument().removeDocumentListener(this);
        guiMaxOccursSp.removeChangeListener(this);
        guiMinOccursSp.removeChangeListener(this);
    }
    
    /**
     * Add change listeners to all fields
     */
    private void addListeners() {
        //bind Creator panel to fields changes
        guiCodeTF.getDocument().putProperty("source", "guiCodeTF");
        guiCodeTF.getDocument().addDocumentListener(this);
        guiRemarkTA.getDocument().putProperty("source", "guiRemarkTA");
        guiRemarkTA.getDocument().addDocumentListener(this);
        guiMandatory.addChangeListener(this);
        defaultValueEditor.addPropertyChangeListener(this);

        guiGroupCodeTF.getDocument().putProperty("source", "guiGroupCodeTF");
        guiGroupCodeTF.getDocument().addDocumentListener(this);
        guiGroupRemarkTA.getDocument().putProperty("source", "guiGroupRemarkTA");
        guiGroupRemarkTA.getDocument().addDocumentListener(this);
        guiMaxOccursSp.addChangeListener(this);
        guiMinOccursSp.addChangeListener(this);
    }
    
    public void editParameter(final GeneralParameterDescriptorPanel parameterPanel, boolean editable) {
        this.parameterPanel = parameterPanel;
        this.type = (parameterPanel instanceof JParameterDescriptorGroupPanel) ? ParameterType.GROUP : ParameterType.SIMPLE;
        removeListeners();
        updateCreatorForm();
        setEditableParameter(editable);
        addListeners();
    }
    
    private void updateCreatorForm() {
        
        contentPanel.removeAll();

        if (type.equals(ParameterType.SIMPLE)) {
            final JParameterDescriptorPanel param = (JParameterDescriptorPanel) parameterPanel;
            oldType = param.getType();

            guiCodeTF.setText(param.getCode());
            guiRemarkTA.setText(param.getRemarks());
            guiMandatory.setSelected(param.isMandatory());
            guiTypeCB.setSelectedItem(param.getType());

            defaultValueEditor.setProperty(FeatureUtilities.toProperty(((ParameterDescriptor)param.getDescriptor()).createValue()));
            contentPanel.add(BorderLayout.CENTER,parameterEditPanel);

        } else {
            final JParameterDescriptorGroupPanel paramGroup = (JParameterDescriptorGroupPanel) parameterPanel;

            guiGroupCodeTF.setText(paramGroup.getCode());
            guiGroupRemarkTA.setText(paramGroup.getRemarks());
            guiMinOccursSp.setValue(paramGroup.getMinOccurs());
            guiMaxOccursSp.setValue(paramGroup.getMaxOccurs());

            contentPanel.add(BorderLayout.CENTER,parameterGroupEditPanel);
        }
        revalidate();
        repaint();
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        parameterEditPanel = new javax.swing.JPanel();
        guiCodeLbl = new javax.swing.JLabel();
        guiCodeTF = new javax.swing.JTextField();
        guiClassLbl = new javax.swing.JLabel();
        guiTypeCB = new javax.swing.JComboBox();
        guiRemarkLbl = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        guiRemarkTA = new javax.swing.JTextArea();
        guiMandatory = new javax.swing.JCheckBox();
        guiMandatoryLbl = new javax.swing.JLabel();
        guiDefaultValueLbl = new javax.swing.JLabel();
        defaultValueEditor = new org.geotoolkit.gui.swing.propertyedit.JAttributeEditor();
        parameterGroupEditPanel = new javax.swing.JPanel();
        guiGroupCodeTF = new javax.swing.JTextField();
        guiGroupCodeLbl = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        guiGroupRemarkTA = new javax.swing.JTextArea();
        guiGroupRemarkLbl = new javax.swing.JLabel();
        guiGroupMinOccurs = new javax.swing.JLabel();
        guiGroupMaxOccurs = new javax.swing.JLabel();
        guiMinOccursSp = new javax.swing.JSpinner();
        guiMaxOccursSp = new javax.swing.JSpinner();
        guiGroupOccurencesLbl = new javax.swing.JLabel();
        contentPanel = new javax.swing.JPanel();

        guiCodeLbl.setLabelFor(guiCodeTF);
        org.openide.awt.Mnemonics.setLocalizedText(guiCodeLbl, MessageBundle.getString("parameters.code")); // NOI18N
        guiCodeLbl.setFocusable(false);

        guiClassLbl.setLabelFor(guiTypeCB);
        org.openide.awt.Mnemonics.setLocalizedText(guiClassLbl, MessageBundle.getString("parameters.type")); // NOI18N
        guiClassLbl.setFocusable(false);

        guiTypeCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiTypeCBActionPerformed(evt);
            }
        });

        guiRemarkLbl.setLabelFor(guiRemarkTA);
        org.openide.awt.Mnemonics.setLocalizedText(guiRemarkLbl, MessageBundle.getString("parameters.description")); // NOI18N
        guiRemarkLbl.setFocusable(false);

        guiRemarkTA.setColumns(20);
        guiRemarkTA.setRows(5);
        jScrollPane1.setViewportView(guiRemarkTA);

        guiMandatory.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        guiMandatoryLbl.setLabelFor(guiMandatory);
        org.openide.awt.Mnemonics.setLocalizedText(guiMandatoryLbl, MessageBundle.getString("parameters.mandatory")); // NOI18N
        guiMandatoryLbl.setFocusable(false);

        org.openide.awt.Mnemonics.setLocalizedText(guiDefaultValueLbl, MessageBundle.getString("parameters.defaultValue")); // NOI18N

        defaultValueEditor.setBorder(null);

        javax.swing.GroupLayout parameterEditPanelLayout = new javax.swing.GroupLayout(parameterEditPanel);
        parameterEditPanel.setLayout(parameterEditPanelLayout);
        parameterEditPanelLayout.setHorizontalGroup(
            parameterEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parameterEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(parameterEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(guiCodeLbl)
                    .addComponent(guiClassLbl)
                    .addComponent(guiRemarkLbl)
                    .addComponent(guiDefaultValueLbl)
                    .addComponent(guiMandatoryLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(parameterEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(guiCodeTF)
                    .addComponent(guiMandatory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(guiTypeCB, 0, 1, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(defaultValueEditor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        parameterEditPanelLayout.setVerticalGroup(
            parameterEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parameterEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(parameterEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(guiMandatoryLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(guiMandatory))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(parameterEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guiCodeTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiCodeLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(parameterEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guiTypeCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiClassLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(parameterEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(guiRemarkLbl)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parameterEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(guiDefaultValueLbl)
                    .addComponent(defaultValueEditor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        parameterEditPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {guiCodeLbl, guiCodeTF});

        parameterEditPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {guiClassLbl, guiTypeCB});

        parameterEditPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {defaultValueEditor, guiDefaultValueLbl});

        guiGroupCodeLbl.setLabelFor(guiGroupCodeTF);
        org.openide.awt.Mnemonics.setLocalizedText(guiGroupCodeLbl, MessageBundle.getString("parameters.code")); // NOI18N
        guiGroupCodeLbl.setFocusable(false);

        guiGroupRemarkTA.setColumns(20);
        guiGroupRemarkTA.setRows(5);
        jScrollPane2.setViewportView(guiGroupRemarkTA);

        guiGroupRemarkLbl.setLabelFor(guiGroupRemarkTA);
        org.openide.awt.Mnemonics.setLocalizedText(guiGroupRemarkLbl, MessageBundle.getString("parameters.description")); // NOI18N

        guiGroupMinOccurs.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        guiGroupMinOccurs.setLabelFor(guiMinOccursSp);
        org.openide.awt.Mnemonics.setLocalizedText(guiGroupMinOccurs, MessageBundle.getString("parameters.min")); // NOI18N
        guiGroupMinOccurs.setFocusable(false);

        guiGroupMaxOccurs.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        guiGroupMaxOccurs.setLabelFor(guiMaxOccursSp);
        org.openide.awt.Mnemonics.setLocalizedText(guiGroupMaxOccurs, MessageBundle.getString("parameters.max")); // NOI18N
        guiGroupMaxOccurs.setFocusable(false);

        org.openide.awt.Mnemonics.setLocalizedText(guiGroupOccurencesLbl, MessageBundle.getString("parameters.occurences")); // NOI18N

        javax.swing.GroupLayout parameterGroupEditPanelLayout = new javax.swing.GroupLayout(parameterGroupEditPanel);
        parameterGroupEditPanel.setLayout(parameterGroupEditPanelLayout);
        parameterGroupEditPanelLayout.setHorizontalGroup(
            parameterGroupEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parameterGroupEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(parameterGroupEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(guiGroupOccurencesLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(parameterGroupEditPanelLayout.createSequentialGroup()
                        .addGroup(parameterGroupEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(guiGroupCodeLbl, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(guiGroupRemarkLbl, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(parameterGroupEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addComponent(guiGroupCodeTF)))
                    .addGroup(parameterGroupEditPanelLayout.createSequentialGroup()
                        .addComponent(guiGroupMinOccurs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiMinOccursSp))
                    .addGroup(parameterGroupEditPanelLayout.createSequentialGroup()
                        .addComponent(guiGroupMaxOccurs)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guiMaxOccursSp)))
                .addContainerGap())
        );

        parameterGroupEditPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {guiGroupCodeLbl, guiGroupMaxOccurs, guiGroupMinOccurs, guiGroupRemarkLbl});

        parameterGroupEditPanelLayout.setVerticalGroup(
            parameterGroupEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parameterGroupEditPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(parameterGroupEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guiGroupCodeTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(guiGroupCodeLbl))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(parameterGroupEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(guiGroupRemarkLbl)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guiGroupOccurencesLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(parameterGroupEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guiGroupMinOccurs)
                    .addComponent(guiMinOccursSp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(parameterGroupEditPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guiGroupMaxOccurs)
                    .addComponent(guiMaxOccursSp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        parameterGroupEditPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {guiGroupCodeLbl, guiGroupCodeTF});

        setLayout(new java.awt.BorderLayout());

        contentPanel.setLayout(new java.awt.BorderLayout());
        add(contentPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void guiTypeCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiTypeCBActionPerformed
        if (editable) {
            if (oldType != null && !oldType.equals(guiTypeCB.getSelectedItem())) {
                oldType = (Class)guiTypeCB.getSelectedItem();

                ((JParameterDescriptorPanel)parameterPanel).setType(oldType);
                ((JParameterDescriptorPanel)parameterPanel).setDefaultValue(null);
                updateCreatorForm();
            }
        }
    }//GEN-LAST:event_guiTypeCBActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private org.geotoolkit.gui.swing.propertyedit.JAttributeEditor defaultValueEditor;
    private javax.swing.JLabel guiClassLbl;
    private javax.swing.JLabel guiCodeLbl;
    private javax.swing.JTextField guiCodeTF;
    private javax.swing.JLabel guiDefaultValueLbl;
    private javax.swing.JLabel guiGroupCodeLbl;
    private javax.swing.JTextField guiGroupCodeTF;
    private javax.swing.JLabel guiGroupMaxOccurs;
    private javax.swing.JLabel guiGroupMinOccurs;
    private javax.swing.JLabel guiGroupOccurencesLbl;
    private javax.swing.JLabel guiGroupRemarkLbl;
    private javax.swing.JTextArea guiGroupRemarkTA;
    private javax.swing.JCheckBox guiMandatory;
    private javax.swing.JLabel guiMandatoryLbl;
    private javax.swing.JSpinner guiMaxOccursSp;
    private javax.swing.JSpinner guiMinOccursSp;
    private javax.swing.JLabel guiRemarkLbl;
    private javax.swing.JTextArea guiRemarkTA;
    private javax.swing.JComboBox guiTypeCB;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel parameterEditPanel;
    private javax.swing.JPanel parameterGroupEditPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void insertUpdate(DocumentEvent e) {
        textFieldValueChange(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        textFieldValueChange(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        textFieldValueChange(e);
    }
    
    /**
     * Event attached to code/remarks TextField and TextArea.
     * @param event 
     */
    private void textFieldValueChange(DocumentEvent event) {
        final String eventSource = (String)event.getDocument().getProperty("source");
        if (editable) {
            if ("guiCodeTF".equals(eventSource)) {
                //validate parameter code
                if (parameterPanel.getParentPanel().isValidCode(guiCodeTF.getText().trim(), parameterPanel)) {
                    parameterPanel.setCode(guiCodeTF.getText().trim());
                    guiCodeTF.setForeground(UIManager.getColor("TextField.foreground"));
                } else {
                    guiCodeTF.setForeground(Color.red);
                }
            } else if ("guiRemarkTA".equals(eventSource)) {
                parameterPanel.setRemarks(guiRemarkTA.getText().trim());
            } else if ("guiGroupCodeTF".equals(eventSource)) {
                
                if (parameterPanel.getParentPanel() != null) {
                    //validate parameter code
                    if (parameterPanel.getParentPanel().isValidCode(guiGroupCodeTF.getText().trim(), parameterPanel)) {
                        parameterPanel.setCode(guiGroupCodeTF.getText().trim());
                        guiGroupCodeTF.setForeground(UIManager.getColor("TextField.foreground"));
                    } else {
                        guiGroupCodeTF.setForeground(Color.red);
                    }
                } else {
                    parameterPanel.setCode(guiGroupCodeTF.getText().trim());
                    guiGroupCodeTF.setForeground(UIManager.getColor("TextField.foreground"));
                }
            } else if ("guiGroupRemarkTA".equals(eventSource)) {
                parameterPanel.setRemarks(guiGroupRemarkTA.getText().trim());
            } 

            parameterPanel.updateContent();
            firePropertyChange(JParameterDescriptorsEditor.DESCIPTOR_CHANGE_EVENT, null, parameterPanel);
        }
    }

    /**
     * Event attached to mandatory checkbox, min/max occurs spinners.
     * @param e 
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        
        if (editable) {
            if (type.equals(ParameterType.SIMPLE)) {
                if (e.getSource().equals(guiMandatory)) {
                    ((JParameterDescriptorPanel)parameterPanel).setMandatory(guiMandatory.isSelected());
                }
            } else {
                final JParameterDescriptorGroupPanel group = (JParameterDescriptorGroupPanel)parameterPanel;
                if (e.getSource().equals(guiMaxOccursSp)) {
                    group.setMaxOccurs((Integer)guiMaxOccursSp.getValue());
                    
                    //maximum can't be lower than minimum occurences.
                    if (group.getMinOccurs() > group.getMaxOccurs()) {
                        guiMaxOccursSp.setValue(group.getMinOccurs());
                    }
                    
                } else if (e.getSource().equals(guiMinOccursSp)) {
                    group.setMinOccurs((Integer)guiMinOccursSp.getValue());
                    
                    //maximum can't be lower than minimum occurences.
                    if (group.getMinOccurs() > group.getMaxOccurs()) {
                        guiMaxOccursSp.setValue(group.getMinOccurs());
                    }
                }
            }

            parameterPanel.updateContent();
            firePropertyChange(JParameterDescriptorsEditor.DESCIPTOR_CHANGE_EVENT, null, parameterPanel);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
        if (editable) {
            //devault value editor changed
            if (JAttributeEditor.VALUE_CHANGE_EVENT.equals(evt.getPropertyName())) {
                ((JParameterDescriptorPanel)parameterPanel).setDefaultValue(defaultValueEditor.getProperty().getValue());
            }

            parameterPanel.updateContent();
            firePropertyChange(JParameterDescriptorsEditor.DESCIPTOR_CHANGE_EVENT, null, parameterPanel);
        }
    }
    
}
