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

import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Polygon;
import org.apache.sis.geometries.Polygon;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.dggs.AbstractDggrsTest;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridHierarchy;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridSystem;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridSystems;
import org.geotoolkit.storage.rs.internal.shared.s2.S2;
import org.junit.Assert;
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
        final GeographicExtent extent10 = zone.getGeographicExtent(10);
        final Polygon polygon = DiscreteGlobalGridSystems.toSISPolygon(extent);
        assertEquals("POLYGON ((135.0 0.0, 180.0 41.810314895778596, 135.0 90.0, 90.0 41.810314895778596, 135.0 0.0))", polygon.asText());
        assertEquals("POLYGON ((135.0 90.0, 90.0 85.32051872234025, 90.0 80.63321111564838, 90.00000000000004 75.93013225242788, 90.0 71.20309496497133, 90.0 66.44353569089878, 90.0 61.64236342367203, 90.0 56.78978385189423, 90.0 51.87508837796015, 90.0 46.88639405410129, 90.0 41.810314895778596, 94.5 36.86989764584401, 99.00000000000001 32.23095263550211, 103.49999999999999 27.81813928465393, 108.00000000000001 23.578178478201828, 112.50000000000001 19.471220634490695, 117.0 15.466009953420548, 121.5 11.536959032815487, 126.0 7.662255660766063, 130.5 3.8225537292743432, 135.0 0.0, 139.5 3.822553729274344, 144.0 7.662255660766065, 148.5 11.53695903281549, 153.0 15.466009953420551, 157.5 19.47122063449069, 162.0 23.578178478201835, 166.5 27.81813928465393, 171.0 32.23095263550211, 175.5 36.86989764584401, 180.0 41.810314895778596, 180.0 46.88639405410129, 180.0 51.87508837796015, 180.0 56.789783851894256, 180.0 61.64236342367203, 180.0 66.44353569089878, 180.0 71.20309496497133, 180.0 75.9301322524279, 180.0 80.63321111564838, 135.0 90.0))", DiscreteGlobalGridSystems.toSISPolygon(extent10).asText());

        final Envelope envelope = zone.getEnvelope();
        assertEquals(90, envelope.getMinimum(0), 0.0);
        assertEquals(180, envelope.getMaximum(0), 0.0);
        assertEquals(0, envelope.getMinimum(1), 0.0);
        assertEquals(90, envelope.getMaximum(1), 0.0);

    }

    @Test
    public void testBboxSearch() throws TransformException {

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -61.6166811325793, -60.69076201948624);
        env.setRange(1, 14.259315787550134, 15.029229808265578);
        S2Polygon envp = S2.toS2Polygon(env);


        final HealpixDggrs dggrs = new HealpixDggrs();
        final DiscreteGlobalGridSystem dggs = dggrs.getGridSystem();
        final DiscreteGlobalGridHierarchy dggh = dggs.getHierarchy();

        final Zone zone19 = dggh.getZone(19l);
        final Zone zone21 = dggh.getZone(21l);
        final Zone zone23 = dggh.getZone(23l);

        final S2Polygon geometry19 = DiscreteGlobalGridSystems.toS2Polygon(zone19.getGeographicExtent(10));
        final S2Polygon geometry21 = DiscreteGlobalGridSystems.toS2Polygon(zone21.getGeographicExtent(10));
        final S2Polygon geometry23 = DiscreteGlobalGridSystems.toS2Polygon(zone23.getGeographicExtent(10));

        Assert.assertFalse(envp.contains(S2LatLng.fromDegrees(40, 0).toPoint()));
        Assert.assertTrue(geometry19.intersects(envp));
        Assert.assertFalse(geometry21.intersects(envp));
        Assert.assertTrue(geometry23.intersects(envp));
    }

}
