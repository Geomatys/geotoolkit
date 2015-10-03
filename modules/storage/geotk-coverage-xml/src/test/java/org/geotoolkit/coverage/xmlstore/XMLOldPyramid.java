/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.coverage.xmlstore;

import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import net.iharder.Base64;
import org.apache.sis.io.wkt.Convention;
import org.apache.sis.io.wkt.FormattableObject;
import org.apache.sis.io.wkt.WKTFormat;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Test XML Binding for {@link XMLPyramid} from different versions.
 *
 * @author Remi Marechal (Geomatys).
 */
class XMLOldPyramid extends XMLPyramid {

    XMLOldPyramid(CoordinateReferenceSystem pyramidCRS) throws DataStoreException {
        super(pyramidCRS);
    }

    /**
     * Binding CRS into WKT1 format and java serialization.
     *
     * @param crs
     * @throws DataStoreException
     */
    @Override
    void setCoordinateReferenceSystem(CoordinateReferenceSystem crs) throws DataStoreException {
        ArgumentChecks.ensureNonNull("Input CRS", crs);
        crsobj = crs;
        this.crs = null;
        this.serializedCrs = null;
        if (crs instanceof FormattableObject) {
            this.crs = ((FormattableObject)crs).toString(Convention.WKT1);
        } else if (crs instanceof org.geotoolkit.io.wkt.Formattable) {
            WKTFormat f = new WKTFormat(null, null);
            f.setConvention(Convention.WKT1);

            this.crs = f.format(crs);
        }

            if (crs instanceof Serializable) {
                try {
                    this.serializedCrs = Base64.encodeObject((Serializable)crs);
            } catch (IOException ex) {
                Logging.getLogger("org.geotoolkit.coverage.xmlstore").log(Level.WARNING, ex.getMessage(), ex);
                }
            }

        if (this.crs == null && serializedCrs == null) {
            throw new DataStoreException("Input CRS cannot be serialized :\n"+crs);
        }
    }
}
