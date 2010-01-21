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
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.container.ContextContainer2D;
import org.geotoolkit.display2d.service.DefaultPortrayalService;

import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.referencing.crs.DefaultCompoundCRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.crs.DefaultTemporalCRS;
import org.geotoolkit.referencing.crs.DefaultVerticalCRS;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import static org.junit.Assert.*;

/**
 * Test envelope configuration on J2DCanvas.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class NDEnvelopeTest {

    public NDEnvelopeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

     @Test
     public void test4DEnvelope() throws Exception {
         final J2DCanvas canvas = new J2DCanvasBuffered(DefaultGeographicCRS.WGS84, new Dimension(800,600));

         //check size
         assertTrue( canvas.getDisplayBounds().getBounds().width == 800 );
         assertTrue( canvas.getDisplayBounds().getBounds().height == 600 );

         //check sended temporal and elevation
         Date[] temps = canvas.getController().getTemporalRange();
         Double[] elev = canvas.getController().getElevationRange();
         assertTrue(temps[0] == null);
         assertTrue(temps[1] == null);
         assertTrue(elev[0] == null);
         assertTrue(elev[1] == null);

         CoordinateReferenceSystem crs = new DefaultCompoundCRS(
               "",
               DefaultGeographicCRS.WGS84,
               DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT,
               DefaultTemporalCRS.JAVA);

         final GeneralEnvelope env = new GeneralEnvelope(crs);
         env.setRange(0, -170, 170);
         env.setRange(1, -80, 80);
         env.setRange(2, -50, 150);
         env.setRange(3, 3000, 6000);

         canvas.getController().setVisibleArea(env);

         temps = canvas.getController().getTemporalRange();
         elev = canvas.getController().getElevationRange();
         assertTrue(temps[0] != null);
         assertTrue(temps[1] != null);
         assertTrue(elev[0] != null);
         assertTrue(elev[1] != null);
         assertTrue(elev[0] == -50);
         assertTrue(elev[1] == 150);
         assertTrue(temps[0].getTime() == 3000);
         assertTrue(temps[1].getTime() == 6000);

     }

     @Test
     public void testCreationWith4Denvelope() throws PortrayalException{

         CoordinateReferenceSystem crs = new DefaultCompoundCRS(
               "",
               DefaultGeographicCRS.WGS84,
               DefaultVerticalCRS.ELLIPSOIDAL_HEIGHT,
               DefaultTemporalCRS.JAVA);

         final GeneralEnvelope env = new GeneralEnvelope(crs);
         env.setRange(0, -170, 170);
         env.setRange(1, -80, 80);
         env.setRange(2, -50, 150);
         env.setRange(3, 3000, 6000);

         MapContext context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);

         //was raising an error since we asked a 4D envelope with a 2D context
         //the canvas should change the crs to 2D to pass this test
         DefaultPortrayalService.portray(context, env, new Dimension(800, 600), true);

     }


}