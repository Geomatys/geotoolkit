/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.io.wkt;

import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.HashMap;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.ParsePosition;
import javax.measure.unit.Unit;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.quantity.Quantity;

import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.util.FactoryException;
import org.opengis.util.NoSuchIdentifierException;

// While start import is usually a deprecated practice, we use such a large amount
// of interfaces in those packages that it we choose to exceptionnaly use * here.
import org.opengis.referencing.cs.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;

import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.datum.BursaWolfParameters;
import org.geotoolkit.referencing.cs.AbstractCS;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.geotoolkit.referencing.factory.ReferencingFactoryContainer;
import org.apache.sis.measure.Units;
import org.geotoolkit.resources.Errors;

import static java.util.Collections.singletonMap;
import static javax.measure.unit.SI.METRE;
import static javax.measure.unit.SI.RADIAN;
import static javax.measure.unit.NonSI.DEGREE_ANGLE;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import static org.geotoolkit.util.collection.XCollections.hashMapCapacity;
import static org.geotoolkit.referencing.datum.DefaultGeodeticDatum.WGS84;
import static org.geotoolkit.referencing.datum.DefaultPrimeMeridian.GREENWICH;
import static org.geotoolkit.referencing.datum.DefaultGeodeticDatum.BURSA_WOLF_KEY;
import static org.geotoolkit.referencing.datum.DefaultVerticalDatum.getVerticalDatumTypeFromLegacyCode;


/**
 * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
 * Known Text</cite> (WKT)</A> parser for referencing objects. This include, but is not limited too,
 * {@linkplain CoordinateReferenceSystem Coordinate Reference System} and {@linkplain MathTransform
 * Math Transform} objects. Note that math transforms are part of the WKT {@code "FITTED_CS"} element.
 *
 * {@section Default axis names}
 * The default axis names differ depending on whatever the parsing shall be strictly compliant to
 * the legacy WKT specification, or whatever ISO 19111 identifiers shall be used instead. The
 * following table compares the names:
 *
 * <table border="1">
 * <tr><th>CRS type</th>  <th>WKT defaults</th><th>ISO abbreviations</th></tr>
 * <tr><td>Geographic</td><td>Lon, Lat</td>    <td>&lambda;, &phi;</td></tr>
 * <tr><td>Vertical</td>  <td>H</td>           <td>h</td></tr>
 * <tr><td>Projected</td> <td>X, Y</td>        <td>x, y</td></tr>
 * <tr><td>Geocentric</td><td>X, Y, Z</td>     <td>X, Y, Z</td></tr>
 * </table>
 *
 * <p>The default behavior is to use the legacy WKT identifiers, for compliance with the WKT
 * specification. This behavior can be changed by call to {@link #setISOConform(boolean)}.
 * Note that Geotk referencing factories like
 * {@link org.geotoolkit.referencing.factory.wkt.WKTParsingAuthorityFactory} perform the
 * above-cited {@code setISOConform(true)} method call on their internal parser instance,
 * for ISO compliance.</p>
 *
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @see <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html">Well Know Text specification</A>
 * @see <A HREF="http://home.gdal.org/projects/opengis/wktproblems.html">OGC WKT Coordinate System Issues</A>
 *
 * @since 2.0
 * @level advanced
 * @module
 */
public class ReferencingParser extends MathTransformParser {
    /**
     * {@code true} in order to allows the non-standard Oracle syntax. Oracle puts the Bursa-Wolf
     * parameters straight into the {@code DATUM} elements, without enclosing them in a
     * {@code TOWGS84} element.
     */
    private static final boolean ALLOW_ORACLE_SYNTAX = true;

    /**
     * {@code true} if the authority declared in the {@code AUTHORITY[<authority>, <code>]} element
     * should be assigned to the name. A previous Geotk version assigned the authority to the name.
     * However experience show that it is often wrong in practice, since peoples declare EPSG codes
     * but still use WKT name much shorter than the EPSG name (for example "<cite>WGS84</cite>"
     * instead than "<cite>World Geodetic System 1984</cite>"). Even our own Geotk implementation
     * make such substitution through the {@link Formatter#getName(IdentifiedObject)} method.
     *
     * @since 3.19
     */
    private static final boolean ASSIGN_AUTHORITY_TO_NAME = false;

    /**
     * The factory to use for creating {@linkplain Datum datum}.
     */
    private final DatumFactory datumFactory;

    /**
     * The factory to use for creating {@linkplain CoordinateSystem coordinate systems}.
     */
    private final CSFactory csFactory;

    /**
     * The factory to use for creating {@linkplain CoordinateReferenceSystem
     * coordinate reference systems}.
     */
    private final CRSFactory crsFactory;

    /**
     * If non-null, forces {@code PRIMEM} and {@code PARAMETER} angular units to the given value
     * instead than inferring it from the context. This field is occasionally set to
     * {@code NonSI.DEGREE_ANGLE} for compatibility with ESRI softwares.
     * <p>
     * Note that this value does not apply to {@code AXIS} elements.
     *
     * @since 3.20
     */
    private Unit<Angle> forcedAngularUnit;

    /**
     * {@code true} if ISO axis identifiers should be used instead than the one defined
     * by the WKT specification.
     *
     * @since 3.18
     */
    private boolean isoConform;

    /**
     * {@code true} if {@code AXIS[...]} elements should be ignored. This is sometime used
     * for simulating a "force longitude first axis order" behavior. It is also used for
     * compatibility with ESRI softwares which ignore axis elements.
     */
    private boolean axisIgnored;

    /**
     * The list of {@linkplain AxisDirection axis directions} from their name.
     * Instantiated at construction time and never modified after that point.
     */
    private final Map<String,AxisDirection> directions;

    /**
     * The value of the last {@linkplain #directions} map created.
     * We keep this reference only on the assumption that the same
     * map will often be reused.
     */
    private static Map<String,AxisDirection> lastDirections;

    /**
     * Creates a parser using the default set of symbols and factories.
     */
    public ReferencingParser() {
        this(Symbols.DEFAULT, (Hints) null);
    }

    /**
     * Creates a parser using the specified set of symbols.
     * Default factories are fetching according the given hints.
     *
     * @param symbols The symbols for parsing and formatting numbers.
     * @param hints   The hints to be used for fetching the factories, or
     *                {@code null} for the system-wide default hints.
     */
    public ReferencingParser(final Symbols symbols, final Hints hints) {
        this(symbols,
             FactoryFinder.getDatumFactory(hints),
             FactoryFinder.getCSFactory(hints),
             FactoryFinder.getCRSFactory(hints),
             FactoryFinder.getMathTransformFactory(hints));
    }

    /**
     * Constructs a parser for the specified set of symbols using the specified set of factories.
     *
     * @param symbols   The symbols for parsing and formatting numbers.
     * @param factories The factories to use.
     */
    public ReferencingParser(final Symbols symbols, final ReferencingFactoryContainer factories) {
        this(symbols,
             factories.getDatumFactory(),
             factories.getCSFactory(),
             factories.getCRSFactory(),
             factories.getMathTransformFactory());
    }

    /**
     * Constructs a parser for the specified set of symbols using the specified set of factories.
     *
     * @param symbols      The symbols for parsing and formatting numbers.
     * @param datumFactory The factory to use for creating {@linkplain Datum datum}.
     * @param csFactory    The factory to use for creating {@linkplain CoordinateSystem coordinate systems}.
     * @param crsFactory   The factory to use for creating {@linkplain CoordinateReferenceSystem coordinate reference systems}.
     * @param mtFactory    The factory to use for creating {@linkplain MathTransform math transform} objects.
     */
    public ReferencingParser(final Symbols symbols,
                             final DatumFactory datumFactory,
                             final CSFactory csFactory,
                             final CRSFactory crsFactory,
                             final MathTransformFactory mtFactory)
    {
        super(symbols, mtFactory);
        this.datumFactory = datumFactory;
        this.csFactory    = csFactory;
        this.crsFactory   = crsFactory;
        ensureNonNull("datumFactory", datumFactory);
        ensureNonNull("csFactory",    csFactory);
        ensureNonNull("crsFactory",   crsFactory);
        /*
         * Gets the map of axis directions.
         */
        final AxisDirection[] values = AxisDirection.values();
        Map<String,AxisDirection> directions = new HashMap<>(hashMapCapacity(values.length));
        final Locale locale = symbols.locale;
        for (int i=0; i<values.length; i++) {
            directions.put(values[i].name().trim().toUpperCase(locale), values[i]);
        }
        /*
         * Replaces by the last generated map if it is the same.
         */
        synchronized (ReferencingParser.class) {
            final Map<String,AxisDirection> existing = lastDirections;
            if (directions.equals(existing)) {
                directions = existing;
            } else {
                lastDirections = directions;
            }
        }
        this.directions = directions;
    }

    /**
     * If non-null, forces {@code PRIMEM} and {@code PARAMETER} angular units to the returned
     * value instead than inferring it from the context. The default value is {@code null},
     * which mean that the angular units are inferred from the context as required by the
     * <a href="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html#PRIMEM">WKT specification</a>.
     *
     * @return The angular unit, or {@code null} for inferring it from the context.
     *
     * @since 3.20
     */
    public Unit<Angle> getForcedAngularUnit() {
        return forcedAngularUnit;
    }

    /**
     * If non-null, forces {@code PRIMEM} and {@code PARAMETER} angular units to the given
     * value instead than inferring it from the context. This property is occasionally set
     * to {@link javax.measure.unit.NonSI#DEGREE_ANGLE} for compatibility with ESRI and GDAL
     * softwares. Note that this value does not apply to {@code AXIS} elements.
     *
     * @param angularUnit The new angular unit, or {@code null} for restoring the default behavior.
     *
     * @see Convention#ESRI
     * @see Convention#PROJ4
     *
     * @since 3.20
     */
    public void setForcedAngularUnit(final Unit<Angle> angularUnit) {
        forcedAngularUnit = angularUnit;
    }

    /**
     * Returns {@code true} if the default names of {@code AXIS[...]} elements shall be ISO 19111
     * identifiers. The default value is {@code false}, which mean that the identifiers specified
     * by the WKT specification are used.
     *
     * @return {@code true} if the default identifiers of {@code AXIS[...]} elements shall be
     *         conform to ISO 19111.
     *
     * @since 3.18
     */
    public boolean isISOConform() {
        return isoConform;
    }

    /**
     * Sets whatever the default names of {@code AXIS[...]} elements shall be ISO identifiers.
     *
     * @param conform {@code true} if the default identifiers of {@code AXIS[...]} elements shall
     *        be conform to ISO 19111.
     *
     * @since 3.18
     */
    public void setISOConform(final boolean conform) {
        isoConform = conform;
    }

    /**
     * Returns {@code true} if {@code AXIS[...]} elements will be ignored during parsing.
     * The default value is {@code false}.
     *
     * @return {@code true} if {@code AXIS[...]} elements will be ignored during parsing.
     *
     * @since 3.00
     */
    public boolean isAxisIgnored() {
        return axisIgnored;
    }

    /**
     * Sets whatever {@code AXIS[...]} elements will be ignored during parsing. The default
     * value is {@code false} as we would expect from a WKT compliant parser. However this
     * flag may occasionally be set to {@code true} for compatibility with ESRI softwares,
     * which ignore {@code AXIS} elements. It may also be used as a way to force the longitude
     * axis to be first.
     * <p>
     * Note that {@code AXIS} elements still need to be well formed even when this flag is set
     * to {@code true}; invalid axis will continue to cause a {@link ParseException} despite
     * their content being ignored.
     *
     * @param ignored {@code true} if {@code AXIS[...]} elements should be ignored during parsing.
     *
     * @since 3.00
     */
    public void setAxisIgnored(final boolean ignored) {
        axisIgnored = ignored;
    }

    /**
     * Parses a coordinate reference system element.
     *
     * @param  text The text to be parsed.
     * @return The coordinate reference system.
     * @throws ParseException if the string can't be parsed.
     */
    public final CoordinateReferenceSystem parseCoordinateReferenceSystem(final String text)
            throws ParseException
    {
        final Element element = getTree(text, new ParsePosition(0));
        final CoordinateReferenceSystem crs = parseCoordinateReferenceSystem(element);
        element.close();
        return crs;
    }

    /**
     * Parses a coordinate reference system element.
     *
     * @param  parent The parent element.
     * @return The next element as a {@link CoordinateReferenceSystem} object.
     * @throws ParseException if the next element can't be parsed.
     */
    private CoordinateReferenceSystem parseCoordinateReferenceSystem(final Element element)
            throws ParseException
    {
        final Object key = element.peek();
        if (key instanceof Element) {
            final String keyword = keyword((Element) key);
            switch (keyword) {
                /*
                 * Note: the following cases are copied in the parseObject(Element) method in
                 * order to take advantage of a single switch statement. If new cases are added
                 * here, then they must be added in parseObject(Element) as well.
                 */
                case "GEOGCS":    return parseGeoGCS  (element);
                case "PROJCS":    return parseProjCS  (element);
                case "GEOCCS":    return parseGeoCCS  (element);
                case "VERT_CS":   return parseVertCS  (element);
                case "LOCAL_CS":  return parseLocalCS (element);
                case "COMPD_CS":  return parseCompdCS (element);
                case "FITTED_CS": return parseFittedCS(element);
            }
        }
        throw element.parseFailed(null, Errors.format(Errors.Keys.UNKNOWN_TYPE_$1, key));
    }

    /**
     * Parses the next element in the specified <cite>Well Know Text</cite> (WKT) tree.
     *
     * @param  element The element to be parsed.
     * @return The object.
     * @throws ParseException if the element can't be parsed.
     */
    @Override
    Object parse(final Element element) throws ParseException {
        final Object key = element.peek();
        if (key instanceof Element) {
            final String keyword = keyword((Element) key);
            switch (keyword) {
                case "AXIS":        return parseAxis      (element, METRE, true);
                case "PRIMEM":      return parsePrimem    (element, DEGREE_ANGLE);
                case "TOWGS84":     return parseToWGS84   (element);
                case "SPHEROID":    return parseSpheroid  (element);
                case "VERT_DATUM":  return parseVertDatum (element);
                case "LOCAL_DATUM": return parseLocalDatum(element);
                case "DATUM":       return parseDatum     (element, GREENWICH);
                /*
                 * Note: the following cases are copied from parseCoordinateReferenceSystem(Element)
                 * method in order to take advantage of a single switch statement. If new cases are
                 * added here, then they must be added in the above method first.
                 */
                case "GEOGCS":    return parseGeoGCS  (element);
                case "PROJCS":    return parseProjCS  (element);
                case "GEOCCS":    return parseGeoCCS  (element);
                case "VERT_CS":   return parseVertCS  (element);
                case "LOCAL_CS":  return parseLocalCS (element);
                case "COMPD_CS":  return parseCompdCS (element);
                case "FITTED_CS": return parseFittedCS(element);
                /*
                 * Note: the following cases are copied from MathTransformParser in order to take
                 * advantage of a single switch statement. If new cases are added there, then the
                 * superclass must be updated first.
                 */
                case "PARAM_MT":       return parseParamMT      (element);
                case "CONCAT_MT":      return parseConcatMT     (element);
                case "INVERSE_MT":     return parseInverseMT    (element);
                case "PASSTHROUGH_MT": return parsePassThroughMT(element);
            }
        }
        throw element.parseFailed(null, Errors.format(Errors.Keys.UNKNOWN_TYPE_$1, key));
    }

    /**
     * Returns the properties to be given to the parsed object. This method is invoked
     * automatically by the parser for the root element only. This method expect on input
     * the properties parsed from the {@code AUTHORITY} element, and returns on output the
     * properties to give to the object to be created. The default implementation returns
     * the {@code properties} map unchanged. Subclasses may override this method in order
     * to add or change properties.
     * <p>
     * <b>Example:</b> if a subclass want to add automatically an authority code when no
     * {@code AUTHORITY} element was explicitly set in the WKT, then it may test for the
     * {@link IdentifiedObject#IDENTIFIERS_KEY} key and add automatically an entry if this
     * key was missing.
     *
     * @param  properties The properties parsed from the WKT file. Entries can be added, removed
     *         or modified directly in this map.
     * @return The properties to be given to the parsed object. This is usually {@code properties}
     *         (maybe after modifications), but could also be a new map.
     *
     * @since 2.3
     */
    protected Map<String,Object> alterProperties(final Map<String,Object> properties) {
        return properties;
    }

    /**
     * Parses an <strong>optional</strong> {@code "AUTHORITY"} element.
     * This element has the following pattern:
     *
     * {@preformat text
     *     AUTHORITY["<name>", "<code>"]
     * }
     *
     * @param  parent The parent element.
     * @param  name The name of the parent object being parsed.
     * @return A properties map with the parent name and the optional authority code.
     * @throws ParseException if the {@code "AUTHORITY"} can't be parsed.
     */
    private Map<String,Object> parseAuthority(final Element parent, final String name)
            throws ParseException
    {
        final boolean  isRoot = parent.isRoot();
        final Element element = parent.pullOptionalElement("AUTHORITY");
        if (element == null && !isRoot) {
            return singletonMap(IdentifiedObject.NAME_KEY, (Object) name);
        }
        Map<String,Object> properties = new HashMap<>(4);
        properties.put(IdentifiedObject.NAME_KEY, name);
        if (element != null) {
            final String auth = element.pullString("name");
            final String code = element.pullObject("code").toString(); // Accepts Integer as well as String.
            element.close();
            final Citation authority = Citations.fromName(auth);
            if (ASSIGN_AUTHORITY_TO_NAME) {
                properties.put(IdentifiedObject.NAME_KEY, new NamedIdentifier(authority, name));
            }
            properties.put(IdentifiedObject.IDENTIFIERS_KEY, new NamedIdentifier(authority, code));
        }
        if (isRoot) {
            properties = alterProperties(properties);
        }
        return properties;
    }

    /**
     * Parses a {@code "UNIT"} element.
     * This element has the following pattern:
     *
     * {@preformat text
     *     UNIT["<name>", <conversion factor> {,<authority>}]
     * }
     *
     * @param  parent The parent element.
     * @param  unit The contextual unit. Usually {@link javax.measure.unit.SI#METRE} or
     *         {@link javax.measure.unit.SI#RADIAN}.
     * @return The {@code "UNIT"} element as an {@link Unit} object.
     * @throws ParseException if the {@code "UNIT"} can't be parsed.
     *
     * @todo Authority code is currently ignored. We may consider to create a subclass of
     *       {@link Unit} which implements {@link IdentifiedObject} in a future version.
     */
    private <T extends Quantity> Unit<T> parseUnit(final Element parent, final Unit<T> unit)
            throws ParseException
    {
        final Element element = parent.pullElement("UNIT");
        final String     name = element.pullString("name");
        final double   factor = element.pullDouble("factor");
        final Map<String,?> properties = parseAuthority(element, name); // NOSONAR: Ignored for now.
        element.close();
        return Units.multiply(unit, factor);
    }

    /**
     * Parses an {@code "AXIS"} element.
     * This element has the following pattern:
     *
     * {@preformat text
     *     AXIS["<name>", NORTH | SOUTH | EAST | WEST | UP | DOWN | OTHER]
     * }
     *
     * {@note There is no AUTHORITY element for AXIS element in OGC specification. However, we
     *        accept it anyway in order to make the parser more tolerant to non-100% compliant
     *        WKT. Note that AXIS is really the only element without such AUTHORITY clause and
     *        the EPSG database provides authority code for all axis.}
     *
     * @param  parent The parent element.
     * @param  unit The contextual unit. Usually {@link javax.measure.unit.NonSI#DEGREE_ANGLE}
     *         or {@link javax.measure.unit.SI#METRE}.
     * @param  required {@code true} if the axis is mandatory,
     *         or {@code false} if it is optional.
     * @return The {@code "AXIS"} element as a {@link CoordinateSystemAxis} object, or {@code null}
     *         if the axis was not required and there is no axis object.
     * @throws ParseException if the {@code "AXIS"} element can't be parsed.
     */
    private CoordinateSystemAxis parseAxis(final Element parent,
                                           final Unit<?> unit,
                                           final boolean required)
            throws ParseException
    {
        final Element element;
        if (required) {
            element = parent.pullElement("AXIS");
        } else {
            element = parent.pullOptionalElement("AXIS");
            if (element == null) {
                return null;
            }
        }
        final String name = element.pullString("name");
        final Element orientation = element.pullVoidElement("orientation");
        final Map<String,?> properties = parseAuthority(element, name); // See javadoc
        element.close();
        final AxisDirection direction = directions.get(keyword(orientation));
        if (direction == null) {
            throw element.parseFailed(null, Errors.format(Errors.Keys.UNKNOWN_TYPE_$1, orientation));
        }
        try {
            return createAxis(properties, name, direction, unit);
        } catch (FactoryException exception) {
            throw element.parseFailed(exception, null);
        }
    }

    /**
     * Creates an axis. If the name matches one of pre-defined axis, the pre-defined one
     * will be returned. This replacement help to get more success when comparing a CS
     * built from WKT against a CS built from one of Geotk constants.
     *
     * @param  properties Name and other properties to give to the new object.
     *         If {@code null}, the abbreviation will be used as the axis name.
     * @param  abbreviation The coordinate axis abbreviation.
     * @param  direction The axis direction.
     * @param  unit The coordinate axis unit.
     * @throws FactoryException if the axis can't be created.
     */
    private CoordinateSystemAxis createAxis(Map<String,?> properties,
                                      final String        abbreviation,
                                      final AxisDirection direction,
                                      final Unit<?>       unit)
            throws FactoryException
    {
        final CoordinateSystemAxis candidate =
                DefaultCoordinateSystemAxis.getPredefined(abbreviation, direction);
        if (candidate != null && unit.equals(candidate.getUnit())) {
            return candidate;
        }
        if (properties == null) {
            properties = singletonMap(IdentifiedObject.NAME_KEY, abbreviation);
        }
        return csFactory.createCoordinateSystemAxis(properties, abbreviation, direction, unit);
    }

    /**
     * Parses a {@code "PRIMEM"} element. This element has the following pattern:
     *
     * {@preformat text
     *     PRIMEM["<name>", <longitude> {,<authority>}]
     * }
     *
     * @param  parent The parent element.
     * @param  angularUnit The contextual unit.
     * @return The {@code "PRIMEM"} element as a {@link PrimeMeridian} object.
     * @throws ParseException if the {@code "PRIMEM"} element can't be parsed.
     */
    private PrimeMeridian parsePrimem(final Element parent, Unit<Angle> angularUnit)
            throws ParseException
    {
        if (forcedAngularUnit != null) {
            angularUnit = forcedAngularUnit;
        }
        final Element   element = parent.pullElement("PRIMEM");
        final String       name = element.pullString("name");
        final double  longitude = element.pullDouble("longitude");
        final Map<String,?> properties = parseAuthority(element, name);
        element.close();
        try {
            return datumFactory.createPrimeMeridian(properties, longitude, angularUnit);
        } catch (FactoryException exception) {
            throw element.parseFailed(exception, null);
        }
    }

    /**
     * Parses an <strong>optional</strong> {@code "TOWGS84"} element.
     * This element has the following pattern:
     *
     * {@preformat text
     *     TOWGS84[<dx>, <dy>, <dz>, <ex>, <ey>, <ez>, <ppm>]
     * }
     *
     * @param  parent The parent element.
     * @return The {@code "TOWGS84"} element as a {@link BursaWolfParameters} object,
     *         or {@code null} if no {@code "TOWGS84"} has been found.
     * @throws ParseException if the {@code "TOWGS84"} can't be parsed.
     */
    private static BursaWolfParameters parseToWGS84(final Element parent)
            throws ParseException
    {
        final Element element = parent.pullOptionalElement("TOWGS84");
        if (element == null) {
            return null;
        }
        final BursaWolfParameters info = new BursaWolfParameters(WGS84);
        info.dx = element.pullDouble("dx");
        info.dy = element.pullDouble("dy");
        info.dz = element.pullDouble("dz");
        if (element.peek() != null) {
            info.ex  = element.pullDouble("ex");
            info.ey  = element.pullDouble("ey");
            info.ez  = element.pullDouble("ez");
            info.ppm = element.pullDouble("ppm");
        }
        element.close();
        return info;
    }

    /**
     * Parses a {@code "SPHEROID"} element. This element has the following pattern:
     *
     * {@preformat text
     *     SPHEROID["<name>", <semi-major axis>, <inverse flattening> {,<authority>}]
     * }
     *
     * @param  parent The parent element.
     * @return The {@code "SPHEROID"} element as an {@link Ellipsoid} object.
     * @throws ParseException if the {@code "SPHEROID"} element can't be parsed.
     */
    private Ellipsoid parseSpheroid(final Element parent) throws ParseException {
        Element          element = parent.pullElement("SPHEROID");
        String              name = element.pullString("name");
        double     semiMajorAxis = element.pullDouble("semiMajorAxis");
        double inverseFlattening = element.pullDouble("inverseFlattening");
        Map<String,?> properties = parseAuthority(element, name);
        element.close();
        if (inverseFlattening == 0) {
            // Inverse flattening null is an OGC convention for a sphere.
            inverseFlattening = Double.POSITIVE_INFINITY;
        }
        try {
            return datumFactory.createFlattenedSphere(properties,
                    semiMajorAxis, inverseFlattening, METRE);
        } catch (FactoryException exception) {
            throw element.parseFailed(exception, null);
        }
    }

    /**
     * Parses a {@code "PROJECTION"} element. This element has the following pattern:
     *
     * {@preformat text
     *     PROJECTION["<name>" {,<authority>}]
     * }
     *
     * @param  parent The parent element.
     * @param  ellipsoid The ellipsoid, or {@code null} if none.
     * @param  linearUnit The linear unit of the parent {@code PROJCS} element, or {@code null}.
     * @param  angularUnit The angular unit of the parent {@code GEOCS} element, or {@code null}.
     * @return The {@code "PROJECTION"} element as a {@link ParameterValueGroup} object.
     * @throws ParseException if the {@code "PROJECTION"} element can't be parsed.
     */
    private ParameterValueGroup parseProjection(final Element      parent,
                                                final Ellipsoid    ellipsoid,
                                                final Unit<Length> linearUnit,
                                                final Unit<Angle>  angularUnit)
            throws ParseException
    {
        final Element          element = parent.pullElement("PROJECTION");
        final String    classification = element.pullString("name");
        final Map<String,?> properties = parseAuthority(element, classification); // NOSONAR: Ignored for now.
        element.close();
        /*
         * Set the list of parameters.  NOTE: Parameters are defined in
         * the parent Element (usually a "PROJCS" element), not in this
         * "PROJECTION" element.
         *
         * We will set the semi-major and semi-minor parameters from the
         * ellipsoid first. If those values were explicitly specified in
         * a "PARAMETER" statement, they will overwrite the values inferred
         * from the ellipsoid.
         */
        final ParameterValueGroup parameters;
        try {
            parameters = mtFactory.getDefaultParameters(classification);
        } catch (NoSuchIdentifierException exception) {
            throw element.parseFailed(exception, null);
        }
        Element param = parent;
        try {
            if (ellipsoid != null) {
                final Unit<Length> axisUnit = ellipsoid.getAxisUnit();
                parameters.parameter("semi_major").setValue(ellipsoid.getSemiMajorAxis(), axisUnit);
                parameters.parameter("semi_minor").setValue(ellipsoid.getSemiMinorAxis(), axisUnit);
            }
            while ((param = parent.pullOptionalElement("PARAMETER")) != null) {
                final String paramName = param.pullString("name");
                final ParameterValue<?> parameter = parameters.parameter(paramName);
                final ParameterDescriptor<?> descriptor = parameter.getDescriptor();
                final Class<?> valueClass = descriptor.getValueClass();
                if (valueClass == String.class) {
                    parameter.setValue(param.pullString("value"));
                } else if (valueClass == Boolean.class) {
                    parameter.setValue(param.pullBoolean("value"));
                } else {
                    /*
                     * Usually, projection parameters contain only double values.  The above
                     * check for other types was done as a safety, but having those types in
                     * a PROJECTION[...] element is unusual. Consequently we make the double
                     * type the default for all unknown types.
                     */
                    final double paramValue = param.pullDouble("value");
                    final Unit<?> expected = descriptor.getUnit();
                    Unit<?> unit = null;
                    if (expected != null && !Unit.ONE.equals(expected)) {
                        if (linearUnit != null && METRE.isCompatible(expected)) {
                            unit = linearUnit;
                        } else if (angularUnit != null && RADIAN.isCompatible(expected)) {
                            unit = angularUnit;
                        }
                    }
                    if (unit != null) {
                        parameter.setValue(paramValue, unit);
                    } else {
                        parameter.setValue(paramValue);
                    }
                }
                param.close();
            }
        } catch (ParameterNotFoundException exception) {
            throw param.parseFailed(exception, Errors.format(
                    Errors.Keys.UNEXPECTED_PARAMETER_$1, exception.getParameterName()));
        }
        return parameters;
    }

    /**
     * Parses a {@code "DATUM"} element. This element has the following pattern:
     *
     * {@preformat text
     *     DATUM["<name>", <spheroid> {,<to wgs84>} {,<authority>}]
     * }
     *
     * @param  parent The parent element.
     * @param  meridian the prime meridian.
     * @return The {@code "DATUM"} element as a {@link GeodeticDatum} object.
     * @throws ParseException if the {@code "DATUM"} element can't be parsed.
     */
    private GeodeticDatum parseDatum(final Element parent,
                                     final PrimeMeridian meridian)
            throws ParseException
    {
        Element             element    = parent.pullElement("DATUM");
        String              name       = element.pullString("name");
        Ellipsoid           ellipsoid  = parseSpheroid(element);
        BursaWolfParameters toWGS84    = parseToWGS84(element); // Optional; may be null.
        Map<String,Object>  properties = parseAuthority(element, name);
        if (ALLOW_ORACLE_SYNTAX && (toWGS84 == null) && (element.peek() instanceof Number)) {
            toWGS84     = new BursaWolfParameters(WGS84);
            toWGS84.dx  = element.pullDouble("dx");
            toWGS84.dy  = element.pullDouble("dy");
            toWGS84.dz  = element.pullDouble("dz");
            toWGS84.ex  = element.pullDouble("ex");
            toWGS84.ey  = element.pullDouble("ey");
            toWGS84.ez  = element.pullDouble("ez");
            toWGS84.ppm = element.pullDouble("ppm");
        }
        element.close();
        if (toWGS84 != null) {
            if (!(properties instanceof HashMap<?,?>)) {
                properties = new HashMap<>(properties);
            }
            properties.put(BURSA_WOLF_KEY, toWGS84);
        }
        try {
            return datumFactory.createGeodeticDatum(properties, ellipsoid, meridian);
        } catch (FactoryException exception) {
            throw element.parseFailed(exception, null);
        }
    }

    /**
     * Parses a {@code "VERT_DATUM"} element. This element has the following pattern:
     *
     * {@preformat text
     *     VERT_DATUM["<name>", <datum type> {,<authority>}]
     * }
     *
     * @param  parent The parent element.
     * @return The {@code "VERT_DATUM"} element as a {@link VerticalDatum} object.
     * @throws ParseException if the {@code "VERT_DATUM"} element can't be parsed.
     */
    private VerticalDatum parseVertDatum(final Element parent) throws ParseException {
        final Element element = parent.pullElement("VERT_DATUM");
        final String     name = element.pullString ("name");
        final int       datum = element.pullInteger("datum");
        final Map<String,?> properties = parseAuthority(element, name);
        element.close();
        final VerticalDatumType type = getVerticalDatumTypeFromLegacyCode(datum);
        if (type == null) {
            throw element.parseFailed(null, Errors.format(Errors.Keys.UNKNOWN_TYPE_$1, datum));
        }
        try {
            return datumFactory.createVerticalDatum(properties, type);
        } catch (FactoryException exception) {
            throw element.parseFailed(exception, null);
        }
    }

    /**
     * Parses a {@code "LOCAL_DATUM"} element. This element has the following pattern:
     *
     * {@preformat text
     *     LOCAL_DATUM["<name>", <datum type> {,<authority>}]
     * }
     *
     * @param  parent The parent element.
     * @return The {@code "LOCAL_DATUM"} element as an {@link EngineeringDatum} object.
     * @throws ParseException if the {@code "LOCAL_DATUM"} element can't be parsed.
     *
     * @todo The vertical datum type is currently ignored.
     */
    private EngineeringDatum parseLocalDatum(final Element parent) throws ParseException {
        final Element element = parent.pullElement("LOCAL_DATUM");
        final String     name = element.pullString ("name");
        final int       datum = element.pullInteger("datum"); // NOSONAR: Ignored for now.
        final Map<String,?> properties = parseAuthority(element, name);
        element.close();
        try {
            return datumFactory.createEngineeringDatum(properties);
        } catch (FactoryException exception) {
            throw element.parseFailed(exception, null);
        }
    }

    /**
     * Parses a {@code "LOCAL_CS"} element.
     * This element has the following pattern:
     *
     * {@preformat text
     *     LOCAL_CS["<name>", <local datum>, <unit>, <axis>, {,<axis>}* {,<authority>}]
     * }
     *
     * @param  parent The parent element.
     * @return The {@code "LOCAL_CS"} element as an {@link EngineeringCRS} object.
     * @throws ParseException if the {@code "LOCAL_CS"} element can't be parsed.
     *
     * @todo The coordinate system used is always a Geotk implementation, since we don't
     *       know which method to invokes in the {@link CSFactory} (is it a Cartesian
     *       coordinate system? a spherical one? etc.).
     */
    private EngineeringCRS parseLocalCS(final Element parent) throws ParseException {
        Element           element = parent.pullElement("LOCAL_CS");
        String               name = element.pullString("name");
        EngineeringDatum    datum = parseLocalDatum(element);
        Unit<Length>   linearUnit = parseUnit(element, METRE);
        CoordinateSystemAxis axis = parseAxis(element, linearUnit, true);
        List<CoordinateSystemAxis> list = new ArrayList<>();
        do {
            list.add(axis);
            axis = parseAxis(element, linearUnit, false);
        } while (axis != null);
        final Map<String,?> properties = parseAuthority(element, name);
        element.close();
        final CoordinateSystem cs;
        cs = new AbstractCS(singletonMap("name", name),
                list.toArray(new CoordinateSystemAxis[list.size()]));
        try {
            return crsFactory.createEngineeringCRS(properties, datum, cs);
        } catch (FactoryException exception) {
            throw element.parseFailed(exception, null);
        }
    }

    /**
     * Parses a {@code "GEOCCS"} element.
     * This element has the following pattern:
     *
     * {@preformat text
     *     GEOCCS["<name>", <datum>, <prime meridian>, <linear unit>
     *            {,<axis> ,<axis> ,<axis>} {,<authority>}]
     * }
     *
     * @param  parent The parent element.
     * @return The {@code "GEOCCS"} element as a {@link GeocentricCRS} object.
     * @throws ParseException if the {@code "GEOCCS"} element can't be parsed.
     */
    private GeocentricCRS parseGeoCCS(final Element parent) throws ParseException {
        final Element          element = parent.pullElement("GEOCCS");
        final String              name = element.pullString("name");
        final Map<String,?> properties = parseAuthority(element, name);
        final PrimeMeridian   meridian = parsePrimem   (element, DEGREE_ANGLE);
        final GeodeticDatum      datum = parseDatum    (element, meridian);
        final Unit<Length>  linearUnit = parseUnit     (element, METRE);
        CoordinateSystemAxis axis0, axis1 = null, axis2 = null;
        axis0 = parseAxis(element, linearUnit, false);
        try {
            if (axis0 != null) {
                axis1 = parseAxis(element, linearUnit, true);
                axis2 = parseAxis(element, linearUnit, true);
            }
            if (axis0 == null || axisIgnored) {
                // Those default values are part of WKT specification.
                axis0 = createAxis(null, "X", AxisDirection.OTHER, linearUnit);
                axis1 = createAxis(null, "Y", AxisDirection.EAST,  linearUnit);
                axis2 = createAxis(null, "Z", AxisDirection.NORTH, linearUnit);
            }
            element.close();
            CartesianCS cs = csFactory.createCartesianCS(properties, axis0, axis1, axis2);
            cs = Convention.replace(cs, false);
            return crsFactory.createGeocentricCRS(properties, datum, cs);
        } catch (FactoryException exception) {
            throw element.parseFailed(exception, null);
        }
    }

    /**
     * Parses an <strong>optional</strong> {@code "VERT_CS"} element.
     * This element has the following pattern:
     *
     * {@preformat text
     *     VERT_CS["<name>", <vert datum>, <linear unit>, {<axis>,} {,<authority>}]
     * }
     *
     * @param  parent The parent element.
     * @return The {@code "VERT_CS"} element as a {@link VerticalCRS} object.
     * @throws ParseException if the {@code "VERT_CS"} element can't be parsed.
     */
    private VerticalCRS parseVertCS(final Element parent) throws ParseException {
        final Element element = parent.pullElement("VERT_CS");
        if (element == null) {
            return null;
        }
        String               name = element.pullString("name");
        VerticalDatum       datum = parseVertDatum(element);
        Unit<Length>   linearUnit = parseUnit(element, METRE);
        CoordinateSystemAxis axis = parseAxis(element, linearUnit, false);
        Map<String,?>  properties = parseAuthority(element, name);
        element.close();
        try {
            if (axis == null || axisIgnored) {
                axis = createAxis(null, isoConform ? "h" : "H", AxisDirection.UP, linearUnit);
            }
            return crsFactory.createVerticalCRS(properties, datum,
                    csFactory.createVerticalCS(singletonMap("name", name), axis));
        } catch (FactoryException exception) {
            throw element.parseFailed(exception, null);
        }
    }

    /**
     * Parses a {@code "GEOGCS"} element. This element has the following pattern:
     *
     * {@preformat text
     *     GEOGCS["<name>", <datum>, <prime meridian>, <angular unit>  {,<twin axes>} {,<authority>}]
     * }
     *
     * @param  parent The parent element.
     * @return The {@code "GEOGCS"} element as a {@link GeographicCRS} object.
     * @throws ParseException if the {@code "GEOGCS"} element can't be parsed.
     */
    private GeographicCRS parseGeoGCS(final Element parent) throws ParseException {
        Element            element = parent.pullElement("GEOGCS");
        String                name = element.pullString("name");
        Map<String,?>   properties = parseAuthority(element, name);
        Unit<Angle>    angularUnit = parseUnit     (element, RADIAN);
        PrimeMeridian     meridian = parsePrimem   (element, angularUnit);
        GeodeticDatum        datum = parseDatum    (element, meridian);
        CoordinateSystemAxis axis0 = parseAxis     (element, angularUnit, false);
        CoordinateSystemAxis axis1 = null;
        try {
            if (axis0 != null) {
                axis1 = parseAxis(element, angularUnit, true);
            }
            if (axis0 == null || axisIgnored) {
                // The (Lon,Lat) default values are part of WKT specification.
                axis0 = createAxis(null, isoConform ? "\u03BB" : "Lon", AxisDirection.EAST,  angularUnit);
                axis1 = createAxis(null, isoConform ? "\u03C6" : "Lat", AxisDirection.NORTH, angularUnit);
            }
            element.close();
            return crsFactory.createGeographicCRS(properties, datum,
                    csFactory.createEllipsoidalCS(properties, axis0, axis1));
        } catch (FactoryException exception) {
            throw element.parseFailed(exception, null);
        }
    }

    /**
     * Parses a {@code "PROJCS"} element.
     * This element has the following pattern:
     *
     * {@preformat text
     *     PROJCS["<name>", <geographic cs>, <projection>, {<parameter>,}*,
     *            <linear unit> {,<twin axes>}{,<authority>}]
     * }
     *
     * @param  parent The parent element.
     * @return The {@code "PROJCS"} element as a {@link ProjectedCRS} object.
     * @throws ParseException if the {@code "GEOGCS"} element can't be parsed.
     */
    private ProjectedCRS parseProjCS(final Element parent) throws ParseException {
        Element                element = parent.pullElement("PROJCS");
        String                    name = element.pullString("name");
        Map<String,?>       properties = parseAuthority(element, name);
        GeographicCRS           geoCRS = parseGeoGCS(element);
        Ellipsoid            ellipsoid = geoCRS.getDatum().getEllipsoid();
        Unit<Length>        linearUnit = parseUnit(element, METRE);
        ParameterValueGroup projection = parseProjection(element, ellipsoid, linearUnit,
                (forcedAngularUnit != null) ? forcedAngularUnit :
                geoCRS.getCoordinateSystem().getAxis(0).getUnit().asType(Angle.class));
        CoordinateSystemAxis axis0 = parseAxis(element, linearUnit, false);
        CoordinateSystemAxis axis1 = null;
        try {
            if (axis0 != null) {
                axis1 = parseAxis(element, linearUnit, true);
            }
            if (axis0 == null || axisIgnored) {
                // The (X,Y) default values are part of WKT specification.
                axis0 = createAxis(null, isoConform ? "x" : "X", AxisDirection.EAST,  linearUnit);
                axis1 = createAxis(null, isoConform ? "y" : "Y", AxisDirection.NORTH, linearUnit);
            }
            element.close();
            final Conversion conversion = new DefiningConversion(name, projection);
            return crsFactory.createProjectedCRS(properties, geoCRS, conversion,
                    csFactory.createCartesianCS(properties, axis0, axis1));
        } catch (FactoryException exception) {
            throw element.parseFailed(exception, null);
        }
    }

    /**
     * Parses a {@code "COMPD_CS"} element.
     * This element has the following pattern:
     *
     * {@preformat text
     *     COMPD_CS["<name>", <head cs>, <tail cs> {,<authority>}]
     * }
     *
     * @param  parent The parent element.
     * @return The {@code "COMPD_CS"} element as a {@link CompoundCRS} object.
     * @throws ParseException if the {@code "COMPD_CS"} element can't be parsed.
     */
    private CompoundCRS parseCompdCS(final Element parent) throws ParseException {
        final CoordinateReferenceSystem[] CRS = new CoordinateReferenceSystem[2];
        Element       element    = parent.pullElement("COMPD_CS");
        String        name       = element.pullString("name");
        Map<String,?> properties = parseAuthority(element, name);
        CRS[0] = parseCoordinateReferenceSystem(element);
        CRS[1] = parseCoordinateReferenceSystem(element);
        element.close();
        try {
            return crsFactory.createCompoundCRS(properties, CRS);
        } catch (FactoryException exception) {
            throw element.parseFailed(exception, null);
        }
    }

    /**
     * Parses a {@code "FITTED_CS"} element.
     * This element has the following pattern:
     *
     * {@preformat text
     *     FITTED_CS["<name>", <to base>, <base cs>]
     * }
     *
     * @param  parent The parent element.
     * @return The {@code "FITTED_CS"} element as a {@link CompoundCRS} object.
     * @throws ParseException if the {@code "COMPD_CS"} element can't be parsed.
     */
    private DerivedCRS parseFittedCS(final Element parent) throws ParseException {
        Element       element    = parent.pullElement("FITTED_CS");
        String        name       = element.pullString("name");
        Map<String,?> properties = parseAuthority(element, name);
        final MathTransform toBase = parseMathTransform(element, true);
        final CoordinateReferenceSystem base = parseCoordinateReferenceSystem(element);
        final OperationMethod method = getOperationMethod();
        element.close();
        /*
         * WKT provides no informations about the underlying CS of a derived CRS.
         * We have to guess some reasonable one with arbitrary units.  We try to
         * construct the one which contains as few information as possible, in
         * order to avoid providing wrong informations.
         */
        final CoordinateSystemAxis[] axis = new CoordinateSystemAxis[toBase.getSourceDimensions()];
        final StringBuilder buffer = new StringBuilder(name);
        buffer.append(" axis ");
        final int start = buffer.length();
        try {
            for (int i=0; i<axis.length; i++) {
                final String number = String.valueOf(i);
                buffer.setLength(start);
                buffer.append(number);
                axis[i] = csFactory.createCoordinateSystemAxis(
                    singletonMap(IdentifiedObject.NAME_KEY, buffer.toString()),
                    number, AxisDirection.OTHER, Unit.ONE);
            }
            final Conversion conversion = new DefiningConversion(
                    singletonMap(IdentifiedObject.NAME_KEY, method.getName().getCode()),
                    method, toBase.inverse());
            final CoordinateSystem cs = new AbstractCS(properties, axis);
            return crsFactory.createDerivedCRS(properties, base, conversion, cs);
        } catch (FactoryException | NoninvertibleTransformException exception) {
            throw element.parseFailed(exception, null);
        }
    }
}
