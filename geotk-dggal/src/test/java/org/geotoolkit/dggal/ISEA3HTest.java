/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.dggal;

import org.apache.sis.geometries.Polygon;
import org.geotoolkit.dggs.AbstractDggrsTest;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridSystems;
import org.junit.jupiter.api.Test;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ISEA3HTest extends AbstractDggrsTest {

    public ISEA3HTest() {
        super(DGGALDggrs.ISEA3H_INSTANCE);
    }

    @Test
    public void testZoneCCW() throws TransformException {

        final Zone zone = DGGALDggrs.ISEA3H_INSTANCE.createCoder().decode("A4-0-A");
        final GeographicExtent extent = zone.getGeographicExtent();
        final Polygon polygon = DiscreteGlobalGridSystems.toSISPolygon(extent);
        System.out.println(polygon);

    }

}
