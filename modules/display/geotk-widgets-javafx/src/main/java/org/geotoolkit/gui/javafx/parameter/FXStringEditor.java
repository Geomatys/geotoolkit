/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXStringEditor extends FXValueEditor {

    private final TextField textField = new TextField();

    public FXStringEditor(FXValueEditorSpi originatingSpi) {
        super(originatingSpi);
    }
        
    @Override
    public Node getComponent() {
        return textField;
    }

    @Override
    public StringProperty valueProperty() {
        return textField.textProperty();
    }
        
    public static final class Spi extends FXValueEditorSpi {

        @Override
        public boolean canHandle(Class binding) {
            return CharSequence.class.isAssignableFrom(binding) || Character.class.isAssignableFrom(binding);
        }

        @Override
        public FXValueEditor createEditor() {
            return new FXStringEditor(this);
        }
    }
}
