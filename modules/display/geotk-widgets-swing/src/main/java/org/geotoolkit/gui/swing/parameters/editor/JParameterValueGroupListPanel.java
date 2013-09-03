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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import org.geotoolkit.gui.swing.propertyedit.featureeditor.PropertyValueEditor;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.FontIconJButton;
import org.geotoolkit.gui.swing.resource.IconBuilder;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.util.SwingUtilities;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.openide.util.NbBundle;


/**
 *
 * @author Quentin Boileau (Geomatys)
 */
public final class JParameterValueGroupListPanel extends GeneralParameterValuePanel {
    
    /**
     * Expender/Collapse icons.
     */
    private static final ImageIcon CARET_DOWN = IconBuilder.createIcon(FontAwesomeIcons.ICON_CARET_DOWN, 18, Color.BLACK);
    private static final ImageIcon CARET_RIGHT = IconBuilder.createIcon(FontAwesomeIcons.ICON_CARET_RIGHT, 18, Color.BLACK);
    
    /*
     * JParameterEditor propertyChangeListener transfered to JParameterValueGroupPanel children
     * GeneralParameterValuePanel. This listener is use by JParameterEditor to know if a parameter
     * is selected or not in order to update creatorPanel.
     */
    private final PropertyChangeListener editorListener; //JParameterEditor 
    
    /*
     * Min/Max group occurences. This define the min/max range for 
     * JParameterValueGroupPanel child list.
     */
    private final int minOccurs;
    private final int maxOccurs;
    
    /*
     * Custom editor used by JParameterValuePanel
     */
    private final CustomParameterEditor customEditor;
    
    /*
     * List of avaible editors used by JParameterValuePanel
     */
    private final List<PropertyValueEditor> availableEditors;
    
    /*
     * Boolean to know if current ParameterValueGroup is unic.
     * True if min and max occurs of current ParameterValueGroup descriptor are equals to 1.
     */ 
    private final boolean isUnicGroup; 
    
    /*
     * Expended state
     */
    private boolean isExpended = true;
    
    /*
     * List of JParameterValueGroupPanel child.
     */
    private List<JParameterValueGroupPanel> valueGroupPanels;
    
    /**
     * Keep default label color in order to restore if a 
     * validation error occurs and turn it in red.
     */
    private Color defaultLabelColor;
    
    /**
     * Create JParameterValueGroupPanel from ParameterDescriptorGroup
     * @param descriptor
     * @param parent
     * @param listener 
     */
    public JParameterValueGroupListPanel(final ParameterDescriptorGroup descriptor, final JParameterValueGroupPanel parent,
            final PropertyChangeListener listener, final List<PropertyValueEditor> availableEditors, final CustomParameterEditor customEditor) {
        this(descriptor.createValue(), parent, listener, availableEditors,customEditor);
    }
    
    /**
     * Create JParameterValueGroupPanel from ParameterValueGroup
     * @param valueGroup
     * @param parent
     * @param listener 
     */
    public JParameterValueGroupListPanel(final ParameterValueGroup valueGroup, final JParameterValueGroupPanel parent, 
            final PropertyChangeListener listener, final List<PropertyValueEditor> availableEditors, final CustomParameterEditor customEditor) {
        this(Collections.singletonList(valueGroup), valueGroup.getDescriptor(), parent, listener, availableEditors, customEditor);
    }
    
    /**
     * Create JParameterValueGroupPanel from a list of ParameterValueGroup.
     * @param valueGroups
     * @param descriptor
     * @param parent
     * @param listener 
     */
    JParameterValueGroupListPanel(final List<ParameterValueGroup> valueGroups, final ParameterDescriptorGroup descriptor,
            final JParameterValueGroupPanel parent, final PropertyChangeListener listener, final List<PropertyValueEditor> availableEditors,
            final CustomParameterEditor customEditor) {
        super(descriptor, parent);
        
        this.availableEditors = availableEditors;
        this.customEditor = customEditor;
        this.editorListener = listener;
        this.minOccurs = paramDesc.getMinimumOccurs();
        this.maxOccurs = paramDesc.getMaximumOccurs();
        this.isUnicGroup = (minOccurs==1 && maxOccurs==1);
        
        //create groups
        this.valueGroupPanels = new LinkedList<JParameterValueGroupPanel>();
        for (ParameterValueGroup valueGroup : valueGroups) {
            valueGroupPanels.add(new JParameterValueGroupPanel(valueGroup, this, editorListener, availableEditors, customEditor));
        }
        
        initComponents();
        
        //expender
        guiExpenderBtn.setText(null);
        guiExpenderBtn.setIcon(CARET_DOWN);
        
        //ToolTipText on buttons.
        guiExpenderBtn.setToolTipText(MessageBundle.getString("parameters.collapse"));
        guiNewGroupBtn.setToolTipText(MessageBundle.getString("parameters.addNewGroupParameter"));
       
        //label
        guiGroupNameLbl.setText(code);
        guiGroupNameLbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
        guiGroupNameLbl.addMouseListener(this);
        SwingUtilities.bold(guiGroupNameLbl);
        defaultLabelColor = guiGroupNameLbl.getForeground();
        
        this.setBorder(new EmptyBorder(1, 1, 1, 1));
        
        if (isUnicGroup) {
            guiNewGroupBtn.setVisible(false);
        }
        
        updateContent();
    }
    
    /**
     * Return all create groups.
     * @return list of <code>ParameterValueGroup</code>
     */
    public List<ParameterValueGroup> getParameterValues() {
        final List<ParameterValueGroup> valueGroups = new ArrayList<ParameterValueGroup>();
        
        for (final JParameterValueGroupPanel valueGroup : valueGroupPanels) {
            valueGroups.add(valueGroup.getParameterValue());
        }
        
        return valueGroups;
    }
    
    public boolean validateValues() {
        boolean valid = true;
        validationError = null;
        
        for (JParameterValueGroupPanel groupEditor : valueGroupPanels) {
            if (!groupEditor.validateValues()) {
                valid = false;
            }
        }
        
        final int groupSize = valueGroupPanels.size();
        if (groupSize < minOccurs || groupSize > maxOccurs) {
            //Error
            final String name = paramDesc.getName().getCode();
            final int key;
            final Object[] param;
            if (groupSize == 0) {
                key = Errors.Keys.NO_PARAMETER_1;
                param = new Object[] {name};
            } else {
                key = Errors.Keys.ILLEGAL_OCCURS_FOR_PARAMETER_4;
                param = new Object[] {name, groupSize, minOccurs, maxOccurs};
            }
            validationError = Errors.formatInternational(key, param).toString();
            valid = false;
        }
        
        if (!valid && validationError != null) {
            guiGroupNameLbl.setForeground(Color.RED);
        } else {
            guiGroupNameLbl.setForeground(defaultLabelColor);
        }
        
        return valid;
    }
    
    @Override
    public void setBackgroundColor(final Color color) {
        topPanel.setBackground(color);
        if (selected) {
            this.setBorder(new LineBorder(color, 2));
        } else {
            this.setBorder(new EmptyBorder(1, 1, 1, 1));
        }
    }

    public int getMinOccurs() {
        return minOccurs;
    }

    public int getMaxOccurs() {
        return maxOccurs;
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
        guiNewGroupBtn = new FontIconJButton(FontAwesomeIcons.ICON_PLUS_SIGN_ALT, 18, Color.BLACK);
        bottomPanel = new javax.swing.JPanel();
        containerPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        topPanel.setLayout(new java.awt.BorderLayout());

        topLeftPanel.setAlignmentX(0.0F);
        topLeftPanel.setOpaque(false);

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
            .addComponent(guiGroupNameLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(guiExpenderBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        topPanel.add(topLeftPanel, java.awt.BorderLayout.LINE_START);

        topRightPanel.setOpaque(false);

        guiNewGroupBtn.setBorderPainted(false);
        guiNewGroupBtn.setContentAreaFilled(false);
        guiNewGroupBtn.setFocusable(false);
        guiNewGroupBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiNewGroupBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout topRightPanelLayout = new javax.swing.GroupLayout(topRightPanel);
        topRightPanel.setLayout(topRightPanelLayout);
        topRightPanelLayout.setHorizontalGroup(
            topRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, topRightPanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(guiNewGroupBtn))
        );
        topRightPanelLayout.setVerticalGroup(
            topRightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(guiNewGroupBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 15, Short.MAX_VALUE)
        );

        topPanel.add(topRightPanel, java.awt.BorderLayout.LINE_END);

        add(topPanel, java.awt.BorderLayout.PAGE_START);

        bottomPanel.setLayout(new java.awt.BorderLayout());

        containerPanel.setLayout(new java.awt.GridBagLayout());
        bottomPanel.add(containerPanel, java.awt.BorderLayout.PAGE_START);

        add(bottomPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Expend/Collapse children parameters.
     * @param evt 
     */
    private void guiExpenderBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiExpenderBtnActionPerformed
        isExpended = !isExpended;
        if (isExpended) {
            guiExpenderBtn.setIcon(CARET_DOWN);
            guiExpenderBtn.setToolTipText(MessageBundle.getString("parameters.collapse"));
            bottomPanel.setVisible(true);
            
        } else {
            guiExpenderBtn.setIcon(CARET_RIGHT);
            guiExpenderBtn.setToolTipText(MessageBundle.getString("parameters.expend"));
            bottomPanel.setVisible(false);
        }
    }//GEN-LAST:event_guiExpenderBtnActionPerformed

    /**
     * Add new JParameterValueGroupPanel to children.
     * @param evt 
     */
    private void guiNewGroupBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiNewGroupBtnActionPerformed
        //create new group only if current number of groups is < to max occurences defined by group descriptor.
        if (valueGroupPanels.size() < maxOccurs) {
            final ParameterValueGroup newGroup = ((ParameterDescriptorGroup)paramDesc).createValue();
            valueGroupPanels.add(new JParameterValueGroupPanel(newGroup, this, editorListener, availableEditors, customEditor));
            updateContent();
        }
    }//GEN-LAST:event_guiNewGroupBtnActionPerformed

    private void guiRemoveGroupBtnAction(ActionEvent event, int index) {
        //remove a group only if current number of groups is > to min occurences defined by group descriptor.
        if (valueGroupPanels.size() > minOccurs) {
            valueGroupPanels.remove(index);
            updateContent();
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JPanel containerPanel;
    private javax.swing.JButton guiExpenderBtn;
    private javax.swing.JLabel guiGroupNameLbl;
    private javax.swing.JButton guiNewGroupBtn;
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
        
        if (valueGroupPanels.size() >= maxOccurs) {
            guiNewGroupBtn.setEnabled(false);
        } else {
            guiNewGroupBtn.setEnabled(true);
        }
        
        boolean canRemove = (valueGroupPanels.size() > minOccurs);
        
        //clear
        containerPanel.removeAll();
        
        GridBagConstraints constraint;
        JLabel guiNumberLbl;
        JButton guiRemoveGroupBtn;
        
        int index = 0;
        
        //first all simple parameters
        for (JParameterValueGroupPanel group : valueGroupPanels) {
            final Color color = index % 2 == 0 ? UIManager.getColor("Label.background") 
                    : SwingUtilities.darker(UIManager.getColor("Label.background"), 0.85f);
            if (group != null) {
                
                if (!isUnicGroup) {
                    //////////////////
                    // group number
                    constraint = new GridBagConstraints();
                    constraint.gridx = 0;
                    constraint.gridy = index;
                    constraint.weightx = 0.0;
                    constraint.weighty = 0.0;
                    constraint.fill = GridBagConstraints.BOTH;

                    guiNumberLbl = new JLabel(String.valueOf(index+1)); //start at 1
                    guiNumberLbl.setBorder(new EmptyBorder(0, 10, 0, 10));
                    guiNumberLbl.setOpaque(true);
                    guiNumberLbl.setBackground(color);
                    containerPanel.add(guiNumberLbl, constraint);
                }
                
                ///////////////////
                //group panel
                constraint = new GridBagConstraints();
                constraint.gridx = 1;
                constraint.gridy = index;
                constraint.weightx = 1.0;
                constraint.weighty = 0.0;
                constraint.fill = GridBagConstraints.BOTH;
                if (isUnicGroup) constraint.gridwidth = GridBagConstraints.REMAINDER;
                
                group.setBorder(new LineBorder(color, 2));
                group.setOpaque(false);
                group.setBackground(color);
                containerPanel.add(group, constraint);
                
                //if current Group is root group or if had multiplicity 1 - 1
                if (!isUnicGroup) {
                    ///////////////////
                    //group remove button
                    constraint = new GridBagConstraints();
                    constraint.gridx = 2;
                    constraint.gridy = index;
                    constraint.weightx = 0.0;
                    constraint.weighty = 0.0;
                    constraint.fill = GridBagConstraints.BOTH;

                    final int currentIndex = index;
                    guiRemoveGroupBtn = new FontIconJButton(FontAwesomeIcons.ICON_MINUS, 18, Color.RED);
                    guiRemoveGroupBtn.setBorder(new EmptyBorder(0, 10, 0, 10));
                    guiRemoveGroupBtn.setOpaque(true);
                    guiRemoveGroupBtn.setBackground(color);
                    guiRemoveGroupBtn.setEnabled(canRemove);
                    guiRemoveGroupBtn.setToolTipText(MessageBundle.getString("parameters.removeGroup"));
                    guiRemoveGroupBtn.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            guiRemoveGroupBtnAction(e, currentIndex);
                        }
                    });
                    containerPanel.add(guiRemoveGroupBtn, constraint);
                }
                
                index++;
            }
        }
        
        this.revalidate();
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
    }
}
