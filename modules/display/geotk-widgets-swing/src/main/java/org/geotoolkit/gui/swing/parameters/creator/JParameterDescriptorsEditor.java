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

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.PropertyValueEditor;
import org.geotoolkit.util.SwingUtilities;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;

/**
 * 
 * @author Quentin Boileau (Geomatys).
 */
public class JParameterDescriptorsEditor extends javax.swing.JPanel implements PropertyChangeListener {

    /**
     * All events fired by firePropertyChange methods.
     */
    public static final String PARAMETER_SELECTED_EVENT = "parameterSelected";
    public static final String PARAMETER_REMOVED_EVENT = "parameterRemoved";
    public static final String PARAMETER_CHANGE_EVENT = "parameterChange";
    public static final String DESCIPTOR_CHANGE_EVENT = "descriptorChange";
    
    private EditableParameterFilter filter;
    private JParameterDescriptorGroupPanel groupPanel;
    private JParameterDescriptorEditorPanel creatorPanel = null;
    
    /*
     * Current selected parameter.
     */
    private GeneralParameterDescriptorPanel selected = null;
    
    /**
     * Create new JParameterEditor.
     * 
     * @param descGroup ParameterDescriptorGroup to edit.
     * @param availableEditors list of {@link PropertyValueEditor} used for default value editing in creation panel.
     * Can be null. In this case, default editors will be used.
     */
    public JParameterDescriptorsEditor(final ParameterDescriptorGroup descGroup, 
            final EditableParameterFilter filter, final List<PropertyValueEditor> availableEditors) {
        this.filter = filter;
        
        initComponents();
        
        groupPanel = new JParameterDescriptorGroupPanel(descGroup, filter, null, this);
        groupPanel.addPropertyChangeListener((PropertyChangeListener)this);
        leftPanel.add(groupPanel, BorderLayout.CENTER);
        
        creatorPanel = new JParameterDescriptorEditorPanel(availableEditors);
        creatorPanel.setVisible(true);
        creatorPanel.addPropertyChangeListener(this); //check for parameter changes
        rightPanel.add(creatorPanel, BorderLayout.CENTER);
        
        groupPanel.setSelected(true);
        updateDividerPosition();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
        //Event launched by parameters panel click event 
        if (PARAMETER_SELECTED_EVENT.equals(evt.getPropertyName())) {
            updateRightPanel((GeneralParameterDescriptorPanel) evt.getNewValue());
        }
        
        //Event launched when a parameter is removed 
        if (PARAMETER_REMOVED_EVENT.equals(evt.getPropertyName())) {
            if (selected != null && selected.equals(evt.getOldValue()) && ((Boolean)evt.getNewValue()) ) {
                selected.setSelected(false);
                selected = null;
            }
        }
        
        //forward event
        if (DESCIPTOR_CHANGE_EVENT.equals(evt.getPropertyName())) {
            firePropertyChange(DESCIPTOR_CHANGE_EVENT, null, this);
        }
    }
    
    /**
     * Build current <code>GeneralParameterDescriptor</code>.
     * @return GeneralParameterDescriptor
     */
    public GeneralParameterDescriptor getDescriptor() {
        return groupPanel.getDescriptor();
    }
    
    public void setDescriptor(ParameterDescriptorGroup descGroup) {
        //clean
        SwingUtilities.removeAllPropertyChangeListeners(groupPanel);
        SwingUtilities.removeAllPropertyChangeListeners(creatorPanel);
        leftPanel.removeAll();
        
        //new
        groupPanel = new JParameterDescriptorGroupPanel(descGroup, filter, null, this);
        groupPanel.addPropertyChangeListener((PropertyChangeListener)this);
        creatorPanel.addPropertyChangeListener(this);
        leftPanel.add(groupPanel, BorderLayout.CENTER);
    }
    
    /**
     * Update creatorPanel on the right side of splitpane.
     * @param panel parameterPanel
     */
    private void updateRightPanel(final GeneralParameterDescriptorPanel panel) {
        if (panel != null) {
            if (selected != null && !selected.equals(panel)) {
                selected.setSelected(false);
                
            }
            selected = (GeneralParameterDescriptorPanel) panel;
            creatorPanel.setVisible(true);
            creatorPanel.editParameter(selected, selected.isEditable());
            updateDividerPosition();

            this.revalidate();
        } else {
            //hide right panel
            SwingUtilities.setDividerLocation(jSplitPane2, 1.0d);
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

        jSplitPane2 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        leftPanel = new javax.swing.JPanel();
        rightPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        jSplitPane2.setResizeWeight(1.0);
        jSplitPane2.setContinuousLayout(true);

        leftPanel.setLayout(new java.awt.BorderLayout());
        jScrollPane1.setViewportView(leftPanel);

        jSplitPane2.setLeftComponent(jScrollPane1);

        rightPanel.setLayout(new java.awt.BorderLayout());
        jSplitPane2.setRightComponent(rightPanel);

        add(jSplitPane2, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel rightPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * Set dividier position depending on the size of creator panel.
     */
    private void updateDividerPosition() {
        
        int total = jSplitPane2.getSize().width;
        int paddingRight = jSplitPane2.getInsets().right;
        int divider = jSplitPane2.getDividerSize();
        //int rightCompSize = creatorPanel.getPreferredSize().width;
        
        jSplitPane2.setDividerLocation(total - paddingRight - divider - 370);
    }

}
