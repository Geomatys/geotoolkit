/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.geometry.jts;

import junit.framework.TestCase;
import org.geotoolkit.geometry.jts.SRIDGenerator.Version;
import org.junit.Test;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class SRIDTest extends TestCase{

    @Test
    public void testSRID(){

        final String epsg4326 = "EPSG:4326";

        final int srid = SRIDGenerator.toSRID(epsg4326, Version.V1);
        assertEquals(srid, 4326);
        String retour = SRIDGenerator.toSRS(srid, Version.V1);
        assertEquals(epsg4326, retour);

        final byte[] bytes = SRIDGenerator.toBytes(srid, Version.V1);
        retour = SRIDGenerator.toSRS(bytes, 0);
        assertEquals(epsg4326, retour);

    }

}
