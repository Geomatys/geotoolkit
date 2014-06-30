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

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import javax.swing.JScrollPane;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.PropertyValueEditor;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.gui.swing.util.SwingUtilities;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Quentin Boileau (Geomatys).
 */
public class JParameterValuesEditor extends javax.swing.JPanel implements PropertyChangeListener {

    /**
     * All events fired by firePropertyChange methods.
     */
    public static final String PARAMETER_SELECTED_EVENT = "parameterSelected";

    private JParameterValueGroupListPanel groupPanel;
    private JParameterHelpPanel helpPanel = null;
    private List<PropertyValueEditor> availableEditors;
    private CustomParameterEditor customEditor;

    private JScrollPane rightScrollPane;
    /*
     * Current selected parameter.
     */
    private GeneralParameterValuePanel selected = null;

    public JParameterValuesEditor() {
        this(null,null);
    }

    public JParameterValuesEditor(final List<PropertyValueEditor> availableEditors,
            final CustomParameterEditor customEditor) {
        this.customEditor = customEditor;
        this.availableEditors = availableEditors;
        initComponents();

        helpPanel = new JParameterHelpPanel(null);
        helpPanel.addPropertyChangeListener(this); //check for parameter changes

        rightScrollPane = new JScrollPane();
        rightScrollPane.setViewportView(helpPanel);
        jSplitPane2.setRightComponent(rightScrollPane);

        updateDividerPosition();
    }

    public JParameterValuesEditor(final ParameterDescriptorGroup descGroup, final List<PropertyValueEditor> availableEditors,
            final CustomParameterEditor customEditor) {
        this(descGroup.createValue(), availableEditors, customEditor);
    }

    /**
     * Create new JParameterValuesEditor.
     *
     * @param valueGroup ParameterValueGroup to edit.
     * @param availableEditors list of {@link PropertyValueEditor} used for default value editing in creation panel.
     * Can be null. In this case, default editors will be used.
     * @param customEditor
     */
    public JParameterValuesEditor(final ParameterValueGroup valueGroup, final List<PropertyValueEditor> availableEditors,
            final CustomParameterEditor customEditor) {
        this.customEditor = customEditor;
        this.availableEditors = availableEditors;
        initComponents();

        helpPanel = new JParameterHelpPanel(null);
        helpPanel.addPropertyChangeListener(this); //check for parameter changes

        rightScrollPane = new JScrollPane();
        rightScrollPane.setViewportView(helpPanel);
        jSplitPane2.setRightComponent(rightScrollPane);
        setParameterValue(valueGroup);
    }

    public void setAvailableEditors(List<PropertyValueEditor> availableEditors) {
        this.availableEditors = availableEditors;
    }

    public boolean isHelpVisible(){
        return jSplitPane2.getRightComponent() != null;
    }

    public void setHelpVisible(boolean visible){
        if(isHelpVisible() == visible) return;

        if(visible){
            jSplitPane2.setRightComponent(rightScrollPane);
            jSplitPane2.setDividerSize(3);
        }else{
            jSplitPane2.setRightComponent(null);
            jSplitPane2.setDividerSize(0);
        }
        jSplitPane2.revalidate();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        //Event launched by parameters panel click event
        if (PARAMETER_SELECTED_EVENT.equals(evt.getPropertyName())) {
            showHelp((GeneralParameterValuePanel) evt.getNewValue());
        }
    }

    /**
     * Build current <code>GeneralParameterDescriptor</code>.
     * @return GeneralParameterDescriptor
     */
    public GeneralParameterDescriptor getDescriptor() {
        return groupPanel.getDescriptor();
    }

    /**
     * Return current <code>GeneralParameterValue</code> edited by user.
     * @return GeneralParameterValue
     */
    public GeneralParameterValue getParameterValue() {
        return groupPanel.getParameterValues().get(0);
    }

    /**
     * Initialize editor with given ParameterValueGroup.
     *
     * @param valueGroup shouln't be null
     */
    public final void setParameterValue(final ParameterValueGroup valueGroup) {
        ArgumentChecks.ensureNonNull("valueGroups", valueGroup);

        //clear
        if (groupPanel != null) {
            SwingUtilities.removeAllPropertyChangeListeners(groupPanel);
            groupPanel = null;
            leftPanel.removeAll();
        }

        final ParameterDescriptorGroup descriptor = valueGroup.getDescriptor();
        if (descriptor.getMinimumOccurs() != 1 && descriptor.getMaximumOccurs() != 1) {
            throw new IllegalArgumentException("Root ParameterValueGroup must have a multiplicity 1/1.");
        }

        groupPanel = new JParameterValueGroupListPanel(Collections.singletonList(valueGroup), descriptor, null, this, availableEditors, customEditor);
        groupPanel.addPropertyChangeListener((PropertyChangeListener)this);
        leftPanel.add(groupPanel, BorderLayout.CENTER);

        updateDividerPosition();
    }

    /**
     * Initialize editor with given ParameterDescriptorGroup.
     *
     * @param descriptor shouln't be null
     */
    public final void setParameterDescriptor(final ParameterDescriptorGroup descriptor) {
        ArgumentChecks.ensureNonNull("descriptor", descriptor);
        setParameterValue(descriptor.createValue());
    }

    /**
     * Check all parameters to see if they are valid.
     * If not, mark not valid parameters in red and return false;
     * @return true if ParaMeterValueGroup is valid, false otherwise.
     */
    public boolean validateValues(){
        return groupPanel.validateValues();
    }

    /**
     * Update creatorPanel on the right side of splitpane.
     * @param panel parameterPanel
     */
    private void showHelp(final GeneralParameterValuePanel panel) {
        if (panel != null) {
            if (selected != null && !selected.equals(panel)) {
                selected.setSelected(false);

            }
            selected = (GeneralParameterValuePanel) panel;
            helpPanel.setParameter(selected);
            updateDividerPosition();

        } else {
            //hide right panel
            SwingUtilities.setDividerLocation(jSplitPane2, 1.0d);
        }

        this.revalidate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        leftPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jSplitPane2.setResizeWeight(1.0);
        jSplitPane2.setContinuousLayout(true);

        leftPanel.setLayout(new java.awt.BorderLayout());
        jScrollPane1.setViewportView(leftPanel);

        jSplitPane2.setLeftComponent(jScrollPane1);

        add(jSplitPane2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JPanel leftPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * Set divider position depending on the size of creator panel.
     */
    private void updateDividerPosition() {

        int total = jSplitPane2.getSize().width;
        int paddingRight = jSplitPane2.getInsets().right;
        int divider = jSplitPane2.getDividerSize();
        int rightCompSize = helpPanel.getPreferredSize().width;

        jSplitPane2.setDividerLocation(total - paddingRight - divider - rightCompSize);
    }

}
