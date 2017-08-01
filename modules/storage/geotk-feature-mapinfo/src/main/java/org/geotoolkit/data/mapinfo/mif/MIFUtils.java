/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.mapinfo.mif;

import com.vividsolutions.jts.geom.*;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.mapinfo.mif.geometry.*;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.geometry.Envelope2D;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.system.DefaultFactories;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.filter.FilterFactory;

/**
 * Utility methods and constants for mif/mid parsing.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 20/02/13
 */
public final class MIFUtils {

    public static final String DEFAULT_CHARSET = "ISO-8859-1";

    private static final int MAX_CHAR_LENGTH = 255;

    private static final DecimalFormat NUM_FORMAT = new DecimalFormat();

    public static final FilterFactory FF = DefaultFactories.forBuildin(FilterFactory.class);

    static {
        NUM_FORMAT.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));
        NUM_FORMAT.setGroupingUsed(false);
        NUM_FORMAT.setDecimalSeparatorAlwaysShown(false);
        NUM_FORMAT.setMaximumFractionDigits(10);
    }

    /**
     * An enum to list the header labels we can encounter in MIF file.
     */
    static public enum HeaderCategory {

        // The headers label, stored in logical order of encounter( as told in specification.

        /** Mif file version */
        VERSION,
        /** character encoding */
        CHARSET,
        /** (Optional) delimiting character in quotation marks */
        DELIMITER,
        /** (Optional) Numbers indicating database column for eventual identifiers. */
        UNIQUE,
        /** (Optional) Numbers for eventual database index. */
        INDEX,
        /** (Optional) Feature CRS. If no provided, data is long/lat format. */
        COORDSYS,
        /** (Optional) Transform coefficients to apply to geometries. */
        TRANSFORM,
        /** The number and definition of the feature attributes */
        COLUMNS,
        /** The beginning of the real data */
        DATA;
    }

    /**
     * An enum to bind a MIF column type with the Java class to use for type representation.
     */
    static public enum AttributeType {
        CHAR(String.class),
        INTEGER(Integer.class),
        SMALLINT(Short.class),
        DECIMAL(Double.class),
        FLOAT(Float.class),
        DATE(Date.class),
        LOGICAL(Boolean.class),
        LONG(Long.class);

        public final Class binding;
        private AttributeType(Class Bind) {
            binding = Bind;
        }
    }

    static public enum GeometryType {
        POINT(new MIFPointBuilder()),
        PLINE(new MIFPolyLineBuilder()),
        LINE(new MIFLineBuilder()),
        REGION(new MIFRegionBuilder()),
        ARC(new MIFArcBuilder()),
        TEXT(new MIFTextBuilder()),
        RECTANGLE(new MIFRectangleBuilder()),
        ROUNDRECT(new MIFRectangleBuilder()),
        ELLIPSE(new MIFEllipseBuilder()),
        MULTIPOINT(new MIFMultiPointBuilder()),
        COLLECTION(new MIFCollectionBuilder()),
        GEOMETRY(new MIFDefaultGeometryBuilder());

        public final MIFGeometryBuilder binding;
        private GeometryType(MIFGeometryBuilder Binder) {
            binding = Binder;
        }

        /**
         * Get the type used to build MIF geometry.
         *
         * @param crs The coordinate reference system to use in this type.
         * @param parent The FeatureType we want to inherit from.
         * @return
         */
        public FeatureType getBinding(CoordinateReferenceSystem crs, FeatureType parent) {
            return binding.buildType(crs, parent);
        }

        public void readGeometry(Scanner reader, Feature toFill, MathTransform toApply) throws DataStoreException {
            binding.buildGeometry(reader, toFill, toApply);
        }

        public String toMIFSyntax(Feature toConvert) throws DataStoreException {
            return binding.toMIFSyntax(toConvert);
        }
    }

    /**
     * Build a MIF representation of the given feature's geometry.
     * @param toConvert The feature we want to extract geometry from.
     * @return A string which is the feature geometry in MIF syntax, or null if there's no compatible geometry in the
     * feature
     * @throws DataStoreException If we get a problem while geometry conversion.
     */
    public static String buildMIFGeometry(Feature toConvert) throws DataStoreException {
        final GeometryType geomBuilder = identifyFeature(toConvert)
                .orElseThrow(() -> new DataStoreException("Unknown geometry type in input feature " + toConvert));
        return geomBuilder.toMIFSyntax(toConvert);
    }

    /**
     * Check the given FeatureType to know if its geometry property can be managed by MIF writer. Here we check feature
     * type and not geometry because of style information which can be stored in the feature.
     * @param toIdentify The feature type to check geometry.
     * @return The MIF {@link GeometryType} corresponding to this feature.
     */
    static Optional<GeometryType> identifyFeature(Feature toIdentify) {
        GeometryType type = null;
        /* We'll check for the exact featureType first, and if there's no matching, we'll refine our search by checking
         * the geometry classes.
         */
        final FeatureType inputType = toIdentify.getType();
        final CoordinateReferenceSystem crsParam = FeatureExt.getCRS(inputType);
        FeatureType superParam = null;
        final Set<? extends FeatureType> superTypes = inputType.getSuperTypes();
        if(!superTypes.isEmpty()) {
            superParam = (FeatureType) superTypes.iterator().next();
        }
        for(GeometryType gType : GeometryType.values()) {
            if(FeatureExt.sameProperties(gType.getBinding(crsParam, superParam), inputType, true)) {
                type = gType;
                break;
            }
        }

        // for some types, we don't need to get the same featureType, only a matching geometry class will be sufficient.
        final IdentifiedType geomType;
        if (type != null) {
            return Optional.of(type);
        } else {
            return FeatureExt.getDefaultGeometryValue(toIdentify)
                    .map(Object::getClass)
                    .map(MIFUtils::getGeometryType);
        }
    }

    private static GeometryType getGeometryType(final Class sourceClass) {
        GeometryType type = null;
        if (Polygon.class.isAssignableFrom(sourceClass) || MultiPolygon.class.isAssignableFrom(sourceClass)) {
            type = GeometryType.REGION;
        } else if (LineString.class.isAssignableFrom(sourceClass) || MultiLineString.class.isAssignableFrom(sourceClass)) {
            type = GeometryType.PLINE;
        } else if (Point.class.isAssignableFrom(sourceClass) || Coordinate.class.isAssignableFrom(sourceClass)) {
            type = GeometryType.POINT;
        } else if (MultiPoint.class.isAssignableFrom(sourceClass) || CoordinateSequence.class.isAssignableFrom(sourceClass)) {
            type = GeometryType.MULTIPOINT;
        } else if (Envelope.class.isAssignableFrom(sourceClass) || Envelope2D.class.isAssignableFrom(sourceClass) || Rectangle2D.class.isAssignableFrom(sourceClass)) {
            type = GeometryType.RECTANGLE;
        } else if (GeometryCollection.class.isAssignableFrom(sourceClass)) {
            type = GeometryType.COLLECTION;
        } else if (Geometry.class.isAssignableFrom(sourceClass)) {
            type = GeometryType.GEOMETRY;
        }

        return type;
    }

    public static Object getGeometryValue(final Feature input) throws DataStoreException {
        return FeatureExt.getDefaultGeometryValue(input)
                .orElseThrow(() -> new DataStoreException(String.format("Given feature has no geometry :%n%s", input)));
    }

    /**
     * Retrieve the Java class bound to the given typename. It works only for primitive attribute types.
     *
     * @param typename The string to retrieve class from.
     * @return A class matching given typename, or null if we can't find one.
     */
    public static Class getColumnJavaType(String typename) {
        ArgumentChecks.ensureNonNull("Type name", typename);
        Class attClass = null;
        for(AttributeType type : AttributeType.values()) {
            if(typename.equalsIgnoreCase(type.name())) {
                attClass = type.binding;
                break;
            }
        }
        return attClass;
    }

    /**
     * Get the MIF name bound to the given Java type. You can look at {@link MIFUtils.AttributeType} for supported types.
     * @param javaBinding The class to retrieve type identifier from.
     * @return The mif primitive type name, or null if no equivalent can be found.
     */
    public static String getColumnMIFType(Class javaBinding) {
        ArgumentChecks.ensureNonNull("Java class", javaBinding);
        String typename = null;
        for(AttributeType type : AttributeType.values()) {
            if(type.binding.isAssignableFrom(javaBinding)) {
                typename = type.name();

                // If we get a char or decimal type, we must set length delimiter on it.
                if (type.equals(AttributeType.CHAR)) {
                    typename = typename+'('+MAX_CHAR_LENGTH+')';
                } else if(type.equals(AttributeType.DECIMAL)) {
                    typename = typename+'('+NUM_FORMAT.getMaximumIntegerDigits()+','+NUM_FORMAT.getMaximumFractionDigits()+')';
                } else if(type.equals(AttributeType.LONG)){
                    typename = AttributeType.DECIMAL.name()+'('+NUM_FORMAT.getMaximumIntegerDigits()+','+NUM_FORMAT.getMaximumFractionDigits()+')';
                }
                break;
            }
        }

        return typename;
    }

    /**
     * Retrieve the feature type bound to the given typename. It works only for geometry types.
     *
     * It accept a null parameter, because when searching pattern in file, the result could be null.
     *
     * @param crs The CRS to use for the geometries.
     * @param typename The string to retrieve type from (Should be a MIF id, like ROUNDRECT, REGION, etc).
     * @param parent The feature type to set as parent of this geometryType.
     * @return A {@link FeatureType} matching given typename, or null if we can't find one.
     */
    public static FeatureType getGeometryType(String typename, CoordinateReferenceSystem crs, FeatureType parent) {
        if(typename == null || typename.isEmpty()) {
            return null;
        }
        FeatureType geomType = null;
        for(GeometryType type : GeometryType.values()) {
            if(typename.equalsIgnoreCase(type.name())) {
                geomType = type.getBinding(crs, parent);
                break;
            }
        }
        return geomType;
    }

    public static GeometryType getGeometryType(String typename) {
        if (typename == null || typename.isEmpty()) {
            return null;
        }
        for (GeometryType type : GeometryType.values()) {
            if (typename.equalsIgnoreCase(type.name())) {
                return type;
            }
        }
        return null;
    }

    /**
     * Parse the given {@link FeatureType} to build a list of types in MIF format (as header COLUMNS category describes them).
     * NOTE : the given type must have no geometry, and its properties must be simple objects.
     *
     * @param toWorkWith The FeatureType to parse, can't be null.
     * @param builder A StringBuilder in which we'll append generated types. Cannot be null
     * @throws org.apache.sis.storage.DataStoreException If given data type contains invalid properties.
     */
    public static void featureTypeToMIFSyntax(FeatureType toWorkWith, StringBuilder builder) throws DataStoreException {
        ArgumentChecks.ensureNonNull("FeatureType to convert", toWorkWith);
        ArgumentChecks.ensureNonNull("Builder", builder);

        if(builder .length() > 0 && builder.charAt(builder.length()-1) != '\n') {
            builder.append('\n');
        }

        for(final PropertyType desc : toWorkWith.getProperties(true)) {
            // geometries are not specified in MIF columns.
            if (AttributeConvention.isGeometryAttribute(desc)) {
                continue;
            }
            Class<?> valueType = FeatureExt.castOrUnwrap(desc)
                    .map(org.opengis.feature.AttributeType::getValueClass)
                    .orElseThrow(() -> new DataStoreException("Cannot use attribute "+desc.getName()+" because we cannot find its value type."));

            final String mifType = getColumnMIFType(valueType);
            if( mifType == null) {
                throw new DataStoreException("Type "+valueType+" has no equivalent in MIF format.");
            }
            builder.append('\t').append(desc.getName().tip().toString()).append(' ').append(mifType.toLowerCase()).append('\n');
        }
    }

    /**
     * Return a String which is the ready-to-write (for MID file) representation of the given property.
     * @param value The property value
     * @return A string which is the value of the given property. Never Null, but can be empty.
     */
    public static String getStringValue(Object value) {
        if(value == null) {
            return "";
        }
        if(value instanceof Number) {
            return NUM_FORMAT.format(value);
        } else if(value instanceof Date) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            return format.format(value);
        }

        return value.toString();
    }

    /**
     *
     * @param container
     * @param propertyName Name of the
     * @return The value associated to the queried property name, or null if the
     * property is not defined.
     */
    public static Object getPropertySafe(final Feature container, final String propertyName) {
        try {
            return container.getPropertyValue(propertyName);
        } catch (PropertyNotFoundException e) {
            return null;
        }
    }
}
