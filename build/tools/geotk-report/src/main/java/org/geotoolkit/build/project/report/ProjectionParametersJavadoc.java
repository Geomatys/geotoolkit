/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2012, Geomatys
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

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.io.IOException;
import javax.measure.unit.Unit;

import org.opengis.util.GenericName;
import org.opengis.util.FactoryException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.GeneralDerivedCRS;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.apache.sis.util.Arrays;

import org.geotoolkit.util.Deprecable;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.converter.Numbers;
import org.geotoolkit.measure.Latitude;
import org.geotoolkit.measure.Longitude;
import org.geotoolkit.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.geotoolkit.referencing.operation.provider.*;
import org.geotoolkit.referencing.CRS;


/**
 * Updates the javadoc of provider classes in the {@link org.geotoolkit.referencing.operation.provider}
 * package. This tools need to be run manually by the developer (together with other report generators)
 * after any provider has been modified.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final class ProjectionParametersJavadoc extends JavadocUpdater {
    /**
     * Runs from the command line.
     *
     * @param  args Ignored.
     * @throws ReflectiveOperationException Should never happen.
     * @throws IOException If an error occurred while updating the source files.
     * @throws FactoryException If an error occurred while querying the authority factory.
     */
    public static void main(final String[] args) throws ReflectiveOperationException, IOException, FactoryException {
        Locale.setDefault(Locale.ENGLISH);
        final ProjectionParametersJavadoc updater = new ProjectionParametersJavadoc();
        for (final Class<?> provider : updater.parameters) {
            final ParameterDescriptorGroup parameters = (ParameterDescriptorGroup) provider.getField("PARAMETERS").get(null);
            final String module;
                 if (provider == WarpPolynomial  .class) module = "coverage/geotk-coverage";
            else if (provider == EllipsoidToGeoid.class) module = "referencing/geotk-referencing3D";
            else                                         module = "referencing/geotk-referencing";
            updater.createSummaryTable(parameters);
            updater.rewriteClassComment(module, provider,
                    "<!-- PARAMETERS " + provider.getSimpleName() + " -->",
                    "<!-- END OF PARAMETERS -->");
            updater.createGlobalTable(parameters);
            updater.rewriteMemberComment(module, provider);
        }
    }

    /**
     * The classes for which to update the javadoc.
     *
     * <b>Tip:</b> copy and paste {@link ParametersTest#parameters},
     * then replace {@code PARAMETERS} by {@code class}.
     */
    private final Class<?>[] parameters = {
        AbridgedMolodensky                .class,
        Affine                            .class,
        AlbersEqualArea                   .class,
        CassiniSoldner                    .class,
        CoordinateFrameRotation           .class,
        EllipsoidToGeocentric             .class,
        EllipsoidToGeoid                  .class, // Provided in geotk-referencing3D module.
        EquidistantCylindrical            .class,
        Exponential                       .class,
        GeocentricToEllipsoid             .class,
        GeocentricTranslation             .class,
        HotineObliqueMercator             .class,
        HotineObliqueMercator.TwoPoint    .class,
        Krovak                            .class,
        LambertAzimuthalEqualArea         .class,
        LambertConformal1SP               .class,
        LambertConformal2SP               .class,
        LambertConformal2SP.Belgium       .class,
        Logarithmic                       .class,
        LongitudeRotation                 .class,
        Mercator1SP                       .class,
        Mercator2SP                       .class,
        MillerCylindrical                 .class,
        Molodensky                        .class,
        NADCON                            .class,
        NTv2                              .class,
        NewZealandMapGrid                 .class,
        ObliqueMercator                   .class,
        ObliqueMercator.TwoPoint          .class,
        ObliqueStereographic              .class,
        Orthographic                      .class,
        PlateCarree                       .class,
        PolarStereographic                .class,
        PolarStereographic.North          .class,
        PolarStereographic.South          .class,
        PolarStereographic.VariantB       .class,
        Polyconic                         .class,
        PositionVector7Param              .class,
        PseudoMercator                    .class,
        RGF93                             .class,
        Stereographic                     .class,
        TransverseMercator                .class,
        TransverseMercator.SouthOrientated.class,
        WarpPolynomial                    .class  // Provided in the geotk-coverage module.
    };

    /**
     * Parameters to default to the latitude of origin. We can hardly detect those cases
     * automatically, since the behavior for the default value is hard-coded in Java.
     */
    private final GeneralParameterDescriptor defaultToLatitudeOfOrigin[] = {
        AlbersEqualArea            .PARAMETERS.descriptor("Latitude of 1st standard parallel"),
        LambertConformal2SP        .PARAMETERS.descriptor("Latitude of 1st standard parallel"),
        LambertConformal2SP.Belgium.PARAMETERS.descriptor("Latitude of 1st standard parallel")
    };

    /**
     * Parameters to default to the first standard parallel. We can hardly detect those
     * cases automatically, since the behavior for the default value is hard-coded in Java.
     */
    private final GeneralParameterDescriptor defaultToStandardParallel1[] = {
        AlbersEqualArea            .PARAMETERS.descriptor("Latitude of 2nd standard parallel"),
        LambertConformal2SP        .PARAMETERS.descriptor("Latitude of 2nd standard parallel"),
        LambertConformal2SP.Belgium.PARAMETERS.descriptor("Latitude of 2nd standard parallel")
    };

    /**
     * Parameters to default to the azimuth. We can hardly detect those cases automatically,
     * since the behavior for the default value is hard-coded in Java.
     */
    private final GeneralParameterDescriptor defaultToAzimuth[] = {
        ObliqueMercator      .PARAMETERS.descriptor("Angle from Rectified to Skew Grid"),
        HotineObliqueMercator.PARAMETERS.descriptor("Angle from Rectified to Skew Grid")
    };

    /**
     * The union of domain of validity of all map projections using a method of the given name.
     */
    private final Map<String, DefaultGeographicBoundingBox> domainOfValidity;

    /**
     * Creates a new instance to be used for updating the javadoc.
     */
    private ProjectionParametersJavadoc() throws IOException, FactoryException {
        super("<!-- GENERATED PARAMETERS", "*/");
        domainOfValidity = new HashMap<>();
        final CRSAuthorityFactory factory = CRS.getAuthorityFactory(Boolean.FALSE);
        for (final String code : factory.getAuthorityCodes(GeneralDerivedCRS.class)) {
            final CoordinateReferenceSystem crs;
            try {
                crs = factory.createCoordinateReferenceSystem(code);
            } catch (FactoryException e) {
                continue; // Ignore and inspect the next element.
            }
            if (crs instanceof GeneralDerivedCRS) {
                final GeographicBoundingBox candidate = CRS.getGeographicBoundingBox(crs);
                if (candidate != null) {
                    final String name = ((GeneralDerivedCRS) crs).getConversionFromBase().getMethod().getName().getCode();
                    DefaultGeographicBoundingBox validity = domainOfValidity.get(name);
                    if (validity == null) {
                        validity = new DefaultGeographicBoundingBox(candidate);
                        domainOfValidity.put(name, validity);
                    } else {
                        validity.add(candidate);
                    }
                }
            }
        }
    }

    /**
     * Creates the table that summarize the operation.
     */
    private void createSummaryTable(final ParameterDescriptorGroup group) {
        DefaultGeographicBoundingBox validity = null;
        for (final GenericName name : group.getAlias()) {
            final String tip = name.tip().toString();
            final DefaultGeographicBoundingBox candidate = domainOfValidity.get(tip);
            if (candidate != null) {
                if (validity == null) {
                    validity = new DefaultGeographicBoundingBox(candidate);
                } else {
                    validity.add(candidate);
                }
            }
        }
        lines.clear();
        lines.add("<p>The following table summarizes the parameters recognized by this provider.");
        lines.add("For a more detailed parameter list, see the {@link #PARAMETERS} constant.</p>");
        lines.add("<blockquote><p><b>Operation name:</b> {@code " + group.getName().getCode() + (validity != null ? "}" : "}</p>"));
        if (validity != null) {
            lines.add("<br><b>Area of use:</b> <font size=\"-1\">(union of CRS domains of validity in EPSG database)</font></p>");
            lines.add("<blockquote><table class=\"compact\">");
            lines.add("  <tr><td><b>in latitudes:</b></td><td class=\"onright\">" +
                    new Latitude(validity.getSouthBoundLatitude()) + "</td><td>to</td><td class=\"onright\">" +
                    new Latitude(validity.getNorthBoundLatitude()) + "</td></tr>");
            lines.add("  <tr><td><b>in longitudes:</b></td><td class=\"onright\">" +
                    new Longitude(validity.getWestBoundLongitude()) + "</td><td>to</td><td class=\"onright\">" +
                    new Longitude(validity.getEastBoundLongitude()) + "</td></tr>");
            lines.add("</table></blockquote>");
        }
        lines.add("<table class=\"geotk\">");
        lines.add("  <tr><th>Parameter name</th><th>Default value</th></tr>");
        for (final GeneralParameterDescriptor gp : group.descriptors()) {
            final ParameterDescriptor<?> param = (ParameterDescriptor<?>) gp;
            lines.add("  <tr><td>{@code " + param.getName().getCode() + "}</td>" +
                    "<td>" + getDefaultValue(param, getUnit(param)) + "</td></tr>");
        }
        lines.add("</table></blockquote>");
    }

    /**
     * Creates the table that describe the operation, and each parameters. This table
     * includes sub-tables created by {@link #createNameTable(GeneralParameterDescriptor)}
     * and {@link #createConditionTable(ParameterDescriptor)}.
     */
    private void createGlobalTable(final ParameterDescriptorGroup group) {
        lines.clear();
        lines.add("<table class=\"geotk\" border=\"1\">");
        lines.add("  <tr><th colspan=\"2\">");
        createNameTable(group);
        lines.add("  </th></tr>");
        for (final GeneralParameterDescriptor param : group.descriptors()) {
            lines.add("  <tr><td>");
            createNameTable(param);
            lines.add("  </td><td>");
            createConditionTable((ParameterDescriptor<?>) param);
            lines.add("  </td></tr>");
        }
        lines.add("</table>");
    }

    /**
     * Creates the table of names for the given parameter. The table formatted by this
     * method will use 3 columns: headers, codespaces and codes.
     */
    private void createNameTable(final GeneralParameterDescriptor descriptor) {
        /*
         * First, build a list of names without duplicated values.
         */
        final Set<String> names = new LinkedHashSet<>();
        names.add(toHTML(descriptor.getName()));
        for (final GenericName name : descriptor.getAlias()) {
            names.add(toHTML(name));
        }
        final int firstIdentifier = names.size();
        for (final ReferenceIdentifier identifier : descriptor.getIdentifiers()) {
            names.add(toHTML(identifier));
        }
        lines.add("    <table class=\"compact\">");
        /*
         * Adds one additional column for separating primary name, aliases and identifiers.
         */
        int i=0;
        for (final String name : names) {
            final String header;
            if      (i == 0)               header = "<b>Name:</b>";
            else if (i == firstIdentifier) header = "<b>Identifier:</b>";
            else if (i == 1)               header = "<b>Alias:</b>";
            else                           header = "";
            lines.add("      <tr><td>" + header + "</td>" + name + "</tr>");
            i++;
        }
        lines.add("    </table>");
    }

    /**
     * Creates the table of conditions for the given parameter (whatever the parameter is
     * mandatory or optional, etc.).
     */
    private void createConditionTable(final ParameterDescriptor<?> descriptor) {
        final Class<?> valueClass = descriptor.getValueClass();
        lines.add("    <table class=\"compact\">");
        lines.add("      <tr><td><b>Type:</b></td><td>{@code " + valueClass.getSimpleName() + "}</td></tr>");
        lines.add("      <tr><td><b>Obligation:</b></td><td>" + (descriptor.getMinimumOccurs() != 0 ? "mandatory" : "optional") + "</td></tr>");
        final String unit = getUnit(descriptor);
        if (Number.class.isAssignableFrom(valueClass)) {
            NumberRange<?> range = NumberRange.createBestFit(
                    (Number) descriptor.getMinimumValue(), true,
                    (Number) descriptor.getMaximumValue(), true);
            if (range == null) {
                range = new NumberRange<>(Double.class, null, null);
            }
            lines.add("      <tr><td><b>Value range:</b>" + "</td><td>" + range + unit + "</td></tr>");
        }
        final String defaultValue = getDefaultValue(descriptor, unit);
        if (!defaultValue.isEmpty()) {
            lines.add("      <tr><td><b>Default value:</b>" + "</td><td>" + defaultValue + "</td></tr>");
        }
        lines.add("    </table>");
    }

    /**
     * Returns the string representation of the given parameter unit,
     * or an empty string (never {@code null}) if none.
     */
    private static String getUnit(final ParameterDescriptor<?> param) {
        final Unit<?> unit = param.getUnit();
        String text;
        if (unit != null) {
            try {
                text = unit.toString();
            } catch (IllegalArgumentException e) { // Workaround for JSR-275 implementation bug.
                text = "";
            }
            if (text.equals("deg"))    text = "°";
            else if (text.equals("m")) text = " metres";
            else if (!text.isEmpty())  text = " " + text;
        } else {
            text = "";
        }
        return text;
    }

    /**
     * Returns the string representation of the given parameter default value,
     * or an empty string (never {@code null}) if none.
     */
    private String getDefaultValue(final ParameterDescriptor<?> param, final String unit) {
        Object defaultValue = param.getDefaultValue();
        if (defaultValue != null) {
            if (defaultValue instanceof Number) {
                // Trim the fractional part if unnecessary (e.g. "0.0" to "0").
                defaultValue = Numbers.finestNumber((Number) defaultValue);
            } else if (defaultValue instanceof String) {
                return "{@code \"" + defaultValue + "\"}";
            }
        } else if (param.getMinimumOccurs() == 0) {
            if (Arrays.contains(defaultToLatitudeOfOrigin, param)) {
                return "<var>latitude of origin</var>";
            } else if (Arrays.contains(defaultToStandardParallel1, param)) {
                return "<var>standard parallel 1</var>";
            } else if (Arrays.contains(defaultToAzimuth, param)) {
                return "<var>Azimuth of initial line</var>";
            } else if (param.getValueClass() == Boolean.class) {
                defaultValue = Boolean.FALSE;
            }
        }
        return (defaultValue != null) ? defaultValue + unit : "";
    }

    /**
     * Returns the HTML string for the given identifier.
     * This method uses 2 table columns.
     */
    private static String toHTML(final ReferenceIdentifier name) {
        String tip = name.getCode();
        if (((Deprecable) name).isDeprecated()) {
            tip = "<del>" + tip + "</del>";
        }
        return toHTML(name.getCodeSpace(), tip);
    }

    /**
     * Returns the HTML string for the given name.
     * This method uses 2 table columns.
     */
    private static String toHTML(final GenericName name) {
        String tip = name.tip().toString();
        if (((Deprecable) name).isDeprecated()) {
            tip = "<del>" + tip + "</del>";
        }
        return toHTML(name.head().toString(), tip);
    }

    /**
     * Returns the HTML string for the given codespace and code.
     * This method uses 2 table columns.
     */
    private static String toHTML(final String codespace, final String code) {
        // Note: use <code>, not {@code}, because the value may contain other HTML tags.
        return "<td class=\"onright\"><code>" + codespace + "</code>:</td><td class=\"onleft\"><code>" + code + "</code></td>";
    }
}
