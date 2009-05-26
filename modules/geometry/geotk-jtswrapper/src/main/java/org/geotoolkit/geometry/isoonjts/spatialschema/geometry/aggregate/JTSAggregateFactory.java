/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate;

import org.geotoolkit.geometry.isoonjts.spatialschema.geometry.aggregate.*;
import java.util.Set;

import org.geotoolkit.factory.Factory;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;

import org.opengis.geometry.aggregate.AggregateFactory;
import org.opengis.geometry.aggregate.MultiCurve;
import org.opengis.geometry.aggregate.MultiPoint;
import org.opengis.geometry.aggregate.MultiPrimitive;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Implementation of AggregateFactory able to make MultiPointImpl but little else.
 * 
 * @author Jody Garnett
 * @author Johann Sorel (Geomatys)
 */
public class JTSAggregateFactory extends Factory implements AggregateFactory {

    private final CoordinateReferenceSystem crs;

    public JTSAggregateFactory(){
        this( DefaultGeographicCRS.WGS84);
    }

    public JTSAggregateFactory( CoordinateReferenceSystem crs ) {
        this.crs = crs;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MultiCurve createMultiCurve( Set arg0 ) {
        throw new UnsupportedOperationException("MultiCurve not implemented");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MultiPoint createMultiPoint( Set arg0 ) {
        return new JTSMultiPoint( crs );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MultiPrimitive createMultiPrimitive( Set arg0 ) {
        throw new UnsupportedOperationException("MultiPrimitive not implemented");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public MultiSurface createMultiSurface( Set arg0 ) {
        throw new UnsupportedOperationException("MultiSurface not implemented");
    }

}
