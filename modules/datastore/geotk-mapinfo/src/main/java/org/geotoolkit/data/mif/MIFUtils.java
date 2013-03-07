package org.geotoolkit.data.mif;

import com.vividsolutions.jts.geom.*;
import org.geotoolkit.data.mif.geometry.*;
import org.geotoolkit.metadata.iso.citation.Citations;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.Projection;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Scanner;

/**
 * Utility methods and constants for mif/mid parsing.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 20/02/13
 */
public final class MIFUtils {

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
        LINE(new MIFLineBuilder()),
        POLYLINE(new MIFPolyLineBuilder()),
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


    public static String crsToMIFSyntax(CoordinateReferenceSystem crs) throws DataStoreException {
        ArgumentChecks.ensureNonNull("CRS to convert", crs);
        final StringBuilder builder = new StringBuilder();
        builder.append(HeaderCategory.COORDSYS).append(" Earth\nProjection");
        // Geographic CRS (special) case, mapinfo proj code is 1.
        if(crs instanceof GeographicCRS) {
            builder.append('1').append(',');
            final GeographicCRS gCRS = (GeographicCRS) crs;

        } else if (crs instanceof ProjectedCRS) {
            final ProjectedCRS pCRS = (ProjectedCRS) crs;
            int projCode = getMapInfoProjectionCode(pCRS.getConversionFromBase());
            if(projCode < 1) {
                throw new DataStoreException("Projection of the given CRS does not get any equivalent in mapInfo.");
            }
            builder.append(projCode).append(',');
        } else {
            throw new DataStoreException("The given CRS can't be converted to MapInfo format.");
        }


        return builder.toString();
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
            builder.append(desc.getLocalName()).append(' ').append(mifType).append('\n');
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
            out = new FileOutputStream(tmpFile);
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
            throw new IOException("Unable to open data in write mode.", e);
        }
        return in;
    }

    public static int getMapInfoProjectionCode(Projection source) {
        ArgumentChecks.ensureNonNull("Source projection", source);
        for(ReferenceIdentifier ref : source.getIdentifiers()) {
            if(ref.getAuthority().equals(Citations.MAP_INFO)) {
                return Integer.decode(ref.getCode());
            }
        }
        return -1;
    }
}
