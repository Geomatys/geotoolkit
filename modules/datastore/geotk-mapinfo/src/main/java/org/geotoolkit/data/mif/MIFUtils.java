package org.geotoolkit.data.mif;

import com.vividsolutions.jts.geom.*;
import org.geotoolkit.data.mif.geometry.*;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.ArgumentChecks;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

import java.util.Date;
import java.util.Scanner;

/**
 * Utility methods and constants for mif/mid parsing.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 20/02/13
 */
public final class MIFUtils {

    private final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel());
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
    public static Class getColumnType(String typename) {
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
}
