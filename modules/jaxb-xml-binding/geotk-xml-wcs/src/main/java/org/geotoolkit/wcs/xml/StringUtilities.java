/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wcs.xml;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.ReferenceIdentifier;


/**
 * Utilities methods that provide conversion functionnalities between real objects and queries
 * (in both ways).
 *
 * @version $Id: StringUtilities.java 1546 2009-04-21 12:57:42Z eclesia $
 *
 * @author Cédric Briançon (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class StringUtilities {

    /**
     * Returns a string representation of the {@code Bounding Box}. It is a comma-separated
     * list matching with this pattern: minx,miny,maxx,maxy.
     *
     * @param envelope The envelope to return the string representation.
     */
    public static String toBboxValue(final Envelope envelope) {
        final StringBuilder builder = new StringBuilder();
        final int dimEnv = envelope.getDimension();
        for (int i=0; i<dimEnv; i++) {
            builder.append(envelope.getMinimum(i)).append(',');
        }
        for (int j=0; j<dimEnv; j++) {
            if (j>0) {
                builder.append(',');
            }
            builder.append(envelope.getMaximum(j));
        }
        return builder.toString();
    }

    /**
     * Returns the CRS code for the specified envelope, or {@code null} if not found.
     *
     * @param envelope The envelope to return the CRS code.
     */
    public static String toCrsCode(final Envelope envelope) {
        if (envelope.getCoordinateReferenceSystem().equals(DefaultGeographicCRS.WGS84)) {
            return "EPSG:4326";
        }
        final Set<ReferenceIdentifier> identifiers = envelope.getCoordinateReferenceSystem().getIdentifiers();
        if (identifiers != null && !identifiers.isEmpty()) {
            return identifiers.iterator().next().toString();
        }
        return null;
    }

    public static String toFormat(String format) throws IllegalArgumentException {
        if (format == null) {
            return null;
        }
        format = format.trim();
        final Set<String> formats = new HashSet<String>(Arrays.asList(ImageIO.getWriterMIMETypes()));
        formats.addAll(Arrays.asList(ImageIO.getWriterFormatNames()));
        if (!formats.contains(format)) {
            throw new IllegalArgumentException("Invalid format specified.");
        }
        return format;
    }

}
