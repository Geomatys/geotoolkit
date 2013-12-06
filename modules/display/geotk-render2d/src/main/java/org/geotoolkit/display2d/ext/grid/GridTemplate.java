/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.display2d.ext.grid;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface GridTemplate {

    CoordinateReferenceSystem getCRS();

    Stroke getLineStroke();

    Paint getLinePaint();

    Stroke getMainLineStroke();

    Paint getMainLinePaint();

    Font getLabelFont();

    Paint getLabelPaint();

    Font getMainLabelFont();

    Paint getMainLabelPaint();

    float getHaloWidth();

    Paint getHaloPaint();

    float getMainHaloWidth();

    Paint getMainHaloPaint();

}
