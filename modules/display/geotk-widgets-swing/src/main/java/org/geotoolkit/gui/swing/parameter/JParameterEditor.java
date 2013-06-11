package org.geotoolkit.gui.swing.parameter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.gui.swing.propertyedit.JAttributeEditor;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.ProcessDescriptor;
import org.opengis.feature.Property;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Panel used to configure a process (ChainElement) and create constants.
 *
 * @author Quentin Boileau (Geomatys)
 */
public class JParameterEditor extends javax.swing.JPanel {

    private static final ImageIcon ICON_OFF = IconBundle.getIcon("16_off");
    private static final ImageIcon ICON_ON = IconBundle.getIcon("16_on");
    private static final int DIVIDER_SIZE = 6;

    private ParameterDescriptorGroup descriptor;
    private ParameterValueGroup parameter;
    private final JDialog optionPaneDialog = new JDialog();

    private boolean displayHelp;
    private int lastHelpPanelSize;
    private int lastParamPanelSize;

    private List<String> parameterActivated; // list of all activated parameters
    private List<String> notSupportedInput; //list of not supported type parameters

    /**
     * Creates new form JProcessConfigurePanel
     */
    public JParameterEditor() {
        initComponents();
    }
    
    public void setEdited(ParameterValueGroup parameter){
        guiParametersGridPane.removeAll();
        
        this.parameter = parameter;
        this.descriptor = parameter.getDescriptor();
        this.notSupportedInput = new ArrayList<String>();
        this.parameterActivated = new ArrayList<String>();



        final MouseAdapter mouseListener = new ParameterMouseListener();
        final FocusListener focusListner = new ParameterFocusListener();
        final GridBagConstraints constraint = new GridBagConstraints();
        JComponent editor = null;

        int index = 0;

        for (final GeneralParameterDescriptor wParameter : descriptor.descriptors()) {

            //get parameter infos
            final String paramName = wParameter.getName().getCode();
            final ParameterDescriptor paramDesc = (ParameterDescriptor) wParameter;
            boolean canToggle = true;

            Object value = paramDesc.getDefaultValue();

            /*
             * PARAMETER LABEL
             */
            final JLabel label = new JLabel(paramName);
            label.setName(paramName);
            label.setOpaque(true);

            if (paramDesc.getMinimumOccurs() == 1 && paramDesc.getMaximumOccurs() == 1) {
                final Font font = label.getFont();
                label.setFont(font.deriveFont(font.getStyle() ^ Font.BOLD));
                label.setText(label.getText() + " *");
                parameterActivated.add(paramName); //mandatory parameters always activate
                canToggle = false;
            }
            label.addMouseListener(mouseListener);
            constraint.gridx = 0;
            constraint.gridy = index;
            constraint.weightx = 0.0;
            constraint.weighty = 0.0;
            constraint.fill = GridBagConstraints.BOTH;

            //add to panel
            guiParametersGridPane.add(label, constraint);

            /*
             * PARAMETER EDITOR
             */
            //init wih a panel
            editor = new JAttributeEditor();
            editor.setName(paramName);

            /*
             * create a default JAttributeEditor if parameter supported
             */
            parameter.parameter(paramName).setValue(value);

            ((JAttributeEditor) editor).setProperty(FeatureUtilities.toProperty(parameter.parameter(paramName)));
            canToggle = checkEditorFound(editor, paramName);
            addRecursivelyFocusListener(editor, focusListner);

            constraint.gridx = 1;
            constraint.gridy = index;
            constraint.weightx = 1.0;
            constraint.weighty = 0.0;
            constraint.fill = GridBagConstraints.HORIZONTAL;

            //add to panel
            guiParametersGridPane.add(editor, constraint);

            /*
             * PARAMETER TOGGLE ENABLE/DISABLE EDITOR
             */
            JComponent toggle = null;
            if (canToggle) {
                if (parameterActivated.contains(paramName)) {
                    toggle = new JButton(ICON_ON);
                }else {
                    toggle = new JButton(ICON_OFF);
                }
                ((JButton)toggle).setBorderPainted(false);
                ((JButton)toggle).setContentAreaFilled(false);
                toggle.setName(paramName);
                toggle.addMouseListener(mouseListener);
            } else {
                toggle = new JPanel();
                toggle.setSize(20, 16);
            }

            constraint.gridx = 2;
            constraint.gridy = index;
            constraint.weightx = 0.0;
            constraint.weighty = 0.0;
            constraint.fill = GridBagConstraints.HORIZONTAL;

            guiParametersGridPane.add(toggle, constraint);
            index++;
        }

        updateParameterEditorState();
        this.displayHelp = true;
        guiToggleHelpBtn.setText("hideHelp");
        setHelpContent(descriptor);
        this.guiParameterTitleLabel.setText(descriptor.getName().getCode() + " : ");
        
    }
    

    /**
     * Show or hide help panel.
     *
     * @param init
     */
    private void updateHelpComponent(final boolean init) {
        if (init) {
            guiSplitPane.setDividerSize(DIVIDER_SIZE);
            guiSplitPane.setDividerLocation(0.75);
            this.lastParamPanelSize = guiSplitPane.getDividerLocation() - (DIVIDER_SIZE / 2);
            this.lastHelpPanelSize = optionPaneDialog.getWidth() - guiSplitPane.getDividerLocation() - DIVIDER_SIZE;

        } else {
            if (displayHelp) {
                guiSplitPane.setDividerSize(DIVIDER_SIZE);
                guiSplitPane.setDividerLocation(lastParamPanelSize + (DIVIDER_SIZE / 2));
                guiHelpPane.setVisible(true);
                guiToggleHelpBtn.setText("hideHelp");
                optionPaneDialog.setSize(lastParamPanelSize + lastHelpPanelSize + DIVIDER_SIZE, optionPaneDialog.getHeight());

            } else {
                lastParamPanelSize = guiSplitPane.getDividerLocation() - (DIVIDER_SIZE / 2);
                lastHelpPanelSize = optionPaneDialog.getWidth() - guiSplitPane.getDividerLocation() - DIVIDER_SIZE;
                guiSplitPane.setDividerLocation(lastParamPanelSize + (DIVIDER_SIZE / 2));
                guiSplitPane.setDividerSize(0);
                guiHelpPane.setVisible(false);
                guiToggleHelpBtn.setText("showHelp");
                optionPaneDialog.setSize(lastParamPanelSize + (DIVIDER_SIZE / 2), optionPaneDialog.getHeight());
            }
        }
        optionPaneDialog.repaint();
    }

    /**
     * Toogle help panel state and update view.
     */
    private void toggleHelp() {
        this.displayHelp = !displayHelp;
        updateHelpComponent(false);
    }

    /**
     * Set the help panel content.
     *
     * @param descriptor can be a ProcessDescriptor or a ParameterDescriptor.
     */
    void setHelpContent(final Object descriptor) {

        String title = null;
        String description = null;
        String mandatory = null;
        String defaultValue = null;
        String[] validValues = null;
        String dataType = null;
        String dataUnits = null;

        if (descriptor instanceof ProcessDescriptor) {
            final ProcessDescriptor processDesc = (ProcessDescriptor) descriptor;
            title = processDesc.getIdentifier().getCode();
            description = processDesc.getProcedureDescription().toString();
        } else if (descriptor instanceof ParameterDescriptor) {
            final ParameterDescriptor paramDesc = (ParameterDescriptor) descriptor;

            title = paramDesc.getName().getCode();
            description = paramDesc.getRemarks() != null ? paramDesc.getRemarks().toString() : null;
            dataType = paramDesc.getValueClass().getSimpleName();
            if (paramDesc.getMinimumOccurs() == 1 && paramDesc.getMaximumOccurs() == 1) {
                mandatory = "editorHelpMandatory";
            } else {
                mandatory = "editorHelpOptional";
            }
            defaultValue = String.valueOf(paramDesc.getDefaultValue());
            final Set valueSet = paramDesc.getValidValues();
            if (valueSet != null) {
                List<String> valueString = new ArrayList<String>();
                for (Object object : valueSet) {
                    valueString.add(String.valueOf(object));
                }
                validValues = valueString.toArray(new String [valueString.size()]);
            }
            dataUnits = paramDesc.getUnit() != null ? paramDesc.getUnit().toString() : null;
        }

        //create html string
        final StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<h1>").append(title).append("</h1>");
        sb.append("<hr/>");
        sb.append("<ul>");
        if (mandatory != null) {
            sb.append("<li>").append("editorHelpMandatoryLabel").append(" : ").append(mandatory).append("</li>");
        }
        if (dataType != null) {
            sb.append("<li>").append("editorHelpTypeLabel").append(" : ").append(dataType).append("</li>");
        }
        if (defaultValue != null) {
            sb.append("<li>").append("editorHelpDefaultLabel").append(" : ").append(defaultValue).append("</li>");
        }
        if (validValues != null) {
            sb.append("<li>").append("editorHelpValidLabel").append(" : ").append(Arrays.toString(validValues)).append("</li>");
        }
        if (dataUnits != null) {
            sb.append("<li>").append("editorHelpUnitLabel").append(" : ").append(dataUnits).append("</li>");
        }
        sb.append("</ul>");
        sb.append("<h3>").append("editorHelpDescriptionLabel").append(" : ").append("</h3>");
        sb.append("<p>").append(description).append("</p>");
        sb.append("</html>");
        guiHelpTextPane.setText(sb.toString());
    }

    /**
     * Extract parameters from form.
     */
    private void updateProcessParameters() {

        final Component[] components = guiParametersGridPane.getComponents();
        for (Component component : components) {
            if (component instanceof JAttributeEditor) {
                final JAttributeEditor editor = (JAttributeEditor) component;
                final String paramName = editor.getName();

                if (parameterActivated.contains(paramName)) {
                    if (editor.getProperty() != null) {
                        final Property prop = editor.getProperty();
                        final Object value = prop.getValue();
                        parameter.parameter(paramName).setValue(value);
                    }
                }
            }
        }
    }

    /**
     * Validate parameters.
     * @return a list of invalid ParameterValue. If empty, configuration is valid.
     */
    private List<ParameterValue> validateParam() {
        final List<ParameterValue> unvalidParameters = new ArrayList<ParameterValue>();

        final List<GeneralParameterDescriptor> paramDescList = descriptor.descriptors();
        for (GeneralParameterDescriptor paramDesc : paramDescList) {
            final String paramName = paramDesc.getName().getCode();
            //skip not supported inputs
            if (!notSupportedInput.contains(paramName) && parameterActivated.contains(paramName)) {
                //skip links not from Constant
                final ParameterValue paramVal = parameter.parameter(paramName);
                final boolean valid = Parameters.isValid(paramVal);
                if (!valid) {
                    unvalidParameters.add(paramVal);
                }
            }
        }
        return unvalidParameters;
    }

    /**
     * Show error on parameters.
     * @param unvalidParam
     */
    private void showParamError(final List<ParameterValue> unvalidParam) {
        final Component[] components = guiParametersGridPane.getComponents();

        for (Component component : components) {

            for (ParameterValue unvalid : unvalidParam) {
                final String paramName = unvalid.getDescriptor().getName().getCode();
                if (component instanceof JLabel) {
                    final JLabel label = (JLabel) component;
                    if (paramName.equals(label.getName())) {
                        label.setForeground(Color.red);
                    } else {
                        label.setForeground(UIManager.getColor("Label.foreground"));
                    }
                }
            }
        }
    }

    /**
     * Add a Focus listener to all child of a component recursively.
     * @param component root component.
     * @param focusListner listener
     */
    private void addRecursivelyFocusListener(final JComponent component, final FocusListener focusListner) {
        component.addFocusListener(focusListner);
        for (final Component child : component.getComponents()) {
            if (child instanceof JComponent) {
                addRecursivelyFocusListener((JComponent)child, focusListner);
            }
        }
    }

    /**
     * For each parameter editor, check if associate parameter is supported and activate.
     * If parameter is not supported or not activate, disable editor.
     * If parameter is supported and activate enable editor.
     */
    private void updateParameterEditorState() {
        final Component[] sibilings = guiParametersGridPane.getComponents();
        for (final Component component : sibilings) {
            if (component instanceof JAttributeEditor) {
                final JAttributeEditor editor = (JAttributeEditor) component;
                if (editor.getProperty() != null) {
                    final String editorParameterName = editor.getProperty().getDescriptor().getName().getLocalPart();
                    if (parameterActivated.contains(editorParameterName) && !notSupportedInput.contains(editorParameterName)) {
                        setRecursivelyEnable(editor, true);
                    } else {
                        setRecursivelyEnable(editor, false);
                    }
                } else {
                    setRecursivelyEnable(editor, false);
                }
            }
        }
        guiParametersGridPane.repaint();
    }

    /**
     * REcursively set component and his children enable or disable.
     * @param component root component
     * @param enable true to enable components, false to disable them.
     */
    private void setRecursivelyEnable (final JComponent component, final boolean enable) {
        component.setEnabled(enable);
        for (final Component child : component.getComponents()) {
            if (child instanceof JComponent) {
                setRecursivelyEnable((JComponent)child, enable);
            }
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

        guiSplitPane = new javax.swing.JSplitPane();
        guiHelpPane = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        guiHelpTextPane = new javax.swing.JTextPane();
        guiParameterPane = new javax.swing.JPanel();
        paramScrollPane = new javax.swing.JScrollPane();
        paramPanel = new javax.swing.JPanel();
        guiParametersGridPane = new javax.swing.JPanel();
        guiToggleHelpBtn = new javax.swing.JToggleButton();
        guiParameterTitleLabel = new javax.swing.JLabel();

        guiSplitPane.setDividerLocation(550);

        guiHelpPane.setBackground(new java.awt.Color(254, 254, 254));

        guiHelpTextPane.setEditable(false);
        guiHelpTextPane.setContentType("text/html"); // NOI18N
        guiHelpTextPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setViewportView(guiHelpTextPane);

        javax.swing.GroupLayout guiHelpPaneLayout = new javax.swing.GroupLayout(guiHelpPane);
        guiHelpPane.setLayout(guiHelpPaneLayout);
        guiHelpPaneLayout.setHorizontalGroup(
            guiHelpPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
        );
        guiHelpPaneLayout.setVerticalGroup(
            guiHelpPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 481, Short.MAX_VALUE)
        );

        guiSplitPane.setRightComponent(guiHelpPane);

        paramPanel.setLayout(new java.awt.BorderLayout());

        guiParametersGridPane.setLayout(new java.awt.GridBagLayout());
        paramPanel.add(guiParametersGridPane, java.awt.BorderLayout.NORTH);

        paramScrollPane.setViewportView(paramPanel);

        org.openide.awt.Mnemonics.setLocalizedText(guiToggleHelpBtn, "toggleHelp"); // NOI18N
        guiToggleHelpBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guiToggleHelpBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(guiParameterTitleLabel, "title"); // NOI18N

        javax.swing.GroupLayout guiParameterPaneLayout = new javax.swing.GroupLayout(guiParameterPane);
        guiParameterPane.setLayout(guiParameterPaneLayout);
        guiParameterPaneLayout.setHorizontalGroup(
            guiParameterPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(guiParameterPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(guiParameterTitleLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(guiToggleHelpBtn))
            .addComponent(paramScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
        );
        guiParameterPaneLayout.setVerticalGroup(
            guiParameterPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(guiParameterPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(guiParameterPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(guiToggleHelpBtn)
                    .addComponent(guiParameterTitleLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paramScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE))
        );

        guiSplitPane.setLeftComponent(guiParameterPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(guiSplitPane, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(guiSplitPane)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void guiToggleHelpBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guiToggleHelpBtnActionPerformed
        toggleHelp();
    }//GEN-LAST:event_guiToggleHelpBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel guiHelpPane;
    private javax.swing.JTextPane guiHelpTextPane;
    private javax.swing.JPanel guiParameterPane;
    private javax.swing.JLabel guiParameterTitleLabel;
    private javax.swing.JPanel guiParametersGridPane;
    private javax.swing.JSplitPane guiSplitPane;
    private javax.swing.JToggleButton guiToggleHelpBtn;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel paramPanel;
    private javax.swing.JScrollPane paramScrollPane;
    // End of variables declaration//GEN-END:variables

//    /**
//     * Open JDialog of editor.
//     */
//    public void showDialog() {
//
//        final JOptionPane optPane = new JOptionPane(this,
//                JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
//
//        optPane.addPropertyChangeListener(new PropertyChangeListener() {
//            public void propertyChange(PropertyChangeEvent e) {
//                if (e.getPropertyName().equals("value")) {
//
//                    switch ((Integer) e.getNewValue()) {
//                        case JOptionPane.OK_OPTION:
//
//                            updateProcessParameters();
//                            final List<ParameterValue> unvalidParam = validateParam();
//                            if (unvalidParam.isEmpty()) {
//                                buildUpdateConstants();
//                                optionPaneDialog.dispose();
//                            } else {
//                                showParamError(unvalidParam);
//                            }
//                            break;
//                        case JOptionPane.CANCEL_OPTION:
//                            optionPaneDialog.dispose();
//                            break;
//
//                    }
//                }
//            }
//        });
//        optionPaneDialog.setTitle(NbBundle.getMessage(ChainTableAction.class, "configProcessTitle", descriptor.getIdentifier().getCode()));
//        optionPaneDialog.setContentPane(optPane);
//        optionPaneDialog.pack();
//        optionPaneDialog.setResizable(true);
//        optionPaneDialog.setLocationRelativeTo(null);
//        optionPaneDialog.setModal(true);
//        optionPaneDialog.setVisible(true);
//        updateHelpComponent(true);
//    }

    /**
     * Check if JAttributeEditor found an editor for this input.
     * If not create a disable textfield Not supported input and return false.
     * @param editor
     * @param paramName
     * @return false if editor not found, true otherwise.
     */
    private boolean checkEditorFound(JComponent editor, String paramName) {
        if (editor.getComponentCount() == 0) {
            parameterActivated.remove(paramName);
            notSupportedInput.add(paramName);
            final JTextField notSupportedTf = new JTextField("inputNotSupported");
            notSupportedTf.setEnabled(false);
            editor.add(BorderLayout.CENTER, notSupportedTf );
            return false;
        }
        return true;
    }

    /**
     * Mouse listener for parameter table.
     */
    private class ParameterMouseListener extends MouseAdapter {

        @Override
        public void mouseEntered(MouseEvent e) {

            final JComponent source = (JComponent) e.getSource();
            if (!(source instanceof JButton)) {
                final String parameterName = source.getName();

                if (parameterName != null) {
                    final ParameterDescriptor paramDesc = (ParameterDescriptor) descriptor.descriptor(parameterName);
                    setHelpContent(paramDesc);
                }
                if (source != null) {
                    final Component[] sibilings = guiParametersGridPane.getComponents();
                    for (final Component component : sibilings) {
                        if (component instanceof JLabel) {
                            final JLabel label = (JLabel) component;
                            if (label.getName().equals(parameterName)) {
                                source.setBackground(UIManager.getColor("List.selectionBackground"));
                            } else {
                                component.setBackground(UIManager.getColor("Label.background"));
                            }
                        }
                    }
                    guiParametersGridPane.repaint();
                }
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            final Component[] sibilings = guiParametersGridPane.getComponents();
            for (final Component component : sibilings) {
                if (component instanceof JLabel) {
                    component.setBackground(UIManager.getColor("Label.background"));
                }
            }
            setHelpContent(descriptor);
        }

        /**
         * Event used to toggle editor state (enable/disable)
         * @param e
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() instanceof JButton) {

                final JButton btn = (JButton) e.getSource();
                final String paramName = btn.getName();

                if (parameterActivated.contains(paramName)) {
                    btn.setIcon(ICON_OFF);
                    parameterActivated.remove(paramName);
                } else {
                    btn.setIcon(ICON_ON);
                    parameterActivated.add(paramName);
                }
                updateParameterEditorState();
            }
        }
    }

    /**
     * FocusListener used by parameter editor.
     */
    private class ParameterFocusListener extends FocusAdapter {

        @Override
        public void focusGained(FocusEvent e) {
            final JAttributeEditor editor = findAttributEditor((JComponent)e.getSource());
            if (editor != null) {
                final String parameterName = editor.getProperty().getDescriptor().getName().getLocalPart();

                // update help panel
                final ParameterDescriptor paramDesc = (ParameterDescriptor)descriptor.descriptor(parameterName);
                setHelpContent(paramDesc);

                //color field label background
                final Component[] sibilings = guiParametersGridPane.getComponents();
                for (final Component component : sibilings) {
                    if (component instanceof JLabel) {
                        if (((JLabel) component).getName().equals(parameterName)) {
                            component.setBackground(UIManager.getColor("List.selectionBackground"));
                        } else {
                            component.setBackground(UIManager.getColor("Label.background"));
                        }
                    }
                }
                editor.setBackground(UIManager.getColor("List.selectionBackground"));
            }
            super.focusGained(e);
        }

        @Override
        public void focusLost(FocusEvent e) {
            final JAttributeEditor editor = findAttributEditor((JComponent)e.getSource());
            if (editor != null) {
                final String parameterName = editor.getProperty().getDescriptor().getName().getLocalPart();

                // update help panel
                final ParameterDescriptor paramDesc = (ParameterDescriptor)descriptor.descriptor(parameterName);
                setHelpContent(paramDesc);

                //color field label background
                final Component[] sibilings = guiParametersGridPane.getComponents();
                for (final Component component : sibilings) {
                    if (component instanceof JLabel) {
                        if (((JLabel) component).getName().equals(parameterName)) {
                            component.setBackground(UIManager.getColor("Label.background"));
                        }
                    }
                }
                editor.setBackground(UIManager.getColor("Desktop.background"));
            }
            super.focusLost(e);
        }

        private JAttributeEditor findAttributEditor(JComponent component) {
            if (component instanceof JAttributeEditor || component.equals(guiParametersGridPane)) {
                if (component instanceof JAttributeEditor) {
                    return (JAttributeEditor)component;
                } else {
                    return null;
                }
            } else {
                return findAttributEditor((JComponent)component.getParent());
            }
        }
    }

}
