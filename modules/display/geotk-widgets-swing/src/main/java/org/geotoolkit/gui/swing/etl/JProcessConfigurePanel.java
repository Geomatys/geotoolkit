/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.gui.swing.etl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.gui.swing.propertyedit.JAttributeEditor;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.chain.ConstantUtilities;
import org.geotoolkit.process.chain.model.ChainDataTypes;
import org.geotoolkit.process.chain.model.Constant;
import org.geotoolkit.process.chain.model.DataLink;
import org.geotoolkit.process.chain.model.ElementProcess;
import org.geotoolkit.process.chain.model.Parameter;
import org.geotoolkit.process.chain.model.event.EventChain;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.geotoolkit.feature.Property;
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
public class JProcessConfigurePanel extends javax.swing.JPanel {

    private static final ImageIcon ICON_OFF = IconBundle.getIcon("16_off");
    private static final ImageIcon ICON_ON = IconBundle.getIcon("16_on");
    private static final int DIVIDER_SIZE = 6;

    private ParameterValueGroup parameter;
    private final ChainScene scene;
    private final EventChain chain;
    private final WElementProcess element;
    private final ProcessDescriptor descriptor;
    private final JDialog optionPaneDialog = new JDialog();

    private boolean displayHelp;
    private int lastHelpPanelSize;
    private int lastParamPanelSize;

    private Map<String, WReceivingParameter> parameterWidget; //map parameter name - widget
    private Map<String, DataLink> parameterLinks; //map parameter name link
    private List<String> parameterActivated; // list of all activated parameters
    private List<String> notSupportedInput; //list of not supported type parameters

    /**
     * Creates new form JProcessConfigurePanel
     */
    public JProcessConfigurePanel(final ChainScene scene, final WElementProcess element) {
        initComponents();

        this.scene = scene;
        this.chain = scene.getChain();
        this.element = element;
        this.parameter = null;

        this.parameterWidget = new HashMap<String, WReceivingParameter>();
        this.parameterLinks = new HashMap<String, DataLink>();
        this.notSupportedInput = new ArrayList<String>();
        this.parameterActivated = new ArrayList<String>();

        descriptor = element.getProcessDescriptor();
        if (!"missing".equals(descriptor.getProcedureDescription().toString())) {
            final ParameterDescriptorGroup inputs = descriptor.getInputDescriptor();
            this.parameter = inputs.createValue();

            final List<WReceivingParameter> wParameters = element.getInputsParameters();
            Collections.reverse(wParameters);

            final MouseAdapter mouseListener = new ParameterMouseListener();
            final FocusListener focusListner = new ParameterFocusListener();
            final GridBagConstraints constraint = new GridBagConstraints();
            JComponent editor = null;

            int index = 0;

            for (final WReceivingParameter wParameter : wParameters) {

                //get parameter infos
                final String paramName = wParameter.getCode();
                final ParameterDescriptor paramDesc = ((ParameterDescriptor) wParameter.getDescriptor());
                final Class paramClass = paramDesc.getValueClass();
                boolean canToggle = true;

                Object value = paramDesc.getDefaultValue();
                parameterWidget.put(paramName, wParameter);

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

                //search links
                final List<DataLink> links = chain.findDataLink(element.getId(), paramName, false);
                if (links != null && links.size() == 1) {
                    final DataLink link = links.get(0);
                    final Object source = link.getSource(chain);
                    parameterLinks.put(paramName, link);

                    /*
                     * Linked to a constant
                     */
                    if (source instanceof Constant) {
                        final Constant cst = (Constant) source;
                        if (!ChainDataTypes.VALID_TYPES.contains(paramClass)) {
                            value = ConstantUtilities.stringToValue(cst.getValue(), paramClass);
                            parameter.parameter(paramName).setValue(value);
                            notSupportedInput.add(paramName);
                            final JTextField notSupportedTf = new JTextField(MessageBundle.getString("inputNotSupported"));
                            notSupportedTf.setEnabled(false);
                            editor.add(BorderLayout.CENTER, notSupportedTf);
                            canToggle = false;
                        } else {
                            value = ConstantUtilities.stringToValue(cst.getValue(), paramClass);
                            parameter.parameter(paramName).setValue(value);
                            ((JAttributeEditor) editor).setProperty(FeatureUtilities.toProperty(parameter.parameter(paramName)));
                            parameterActivated.add(paramName);
                            canToggle = checkEditorFound(editor, paramName);
                            addRecursivelyFocusListener(editor, focusListner);
                        }

                        /*
                         * Linked to a process output or chain input. -> Disable textfield
                         */
                    } else if(source instanceof ElementProcess || source instanceof Parameter) {

                        if (source instanceof ElementProcess) {
                            final ElementProcess chainElement = (ElementProcess) source;
                            final JTextField field = new JTextField(chainElement.getCode()+" - "+link.getSourceCode());
                            field.setEnabled(false);
                            editor.add(BorderLayout.CENTER, field);

                        } else {
                            final Parameter inputParam = (Parameter) source;
                            final JTextField field = new JTextField(chain.getName()+" - "+inputParam.getCode());
                            field.setEnabled(false);
                            editor.add(BorderLayout.CENTER, field);
                        }
                        canToggle = false;
                    }

                } else {
                    /*
                     * No link -> create a default JAttributeEditor if parameter supported
                     */
                    parameter.parameter(paramName).setValue(value);

                    if (!ChainDataTypes.VALID_TYPES.contains(paramClass)) {
                        notSupportedInput.add(paramName);
                        final JTextField notSupportedTf = new JTextField(MessageBundle.getString("inputNotSupported"));
                        notSupportedTf.setEnabled(false);
                        editor.add(BorderLayout.CENTER, notSupportedTf );
                        canToggle = false;
                    } else {
                        ((JAttributeEditor) editor).setProperty(FeatureUtilities.toProperty(parameter.parameter(paramName)));
                        canToggle = checkEditorFound(editor, paramName);
                        addRecursivelyFocusListener(editor, focusListner);
                    }
                }

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
        }

        updateParameterEditorState();
        this.displayHelp = true;
        guiToggleHelpBtn.setText(MessageBundle.getString("hideHelp"));
        setHelpContent(descriptor);
        this.guiParameterTitleLabel.setText(MessageBundle.getString("processInputParameters") + descriptor.getIdentifier().getCode() + " : ");
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
                guiToggleHelpBtn.setText(MessageBundle.getString("hideHelp"));
                optionPaneDialog.setSize(lastParamPanelSize + lastHelpPanelSize + DIVIDER_SIZE, optionPaneDialog.getHeight());

            } else {
                lastParamPanelSize = guiSplitPane.getDividerLocation() - (DIVIDER_SIZE / 2);
                lastHelpPanelSize = optionPaneDialog.getWidth() - guiSplitPane.getDividerLocation() - DIVIDER_SIZE;
                guiSplitPane.setDividerLocation(lastParamPanelSize + (DIVIDER_SIZE / 2));
                guiSplitPane.setDividerSize(0);
                guiHelpPane.setVisible(false);
                guiToggleHelpBtn.setText(MessageBundle.getString("showHelp"));
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
                mandatory = MessageBundle.getString("editorHelpMandatory");
            } else {
                mandatory = MessageBundle.getString("editorHelpOptional");
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
            sb.append("<li>").append(MessageBundle.getString("editorHelpMandatoryLabel")).append(" : ").append(mandatory).append("</li>");
        }
        if (dataType != null) {
            sb.append("<li>").append(MessageBundle.getString("editorHelpTypeLabel")).append(" : ").append(dataType).append("</li>");
        }
        if (defaultValue != null) {
            sb.append("<li>").append(MessageBundle.getString("editorHelpDefaultLabel")).append(" : ").append(defaultValue).append("</li>");
        }
        if (validValues != null) {
            sb.append("<li>").append(MessageBundle.getString("editorHelpValidLabel")).append(" : ").append(Arrays.toString(validValues)).append("</li>");
        }
        if (dataUnits != null) {
            sb.append("<li>").append(MessageBundle.getString("editorHelpUnitLabel")).append(" : ").append(dataUnits).append("</li>");
        }
        sb.append("</ul>");
        sb.append("<h3>").append(MessageBundle.getString("editorHelpDescriptionLabel")).append(" : ").append("</h3>");
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

        final List<GeneralParameterDescriptor> paramDescList = descriptor.getInputDescriptor().descriptors();
        for (GeneralParameterDescriptor paramDesc : paramDescList) {

            final String paramName = paramDesc.getName().getCode();

            //skip not supported inputs
            if (!notSupportedInput.contains(paramName) && parameterActivated.contains(paramName)) {
                //skip links not from Constant
                final DataLink link = parameterLinks.get(paramName);
                if (link != null) {
                    final Object source = link.getSource(chain);
                    if (! (source instanceof Constant)) {
                        continue;
                    }
                }

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
     * Create constants and links in chain and scene.
     */
    private void buildUpdateConstants() {

        for (final GeneralParameterDescriptor generalParamDesc : descriptor.getInputDescriptor().descriptors()) {
            final ParameterDescriptor paramDesc = (ParameterDescriptor) generalParamDesc;
            final ParameterValue paramValue = parameter.parameter(paramDesc.getName().getCode());
            final WReceivingParameter paramWidget = parameterWidget.get(paramDesc.getName().getCode());
            final String paramName = paramDesc.getName().getCode();

            //parameter value not null and different from default value.
            if (paramValue.getValue() != null) {
                if (paramValue.getValue() instanceof String && ((String)paramValue.getValue()).isEmpty()) {
                    continue;
                }
                final DataLink link = parameterLinks.get(paramName);

                Constant candidate = null;

                if (link != null) {
                    //delete constant and link
                    final Object source = link.getSource(chain);
                    if (source instanceof Constant) {
                        final Constant cst = (Constant) source;
                        if (chain.findDataLink(cst.getId(), null, true).size() == 1) {
                            //if a constant was already linked to the parameter and have only one link
                            //kepp constant to update her or remove her if associate parameter is activated or not.
                            if (parameterActivated.contains(paramName)) {
                                candidate = cst;
                            } else {
                                chain.getConstants().remove(cst);
                            }
                        }
                        chain.getDataLinks().remove(link);
                    }
                }

                if (parameterActivated.contains(paramName)) {

                    final String constantValue = ConstantUtilities.valueToString(paramValue.getValue());

                    //create a new constant with her link.
                    if (candidate == null) {

                        // compute scene location of paramWidget.
                        final Point pinpointLocation = paramWidget != null ? paramWidget.getLocation() : element.getLocation();
                        Widget parent = element;
                        while (parent != null && !(parent instanceof Scene)) {
                            if (parent.getPreferredLocation() != null) {
                                pinpointLocation.x += parent.getPreferredLocation().x;
                                pinpointLocation.y += parent.getPreferredLocation().y;
                            } else {
                                pinpointLocation.x += parent.getLocation().x;
                                pinpointLocation.y += parent.getLocation().y;
                            }
                            parent = parent.getParentWidget();
                        }

                        // Add constant to chain a drow it a paramWidget location. find Wconstant component to get his size and replace him at paramPos - WConstant size.
                        final Constant constant = new Constant(chain.getNextId(), paramDesc.getValueClass(), constantValue, pinpointLocation.x, pinpointLocation.y);
                        chain.getConstants().add(constant);
                        chain.getDataLinks().add(new DataLink(constant.getId(), (String)null, element.getId(), paramName));
                        scene.validate();
                        final Widget wConst = scene.getWidget(constant);
                        if (wConst != null) {
                            final Double width = wConst.getBounds().getWidth();
                            final Double height = wConst.getBounds().getHeight();

                            wConst.setPreferredLocation(new Point(pinpointLocation.x - width.intValue() - 8, pinpointLocation.y - (height.intValue()/2) - 2));
                            scene.validate();
                        }
                    } else {
                        //update constant and recreate her link.
                        candidate.setValue(constantValue);
                        chain.getDataLinks().add(new DataLink(candidate.getId(), (String)null, element.getId(), paramName));
                        scene.validate();
                    }
                }
            }
        }
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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
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
                .addComponent(paramScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE))
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
            .addComponent(guiSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 477, Short.MAX_VALUE)
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

    /**
     * Open JDialog of editor.
     */
    public void showDialog() {

        final JOptionPane optPane = new JOptionPane(this,
                JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);

        optPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals("value")) {

                    switch ((Integer) e.getNewValue()) {
                        case JOptionPane.OK_OPTION:

                            updateProcessParameters();
                            final List<ParameterValue> unvalidParam = validateParam();
                            if (unvalidParam.isEmpty()) {
                                buildUpdateConstants();
                                optionPaneDialog.dispose();
                            } else {
                                showParamError(unvalidParam);
                            }
                            break;
                        case JOptionPane.CANCEL_OPTION:
                            optionPaneDialog.dispose();
                            break;

                    }
                }
            }
        });
        optionPaneDialog.setTitle(MessageBundle.getString("configProcessTitle")+descriptor.getIdentifier().getCode());
        optionPaneDialog.setContentPane(optPane);
        optionPaneDialog.pack();
        optionPaneDialog.setResizable(true);
        optionPaneDialog.setLocationRelativeTo(null);
        optionPaneDialog.setModal(true);
        optionPaneDialog.setVisible(true);
        updateHelpComponent(true);
    }

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
            final JTextField notSupportedTf = new JTextField(MessageBundle.getString("inputNotSupported"));
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
                    final ParameterDescriptor paramDesc = (ParameterDescriptor) descriptor.getInputDescriptor().descriptor(parameterName);
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
                final ParameterDescriptor paramDesc = (ParameterDescriptor)descriptor.getInputDescriptor().descriptor(parameterName);
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
                final ParameterDescriptor paramDesc = (ParameterDescriptor)descriptor.getInputDescriptor().descriptor(parameterName);
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
