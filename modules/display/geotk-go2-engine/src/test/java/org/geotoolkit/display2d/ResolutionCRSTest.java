/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

import org.apache.sis.geometry.GeneralDirectPosition;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.junit.Test;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 * Test Resolution class.
 *
 * @author Remi Marechal (Geomatys).
 */
public abstract class ResolutionCRSTest extends ResolutionTest {

    private final CoordinateReferenceSystem targetCrs;
    private final CoordinateReferenceSystem crsBase = CRS.decode("EPSG:4326");
    protected final GeneralDirectPosition lowerCorner = new GeneralDirectPosition(crsBase);
    protected final GeneralDirectPosition upperCorner = new GeneralDirectPosition(crsBase);

    public ResolutionCRSTest(CoordinateReferenceSystem crs1, CoordinateReferenceSystem crs2, double[] res, double ratio) throws NoninvertibleTransformException, NoSuchAuthorityCodeException, FactoryException {
        super(CRS.findMathTransform(crs1, crs2), res, ratio);
        targetCrs = crs2;
    }

    @Override
    protected void testFractEnvelope(Envelope envelopeDest) throws MismatchedDimensionException, TransformException {
        super.testFractEnvelope(CRS.transform(envelopeDest, targetCrs));
    }

    /**
     * Test on GreenWich meridian at North.
     *
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    @Test
    public void testA() throws MismatchedDimensionException, TransformException {
        lowerCorner.setOrdinate(0, -50);
        lowerCorner.setOrdinate(1, 70);
        upperCorner.setOrdinate(0, 50);
        upperCorner.setOrdinate(1, 89.9);
        this.testFractEnvelope(new GeneralEnvelope(lowerCorner, upperCorner));
    }

    /**
     * Test on GreenWich meridian and equator intersection.
     *
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    @Test
    public void testB() throws MismatchedDimensionException, TransformException {
        lowerCorner.setOrdinate(0, -50);
        lowerCorner.setOrdinate(1, -10);
        upperCorner.setOrdinate(0, 20);
        upperCorner.setOrdinate(1, 25);
        this.testFractEnvelope(new GeneralEnvelope(lowerCorner, upperCorner));
    }

    /**
     * Test on GreenWich meridian at South.
     *
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    @Test
    public void testC() throws MismatchedDimensionException, TransformException {
        lowerCorner.setOrdinate(0, -50);
        lowerCorner.setOrdinate(1, -89.9);
        upperCorner.setOrdinate(0, 20);
        upperCorner.setOrdinate(1, -80);
        this.testFractEnvelope(new GeneralEnvelope(lowerCorner, upperCorner));
    }

    /**
     * Test at South West.
     *
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
     @Test
    public void testD() throws MismatchedDimensionException, TransformException {
        lowerCorner.setOrdinate(0, -179.9);
        lowerCorner.setOrdinate(1, -89.9);
        upperCorner.setOrdinate(0, -130);
        upperCorner.setOrdinate(1, -40);
        this.testFractEnvelope(new GeneralEnvelope(lowerCorner, upperCorner));
    }

     /**
     * Test on equator West.
     *
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    @Test
    public void testE() throws MismatchedDimensionException, TransformException {
        lowerCorner.setOrdinate(0, -179.9);
        lowerCorner.setOrdinate(1, -10);
        upperCorner.setOrdinate(0, -150);
        upperCorner.setOrdinate(1, 40);
        this.testFractEnvelope(new GeneralEnvelope(lowerCorner, upperCorner));
    }

    /**
     * Test at North West.
     *
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    @Test
    public void testF() throws MismatchedDimensionException, TransformException {
        lowerCorner.setOrdinate(0, -179.9);
        lowerCorner.setOrdinate(1, 70);
        upperCorner.setOrdinate(0, -160);
        upperCorner.setOrdinate(1, 89.9);
        this.testFractEnvelope(new GeneralEnvelope(lowerCorner, upperCorner));
    }

    /**
     * Test at South East.
     *
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
     @Test
    public void testG() throws MismatchedDimensionException, TransformException {
        lowerCorner.setOrdinate(0, 170);
        lowerCorner.setOrdinate(1, -89.9);
        upperCorner.setOrdinate(0, 189.9);
        upperCorner.setOrdinate(1, -40);
        this.testFractEnvelope(new GeneralEnvelope(lowerCorner, upperCorner));
    }

     /**
     * Test on equator East.
     *
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    @Test
    public void testH() throws MismatchedDimensionException, TransformException {
        lowerCorner.setOrdinate(0, 160);
        lowerCorner.setOrdinate(1, -10);
        upperCorner.setOrdinate(0, 189.9);
        upperCorner.setOrdinate(1, 40);
        this.testFractEnvelope(new GeneralEnvelope(lowerCorner, upperCorner));
    }

    /**
     * Test at North East.
     *
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    @Test
    public void testI() throws MismatchedDimensionException, TransformException {
        lowerCorner.setOrdinate(0, 160);
        lowerCorner.setOrdinate(1, 60);
        upperCorner.setOrdinate(0, 189.9);
        upperCorner.setOrdinate(1, 89.9);
        this.testFractEnvelope(new GeneralEnvelope(lowerCorner, upperCorner));
    }
}
