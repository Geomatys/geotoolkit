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

package org.geotoolkit.filter.binaryspatial;

import org.locationtech.jts.geom.Geometry;
import org.opengis.filter.ValueReference;
import org.opengis.filter.Literal;
import org.opengis.geometry.Envelope;

/**
 * Perform the same work as the LooseBBox expect it does not reproject the candidate geometry to the filter CRS.
 * Use this only if you are sure that the evaluated object are in the same CRS than the filter
 *
 * @author Guilhem Legal (Geomatys)
 */
public class UnreprojectedLooseBBox extends LooseBBox {

    public UnreprojectedLooseBBox(final ValueReference property, final Literal<Object, Envelope> bbox) {
        super(property,bbox);
    }

    @Override
    public boolean test(final Object object) {
        Geometry candidate = toGeometry(object, left);
        if (candidate == null) {
            return false;
        }
        final org.locationtech.jts.geom.Envelope candidateEnv = candidate.getEnvelopeInternal();
        return boundingEnv.intersects(candidateEnv);
    }
}
