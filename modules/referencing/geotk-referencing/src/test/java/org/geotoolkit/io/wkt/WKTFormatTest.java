/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.io.wkt;

import java.text.ParseException;
import org.opengis.referencing.operation.CoordinateOperation;
import org.junit.*;

import static org.geotoolkit.referencing.Assert.*;


/**
 * Tests the {@link WKTFormat} implementation.
 *
 * @author Yann Cézard (IRD)
 * @author Rémi Eve (IRD)
 * @author Martin Desruisseaux (IRD, Geomatys)
 * @version 3.20
 *
 * @since 2.0
 */
public final strictfp class WKTFormatTest {
    /**
     * Parses a coordinate operation.
     *
     * @throws ParseException if the parsing failed.
     *
     * @see <a href="http://docs.opengeospatial.org/is/12-063r5/12-063r5.html#124">OGC 12-063r5 §17.3 example 1</a>
     */
    @Test
    public void parseCoordinateOperation() throws ParseException {
        final WKTFormat wktFormat = new WKTFormat();
        final CoordinateOperation op = (CoordinateOperation) wktFormat.parseObject(
                "COORDINATEOPERATION[“Tokyo to JGD2000 (GSI)”,\n" +
                "     SOURCECRS[\n" +
                "       GEODCRS[“Tokyo”,\n" +
                "         DATUM[“Tokyo 1918”,\n" +
                "          ELLIPSOID[“Bessel 1841”, 6377397.155, 299.1528128, LENGTHUNIT[“metre”,1.0]]],\n" +
                "        CS[Cartesian,3],\n" +
                "        AXIS[“(X)”,geocentricX,ORDER[1]],\n" +
                "        AXIS[“(Y)”,geocentricY,ORDER[2]],\n" +
                "        AXIS[“(Z)”,geocentricZ,ORDER[3]],\n" +
                "        LENGTHUNIT[“metre”,1.0]]],\n" +
                "     TARGETCRS[\n" +
                "       GEODCRS[“JGD2000”,\n" +
                "         DATUM[“Japanese Geodetic Datum 2000”,\n" +
                "          ELLIPSOID[“GRS 1980”,6378137.0,298.257222101,LENGTHUNIT[“metre”,1.0]]],\n" +
                "        CS[Cartesian,3],\n" +
                "        AXIS[“(X)”,geocentricX],\n" +
                "        AXIS[“(Y)”,geocentricY],\n" +
                "        AXIS[“(Z)”,geocentricZ],\n" +
                "        LENGTHUNIT[“metre”,1.0]]],\n" +
                "     METHOD[“Geocentric translations”,ID[“EPSG”,1031]],\n" +
                "     PARAMETER[“X-axis translation”,-146.414,\n" +
                "       LENGTHUNIT[“metre”,1.0],ID[“EPSG”,8605]],\n" +
                "     PARAMETER[“Y-axis translation”,507.337,\n" +
                "       LENGTHUNIT[“metre”,1.0],ID[“EPSG”,8606]],\n" +
                "     PARAMETER[“Z-axis translation”,680.507,\n" +
                "       LENGTHUNIT[“metre”,1.0],ID[“EPSG”,8607]]]");

        assertEquals("Tokyo",   op.getSourceCRS().getName().getCode());
        assertEquals("JGD2000", op.getTargetCRS().getName().getCode());
    }
}
