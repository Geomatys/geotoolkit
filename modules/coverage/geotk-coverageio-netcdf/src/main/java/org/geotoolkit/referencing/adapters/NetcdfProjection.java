/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2011, Geomatys
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

import java.util.Collection;
import java.util.Collections;

import org.opengis.util.InternationalString;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.quality.PositionalAccuracy;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.OperationMethod;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotoolkit.metadata.iso.extent.DefaultExtent;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;

import ucar.unidata.geoloc.LatLonRect;
import ucar.unidata.geoloc.Projection;
import ucar.unidata.geoloc.projection.ProjectionAdapter;


/**
 * Wraps a NetCDF {@link Projection} object in a GeoAPI
 * {@link org.opengis.referencing.operation.Projection}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 * @module
 */
final class NetcdfProjection extends NetcdfIdentifiedObject implements
        org.opengis.referencing.operation.Projection
{
    /**
     * The NetCDF projection object wrapped in a {@link MathTransform} instance.
     */
    protected final NetcdfTransform transform;

    /**
     * The source coordinate reference system, usually geographic.
     */
    private final CoordinateReferenceSystem sourceCRS;

    /**
     * The target coordinate reference system, usually projected.
     */
    private final CoordinateReferenceSystem targetCRS;

    /**
     * Creates a new wrapper for the given NetCDF projection.
     *
     * @param projection The NetCDF projection object to wrap.
     */
    public NetcdfProjection(final Projection projection,
            final CoordinateReferenceSystem sourceCRS,
            final CoordinateReferenceSystem targetCRS)
    {
        this.transform = new NetcdfTransform(projection);
        this.sourceCRS = sourceCRS;
        this.targetCRS = targetCRS;
    }

    /**
     * Returns the NetCDF projection wrapped by this adapter.
     *
     * @return The NetCDF projection object.
     */
    @Override
    public Projection delegate() {
        return transform.projection;
    }

    /**
     * Returns the projection name. The default implementation delegates to
     * {@link Projection#getName()}.
     *
     * @return The projection name.
     *
     * @see Projection#getName()
     */
    @Override
    public String getCode() {
        return transform.projection.getName();
    }

    /**
     * Returns the source coordinate reference system, which is usually geographic.
     */
    @Override
    public CoordinateReferenceSystem getSourceCRS() {
        return sourceCRS;
    }

    /**
     * Returns the target coordinate reference system, which is usually projected.
     */
    @Override
    public CoordinateReferenceSystem getTargetCRS() {
        return targetCRS;
    }

    /**
     * Always {@code null} for a projection.
     */
    @Override
    public String getOperationVersion() {
        return null;
    }

    /**
     * Returns the operation method.
     */
    @Override
    public OperationMethod getMethod() {
        return transform.new Method();
    }

    /**
     * Wraps the NetCDF parameters in a GeoAPI parameter object. This method returns
     * a wrapper around the NetCDF {@link ucar.unidata.util.Parameter} objects.
     *
     * @see Projection#getProjectionParameters()
     */
    @Override
    public ParameterValueGroup getParameterValues() {
        return transform.getParameterValues();
    }

    /**
     * Returns an empty set, since this adapter have no information about the operation
     * accuracy.
     */
    @Override
    public Collection<PositionalAccuracy> getCoordinateOperationAccuracy() {
        return Collections.emptySet();
    }

    /**
     * Returns the domain of validity declared by the NetCDF projection, or {@code null} if none.
     *
     * @see ucar.unidata.geoloc.ProjectionImpl#getDefaultMapAreaLL()
     */
    @Override
    public Extent getDomainOfValidity() {
        final LatLonRect domain = ProjectionAdapter.factory(transform.projection).getDefaultMapAreaLL();
        if (domain != null) {
            final DefaultExtent extent = new DefaultExtent();
            extent.getGeographicElements().add(new DefaultGeographicBoundingBox(
                    domain.getLonMin(), domain.getLonMax(),
                    domain.getLatMin(), domain.getLatMax()));
            return extent;
        }
        return null;
    }

    /**
     * Returns {@code null}, since this adapter does not have information about the projection
     * scope.
     */
    @Override
    public InternationalString getScope() {
        return null;
    }

    /**
     * Returns a wrapper around the NetCDF projection for performing coordinate operations.
     */
    @Override
    public MathTransform getMathTransform() {
        return transform;
    }
}
