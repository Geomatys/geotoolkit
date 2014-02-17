/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
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
 * @module pending
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