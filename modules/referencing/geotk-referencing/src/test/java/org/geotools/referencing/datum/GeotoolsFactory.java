/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotools.referencing.datum;

import java.util.Map;
import java.util.Date;

import javax.measure.unit.Unit;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;

import org.opengis.util.FactoryException;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.datum.DatumFactory;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.EngineeringDatum;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.datum.ImageDatum;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.datum.PrimeMeridian;
import org.opengis.referencing.datum.TemporalDatum;
import org.opengis.referencing.datum.VerticalDatum;
import org.opengis.referencing.datum.VerticalDatumType;

import org.apache.sis.metadata.iso.citation.DefaultCitation;


/**
 * A dummy factory which declare itself as a GeoTools implementation. The purpose of this
 * factory is to test the cohabitation of GeoTools and Geotk on the same classpath.
 * <p>
 * Every methods in this class except {@link #getVendor} throw an {@link AssertionError}.
 * They should never been invoked, because the Geotk implementation should always be
 * selected preferably to this one.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.02
 *
 * @since 3.02
 */
public final strictfp class GeotoolsFactory implements DatumFactory {
    /**
     * Returns the vendor, which is {@code "Geotools"} for this test. The lower-case
     * {@code "t"} is intentional since it was written that way in GeoTools 2.5.
     *
     * @return The "Geotools" vendor.
     */
    @Override
    public Citation getVendor() {
        return new DefaultCitation("Geotools");
    }

    @Override
    public EngineeringDatum createEngineeringDatum(Map<String, ?> properties) throws FactoryException {
        throw new AssertionError(properties);
    }

    @Override
    public GeodeticDatum createGeodeticDatum(Map<String, ?> properties, Ellipsoid ellipsoid, PrimeMeridian primeMeridian) throws FactoryException {
        throw new AssertionError(properties);
    }

    @Override
    public ImageDatum createImageDatum(Map<String, ?> properties, PixelInCell pixelInCell) throws FactoryException {
        throw new AssertionError(properties);
    }

    @Override
    public TemporalDatum createTemporalDatum(Map<String, ?> properties, Date origin) throws FactoryException {
        throw new AssertionError(properties);
    }

    @Override
    public VerticalDatum createVerticalDatum(Map<String, ?> properties, VerticalDatumType type) throws FactoryException {
        throw new AssertionError(properties);
    }

    @Override
    public Ellipsoid createEllipsoid(Map<String, ?> properties, double semiMajorAxis, double semiMinorAxis, Unit<Length> unit) throws FactoryException {
        throw new AssertionError(properties);
    }

    @Override
    public Ellipsoid createFlattenedSphere(Map<String, ?> properties, double semiMajorAxis, double inverseFlattening, Unit<Length> unit) throws FactoryException {
        throw new AssertionError(properties);
    }

    @Override
    public PrimeMeridian createPrimeMeridian(Map<String, ?> properties, double longitude, Unit<Angle> unit) throws FactoryException {
        throw new AssertionError(properties);
    }
}
