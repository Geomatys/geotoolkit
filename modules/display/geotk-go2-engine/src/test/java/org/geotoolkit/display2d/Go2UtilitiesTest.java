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

package org.geotoolkit.display2d;

import java.util.Date;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import static org.junit.Assert.*;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class Go2UtilitiesTest {

    public Go2UtilitiesTest() {
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
    public void ensureCompoundCRSHaveName() throws TransformException{
        GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        Envelope cenv = GO2Utilities.combine(env,
                new Date[]{new Date(0),new Date(10000000)},
                new Double[]{-50d,150d});

        assertNotNull(cenv);
        assertNotNull(cenv.getCoordinateReferenceSystem().getName().getCode());
    }

}