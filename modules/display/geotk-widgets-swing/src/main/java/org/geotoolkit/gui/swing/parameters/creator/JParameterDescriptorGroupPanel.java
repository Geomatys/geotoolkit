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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;
import org.apache.sis.parameter.ParameterBuilder;

import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.FontIconJButton;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.geotoolkit.gui.swing.util.SwingUtilities;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.util.InternationalString;

/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public final class JParameterDescriptorGroupPanel extends GeneralParameterDescriptorPanel {
    
    /**
     * Expender/Collapse icons.
     */
    private static final ImageIcon CARET_DOWN = IconBuilder.createIcon(FontAwesomeIcons.ICON_CARET_DOWN, 18, Color.BLACK);
    private static final ImageIcon CARET_RIGHT = IconBuilder.createIcon(FontAwesomeIcons.ICON_CARET_RIGHT, 18, Color.BLACK);
    
    /**
     * Prefix used on new parameter or group creation.
     */
    private static final String GROUP_PREFIX = "group";
    private static final String PARAM_PREFIX = "param";
    
    /**
     * JParameterEditor propertyChangeListener transfered to JParameterDescriptorGroupPanel children
     * GeneralParameterDescriptorPanel. This listener is use by JParameterEditor to know if a parameter
     * is selected or not in order to update creatorPanel.
     */
    private final PropertyChangeListener focusListener; //JParameterEditor 
    private boolean rootGroup = false;
    private boolean editable;
    private boolean removable;
    
    private boolean expended = true;
    
    private int minOccurs;
    private int maxOccurs;
    private List<GeneralParameterDescriptorPanel> simpleParameters;
    private List<GeneralParameterDescriptorPanel> groupParameters;
    
    /**
     * Create new form JParametersCreator
     */
    public JParameterDescriptorGroupPanel(final ParameterDescriptorGroup descGroup, final EditableParameterFilter filter, 
            final JParameterDescriptorGroupPanel parent, final PropertyChangeListener listener) {
        super(descGroup, parent);
        
        this.focusListener = listener;
        this.rootGroup = parent == null;
        this.editable = filter != null ? filter.isEditable(descGroup) : true;
        this.removable = filter != null ? filter.isRemovable(descGroup) : true;
        
        this.minOccurs = descGroup.getMinimumOccurs();
        this.maxOccurs = descGroup.getMaximumOccurs();
        
        this.simpleParameters = new LinkedList<GeneralParameterDescriptorPanel>();
        this.groupParameters = new LinkedList<GeneralParameterDescriptorPanel>();
        
        for (GeneralParameterDescriptor param : descGroup.descriptors()) {
            
            GeneralParameterDescriptorPanel comp = null;
            if (param instanceof ParameterDescriptor) {
                comp = new JParameterDescriptorPanel((ParameterDescriptor) param, this, filter);
                this.simpleParameters.add(comp);
            } else if (param instanceof ParameterDescriptorGroup) {
                comp = new JParameterDescriptorGroupPanel((ParameterDescriptorGroup) param, filter, this, focusListener);
                this.groupParameters.add(comp);
            }
        }
        //sort in alphabetical order using parameters code
        Collections.sort(simpleParameters); 
        Collections.sort(groupParameters); 
        
        initComponents();
        
        paddingPanel.setBorder(new EmptyBorder(5, 28, 5, 28));
        
        //disable add/remove button if group not editable/removable
        guiRemoveParamBtn.setEnabled(!rootGroup && removable);
        guiNewGroupBtn.setEnabled(editable);
        guiNewParamBtn.setEnabled(editable);
        
        //expender init
        guiExpenderBtn.setText(null);
        guiExpenderBtn.setIcon(CARET_DOWN);
        
        //ToolTipText on buttons.
        guiExpenderBtn.setToolTipText(MessageBundle.format("parameters_collapse"));
        guiNewGroupBtn.setToolTipText(MessageBundle.format("parameters_addNewGroupParameter"));
        guiNewParamBtn.setToolTipText(MessageBundle.format("parameters_addNewSimpleParameter"));
        guiRemoveParamBtn.setToolTipText(MessageBundle.format("parameters_removeParameter"));
       
        //label init
        guiGroupNameLbl.setText(code);
        guiGroupNameLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        guiGroupNameLbl.addMouseListener(this);
        
        updateContent();
    }
    
    @Override
    public boolean isEditable() {
        return !rootGroup && editable;
    }

    public int getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(int minOccurs) {
        this.minOccurs = minOccurs;
    }

    public int getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(int maxOccurs) {
        this.maxOccurs = maxOccurs;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        topPanel = new javax.swing.JPanel();
        topLeftPanel = new javax.swing.JPanel();
        guiExpenderBtn = new FontIconJButton(FontAwesomeIcons.ICON_CARET_DOWN, 18, Color.BLACK);
        guiGroupNameLbl = new javax.swing.JLabel();
        topRightPanel = new javax.swing.JPanel();
        guiRemoveParamBtn = new FontIconJButton(FontAwesomeIcons.ICON_MINUS_CIRCLE, 18, Color.RED);
        guiNewGroupBtn = new FontIconJButton(FontAwesomeIcons.ICON_PLUS_CIRCLE, 18, Color.BLACK);
        guiNewParamBtn = new FontIconJButton(FontAwesomeIcons.ICON_PLUS, 18, Color.BLACK);
        bottomPanel = new javax.swing.JPanel();
        paddingPanel = new javax.swing.JPanel();
        parametersContainerPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        topPanel.setLayout(new java.awt.BorderLayout());

        topLeftPanel.setAlignmentX(0.0F);

        guiExpenderBtn.setBorderPainted(false);
        guiExpenderBtn.setContentAreaFilled(false);
        guiExpenderBtn.setFocusable(false);
        guiExpenderBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiExpenderBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(guiGroupNameLbl, null);

        javax.swing.GroupLayout topLeftPanelLayout = new javax.swing.GroupLayout(topLeftPanel);
        topLeftPanel.setLayout(topLeftPanelLayout);
        topLeftPanelLayout.setHorizontalGroup(
            topLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topLeftPanelLayout.createSequentialGroup()
                .addComponent(guiExpenderBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guiGroupNameLbl)
                .addContainerGap())
        );
        topLeftPanelLayout.setVerticalGroup(
            topLeftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(guiGroupNameLbl)
            .addComponent(guiExpenderBtn)
        );

        topLeftPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {guiExpenderBtn, guiGroupNameLbl});

        topPanel.add(topLeftPanel, java.awt.BorderLayout.LINE_START);

        guiRemoveParamBtn.setBorderPainted(false);
        guiRemoveParamBtn.setContentAreaFilled(false);
        guiRemoveParamBtn.setFocusable(false);
        guiRemoveParamBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiRemoveParamBtnActionPerformed(evt);
            }
        });

        guiNewGroupBtn.setBorderPainted(false);
        guiNewGroupBtn.setContentAreaFilled(false);
        guiNewGroupBtn.setFocusable(false);
        guiNewGroupBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiNewGroupBtnActionPerformed(evt);
            }
        });

        guiNewParamBtn.setBorderPainted(false);
        guiNewParamBtn.setContentAreaFilled(false);
        guiNewParamBtn.setFocusable(false);
        guiNewParamBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiNewParamBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout topRightPanelLayout = new javax.swing.GroupLayout(topRightPanel);
        topRightPanel.setLayout(topRightPanelLayout);
        topRightPanelLayout.setHorizontalGroup(
            topRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topRightPanelLayout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(guiNewParamBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guiNewGroupBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guiRemoveParamBtn))
        );
        topRightPanelLayout.setVerticalGroup(
            topRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addComponent(guiNewGroupBtn)
                .addComponent(guiRemoveParamBtn)
                .addComponent(guiNewParamBtn))
        );

        topRightPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {guiNewGroupBtn, guiNewParamBtn, guiRemoveParamBtn});

        topPanel.add(topRightPanel, java.awt.BorderLayout.LINE_END);

        add(topPanel, java.awt.BorderLayout.PAGE_START);

        bottomPanel.setLayout(new java.awt.BorderLayout());

        paddingPanel.setLayout(new java.awt.BorderLayout());

        parametersContainerPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        parametersContainerPanel.setLayout(new java.awt.GridBagLayout());
        paddingPanel.add(parametersContainerPanel, java.awt.BorderLayout.PAGE_START);

        bottomPanel.add(paddingPanel, java.awt.BorderLayout.PAGE_START);

        add(bottomPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Expend/Collapse children parameters.
     * @param evt 
     */
    private void guiExpenderBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiExpenderBtnActionPerformed
        expended = !expended;
        if (expended) {
            guiExpenderBtn.setIcon(CARET_DOWN);
            guiExpenderBtn.setToolTipText(MessageBundle.format("parameters_collapse"));
            bottomPanel.setVisible(true);
            
        } else {
            guiExpenderBtn.setIcon(CARET_RIGHT);
            guiExpenderBtn.setToolTipText(MessageBundle.format("parameters_expend"));
            bottomPanel.setVisible(false);
        }
    }//GEN-LAST:event_guiExpenderBtnActionPerformed

    /**
     * Fire event to parent JParameterDescriptorGroupPanel
     * @param evt 
     */
    private void guiRemoveParamBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiRemoveParamBtnActionPerformed
        firePropertyChange(JParameterDescriptorsEditor.PARAMETER_REMOVED_EVENT, this, false);
    }//GEN-LAST:event_guiRemoveParamBtnActionPerformed

    /**
     * Add new JParameterDescriptorGroupPanel to children.
     * @param evt 
     */
    private void guiNewGroupBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiNewGroupBtnActionPerformed
         final ParameterDescriptorGroup newGroup = new ParameterBuilder().addName(nextPrefixCode(GROUP_PREFIX)).createGroup();
         final JParameterDescriptorGroupPanel groupPanel = new JParameterDescriptorGroupPanel(newGroup, null, this, focusListener);
         groupParameters.add(groupPanel);
         
         updateContent();
         groupPanel.setSelected(true);
         firePropertyChange(JParameterDescriptorsEditor.DESCIPTOR_CHANGE_EVENT, null, newGroup);
    }//GEN-LAST:event_guiNewGroupBtnActionPerformed

    /**
     * Add new JParameterDescriptorPanel to children.
     * @param evt 
     */
    private void guiNewParamBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiNewParamBtnActionPerformed
        final ParameterDescriptor newDesc = new DefaultParameterDescriptor(nextPrefixCode(PARAM_PREFIX), String.class, null, null);
        final JParameterDescriptorPanel panel = new JParameterDescriptorPanel(newDesc, this, null);
        simpleParameters.add(panel);
        
        updateContent();
        panel.setSelected(true);
        firePropertyChange(JParameterDescriptorsEditor.DESCIPTOR_CHANGE_EVENT, null, newDesc);
    }//GEN-LAST:event_guiNewParamBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton guiExpenderBtn;
    private javax.swing.JLabel guiGroupNameLbl;
    private javax.swing.JButton guiNewGroupBtn;
    private javax.swing.JButton guiNewParamBtn;
    private javax.swing.JButton guiRemoveParamBtn;
    private javax.swing.JPanel paddingPanel;
    private javax.swing.JPanel parametersContainerPanel;
    private javax.swing.JPanel topLeftPanel;
    private javax.swing.JPanel topPanel;
    private javax.swing.JPanel topRightPanel;
    // End of variables declaration//GEN-END:variables

    /**
     * Remove and rebuild parameters list using GridBag layout.
     */
    @Override
    public void updateContent() {
        
        guiGroupNameLbl.setText(code);
        
        //clear
        parametersContainerPanel.removeAll();
        
        final GridBagConstraints constraint = new GridBagConstraints();
        int index = 0;
        
        //first all simple parameters
        for (GeneralParameterDescriptorPanel param : simpleParameters) {
            
            if (param != null) {
                constraint.gridx = 0;
                constraint.gridy = index;
                constraint.weightx = 1.0;
                constraint.weighty = 0.0;
                constraint.fill = GridBagConstraints.BOTH;

                //add to panel
                param.addPropertyChangeListener(focusListener);  //focusListener
                param.addPropertyChangeListener(this);           //removeListener
                parametersContainerPanel.add(param, constraint);
                index++;
            }
        }
        
        //finish with group parameters
        for (GeneralParameterDescriptorPanel param : groupParameters) {
            
            if (param != null) {
                constraint.gridx = 0;
                constraint.gridy = index;
                constraint.weightx = 1.0;
                constraint.weighty = 0.0;
                constraint.fill = GridBagConstraints.BOTH;

                //add to panel
                param.addPropertyChangeListener(focusListener);  //focusListener
                param.addPropertyChangeListener(this);           //removeListener
                parametersContainerPanel.add(param, constraint);
                index++;
            }
        }
        this.revalidate();
    }
    
    /**
     * Generate next unused prefix code.
     * 
     * @param prefix
     * @return prefix string + index
     */
    private String nextPrefixCode(final String prefix){
        int i=1;
        incloop:
        for(;;i++){
            if (PARAM_PREFIX.equals(prefix)) {
                for (GeneralParameterDescriptorPanel param : simpleParameters) {
                    if( (prefix+i).equalsIgnoreCase(param.getCode())){
                        continue incloop;
                    }
                }
            } else {
                for (GeneralParameterDescriptorPanel param : groupParameters) {
                    if( (prefix+i).equalsIgnoreCase(param.getCode())){
                        continue incloop;
                    }
                }
            }
            break incloop;
        }
        return prefix+i;
    }
    
    /**
     * Check if input code String is a already used by another parameter or not.
     * 
     * @param code String to test
     * @param ignore GeneralParameterDescriptorPanel to ignore for test. 
     * @return true if code is free and false otherwise.
     */
    public boolean isValidCode(final String code, final GeneralParameterDescriptorPanel ignore) {
        if (code == null) return false;
        boolean valid = true;
        
        for (final GeneralParameterDescriptorPanel param : simpleParameters) {
            if (!param.equals(ignore) && code.equalsIgnoreCase(param.getCode())) {
                valid = false;
            }
        }
        
        for (final GeneralParameterDescriptorPanel param : groupParameters) {
            if (!param.equals(ignore) && code.equalsIgnoreCase(param.getCode())) {
                valid = false;
            }
        }
        return valid;
    }

    @Override
    public GeneralParameterDescriptor getDescriptor() {
        final List<GeneralParameterDescriptor> descriptors = new ArrayList<GeneralParameterDescriptor>();
        for (GeneralParameterDescriptorPanel param : simpleParameters) {
            descriptors.add(param.getDescriptor());
        }
        for (GeneralParameterDescriptorPanel param : groupParameters) {
            descriptors.add(param.getDescriptor());
        }
        
        final InternationalString remark = remarks != null ? new SimpleInternationalString(remarks) : null;
        return ParametersExt.createParameterDescriptorGroup(code, remark, minOccurs, maxOccurs, descriptors);
    }
    
    @Override
    public void setBackgroundColor(final Color color) {
        topPanel.setBackground(color);
        topLeftPanel.setBackground(color);
        topRightPanel.setBackground(color);
        paddingPanel.setBackground(color);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
        //Parameter removed event
        //If parameter to remove isn't already removed, try to remove it and forward event.
        if (JParameterDescriptorsEditor.PARAMETER_REMOVED_EVENT.equals(evt.getPropertyName())) {
            boolean removed = (Boolean)evt.getNewValue();
            final GeneralParameterDescriptorPanel toRemove = (GeneralParameterDescriptorPanel)evt.getOldValue();
            if (simpleParameters.contains(toRemove) && !removed) {
                toRemove.setSelected(false);
                SwingUtilities.removeAllPropertyChangeListeners(toRemove);
                simpleParameters.remove(toRemove);
                updateContent();
                removed = true;
                this.setSelected(true);
            }
            if (groupParameters.contains(toRemove) && !removed) {
                toRemove.setSelected(false);
                SwingUtilities.removeAllPropertyChangeListeners(toRemove);
                groupParameters.remove(toRemove);
                updateContent();
                removed = true;
                this.setSelected(true);
            }
            firePropertyChange(JParameterDescriptorsEditor.PARAMETER_REMOVED_EVENT, evt.getOldValue(), removed);
            firePropertyChange(JParameterDescriptorsEditor.DESCIPTOR_CHANGE_EVENT, null, this);
            
        }
        
        //Parameter change event.
        if (JParameterDescriptorsEditor.PARAMETER_CHANGE_EVENT.equals(evt.getPropertyName())) {
            this.revalidate();
        }
        
        //forward event
        if (JParameterDescriptorsEditor.DESCIPTOR_CHANGE_EVENT.equals(evt.getPropertyName())) {
            firePropertyChange(JParameterDescriptorsEditor.DESCIPTOR_CHANGE_EVENT, null, this);
        }
        
    }
    
    /**
     * Sort GeneralParameterDescriptorPanel by first JParameterDescriptorPanel and then JParameterDescriptorGroupPanel.
     */
    private class ParameterPanelComparator implements Comparator<GeneralParameterDescriptorPanel> {

        @Override
        public int compare(GeneralParameterDescriptorPanel o1, GeneralParameterDescriptorPanel o2) {
            if (o1 != null && o2 != null) {
                if (o1 instanceof JParameterDescriptorPanel) {
                    if (o2 instanceof JParameterDescriptorPanel) {
                        return o1.compareTo(o2);
                    } else {
                        return -1;
                    }
                } else {
                    if (o2 instanceof JParameterDescriptorPanel) {
                        return 1;
                    } else {
                        return o1.compareTo(o2);
                    }
                }
            } 
            
            if (o1 == null) return -1;
            if (o2 == null) return 1;
            
            return 0;
        }
    }
}
