/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2001-2009, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009, Geomatys
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
package org.geotoolkit.referencing.crs;

import java.util.Map;
import java.util.HashMap;
import javax.measure.unit.Unit;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlAttribute;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.geotoolkit.internal.jaxb.text.StringConverter;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.util.InternationalString;

import org.geotoolkit.measure.Measure;
import org.geotoolkit.io.wkt.Formatter;
import org.geotoolkit.referencing.cs.AbstractCS;
import org.geotoolkit.referencing.AbstractReferenceSystem;
import org.geotoolkit.referencing.AbstractIdentifiedObject;
import org.geotoolkit.util.UnsupportedImplementationException;
import org.geotoolkit.internal.referencing.CRSUtilities;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.xml.Namespaces;


/**
 * Abstract coordinate reference system, usually defined by a coordinate system and a datum.
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.00
 *
 * @see AbstractCS
 * @see org.geotoolkit.referencing.datum.AbstractDatum
 *
 * @since 1.2
 * @module
 */
public abstract class AbstractCRS extends AbstractReferenceSystem implements CoordinateReferenceSystem {
    /**
     * Serial number for interoperability with different versions.
     */
    private static final long serialVersionUID = -7433284548909530047L;

    /**
     * The {@code gml:id}, which is mandatory.
     */
    @XmlID
    @XmlAttribute(name = "id", namespace = Namespaces.GML ,required = true)
    @XmlJavaTypeAdapter(StringConverter.class)
    private String getID() {
        return "coordinate-reference-system";
    }

    /**
     * The coordinate system. This field should be considered as final.
     * It is modified only by JAXB at unmarshalling time.
     */
    CoordinateSystem coordinateSystem;

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private AbstractCRS() {
        this(org.geotoolkit.internal.referencing.NullReferencingObject.INSTANCE);
    }

    /**
     * Constructs a new coordinate reference system with the same values than the specified one.
     * This copy constructor provides a way to wrap an arbitrary implementation into a
     * Geotoolkit one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param crs The coordinate reference system to copy.
     *
     * @since 2.2
     */
    public AbstractCRS(final CoordinateReferenceSystem crs) {
        super(crs);
        coordinateSystem = crs.getCoordinateSystem();
    }

    /**
     * Constructs a coordinate reference system from a set of properties. The properties are given
     * unchanged to the {@linkplain AbstractReferenceSystem#AbstractReferenceSystem(Map) super-class
     * constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param cs The coordinate system.
     */
    public AbstractCRS(final Map<String,?> properties, final CoordinateSystem cs) {
        super(properties);
        ensureNonNull("cs", cs);
        this.coordinateSystem = cs;
    }

    /**
     * Creates a name for the predefined constants in subclasses. The name is a {@link String}
     * object in a fixed locale. In many case this fixed locale is the English one, but for this
     * particular method we take the system default. We do that way because this method is used
     * for the creation of convenience objects only, not for objects created from an official
     * database. Concequently the "unlocalized" name is actually choosen according the user's
     * locale at class initialization time.
     * <p>
     * The same name is also added in a localizable form as an alias. Since the {@link #nameMatches}
     * convenience method checks the alias, it still possible to consider two objects as equivalent
     * even if their names were formatted in different locales.
     */
    static Map<String,?> name(final int key) {
        final Map<String,Object> properties = new HashMap<String,Object>(4);
        final InternationalString name = Vocabulary.formatInternational(key);
        properties.put(NAME_KEY,  name.toString());
        properties.put(ALIAS_KEY, name);
        return properties;
    }

    /**
     * Returns the coordinate system.
     */
    @Override
    public CoordinateSystem getCoordinateSystem() {
        return coordinateSystem;
    }

    /**
     * Returns the unit used for all axis. If not all axis uses the same unit,
     * then this method returns {@code null}. This method is often used for
     * Well Know Text (WKT) formatting.
     */
    final Unit<?> getUnit() {
        return CRSUtilities.getUnit(coordinateSystem);
    }

    /**
     * Computes the distance between two points. This convenience method delegates the work to
     * the underlyling {@linkplain AbstractCS coordinate system}, if possible.
     *
     * @param  coord1 Coordinates of the first point.
     * @param  coord2 Coordinates of the second point.
     * @return The distance between {@code coord1} and {@code coord2}.
     * @throws UnsupportedOperationException if this coordinate reference system can't compute
     *         distances.
     * @throws MismatchedDimensionException if a coordinate doesn't have the expected dimension.
     */
    public Measure distance(final double[] coord1, final double[] coord2)
            throws UnsupportedOperationException, MismatchedDimensionException
    {
        if (coordinateSystem instanceof AbstractCS) {
            return ((AbstractCS) coordinateSystem).distance(coord1, coord2);
        }
        throw new UnsupportedImplementationException(coordinateSystem.getClass());
    }

    /**
     * Compares this coordinate reference system with the specified object for equality.
     * If {@code compareMetadata} is {@code true}, then all available properties are
     * compared including the {@linkplain #getDomainOfValidity domain of validity} and
     * the {@linkplain #getScope scope}.
     *
     * @param  object The object to compare to {@code this}.
     * @param  compareMetadata {@code true} for performing a strict comparison, or
     *         {@code false} for comparing only properties relevant to transformations.
     * @return {@code true} if both objects are equal.
     */
    @Override
    public boolean equals(final AbstractIdentifiedObject object, final boolean compareMetadata) {
        if (super.equals(object, compareMetadata)) {
            final AbstractCRS that = (AbstractCRS) object;
            return equals(this.coordinateSystem, that.coordinateSystem, compareMetadata);
        }
        return false;
    }

    /**
     * Returns a hash value for this CRS. {@linkplain #getName Name}, {@linkplain #getIdentifiers
     * identifiers} and {@linkplain #getRemarks remarks} are not taken in account. In other words,
     * two CRS objects will return the same hash value if they are equal in the sense of
     * <code>{@link #equals(AbstractIdentifiedObject,boolean) equals}(AbstractIdentifiedObject,
     * <strong>false</strong>)</code>.
     *
     * @return The hash code value. This value doesn't need to be the same
     *         in past or future versions of this class.
     */
    @Override
    public int hashCode() {
        return (int)serialVersionUID ^ coordinateSystem.hashCode();
    }

    /**
     * Formats the inner part of a
     * <A HREF="http://geoapi.sourceforge.net/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html"><cite>Well
     * Known Text</cite> (WKT)</A> element. The default implementation writes the following
     * elements:
     * <p>
     * <ul>
     *   <li>The {@linkplain AbstractSingleCRS#datum datum}, if any.</li>
     *   <li>The unit if all axis use the same unit. Otherwise the unit is omitted and
     *       the WKT format is {@linkplain Formatter#isInvalidWKT flagged as invalid}.</li>
     *   <li>All {@linkplain #coordinateSystem coordinate system}'s axis.</li>
     * </ul>
     *
     * @param  formatter The formatter to use.
     * @return The name of the WKT element type (e.g. {@code "GEOGCS"}).
     */
    @Override
    public String formatWKT(final Formatter formatter) {
        formatDefaultWKT(formatter);
        // Will declares the WKT as invalid.
        return super.formatWKT(formatter);
    }

    /**
     * Default implementation of {@link #formatWKT}. For {@link DefaultEngineeringCRS}
     * and {@link DefaultVerticalCRS} use only.
     */
    void formatDefaultWKT(final Formatter formatter) {
        final Unit<?> unit = getUnit();
        formatter.append(unit);
        final int dimension = coordinateSystem.getDimension();
        for (int i=0; i<dimension; i++) {
            formatter.append(coordinateSystem.getAxis(i));
        }
        if (unit == null) {
            formatter.setInvalidWKT(CoordinateReferenceSystem.class);
        }
    }
}
