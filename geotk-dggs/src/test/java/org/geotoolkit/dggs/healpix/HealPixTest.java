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
package org.geotoolkit.dggs.healpix;

import org.apache.sis.geometries.Polygon;
import org.geotoolkit.dggs.AbstractDggrsTest;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridSystems;
import static org.junit.Assert.assertEquals;
import org.junit.jupiter.api.Test;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicExtent;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class HealPixTest extends AbstractDggrsTest {

    public HealPixTest() {
        super(new HealpixDggrs());
    }

    /**
     * Check the antimeridian zone has a proper envelope
     */
    @Test
    public void testAntimeridian() throws TransformException {

        final HealpixDggrs dggrs = new HealpixDggrs();
        final Zone zone = dggrs.createCoder().decode("11");

        final GeographicExtent extent = zone.getGeographicExtent();
        final Polygon polygon = DiscreteGlobalGridSystems.toSISPolygon(extent);
        assertEquals("POLYGON ((135.0 0.0, 180.0 41.810314895778596, 135.0 90.0, 90.0 41.810314895778596, 135.0 0.0))", polygon.asText());

        final Envelope envelope = zone.getEnvelope();
        assertEquals(90, envelope.getMinimum(0), 0.0);
        assertEquals(180, envelope.getMaximum(0), 0.0);
        assertEquals(0, envelope.getMinimum(1), 0.0);
        assertEquals(90, envelope.getMaximum(1), 0.0);

    }

}
