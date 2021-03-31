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
import org.apache.sis.referencing.CRS;
import org.opengis.filter.ValueReference;
import org.geotoolkit.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.filter.Literal;
import org.opengis.geometry.Envelope;

/**
 * Perform the same work as the BBox expect it evaluate intersection only against
 * the geometry envelope.
 *
 * @author Johann Sorel (Geomatys)
 */
public class LooseBBox extends DefaultBBox {

    public LooseBBox(final ValueReference property, final Literal<Object,BoundingBox> bbox) {
        super(property,bbox);
    }

    @Override
    public boolean test(final Object object) {
        Geometry candidate = toGeometry(object, left);
        if (candidate == null) {
            return false;
        }
        //we don't know in which crs it is, try to find it
        final CoordinateReferenceSystem candidateCrs = findCRS(object, candidate);

        //if we don't know the crs, we will assume it's the objective crs already
        if (candidateCrs != null) {
            //reproject in objective crs if needed
            final Envelope e = new JTSEnvelope2D(candidate.getEnvelopeInternal(),CRS.getHorizontalComponent(candidateCrs));
            return ReferencingUtilities.intersects(e, right.getValue());
        }
        final org.locationtech.jts.geom.Envelope candidateEnv = candidate.getEnvelopeInternal();
        return boundingEnv.intersects(candidateEnv);
    }
}
