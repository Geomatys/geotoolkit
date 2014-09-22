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

import java.awt.Color;
import org.opengis.style.Halo;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class FXHalo extends FXStyleElementController<FXHalo, Halo>{

    @Override
    public Class<Halo> getEditedClass() {
        return Halo.class;
    }

    @Override
    public Halo newValue() {
        return getStyleFactory().halo(Color.WHITE, 1);
    }
    
}
