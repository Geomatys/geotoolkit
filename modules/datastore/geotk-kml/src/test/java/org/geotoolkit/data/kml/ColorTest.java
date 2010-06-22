package org.geotoolkit.data.kml;

import java.awt.Color;
import org.geotoolkit.data.kml.model.KmlException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Samuel Andrés
 */
public class ColorTest {

    public ColorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

     @Test
     public void colorParsingTest() throws KmlException {

         String h;
         for (int i = 0; i<=255; i++){
             h = Integer.toHexString(i);
             h = ((h.length() == 1) ? "0" : "") + h;
             Color color1 = new Color(i,i,i,i);
             Color color2 = KmlUtilities.parseColor(h+h+h+h);
             assertEquals(color2, color1);
         }
     }

     @Test
     public void kmlColorTest() throws KmlException {

         String h;
         for (int i = 0; i<=255; i++){
             h = Integer.toHexString(i);
             h = ((h.length() == 1) ? "0" : "") + h;
             String color1 = h+h+h+h;
             String color2 = KmlUtilities.toKmlColor(new Color(i,i,i,i));
             assertEquals(color2, color1);
         }
     }

}