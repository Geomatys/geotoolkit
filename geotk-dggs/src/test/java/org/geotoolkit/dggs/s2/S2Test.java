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
package org.geotoolkit.dggs.s2;

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
public class S2Test extends AbstractDggrsTest {

    public S2Test() {
        super(new S2Dggrs());
    }

    /**
     * Check the antimeridian zone has a proper envelope
     */
    @Test
    public void testAntimeridian() throws TransformException {

        final S2Dggrs dggrs = new S2Dggrs();
        final Zone zone = dggrs.createCoder().decode("7cc");

        final GeographicExtent extent = zone.getGeographicExtent();
        final Polygon polygon = DiscreteGlobalGridSystems.toSISPolygon(extent);
        assertEquals("POLYGON ((-180.0 34.50852298766839, -180.0 22.619864948040426, -169.38034472384487 22.270575488008195, -169.38034472384487 34.04786296943431, -180.0 34.50852298766839))", polygon.asText());

        final Envelope envelope = zone.getEnvelope();
        assertEquals(-180, envelope.getMinimum(0), 0.0);
        assertEquals(-169.38034472384487, envelope.getMaximum(0), 0.0);
        assertEquals(22.270575488008195, envelope.getMinimum(1), 0.0);
        assertEquals(34.50852298766839, envelope.getMaximum(1), 0.0);

    }
}
