/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Open Source Geospatial Foundation (OSGeo)
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
 */
package org.geotoolkit.image.io.metadata;

import java.util.Map;
import java.util.Collections;
import javax.imageio.metadata.IIOMetadata;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.unit.Unit;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.IdentifiedObject;
import org.opengis.referencing.crs.*;
import org.opengis.referencing.cs.*;
import org.opengis.referencing.datum.*;

import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.cs.DefaultEllipsoidalCS;
import org.geotoolkit.referencing.datum.DefaultEllipsoid;
import org.geotoolkit.referencing.datum.DefaultGeodeticDatum;
import org.geotoolkit.referencing.datum.DefaultPrimeMeridian;
import org.geotoolkit.referencing.datum.DefaultEngineeringDatum;
import org.geotoolkit.referencing.factory.ReferencingFactoryContainer;
import org.geotoolkit.resources.Errors;
import org.geotoolkit.resources.Loggings;
import org.geotoolkit.resources.IndexedResourceBundle;
import org.geotoolkit.internal.StringUtilities;
import org.geotoolkit.internal.image.io.MetadataEnum;

import static org.geotoolkit.image.io.metadata.SpatialMetadataFormat.FORMAT_NAME;


/**
 * Builds referencing objects from a {@link IIOMetadata} object. The main method is
 * {@link #getCoordinateReferenceSystem()}. The other methods are hooks for overriding
 * by subclasses.
 *
 * {@note We do not use the reflection mechanism like what we do for ISO 19115-2 metadata,
 *        because the mapping between Image I/O metadata and the referencing objects is more
 *        indirect. For example the kind of object to create depends on the value of the
 *        <code>"type"</code> attribute.}
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.07
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
     * Gets the {@code "name"} attribute from the given accessor.
     *
     * @param  accessor The accessor to use for getting the name attribute.
     * @return A map containing the name attribute, or an empty map.
     */
    private static Map<String,String> getName(final MetadataAccessor accessor) {
        return getName(accessor, "name");
    }

    /**
     * Same as {@link #getName}, but uses the given attribute name instead than {@code "name"}.
     *
     * @param  accessor  The accessor to use for getting the name attribute.
     * @param  attribute The attribute name ({@code "name"} by default).
     * @return A map containing the name attribute, or an empty map.
     */
    private static Map<String,String> getName(final MetadataAccessor accessor, final String attribute) {
        final String name = accessor.getAttribute(attribute);
        if (name != null) {
            return Collections.singletonMap(IdentifiedObject.NAME_KEY, name);
        } else {
            return Collections.emptyMap();
        }
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
        final Map<String,String> properties = getName(pmAccessor);
        final Double greenwich = pmAccessor.getAttributeAsDouble("greenwichLongitude");
        if (isNonNull("getEllipsoid", "greenwichLongitude", greenwich)) {
            final Unit<Angle> unit = pmAccessor.getAttributeAsUnit("angularUnit", Angle.class);
            if (isNonNull("getPrimeMeridian", "angularUnit", unit)) {
                final DatumFactory factory = factories.getDatumFactory();
                return factory.createPrimeMeridian(properties, greenwich, unit);
            }
        }
        return getDefault("getPrimeMeridian", PrimeMeridian.class);
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
        final Map<String,String> properties = getName(ellipsoidAccessor);
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
        return getDefault("getEllipsoid", Ellipsoid.class);
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
        final Map<String,String> properties = getName(datumAccessor);
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
        return getDefault("getDatum", baseType);
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
        final Map<String,String> properties = getName(csAccessor);
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
                final Map<String,String> axesProperties = getName(axesAccessor);
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
                    abbreviation = axesProperties.get(IdentifiedObject.NAME_KEY);
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
        return getDefault("getCoordinateSystem", baseType);
    }

    /**
     * Gets the coordinate reference system.
     *
     * @return The coordinate reference system, or {@code null}.
     * @throws FactoryException If the coordinate reference system can not be created.
     *
     * @todo Current implementation supports only <code>GeographicCRS</code>.
     */
    public CoordinateReferenceSystem getCoordinateReferenceSystem() throws FactoryException {
        final Map<String,String> properties = getName(accessor);
        final Class<? extends SingleCRS> type = getInterface("getCRS", SingleCRS.class, accessor);
        if (type != null) {
            final CRSFactory factory = factories.getCRSFactory();
            if (GeographicCRS.class.isAssignableFrom(type)) {
                return factory.createGeographicCRS(properties,
                        getDatum(GeodeticDatum.class),
                        getCoordinateSystem(EllipsoidalCS.class));
            } else {
                // TODO: test for other types of CRS here (VerticalCRS, etc.)
                warning("getCoordinateReferenceSystem", Errors.Keys.UNKNOW_TYPE_$1, type);
            }
        }
        return null;
    }

    /**
     * Returns a default object of the given class. This method is invoked automatically
     * when the object was not explicitly defined in the metadata, or can not be parsed.
     * The default implementation provides the following default objects. Subclasses can
     * override this method for changing the defaults.
     * <p>
     * <table border="1">
     * <tr bgcolor="lightblue">
     *   <th>Type</th>
     *   <th>Default</th>
     * </tr><tr>
     *   <td>&nbsp;{@link PrimeMeridian}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultPrimeMeridian#GREENWICH GREENWICH}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link Ellipsoid}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultEllipsoid#WGS84 WGS84}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link GeodeticDatum}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultGeodeticDatum#WGS84 WGS84}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link EngineeringDatum}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultEngineeringDatum#UNKNOW UNKNOW}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link EllipsoidalCS}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultEllipsoidalCS#GEODETIC_2D GEODETIC_2D}&nbsp;</td>
     * </tr><tr>
     *   <td>&nbsp;{@link CartesianCS}&nbsp;</td>
     *   <td>&nbsp;{@link DefaultCartesianCS#GENERIC_2D GENERIC_2D}&nbsp;</td>
     * </tr>
     * </table>
     *
     * @param  <T>  The compile-time type of the {@code type} argument.
     * @param  type The type of the object to be returned.
     * @return The default object of the given type, or {@code null} if none.
     * @throws IllegalArgumentException If the given type is unknown to this method.
     * @throws FactoryException If the default object can not be created.
     */
    protected <T extends IdentifiedObject> T getDefault(final Class<T> type)
            throws IllegalArgumentException, FactoryException
    {
        final IdentifiedObject object;
        if (PrimeMeridian.class.isAssignableFrom(type)) {
            object = DefaultPrimeMeridian.GREENWICH;
        } else if (Ellipsoid.class.isAssignableFrom(type)) {
            object = DefaultEllipsoid.WGS84;
        } else if (GeodeticDatum.class.isAssignableFrom(type)) {
            object = DefaultGeodeticDatum.WGS84;
        } else if (EngineeringDatum.class.isAssignableFrom(type)) {
            object = DefaultEngineeringDatum.UNKNOW;
        } else if (EllipsoidalCS.class.isAssignableFrom(type)) {
            object = DefaultEllipsoidalCS.GEODETIC_2D;
        } else if (CartesianCS.class.isAssignableFrom(type)) {
            object = DefaultCartesianCS.GENERIC_2D;
        } else {
            throw new IllegalArgumentException(Errors.format(Errors.Keys.UNKNOW_TYPE_$1, type));
        }
        return type.cast(object);
    }

    /**
     * Returns a default object of the given class. This method logs
     * a warning telling that the returned object is used as a fallback.
     */
    private <T extends IdentifiedObject> T getDefault(final String method, final Class<T> type)
            throws FactoryException
    {
        final T object = getDefault(type);
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
