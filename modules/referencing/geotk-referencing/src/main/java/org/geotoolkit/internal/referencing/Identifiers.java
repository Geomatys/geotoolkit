/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.internal.referencing;

import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import javax.measure.unit.SI;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.opengis.util.GenericName;
import org.opengis.metadata.citation.Citation;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.referencing.ReferenceIdentifier;

import org.geotoolkit.util.XArrays;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.parameter.DefaultParameterDescriptor;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;

import static java.lang.Boolean.TRUE;
import static java.lang.Boolean.FALSE;
import static org.opengis.referencing.operation.SingleOperation.*;
import static org.geotoolkit.metadata.iso.citation.Citations.*;
import static org.geotoolkit.util.collection.XCollections.hashMapCapacity;


/**
 * A parameter descriptor with all known identifiers declared, even the one that are not applicable
 * to all projections. This descriptor should never appear in public API, but is used internally by
 * the referencing framework in order to be tolerance will parsing possibly malformed WKT.
 * <p>
 * As a convenience for projection providers creating the descriptors to be exposed in public API,
 * this class can produces arrays of {@link NamedIdentifier} where only one name is selected for
 * each authority. If there is more than one name for the same authority, then only a given name
 * is retained and the other ones are removed.
 * <p>
 * This class is used as helper methods for Geotk implementation of math transform provider.
 * The current approach is too specific to deserve a public API.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 3.00
 * @module
 */
public final class Identifiers extends DefaultParameterDescriptor<Double> {
    /**
     * For cross-version compatibility. Provided as a safety, however
     * we do not expect instance of this class to be serialized since
     * they should not appear in public API.
     */
    private static final long serialVersionUID = -4608976443553166518L;

    /**
     * The identifiers for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#centralMeridian
     * central meridian} parameter. Valid values range is from -180 to 180&deg;.
     * <p>
     * This parameter is mandatory - meaning that it appears in {@linkplain ParameterValueGroup
     * parameter value group} even if the user didn't set it explicitly - and its default value
     * is 0&deg;.
     *
     * {@note ESRI uses <code>"Longitude_Of_Center"</code> in orthographic, not to be
     *        confused with <code>"Longitude_Of_Center"</code> in oblique mercator.}
     */
    public static final Identifiers CENTRAL_MERIDIAN = new Identifiers(new NamedIdentifier[] {
            new NamedIdentifier(OGC,     "central_meridian"),
            new NamedIdentifier(OGC,     "longitude_of_center"),
            new NamedIdentifier(EPSG,    "Longitude of origin"),
            new NamedIdentifier(EPSG,    "Longitude of false origin"),
            new NamedIdentifier(EPSG,    "Longitude of natural origin"),
            new NamedIdentifier(EPSG,    "Spherical longitude of origin"),
            new NamedIdentifier(EPSG,    "Longitude of projection centre"),
            new NamedIdentifier(ESRI,    "Central_Meridian"),
            new NamedIdentifier(ESRI,    "Longitude_Of_Center"),
            new NamedIdentifier(ESRI,    "Longitude_Of_Origin"),
            new NamedIdentifier(NETCDF,  "longitude_of_projection_origin"),
            new NamedIdentifier(NETCDF,  "longitude_of_central_meridian"),
            new NamedIdentifier(GEOTIFF, "NatOriginLong"),
            new NamedIdentifier(GEOTIFF, "FalseOriginLong"),
            new NamedIdentifier(GEOTIFF, "ProjCenterLong"),
            new NamedIdentifier(GEOTIFF, "CenterLong"),
            new NamedIdentifier(GEOTIFF, "StraightVertPoleLong"),
            new NamedIdentifier(PROJ4,   "lon_0")
        }, 0, -180, 180, NonSI.DEGREE_ANGLE, true);

    /**
     * The identifiers for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#latitudeOfOrigin
     * latitude of origin} parameter. Valid values range is from -90 to 90&deg;.
     * <p>
     * This parameter is mandatory - meaning that it appears in {@linkplain ParameterValueGroup
     * parameter value group} even if the user didn't set it explicitly - and its default value
     * is 0&deg;.
     *
     * {@note ESRI uses <code>"Latitude_Of_Center"</code> in orthographic.}
     */
    public static final Identifiers LATITUDE_OF_ORIGIN;

    /**
     * The operation parameter descriptor for the standard parallel 1 parameter value.
     * Valid values range is from -90 to 90&deg;. This parameter is optional.
     */
    public static final Identifiers STANDARD_PARALLEL_1;

    /**
     * Creates the above constants together in order to share instances of identifiers
     * that appear in both cases. Those common identifiers are misplaced for historical
     * reasons (in the EPSG case, one of them is usually deprecated). We still need to
     * declare them in both places for compatibility with historical data.
     */
    static {
        final NamedIdentifier esri = new NamedIdentifier(ESRI, "Standard_Parallel_1");
        final NamedIdentifier epsg = new NamedIdentifier(EPSG, "Latitude of 1st standard parallel");

        LATITUDE_OF_ORIGIN = new Identifiers(new NamedIdentifier[] {
            new NamedIdentifier(OGC,     "latitude_of_origin"),
            new NamedIdentifier(OGC,     "latitude_of_center"),
            new NamedIdentifier(EPSG,    "Latitude of false origin"),
            new NamedIdentifier(EPSG,    "Latitude of natural origin"),
            new NamedIdentifier(EPSG,    "Spherical latitude of origin"),
            new NamedIdentifier(EPSG,    "Latitude of projection centre"), epsg,
            new NamedIdentifier(ESRI,    "Latitude_Of_Origin"),
            new NamedIdentifier(ESRI,    "Latitude_Of_Center"), esri,
            new NamedIdentifier(NETCDF,  "latitude_of_projection_origin"),
            new NamedIdentifier(GEOTIFF, "NatOriginLat"),
            new NamedIdentifier(GEOTIFF, "FalseOriginLat"),
            new NamedIdentifier(GEOTIFF, "ProjCenterLat"),
            new NamedIdentifier(GEOTIFF, "CenterLat"),
            new NamedIdentifier(PROJ4,   "lat_0")
        }, 0, -90, 90, NonSI.DEGREE_ANGLE, true);

        STANDARD_PARALLEL_1 = new Identifiers(new NamedIdentifier[] {
            new NamedIdentifier(OGC,     "standard_parallel_1"),
            new NamedIdentifier(OGC,     "pseudo_standard_parallel_1"),
            new NamedIdentifier(EPSG,    "Latitude of standard parallel"), epsg,
            new NamedIdentifier(EPSG,    "Latitude of pseudo standard parallel"),
            new NamedIdentifier(ESRI,    "Pseudo_Standard_Parallel_1"), esri,
            new NamedIdentifier(NETCDF,  "standard_parallel[1]"),
            new NamedIdentifier(GEOTIFF, "StdParallel1"),
            new NamedIdentifier(PROJ4,   "lat_1")
        }, Double.NaN, -90, 90, NonSI.DEGREE_ANGLE, false);
    }

    /**
     * The operation parameter descriptor for the standard parallel 2 parameter value.
     * Valid values range is from -90 to 90&deg;. This parameter is optional.
     */
    public static final Identifiers STANDARD_PARALLEL_2 = new Identifiers(new NamedIdentifier[] {
            new NamedIdentifier(OGC,     "standard_parallel_2"),
            new NamedIdentifier(EPSG,    "Latitude of 2nd standard parallel"),
            new NamedIdentifier(ESRI,    "Standard_Parallel_2"),
            new NamedIdentifier(NETCDF,  "standard_parallel[2]"),
            new NamedIdentifier(GEOTIFF, "StdParallel2"),
            new NamedIdentifier(PROJ4,   "lat_2")
        }, Double.NaN, -90, 90, NonSI.DEGREE_ANGLE, false);

    /**
     * The operation parameter descriptor for the {@code latitudeOf1stPoint} parameter value.
     * Valid values range is from -90 to 90&deg;. This parameter is mandatory and have no
     * default value.
     */
    public static final Identifiers LAT_OF_1ST_POINT = new Identifiers(new NamedIdentifier[] {
            new NamedIdentifier(Citations.ESRI, "Latitude_Of_1st_Point")
        }, Double.NaN, -90, 90, NonSI.DEGREE_ANGLE, true);

    /**
     * The operation parameter descriptor for the {@code longitudeOf1stPoint} parameter value.
     * Valid values range is from -180 to 180&deg;. This parameter is mandatory and have no
     * default value.
     */
    public static final Identifiers LONG_OF_1ST_POINT = new Identifiers(new NamedIdentifier[] {
            new NamedIdentifier(Citations.ESRI, "Longitude_Of_1st_Point")
        }, Double.NaN, -180, 180, NonSI.DEGREE_ANGLE, true);

    /**
     * The operation parameter descriptor for the {@code latitudeOf2ndPoint} parameter value.
     * Valid values range is from -90 to 90&deg;. This parameter is mandatory and have no
     * default value.
     */
    public static final Identifiers LAT_OF_2ND_POINT = new Identifiers(new NamedIdentifier[] {
            new NamedIdentifier(Citations.ESRI, "Latitude_Of_2nd_Point")
        }, Double.NaN, -90, 90, NonSI.DEGREE_ANGLE, true);

    /**
     * The operation parameter descriptor for the {@code longitudeOf2ndPoint} parameter value.
     * Valid values range is from -180 to 180&deg;. This parameter is mandatory and have no
     * default value.
     */
    public static final Identifiers LONG_OF_2ND_POINT = new Identifiers(new NamedIdentifier[] {
            new NamedIdentifier(Citations.ESRI, "Longitude_Of_2nd_Point")
        }, Double.NaN, -180, 180, NonSI.DEGREE_ANGLE, true);

    /**
     * The operation parameter descriptor for the {@code azimuth} parameter value.
     * This parameter is mandatory and have no default value.
     */
    public static final Identifiers AZIMUTH = new Identifiers(new NamedIdentifier[] {
            new NamedIdentifier(OGC,      "azimuth"),
            new NamedIdentifier(EPSG,     "Azimuth of initial line"),
            new NamedIdentifier(EPSG,     "Co-latitude of cone axis"), // Used in Krovak projection.
            new NamedIdentifier(ESRI,     "Azimuth"),
            new NamedIdentifier(GEOTIFF,  "AzimuthAngle")
        }, Double.NaN, -360, 360, NonSI.DEGREE_ANGLE, true);

    /**
     * The operation parameter descriptor for the {@code rectifiedGridAngle} parameter value.
     * This is an optional parameter with valid values ranging from -360 to 360&deg;.
     * The default value is the azimuth.
     */
    public static final Identifiers RECTIFIED_GRID_ANGLE = new Identifiers(new NamedIdentifier[] {
            new NamedIdentifier(Citations.OGC,      "rectified_grid_angle"),
            new NamedIdentifier(Citations.EPSG,     "Angle from Rectified to Skew Grid"),
            new NamedIdentifier(Citations.ESRI,     "XY_Plane_Rotation"),
            new NamedIdentifier(Citations.GEOTIFF,  "RectifiedGridAngle")
        }, Double.NaN, -360, 360, NonSI.DEGREE_ANGLE, false);

    /**
     * The identifiers for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#scaleFactor
     * scale factor} parameter. Valid values range is from 0 to infinity.
     * <p>
     * This parameter is mandatory - meaning that it appears in {@linkplain ParameterValueGroup
     * parameter value group} even if the user didn't set it explicitly - and its default value
     * is 1.
     */
    public static final Identifiers SCALE_FACTOR = new Identifiers(new NamedIdentifier[] {
            new NamedIdentifier(OGC,     "scale_factor"),
            new NamedIdentifier(EPSG,    "Scale factor at natural origin"),
            new NamedIdentifier(EPSG,    "Scale factor on initial line"),
            new NamedIdentifier(EPSG,    "Scale factor on pseudo standard parallel"),
            new NamedIdentifier(ESRI,    "Scale_Factor"),
            new NamedIdentifier(NETCDF,  "scale_factor_at_projection_origin"),
            new NamedIdentifier(NETCDF,  "scale_factor_at_central_meridian"),
            new NamedIdentifier(GEOTIFF, "ScaleAtNatOrigin"),
            new NamedIdentifier(GEOTIFF, "ScaleAtCenter"),
            new NamedIdentifier(PROJ4,   "k")
        }, 1, 0, Double.POSITIVE_INFINITY, Unit.ONE, true);

    /**
     * The identifiers for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseEasting
     * false easting} parameter. Valid values range is unrestricted.
     * <p>
     * This parameter is mandatory - meaning that it appears in {@linkplain ParameterValueGroup
     * parameter value group} even if the user didn't set it explicitly - and its default value
     * is 0 metres.
     */
    public static final Identifiers FALSE_EASTING = new Identifiers(new NamedIdentifier[] {
            new NamedIdentifier(OGC,     "false_easting"),
            new NamedIdentifier(EPSG,    "False easting"),
            new NamedIdentifier(EPSG,    "Easting at false origin"),
            new NamedIdentifier(EPSG,    "Easting at projection centre"),
            new NamedIdentifier(ESRI,    "False_Easting"),
            new NamedIdentifier(NETCDF,  "false_easting"),
            new NamedIdentifier(GEOTIFF, "FalseEasting"),
            new NamedIdentifier(GEOTIFF, "FalseOriginEasting"),
            new NamedIdentifier(PROJ4,   "x_0")
        }, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, SI.METRE, true);

    /**
     * The identifiers for the {@linkplain
     * org.geotoolkit.referencing.operation.projection.UnitaryProjection.Parameters#falseNorthing
     * false northing} parameter. Valid values range is unrestricted.
     * <p>
     * This parameter is mandatory - meaning that it appears in {@linkplain ParameterValueGroup
     * parameter value group} even if the user didn't set it explicitly - and its default value
     * is 0 metres.
     */
    public static final Identifiers FALSE_NORTHING = new Identifiers(new NamedIdentifier[] {
            new NamedIdentifier(OGC,     "false_northing"),
            new NamedIdentifier(EPSG,    "False northing"),
            new NamedIdentifier(EPSG,    "Northing at false origin"),
            new NamedIdentifier(EPSG,    "Northing at projection centre"),
            new NamedIdentifier(ESRI,    "False_Northing"),
            new NamedIdentifier(NETCDF,  "false_northing"),
            new NamedIdentifier(GEOTIFF, "FalseNorthing"),
            new NamedIdentifier(GEOTIFF, "FalseOriginNorthing"),
            new NamedIdentifier(PROJ4,   "y_0")
        }, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, SI.METRE, true);

    /**
     * The identifiers which can be declared to the descriptor. Only a subset of those values
     * will actually be used. The subset is specified by a call to a {@code select} method.
     */
    private final NamedIdentifier[] identifiers;

    /**
     * Locates the identifiers by their {@linkplain ReferenceIdentifier#getCode() code}.
     * The {@linkplain Map#values() values} are the same than the {@linkplain #identifiers}
     * array, or a subset of the identifiers array. The later case (only a subset) occurs
     * if many authorities use the same code. In such case, we map only the first authority.
     */
    private final Map<String,NamedIdentifier> identifiersMap;

    /**
     * Creates a new instance of {@code Identifiers} for the given identifiers.
     * The array given in argument should never be modified, since it will not be cloned.
     *
     * @param identifiers  The parameter identifiers. Must contains at least one entry.
     * @param defaultValue The default value for the parameter, or {@link Double#NaN} if none.
     * @param minimum      The minimum parameter value, or {@link Double#NEGATIVE_INFINITY} if none.
     * @param maximum      The maximum parameter value, or {@link Double#POSITIVE_INFINITY} if none.
     * @param unit         The unit for default, minimum and maximum values.
     * @param required     {@code true} if the parameter is mandatory.
     */
    private Identifiers(final NamedIdentifier[] identifiers, final double defaultValue,
            final double minimum, final double maximum, final Unit<?> unit, final boolean required)
    {
        super(toMap(identifiers), Double.class, null,
                Double.isNaN(defaultValue)          ? null : Double.valueOf(defaultValue),
                minimum == Double.NEGATIVE_INFINITY ? null : Double.valueOf(minimum),
                maximum == Double.POSITIVE_INFINITY ? null : Double.valueOf(maximum), unit, required);
        this.identifiers = identifiers;
        identifiersMap = new HashMap<>(hashMapCapacity(identifiers.length));
        // Put elements in reverse order in order to give precedence to the first occurrence.
        for (int i=identifiers.length; --i>=0;) {
            final NamedIdentifier id = identifiers[i];
            identifiersMap.put(id.getCode(), id);
        }
    }

    /**
     * Returns a new descriptor having the same identifiers than this descriptor.
     * The given array is used for disambiguation when the same authority defines
     * many names.
     *
     * @param  names    The names to be used for disambiguation.
     * @return The requested identifiers.
     */
    public ParameterDescriptor<Double> select(final String... names) {
        return select(getMinimumOccurs() != 0, getDefaultValue(), null, null, names);
    }

    /**
     * Returns a new descriptor having the same identifiers than this descriptor.
     * The given array is used for disambiguation when the same authority defines
     * many names.
     *
     * @param  excludes   The authorities to exclude, or {@code null} if none.
     * @param  deprecated The names of deprecated identifiers, or {@code null} if none.
     * @param  names      The names to be used for disambiguation.
     * @return The requested identifiers.
     */
    public ParameterDescriptor<Double> select(final Citation[] excludes, final String[] deprecated, final String... names) {
        return select(getMinimumOccurs() != 0, getDefaultValue(), excludes, deprecated, names);
    }

    /**
     * Returns a new descriptor having the same identifiers than this descriptor but a different
     * {@code mandatory} status. The given array is used for disambiguation when the same authority
     * defines many names.
     *
     * @param  required Whatever the parameter shall be mandatory or not.
     * @param  names    The names to be used for disambiguation.
     * @return The requested identifiers.
     */
    public ParameterDescriptor<Double> select(final boolean required, final String... names) {
        return select(required, getDefaultValue(), null, null, names);
    }

    /**
     * Returns a new descriptor having the same identifiers than this descriptor but a different
     * {@code mandatory} status and default value. The given array is used for disambiguation when
     * the same authority defines many names.
     *
     * @param  required     Whatever the parameter shall be mandatory or not.
     * @param  defaultValue The default value.
     * @param  excludes     The authorities to exclude, or {@code null} if none.
     * @param  names        The names to be used for disambiguation.
     * @return The requested identifiers.
     */
    public ParameterDescriptor<Double> select(final boolean required, final double defaultValue,
            final Citation[] excludes, final String... names)
    {
        return select(required, Double.valueOf(defaultValue), excludes, null, names);
    }

    /**
     * Implementation of all {@code select} methods.
     * This method returns a new descriptor having the same identifiers than this descriptor,
     * except for the one that are not explicitly declared in the names array and that can not
     * be inherited.
     *
     * @param  required     Whatever the parameter shall be mandatory or not.
     * @param  defaultValue The default value, or {@code null} if none.
     * @param  excludes     The authorities to exclude, or {@code null} if none.
     * @param  deprecated   The names of deprecated identifiers, or {@code null} if none.
     * @param  names        The names to be used for disambiguation.
     *                      The same name may be used for more than one authority.
     * @return The requested identifiers.
     */
    private ParameterDescriptor<Double> select(final boolean required, Double defaultValue,
            final Citation[] excludes, final String[] deprecated, final String... names)
    {
        final Map<Citation,Boolean> authorities = new HashMap<>();
        NamedIdentifier[] selected = identifiers.clone();
        final boolean[] idUsed = new boolean[selected.length];
        final boolean[] nameUsed = new boolean[names.length];
        /*
         * Finds every identifiers explicitly requested by the names array given in argument.
         * Elements for which no identifier has been found at this stage will be left to null.
         */
        for (int i=0; i<selected.length; i++) {
            final NamedIdentifier candidate = selected[i];
            final String code = candidate.getCode();
            for (int j=names.length; --j>=0;) {
                if (code.equals(names[j])) {
                    final Citation authority = candidate.getAuthority();
                    if (!XArrays.contains(excludes, authority)) {
                        if (authorities.put(authority, TRUE) != null) {
                            throw new IllegalStateException(Errors.format(
                                    Errors.Keys.VALUE_ALREADY_DEFINED_$1, authority));
                        }
                        nameUsed[j] = true;
                        idUsed[i] = true;
                        break;
                    }
                }
            }
        }
        /*
         * If a name has not been used, this is considered as an error. We perform
         * this check for reducing the risk of erroneous declaration in providers.
         * Note that the same name may be used for more than one authority.
         */
        for (int i=0; i<nameUsed.length; i++) {
            if (!nameUsed[i]) {
                throw new IllegalArgumentException(Errors.format(
                        Errors.Keys.UNKNOWN_PARAMETER_$1, names[i]));
            }
        }
        /*
         * If some identifiers were selected as a result of explicit requirement through the
         * names array, discards all other identifiers of that authority. Otherwise if there
         * is some remaining authorities declaring exactly one identifier, inherits that
         * identifier silently. If more than one identifier is found for the same authority,
         * this is considered an error.
         */
        int n=0;
        for (int i=0; i<selected.length; i++) {
            final NamedIdentifier candidate = selected[i];
            final Citation authority = candidate.getAuthority();
            if (!XArrays.contains(excludes, authority)) {
                if (!idUsed[i]) {
                    final Boolean explicit = authorities.get(authority);
                    if (explicit == null) {
                        // Inherit silently an identifier for a new authority.
                        authorities.put(authority, FALSE);
                    } else {
                        // An identifier was already specified for this authority.
                        // If the identifier was specified explicitly by the user,
                        // do nothing. Otherwise we have ambiguity, so remove the
                        // identifier we added previously.
                        if (explicit.equals(FALSE)) {
                            for (int j=0; j<n; j++) {
                                if (authority.equals(selected[j].getAuthority())) {
                                    System.arraycopy(selected, j+1, selected, j, (--n)-j);
                                }
                            }
                        }
                        continue;
                    }
                }
                selected[n++] = candidate;
            }
        }
        /*
         * Adds deprecated names, if any. Those names will appears last in the names array.
         * Note that at the difference of ordinary names, we don't share deprecated names
         * between different provider. Deprecated names are rare enough that this is not needed.
         */
        if (deprecated != null) {
            selected = XArrays.resize(selected, n + deprecated.length);
            for (final String code : deprecated) {
                selected[n++] = new DeprecatedName(identifiersMap.get(code));
            }
        }
        selected = XArrays.resize(selected, n);
        if (required && (defaultValue == null || defaultValue.isNaN())) {
            defaultValue = Double.valueOf(0);
        }
        return new DefaultParameterDescriptor<>(toMap(selected), Double.class, null,
                defaultValue, getMinimumValue(), getMaximumValue(), getUnit(), required);
    }

    /**
     * Returns a copy of the given descriptor, excluding the names from the given authorities.
     *
     * @param  model    The descriptor to copy.
     * @param  excludes The authorities to exclude.
     * @return A copy of the given parameter, excluding the names of the given authorities.
     */
    public static ParameterDescriptor<Double> exclude(final ParameterDescriptor<Double> model, final Citation... excludes) {
        final Object[] alias;
        final Map<String,Object> properties = new HashMap<>(IdentifiedObjects.getProperties(model));
        properties.put(IDENTIFIERS_KEY, exclude(excludes, (Object[]) properties.get(IDENTIFIERS_KEY)));
        properties.put(ALIAS_KEY, alias=exclude(excludes, (Object[]) properties.get(ALIAS_KEY)));
        properties.put(NAME_KEY, alias[0]); // In case the primary name is one of the excluded names.
        return new DefaultParameterDescriptor<>(properties, Double.class, null,
                model.getDefaultValue(), model.getMinimumValue(), model.getMaximumValue(),
                model.getUnit(), model.getMinimumOccurs() != 0);
    }

    /**
     * Removes the identifier of the given authorities from the given array. This method will
     * modify the given {@code array} in-place before to return a new array. This method is
     * only for {@link #exclude(ParameterDescriptor, Citation[])} internal working.
     */
    private static Object[] exclude(final Citation[] excludes, final Object[] array) {
        int n = 0;
        if (array != null) {
            for (int i=0; i<array.length; i++) {
                final Object candidate = array[i];
                if (candidate instanceof ReferenceIdentifier) {
                    if (XArrays.contains(excludes, ((ReferenceIdentifier) candidate).getAuthority())) {
                        continue;
                    }
                }
                array[n++] = candidate;
            }
        }
        return XArrays.resize(array, n);
    }

    /**
     * If the given collection contains a descriptor having an (<var>authority</var>, <var>code</var>)
     * pair matching at least one (<var>authority</var>, <var>code</var>) pair of this object, returns
     * that descriptor.
     *
     * @param  candidates The collection of descriptors in which one of them may be identified
     *         by the identifiers stored in this {@code Identifiers} object.
     * @return A descriptor from the given collection, or {@code null} if this method did not
     *         found any descriptor using the identifiers stored in this object.
     */
    public ParameterDescriptor<?> find(final Collection<GeneralParameterDescriptor> candidates) {
        for (final GeneralParameterDescriptor candidate : candidates) {
            final ReferenceIdentifier search = candidate.getName();
            final NamedIdentifier identifier = identifiersMap.get(search.getCode());
            if (identifier != null) {
                if (identifierMatches(search.getAuthority(), identifier.getAuthority())) {
                    /*
                     * If we have really found the identifier we were looking for, then we should
                     * have (search == identifier) because the collection given in argument shall
                     * always be derived from the select(String...) method (and consequently from
                     * the same NamedIdentifier instances) in Geotk implementation.
                     *
                     * However we relax the check in case this class get a wider usage than we
                     * expected. Since we already checked the name, only the authorities need
                     * to be compared.
                     */
                    if (candidate instanceof ParameterDescriptor<?>) {
                        return (ParameterDescriptor<?>) candidate;
                    }
                    // Name matches, but this is not an instance of parameter descriptor.
                    // It is probably an error. For now continue the search, but future
                    // implementations may do some other action here.
                }
            }
        }
        return null;
    }

    /**
     * Constructs a parameter descriptor for a mandatory floating point value. The parameter is
     * identified by codes in the namespace of one or more authorities ({@link Citations#OGC OGC},
     * {@link Citations#EPSG EPSG}, <i>etc.</i>). Those codes are declared as elements in the
     * {@code identifiers} array argument. The first element ({@code identifiers[0]}) is both the
     * {@linkplain ParameterDescriptor#getName main name} and the
     * {@linkplain ParameterDescriptor#getIdentifiers identifiers}.
     * All others elements are {@linkplain ParameterDescriptor#getAlias aliases}.
     * <p>
     * The descriptor created by this method is flagged as <cite>mandatory</cite>, meaning that
     * it will always appear in the list of parameter values that a user shall provides. However
     * the value will be initialized with the given default value (if different than {@linkplain
     * Double#NaN NaN}), so the user may not needs to supply explicitly a value.
     *
     * @param  identifiers  The parameter identifiers. Must contains at least one entry.
     * @param  defaultValue The default value for the parameter, or {@link Double#NaN} if none.
     * @param  minimum      The minimum parameter value, or {@link Double#NEGATIVE_INFINITY} if none.
     * @param  maximum      The maximum parameter value, or {@link Double#POSITIVE_INFINITY} if none.
     * @param  unit         The unit for default, minimum and maximum values.
     * @return The descriptor for the given identifiers.
     */
    public static ParameterDescriptor<Double> createDescriptor(
            final ReferenceIdentifier[] identifiers, final double defaultValue,
            final double minimum, final double maximum, final Unit<?> unit)
    {
        return DefaultParameterDescriptor.create(toMap(identifiers),
                defaultValue, minimum, maximum, unit, true);
    }

    /**
     * Constructs a parameter descriptor for an optional floating point value. The identifiers
     * are handled as described in the above {@link #createDescriptor createDescriptor} method.
     * <p>
     * The descriptor created by this method is flagged as <cite>optional</cite>, meaning that
     * it will appear in the list of parameter values only if set to a value different than
     * the default value.
     *
     * @param  identifiers  The parameter identifiers. Must contains at least one entry.
     * @param  defaultValue The default value for the parameter, or {@link Double#NaN} if none.
     * @param  minimum      The minimum parameter value, or {@link Double#NEGATIVE_INFINITY} if none.
     * @param  maximum      The maximum parameter value, or {@link Double#POSITIVE_INFINITY} if none.
     * @param  unit         The unit for default, minimum and maximum values.
     * @return The descriptor for the given identifiers.
     */
    public static ParameterDescriptor<Double> createOptionalDescriptor(
            final ReferenceIdentifier[] identifiers, final double defaultValue,
            final double minimum, final double maximum, final Unit<?> unit)
    {
        return DefaultParameterDescriptor.create(toMap(identifiers),
                defaultValue, minimum, maximum, unit, false);
    }

    /**
     * Constructs a parameter group from a set of alias. The parameter group is
     * identified by codes provided by one or more authorities. Common authorities are
     * {@link Citations#OGC OGC} and {@link Citations#EPSG EPSG} for example.
     * <p>
     * Special rules:
     * <ul>
     *   <li>The first entry in the {@code identifiers} array is the
     *       {@linkplain ParameterDescriptorGroup#getName primary name}.</li>
     *   <li>If a an entry do not implements the {@link GenericName} interface, it is
     *       an {@linkplain ParameterDescriptorGroup#getIdentifiers identifiers}.</li>
     *   <li>All others are {@linkplain ParameterDescriptorGroup#getAlias aliases}.</li>
     * </ul>
     *
     * @param  identifiers  The operation identifiers. Most contains at least one entry.
     * @param  parameters   The set of parameters, or {@code null} or an empty array if none.
     * @return The descriptor for the given identifiers.
     */
    public static ParameterDescriptorGroup createDescriptorGroup(
            final ReferenceIdentifier[] identifiers, final GeneralParameterDescriptor[] parameters)
    {
        return new DefaultParameterDescriptorGroup(toMap(identifiers), parameters);
    }

    /**
     * Puts the identifiers into a properties map suitable for {@link ParameterDescriptorGroup}
     * constructor. The first identifier is used as the primary name. All other elements are aliases.
     */
    private static Map<String,Object> toMap(final ReferenceIdentifier[] identifiers) {
        int idCount    = 0;
        int aliasCount = 0;
        GenericName[] alias = null;
        ReferenceIdentifier[] id = null;
        for (int i=0; i<identifiers.length; i++) {
            final ReferenceIdentifier candidate = identifiers[i];
            if (candidate instanceof GenericName) {
                if (alias == null) {
                    alias = new GenericName[identifiers.length - i];
                }
                alias[aliasCount++] = (GenericName) candidate;
            } else {
                if (id == null) {
                    id = new ReferenceIdentifier[identifiers.length - i];
                }
                id[idCount++] = candidate;
            }
        }
        id    = XArrays.resize(id,    idCount);
        alias = XArrays.resize(alias, aliasCount);
        final Map<String,Object> properties = new HashMap<>(4);
        properties.put(NAME_KEY,        identifiers[0]);
        properties.put(IDENTIFIERS_KEY, id);
        properties.put(ALIAS_KEY,       alias);
        return properties;
    }
}
