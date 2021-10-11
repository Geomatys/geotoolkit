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

package org.geotoolkit.data.osm.model;

import org.apache.sis.geometry.ImmutableEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.opengis.geometry.Envelope;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class Bound {

    private Bound(){};

    /**
     *
     * @param xmin : minimum longitude
     * @param xmax : maximum longitude
     * @param ymin : minimum latitude
     * @param ymax : maximum latitude
     * @return Immutable envelope in WGS84 with the given extents.
     */
    public static Envelope create(final double xmin, final double xmax, final double ymin, final double ymax){
        return new ImmutableEnvelope(new double[] {xmin, ymin}, new double[] {xmax, ymax}, CommonCRS.WGS84.normalizedGeographic());
    }

}
