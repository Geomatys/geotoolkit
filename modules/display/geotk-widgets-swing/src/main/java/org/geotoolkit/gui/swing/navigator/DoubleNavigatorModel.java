/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.gui.swing.navigator;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DoubleNavigatorModel extends AbstractNavigatorModel<Double>{

    @Override
    public Double getValueAt(double d) {
        final double scale = getScale();
        final double translate = getTranslation();
        return scale*d + translate;
    }

    @Override
    public double getPosition(Double candidate) {
        final double scale = getScale();
        final double translate = getTranslation();
        return ( candidate - translate ) / scale;
    }

}
