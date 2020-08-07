/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.geotoolkit.display2d.presentation;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.text.AttributedString;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.map.MapLayer;
import org.opengis.feature.Feature;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class TextPresentation2 extends Grid2DPresentation {

    public AlphaComposite composite = GO2Utilities.ALPHA_COMPOSITE_1F;
    public Paint paint;
    public AttributedString text;
    public Font font;
    public AffineTransform displayTransform;
    public float x;
    public float y;

    public TextPresentation2(MapLayer layer, Feature feature) {
        super(layer,feature);
    }
}
