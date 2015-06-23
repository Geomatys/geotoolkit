/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.gui.javafx.parameter;

import java.awt.Color;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.internal.GeotkFX;
import org.geotoolkit.parameter.ParameterGroup;
import org.geotoolkit.utility.parameter.ParametersExt;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.InternationalString;

/**
 * A configurable panel for edition of parameter groups and their contained
 * parameter values with a responsive design.
 *
 * TODO : ADD A "REMOVE" BUTTON FOR PARAMETER EDITOR.
 * TODO : ADD a button on root component which show descriptor group structure.
 * TODO : IMPROVE RESIZING RULES.
 *
 * Note : ISO-111 defines {@link ParameterValue} as unitary. However, SIS
 * implementation allows for their multiplicity (even if it does not provide any
 * commodity method), so we allow it too.
 *
 * @author Alexis Manin (Geomatys)
 */
public class FXParameterGroupPane extends BorderPane {

    private static final String FLAT_BUTTON_CLASS = "flatbutton";
    private static final String INFO_BUTTON_CLASS = "infobutton";
    private static final String DESCRIPTOR_CONTAINER_CLASS = "descriptor-container";
    private static final String DESCRIPTOR_CONTENT_CLASS = "descriptor-content";
    private static final String PARAMETER_EDITOR_CLASS = "parameter-editor";
    private static final String ROOT_CLASS = "root";
    private static final String INFO_LABEL_CLASS = "infolabel";
    
    private static final Image ICON_PLUS = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_PLUS, 16, new Color(74,123,165)), null);
    private static final Image ICON_INFO = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_INFO, 16, Color.WHITE), null);
    private static final Image ICON_MORE = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_TH_LIST, 16, Color.BLACK), null);
    
    private static final String ADVANCED_VIEW_TOOLTIP = GeotkFX.getString("org.geotoolkit.gui.javafx.parameter.advancedViewTooltip");
    private static final String SIMPLE_VIEW_TOOLTIP = GeotkFX.getString("org.geotoolkit.gui.javafx.parameter.simpleViewTooltip");
    
    /**
     * Flow pane in which we'll add all {@link ParameterValue} editors. 
     */
    @FXML
    private TilePane uiInnerValues;
    
    /**
     * Flow pane in which we'll add all {@link ParameterGroup} editors. 
     */
    @FXML
    private FlowPane uiInnerGroups;

    @FXML
    private MenuButton uiAddBtn;

    @FXML
    private ToggleButton uiAdvancedBtn;

    public final SimpleObjectProperty<ParameterValueGroup> inputGroup = new SimpleObjectProperty<>();

    private final HashMap<String, DescriptorPanel> editionGroups = new HashMap<>();

    private final SimpleIntegerProperty optionalParameterCount = new SimpleIntegerProperty(0);
    
    /**
     * A popup displayed when information button is clicked. It shows chosen parameter description.
     */
    private final Popup descriptionPopup = new Popup();
    private final Label infoLabel = new Label();
    
    public FXParameterGroupPane() {
        super();
        GeotkFX.loadJRXML(this, this.getClass(), false);
        getStyleClass().add(ROOT_CLASS);
        
        uiAdvancedBtn.setGraphic(new ImageView(ICON_MORE));
        uiAdvancedBtn.visibleProperty().bind(optionalParameterCount.greaterThan(0));
        uiAdvancedBtn.managedProperty().bind(uiAdvancedBtn.visibleProperty());
        uiAdvancedBtn.setTooltip(new Tooltip(ADVANCED_VIEW_TOOLTIP));
        uiAdvancedBtn.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            if (Boolean.TRUE.equals(newValue)) {
                uiAdvancedBtn.setTooltip(new Tooltip(SIMPLE_VIEW_TOOLTIP));
            } else if (Boolean.FALSE.equals(newValue)) {
                uiAdvancedBtn.setTooltip(new Tooltip(ADVANCED_VIEW_TOOLTIP));
            } else {
                uiAdvancedBtn.setTooltip(null);
            }
        });
        
        uiAddBtn.setGraphic(new ImageView(ICON_PLUS));
        uiAddBtn.visibleProperty().bind(uiAdvancedBtn.selectedProperty().and(Bindings.isNotEmpty(uiAddBtn.getItems())));
        uiAddBtn.managedProperty().bind(uiAddBtn.visibleProperty());

        inputGroup.addListener(this::updateParameterGroup);
        
        descriptionPopup.setAutoHide(true);
        infoLabel.getStylesheets().addAll(this.getStylesheets());
        infoLabel.getStyleClass().add(INFO_LABEL_CLASS);
        descriptionPopup.getContent().add(infoLabel);
    }

    public FXParameterGroupPane(ParameterValueGroup group) {
        this();
        inputGroup.set(group);
    }

    public void updateParameterGroup(ObservableValue<? extends ParameterValueGroup> observable, ParameterValueGroup oldValue, ParameterValueGroup newValue) {
        // Remove all content from panel.
        uiInnerValues.getChildren().clear();
        uiInnerGroups.getChildren().clear();
        uiAddBtn.getItems().clear();
        editionGroups.clear();
        optionalParameterCount.set(0);

        if (newValue != null) {
            final ParameterDescriptorGroup newDescriptor = newValue.getDescriptor();
            // Optional or multi-occurence descriptors. We allow user to add more in editor.
            for (final GeneralParameterDescriptor childDesc : newDescriptor.descriptors()) {
                if ((childDesc.getMaximumOccurs() - childDesc.getMinimumOccurs()) > 0) {
                    uiAddBtn.getItems().add(new AddMenuItem(childDesc));
                    optionalParameterCount.set(optionalParameterCount.get()+1);
                    // Default valued parameters are not displayed in simple mode.
                } else if ((childDesc instanceof ParameterDescriptor) && 
                        ((ParameterDescriptor)childDesc).getDefaultValue() != null) {
                    optionalParameterCount.set(optionalParameterCount.get()+1);
                }
            }

            for (GeneralParameterValue parameter : newValue.values()) {
                GeneralParameterDescriptor descriptor = parameter.getDescriptor();
                final DescriptorPanel descriptorPanel = getOrCreateDescriptorPanel(descriptor);
                descriptorPanel.addEditor(parameter);
            }
        }
    }

    /**
     * Try to get an editor for given input parameter value. If we can find one,
     * it's integrated into given grid pane at specified row.
     *
     * Note : By default, non-editable parameters are ignored, and optional ones
     * (and the ones with a default value) are marked as visible only in
     * advanced view.
     *
     * @param value Parameter to find an editor for.
     * @return Next available row indice, or the same as input if no editor has
     * been inserted.
     */
    protected Optional<Node> getValueEditor(final ParameterValue value) {
        final ParameterDescriptor descriptor = value.getDescriptor();

        Optional<FXValueEditor> opt = FXValueEditorSpi.findEditor(descriptor);
        if (opt.isPresent()) {
            final FXValueEditor editor = opt.get();
            // Bind editor input to parameter value.
            if (descriptor.getDefaultValue() != null) {
                editor.valueProperty().setValue(descriptor.getDefaultValue());
            }
            editor.valueProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
                value.setValue(newValue);
            });

            final Node component = editor.getComponent();

            // If it's a locked value, we prevent its edition.
            Set validValues = descriptor.getValidValues();
            if (validValues != null && validValues.size() < 2) {
                component.setDisable(true);
            }

            final HBox container = new HBox(5, component);
            container.setMaxWidth(Double.MAX_VALUE);
            container.setFillHeight(true);
            HBox.setHgrow(component, Priority.ALWAYS);

            return Optional.of(container);
        }
        return Optional.empty();
    }

    private String getTitle(final GeneralParameterDescriptor parameter) {
        return parameter.getName().getCode();
    }

    protected DescriptorPanel getOrCreateDescriptorPanel(final GeneralParameterDescriptor descriptor) {
        DescriptorPanel panel = editionGroups.get(descriptor.getName().getCode());
        if (panel == null) {
            panel = new DescriptorPanel(descriptor);
            editionGroups.put(descriptor.getName().getCode(), panel);
            if (descriptor instanceof ParameterDescriptorGroup) {
                uiInnerGroups.getChildren().add(panel);
            } else if (descriptor instanceof ParameterDescriptor) {
                uiInnerValues.getChildren().add(panel);
            }
        }
        return panel;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    //
    // PRIVATE CLASSES
    //
    ///////////////////////////////////////////////////////////////////////////
    
    private class AddMenuItem extends MenuItem {

        private final GeneralParameterDescriptor parameter;

        public AddMenuItem(final GeneralParameterDescriptor parameter) {
            super();
            ArgumentChecks.ensureNonNull("input parameter", parameter);
            this.parameter = parameter;
            setText(getTitle(parameter));
            setOnAction(event -> {
                final DescriptorPanel panel = getOrCreateDescriptorPanel(parameter);
                if (panel.addParameterEditor() >= this.parameter.getMaximumOccurs()) {
                    uiAddBtn.getItems().remove(this);
                }
            });
        }
    }

    private class DescriptorPanel extends TitledPane {

        private final VBox content = new VBox();
        
        private final Label uiTitle = new Label();
        private final Button uiAdd = new Button(null, new ImageView(ICON_PLUS));
        final Separator headerExpander = new Separator();
        private final HBox uiToolbar = new HBox(10, uiTitle, headerExpander, uiAdd);

        private final GeneralParameterDescriptor descriptor;

        public DescriptorPanel(final GeneralParameterDescriptor descriptor) {
            super();
            ArgumentChecks.ensureNonNull("Parameter descriptor", descriptor);
            this.descriptor = descriptor;
            uiTitle.setText(descriptor.getName().getCode());

            getStyleClass().add(DESCRIPTOR_CONTAINER_CLASS);
            content.getStyleClass().add(DESCRIPTOR_CONTENT_CLASS);
            content.setFillWidth(true);
//            setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            uiAdd.getStyleClass().add(FLAT_BUTTON_CLASS);
            uiAdd.managedProperty().bind(uiAdd.visibleProperty());
            uiAdd.setVisible((ParametersExt.getParameters(inputGroup.get(), descriptor.getName().getCode()).size() < descriptor.getMaximumOccurs()));
            uiAdd.setOnAction(event -> addParameterEditor());

            // Make panel visible only in advanced mode for optional / preconfigured parameters.
            if (this.descriptor.getMinimumOccurs() < 1
                    || (this.descriptor instanceof ParameterDescriptor && ((ParameterDescriptor) this.descriptor).getDefaultValue() != null)) {
                visibleProperty().bind(uiAdvancedBtn.selectedProperty());
                managedProperty().bind(visibleProperty());
            }

            final InternationalString description = (descriptor.getDescription() != null)
                    ? descriptor.getDescription() : descriptor.getRemarks();
            if (description != null) {
                final Button descriptionButton = new Button(null, new ImageView(ICON_INFO));
                descriptionButton.setOnAction(event -> {
                    infoLabel.setText(description.toString());
                    Bounds localToScreen = descriptionButton.localToScreen(descriptionButton.getBoundsInLocal());
                    descriptionPopup.show(descriptionButton, localToScreen.getMinX(), localToScreen.getMinY());
                });

                descriptionButton.setAlignment(Pos.CENTER);
                descriptionButton.getStyleClass().add(INFO_BUTTON_CLASS);
                uiToolbar.getChildren().add(descriptionButton);
            }

            setContent(content);
            setGraphic(uiToolbar);
            
            /* 
             * CONFIGURE HEADER POSITION
             */
            uiToolbar.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(headerExpander, Priority.ALWAYS);
            headerExpander.setVisible(false);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            uiToolbar.setMaxWidth(Double.MAX_VALUE);
            uiToolbar.prefWidthProperty().bind(widthProperty().subtract(50));
        }

        /**
         * Check that input editor has not reached maximum occurence number,
         * then add an editor for it in current panel.
         *
         * @param descriptor Descriptor for which we want a new editor.
         * @return Current number of parameters of input type in main parameter
         * group {@link #inputGroup}.
         */
        private int addParameterEditor() {
            final int maxOccurs = descriptor.getMaximumOccurs();
            int currentOccurs = 0;
            if (descriptor instanceof ParameterDescriptorGroup) {
                currentOccurs = inputGroup.get().groups(descriptor.getName().getCode()).size();
            } else
                for (final GeneralParameterValue param : inputGroup.get().values()) {
                    if (param.getDescriptor().equals(descriptor)) {
                        currentOccurs++;
                    }
                }

            GeneralParameterValue value = null;
            if (currentOccurs < maxOccurs) {
                if (descriptor instanceof ParameterDescriptorGroup) {
                    value = inputGroup.get().addGroup(descriptor.getName().getCode());
                    currentOccurs++;
                } else if (descriptor instanceof ParameterDescriptor) {
                    value = ((ParameterDescriptor) descriptor).createValue();
                    currentOccurs++;
                }

                if (value != null) {
                    addEditor(value);
                }
            }

            if (currentOccurs >= maxOccurs) {
                uiAdd.setVisible(false);
            }
            return currentOccurs;
        }

        public Node addEditor(final GeneralParameterValue parameter) {
            Node editor = null;
            if (parameter instanceof ParameterValueGroup) {
                editor = new FXParameterGroupPane((ParameterValueGroup) parameter);

            } else if (parameter instanceof ParameterValue) {
                final Optional<Node> optional = getValueEditor((ParameterValue)parameter);
                if (optional.isPresent()) {
                    editor = optional.get();
                    editor.getStyleClass().add(PARAMETER_EDITOR_CLASS);
                }
            }

            if (editor != null) {
                content.getChildren().add(editor);
            }
            return editor;
        }
    }
}
