
package org.geotoolkit.gui.javafx.util;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * Copy version of JavaFX CheckBoxTreeTableCell for a ToggleButton.
 * 
 */
public class ToggleButtonTreeTableCell<S,T> extends TreeTableCell<S,T> {
    
    private final static StringConverter defaultStringConverter = new StringConverter<Object>() {
        @Override public String toString(Object t) {
            return t == null ? null : t.toString();
        }

        @Override public Object fromString(String string) {
            return (Object) string;
        }
    };
    
    public static <S> Callback<TreeTableColumn<S,Boolean>, TreeTableCell<S,Boolean>> forTreeTableColumn(
            final TreeTableColumn<S, Boolean> column) {
        return forTreeTableColumn(null, null);
    }
    
    public static <S,T> Callback<TreeTableColumn<S,T>, TreeTableCell<S,T>> forTreeTableColumn(
            final Callback<Integer, ObservableValue<Boolean>> getSelectedProperty) {
        return forTreeTableColumn(getSelectedProperty, null);
    }
    
    public static <S,T> Callback<TreeTableColumn<S,T>, TreeTableCell<S,T>> forTreeTableColumn(
            final Callback<Integer, ObservableValue<Boolean>> getSelectedProperty, 
            final boolean showLabel) {
        StringConverter<T> converter = ! showLabel ? 
                null : defaultStringConverter;
        return forTreeTableColumn(getSelectedProperty, converter);
    }
    
    public static <S,T> Callback<TreeTableColumn<S,T>, TreeTableCell<S,T>> forTreeTableColumn(
            final Callback<Integer, ObservableValue<Boolean>> getSelectedProperty, 
            final StringConverter<T> converter) {
        return list -> new ToggleButtonTreeTableCell<S,T>(getSelectedProperty, converter);
    }
    
    private final ToggleButton toggleButton;
    private boolean showLabel;
    private ObservableValue<Boolean> booleanProperty;
    
    
    public ToggleButtonTreeTableCell() {
        this(null, null);
    }
    
    public ToggleButtonTreeTableCell(
            final Callback<Integer, ObservableValue<Boolean>> getSelectedProperty) {
        this(getSelectedProperty, null);
    }

    public ToggleButtonTreeTableCell(
            final Callback<Integer, ObservableValue<Boolean>> getSelectedProperty, 
            final StringConverter<T> converter) {        
        this.toggleButton = new ToggleButton();
        setGraphic(null);
        setSelectedStateCallback(getSelectedProperty);
        setConverter(converter);
        
    }

    public ToggleButton getToggleButton() {
        return toggleButton;
    }
    
    private ObjectProperty<StringConverter<T>> converter = 
            new SimpleObjectProperty<StringConverter<T>>(this, "converter") {
        protected void invalidated() {
            updateShowLabel();
        }
    };

    public final ObjectProperty<StringConverter<T>> converterProperty() { 
        return converter; 
    }
    
    public final void setConverter(StringConverter<T> value) { 
        converterProperty().set(value); 
    }
    
    public final StringConverter<T> getConverter() { 
        return converterProperty().get(); 
    }
    
    private ObjectProperty<Callback<Integer, ObservableValue<Boolean>>> 
            selectedStateCallback = 
            new SimpleObjectProperty<Callback<Integer, ObservableValue<Boolean>>>(
            this, "selectedStateCallback");

    public final ObjectProperty<Callback<Integer, ObservableValue<Boolean>>> selectedStateCallbackProperty() { 
        return selectedStateCallback; 
    }
    
    public final void setSelectedStateCallback(Callback<Integer, ObservableValue<Boolean>> value) { 
        selectedStateCallbackProperty().set(value); 
    }
    
    public final Callback<Integer, ObservableValue<Boolean>> getSelectedStateCallback() { 
        return selectedStateCallbackProperty().get(); 
    }
    
    @SuppressWarnings("unchecked")
    @Override public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            StringConverter<T> c = getConverter();
            
            if (showLabel) {
                setText(c.toString(item));
            }
            setGraphic(toggleButton);
            
            if (booleanProperty instanceof BooleanProperty) {
                toggleButton.selectedProperty().unbindBidirectional((BooleanProperty)booleanProperty);
            }
            ObservableValue<?> obsValue = getSelectedProperty();
            if (obsValue instanceof BooleanProperty) {
                booleanProperty = (ObservableValue<Boolean>) obsValue;
                toggleButton.selectedProperty().bindBidirectional((BooleanProperty)booleanProperty);
            }
            
            toggleButton.disableProperty().bind(Bindings.not(
                    getTreeTableView().editableProperty().and(
                    getTableColumn().editableProperty()).and(
                    editableProperty())
                ));
        }
    }
    
    private void updateShowLabel() {
        this.showLabel = converter != null;
        this.toggleButton.setAlignment(showLabel ? Pos.CENTER_LEFT : Pos.CENTER);
    }
    
    private ObservableValue<?> getSelectedProperty() {
        return getSelectedStateCallback() != null ?
                getSelectedStateCallback().call(getIndex()) :
                getTableColumn().getCellObservableValue(getIndex());
    }
}
