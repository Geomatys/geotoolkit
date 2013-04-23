/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.image.io.metadata;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.NoSuchElementException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.opengis.parameter.*;
import org.opengis.referencing.*;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.util.FactoryException;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.DefaultReferenceIdentifier;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.geotoolkit.referencing.factory.ReferencingFactoryContainer;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.IndexedResourceBundle;
import org.apache.sis.internal.util.Citations;
import org.apache.sis.util.iso.Types;
import org.geotoolkit.internal.image.io.DataTypes;
import org.geotoolkit.util.Strings;
import org.apache.sis.util.NullArgumentException;
import org.geotoolkit.util.logging.Logging;
import org.geotoolkit.naming.DefaultNameSpace;
import org.geotoolkit.lang.Builder;

import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.GEOTK_FORMAT_NAME;


/**
 * Builds referencing objects from an {@link IIOMetadata} object. This class uses a
 * {@link MetadataNodeAccessor} for reading and writing the attribute values in the
 * {@link IIOMetadata} object given at construction time. By default, this class
 * uses an accessor for the {@code "RectifiedGridDomain/CoordinateReferenceSystem"}
 * node of the {@value org.geotoolkit.image.io.metadata.SpatialMetadataFormat#GEOTK_FORMAT_NAME}
 * format. However a different accessor can be given to the constructor.
 *
 * {@note This class exists because we do not use the reflection mechanism like what we do for
 *        ISO 19115-2 metadata. Dedicated code is needed because the mapping between Image I/O
 *        metadata and the referencing objects is more indirect. For example the kind of object
 *        to create depends on the value of the <code>"type"</code> attribute.}
 *
 * The main methods in this class are {@link #build()} for reading, and
 * {@link #setCoordinateReferenceSystem(CoordinateReferenceSystem) setCoordinateReferenceSystem(...)}
 * for writing. The other getter methods are provided mostly as hooks that subclasses can override.
 * The table below summarizes them:
 * <p>
 * <table border="1" cellspacing="0">
 *   <tr bgcolor="lightblue">
 *     <th nowrap>&nbsp;Class&nbsp;</th>
 *     <th nowrap>&nbsp;Getter&nbsp;</th>
 *     <th nowrap>&nbsp;Setter&nbsp;</th>
 *   </tr><tr>
 *     <td>&nbsp;{@link CoordinateReferenceSystem}&nbsp;</td>
 *     <td>&nbsp;{@link #build()}&nbsp;</td>
 *     <td>&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link CoordinateReferenceSystem}&nbsp;</td>
 *     <td>&nbsp;{@link #getCoordinateReferenceSystem(Class)}&nbsp;</td>
 *     <td>&nbsp;{@link #setCoordinateReferenceSystem(CoordinateReferenceSystem)}&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link CoordinateSystem}&nbsp;</td>
 *     <td>&nbsp;{@link #getCoordinateSystem(Class)}&nbsp;</td>
 *     <td>&nbsp;{@link #setCoordinateSystem(CoordinateSystem)}&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link Datum}&nbsp;</td>
 *     <td>&nbsp;{@link #getDatum(Class)}&nbsp;</td>
 *     <td>&nbsp;{@link #setDatum(Datum)}&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link Ellipsoid}&nbsp;</td>
 *     <td>&nbsp;{@link #getEllipsoid(MetadataNodeParser)}&nbsp;</td>
 *     <td>&nbsp;</td>
 *   </tr><tr>
 *     <td>&nbsp;{@link PrimeMeridian}&nbsp;</td>
 *     <td>&nbsp;{@link #getPrimeMeridian(MetadataNodeParser)}&nbsp;</td>
 *     <td>&nbsp;</td>
 *   </tr>
 * </table>
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.08 (derived from 3.07)
 * @module
 */
public class ReferencingBuilder extends Builder<CoordinateReferenceSystem> {
    /**
     * The default path to the CRS node.
     */
    static final String PATH = "RectifiedGridDomain/CoordinateReferenceSystem";

    /**
     * Small tolerance factor for comparisons of floating point numbers.
     */
    private static final double EPS = 1E-10;

    /**
     * {@code true} if the elements that are equal to the default value should be omitted.
     * This apply to write operations only.
     */
    private static final boolean OMMIT_DEFAULTS = true;

    /**
     * The factories to use for creating the referencing objects.
     * Will be created only when first needed.
     */
    private transient ReferencingFactoryContainer factories;

    /**
     * The metadata accessor for the {@code "CoordinateReferenceSystem"} node.
     * Must be an instance of {@link MetadataNodeAccessor} if setter methods will be invoked.
     */
    private final MetadataNodeParser accessor;

    /**
     * {@code true} if this helper should not {@linkplain MetadataNodeParser#getUserObject() get}
     * or {@linkplain MetadataNodeAccessor#setUserObject(Object) set} the user object property.
     * The default value is {@code false}.
     */
    private boolean ignoreUserObject;

    /**
     * Creates a new metadata helper for the given metadata.
     * The new {@code ReferencingBuilder} can be used for read and write operations.
     *
     * @param  metadata The Image I/O metadata. An instance of the {@link SpatialMetadata}
     *                  sub-class is recommended, but not mandatory.
     * @throws NoSuchElementException If the underlying {@code IIOMetadata}
     *         {@linkplain IIOMetadata#isReadOnly() is read only} and doesn't
     *         contains a node for the element to fetch.
     */
    public ReferencingBuilder(final IIOMetadata metadata) throws NoSuchElementException {
        this(new MetadataNodeAccessor(metadata, GEOTK_FORMAT_NAME, PATH, null));
    }

    /**
     * Creates a new metadata helper using the given accessor. Accessors for child elements
     * will be derived from the given accessor. Subclasses can control the name of child
     * elements by overriding the {@link #createNodeReader createNodeReader} and
     * {@link #createNodeWriter createNodeWriter} methods.
     *
     * @param accessor The accessor to the Coordinate Reference System node. Must be an instance
     *        of {@link MetadataNodeAccessor} if setter methods will be invoked.
     */
    public ReferencingBuilder(final MetadataNodeParser accessor) {
        this.accessor = accessor;
    }

    /**
     * Returns the factories to use for creating the referencing objects.
     */
    private ReferencingFactoryContainer factories() {
        if (factories == null) {
            factories = ReferencingFactoryContainer.instance(null);
        }
        return factories;
    }

    /**
     * Returns the user object of the given class, or {@code null} if none.
     *
     * @since 3.09
     */
    private <T extends IdentifiedObject> T getUserObject(final MetadataNodeParser accessor, final Class<T> type) {
        return (!ignoreUserObject) ? accessor.getUserObject(type) : null;
    }

    /**
     * Sets the user object in the given accessor, if this operation is supported.
     *
     * @since 3.09
     */
    private void setUserObject(final MetadataNodeAccessor accessor, final IdentifiedObject object) {
        if (!ignoreUserObject) try {
            accessor.setUserObject(object);
        } catch (UnsupportedOperationException e) {
            // The underlying node is not an instance of IIOMetadataNode.
            // Ignore without warning, since this operation is optional.
            Logging.recoverableException(MetadataNodeAccessor.LOGGER,
                    ReferencingBuilder.class, "setUserObject", e);
        }
    }

    /**
     * Returns {@code true} if this helper class should not
     * {@linkplain MetadataNodeParser#getUserObject() get} or
     * {@linkplain MetadataNodeAccessor#setUserObject(Object) set} the <cite>User Object</cite>
     * node property. The default value is {@code false}, in which case:
     * <p>
     * <ul>
     *   <li>Every call to a setter method in this {@code ReferencingBuilder} class will
     *       {@linkplain MetadataNodeAccessor#setUserObject(Object) set the user object} to
     *       the given value, if possible.</li>
     *   <li>Every call to a getter method in this {@code ReferencingBuilder} class will
     *       {@linkplain MetadataNodeParser#getUserObject() get the user object} and return
     *       it if it exist.</li>
     * </ul>
     * <p>
     * If this method returns {@code true}, then the above steps are skipped. This implies
     * that every call to a getter method will create a new object from the values declared
     * in node attributes.
     *
     * @return {@code true} if user objects should be ignored.
     *
     * @see javax.imageio.metadata.IIOMetadataNode#getUserObject()
     * @see MetadataNodeParser#getUserObject()
     *
     * @since 3.09
     */
    public boolean getIgnoreUserObject() {
        return ignoreUserObject;
    }

    /**
     * Sets whatever this helper class is allowed to
     * {@linkplain MetadataNodeParser#getUserObject() get} or
     * {@linkplain MetadataNodeAccessor#setUserObject(Object) set} the <cite>User Object</cite>
     * node property. See {@link #getIgnoreUserObject()} for more information.
     *
     * @param ignore {@code true} if user objects should be ignored.
     *
     * @since 3.09
     */
    public void setIgnoreUserObject(final boolean ignore) {
        ignoreUserObject = ignore;
    }

    /**
     * Returns the coordinate reference system, or {@code null} if it can not be created.
     * This method delegates to {@link #getCoordinateReferenceSystem(Class)} and catch the
     * exception. If an exception has been thrown, the exception is
     * {@linkplain MetadataNodeParser#warningOccurred logged} and this method returns {@code null}.
     *
     * @return The CRS, or {@code null} if none.
     *
     * @since 3.20 (derived from 3.07)
     */
    @Override
    public CoordinateReferenceSystem build() {
        Exception failure;
        try {
            return getCoordinateReferenceSystem(CoordinateReferenceSystem.class);
        } catch (FactoryException       |
                 NoSuchElementException | // Throws by MetadataNodeParser if an element is absents and IIOMetadata is read only.
                 NullArgumentException e) // Throws by 'isNonNull' (in this class) if a mandatory element is absents.
        {
            failure = e;
        }
        accessor.warning(null, getClass(), "build", failure);
        return null;
    }

    /**
     * Gets the coordinate reference system. If no CRS is explicitly defined, then a
     * {@linkplain MetadataNodeParser#warningOccurred warning is logged} and a
     * {@linkplain #getDefault(Class) default CRS} is returned, which is {@code null}
     * in the default implementation.
     *
     * @param  <T> The compile-time type of {@code baseType}.
     * @param  baseType The expected CRS type.
     * @return The coordinate reference system, or {@code null} if the CRS
     *         can not be parsed and there is no default value.
     * @throws FactoryException If the coordinate reference system can not be created.
     */
    public <T extends CoordinateReferenceSystem> T getCoordinateReferenceSystem(final Class<T> baseType)
            throws FactoryException
    {
        final T userObject = getUserObject(accessor, baseType);
        if (userObject != null) {
            return userObject;
        }
        if (!accessor.isEmpty()) {
            final Class<? extends CoordinateReferenceSystem> type =
                    getInterface("getCoordinateReferenceSystem", baseType, accessor);
            if (type != null) {
                final Map<String,?> properties = getName(accessor);
                final CRSFactory factory = factories().getCRSFactory();
                if (GeographicCRS.class.isAssignableFrom(type)) {
                    return baseType.cast(factory.createGeographicCRS(properties,
                            getDatum(GeodeticDatum.class),
                            getCoordinateSystem(EllipsoidalCS.class)));
                } else if (ProjectedCRS.class.isAssignableFrom(type)) {
                    final GeographicCRS baseCRS = factory.createGeographicCRS(
                            Collections.singletonMap(GeographicCRS.NAME_KEY, untitled(accessor)),
                            getDatum(GeodeticDatum.class), DefaultEllipsoidalCS.GEODETIC_2D);
                    final CartesianCS derivedCS = getCoordinateSystem(CartesianCS.class);
                    return baseType.cast(factory.createProjectedCRS(properties, baseCRS,
                            getConversionFromBase(baseCRS, derivedCS), derivedCS));
                } else {
                    // TODO: test for other types of CRS here (VerticalCRS, etc.)
                    warning("getCoordinateReferenceSystem", Errors.Keys.UNKNOWN_TYPE_1, type);
                }
            }
        }
        return getDefault("getCoordinateReferenceSystem", accessor, baseType);
    }

    /**
     * Returns the defining conversion from the base geographic CRS to the projected CRS.
     * If no coordinate system is explicitly defined, then a
     * {@linkplain MetadataNodeParser#warningOccurred warning is logged} and a
     * {@linkplain #getDefault(Class) default conversion} is returned.
     *
     * @return The conversion from geographic to projected CRS, or {@code null}.
     * @throws FactoryException If the conversion can not be created.
     */
    private Conversion getConversionFromBase(final CoordinateReferenceSystem baseCRS,
            final CoordinateSystem derivedCS) throws FactoryException
    {
        final MetadataNodeParser cvAccessor = createNodeReader(accessor, "Conversion", null);
        final String method = cvAccessor.getAttribute("method");
        if (isNonNull("getBaseToCRS", "method", method)) {
            final Map<String,?>       properties = getName(cvAccessor);
            final MathTransformFactory   factory = factories().getMathTransformFactory();
            final ParameterValueGroup parameters = factory.getDefaultParameters(method);
            try {
                final MetadataNodeParser paramAccessor = createNodeReader(cvAccessor, "Parameters", "ParameterValue");
                final int numParam = paramAccessor.childCount();
                for (int i=0; i<numParam; i++) {
                    paramAccessor.selectChild(i);
                    final String name  = paramAccessor.getAttribute("name");
                    if (isNonNull("getBaseToCRS", "name", name)) {
                        final Double value = paramAccessor.getAttributeAsDouble("value");
                        if (isNonNull("getBaseToCRS", "value", value)) try {
                            parameters.parameter(name).setValue(value.doubleValue());
                        } catch (IllegalArgumentException e) {
                            paramAccessor.warning(null, getClass(), "getBaseToCRS", e);
                        }
                    }
                }
            } catch (NoSuchElementException e) {
                // May happen if there is no "Parameters" node, for
                // example because all parameters have their default value.
                accessor.warning(null, getClass(), "getConversionFromBase", e);
            }
            final MathTransform tr = factory.createBaseToDerived(baseCRS, parameters, derivedCS);
            return new DefiningConversion(properties, factory.getLastMethodUsed(), tr);
        }
        return getDefault("getBaseToCRS", cvAccessor, Conversion.class);
    }

    /**
     * Sets the coordinate reference system to the given value.
     *
     * @param crs The coordinate reference system.
     *
     * @todo The base CRS is not yet declared for the {@code DerivedCRS} case.
     */
    public void setCoordinateReferenceSystem(final CoordinateReferenceSystem crs) {
        final MetadataNodeAccessor accessor;
        try {
            accessor = (MetadataNodeAccessor) this.accessor;
        } catch (ClassCastException e) {
            // We catch the ClassCastException rather than performing an instanceof check in order
            // to help the developer to see which instance was expected in the "caused by" part.
            throw new UnsupportedOperationException(this.accessor.getErrorResources()
                    .getString(Errors.Keys.UNMODIFIABLE_METADATA), e);
        }
        setName(crs, accessor);
        accessor.setAttribute("type", DataTypes.getType(crs));
        final Datum datum = CRS.getDatum(crs);
        if (datum != null) {
            setDatum(datum);
        }
        final CoordinateSystem cs = crs.getCoordinateSystem();
        if (cs != null) {
            setCoordinateSystem(cs);
        }
        /*
         * For ProjectedCRS, the baseCRS is implicitly a GeographicCRS with the same datum.
         * For other kind of DerivedCRS, we need to declare the baseCRS (TODO).
         */
        if (crs instanceof GeneralDerivedCRS) {
            final Conversion conversion = ((GeneralDerivedCRS) crs).getConversionFromBase();
            final MetadataNodeAccessor opAccessor = createNodeWriter(accessor, "Conversion", null);
            setName(conversion, opAccessor);
            setName(conversion.getMethod(), false, opAccessor, "method");
            addParameter(new MetadataNodeAccessor[] {opAccessor, null}, conversion.getParameterValues(),
                    CRS.getEllipsoid(crs));
        }
        setUserObject(accessor, crs);
    }

    /**
     * Adds the given parameter value using the given accessor. If the parameter value is actually
     * a {@link ParameterValueGroup}, then its child are added recursively.
     * <p>
     * In order to keep the metadata simpler, this method omits some parameters that are equal
     * to the default value. In order to reduce the risk of error, we omits a parameter only if
     * its default value is 0, or 1 in the particular case of the scale factor.
     *
     * @param accessors An array of length 2 where the first element is the accessor for the
     *        {@link Conversion} element. The second element will be created by this method
     *        when first needed, in order to create a {@code "Parameters"} element only if
     *        there is at least one parameter to write.
     * @param param The parameter or group of parameters to add.
     * @param ellipsoid The ellipsoid defined in the datum, or {@code null} if none.
     */
    private void addParameter(final MetadataNodeAccessor[] accessors,
            final GeneralParameterValue param, final Ellipsoid ellipsoid)
    {
        if (param instanceof ParameterValueGroup) {
            for (final GeneralParameterValue p : ((ParameterValueGroup) param).values()) {
                addParameter(accessors, p, ellipsoid);
            }
        }
        if (param instanceof ParameterValue<?>) {
            final ParameterValue<?> pv = (ParameterValue<?>) param;
            final Object value = pv.getValue();
            if (value != null) {
                final ParameterDescriptor<?> descriptor = pv.getDescriptor();
                final String name = descriptor.getName().getCode().trim();
                if (value instanceof Number) {
                    /*
                     * Check if we should skip this value (see the method javadoc for more details).
                     * Note that the omission of values equal to the default values can be disabled,
                     * but not the omission of ellipsoid value. This is for consistency with WKT
                     * formatting.
                     */
                    final double numericValue = ((Number) value).doubleValue();
                    if (ellipsoid != null) {
                        if (name.equalsIgnoreCase("semi_major")) {
                            if (equals(numericValue, ellipsoid.getSemiMajorAxis())) {
                                return;
                            }
                        } else if (name.equalsIgnoreCase("semi_minor")) {
                            if (equals(numericValue, ellipsoid.getSemiMinorAxis())) {
                                return;
                            }
                        }
                    }
                    if (OMMIT_DEFAULTS) {
                        final Object defaultValue = descriptor.getDefaultValue();
                        if (defaultValue instanceof Number) {
                            final double df = ((Number) defaultValue).doubleValue();
                            if (equals(numericValue, df)) {
                                if (df == (name.equalsIgnoreCase("scale_factor") ? 1 : 0)) {
                                    return;
                                }
                            }
                        }
                    }
                }
                MetadataNodeAccessor accessor = accessors[1];
                if (accessor == null) {
                    accessor = createNodeWriter(accessors[0], "Parameters", "ParameterValue");
                    accessors[1] = accessor;
                }
                accessor.selectChild(accessor.appendChild());
                accessor.setAttribute("name", name);
                accessor.setAttribute("value", value.toString());
            }
        }
    }

    /**
     * Gets the coordinate system. If no coordinate system is explicitly defined, then a
     * {@linkplain MetadataNodeParser#warningOccurred warning is logged} and a
     * {@linkplain #getDefault(Class) default coordinate system} is returned,
     * which is {@link org.geotoolkit.referencing.cs.DefaultEllipsoidalCS#GEODETIC_2D},
     * {@link org.geotoolkit.referencing.cs.DefaultCartesianCS#GENERIC_2D} or {@code null}
     * (depending on the {@code baseType} argument) in the default implementation.
     *
     * @param  <T> The compile-time type of {@code baseType}.
     * @param  baseType The expected coordinate system type.
     * @return The coordinate system, or {@code null} if the coordinate system
     *         can not be parsed and there is no default value.
     * @throws FactoryException If the coordinate system can not be created.
     */
    @SuppressWarnings("fallthrough")
    public <T extends CoordinateSystem> T getCoordinateSystem(final Class<T> baseType)
            throws FactoryException
    {
        final MetadataNodeParser csAccessor = createNodeReader(accessor, "CoordinateSystem", null);
        final T userObject = getUserObject(csAccessor, baseType);
        if (userObject != null) {
            return userObject;
        }
        final Class<? extends CoordinateSystem> type = getInterface("getCoordinateSystem", baseType, csAccessor);
        if (type != null) {
            final Map<String,?> properties = getName(csAccessor);
            final Integer dimension = csAccessor.getAttributeAsInteger("dimension");
            final MetadataNodeParser axesAccessor = createNodeReader(csAccessor, "Axes", "CoordinateSystemAxis");
            final int numAxes = axesAccessor.childCount();
            if (dimension != null && dimension != numAxes) {
                warning("getCoordinateSystem", Errors.Keys.MISMATCHED_DIMENSION_3,
                        new Object[] {"Axes", numAxes, dimension});
            }
            final CoordinateSystemAxis[] axes = new CoordinateSystemAxis[numAxes];
            final CSFactory factory = factories().getCSFactory();
            for (int i=0; i<numAxes; i++) {
                axesAccessor.selectChild(i);
                final Map<String,?> axesProperties = getName(axesAccessor);
                String abbreviation = axesAccessor.getAttribute("axisAbbrev");
                final AxisDirection direction = axesAccessor.getAttributeAsCode("direction", AxisDirection.class);
                if (!isNonNull("getCoordinateSystem", "direction", direction)) {
                    return null;
                }
                if (abbreviation == null) {
                    /*
                     * If no abbreviation has been explicitly specified, use the first letter of the
                     * name. Note that if non-null, the name is guaranteed to have a length greater
                     * than 0 has of MetadataNodeParser.getAttribute(String) method implementation.
                     */
                    abbreviation = axesProperties.get(IdentifiedObject.NAME_KEY).toString();
                    if (abbreviation == null) {
                        abbreviation = Types.getCodeName(direction);
                    }
                    abbreviation = Strings.camelCaseToAcronym(abbreviation);
                }
                final Unit<?> unit = axesAccessor.getAttributeAsUnit("unit", null);
                if (!isNonNull("getCoordinateSystem", "unit", unit)) {
                    return null;
                }
                axes[i] = factory.createCoordinateSystemAxis(axesProperties, abbreviation, direction, unit);
            }
            /*
             * At this point, we have created the set of axes.
             * Now create the coordinate system.
             */
            Boolean isEllipsoidal = null;
            if (EllipsoidalCS.class.isAssignableFrom(type)) {
                isEllipsoidal = Boolean.TRUE;
            } else if (CartesianCS.class.isAssignableFrom(type)) {
                isEllipsoidal = Boolean.FALSE;
            }
            if (isEllipsoidal != null) {
                switch (numAxes) {
                    case 0: // Fall through
                    case 1: {
                        warning("getCoordinateSystem", Errors.Keys.NOT_TWO_DIMENSIONAL_1, numAxes);
                        break;
                    }
                    case 2: {
                        return baseType.cast(isEllipsoidal ?
                                factory.createEllipsoidalCS(properties, axes[0], axes[1]) :
                                factory.createCartesianCS  (properties, axes[0], axes[1]));
                    }
                    default: {
                        warning("getCoordinateSystem", Errors.Keys.UNEXPECTED_DIMENSION_FOR_CS_1, type);
                        // Fall through
                    }
                    case 3: {
                        return baseType.cast(isEllipsoidal ?
                                factory.createEllipsoidalCS(properties, axes[0], axes[1], axes[2]) :
                                factory.createCartesianCS  (properties, axes[0], axes[1], axes[2]));
                    }
                }
            } else {
                // TODO: test for other types of CS here (VerticalCS, etc.)
                warning("getCoordinateSystem", Errors.Keys.UNKNOWN_TYPE_1, type);
            }
        }
        return getDefault("getCoordinateSystem", csAccessor, baseType);
    }

    /**
     * Sets the coordinate system to the given value.
     *
     * @param cs The coordinate system, or {@code null} if none.
     */
    public void setCoordinateSystem(final CoordinateSystem cs) {
        final MetadataNodeAccessor csAccessor = createNodeWriter(accessor, "CoordinateSystem", null);
        setName(cs, csAccessor);
        csAccessor.setAttribute("type", DataTypes.getType(cs));
        final int dimension = cs.getDimension();
        csAccessor.setAttribute("dimension", dimension);
        final MetadataNodeAccessor axes = createNodeWriter(csAccessor, "Axes", "CoordinateSystemAxis");
        for (int i=0; i<dimension; i++) {
            final CoordinateSystemAxis axis = cs.getAxis(i);
            axes.selectChild(axes.appendChild());
            setName(axis, axes);
            final String abbreviation = axis.getAbbreviation();
            if (!abbreviation.equals(axis.getName().getCode())) {
                axes.setAttribute("axisAbbrev", abbreviation);
            }
            axes.setAttribute("direction", axis.getDirection());
            boolean hasRangeMeaning = false;
            double value = axis.getMinimumValue();
            if (value > Double.NEGATIVE_INFINITY) {
                axes.setAttribute("minimumValue", value);
                hasRangeMeaning = true;
            }
            value = axis.getMaximumValue();
            if (value < Double.POSITIVE_INFINITY) {
                axes.setAttribute("maximumValue", value);
                hasRangeMeaning = true;
            }
            if (hasRangeMeaning) {
                axes.setAttribute("rangeMeaning", axis.getRangeMeaning());
            }
            axes.setAttribute("unit", axis.getUnit());
            setUserObject(axes, axis);
        }
        setUserObject(csAccessor, cs);
    }

    /**
     * Gets the datum. If no datum is explicitly defined, then a
     * {@linkplain MetadataNodeParser#warningOccurred warning is logged} and a
     * {@linkplain #getDefault(Class) default datum} is returned,
     * which is {@link org.geotoolkit.referencing.datum.DefaultGeodeticDatum#WGS84}
     * in the default implementation.
     *
     * @param  <T> The compile-time type of {@code baseType}.
     * @param  baseType The expected datum type.
     * @return The datum, or {@code null} if the datum can not be parsed
     *         and there is no default value.
     * @throws FactoryException If the datum can not be created.
     *
     * @todo {@code VerticalDatum}, {@code TemporalDatum} and {@code ImageDatum} are not yet
     *       implemented.
     */
    public <T extends Datum> T getDatum(final Class<T> baseType) throws FactoryException {
        final MetadataNodeParser datumAccessor = createNodeReader(accessor, "Datum", null);
        final T userObject = getUserObject(datumAccessor, baseType);
        if (userObject != null) {
            return userObject;
        }
        final Class<? extends Datum> type = getInterface("getDatum", baseType, datumAccessor);
        if (type != null) {
            final Map<String,?> properties = getName(datumAccessor);
            final DatumFactory factory = factories().getDatumFactory();
            if (GeodeticDatum.class.isAssignableFrom(type)) {
                final Ellipsoid ellipsoid = getEllipsoid(datumAccessor);
                final PrimeMeridian pm = getPrimeMeridian(datumAccessor);
                return baseType.cast(factory.createGeodeticDatum(properties, ellipsoid, pm));
            } else if (EngineeringDatum.class.isAssignableFrom(type)) {
                return baseType.cast(factory.createEngineeringDatum(properties));
            } else {
                warning("getDatum", Errors.Keys.UNKNOWN_TYPE_1, type);
            }
        }
        return getDefault("getDatum", datumAccessor, baseType);
    }

    /**
     * Sets the datum to the given value.
     *
     * @param datum The datum, or {@code null} if none.
     */
    public void setDatum(final Datum datum) {
        final MetadataNodeAccessor datumAccessor = createNodeWriter(accessor, "Datum", null);
        setName(datum, datumAccessor);
        datumAccessor.setAttribute("type", DataTypes.getType(datum));
        if (datum instanceof GeodeticDatum) {
            final GeodeticDatum gd = (GeodeticDatum) datum;
            final Ellipsoid ellipsoid = gd.getEllipsoid();
            if (ellipsoid != null) {
                final MetadataNodeAccessor child = createNodeWriter(datumAccessor, "Ellipsoid", null);
                setName(ellipsoid, child);
                child.setAttribute("axisUnit", ellipsoid.getAxisUnit());
                child.setAttribute("semiMajorAxis", ellipsoid.getSemiMajorAxis());
                if (ellipsoid.isIvfDefinitive()) {
                    child.setAttribute("inverseFlattening", ellipsoid.getInverseFlattening());
                } else {
                    child.setAttribute("semiMinorAxis", ellipsoid.getSemiMinorAxis());
                }
                setUserObject(child, ellipsoid);
            }
            final PrimeMeridian pm = gd.getPrimeMeridian();
            if (pm != null) {
                final MetadataNodeAccessor child = createNodeWriter(datumAccessor, "PrimeMeridian", null);
                setName(pm, child);
                child.setAttribute("greenwichLongitude", pm.getGreenwichLongitude());
                child.setAttribute("angularUnit", pm.getAngularUnit());
                setUserObject(child, pm);
            }
        }
        setUserObject(datumAccessor, datum);
    }

    /**
     * Gets the ellipsoid. If no ellipsoid is explicitly defined, then a
     * {@linkplain MetadataNodeParser#warningOccurred warning is logged} and a
     * {@linkplain #getDefault(Class) default ellipsoid} is returned,
     * which is {@link org.geotoolkit.referencing.datum.DefaultEllipsoid#WGS84}
     * in the default implementation.
     *
     * @param  datumAccessor The accessor of the datum enclosing the ellipsoid.
     * @return The ellipsoid, or {@code null} if the ellipsoid can not be parsed
     *         and there is no default value.
     * @throws FactoryException If the ellipsoid can not be created.
     */
    protected Ellipsoid getEllipsoid(final MetadataNodeParser datumAccessor) throws FactoryException {
        final MetadataNodeParser ellipsoidAccessor = createNodeReader(datumAccessor, "Ellipsoid", null);
        final Ellipsoid userObject = getUserObject(ellipsoidAccessor, Ellipsoid.class);
        if (userObject != null) {
            return userObject;
        }
        final Unit<Length> unit = ellipsoidAccessor.getAttributeAsUnit("axisUnit", Length.class);
        if (isNonNull("getEllipsoid", "axisUnit", unit)) {
            final Map<String,?> properties = getName(ellipsoidAccessor);
            final Double semiMajor = ellipsoidAccessor.getAttributeAsDouble("semiMajorAxis");
            if (isNonNull("getEllipsoid", "semiMajorAxis", semiMajor)) {
                final DatumFactory factory = factories().getDatumFactory();
                final Double semiMinor = ellipsoidAccessor.getAttributeAsDouble("semiMinorAxis");
                if (semiMinor != null) {
                    return factory.createEllipsoid(properties, semiMajor, semiMinor, unit);
                }
                final Double ivf = ellipsoidAccessor.getAttributeAsDouble("inverseFlattening");
                if (isNonNull("getEllipsoid", "inverseFlattening", ivf)) {
                    return factory.createFlattenedSphere(properties, semiMajor, ivf, unit);
                }
            }
        }
        return getDefault("getEllipsoid", ellipsoidAccessor, Ellipsoid.class);
    }

    /**
     * Gets the prime meridian. If no prime meridian is explicitly defined, then a
     * {@linkplain MetadataNodeParser#warningOccurred warning is logged} and a
     * {@linkplain #getDefault(Class) default prime meridian} is returned,
     * which is {@link org.geotoolkit.referencing.datum.DefaultPrimeMeridian#GREENWICH}
     * in the default implementation.
     *
     * @param  datumAccessor The accessor of the datum enclosing the prime meridian.
     * @return The prime meridian, or {@code null} if the prime meridian can not be
     *         parsed and there is no default value.
     * @throws FactoryException If the prime meridian can not be created.
     */
    protected PrimeMeridian getPrimeMeridian(final MetadataNodeParser datumAccessor) throws FactoryException {
        final MetadataNodeParser pmAccessor = createNodeReader(datumAccessor, "PrimeMeridian", null);
        final PrimeMeridian userObject = getUserObject(pmAccessor, PrimeMeridian.class);
        if (userObject != null) {
            return userObject;
        }
        final Double greenwich = pmAccessor.getAttributeAsDouble("greenwichLongitude");
        if (isNonNull("getEllipsoid", "greenwichLongitude", greenwich)) {
            final Map<String,?> properties = getName(pmAccessor);
            final Unit<Angle> unit = pmAccessor.getAttributeAsUnit("angularUnit", Angle.class);
            if (isNonNull("getPrimeMeridian", "angularUnit", unit)) {
                final DatumFactory factory = factories().getDatumFactory();
                return factory.createPrimeMeridian(properties, greenwich, unit);
            }
        }
        return getDefault("getPrimeMeridian", pmAccessor, PrimeMeridian.class);
    }

    /**
     * Returns a default object of the given class. This method is invoked automatically
     * when the object was not explicitly defined in the metadata, or can not be parsed.
     * <p>
     * The default implementation delegates to {@link SpatialMetadataFormat#getDefaultValue(Class)}
     * for every types except {@link CoordinateReferenceSystem}. The later method is preferred to
     * {@link IIOMetadataFormat#getObjectDefaultValue(String)} because the default value may depend
     * on the {@code "type"} attribute in the enclosing element. For example if the CRS type is
     * {@code "geographic"}, then the default coordinate system shall be a {@link EllipsoidalCS}.
     * But if the CRS type is {@code "projected"} instead, then the default coordinate system shall
     * rather be a {@link CartesianCS}.
     * <p>
     * Subclasses can override this method if they want to provide different default values.
     *
     * @param  <T>  The compile-time type of the {@code type} argument.
     * @param  type The type of the object to be returned.
     * @return The default object of the given type, or {@code null} if none.
     * @throws FactoryException If the default object can not be created.
     *
     * @see SpatialMetadataFormat#getDefaultValue(Class)
     * @see IIOMetadataFormat#getObjectDefaultValue(String)
     */
    protected <T extends IdentifiedObject> T getDefault(final Class<T> type) throws FactoryException {
        if (!CoordinateReferenceSystem.class.isAssignableFrom(type)) {
            if (accessor.format instanceof SpatialMetadataFormat) {
                return ((SpatialMetadataFormat) accessor.format).getDefaultValue(type);
            }
        }
        return null;
    }

    /**
     * Returns a default object of the given class. This method logs a warning telling that the
     * returned object is used as a fallback. The default implementation delegates to the first
     * of the following methods which return a non-null default value:
     * <p>
     * <ul>
     *   <li>{@link SpatialMetadataFormat#getDefaultValue(Class)}</li>
     *   <li>{@link IIOMetadataFormat#getObjectDefaultValue(String)}</li>
     * </ul>
     */
    private <T extends IdentifiedObject> T getDefault(final String method,
            final MetadataNodeParser accessor, final Class<T> type) throws FactoryException
    {
        T object = getDefault(type);
        if (object == null) {
            object = type.cast(accessor.format.getObjectDefaultValue(accessor.name()));
        }
        if (object != null) {
            warning(method, Loggings.getResources(accessor.getLocale()),
                    Loggings.Keys.USING_FALLBACK_1, object.getName());
        }
        return object;
    }

    /**
     * Creates a read-only accessor for a child element. This method is invoked automatically when
     * a new accessor needs to be created for a child element, for example the {@code "Datum"}
     * element inside the {@code "CoordinateReferenceSystem"} element.
     * <p>
     * The default implementation is as below:
     *
     * {@preformat java
     *     return new MetadataNodeAccessor(parent, path, childPath);
     * }
     *
     * Subclasses can override this method in order to create different accessors,
     * for example in order to use different names for the child elements.
     *
     * @param parent    The accessor for which the {@code path} is relative.
     * @param path      The path to the node of interest.
     * @param childPath The path to the child elements, or {@code null} if none.
     * @return The accessor to use.
     *
     * @see MetadataNodeParser#MetadataNodeParser(MetadataNodeParser, String, String)
     *
     * @since 3.20
     */
    protected MetadataNodeParser createNodeReader(final MetadataNodeParser parent,
            final String path, final String childPath)
    {
        return new MetadataNodeParser(parent, path, childPath);
    }

    /**
     * Creates the read/write accessor for a child element. This method is invoked automatically
     * when a new accessor needs to be created for a child element, for example the {@code "Datum"}
     * element inside the {@code "CoordinateReferenceSystem"} element.
     * <p>
     * The default implementation is as below:
     *
     * {@preformat java
     *     return new MetadataNodeAccessor(parent, path, childPath);
     * }
     *
     * Subclasses can override this method in order to create different accessors,
     * for example in order to use different names for the child elements.
     *
     * @param parent    The accessor for which the {@code path} is relative.
     * @param path      The path to the node of interest.
     * @param childPath The path to the child elements, or {@code null} if none.
     * @return The accessor to use.
     *
     * @see MetadataNodeAccessor#MetadataNodeAccessor(MetadataNodeParser, String, String)
     *
     * @since 3.20
     */
    protected MetadataNodeAccessor createNodeWriter(final MetadataNodeParser parent,
            final String path, final String childPath)
    {
        return new MetadataNodeAccessor(parent, path, childPath);
    }

    /**
     * Returns the interface for the {@code "type"} attribute of the given accessor, or
     * {@code baseType} if unknown. In the later case, a warning message will be emitted.
     *
     * @param  <T>      The compile-time type of the {@code baseType} argument.
     * @param  method   The name of the method invoking this one (for logging purpose only).
     * @param  baseType {@link CoordinateReferenceSystem}, {@link CoordinateSystem} or {@link Datum}.
     * @param  accessor The accessor from which to get the value of the {@code "type"} attribute.
     * @return The value of the {@code "type"} attribute as an interface, or {@code baseType}.
     */
    private <T extends IdentifiedObject> Class<? extends T> getInterface(final String method,
            final Class<T> baseType, final MetadataNodeParser accessor)
    {
        final String type = accessor.getAttribute("type");
        if (type == null) {
            /*
             * If the type was not specified, log a warning only if the type was not
             * already known anyway. It may be known because some types of CRS accepts
             * only one specific type of datum or coordinate system.
             */
            if (baseType.equals(Datum.class) || baseType.equals(CoordinateSystem.class)) {
                warning(method, Errors.Keys.NO_PARAMETER_VALUE_1, "type");
            }
        } else try {
            // Following line may throw a ClassCastException (as of method contract).
            final Class<? extends T> classe = DataTypes.getInterface(baseType, type);
            if (classe != null) {
                return classe;
            }
            warning(method, Errors.Keys.UNKNOWN_TYPE_1, type);
        } catch (ClassCastException e) {
            warning(method, Errors.Keys.ILLEGAL_CLASS_2, new Object[] {type, baseType});
        }
        return baseType;
    }

    /**
     * Gets the {@code "name"} attribute from the given accessor.
     * If this attribute is not found, then a default name is generated.
     *
     * @param  accessor The accessor to use for getting the name attribute.
     * @return A map containing the name attribute.
     */
    private static Map<String,Object> getName(final MetadataNodeParser accessor) {
        String name = accessor.getAttribute("name");
        if (name == null) {
            name = untitled(accessor);
        } else {
            final int s = name.indexOf(DefaultNameSpace.DEFAULT_SEPARATOR);
            if (s >= 0) {
                final String authority = name.substring(0, s).trim();
                name = name.substring(s + 1).trim();
                if (name.isEmpty()) {
                    name = authority;
                } else if (!authority.isEmpty()) {
                    final Map<String,Object> properties = new HashMap<>(6);
                    properties.put(ReferenceIdentifier.CODESPACE_KEY, authority);
                    properties.put(ReferenceIdentifier.CODE_KEY, name);
                    final ReferenceIdentifier id = new DefaultReferenceIdentifier(properties);
                    return Collections.<String,Object>singletonMap(IdentifiedObject.NAME_KEY, id);
                }
            }
        }
        return Collections.<String,Object>singletonMap(IdentifiedObject.NAME_KEY, name);
    }

    /**
     * Sets the {@code "name"} attribute for the given object.
     *
     * @param object    The object from which to fetch the name.
     * @param accessor  The accessor to use for setting the name attribute.
     */
    private static void setName(final IdentifiedObject object, final MetadataNodeAccessor accessor) {
        setName(object, true, accessor, "name");
    }

    /**
     * Same as {@link #setName}, but uses the given attribute name instead than {@code "name"}.
     *
     * @param object    The object from which to fetch the name.
     * @param scoped    {@code true} if the name should contains the authority prefix.
     * @param accessor  The accessor to use for setting the name attribute.
     * @param attribute The attribute name ({@code "name"} by default).
     */
    private static void setName(final IdentifiedObject object, final boolean scoped,
            final MetadataNodeAccessor accessor, final String attribute)
    {
        final ReferenceIdentifier id = object.getName();
        if (id != null) {
            String name = id.getCode();
            if (scoped) {
                final String authority = Citations.getIdentifier(id.getAuthority());
                if (authority != null) {
                    name = authority + DefaultNameSpace.DEFAULT_SEPARATOR + name;
                }
            }
            accessor.setAttribute(attribute, name);
        }
    }

    /**
     * Returns {@code "untitled"} in the locale of the given accessor.
     */
    private static String untitled(final MetadataNodeParser accessor) {
        return Vocabulary.getResources(accessor.getLocale()).getString(Vocabulary.Keys.UNTITLED);
    }

    /**
     * Returns {@code true} if the given value is equals to the expected one,
     * accepting a tolerance interval.
     */
    private static boolean equals(final double actual, final double expected) {
        return Math.abs(actual - expected) <= Math.abs(expected)*EPS;
    }

    /**
     * Returns {@code true} if the given object is non-null.
     * Otherwise emmits a warning and returns {@code false}.
     */
    private boolean isNonNull(final String method, final String attribute, final Object value) {
        if (value != null) {
            return true;
        }
        warning(method, Errors.Keys.NO_PARAMETER_VALUE_1, attribute);
        return false;
    }

    /**
     * Convenience method for logging a warning using the error resource bundle.
     */
    private void warning(final String method, final int key, final Object value) {
        warning(method, Errors.getResources(accessor.getLocale()), key, value);
    }

    /**
     * Convenience method for logging a warning using the given resource bundle.
     */
    private void warning(final String method, final IndexedResourceBundle resources,
            final int key, final Object value)
    {
        accessor.warning(getClass(), method, resources, key, value);
    }
}
