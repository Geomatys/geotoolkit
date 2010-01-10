/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataFormat;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.datum.*;
import org.opengis.referencing.operation.*;
import org.opengis.parameter.ParameterValueGroup;

import org.geotoolkit.referencing.DefaultReferenceIdentifier;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.geotoolkit.referencing.factory.ReferencingFactoryContainer;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.Vocabulary;
import org.geotoolkit.resources.IndexedResourceBundle;
import org.geotoolkit.internal.StringUtilities;
import org.geotoolkit.internal.image.io.MetadataEnum;
import org.geotoolkit.naming.DefaultNameSpace;
import org.geotoolkit.util.NullArgumentException;

import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.FORMAT_NAME;


/**
 * Builds referencing objects from a {@link IIOMetadata} object. The main method is
 * {@link #getOptionalCRS()}. The other methods are hooks for overriding by subclasses.
 *
 * {@note We do not use the reflection mechanism like what we do for ISO 19115-2 metadata,
 *        because the mapping between Image I/O metadata and the referencing objects is more
 *        indirect. For example the kind of object to create depends on the value of the
 *        <code>"type"</code> attribute.}
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.08
 *
 * @since 3.07
 * @module
 */
public class ReferencingMetadataHelper extends MetadataHelper {
    /**
     * The factories to use for creating the referencing objects.
     */
    private final ReferencingFactoryContainer factories;

    /**
     * The metadata accessor for the {@code "CoordinateReferenceSystem"} node.
     */
    private final MetadataAccessor accessor;

    /**
     * Creates a new metadata helper for the given metadata.
     *
     * @param metadata The Image I/O metadata. An instance of the {@link SpatialMetadata}
     *                 sub-class is recommanded, but not mandatory.
     */
    public ReferencingMetadataHelper(final IIOMetadata metadata) {
        this(new MetadataAccessor(metadata, FORMAT_NAME, "CoordinateReferenceSystem", null));
    }

    /**
     * Creates a new metadata helper using the given accessor.
     */
    private ReferencingMetadataHelper(final MetadataAccessor accessor) {
        super(accessor);
        this.accessor = accessor;
        factories = ReferencingFactoryContainer.instance(null);
    }

    /**
     * Returns {@code "untitled"} in the locale of the given accessor.
     */
    private static String untitled(final MetadataAccessor accessor) {
        return Vocabulary.getResources(accessor.getLocale()).getString(Vocabulary.Keys.UNTITLED);
    }

    /**
     * Gets the {@code "name"} attribute from the given accessor.
     * If this attribute is not found, then a default name is generated.
     *
     * @param  accessor The accessor to use for getting the name attribute.
     * @return A map containing the name attribute.
     */
    private static Map<String,Object> getName(final MetadataAccessor accessor) {
        String name = accessor.getAttribute("name");
        if (name == null) {
            name = untitled(accessor);
        } else {
            final int s = name.indexOf(DefaultNameSpace.DEFAULT_SEPARATOR);
            if (s >= 0) {
                final String authority = name.substring(0, s).trim();
                name = name.substring(s + 1).trim();
                if (name.length() == 0) {
                    name = authority;
                } else if (authority.length() != 0) {
                    final Map<String,Object> properties = new HashMap<String,Object>(6);
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
            final Class<T> baseType, final MetadataAccessor accessor)
    {
        final String type = accessor.getAttribute("type");
        if (type == null) {
            /*
             * If the type was not specified, log a warning only if the type was not
             * already known anyway. It may be known because some types of CRS accepts
             * only one specific type of datum or coordinate system.
             */
            if (baseType.equals(Datum.class) || baseType.equals(CoordinateSystem.class)) {
                warning(method, Errors.Keys.MISSING_PARAMETER_VALUE_$1, "type");
            }
        } else try {
            // Following line may throw a ClassCastException (as of method contract).
            final Class<? extends T> classe = MetadataEnum.getInterface(baseType, type);
            if (classe != null) {
                return classe;
            }
            warning(method, Errors.Keys.UNKNOW_TYPE_$1, type);
        } catch (ClassCastException e) {
            warning(method, Errors.Keys.ILLEGAL_CLASS_$2, new Object[] {type, baseType});
        }
        return baseType;
    }

    /**
     * Gets the prime meridian. If no prime meridian is explicitly defined, then a
     * {@linkplain MetadataAccessor#warningOccurred warning is logged} and a
     * {@linkplain #getDefault(Class) default prime meridian} is returned.
     *
     * @param  datumAccessor The accessor to the datum enclosing the prime meridian.
     * @return The prime meridian, or {@code null} if the prime meridian can not be
     *         parsed and there is no default value.
     * @throws FactoryException If the prime meridian can not be created.
     */
    protected PrimeMeridian getPrimeMeridian(final MetadataAccessor datumAccessor) throws FactoryException {
        final MetadataAccessor pmAccessor = new MetadataAccessor(datumAccessor, "PrimeMeridian", null);
        final Map<String,?> properties = getName(pmAccessor);
        final Double greenwich = pmAccessor.getAttributeAsDouble("greenwichLongitude");
        if (isNonNull("getEllipsoid", "greenwichLongitude", greenwich)) {
            final Unit<Angle> unit = pmAccessor.getAttributeAsUnit("angularUnit", Angle.class);
            if (isNonNull("getPrimeMeridian", "angularUnit", unit)) {
                final DatumFactory factory = factories.getDatumFactory();
                return factory.createPrimeMeridian(properties, greenwich, unit);
            }
        }
        return getDefault("getPrimeMeridian", pmAccessor, PrimeMeridian.class);
    }

    /**
     * Gets the ellipsoid. If no ellipsoid is explicitly defined, then a
     * {@linkplain MetadataAccessor#warningOccurred warning is logged} and a
     * {@linkplain #getDefault(Class) default ellipsoid} is returned.
     *
     * @param  datumAccessor The accessor to the datum enclosing the ellipsoid.
     * @return The ellipsoid, or {@code null} if the ellipsoid can not be parsed
     *         and there is no default value.
     * @throws FactoryException If the ellipsoid can not be created.
     */
    protected Ellipsoid getEllipsoid(final MetadataAccessor datumAccessor) throws FactoryException {
        final MetadataAccessor ellipsoidAccessor = new MetadataAccessor(datumAccessor, "Ellipsoid", null);
        final Map<String,?> properties = getName(ellipsoidAccessor);
        final Unit<Length> unit = ellipsoidAccessor.getAttributeAsUnit("axisUnit", Length.class);
        if (isNonNull("getEllipsoid", "axisUnit", unit)) {
            final Double semiMajor = ellipsoidAccessor.getAttributeAsDouble("semiMajorAxis");
            if (isNonNull("getEllipsoid", "semiMajorAxis", semiMajor)) {
                final DatumFactory factory = factories.getDatumFactory();
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
     * Gets the datum. If no datum is explicitly defined, then a
     * {@linkplain MetadataAccessor#warningOccurred warning is logged} and a
     * {@linkplain #getDefault(Class) default datum} is returned.
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
    protected <T extends Datum> T getDatum(final Class<T> baseType) throws FactoryException {
        final MetadataAccessor datumAccessor = new MetadataAccessor(accessor, "Datum", null);
        final Map<String,?> properties = getName(datumAccessor);
        final Class<? extends Datum> type = getInterface("getDatum", baseType, datumAccessor);
        if (type != null) {
            final DatumFactory factory = factories.getDatumFactory();
            if (GeodeticDatum.class.isAssignableFrom(type)) {
                final Ellipsoid ellipsoid = getEllipsoid(datumAccessor);
                final PrimeMeridian pm = getPrimeMeridian(datumAccessor);
                return baseType.cast(factory.createGeodeticDatum(properties, ellipsoid, pm));
            } else if (EngineeringDatum.class.isAssignableFrom(type)) {
                return baseType.cast(factory.createEngineeringDatum(properties));
            } else {
                warning("getDatum", Errors.Keys.UNKNOW_TYPE_$1, type);
            }
        }
        return getDefault("getDatum", datumAccessor, baseType);
    }

    /**
     * Gets the coordinate system. If no coordinate system is explicitly defined, then a
     * {@linkplain MetadataAccessor#warningOccurred warning is logged} and a
     * {@linkplain #getDefault(Class) default coordinate system} is returned.
     *
     * @param  <T> The compile-time type of {@code baseType}.
     * @param  baseType The expected coordinate system type.
     * @return The coordinate system, or {@code null} if the coordinate system
     *         can not be parsed and there is no default value.
     * @throws FactoryException If the coordinate system can not be created.
     */
    @SuppressWarnings("fallthrough")
    protected <T extends CoordinateSystem> T getCoordinateSystem(final Class<T> baseType)
            throws FactoryException
    {
        final MetadataAccessor csAccessor = new MetadataAccessor(accessor, "CoordinateSystem", null);
        final Map<String,?> properties = getName(csAccessor);
        final Class<? extends CoordinateSystem> type = getInterface("getCoordinateSystem", baseType, csAccessor);
        if (type != null) {
            final Integer dimension = csAccessor.getAttributeAsInteger("dimension");
            final MetadataAccessor axesAccessor = new MetadataAccessor(csAccessor, "Axes", "CoordinateSystemAxis");
            final int numAxes = axesAccessor.childCount();
            if (dimension != null && dimension != numAxes) {
                warning("getCoordinateSystem", Errors.Keys.MISMATCHED_DIMENSION_$3,
                        new Object[] {"Axes", numAxes, dimension});
            }
            final CoordinateSystemAxis[] axes = new CoordinateSystemAxis[numAxes];
            final CSFactory factory = factories.getCSFactory();
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
                     * name. Note that if non-null, the name is garanteed to have a length greater
                     * than 0 has of MetadataAccessor.getAttribute(String) method implementation.
                     */
                    abbreviation = axesProperties.get(IdentifiedObject.NAME_KEY).toString();
                    if (abbreviation == null) {
                        abbreviation = direction.identifier();
                    }
                    abbreviation = StringUtilities.acronym(abbreviation);
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
                        warning("getCoordinateSystem", Errors.Keys.NOT_TWO_DIMENSIONAL_$1, numAxes);
                        break;
                    }
                    case 2: {
                        return baseType.cast(isEllipsoidal ?
                                factory.createEllipsoidalCS(properties, axes[0], axes[1]) :
                                factory.createCartesianCS  (properties, axes[0], axes[1]));
                    }
                    default: {
                        warning("getCoordinateSystem", Errors.Keys.UNEXPECTED_DIMENSION_FOR_CS_$1, type);
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
                warning("getCoordinateSystem", Errors.Keys.UNKNOW_TYPE_$1, type);
            }
        }
        return getDefault("getCoordinateSystem", csAccessor, baseType);
    }

    /**
     * Gets the coordinate reference system. If no CRS is explicitly defined, then a
     * {@linkplain MetadataAccessor#warningOccurred warning is logged} and a
     * {@linkplain #getDefault(Class) default CRS} is returned.
     *
     * @param  <T> The compile-time type of {@code baseType}.
     * @param  baseType The expected CRS type.
     * @return The coordinate reference system, or {@code null} if the CRS
     *         can not be parsed and there is no default value.
     * @throws FactoryException If the coordinate reference system can not be created.
     */
    protected <T extends SingleCRS> CoordinateReferenceSystem getCoordinateReferenceSystem(final Class<T> baseType)
            throws FactoryException
    {
        final Map<String,?> properties = getName(accessor);
        final Class<? extends SingleCRS> type = getInterface("getCRS", baseType, accessor);
        if (type != null) {
            final CRSFactory factory = factories.getCRSFactory();
            if (GeographicCRS.class.isAssignableFrom(type)) {
                return factory.createGeographicCRS(properties,
                        getDatum(GeodeticDatum.class),
                        getCoordinateSystem(EllipsoidalCS.class));
            } else if (ProjectedCRS.class.isAssignableFrom(type)) {
                final GeographicCRS baseCRS = factory.createGeographicCRS(
                        Collections.singletonMap(GeographicCRS.NAME_KEY, untitled(accessor)),
                        getDatum(GeodeticDatum.class), DefaultEllipsoidalCS.GEODETIC_2D);
                final CartesianCS derivedCS = getCoordinateSystem(CartesianCS.class);
                return factory.createProjectedCRS(properties, baseCRS,
                        getConversionFromBase(baseCRS, derivedCS), derivedCS);
            } else {
                // TODO: test for other types of CRS here (VerticalCRS, etc.)
                warning("getCoordinateReferenceSystem", Errors.Keys.UNKNOW_TYPE_$1, type);
            }
        }
        return getDefault("getCoordinateReferenceSystem", accessor, baseType);
    }

    /**
     * Returns the defining conversion from the base geographic CRS to the projected CRS.
     * If no coordinate system is explicitly defined, then a
     * {@linkplain MetadataAccessor#warningOccurred warning is logged} and a
     * {@linkplain #getDefault(Class) default conversion} is returned.
     *
     * @return The conversion from geographic to projected CRS, or {@code null}.
     * @throws FactoryException If the conversion can not be created.
     */
    private Conversion getConversionFromBase(final CoordinateReferenceSystem baseCRS,
            final CoordinateSystem derivedCS) throws FactoryException
    {
        final MetadataAccessor cvAccessor = new MetadataAccessor(accessor, "Conversion", null);
        final Map<String,?> properties = getName(cvAccessor);
        final String method = cvAccessor.getAttribute("method");
        if (isNonNull("getBaseToCRS", "method", method)) {
            final MathTransformFactory   factory = factories.getMathTransformFactory();
            final ParameterValueGroup parameters = factory.getDefaultParameters(method);
            final MetadataAccessor paramAccessor = new MetadataAccessor(cvAccessor, "Parameters", "ParameterValue");
            final int numParam = paramAccessor.childCount();
            for (int i=0; i<numParam; i++) {
                paramAccessor.selectChild(i);
                final String name  = paramAccessor.getAttribute("name");
                if (isNonNull("getBaseToCRS", "name", name)) {
                    final Double value = paramAccessor.getAttributeAsDouble("value");
                    if (isNonNull("getBaseToCRS", "value", value)) try {
                        parameters.parameter(name).setValue(value.doubleValue());
                    } catch (IllegalArgumentException e) {
                        paramAccessor.warning(getClass(), "getBaseToCRS", e);
                    }
                }
            }
            final MathTransform tr = factory.createBaseToDerived(baseCRS, parameters, derivedCS);
            return new DefiningConversion(properties, factory.getLastMethodUsed(), tr);
        }
        return getDefault("getBaseToCRS", cvAccessor, Conversion.class);
    }

    /**
     * Returns the coordinate reference system, or {@code null} if it can not be created.
     * This method delegates to {@link #getCoordinateReferenceSystem(Class)} and catch the
     * exception. If an exception has been thrown, the exception is
     * {@linkplain MetadataAccessor#warningOccurred logged} and this method returns {@code null}.
     *
     * @return The CRS, or {@code null} if none.
     */
    public CoordinateReferenceSystem getOptionalCRS() {
        Exception failure;
        try {
            return getCoordinateReferenceSystem(SingleCRS.class);
        } catch (FactoryException e) {
            failure = e;
        } catch (NullArgumentException e) { // Happen if a mandatory element is absents.
            failure = e;
        }
        accessor.warning(getClass(), "getOptionalCRS", failure);
        return null;
    }

    /**
     * Returns a default object of the given class. This method is invoked automatically
     * when the object was not explicitly defined in the metadata, or can not be parsed.
     * <p>
     * The default implementation delegates to {@link PredefinedMetadataFormat#getDefaultValue(Class)}.
     * The later is preferred to {@link IIOMetadataFormat#getObjectDefaultValue(String)} because the
     * default value may vary depending on the {@code "type"} attribute in the enclosing element.
     * For example if the CRS type is {@code "geographic"}, then the default coordinate system
     * shall be a {@link EllipsoidalCS}. But if the CRS type is {@code "projected"} instead,
     * then the default coordinate system shall rather be a {@link CartesianCS}.
     * <p>
     * Subclasses can override this method if they want to provide different default values.
     *
     * @param  <T>  The compile-time type of the {@code type} argument.
     * @param  type The type of the object to be returned.
     * @return The default object of the given type, or {@code null} if none.
     * @throws FactoryException If the default object can not be created.
     *
     * @see PredefinedMetadataFormat#getDefaultValue(Class)
     * @see IIOMetadataFormat#getObjectDefaultValue(String)
     */
    protected <T extends IdentifiedObject> T getDefault(final Class<T> type) throws FactoryException {
        if (accessor.format instanceof PredefinedMetadataFormat) {
            return ((PredefinedMetadataFormat) accessor.format).getDefaultValue(type);
        }
        return null;
    }

    /**
     * Returns a default object of the given class. This method logs a warning telling that the
     * returned object is used as a fallback. The default implementation delegates to the first
     * of the following methods which return a non-null default value:
     * <p>
     * <ul>
     *   <li>{@link PredefinedMetadataFormat#getDefaultValue(Class)}</li>
     *   <li>{@link IIOMetadataFormat#getObjectDefaultValue(String)}</li>
     * </ul>
     */
    private <T extends IdentifiedObject> T getDefault(final String method,
            final MetadataAccessor accessor, final Class<T> type) throws FactoryException
    {
        T object = getDefault(type);
        if (object == null) {
            object = type.cast(accessor.format.getObjectDefaultValue(accessor.name()));
        }
        if (object != null) {
            warning(method, Loggings.getResources(accessor.getLocale()),
                    Loggings.Keys.USING_FALLBACK_$1, object.getName());
        }
        return object;
    }

    /**
     * Returns {@code true} if the given object is non-null.
     * Otherwise emmits a warning and returns {@code false}.
     */
    private boolean isNonNull(final String method, final String attribute, final Object value) {
        if (value != null) {
            return true;
        }
        warning(method, Errors.Keys.MISSING_PARAMETER_VALUE_$1, attribute);
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
