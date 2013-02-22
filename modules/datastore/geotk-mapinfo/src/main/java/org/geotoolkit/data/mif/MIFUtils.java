package org.geotoolkit.data.mif;

import com.vividsolutions.jts.geom.*;
import org.geotoolkit.util.ArgumentChecks;

import java.awt.geom.Arc2D;
import java.util.Date;

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
        POINT(Point.class),
        LINE(LineString.class),
        POLYLINE(MultiLineString.class),
        REGION(MultiPolygon.class),
        /** todo Give a better work on it */
        ARC(LineString.class),
        TEXT(String.class),
        RECTANGLE(Polygon.class),
        /** todo Give a better work on it */
        ROUNDRECT(Envelope.class),
        /** todo Give a better work on it */
        ELLIPSE(LinearRing.class),
        MULTIPOINT(MultiPoint.class),
        COLLECTION(GeometryCollection.class);

        public final Class binding;
        private GeometryType(Class Bind) {
            binding = Bind;
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
     * Retrieve the Java class bound to the given typename. It works only for geometry types.
     *
     * It accept a null parameter, because when searching pattern in file, the result could be null.
     *
     * @param typename The string to retrieve class from.
     * @return A class matching given typename, or null if we can't find one.
     */
    public static Class getGeometryType(String typename) {
        if(typename == null) {
            return null;
        }
        Class geomClass = null;
        for(GeometryType type : GeometryType.values()) {
            if(typename.equalsIgnoreCase(type.name())) {
                geomClass = type.binding;
                break;
            }
        }
        return geomClass;
    }
}
