/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
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
package org.geotoolkit.metadata.dimap;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.util.DomUtilities;

import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DimapAccessorTest {

    private final Element dimap;

    public DimapAccessorTest() throws ParserConfigurationException, SAXException, IOException {
        Document doc = DomUtilities.read(DimapAccessorTest.class.getResourceAsStream("/org/geotoolkit/image/dimap/spotscene.xml"));
        dimap = doc.getDocumentElement();
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
    public void testReadCRS() throws NoSuchAuthorityCodeException, FactoryException {
        final CoordinateReferenceSystem crs = DimapAccessor.readCRS(dimap);

        assertNotNull(crs);
        assertEquals(crs, CRS.decode("EPSG:32738"));

    }

    @Test
    public void testReadAffine() throws FactoryException, TransformException{
        final AffineTransform trs = DimapAccessor.readGridToCRS(dimap);

        final AffineTransform expected = new AffineTransform(10, 0, 0, -20, 300, 700);

        assertNotNull(trs);
        assertEquals(expected, trs);


    }

    @Test
    public void testReadColorBandsMapping(){
        final int[] mapping = DimapAccessor.readColorBandMapping(dimap);

        assertNotNull(mapping);
        assertEquals(3, mapping.length);

        //band index must start at 0 not like in the xml where it starts at one.
        assertEquals(0, mapping[0]);
        assertEquals(1, mapping[1]);
        assertEquals(2, mapping[2]);
    }

    @Test
    public void testReadRasterDimension(){
        final int[] dims = DimapAccessor.readRasterDimension(dimap);

        assertNotNull(dims);
        assertEquals(3, dims.length);

        assertEquals(7308, dims[0]); //rows
        assertEquals(7762, dims[1]); // cols
        assertEquals(4, dims[2]); //bands
    }


}
