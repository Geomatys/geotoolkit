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

import java.util.Map;
import java.util.Collections;
import javax.measure.unit.Unit;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import net.jcip.annotations.Immutable;

import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem; // For javadoc
import org.opengis.referencing.cs.CartesianCS;
import org.opengis.referencing.cs.CoordinateSystem; // For javadoc
import org.opengis.referencing.datum.Ellipsoid;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.operation.Conversion;
import org.opengis.referencing.operation.Projection;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.geometry.MismatchedDimensionException;

import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.referencing.operation.DefaultProjection;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.geotoolkit.referencing.operation.DefaultOperationMethod;
import org.geotoolkit.referencing.operation.transform.AbstractMathTransform;
import org.apache.sis.internal.metadata.ReferencingUtilities;
import org.apache.sis.internal.referencing.WKTUtilities;
import org.apache.sis.io.wkt.Formatter;

import static org.apache.sis.internal.referencing.WKTUtilities.toFormattable;


/**
 * A 2D coordinate reference system used to approximate the shape of the earth on a planar surface.
 * It is done in such a way that the distortion that is inherent to the approximation is carefully
 * controlled and known. Distortion correction is commonly applied to calculated bearings and
 * distances to produce values that are a close match to actual field values.
 *
 * <TABLE CELLPADDING='6' BORDER='1'>
 * <TR BGCOLOR="#EEEEFF"><TH NOWRAP>Used with CS type(s)</TH></TR>
 * <TR><TD>
 *   {@link CartesianCS Cartesian}
 * </TD></TR></TABLE>
 *
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.19
 *
 * @since 1.2
 * @module
 */
@Immutable
@XmlRootElement(name = "ProjectedCRS")
public class DefaultProjectedCRS extends AbstractDerivedCRS implements ProjectedCRS {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -4502680112031773028L;

    /**
     * Name of the {@value} projection parameter, which is handled specially during WKT formatting.
     */
    private static final String SEMI_MAJOR = "semi_major", SEMI_MINOR = "semi_minor";

    /**
     * Constructs a new object in which every attributes are set to a default value.
     * <strong>This is not a valid object.</strong> This constructor is strictly
     * reserved to JAXB, which will assign values to the fields using reflexion.
     */
    private DefaultProjectedCRS() {
        this(org.geotoolkit.internal.referencing.NilReferencingObject.INSTANCE);
    }

    /**
     * Constructs a new projected CRS with the same values than the specified one.
     * This copy constructor provides a way to convert an arbitrary implementation into a
     * Geotk one or a user-defined one (as a subclass), usually in order to leverage
     * some implementation-specific API. This constructor performs a shallow copy,
     * i.e. the properties are not cloned.
     *
     * @param crs The coordinate reference system to copy.
     *
     * @since 2.2
     */
    public DefaultProjectedCRS(final ProjectedCRS crs) {
        super(crs);
    }

    /**
     * Constructs a projected CRS from a name. A {@linkplain DefaultOperationMethod default
     * operation method} is inferred from the {@linkplain AbstractMathTransform math transform}.
     * This is a convenience constructor that is not guaranteed to work reliably for non-Geotk
     * implementations. Use the constructor expecting a {@linkplain DefiningConversion defining
     * conversion} for more determinist result.
     *
     * @param  name The name.
     * @param  base Coordinate reference system to base the derived CRS on.
     * @param  baseToDerived The transform from the base CRS to returned CRS.
     * @param  derivedCS The coordinate system for the derived CRS. The number of axes
     *         must match the target dimension of the transform {@code baseToDerived}.
     * @throws MismatchedDimensionException if the source and target dimension of
     *         {@code baseToDeviced} don't match the dimension of {@code base}
     *         and {@code derivedCS} respectively.
     *
     * @since 2.5
     */
    public DefaultProjectedCRS(final String                 name,
                               final GeographicCRS          base,
                               final MathTransform baseToDerived,
                               final CartesianCS       derivedCS)
            throws MismatchedDimensionException
    {
        this(Collections.singletonMap(NAME_KEY, name), base, baseToDerived, derivedCS);
    }

    /**
     * Constructs a projected CRS from a set of properties. A {@linkplain DefaultOperationMethod
     * default operation method} is inferred from the {@linkplain AbstractMathTransform math transform}.
     * This is a convenience constructor that is not guaranteed to work reliably for non-Geotk
     * implementations. Use the constructor expecting a {@linkplain DefiningConversion defining
     * conversion} for more determinist result.
     * <p>
     * The properties are given unchanged to the
     * {@linkplain AbstractDerivedCRS#AbstractDerivedCRS(Map, CoordinateReferenceSystem,
     * MathTransform, CoordinateSystem) super-class constructor}.
     *
     * @param  properties Name and other properties to give to the new derived CRS object and to
     *         the underlying {@linkplain DefaultProjection projection}.
     * @param  base Coordinate reference system to base the derived CRS on.
     * @param  baseToDerived The transform from the base CRS to returned CRS.
     * @param  derivedCS The coordinate system for the derived CRS. The number of axes
     *         must match the target dimension of the transform {@code baseToDerived}.
     * @throws MismatchedDimensionException if the source and target dimension of
     *         {@code baseToDeviced} don't match the dimension of {@code base}
     *         and {@code derivedCS} respectively.
     *
     * @since 2.5
     */
    public DefaultProjectedCRS(final Map<String,?>    properties,
                               final GeographicCRS          base,
                               final MathTransform baseToDerived,
                               final CartesianCS       derivedCS)
            throws MismatchedDimensionException
    {
        super(properties, base, baseToDerived, derivedCS);
    }

    /**
     * Constructs a projected CRS from a {@linkplain DefiningConversion defining conversion}. The
     * properties are given unchanged to the {@linkplain AbstractDerivedCRS#AbstractDerivedCRS(Map,
     * Conversion, CoordinateReferenceSystem, MathTransform, CoordinateSystem) super-class constructor}.
     *
     * @param  properties Name and other properties to give to the new projected CRS object.
     * @param  conversionFromBase The {@linkplain DefiningConversion defining conversion}.
     * @param  base Coordinate reference system to base the projected CRS on.
     * @param  baseToDerived The transform from the base CRS to returned CRS.
     * @param  derivedCS The coordinate system for the projected CRS. The number of axes
     *         must match the target dimension of the transform {@code baseToDerived}.
     * @throws MismatchedDimensionException if the source and target dimension of
     *         {@code baseToDerived} don't match the dimension of {@code base}
     *         and {@code derivedCS} respectively.
     */
    public DefaultProjectedCRS(final Map<String,?>       properties,
                               final Conversion  conversionFromBase,
                               final GeographicCRS             base,
                               final MathTransform    baseToDerived,
                               final CartesianCS          derivedCS)
            throws MismatchedDimensionException
    {
        super(properties, conversionFromBase, base, baseToDerived, derivedCS);
    }

    /**
     * Returns a Geotk CRS implementation with the same values than the given arbitrary
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
    public static DefaultProjectedCRS castOrCopy(final ProjectedCRS object) {
        return (object == null) || (object instanceof DefaultProjectedCRS)
                ? (DefaultProjectedCRS) object : new DefaultProjectedCRS(object);
    }

    /**
     * Returns the GeoAPI interface implemented by this class.
     * The SIS implementation returns {@code ProjectedCRS.class}.
     *
     * {@note Subclasses usually do not need to override this method since GeoAPI does not define
     *        <code>ProjectedCRS</code> sub-interface. Overriding possibility is left mostly for
     *        implementors who wish to extend GeoAPI with their own set of interfaces.}
     *
     * @return {@code ProjectedCRS.class} or a user-defined sub-interface.
     */
    @Override
    public Class<? extends ProjectedCRS> getInterface() {
        return ProjectedCRS.class;
    }

    /**
     * Returns the coordinate system.
     */
    @Override
    @XmlElement(name="cartesianCS")
    public CartesianCS getCoordinateSystem() {
        return (CartesianCS) super.getCoordinateSystem();
    }

    /**
     * Used by JAXB only (invoked by reflection).
     *
     * @todo Temporary patch.
     */
    final void setCoordinateSystem(final CartesianCS cs) {
        try {
            final java.lang.reflect.Method method = org.apache.sis.referencing.crs.AbstractCRS.class.getMethod(
                    "setCoordinateSystem", String.class, CoordinateSystem.class);
            method.setAccessible(true);
            method.invoke("cartesianCS", cs);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Returns the datum.
     */
    @Override
    @XmlElement(name="geodeticDatum")
    public GeodeticDatum getDatum() {
        return (GeodeticDatum) super.getDatum();
    }

    /**
     * Used by JAXB only (invoked by reflection).
     */
    final void setDatum(final GeodeticDatum datum) {
        super.setDatum(datum);
    }

    /**
     * Returns the base coordinate reference system, which must be geographic.
     *
     * @return The base CRS.
     */
    @Override
    public GeographicCRS getBaseCRS() {
        return (GeographicCRS) super.getBaseCRS();
    }

    /**
     * Returns the map projection from the {@linkplain #getBaseCRS base CRS} to this CRS.
     *
     * @return The map projection from base CRS to this CRS.
     */
    @Override
    public Projection getConversionFromBase() {
        return (Projection) super.getConversionFromBase();
    }

    /**
     * Returns the expected type of conversion.
     */
    @Override
    Class<? extends Projection> getConversionType() {
        return Projection.class;
    }

    /**
     * Formats the inner part of a
     * <A HREF="http://www.geoapi.org/snapshot/javadoc/org/opengis/referencing/doc-files/WKT.html#PROJCS"><cite>Well
     * Known Text</cite> (WKT)</A> element.
     *
     * @param  formatter The formatter to use.
     * @return The name of the WKT element type, which is {@code "PROJCS"}.
     */
    @Override
    public String formatTo(final Formatter formatter) { // TODO: should be protected
        WKTUtilities.appendName(this, formatter, null);
        final Ellipsoid ellipsoid = getDatum().getEllipsoid();
        @SuppressWarnings({"unchecked","rawtypes"}) // Formatter.setLinearUnit(...) will do the check for us.
        final Unit<Length> unit        = (Unit) ReferencingUtilities.getUnit(super.getCoordinateSystem());
        final Unit<Angle>  geoUnit     = DefaultGeographicCRS.getAngularUnit(baseCRS.getCoordinateSystem());
        final Unit<Length> linearUnit  = formatter.addContextualUnit(unit);
        final Unit<Angle>  angularUnit = formatter.addContextualUnit(geoUnit);
        final Unit<Length> axisUnit    = ellipsoid.getAxisUnit();
        formatter.newLine();
        formatter.append(toFormattable(baseCRS));
        formatter.newLine();
        formatter.append((org.apache.sis.io.wkt.FormattableObject) conversionFromBase.getMethod()); // TODO
        formatter.newLine();
        for (final GeneralParameterValue param : conversionFromBase.getParameterValues().values()) {
            final GeneralParameterDescriptor desc = param.getDescriptor();
            String name;
            if (IdentifiedObjects.nameMatches(desc, name = SEMI_MAJOR) ||
                IdentifiedObjects.nameMatches(desc, name = SEMI_MINOR))
            {
                /*
                 * Do not format semi-major and semi-minor axis length in most cases,  since those
                 * informations are provided in the ellipsoid. An exception to this rule occurs if
                 * the lengths are different from the ones declared in the datum.
                 */
                if (param instanceof ParameterValue<?>) {
                    final double value = ((ParameterValue<?>) param).doubleValue(axisUnit);
                    final double expected = (name == SEMI_MINOR) ? // NOSONAR: using '==' is okay here.
                            ellipsoid.getSemiMinorAxis() : ellipsoid.getSemiMajorAxis();
                    if (value == expected) {
                        continue;
                    }
                }
            }
            WKTUtilities.append(param, formatter);
        }
        formatter.append(unit);
        final CartesianCS cs = getCoordinateSystem();
        final int dimension = cs.getDimension();
        for (int i=0; i<dimension; i++) {
            formatter.newLine();
            formatter.append(toFormattable(cs.getAxis(i)));
        }
        if (unit == null) {
            formatter.setInvalidWKT(this, null);
        }
        formatter.removeContextualUnit(unit);
        formatter.removeContextualUnit(geoUnit);
        formatter.addContextualUnit(angularUnit);
        formatter.addContextualUnit(linearUnit);
        formatter.newLine(); // For writing the ID[…] element on its own line.
        return "PROJCS";
    }
}
