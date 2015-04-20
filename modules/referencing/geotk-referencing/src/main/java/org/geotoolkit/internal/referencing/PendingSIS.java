/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.internal.referencing;

import java.text.ParseException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.geotoolkit.io.wkt.MathTransformParser;
import org.apache.sis.internal.referencing.Pending;
import org.apache.sis.io.wkt.Symbols;


/**
 * Temporary class to be removed after the post to SIS has been completed.
 */
public final class PendingSIS extends Pending {
    @Override
    public MathTransform createFromWKT(MathTransformFactory factory, final String text) throws ParseException {
        // TODO: recycle a parser.
        final MathTransformParser parser = new MathTransformParser(Symbols.getDefault(), factory);
        return parser.parseMathTransform(text);
    }
}
