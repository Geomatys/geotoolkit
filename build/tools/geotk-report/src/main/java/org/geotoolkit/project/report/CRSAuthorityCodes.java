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
package org.geotoolkit.project.report;

import java.io.File;
import java.io.IOException;

import org.opengis.util.FactoryException;
import org.opengis.referencing.AuthorityFactory;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.SphericalCS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.crs.GeocentricCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.EngineeringCRS;
import org.opengis.referencing.crs.GeneralDerivedCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.datum.VerticalDatumType;
import org.opengis.test.report.AuthorityCodesReport;

import org.geotoolkit.util.Strings;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.referencing.CRS;

import static org.geotoolkit.internal.referencing.CRSUtilities.EPSG_VERSION;


/**
 * Generates a list of supported CRS in the current directory. This class is for manual execution
 * after the EPSG database has been updated, or the projection implementations changed.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.16
 */
public final class CRSAuthorityCodes extends AuthorityCodesReport {
    /**
     * The symbol to write in from of EPSG code of CRS having an axis order different
     * then the (longitude, latitude) one.
     */
    private static final char YX_ORDER = '\u21B7';

    /**
     * The factory which create CRS instances having the longitude axis before the latitude axis.
     */
    private final CRSAuthorityFactory xyOrder;

    /**
     * Creates a new instance.
     */
    private CRSAuthorityCodes() throws FactoryException {
        super(null);
        Reports.initialize(properties);
        properties.setProperty("FACTORY.NAME", "EPSG");
        properties.setProperty("FACTORY.VERSION", EPSG_VERSION);
        xyOrder = CRS.getAuthorityFactory(true);
        add(CRS.getAuthorityFactory(false));
    }

    /**
     * Generates the HTML report.
     *
     * @param  args Ignored.
     * @throws FactoryException If an error occurred while fetching the CRS.
     * @throws IOException If an error occurred while writing the HTML file.
     */
    public static void main(final String[] args) throws FactoryException, IOException {
        final CRSAuthorityCodes writer = new CRSAuthorityCodes();
        writer.write(new File("supported-codes.html"));
    }

    /**
     * Creates the remarks for the given CRS.
     */
    private String getRemark(final CoordinateReferenceSystem crs) {
        if (crs instanceof GeographicCRS) {
            return (crs.getCoordinateSystem().getDimension() == 3) ? "Geographic 3D" : "Geographic";
        }
        if (crs instanceof GeneralDerivedCRS) {
            return ((GeneralDerivedCRS) crs).getConversionFromBase().getMethod().getName().getCode().replace('_', ' ');
        }
        if (crs instanceof GeocentricCRS) {
            final CoordinateSystem cs = crs.getCoordinateSystem();
            if (cs instanceof CartesianCS) {
                return "Geocentric (Cartesian coordinate system)";
            } else if (cs instanceof SphericalCS) {
                return "Geocentric (spherical coordinate system)";
            }
            return "Geocentric";
        }
        if (crs instanceof VerticalCRS) {
            final VerticalDatumType type = ((VerticalCRS) crs).getDatum().getVerticalDatumType();
            return Strings.camelCaseToSentence(type.name().toLowerCase(getLocale())) + " height";
        }
        if (crs instanceof CompoundCRS) {
            final StringBuilder buffer = new StringBuilder();
            for (final CoordinateReferenceSystem component : ((CompoundCRS) crs).getComponents()) {
                if (buffer.length() != 0) {
                    buffer.append(" + ");
                }
                buffer.append(getRemark(component));
            }
            return buffer.toString();
        }
        if (crs instanceof EngineeringCRS) {
            return "Engineering (" + crs.getCoordinateSystem().getName().getCode() + ')';
        }
        return "";
    }

    /**
     * Invoked when a CRS has been successfully created. This method modifies the default
     * {@link Row} attribute values created by GeoAPI.
     */
    @Override
    protected Row createRow(final AuthorityFactory factory, final String code, final IdentifiedObject object) throws FactoryException {
        final Row row = super.createRow(factory, code, object);
        if (code.startsWith("AUTO2:")) {
            row.remark = "Projected";
        } else {
            final CoordinateReferenceSystem crs = (CoordinateReferenceSystem) object;
            try {
                final CoordinateReferenceSystem crsXY = xyOrder.createCoordinateReferenceSystem(code);
                if (!CRS.equalsIgnoreMetadata(crs.getCoordinateSystem(), crsXY.getCoordinateSystem())) {
                    row.annotation = YX_ORDER;
                }
            } catch (FactoryException e) {
                Logging.unexpectedException(CRSAuthorityCodes.class, "createRow", e);
            }
            row.remark = getRemark(crs);
        }
        return row;
    }

    /**
     * Invoked when a CRS creation failed. This method modifies the default
     * {@link Row} attribute values created by GeoAPI.
     */
    @Override
    protected Row createRow(final AuthorityFactory factory, final String code, final FactoryException exception) throws FactoryException {
        final Row row = super.createRow(factory, code, exception);
        row.name = factory.getDescriptionText(code).toString(getLocale());
        String message = exception.getMessage();
        if (message.contains("Unable to format units in UCUM")) {
            // Simplify a very long and badly formatted message.
            message = "Unable to format units in UCUM";
        }
        row.remark = message;
        return row;
    }
}
