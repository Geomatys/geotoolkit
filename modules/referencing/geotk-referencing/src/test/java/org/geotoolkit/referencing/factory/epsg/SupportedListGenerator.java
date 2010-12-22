/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.factory.epsg;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import org.opengis.util.FactoryException;
import org.opengis.util.InternationalString;
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

import org.geotoolkit.util.Version;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.test.ReportGenerator;
import org.geotoolkit.internal.StringUtilities;


/**
 * Generated a list of supported CRS in the current directory. This class is not really a test,
 * but failure to execute it would be an indication of problem. This class is for manual execution
 * after the EPSG database has been updated, or the projection implementations changed.
 * <p>
 * The result are formatted in the HTML format.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.16
 *
 * @since 3.16
 */
public final class SupportedListGenerator extends ReportGenerator {
    /**
     * The symbol to write in from of EPSG code of CRS having an axis order different
     * then the (longitude, latitude) one.
     */
    private static final char YX_ORDER = '\u21B7';

    /**
     * The authority code.
     */
    private final String code;

    /**
     * The CRS description, or {@code null} if none.
     */
    private final String description;

    /**
     * Whatever the CRS is supported.
     */
    private boolean isSupported;

    /**
     * Whatever the CRS orders longitude before latitude.
     */
    private boolean isLongitudeFirst = true;

    /**
     * A message to display after the name.
     */
    private String message;

    /**
     * For internal usage only.
     */
    private SupportedListGenerator(final String code, final InternationalString description) {
        this.code = code;
        this.description = (description != null) ? description.toString(LOCALE) : null;
    }

    /**
     * Writes this object to the given stream.
     */
    private void write(final Writer out, final boolean highlight) throws IOException {
        out.write("<tr");
        if (highlight) {
            out.write(" bgcolor=\"lavender\"");
        }
        out.write("><td>");
        if (!isLongitudeFirst) {
            out.write(YX_ORDER);
        }
        out.write("</td><td nowrap><code>");
        out.write(code);
        out.write("&nbsp;</code></td nowrap><td>");
        out.write(description);
        out.write("</td><td nowrap>");
        if (!isSupported) {
            out.write("<font color=\"red\">");
        }
        out.write(message);
        if (!isSupported) {
            out.write("</font>");
        }
        out.write("</td></tr>\n");
    }

    /**
     * Returns a message for the given CRS.
     */
    private static String getMessage(final CoordinateReferenceSystem crs) {
        if (crs instanceof GeographicCRS) {
            return (crs.getCoordinateSystem().getDimension() == 3) ? "Geographic 3D" : "Geographic";
        }
        if (crs instanceof GeneralDerivedCRS) {
            return ((GeneralDerivedCRS) crs).getConversionFromBase().getMethod().getName().getCode().replace('_', ' ');
        }
        if (crs instanceof GeocentricCRS) {
            final CoordinateSystem cs = crs.getCoordinateSystem();
            if (cs instanceof CartesianCS) {
                return "Geocentric (cartesian coordinate system)";
            } else if (cs instanceof SphericalCS) {
                return "Geocentric (spherical coordinate system)";
            }
            return "Geocentric";
        }
        if (crs instanceof VerticalCRS) {
            final VerticalDatumType type = ((VerticalCRS) crs).getDatum().getVerticalDatumType();
            return StringUtilities.makeSentence(type.name().toLowerCase(LOCALE)) + " height";
        }
        if (crs instanceof CompoundCRS) {
            final StringBuilder buffer = new StringBuilder();
            for (final CoordinateReferenceSystem component : ((CompoundCRS) crs).getComponents()) {
                if (buffer.length() != 0) {
                    buffer.append(" + ");
                }
                buffer.append(getMessage(component));
            }
            return buffer.toString();
        }
        if (crs instanceof EngineeringCRS) {
            return "Engineering (" + crs.getCoordinateSystem().getName().getCode() + ')';
        }
        return "";
    }

    /**
     * Generates the list of CRS now.
     *
     * @param args Ignored.
     * @throws Exception If an error occurred while reading the database, or writing the HTML file.
     */
    public static void main(final String[] args) throws Exception {
        int numValids = 0, numYX = 0;
        Locale.setDefault(LOCALE);
        final List<SupportedListGenerator> list = new ArrayList<SupportedListGenerator>();
        final CRSAuthorityFactory factory = CRS.getAuthorityFactory(false);
        final CRSAuthorityFactory xyOrder = CRS.getAuthorityFactory(true);
        for (final String code : factory.getAuthorityCodes(CoordinateReferenceSystem.class)) {
            final SupportedListGenerator element = new SupportedListGenerator(code, factory.getDescriptionText(code));
            if (code.startsWith("AUTO2:")) {
                element.message = "Projected";
                element.isSupported = true;
                numValids++;
            } else try {
                final CoordinateReferenceSystem crs = factory.createCoordinateReferenceSystem(code);
                element.isLongitudeFirst = CRS.equalsIgnoreMetadata(crs.getCoordinateSystem(),
                        xyOrder.createCoordinateReferenceSystem(code).getCoordinateSystem());
                element.message = getMessage(crs);
                element.isSupported = true;
                numValids++;
            } catch (FactoryException exception) {
                String message = message = exception.getMessage();
                if (message.contains("Unable to format units in UCUM")) {
                    // Simplify a very long and badly formatted message.
                    message = "Unable to format units in UCUM";
                }
                element.message = message;
            }
            if (!element.isLongitudeFirst) {
                numYX++;
            }
            list.add(element);
        }
        int n = 0;
        final Writer out = openHTML(new File("supported-codes.html"), "Authority codes for Coordinate Reference Systems");
        out.write("<p>This list is generated from the EPSG database version ");
        out.write(ThreadedEpsgFactory.VERSION);
        out.write(", together with other sources.\n");
        out.write("All those <cite>Coordinate Reference Systems</cite> (CRS) are supported by the " +
                  "<a href=\"http://www.geotoolkit.org/modules/referencing/index.html\">" +
                  "Geotoolkit.org referencing module</a> version ");
        String version = Version.GEOTOOLKIT.toString();
        final int snapshot = version.lastIndexOf('-');
        if (snapshot >= 2) {
            version = version.substring(0, snapshot);
        }
        out.write(version);
        out.write(", except those with a red text in the last column.\nThere is ");
        out.write(String.valueOf(list.size()));
        out.write(" codes, ");
        out.write(String.valueOf(100 * numValids / list.size())); // Really want rounding toward 0.
        out.write("% of them being supported.</p>\n" +
                  "<p><b>Notation:</b></p>\n" +
                  "<ul>\n" +
                  "  <li>The " + YX_ORDER + " symbol in front of authority codes (");
        out.write(String.valueOf(Math.round(100.0 * numYX / list.size())));
        out.write("% of them) identifies the CRS having an axis order different than " +
                  "(<var>easting</var>, <var>northing</var>).</li>\n" +
                  "</ul>");

        out.write("<table bgcolor=\"aliceblue\" cellpadding=\"0\" cellspacing=\"0\">\n");
        out.write("<tr bgcolor=\"lightskyblue\" align=\"left\">"
                + "<th height=\"24\"></th>"
                + "<th>Code</th>"
                + "<th>Description</th>"
                + "<th>Type, or reason for unsupport</th>\n");
        for (final SupportedListGenerator element : list) {
            element.write(out, (n & 2) != 0);
            n++;
        }
        out.write("</table>\n");
        closeHTML(out);
        System.exit(0);
    }
}
