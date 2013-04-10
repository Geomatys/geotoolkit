package org.geotoolkit.data.mapinfo.mif;

import com.vividsolutions.jts.geom.*;
import org.geotoolkit.data.mapinfo.DatumIdentifier;
import org.geotoolkit.data.mapinfo.ProjectionUtils;
import org.geotoolkit.data.mapinfo.UnitIdentifier;
import org.geotoolkit.data.mapinfo.mif.geometry.*;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.filter.function.other.DateFormatFunction;
import org.geotoolkit.geometry.Envelope2D;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ArgumentChecks;
import org.geotoolkit.util.StringUtilities;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Projection;

import java.awt.geom.Rectangle2D;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 * Utility methods and constants for mif/mid parsing.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 20/02/13
 */
public final class MIFUtils {

    private static final int MAX_CHAR_LENGTH = 255;

    private static final DecimalFormat NUM_FORMAT = new DecimalFormat();

    static {
        NUM_FORMAT.setGroupingUsed(false);
        NUM_FORMAT.setDecimalSeparatorAlwaysShown(false);
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
        LOGICAL(Boolean.class);

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
        COLLECTION(new MIFCollectionBuilder());

        private final MIFGeometryBuilder binding;
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
        String mifGeom = null;
        if (toConvert.getDefaultGeometryProperty() != null) {
            final GeometryType geomBuilder = identifyFeature(toConvert.getType());
            if (geomBuilder != null) {
                mifGeom = geomBuilder.toMIFSyntax(toConvert);
            }
        }
        return mifGeom;
    }

    /**
     * Check the given FeatureType to know if its geometry property can be managed by MIF writer. Here we check feature
     * type and not geometry because of style information which can be stored in the feature.
     * @param toIdentify The feature type to check geometry.
     * @return The MIF {@link GeometryType} corresponding to this feature.
     */
    public static GeometryType identifyFeature(FeatureType toIdentify) {
        GeometryType type = null;
        /* We'll check for the exact featureType first, and if there's no matching, we'll refine our search by checking
         * the geometry classes.
         */
        final CoordinateReferenceSystem crsParam = toIdentify.getCoordinateReferenceSystem();
        FeatureType superParam = null;
        if( toIdentify.getSuper() instanceof FeatureType) {
            superParam = (FeatureType) toIdentify.getSuper();
        }
        for(GeometryType gType : GeometryType.values()) {
            if(gType.getBinding(crsParam, superParam).equals(toIdentify)) {
                type = gType;
                break;
            }
        }

        // for some types, we don't need to get the same featureType, only a matching geometry class will be sufficient.
        if(type == null && toIdentify.getGeometryDescriptor() != null) {
            final Class sourceClass = toIdentify.getGeometryDescriptor().getType().getBinding();
            if(Polygon.class.isAssignableFrom(sourceClass) || MultiPolygon.class.isAssignableFrom(sourceClass)) {
                type = GeometryType.REGION;
            } else if(LineString.class.isAssignableFrom(sourceClass) || MultiLineString.class.isAssignableFrom(sourceClass)) {
                type = GeometryType.PLINE;
            } else if(Point.class.isAssignableFrom(sourceClass) || Coordinate.class.isAssignableFrom(sourceClass)) {
                type = GeometryType.POINT;
            } else if(MultiPoint.class.isAssignableFrom(sourceClass) || CoordinateSequence.class.isAssignableFrom(sourceClass)) {
                type = GeometryType.MULTIPOINT;
            } else if(Envelope.class.isAssignableFrom(sourceClass) || Envelope2D.class.isAssignableFrom(sourceClass) || Rectangle2D.class.isAssignableFrom(sourceClass)) {
                type = GeometryType.RECTANGLE;
            } else if(GeometryCollection.class.isAssignableFrom(sourceClass)) {
                type = GeometryType.COLLECTION;
            }
        }

        return type;
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
            if(javaBinding.equals(type.binding)) {
                typename = type.name();

                // If we get a char or decimal type, we must set length delimiter on it.
                if (type.equals(AttributeType.CHAR)) {
                    typename = typename+'('+MAX_CHAR_LENGTH+')';
                } else if(type.equals(AttributeType.DECIMAL)) {
                    typename = typename+'('+NUM_FORMAT.getMaximumIntegerDigits()+','+NUM_FORMAT.getMaximumFractionDigits()+')';
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
     * Read the given input to build a geometry which type match the given typename
     * @param typeName The type of geometry to build.
     * @param reader The scanner to use for reading (at the wanted geometry position).
     * @param toFill The feature to fill with geometry data.
     * @throws DataStoreException If geometry can't be read.
     */
    public void readGeometry(String typeName, Scanner reader, Feature toFill, MathTransform toApply) throws DataStoreException {
        ArgumentChecks.ensureNonNull("Reader", reader);
        ArgumentChecks.ensureNonNull("Feature to fill", toFill);
        for(GeometryType type : GeometryType.values()) {
            if(typeName.equalsIgnoreCase(type.name())) {
                type.readGeometry(reader, toFill, toApply);
                return;
            }
        }
    }


    /**
     * Parse the given {@link SimpleFeatureType} to build a list of types in MIF format (as header COLUMNS category describes them).
     * @param toWorkWith The FeatureType to parse, can't be null.
     * @param builder A StringBuilder in which we'll append generated types. If null, a new one is created.
     */
    public static void featureTypeToMIFSyntax(SimpleFeatureType toWorkWith, StringBuilder builder) throws DataStoreException {
        ArgumentChecks.ensureNonNull("FeatureType to convert", toWorkWith);

        if(builder == null) {
            builder = new StringBuilder();
        }

        if(builder .length() > 0 && builder.charAt(builder.length()-1) != '\n') {
            builder.append('\n');
        }

        for(AttributeDescriptor desc : toWorkWith.getAttributeDescriptors()) {
            // geometries are not specified in MIF columns.
            if (desc instanceof GeometryDescriptor) {
                continue;
            }
            final String mifType = getColumnMIFType(desc.getType().getBinding());
            if( mifType == null) {
                throw new DataStoreException("Type "+desc.getType().getBinding()+" has no equivalent in MIF format.");
            }
            builder.append('\t').append(desc.getLocalName()).append(' ').append(mifType.toLowerCase()).append('\n');
        }
    }

    /**
     * Check if the data pointed by given URL is inside or outside current fileSystem.
     * @param url The address of the file to test.
     * @return true if the URL describe a local file, false otherwise.
     */
    public static boolean isLocal(final URL url){
        return url.toExternalForm().toLowerCase().startsWith("file:");
    }



    public static OutputStream openOutConnection(URL source) throws IOException {
        OutputStream out = null;

        try {
        if(MIFUtils.isLocal(source)) {
            final File tmpFile = new File(source.toURI());
            out = new FileOutputStream(tmpFile, true);
        } else {
            URLConnection connection = source.openConnection();
            connection.setDoOutput(true);
            out = connection.getOutputStream();
        }
        } catch (Exception e) {
            throw new IOException("Unable to open data in write mode.", e);
        }
        return out;
    }


    public static InputStream openInConnection(URL source) throws IOException {
        InputStream in = null;

        try {
        if(MIFUtils.isLocal(source)) {
            final File tmpFile = new File(source.toURI());
            in = new FileInputStream(tmpFile);
        } else {
            URLConnection connection = source.openConnection();
            connection.setDoOutput(true);
            in = connection.getInputStream();
        }
        } catch (Exception e) {
            throw new IOException("Unable to open data in read mode.", e);
        }
        return in;
    }


    /**
     * Write a stream into another.
     * @param in The source inputStream
     * @param writer The {@link OutputStreamWriter} which will write input stream into destination stream.
     * @throws IOException If there's a problem connecting to one of the streams.
     */
    public static void write(InputStream in, OutputStreamWriter writer) throws IOException {
        InputStreamReader reader = new InputStreamReader(in);
        char[] inBuffer = new char[1024];
        int byteRead = 0;
        while((byteRead = reader.read(inBuffer)) >= 0) {
            writer.write(inBuffer, 0, byteRead);
        }
    }


    /**
     * Return a String which is the ready-to-write (for MID file) representation of the given property.
     * @param prop The property to extract value from.
     * @return A string which is the value of the given property. Never Null, but can be empty.
     */
    public static String getStringValue(Property prop) {
        if(prop == null || prop.getValue() == null) {
            return "";
        }
        final Object value = prop.getValue();
        if(value instanceof Number) {
            return NUM_FORMAT.format(value);
        } else if(value instanceof Date) {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            return format.format(value);
        }

        return value.toString();
    }

}
