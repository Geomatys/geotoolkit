/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.display2d.canvas;

import java.awt.Dimension;
import java.util.Date;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.privy.GeodeticObjectBuilder;
import org.apache.sis.map.MapLayers;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.map.MapBuilder;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 * Test envelope configuration on J2DCanvas.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class J2DCanvasTest {

    public J2DCanvasTest() {
    }

    @Test
    public void test4DEnvelope() throws Exception {
         final J2DCanvas canvas = new J2DCanvasBuffered(CommonCRS.WGS84.normalizedGeographic(), new Dimension(800,600));

         //check size
         assertEquals(800, canvas.getDisplayBounds().getBounds().width);
         assertEquals(600, canvas.getDisplayBounds().getBounds().height);

         //check sended temporal and elevation
         Date[] temps = canvas.getTemporalRange();
         Double[] elev = canvas.getElevationRange();
         assertNull(temps);
         assertNull(elev);

        CoordinateReferenceSystem crs = new GeodeticObjectBuilder().addName("WGS84-4D")
                                                                   .createCompoundCRS(CommonCRS.WGS84.normalizedGeographic(),
                                                                                      CommonCRS.Vertical.ELLIPSOIDAL.crs(),
                                                                                      CommonCRS.Temporal.JAVA.crs());

        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, -170, 170);
        env.setRange(1, -80, 80);
        env.setRange(2, -50, 150);
        env.setRange(3, 3000, 6000);

        canvas.setObjectiveCRS(crs);
        canvas.setVisibleArea(env);

        temps = canvas.getTemporalRange();
        elev = canvas.getElevationRange();
        assertNotNull(temps[0]);
        assertNotNull(temps[1]);
        assertNotNull(elev[0]);
        assertNotNull(elev[1]);
        assertEquals(-50, elev[0], 0);
        assertEquals(150, elev[1], 0);
        assertEquals(3000, temps[0].getTime());
        assertEquals(6000, temps[1].getTime());
    }

    @Test
    public void testCreationWith4Denvelope() throws PortrayalException, FactoryException {
        CoordinateReferenceSystem crs = new GeodeticObjectBuilder().addName("WGS84-4D")
                                                                   .createCompoundCRS(CommonCRS.WGS84.normalizedGeographic(),
                                                                                      CommonCRS.Vertical.ELLIPSOIDAL.crs(),
                                                                                      CommonCRS.Temporal.JAVA.crs());

        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, -170, 170);
        env.setRange(1, -80, 80);
        env.setRange(2, -50, 150);
        env.setRange(3, 3000, 6000);

        MapLayers context = MapBuilder.createContext(CommonCRS.WGS84.normalizedGeographic());

        //was raising an error since we asked a 4D envelope with a 2D context
        //the canvas should change the crs to 2D to pass this test
        DefaultPortrayalService.portray(new CanvasDef(new Dimension(800, 600), env),  new SceneDef(context));
     }

}
