/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.apache.sis.referencing.operation.transform;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import org.opengis.referencing.operation.MathTransform2D;
import org.opengis.referencing.operation.TransformException;


/**
 * @deprecated Temporary bridge class to be removed after Geotk code have been ported to Apache SIS.
 */
@Deprecated
public final class Accessor {
    public static Shape createTransformedShape(final MathTransform2D mt,
                                               final Shape           shape,
                                               final AffineTransform preTransform,
                                               final AffineTransform postTransform,
                                               final boolean         horizontal)
            throws TransformException
    {
        return AbstractMathTransform2D.createTransformedShape(mt, shape, preTransform, postTransform, horizontal);
    }
}
