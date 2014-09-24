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

package org.geotoolkit.gui.javafx.style;

import org.opengis.style.ColorMap;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXColorMap extends FXStyleElementController<FXColorMap,ColorMap> {

    public FXColorMap() {
    }

    @Override
    public Class<ColorMap> getEditedClass() {
        return ColorMap.class;
    }

    @Override
    public ColorMap newValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected void updateEditor(ColorMap styleElement) {
    }
    
    
}
