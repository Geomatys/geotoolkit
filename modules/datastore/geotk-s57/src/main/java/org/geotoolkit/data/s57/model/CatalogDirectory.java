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
package org.geotoolkit.data.s57.model;

import java.io.IOException;
import org.geotoolkit.data.iso8211.Field;
import org.geotoolkit.data.iso8211.SubField;
import static org.geotoolkit.data.s57.S57Constants.*;
import static org.geotoolkit.data.s57.model.S57Object.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CatalogDirectory extends S57Object {
    
    //7.4.1 Catalogue directory record structure
    public static final String CATD = "CATD";
    public static final String CATD_RCNM = "RCNM";
    public static final String CATD_RCID = "RCID";
    public static final String CATD_FILE = "FILE";
    public static final String CATD_LFIL = "LFIL";
    public static final String CATD_VOLM = "VOLM";
    public static final String CATD_IMPL= "IMPL";
    public static final String CATD_SLAT = "SLAT";
    public static final String CATD_WLON = "WLON";
    public static final String CATD_NLAT = "NLAT";
    public static final String CATD_ELON = "ELON";
    /**
     * A “Cyclic Redundancy Check” (CRC) algorithm can be used to ensure that the data has not been
     * corrupted during the exchange process. Different CRC algorithms can be used for different applications.
     * The algorithm used is, therefore, described in the relevant product specification (see Appendix B –
     * Product Specifications).
     * A CRC value for every file in an exchange set can be encoded in the “Catalogue Directory” [CATD] field,
     * CRCS subfield.
     */
    public static final String CATD_CRCS = "CRCS";
    public static final String CATD_COMT = "COMT";
 
    public String file;
    public String fileName;
    public String volume;
    public Implementation impl;
    public double southmostLatitude;
    public double westmostLongitude;
    public double northmostLatitude;
    public double eastmostLongitude;
    
    public String crc;
    public String comment;
    
    @Override
    public void read(Field isofield) throws IOException {
        for(SubField sf : isofield.getSubFields()){
            final String tag = sf.getType().getTag();
            final Object value = sf.getValue();
                 if (CATD_RCNM.equalsIgnoreCase(tag)) type = RecordType.valueOf(value);
            else if (CATD_RCID.equalsIgnoreCase(tag)) id = toInteger(value);
            else if (CATD_FILE.equalsIgnoreCase(tag)) file = toString(value);
            else if (CATD_LFIL.equalsIgnoreCase(tag)) fileName = toString(value);
            else if (CATD_VOLM.equalsIgnoreCase(tag)) volume = toString(value);
            else if (CATD_IMPL.equalsIgnoreCase(tag)) impl = Implementation.valueOf(value);
            else if (CATD_SLAT.equalsIgnoreCase(tag)) southmostLatitude = toDouble(value);
            else if (CATD_WLON.equalsIgnoreCase(tag)) westmostLongitude = toDouble(value);
            else if (CATD_NLAT.equalsIgnoreCase(tag)) northmostLatitude = toDouble(value);
            else if (CATD_ELON.equalsIgnoreCase(tag)) eastmostLongitude = toDouble(value);
            else if (CATD_CRCS.equalsIgnoreCase(tag)) crc = toString(value);
            else if (CATD_COMT.equalsIgnoreCase(tag)) comment = toString(value);
        }
    }
    
}
