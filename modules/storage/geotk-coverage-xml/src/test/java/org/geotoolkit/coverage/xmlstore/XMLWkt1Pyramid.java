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

import org.apache.sis.io.wkt.Convention;
import org.apache.sis.io.wkt.FormattableObject;
import org.apache.sis.io.wkt.WKTFormat;
import org.apache.sis.storage.DataStoreException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Test XML Binding for {@link XMLPyramid} with only Wkt1 formattable CRS.
 *
 * @author Remi Marechal (Geomatys).
 */
class XMLWkt1Pyramid extends XMLOldPyramid {

    XMLWkt1Pyramid(CoordinateReferenceSystem pyramidCRS) throws DataStoreException {
        super(pyramidCRS);
    }

    /**
     * Only serialize into WKT1 version.
     *
     * @param crs
     * @throws DataStoreException
     */
    @Override
    void setCoordinateReferenceSystem(CoordinateReferenceSystem crs) throws DataStoreException {
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

        if (this.crs == null && serializedCrs == null) {
            throw new DataStoreException("Input CRS cannot be serialized :\n"+crs);
        }
    }
}
