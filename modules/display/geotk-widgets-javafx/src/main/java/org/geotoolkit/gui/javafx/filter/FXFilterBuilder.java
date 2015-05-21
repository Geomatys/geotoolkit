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
package org.geotoolkit.gui.javafx.filter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.ServiceLoader;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.gui.javafx.util.ComboBoxCompletion;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;

/**
 * A panel to create and chain filters on properties.
 * To fill list of properties which can be filtered, use {@link #getAvailableProperties() }.
 * 
 * @author Alexis Manin (Geomatys)
 */
public class FXFilterBuilder extends BorderPane {
    
    protected static enum JOIN_TYPE {
        AND,
        OR;
    }
    protected static final ObservableList<JOIN_TYPE> JOIN_TYPES = FXCollections.observableArrayList(JOIN_TYPE.values());
    
    private static final Image ICON_PLUS = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_PLUS, 16, new Color(74,123,165)), null);
    private static final Image ICON_MINUS = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_MINUS, 16, Color.RED), null);
    
    protected static final ServiceLoader<FXFilterOperator> OPERATORS = ServiceLoader.load(FXFilterOperator.class);
    
    /**
     * A simple button to add a new filter in filter chain.
     */
    protected final Button addFilter;
    
    /**
     * A grid pane which will contain filter editors bound by an AND or OR filter.
     * Columns are : 
     * 0 --> Type of link (AND, OR, or nothing for first row).
     * 1 --> Property to apply filter upon,
     * 2 --> Type of filter operator to apply,
     * 3 --> Filter operator form,
     * 4 --> Remove filter button.
     */
    protected final GridPane filterEditors;
    
    /**
     * Properties which can be filtered. User will be able to choose which one to filter on using a combo-box.
     */
    protected final ObservableList<PropertyType> availableProperties = FXCollections.observableArrayList();
    
    /**
     * A simple string converter for property display in a combo-box. it uses method {@link #getPropertyTitle(org.opengis.filter.expression.PropertyName) }.
     */
    private final StringConverter<PropertyType> propertyConverter;
    
    private final StringConverter<FXFilterOperator> operatorConverter;
    
    /**
     * Contains main information about edited filters.
     */
    private final ObservableList<FilterBox> sandboxes = FXCollections.observableArrayList();
    
    /**
     * A simple observable boolean to know if we have multiple filters (rows in 
     * inner grid pane) active (true) or not (false).
     */
    public final BooleanBinding multipleRows;
    
    public FXFilterBuilder() {
        super();
        
        // String converters
        propertyConverter = new StringConverter<PropertyType>() {
            @Override
            public String toString(PropertyType object) {
                return getTitle(object);
            }

            @Override
            public PropertyType fromString(String string) {
                for (final PropertyType property : availableProperties) {
                    if (getTitle(property).equals(string)) return property;
                }
                return null;
            }
        };
        
        operatorConverter = new StringConverter<FXFilterOperator>() {
            @Override
            public String toString(FXFilterOperator object) {
                return object.getTitle().toString();
            }

            @Override
            public FXFilterOperator fromString(String string) {
                for (final FXFilterOperator op : OPERATORS) {
                    if (op.getTitle().toString().equals(string)) return op;
                }
                return null;
            }
        };
        
        // display rules
        filterEditors = new GridPane();
        filterEditors.getColumnConstraints().addAll(
                new ColumnConstraints(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE, USE_PREF_SIZE, Priority.NEVER, HPos.CENTER, true),
                new ColumnConstraints(0, USE_COMPUTED_SIZE, USE_PREF_SIZE, Priority.SOMETIMES, HPos.CENTER, true),
                new ColumnConstraints(USE_PREF_SIZE, USE_COMPUTED_SIZE, USE_PREF_SIZE, Priority.NEVER, HPos.CENTER, true),
                new ColumnConstraints(0, USE_COMPUTED_SIZE, Double.MAX_VALUE, Priority.SOMETIMES, HPos.CENTER, true),
                new ColumnConstraints(USE_PREF_SIZE, USE_COMPUTED_SIZE, USE_PREF_SIZE, Priority.NEVER, HPos.CENTER, true)
        );
        multipleRows = Bindings.size(sandboxes).greaterThan(1);
        
        addFilter = new Button(null, new ImageView(ICON_PLUS));
        addFilter.visibleProperty().bind(Bindings.isNotEmpty(availableProperties));
        addFilter.setOnAction(event -> addFilterRow());
        
        setTop(addFilter);
        setCenter(filterEditors);
    }
    
    /**
     * @return A list of properties available for filtering. Use it to add new 
     * filter possibilities.
     */
    public ObservableList<PropertyType> getAvailableProperties() {
        return availableProperties;
    }
    
    /**
     * Build a filter which is the aggregation of all filters edited.
     * AND conditions are grouped first, then they are joined by or conditions, 
     * which make the final filter to return.
     * @return A filter representing user input. Never null, but a runtime exception
     * can be thrown if user input is not wring or not sufficient to build a proper filter.
     */
    public Filter getFilter() {
        if (sandboxes.isEmpty()) throw new IllegalStateException("No valid filter definition.");
        final ArrayList<Filter> andGroups = new ArrayList<>();
        final ArrayList<Filter> orGroups = new ArrayList<>();
        FilterBox box = sandboxes.get(0);
        andGroups.add(box.buildFilter());
        for (int i = 1; i < sandboxes.size(); i++) {
            box = sandboxes.get(i);
            if (JOIN_TYPE.OR.equals(box.joinType.get())) {
                orGroups.add((andGroups.size()==1)? andGroups.get(0) : GO2Utilities.FILTER_FACTORY.and(andGroups));
                andGroups.clear();
            }
            
            andGroups.add(box.buildFilter());
        }
        
        orGroups.add((andGroups.size()==1)? andGroups.get(0) : GO2Utilities.FILTER_FACTORY.and(andGroups));
        return (orGroups.size() == 1)? orGroups.get(0) : GO2Utilities.FILTER_FACTORY.or(orGroups);
    }
    
    /**
     * Return a title to be displayed in combo-box for user. By default, raw name
     * is returned,
     * @param candidate The expression to get a title for.
     * @return A title or raw name of input property.
     */
    protected String getTitle(PropertyType candidate) {
        if (candidate == null)
            return "";
        return candidate.getName().head().toString();
    }
    
    /**
     * Add a new filter, and display an editor to allow user to configure it.
     */
    protected void addFilterRow() {
        
        // TODO : make international label for join types.
        final ChoiceBox<JOIN_TYPE> joinType = new ChoiceBox<>(JOIN_TYPES);
        final ComboBox<PropertyType> propertyChoice = createPropertyChoice();
        final ObservableList<FXFilterOperator> operators = FXCollections.observableArrayList();
        final ChoiceBox<FXFilterOperator> operatorChoice = new ChoiceBox<>(operators);
        final StackPane editorPane = new StackPane();
        final Button removeButton = new Button(null, new ImageView(ICON_MINUS));
        
        // bind remove button and join type visibility to number of rows. (only one row left = no join or deletion).
        joinType.getSelectionModel().select(JOIN_TYPE.AND);
        joinType.visibleProperty().bind(multipleRows);
        removeButton.visibleProperty().bind(multipleRows);
        
        operatorChoice.setConverter(operatorConverter);
        
        final FilterBox filterBox = new FilterBox(joinType.valueProperty(), propertyChoice.valueProperty(), operatorChoice.valueProperty(), editorPane);
        filterBox.propertyType.addListener(new PropertyChoiceListener(operators));
                
        removeButton.setOnAction(event -> {
            filterEditors.getChildren().removeAll(joinType, propertyChoice, operatorChoice, editorPane, removeButton);
            // Delete filter entry
            sandboxes.remove(filterBox);
        });
        
        // To add a new line in the grid, we must get row indice of the last added pane.
        final int newRowIndice;
        if (sandboxes.isEmpty()) {
            newRowIndice = 0;
        } else {
            final FilterBox box = sandboxes.get(sandboxes.size()-1);
            newRowIndice = GridPane.getRowIndex(box.filterEditorContainer) + 1;
        }
        
        filterEditors.addRow(newRowIndice, joinType, propertyChoice, operatorChoice, editorPane, removeButton);
        sandboxes.add(filterBox);
    }
    
    /**
     * Create a combo-box (by default its editable with auto-completion) to allow 
     * user to pick a property as filter target.
     * @return 
     */
    protected ComboBox<PropertyType> createPropertyChoice() {
        final ComboBox<PropertyType> cBox = new ComboBox<>(availableProperties);
        cBox.setConverter(propertyConverter);
        cBox.setEditable(true);
        new ComboBoxCompletion(cBox);
        
        return cBox;
    }
    
    /**
     * Update list of compatible operators when target property is changed.
     */
    private class PropertyChoiceListener implements ChangeListener<PropertyType> {

        private final ObservableList operatorList;
        
        public PropertyChoiceListener(final ObservableList operators) {
            ArgumentChecks.ensureNonNull("operator list", operators);
            operatorList = operators;
        }
        
        @Override
        public void changed(ObservableValue<? extends PropertyType> observable, PropertyType oldValue, PropertyType newValue) {
            if (newValue == null) {
                operatorList.clear();
                
            } else {
                // Do not clear and re-fill operator list to keep selected operator if possible.
                
                // Remove filters which are not valid for current property.
                final Iterator<FXFilterOperator> it = operatorList.iterator();
                while (it.hasNext()) {
                    if (!it.next().canHandle(newValue)) {
                        it.remove();
                    }
                }
                
                // Check if new filters can be applied to data
                for (final FXFilterOperator operator : OPERATORS) {
                    if (!operatorList.contains(operator) && operator.canHandle(newValue)) {
                        operatorList.add(operator);
                    }
                }
            }
        }
    }
    
    /**
     * A simple POJO to keep references of a grid line row, which describes a filter edition rule.
     */
    private static class FilterBox {
        final ObjectProperty<JOIN_TYPE> joinType;
        final ObjectProperty<PropertyType> propertyType;
        final ObjectProperty<FXFilterOperator> operator;
        final Pane filterEditorContainer;

        public FilterBox(
                ObjectProperty<JOIN_TYPE> joinType,
                ObjectProperty<PropertyType> propertyType,
                ObjectProperty<FXFilterOperator> operator,
                Pane filterEditorContainer) {
            this.joinType = joinType;
            this.propertyType = propertyType;
            this.operator = operator;
            this.filterEditorContainer = filterEditorContainer;
            
            this.propertyType.addListener(this::updateOperator);
            this.operator.addListener(this::updateOperator);
        }
        
        /**
         * Build filter described by current attributes.
         * @return Never null.
         * @throws IllegalStateException If neither property name nor operator has a valid value.
         */
        public Filter buildFilter() {
            if (propertyType.get() == null) {
                throw new IllegalStateException("Cannot build filter : target property is not set");
            } else if (operator.get() == null) {
                throw new IllegalStateException("Cannot build filter : operator is not set");
            }
            final ObservableList<Node> paneChildren = filterEditorContainer.getChildren();
            return operator.get().getFilterOver(
                    GO2Utilities.FILTER_FACTORY.property(propertyType.get().getName()),
                    paneChildren.isEmpty()? null : paneChildren.get(0));
        }
        
        private void updateOperator(ObservableValue observable, Object oldValue, Object newValue) {
            final FXFilterOperator op = operator.get();
            final PropertyType property = propertyType.get();
            
            if (op == null || property == null) {
                filterEditorContainer.getChildren().clear();
            } else {
                // Update editor only if old one is not compatible with the new editor.
                if (filterEditorContainer.getChildren().isEmpty() || !op.canExtractSettings(property, filterEditorContainer.getChildren().get(0))) {
                    final Optional<Node> filterEditor = op.createFilterEditor(property);
                    if (filterEditor.isPresent()) {
                        filterEditorContainer.getChildren().setAll(filterEditor.get());
                    } else {
                        filterEditorContainer.getChildren().clear();
                    }
                }
            }
        }
    }   
}
