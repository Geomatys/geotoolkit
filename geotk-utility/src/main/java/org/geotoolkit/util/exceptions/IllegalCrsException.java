package org.geotoolkit.util.exceptions;

import java.util.function.Supplier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Denote a problem with a coordinate reference system. The problematic crs can
 * be fetched using {@link #get() } method.
 *
 * @author Alexis Manin (Geomatys)
 */
public class IllegalCrsException extends IllegalStateException implements Supplier<CoordinateReferenceSystem> {

    final CoordinateReferenceSystem crs;

    public IllegalCrsException(CoordinateReferenceSystem crs) {
        this.crs = crs;
    }

    public IllegalCrsException(CoordinateReferenceSystem crs, String s) {
        super(s);
        this.crs = crs;
    }

    public IllegalCrsException(CoordinateReferenceSystem crs, String message, Throwable cause) {
        super(message, cause);
        this.crs = crs;
    }

    public IllegalCrsException(CoordinateReferenceSystem crs, Throwable cause) {
        super(cause);
        this.crs = crs;
    }

    /**
     *
     * @return The coordinate reference system which caused the error.
     */
    @Override
    public CoordinateReferenceSystem get() {
        return crs;
    }
}
