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
import static org.geotoolkit.data.s57.model.S57ModelObject.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class DataSetAccuracy extends S57ModelObject {
    
    //7.3.4 Data set accuracy record structure
    public static final String DSAC = "DSAC";
    public static final String DSAC_RCNM = "RCNM";
    public static final String DSAC_RCID = "RCID";
    public static final String DSAC_PACC = "PACC";
    public static final String DSAC_HACC = "HACC";
    public static final String DSAC_SACC = "SACC";
    public static final String DSAC_FPMF = "FPMF";
    public static final String DSAC_COMT = "COMT";
    
    public RCNM type;
    public long id;
    public double positionAccuracy;
    public double hvmeasureAccuracy;
    public double soundingAccuracy;
    public int factor;
    public String comment;
    
    
    @Override
    public void read(Field isofield) throws IOException {
        for(SubField sf : isofield.getSubFields()){
            final String tag = sf.getType().getTag();
            final Object val = sf.getValue();
                 if (DSAC_RCNM.equalsIgnoreCase(tag)) type = RCNM.read(val);
            else if (DSAC_RCID.equalsIgnoreCase(tag)) id = toLong(val);
            else if (DSAC_PACC.equalsIgnoreCase(tag)) positionAccuracy = toDouble(val);
            else if (DSAC_HACC.equalsIgnoreCase(tag)) hvmeasureAccuracy = toDouble(val);
            else if (DSAC_SACC.equalsIgnoreCase(tag)) soundingAccuracy = toDouble(val);
            else if (DSAC_FPMF.equalsIgnoreCase(tag)) factor = toInteger(val);
            else if (DSAC_COMT.equalsIgnoreCase(tag)) comment = toString(val);
        }
    }
    
}
