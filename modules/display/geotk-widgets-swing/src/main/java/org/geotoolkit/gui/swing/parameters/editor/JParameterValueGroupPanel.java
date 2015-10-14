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
package org.geotoolkit.gui.swing.parameters.editor;

import java.awt.GridBagConstraints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.PropertyValueEditor;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public final class JParameterValueGroupPanel extends JPanel implements PropertyChangeListener {

    public static final String PARAMETER_EDITOR_ADDED_EVENT = "parameterEditorAdded";

    private ParameterDescriptorGroup descGroup;

    private final PropertyChangeListener editorListener;
    private List<JParameterValuePanel> simpleParameters;
    private List<JParameterValueGroupListPanel> groupParameters;

    /**
     * Create JParameterValueGroupPanel from ParameterDescriptorGroup
     * @param descriptor
     * @param parent
     * @param listener
     */
    public JParameterValueGroupPanel(final ParameterDescriptorGroup descriptor, final JParameterValueGroupListPanel parent,
            final PropertyChangeListener listener, final List<PropertyValueEditor> availableEditors, final CustomParameterEditor customEditor) {
        this(descriptor.createValue(), parent, listener, availableEditors, customEditor);
    }
    /**
     * Create JParameterValueGroupPanel from ParameterValueGroup
     * @param valueGroup
     * @param parent
     * @param parent
     * @param listener
     */
    public JParameterValueGroupPanel(final ParameterValueGroup valueGroup, final JParameterValueGroupListPanel parent,
            final PropertyChangeListener listener, final List<PropertyValueEditor> availableEditors, final CustomParameterEditor customEditor) {

        this.descGroup = valueGroup.getDescriptor();
        final List<GeneralParameterDescriptor> descriptors = descGroup.descriptors();

        this.editorListener = listener;
        this.simpleParameters = new LinkedList<JParameterValuePanel>();
        this.groupParameters = new LinkedList<JParameterValueGroupListPanel>();

        for (GeneralParameterDescriptor param : descriptors) {

            final String paramCode = param.getName().getCode();

            GeneralParameterValuePanel comp = null;
            if (param instanceof ParameterDescriptor) {
                comp = new JParameterValuePanel(valueGroup.parameter(paramCode), this, availableEditors, customEditor);
                this.simpleParameters.add((JParameterValuePanel) comp);
            } else if (param instanceof ParameterDescriptorGroup) {
                comp = new JParameterValueGroupListPanel(valueGroup.groups(paramCode), (ParameterDescriptorGroup)param, this, editorListener, availableEditors, customEditor);
                this.groupParameters.add((JParameterValueGroupListPanel) comp);
            }
        }
        //sort in alphabetical order using parameters code
        Collections.sort(simpleParameters);
        Collections.sort(groupParameters);

        initComponents();

        paddingPanel.setOpaque(false);
        parametersContainerPanel.setOpaque(false);
        this.setOpaque(false);

        updateContent();
    }

    public ParameterValueGroup getParameterValue() {
        //valueGroup to fill
        final ParameterValueGroup valueGroup = descGroup.createValue();

        for (GeneralParameterDescriptor desc : descGroup.descriptors()) {
            final String paramCode = desc.getName().getCode();

            //Simple parameter -> find parameter associated panel and set value
            if (desc instanceof ParameterDescriptor) {

                ParameterValue paramValue = null;
                for (JParameterValuePanel simpleValuePanel : simpleParameters) {
                    if (simpleValuePanel.getDescriptor().equals(desc)) {
                        paramValue = simpleValuePanel.getParameterValue();
                        break;
                    }
                }

                if (paramValue != null) {
                    ParametersExt.getOrCreateValue(valueGroup, paramCode).setValue(paramValue.getValue());
                } else {
                    if (desc.getMinimumOccurs() > 0) {
                        Logging.getLogger("org.geotoolkit.gui.swing.parameters.editor").log(Level.WARNING, "Mandatory parameter {0} doesn't have a value", paramCode);
                    }
                }

            } else {

                //group parameter -> find group panels, create groups if needed and deep copy into it.
                List<ParameterValueGroup> paramValues = null;
                for (JParameterValueGroupListPanel groupValuePanel : groupParameters) {
                    if (groupValuePanel.getDescriptor().equals(desc)) {
                        paramValues = groupValuePanel.getParameterValues();
                        break;
                    }
                }

                if (paramValues != null && !paramValues.isEmpty()) {
                    int groupsSize =  valueGroup.groups(paramCode).size();

                    // paramValues.size() should never be beyond min/max occurs of group
                    // define by group descriptor. This is ensured by editor
                    if (groupsSize < paramValues.size()) {
                        int toAdd = paramValues.size() - groupsSize;
                        for (int i = 0; i < toAdd; i++) {
                            valueGroup.addGroup(paramCode);
                        }
                    }

                    List<ParameterValueGroup> groups = valueGroup.groups(paramCode);

                    for (int i=0; i<paramValues.size(); i++) {
                        ParametersExt.deepCopy(paramValues.get(i), groups.get(i));
                    }

                } else {
                    if (desc.getMinimumOccurs() > 0) {
                        Logging.getLogger("org.geotoolkit.gui.swing.parameters.editor").log(Level.WARNING, "Mandatory parameter {0} doesn't have a value", paramCode);
                    }
                }
            }
        }

        return valueGroup;
    }

     public boolean validateValues() {
         boolean valid = true;

         for (JParameterValuePanel simpleParam : simpleParameters) {
             if (!simpleParam.validateValue()) {
                 valid = false;
             }
         }

         for (JParameterValueGroupListPanel groupParam : groupParameters) {
             if (!groupParam.validateValues()) {
                 valid = false;
             }
         }

         return valid;
     }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paddingPanel = new javax.swing.JPanel();
        parametersContainerPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        paddingPanel.setLayout(new java.awt.BorderLayout());

        parametersContainerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        parametersContainerPanel.setLayout(new java.awt.GridBagLayout());
        paddingPanel.add(parametersContainerPanel, java.awt.BorderLayout.PAGE_START);

        add(paddingPanel, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel paddingPanel;
    private javax.swing.JPanel parametersContainerPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * Remove and rebuild parameters list using GridBag layout.
     */
    public void updateContent() {

        //clear
        parametersContainerPanel.removeAll();

        GridBagConstraints constraint;
        JSeparator separator;

        int index = 0;

        //first all simple parameters
        for (JParameterValuePanel param : simpleParameters) {

            if (param != null) {
                param.addPropertyChangeListener(editorListener);
                param.addPropertyChangeListener(this);
                param.addToContainer(parametersContainerPanel, index);
                index++;
            }
        }

        //finish with group parameters
        for (GeneralParameterValuePanel param : groupParameters) {

            if (param != null) {

                // separator
                constraint = new GridBagConstraints();
                constraint.gridx = 0;
                constraint.gridy = index;
                constraint.weightx = 0.0;
                constraint.weighty = 0.0;
                constraint.fill = GridBagConstraints.HORIZONTAL;
                constraint.gridwidth = GridBagConstraints.REMAINDER;
                separator = new JSeparator(JSeparator.HORIZONTAL);
                separator.setBorder(new EmptyBorder(8, 0, 0, 0));
                parametersContainerPanel.add(separator, constraint);
                index++;

                //group
                constraint = new GridBagConstraints();
                constraint.gridx = 0;
                constraint.gridy = index;
                constraint.weightx = 1.0;
                constraint.weighty = 0.0;
                constraint.fill = GridBagConstraints.BOTH;
                constraint.gridwidth = GridBagConstraints.REMAINDER;

                //add to panel
                param.addPropertyChangeListener(editorListener);  //editorListener
                parametersContainerPanel.add(param, constraint);
                index++;
            }
        }
        this.revalidate();
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

}
