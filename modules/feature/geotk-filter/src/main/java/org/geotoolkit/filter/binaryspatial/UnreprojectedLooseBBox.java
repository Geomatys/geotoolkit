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

import org.opengis.feature.Feature;
import org.opengis.filter.expression.PropertyName;
import org.opengis.geometry.BoundingBox;

/**
 * Perform the same work as the LooseBBox expect it does not reproject the candidate geometry to the filter CRS.
 * Use this only if you are sure that the evaluated object are in the same CRS than the filter
 *
 * @author Guilhem Legal (Geomatys)
 * @module pending
 */
public class UnreprojectedLooseBBox extends LooseBBox{

    public UnreprojectedLooseBBox(final PropertyName property, final DefaultLiteral<BoundingBox> bbox) {
        super(property,bbox);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean evaluate(final Object object) {
        Geometry candidate;
        if (object instanceof Feature && left.getPropertyName().isEmpty()) {
            candidate = (Geometry) ((Feature)object).getDefaultGeometryProperty().getValue();
        } else {
            candidate = left.evaluate(object, Geometry.class);
        }

        if(candidate == null){
            return false;
        }

        final com.vividsolutions.jts.geom.Envelope candidateEnv = candidate.getEnvelopeInternal();
        return boundingEnv.intersects(candidateEnv);
    }

}
