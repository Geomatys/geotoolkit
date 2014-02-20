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
package org.geotoolkit.referencing.crs;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import javax.measure.unit.SI;
import javax.xml.bind.annotation.XmlTransient;
import net.jcip.annotations.Immutable;

import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.crs.EngineeringCRS;
import org.opengis.referencing.datum.EngineeringDatum;

import org.apache.sis.referencing.AbstractReferenceSystem;
import org.geotoolkit.referencing.datum.DefaultEngineeringDatum;
import org.geotoolkit.referencing.cs.DefaultCoordinateSystemAxis;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.resources.Vocabulary;
import org.apache.sis.util.ComparisonMode;

import static org.geotoolkit.referencing.crs.UnprefixedMap.name;


/**
 * A contextually local coordinate reference system. It can be divided into two broad categories:
 * <p>
 * <ul>
 *   <li>earth-fixed systems applied to engineering activities on or near the surface of the
 *       earth;</li>
 *   <li>CRSs on moving platforms such as road vehicles, vessels, aircraft, or spacecraft.</li>
 * </ul>
 *
 * <TABLE CELLPADDING='6' BORDER='1'>
 * <TR BGCOLOR="#EEEEFF"><TH NOWRAP>Used with CS type(s)</TH></TR>
 * <TR><TD>
 *   {@link org.opengis.referencing.cs.CartesianCS    Cartesian},
 *   {@link org.opengis.referencing.cs.AffineCS       Affine},
 *   {@link org.opengis.referencing.cs.EllipsoidalCS  Ellipsoidal},
 *   {@link org.opengis.referencing.cs.SphericalCS    Spherical},
 *   {@link org.opengis.referencing.cs.CylindricalCS  Cylindrical},
 *   {@link org.opengis.referencing.cs.PolarCS        Polar},
 *   {@link org.opengis.referencing.cs.VerticalCS     Vertical},
 *   {@link org.opengis.referencing.cs.LinearCS       Linear}
 * </TD></TR></TABLE>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 1.2
 * @module
 *
 * @deprecated Moved to Apache SIS.
 */
@Deprecated
@Immutable
@XmlTransient
public class DefaultEngineeringCRS extends org.apache.sis.referencing.crs.DefaultEngineeringCRS {
    /**
     * A Cartesian local coordinate system.
     */
    private static final class Cartesian extends DefaultEngineeringCRS {
        /** Serial number for inter-operability with different versions. */
        private static final long serialVersionUID = -1773381554353809683L;

        /** Constructs a coordinate system with the given name. */
        public Cartesian(final int key, final CoordinateSystem cs) {
            super(name(key), DefaultEngineeringDatum.UNKNOWN, cs);
        }

        /**
         * Compares the specified object to this CRS for equality. This method is overridden
         * because, otherwise, {@code CARTESIAN_xD} and {@code GENERIC_xD} would be considered
         * equals when metadata are ignored.
         */
        @Override
        public boolean equals(final Object object, final ComparisonMode mode) {
            if (object instanceof EngineeringCRS && super.equals(object, mode)) {
                switch (mode) {
                    case STRICT:
                    case BY_CONTRACT: {
                        // No need to performs the check below if metadata were already compared.
                        return true;
                    }
                    default: {
                        final EngineeringCRS that = (EngineeringCRS) object;
                        return Objects.equals(getName().getCode(), that.getName().getCode());
                    }
                }
            }
            return false;
        }
    }

    /**
     * A two-dimensional Cartesian coordinate reference system with
     * {@linkplain DefaultCoordinateSystemAxis#X x},
     * {@linkplain DefaultCoordinateSystemAxis#Y y}
     * axes in {@linkplain SI#METRE metres}. By default, this CRS has no transformation
     * path to any other CRS (i.e. a map using this CS can't be reprojected to a
     * {@linkplain DefaultGeographicCRS geographic coordinate reference system} for example).
     */
    public static final DefaultEngineeringCRS CARTESIAN_2D =
            new Cartesian(Vocabulary.Keys.CARTESIAN_2D, DefaultCartesianCS.GENERIC_2D);

    /**
     * A three-dimensional Cartesian coordinate reference system with
     * {@linkplain DefaultCoordinateSystemAxis#X x},
     * {@linkplain DefaultCoordinateSystemAxis#Y y},
     * {@linkplain DefaultCoordinateSystemAxis#Z z}
     * axes in {@linkplain SI#METRE metres}. By default, this CRS has no transformation
     * path to any other CRS (i.e. a map using this CS can't be reprojected to a
     * {@linkplain DefaultGeographicCRS geographic coordinate reference system} for example).
     */
    public static final DefaultEngineeringCRS CARTESIAN_3D =
            new Cartesian(Vocabulary.Keys.CARTESIAN_3D, DefaultCartesianCS.GENERIC_3D);

    /**
     * A two-dimensional wildcard coordinate system with
     * {@linkplain DefaultCoordinateSystemAxis#X x},
     * {@linkplain DefaultCoordinateSystemAxis#Y y}
     * axes in {@linkplain SI#METRE metres}. At the difference of {@link #CARTESIAN_2D},
     * this coordinate system is treated specially by the default {@linkplain
     * org.geotoolkit.referencing.operation.DefaultCoordinateOperationFactory coordinate operation
     * factory} with loose transformation rules: if no transformation path were found (for example
     * through a {@linkplain DefaultDerivedCRS derived CRS}), then the transformation from this
     * CRS to any CRS with a compatible number of dimensions is assumed to be the identity
     * transform. This CRS is useful as a kind of wildcard when no CRS were explicitly specified.
     */
    public static final DefaultEngineeringCRS GENERIC_2D =
            new Cartesian(Vocabulary.Keys.GENERIC_CARTESIAN_2D, DefaultCartesianCS.GENERIC_2D);

    /**
     * A three-dimensional wildcard coordinate system with
     * {@linkplain DefaultCoordinateSystemAxis#X x},
     * {@linkplain DefaultCoordinateSystemAxis#Y y},
     * {@linkplain DefaultCoordinateSystemAxis#Z z}
     * axes in {@linkplain SI#METRE metres}. At the difference of {@link #CARTESIAN_3D},
     * this coordinate system is treated specially by the default {@linkplain
     * org.geotoolkit.referencing.operation.DefaultCoordinateOperationFactory coordinate operation
     * factory} with loose transformation rules: if no transformation path were found (for example
     * through a {@linkplain DefaultDerivedCRS derived CRS}), then the transformation from this
     * CRS to any CRS with a compatible number of dimensions is assumed to be the identity
     * transform. This CRS is useful as a kind of wildcard when no CRS were explicitly specified.
     */
    public static final DefaultEngineeringCRS GENERIC_3D =
            new Cartesian(Vocabulary.Keys.GENERIC_CARTESIAN_3D, DefaultCartesianCS.GENERIC_3D);

    /**
     * Constructs a new enginnering CRS with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param crs The CRS to copy.
     *
     * @since 2.2
     */
    public DefaultEngineeringCRS(final EngineeringCRS crs) {
        super(crs);
    }

    /**
     * Constructs an engineering CRS from a name.
     *
     * @param name The name.
     * @param datum The datum.
     * @param cs The coordinate system.
     */
    public DefaultEngineeringCRS(final String            name,
                                 final EngineeringDatum datum,
                                 final CoordinateSystem    cs)
    {
        this(Collections.singletonMap(NAME_KEY, name), datum, cs);
    }

    /**
     * Constructs an engineering CRS from a set of properties. The properties are given unchanged to
     * the {@linkplain AbstractReferenceSystem#AbstractReferenceSystem(Map) super-class constructor}.
     *
     * @param properties Set of properties. Should contains at least {@code "name"}.
     * @param datum The datum.
     * @param cs The coordinate system.
     */
    public DefaultEngineeringCRS(final Map<String,?> properties,
                                 final EngineeringDatum   datum,
                                 final CoordinateSystem      cs)
    {
        super(properties, datum, cs);
    }
}
