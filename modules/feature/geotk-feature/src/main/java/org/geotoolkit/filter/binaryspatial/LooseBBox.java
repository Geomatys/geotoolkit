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

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.filter.DefaultLiteral;
import org.apache.sis.referencing.CRS;
import org.opengis.filter.expression.PropertyName;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.referencing.ReferencingUtilities;
import org.opengis.geometry.Envelope;

/**
 * Perform the same work as the BBox expect it evaluate intersection only against
 * the geometry envelope.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class LooseBBox extends DefaultBBox{

    public LooseBBox(final PropertyName property, final DefaultLiteral<BoundingBox> bbox) {
        super(property,bbox);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean evaluate(final Object object) {
        Geometry candidate = toGeometry(object, left);

        if(candidate == null){
            return false;
        }

        //we don't know in which crs it is, try to find it
        final CoordinateReferenceSystem candidateCrs = findCRS(object, candidate);

        //if we don't know the crs, we will assume it's the objective crs already
        if(candidateCrs != null){
            //reproject in objective crs if needed
            final Envelope e = new JTSEnvelope2D(candidate.getEnvelopeInternal(),CRS.getHorizontalComponent(candidateCrs));
            return ReferencingUtilities.intersects(e, right.getValue());
        }
        final com.vividsolutions.jts.geom.Envelope candidateEnv = candidate.getEnvelopeInternal();
        return boundingEnv.intersects(candidateEnv);
    }

}
