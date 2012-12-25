/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2012, Open Source Geospatial Foundation (OSGeo)
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */
package org.geotoolkit.referencing.datum;

import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.datum.Datum;
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.PrimeMeridian;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.operation.Matrix;

import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.operation.matrix.XMatrix;
import org.geotoolkit.referencing.AbstractIdentifiedObject;
import org.geotoolkit.referencing.NamedIdentifier;
import org.geotoolkit.util.ComparisonMode;
import org.geotoolkit.io.wkt.Formatter;

import static org.geotoolkit.util.Utilities.hash;
import static org.geotoolkit.util.Utilities.deepEquals;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;


/**
 * Defines the location and precise orientation in 3-dimensional space of a defined ellipsoid
 * (or sphere) that approximates the shape of the earth. Used also for Cartesian coordinate
 * system centered in this ellipsoid (or sphere).
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @see Ellipsoid
 * @see PrimeMeridian
 *
 * @since 1.2
 * @module
 */
@Immutable
@XmlType(name = "GeodeticDatumType", propOrder={
    "primeMeridian",
    "ellipsoid"
})
@XmlRootElement(name = "GeodeticDatum")
public class DefaultGeodeticDatum extends AbstractDatum implements GeodeticDatum {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = 8832100095648302943L;

    /**
     * The array to be returned when there is no Bursa Wolf parameters.
     */
    private static final BursaWolfParameters[] EMPTY_BURSAWOLF = new BursaWolfParameters[0];

    /**
     * Default WGS 1984 datum (EPSG:6326).
     * Prime meridian is {@linkplain DefaultPrimeMeridian#GREENWICH Greenwich}.
     * This datum is used in GPS systems and is the default for most {@code org.geotoolkit} packages.
     *
     * @see DefaultEllipsoid#WGS84
     * @see org.geotoolkit.referencing.crs.DefaultGeographicCRS#WGS84
     */
    public static final DefaultGeodeticDatum WGS84;
    static {
        final ReferenceIdentifier[] identifiers = {
            new NamedIdentifier(Citations.OGC,    "WGS84"),
            new NamedIdentifier(Citations.ORACLE, "WGS 84"),
            new NamedIdentifier(null,             "WGS_84"),
            new NamedIdentifier(null,             "WGS 1984"),
            new NamedIdentifier(null,             "WGS_1984"),
            new NamedIdentifier(Citations.ESRI,   "D_WGS_1984"),
            new NamedIdentifier(Citations.EPSG,   "World Geodetic System 1984")
        };
        final Map<String,Object> properties = new HashMap<>(6);
        properties.put(NAME_KEY,  identifiers[0]);
        properties.put(ALIAS_KEY, identifiers);
        properties.put(IDENTIFIERS_KEY, new NamedIdentifier(Citations.EPSG, "6326"));
        WGS84 = new DefaultGeodeticDatum(properties, DefaultEllipsoid.WGS84);
    }

    /**
     * Default WGS 1972 datum (EPSG:6322).
     * Prime meridian is {@linkplain DefaultPrimeMeridian#GREENWICH Greenwich}.
     * This datum is used, together with {@linkplain #WGS84}, in
     * {@linkplain org.geotoolkit.referencing.operation.transform.EarthGravitationalModel
     * Earth Gravitational Model}.
     *
     * @see DefaultEllipsoid#WGS72
     *
     * @since 3.00
     */
    public static final DefaultGeodeticDatum WGS72;
    static {
        final ReferenceIdentifier[] identifiers = {
            new NamedIdentifier(Citations.OGC,  "WGS72"),
            new NamedIdentifier(Citations.EPSG, "World Geodetic System 1972")
        };
        final Map<String,Object> properties = new HashMap<>(6);
        properties.put(NAME_KEY,  identifiers[0]);
        properties.put(ALIAS_KEY, identifiers);
        properties.put(IDENTIFIERS_KEY, new NamedIdentifier(Citations.EPSG, "6322"));
        WGS72 = new DefaultGeodeticDatum(properties, DefaultEllipsoid.WGS72);
    }

    /**
     * Default spherical datum.
     * Prime meridian is {@linkplain DefaultPrimeMeridian#GREENWICH Greenwich}.
     *
     * {@note This datum is close, but not identical, to the datum based on <cite>GRS 1980
     *        Authalic Sphere</cite> (EPSG:6047). This datum uses a sphere radius of 6371000
     *        metres, while the GRS 1980 Authalic Sphere uses a sphere radius of 6371007 metres.}
     *
     * @see DefaultEllipsoid#SPHERE
     * @see org.geotoolkit.referencing.crs.DefaultGeographicCRS#SPHERE
     *
     * @since 3.15
     */
    public static final DefaultGeodeticDatum SPHERE = new DefaultGeodeticDatum(
            IdentifiedObjects.getProperties(DefaultEllipsoid.SPHERE), DefaultEllipsoid.SPHERE);

    /**
     * The <code>{@value #BURSA_WOLF_KEY}</code> property for
     * {@linkplain #getAffineTransform datum shifts}.
     */
    public static final String BURSA_WOLF_KEY = "bursaWolf";

    /**
     * The ellipsoid.
     */
    @XmlElement
    private final Ellipsoid ellipsoid;

    /**
     * The prime meridian.
     */
    @XmlElement
    private final PrimeMeridian primeMeridian;

    /**
     * Bursa Wolf parameters for datum shifts, or {@code null} if none.
     */
    private final BursaWolfParameters[] bursaWolf;

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private DefaultGeodeticDatum() {
        this(org.geotoolkit.internal.referencing.NilReferencingObject.INSTANCE);
    }

    /**
     * Constructs a new datum with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param datum The datum to copy.
     *
     * @since 2.2
     */
    public DefaultGeodeticDatum(final GeodeticDatum datum) {
        super(datum);
        ellipsoid     = datum.getEllipsoid();
        primeMeridian = datum.getPrimeMeridian();
        bursaWolf     = (datum instanceof DefaultGeodeticDatum) ?
                        ((DefaultGeodeticDatum) datum).bursaWolf : null;
    }

    /**
     * Constructs a geodetic datum using the {@linkplain DefaultPrimeMeridian#GREENWICH Greenwich}
     * prime meridian. This is a convenience constructor for the very common case where the prime
     * meridian is the Greenwich one.
     *
     * @param name      The datum name.
     * @param ellipsoid The ellipsoid.
     *
     * @since 3.15
     */
    public DefaultGeodeticDatum(final String name, final Ellipsoid ellipsoid) {
        this(name, ellipsoid, DefaultPrimeMeridian.GREENWICH);
    }

    /**
     * Constructs a geodetic datum using the {@linkplain DefaultPrimeMeridian#GREENWICH Greenwich}
     * prime meridian. This is a convenience constructor for the very common case where the prime
     * meridian is the Greenwich one.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param ellipsoid  The ellipsoid.
     *
     * @since 3.15
     */
    public DefaultGeodeticDatum(final Map<String,?> properties, final Ellipsoid ellipsoid) {
        this(properties, ellipsoid, DefaultPrimeMeridian.GREENWICH);
    }

    /**
     * Constructs a geodetic datum from a name and the given prime meridian.
     *
     * @param name          The datum name.
     * @param ellipsoid     The ellipsoid.
     * @param primeMeridian The prime meridian. If omitted, the default is
     *                      {@linkplain DefaultPrimeMeridian#GREENWICH Greenwich}.
     */
    public DefaultGeodeticDatum(final String        name,
                                final Ellipsoid     ellipsoid,
                                final PrimeMeridian primeMeridian)
    {
        this(Collections.singletonMap(NAME_KEY, name), ellipsoid, primeMeridian);
    }

    /**
     * Constructs a geodetic datum from a set of properties. The properties map is given
     * unchanged to the {@linkplain AbstractDatum#AbstractDatum(Map) super-class constructor}.
     * Additionally, the following properties are understood by this constructor:
     * <p>
     * <table border='1'>
     *   <tr bgcolor="#CCCCFF" class="TableHeadingColor">
     *     <th nowrap>Property name</th>
     *     <th nowrap>Value type</th>
     *     <th nowrap>Value given to</th>
     *   </tr>
     *   <tr>
     *     <td nowrap>&nbsp;{@value #BURSA_WOLF_KEY}&nbsp;</td>
     *     <td nowrap>&nbsp;{@link BursaWolfParameters} or an array of those&nbsp;</td>
     *     <td nowrap>&nbsp;{@link #getBursaWolfParameters}</td>
     *   </tr>
     * </table>
     *
     * @param properties    Set of properties. Should contains at least {@code "name"}.
     * @param ellipsoid     The ellipsoid.
     * @param primeMeridian The prime meridian. If omitted, the default is
     *                      {@linkplain DefaultPrimeMeridian#GREENWICH Greenwich}.
     */
    public DefaultGeodeticDatum(final Map<String,?> properties,
                                final Ellipsoid     ellipsoid,
                                final PrimeMeridian primeMeridian)
    {
        super(properties);
        this.ellipsoid     = ellipsoid;
        this.primeMeridian = primeMeridian;
        ensureNonNull("ellipsoid",     ellipsoid);
        ensureNonNull("primeMeridian", primeMeridian);
        BursaWolfParameters[] bursaWolf;
        final Object object = properties.get(BURSA_WOLF_KEY);
        if (object instanceof BursaWolfParameters) {
            bursaWolf = new BursaWolfParameters[] {
                ((BursaWolfParameters) object).clone()
            };
        } else {
            bursaWolf = (BursaWolfParameters[]) object;
            if (bursaWolf != null) {
                if (bursaWolf.length == 0) {
                    bursaWolf = null;
                } else {
                    final Set<BursaWolfParameters> s = new LinkedHashSet<>();
                    for (int i=0; i<bursaWolf.length; i++) {
                        s.add(bursaWolf[i].clone());
                    }
                    bursaWolf = s.toArray(new BursaWolfParameters[s.size()]);
                }
            }
        }
        this.bursaWolf = bursaWolf;
    }

    /**
     * Returns a Geotk datum implementation with the same values than the given arbitrary
     * implementation. If the given object is {@code null}, then this method returns {@code null}.
     * Otherwise if the given object is already a Geotk implementation, then the given object is
     * returned unchanged. Otherwise a new Geotk implementation is created and initialized to the
     * attribute values of the given object.
     *
     * @param  object The object to get as a Geotk implementation, or {@code null} if none.
     * @return A Geotk implementation containing the values of the given object (may be the
     *         given object itself), or {@code null} if the argument was null.
     *
     * @since 3.18
     */
    public static DefaultGeodeticDatum castOrCopy(final GeodeticDatum object) {
        return (object == null) || (object instanceof DefaultGeodeticDatum)
                ? (DefaultGeodeticDatum) object : new DefaultGeodeticDatum(object);
    }

    /**
     * Returns the ellipsoid.
     */
    @Override
    public Ellipsoid getEllipsoid() {
        return ellipsoid;
    }

    /**
     * Returns the prime meridian.
     */
    @Override
    public PrimeMeridian getPrimeMeridian() {
        return primeMeridian;
    }

    /**
     * Returns all Bursa Wolf parameters specified in the {@code properties} map at
     * construction time.
     *
     * @return The Bursa Wolf parameters, or an empty array if none.
     *
     * @since 2.4
     */
    public BursaWolfParameters[] getBursaWolfParameters() {
        if (bursaWolf != null) {
            return bursaWolf.clone();
        }
        return EMPTY_BURSAWOLF;
    }

    /**
     * Returns Bursa Wolf parameters for a datum shift toward the specified target, or {@code null}
     * if none. This method search only for Bursa-Wolf parameters explicitly specified in the
     * {@code properties} map at construction time. This method doesn't try to infer a set of
     * parameters from indirect informations. For example it doesn't try to inverse the parameters
     * specified in the {@code target} datum if none were found in this datum. If such an elaborated
     * search is wanted, use {@link #getAffineTransform} instead.
     *
     * @param  target The target geodetic datum.
     * @return Bursa Wolf parameters from this datum to the given target datum,
     *         or {@code null} if none.
     */
    public BursaWolfParameters getBursaWolfParameters(final GeodeticDatum target) {
        if (bursaWolf != null) {
            for (int i=0; i<bursaWolf.length; i++) {
                final BursaWolfParameters candidate = bursaWolf[i];
                if (deepEquals(target, candidate.targetDatum, ComparisonMode.IGNORE_METADATA)) {
                    return candidate.clone();
                }
            }
        }
        return null;
    }

    /**
     * Returns a matrix that can be used to define a transformation to the specified datum.
     * If no transformation path is found, then this method returns {@code null}.
     *
     * @param  source The source datum.
     * @param  target The target datum.
     * @return An affine transform from {@code source} to {@code target}, or {@code null} if none.
     *
     * @see BursaWolfParameters#getAffineTransform
     */
    public static Matrix getAffineTransform(final GeodeticDatum source,
                                            final GeodeticDatum target)
    {
        return getAffineTransform(source, target, null);
    }

    /**
     * Returns a matrix that can be used to define a transformation to the specified datum.
     * If no transformation path is found, then this method returns {@code null}.
     *
     * @param  source The source datum.
     * @param  target The target datum.
     * @param  exclusion The set of datum to exclude from the search, or {@code null}.
     *         This is used in order to avoid never-ending recursivity.
     * @return An affine transform from {@code source} to {@code target}, or {@code null} if none.
     *
     * @see BursaWolfParameters#getAffineTransform
     */
    private static XMatrix getAffineTransform(final GeodeticDatum source,
                                              final GeodeticDatum target,
                                              Set<GeodeticDatum> exclusion)
    {
        ensureNonNull("source", source);
        ensureNonNull("target", target);
        if (source instanceof DefaultGeodeticDatum) {
            final BursaWolfParameters[] bursaWolf = ((DefaultGeodeticDatum) source).bursaWolf;
            if (bursaWolf != null) {
                for (int i=0; i<bursaWolf.length; i++) {
                    final BursaWolfParameters transformation = bursaWolf[i];
                    if (deepEquals(target, transformation.targetDatum, ComparisonMode.IGNORE_METADATA)) {
                        return transformation.getAffineTransform();
                    }
                }
            }
        }
        /*
         * No transformation found to the specified target datum.
         * Search if a transform exists in the opposite direction.
         */
        if (target instanceof DefaultGeodeticDatum) {
            final BursaWolfParameters[] bursaWolf = ((DefaultGeodeticDatum) target).bursaWolf;
            if (bursaWolf != null) {
                for (int i=0; i<bursaWolf.length; i++) {
                    final BursaWolfParameters transformation = bursaWolf[i];
                    if (deepEquals(source, transformation.targetDatum, ComparisonMode.IGNORE_METADATA)) {
                        final XMatrix matrix = transformation.getAffineTransform();
                        matrix.invert();
                        return matrix;
                    }
                }
            }
        }
        /*
         * No direct tranformation found. Search for a path through some intermediate datum.
         * First, search if there is some BursaWolfParameters for the same target in both
         * 'source' and 'target' datum. If such an intermediate is found, ask for a path
         * as below:
         *
         *    source   →   [common datum]   →   target
         */
        if (source instanceof DefaultGeodeticDatum && target instanceof DefaultGeodeticDatum) {
            final BursaWolfParameters[] sourceParam = ((DefaultGeodeticDatum) source).bursaWolf;
            final BursaWolfParameters[] targetParam = ((DefaultGeodeticDatum) target).bursaWolf;
            if (sourceParam!=null && targetParam!=null) {
                GeodeticDatum sourceStep;
                GeodeticDatum targetStep;
                for (int i=0; i<sourceParam.length; i++) {
                    sourceStep = sourceParam[i].targetDatum;
                    for (int j=0; j<targetParam.length; j++) {
                        targetStep = targetParam[j].targetDatum;
                        if (deepEquals(sourceStep, targetStep, ComparisonMode.IGNORE_METADATA)) {
                            final XMatrix step1, step2;
                            if (exclusion == null) {
                                exclusion = new HashSet<>();
                            }
                            if (exclusion.add(source)) {
                                if (exclusion.add(target)) {
                                    step1 = getAffineTransform(source, sourceStep, exclusion);
                                    if (step1 != null) {
                                        step2 = getAffineTransform(targetStep, target, exclusion);
                                        if (step2 != null) {
                                            /*
                                             * Note: XMatrix.multiply(XMatrix) is equivalent to
                                             *       AffineTransform.concatenate(...): First
                                             *       transform by the supplied transform and
                                             *       then transform the result by the original
                                             *       transform.
                                             */
                                            step2.multiply(step1);
                                            return step2;
                                        }
                                    }
                                    exclusion.remove(target);
                                }
                                exclusion.remove(source);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns {@code true} if the specified object is equals (at least on computation purpose)
     * to the {@link #WGS84} datum. This method may conservatively returns {@code false} if the
     * specified datum is uncertain (for example because it come from an other implementation).
     *
     * @param datum The datum to inspect.
     * @return {@code true} if the given datum is equal to WGS84 for computational purpose.
     */
    public static boolean isWGS84(final Datum datum) {
        if (datum instanceof AbstractIdentifiedObject) {
            return WGS84.equals((AbstractIdentifiedObject) datum, ComparisonMode.IGNORE_METADATA);
        }
        // Maybe the specified object has its own test...
        return datum!=null && datum.equals(WGS84);
    }

    /**
     * Compare this datum with the specified object for equality.
     *
     * @param  object The object to compare to {@code this}.
     * @param  mode {@link ComparisonMode#STRICT STRICT} for performing a strict comparison, or
     *         {@link ComparisonMode#IGNORE_METADATA IGNORE_METADATA} for comparing only properties
     *         relevant to transformations.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true; // Slight optimization.
        }
        if (super.equals(object, mode)) {
            switch (mode) {
                case STRICT: {
                    final DefaultGeodeticDatum that = (DefaultGeodeticDatum) object;
                    return Objects.equals(this.ellipsoid,     that.ellipsoid)     &&
                           Objects.equals(this.primeMeridian, that.primeMeridian) &&
                            Arrays.equals(this.bursaWolf,     that.bursaWolf);
                }
                default: {
                    final GeodeticDatum that = (GeodeticDatum) object;
                    return deepEquals(getEllipsoid(),     that.getEllipsoid(),     mode) &&
                           deepEquals(getPrimeMeridian(), that.getPrimeMeridian(), mode);
                    /*
                     * HACK: We do not consider Bursa Wolf parameters as a non-metadata field.
                     *       This is needed in order to get equalsIgnoreMetadata(...) to returns
                     *       'true' when comparing the WGS84 constant in this class with a WKT
                     *       DATUM element with a TOWGS84[0,0,0,0,0,0,0] element. Furthermore,
                     *       the Bursa Wolf parameters are not part of ISO 19111 specification.
                     *       We don't want two CRS to be considered as different because one has
                     *       more of those transformation informations (which is nice, but doesn't
                     *       change the CRS itself).
                     */
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int computeHashCode() {
        return hash(ellipsoid, hash(primeMeridian, super.computeHashCode()));
    }

    /**
     * Formats the inner part of a
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html#DATUM"><cite>Well
     * Known Text</cite> (WKT)</A> element.
     *
     * @param  formatter The formatter to use.
     * @return The WKT element name, which is {@code "DATUM"}.
     */
    @Override
    public String formatWKT(final Formatter formatter) {
        // Do NOT invokes the super-class method, because
        // horizontal datum do not write the datum type.
        formatter.append(ellipsoid);
        if (bursaWolf != null) {
            for (int i=0; i<bursaWolf.length; i++) {
                final BursaWolfParameters transformation = bursaWolf[i];
                if (isWGS84(transformation.targetDatum)) {
                    formatter.append(transformation);
                    break;
                }
            }
        }
        return "DATUM";
    }
}
