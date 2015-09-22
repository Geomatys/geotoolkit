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
package org.geotoolkit.build.project.report;

import java.util.Locale;
import java.io.File;
import java.io.IOException;

import org.opengis.util.FactoryException;
import org.opengis.util.InternationalString;
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

import org.apache.sis.util.CharSequences;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.referencing.CRS;
import org.apache.sis.referencing.AbstractIdentifiedObject;

import static org.geotoolkit.internal.referencing.CRSUtilities.EPSG_VERSION;


/**
 * Generates a list of supported CRS in the current directory. This class is for manual execution
 * after the EPSG database has been updated, or the projection implementations changed.
 * <p>
 * The {@linkplain #main(String[])} method creates a "{@code supported-codes.html}"
 * file in the {@code "modules/referencing/src/site/resources/"} project directory.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.16
 */
public final class CRSAuthorityCodesReport extends AuthorityCodesReport {
    /**
     * The symbol to write in from of EPSG code of CRS having an axis order different
     * then the (longitude, latitude) one.
     */
    private static final char YX_ORDER = '\u21B7';

    /**
     * The factory which create CRS instances.
     */
    private final CRSAuthorityFactory factory, xyOrder;

    /**
     * Creates a new instance.
     */
    private CRSAuthorityCodesReport() throws FactoryException {
        super(null);
        Reports.initialize(properties);
        properties.setProperty("FACTORY.NAME", "EPSG");
        properties.setProperty("FACTORY.VERSION", EPSG_VERSION);
        properties.setProperty("FACTORY.VERSION.SUFFIX", ", together with other sources");
        properties.setProperty("DESCRIPTION", "<p><b>Notation:</b></p>\n" +
                "<ul>\n" +
                "  <li>The " + YX_ORDER + " symbol in front of authority codes (${PERCENT.ANNOTATED} of them)" +
                " identifies the CRS having an axis order different than (<var>easting</var>, <var>northing</var>).</li>\n" +
                "  <li>The <del>codes with a strike</del> (${PERCENT.DEPRECATED} of them) identify deprecated CRS." +
                " In some cases, the remarks column indicates the replacement.</li>\n" +
                "</ul>");
        factory = CRS.getAuthorityFactory(false);
        xyOrder = CRS.getAuthorityFactory(true);
        add(factory);
        /*
         * We have to use this hack for now because exceptions are formatted in the current locale.
         */
        Locale.setDefault(getLocale());
    }

    /**
     * Generates the HTML report.
     *
     * @param  args Ignored.
     * @throws FactoryException If an error occurred while fetching the CRS.
     * @throws IOException If an error occurred while writing the HTML file.
     */
    public static void main(final String[] args) throws FactoryException, IOException {
        final CRSAuthorityCodesReport writer = new CRSAuthorityCodesReport();
        final File file = writer.write(new File(Reports.getProjectRootDirectory(),
                "modules/referencing/src/site/resources/supported-codes.html"));
        System.out.println("Created " + file.getAbsolutePath());
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
            return CharSequences.camelCaseToSentence(type.name().toLowerCase(getLocale())) + " height";
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
     * {@link org.opengis.test.report.AuthorityCodesReport.Row} attribute values created
     * by GeoAPI.
     */
    @Override
    protected Row createRow(final String code, final IdentifiedObject object) {
        final Row row = super.createRow(code, object);
        final CoordinateReferenceSystem crs = (CoordinateReferenceSystem) object;
        try {
            final CoordinateReferenceSystem crsXY = xyOrder.createCoordinateReferenceSystem(code);
            if (!CRS.equalsIgnoreMetadata(crs.getCoordinateSystem(), crsXY.getCoordinateSystem())) {
                row.annotation = YX_ORDER;
            }
        } catch (FactoryException e) {
            Logging.unexpectedException(null, CRSAuthorityCodesReport.class, "createRow", e);
        }
        row.remark = getRemark(crs);
        if (object instanceof AbstractIdentifiedObject) {
            row.isDeprecated = ((AbstractIdentifiedObject) object).isDeprecated();
        }
        /*
         * If the object is deprecated, try to find the reason.
         * Don't take the whole comment, because it may be pretty long.
         */
        if (row.isDeprecated) {
            final InternationalString i18n = object.getRemarks();
            if (i18n != null) {
                String remark = i18n.toString(getLocale());
                final int s = Math.max(remark.lastIndexOf("Superseded"),
                              Math.max(remark.lastIndexOf("superseded"),
                              Math.max(remark.lastIndexOf("Replaced"),
                              Math.max(remark.lastIndexOf("replaced"),
                              Math.max(remark.lastIndexOf("See"),
                                       remark.lastIndexOf("see"))))));
                if (s >= 0) {
                    final int start = remark.lastIndexOf('.', s) + 1;
                    final int end = remark.indexOf('.', s);
                    remark = (end >= 0) ? remark.substring(start, end) : remark.substring(start);
                    remark = CharSequences.trimWhitespaces(remark.replace('¶', '\n').trim());
                    if (!remark.isEmpty()) {
                        row.remark = remark;
                    }
                }
            }
        }
        return row;
    }

    /**
     * Invoked when a CRS creation failed. This method modifies the default
     * {@link org.opengis.test.report.AuthorityCodesReport.Row} attribute values
     * created by GeoAPI.
     */
    @Override
    protected Row createRow(final String code, final FactoryException exception) {
        final Row row = super.createRow(code, exception);
        try {
            row.name = factory.getDescriptionText(code).toString(getLocale());
        } catch (FactoryException e) {
            Logging.unexpectedException(null, CRSAuthorityCodesReport.class, "createRow", e);
        }
        String message;
        if (code.startsWith("AUTO2:")) {
            // It is normal to be unable to instantiate an "AUTO" CRS,
            // because those authority codes need parameters.
            message = "Projected";
            row.hasError = false;
        } else {
            message = exception.getMessage();
            if (message.contains("Unable to format units in UCUM")) {
                // Simplify a very long and badly formatted message.
                message = "Unable to format units in UCUM";
            }
        }
        row.remark = message;
        return row;
    }
}
