package org.geotoolkit.data.mif;

/**
 * An enum to list the header labels we can encounter in MIF file.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 20/02/13
 */
public enum MIFHeaderCategory {

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
