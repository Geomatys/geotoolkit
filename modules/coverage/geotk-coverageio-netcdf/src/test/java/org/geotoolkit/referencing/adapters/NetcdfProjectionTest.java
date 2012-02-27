/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010-2012, Geomatys
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
package org.geotoolkit.referencing.adapters;

import ucar.unidata.geoloc.Projection;

import org.opengis.referencing.operation.SingleOperation;


/**
 * Tests the {@link NetcdfProjection} class. This class inherits the tests from the
 * {@code geoapi-netcdf} module.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final strictfp class NetcdfProjectionTest extends org.opengis.wrapper.netcdf.NetcdfProjectionTest {
    /**
     * Wraps the given NetCDF projection into the object to test.
     *
     * @param  projection The NetCDF projection to wrap.
     * @return An operation implementation created from the given projection.
     */
    @Override
    protected SingleOperation wrap(final Projection projection) {
        return new NetcdfProjection(projection, null, null);
    }
}
