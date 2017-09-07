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

import javafx.beans.property.Property;
import javafx.scene.Node;
import org.apache.sis.gui.crs.CRSButton;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXCRSEditor extends FXValueEditor {

    private final CRSButton button = new CRSButton();

    public FXCRSEditor(FXValueEditorSpi originatingSpi) {
        super(originatingSpi);
    }

    @Override
    public Property valueProperty() {
        return button.crsProperty();
    }

    @Override
    public Node getComponent() {
        return button;
    }

    public static final class Spi extends FXValueEditorSpi {

        @Override
        public boolean canHandle(Class binding) {
            return CoordinateReferenceSystem.class.isAssignableFrom(binding);
        }

        @Override
        public FXValueEditor createEditor() {
            return new FXCRSEditor(this);
        }
    }
}
