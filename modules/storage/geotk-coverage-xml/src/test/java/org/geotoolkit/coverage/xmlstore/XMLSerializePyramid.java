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
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.logging.Logging;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Test XML Binding for {@link XMLPyramid} with only serialized CRS.
 *
 * @author Remi Marechal (Geomatys).
 */
class XMLSerializePyramid extends XMLOldPyramid {

    XMLSerializePyramid(CoordinateReferenceSystem pyramidCRS) throws DataStoreException {
        super(pyramidCRS);
    }

    /**
     * Only use java serialization.
     *
     * @param crs
     * @throws DataStoreException
     */
    @Override
    void setCoordinateReferenceSystem(CoordinateReferenceSystem crs) throws DataStoreException {
        crsobj = crs;
        this.crs = null;
        this.serializedCrs = null;

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
