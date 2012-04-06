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
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;

import org.opengis.util.GenericName;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterDescriptor;

import org.geotoolkit.util.Deprecable;
import org.geotoolkit.util.NumberRange;
import org.geotoolkit.util.converter.Numbers;
import org.geotoolkit.referencing.operation.provider.*;


/**
 * Updates the javadoc of provider classes in the {@link org.geotoolkit.referencing.operation.provider}
 * package. This tools need to be run manually be the developer (together with other report generators)
 * after any provider has been modified.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.20
 */
public final class ProjectionParametersJavadoc {
    /**
     * Beginning of a row.
     */
    private static final String ROW_START = "      <tr><th align=\"left\">";

    /**
     * Some spaces to insert between the first and the second column.
     */
    private static final String SEPARATOR = "&nbsp;&nbsp;</th><td>";

    /**
     * Ending of a row.
     */
    private static final String ROW_END = "</td></tr>";

    /**
     * Runs from the command line.
     *
     * @param args Ignored.
     * @throws ReflectiveOperationException Should never happen.
     */
    public static void main(String[] args) throws ReflectiveOperationException {
        Locale.setDefault(Locale.ENGLISH);
        final ProjectionParametersJavadoc updater = new ProjectionParametersJavadoc();
        for (final Class<?> parameter : updater.parameters) {
            updater.createGlobalTable((ParameterDescriptorGroup) parameter.getField("PARAMETERS").get(null));
        }
        System.out.println(updater);
    }

    /**
     * The classes for which to update the javadoc.
     *
     * <b>Tip:</b> copy and paste {@link ParametersTest#parameters},
     * then replace {@code PARAMETERS} by {@code class}.
     */
    private final Class<?>[] parameters = {
        AbridgedMolodensky       .class,
        Affine                   .class,
        AlbersEqualArea          .class,
        CassiniSoldner           .class,
        CoordinateFrameRotation  .class,
        EllipsoidToGeocentric    .class,
        EquidistantCylindrical   .class,
        Exponential              .class,
        GeocentricToEllipsoid    .class,
        GeocentricTranslation    .class,
        HotineObliqueMercator    .class,
        Krovak                   .class,
        LambertAzimuthalEqualArea.class,
        LambertConformal1SP      .class,
        LambertConformal2SP      .class,
        Logarithmic              .class,
        LongitudeRotation        .class,
        Mercator1SP              .class,
        Mercator2SP              .class,
        MillerCylindrical        .class,
        Molodensky               .class,
        NADCON                   .class,
        NTv2                     .class,
        NewZealandMapGrid        .class,
        ObliqueMercator          .class,
        ObliqueStereographic     .class,
        Orthographic             .class,
        PlateCarree              .class,
        PolarStereographic       .class,
        Polyconic                .class,
        PositionVector7Param     .class,
        PseudoMercator           .class,
        RGF93                    .class,
        Stereographic            .class,
        TransverseMercator       .class
    };

    /**
     * The lines in HTML formats, without carriage returns. All {@code createFoo(...)} methods
     * defined in this class will append lines in HTML format to this list. After the list has
     * been completed, its content can be printed directly (for example by {@link #toString()},
     * or can be prefixed by the {@code " * "} characters of the lines are to be inserted in a
     * class Javadoc.
     */
    private final List<String> lines;

    /**
     * Creates a new instance to be used for updating the javadoc.
     */
    private ProjectionParametersJavadoc() {
        lines = new ArrayList<>();
    }

    /**
     * Creates the table that describe the operation, and each parameters. This table
     * includes sub-tables created by {@link #createNameTable(GeneralParameterDescriptor)}
     * and {@link #createConditionTable(ParameterDescriptor)}.
     */
    private void createGlobalTable(final ParameterDescriptorGroup group) {
        lines.add("<table bgcolor=\"#F4F8FF\" border=\"1\" cellspacing=\"0\" cellpadding=\"6\">");
        lines.add("  <tr bgcolor=\"#B9DCFF\" valign=\"top\"><td colspan=\"2\">");
        createNameTable(group);
        lines.add("  </td></tr>");
        for (final GeneralParameterDescriptor param : group.descriptors()) {
            lines.add("  <tr valign=\"top\"><td>");
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
        lines.add("    <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
        /*
         * Adds one additional column for separating primary name, aliases and identifiers.
         */
        int i=0;
        for (final String name : names) {
            final String header;
            if      (i == 0)               header = "Name:";
            else if (i == firstIdentifier) header = "Identifier:";
            else if (i == 1)               header = "Alias:";
            else                           header = "";
            lines.add(ROW_START + header + SEPARATOR + name + ROW_END);
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
        lines.add("    <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
        lines.add(ROW_START + "Type:" + SEPARATOR + "<code>" + valueClass.getSimpleName() + "</code></td></tr>");
        lines.add(ROW_START + "Obligation:" + SEPARATOR + (descriptor.getMinimumOccurs() != 0 ? "mandatory" : "optional") + "</td></tr>");
        /*
         * Get a string representation of the units.
         * This will be used both by the range and the default value.
         */
        Object unit = descriptor.getUnit();
        if (unit != null) {
            try {
                unit = unit.toString();
            } catch (IllegalArgumentException e) { // Workaround for JSR-275 implementation bug.
                unit = "";
            }
            if (unit.equals("deg")) unit = "°";
            else if (unit.equals("m")) unit = " metres";
            else if (!unit.equals("")) unit = " " + unit;
        } else {
            unit = "";
        }
        /*
         * Format the range of valid values. This range may include infinity symbols.
         */
        if (Number.class.isAssignableFrom(valueClass)) {
            NumberRange<?> range = NumberRange.createBestFit(
                    (Number) descriptor.getMinimumValue(), true,
                    (Number) descriptor.getMaximumValue(), true);
            if (range == null) {
                range = new NumberRange<>(Double.class, null, null);
            }
            lines.add(ROW_START +  "Value range:" + SEPARATOR + range + unit + ROW_END);
        }
        /*
         * Format the default value. If no default value were explicitely specified and the
         * parameter is optional, then '0' or 'false' is used as an implicit default value.
         * The default value are sometime not explicitely specified for optional parameters
         * in order to avoid the creation of a ParameterDescriptor entry with that default
         * value in the ParameterDescriptorGroup.
         */
        Object defaultValue = descriptor.getDefaultValue();
        if (defaultValue == null && descriptor.getMinimumOccurs() == 0) {
            if (Number.class.isAssignableFrom(valueClass)) {
                defaultValue = 0;
            } else if (valueClass == Boolean.class) {
                defaultValue = Boolean.FALSE;
            }
        }
        if (defaultValue != null) {
            if (defaultValue instanceof Number) {
                // Trim the fractional part if unnecessary (e.g. "0.0" to "0").
                defaultValue = Numbers.finestNumber((Number) defaultValue);
            }
            lines.add(ROW_START + "Default value:" + SEPARATOR + defaultValue + unit + ROW_END);
        }
        lines.add("    </table>");
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
        return "<code>" + codespace + ":</code></td><td><code>" + code + "</code>";
    }

    /**
     * Returns the HTML code for debugging purpose
     */
    @Override
    public String toString() {
        final String lineSeparator = System.lineSeparator();
        final StringBuilder buffer = new StringBuilder();
        for (final String line : lines) {
            buffer.append(line).append(lineSeparator);
        }
        return buffer.toString();
    }
}
