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

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import org.apache.sis.util.UnconvertibleObjectException;
import org.geotoolkit.gui.javafx.util.FXDateField;

/**
 *
 * @author Alexis Manin (Geomatys)
 */
public class FXDateEditor extends FXValueEditor {

    static final Class[] SUPPORTED_CLASSES = new Class[]{Date.class, LocalDate.class, LocalDateTime.class};

    private final SimpleObjectProperty dateProperty = new SimpleObjectProperty();
    private final FXDateField editor = new FXDateField();

    public FXDateEditor(final Spi spi) {
        super(spi);

        dateProperty.addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            if (editor.valueProperty().isBound())
                return; // Do not throw exception, because user has set editor property as a target for one of his own property.

            if (newValue == null)
                editor.valueProperty().set(null);
            else if (newValue instanceof LocalDateTime)
                editor.valueProperty().set((LocalDateTime)newValue);
            else if (newValue instanceof LocalDate)
                editor.valueProperty().set(((LocalDate)newValue).atStartOfDay());
            else if (newValue instanceof Date) {
                editor.valueProperty().set(new Timestamp(((Date)newValue).getTime()).toLocalDateTime());
            } else throw new UnconvertibleObjectException("Cannot convert from "+newValue.getClass() + " to "+LocalDateTime.class);
        });

        editor.valueProperty().addListener((ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) -> {
            if (dateProperty.isBound())
                return; // Do not throw exception, because user has set exposed property as a target for one of his own property.

            if (newValue == null) {
                dateProperty.set(null);
            } else {
                Class valueClass = getValueClass();
                if (valueClass == null)
                    dateProperty.set(newValue); // TODO : throw an exception ? editor has not been configured, but user already started to work with it.
                else if (LocalDateTime.class.isAssignableFrom(valueClass))
                    dateProperty.set(newValue);
                else if (LocalDate.class.isAssignableFrom(valueClass))
                    dateProperty.set(newValue.toLocalDate());
                else if (Date.class.isAssignableFrom(valueClass))
                    dateProperty.set(Timestamp.valueOf(newValue));
            }
        });
    }

    @Override
    public Property valueProperty() {
        return dateProperty;
    }

    @Override
    public Node getComponent() {
        return editor;
    }

    /**
     * SPI
     */
    public static final class Spi extends FXValueEditorSpi {

        @Override
        public boolean canHandle(Class binding) {
            for (final Class c : SUPPORTED_CLASSES) {
                if (c.isAssignableFrom(binding))
                    return true;
            }
            return false;
        }

        @Override
        public FXValueEditor createEditor() {
            return new FXDateEditor(this);
        }
    }
}
