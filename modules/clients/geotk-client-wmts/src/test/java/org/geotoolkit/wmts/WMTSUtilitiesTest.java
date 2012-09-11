/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.wmts;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.geometry.Envelopes;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import static org.junit.Assert.*;

/**
 * Test if {@link CrsChoice#getAppropriateCRS(org.opengis.geometry.Envelope, java.util.List)}
 * method return {@code CoordinateReferenceSystem} with lesser deformation from a 
 * {@code CoordinateReferenceSystem} referent.
 *
 * @author Rémi Marechal (Geomatys).
 */
public class WMTSUtilitiesTest {

    CoordinateReferenceSystem crs1, crs2, crs3, crs84, crsGeo, crsLambertZ1, crsLambertZ2;
    GeneralEnvelope envelop;

    public WMTSUtilitiesTest() throws NoSuchAuthorityCodeException, FactoryException {
        crs1 = CRS.decode("EPSG:3395");
        crs2 = CRS.decode("EPSG:2154");
        crs3 = CRS.decode("EPSG:3031");
        crsGeo = CRS.decode("EPSG:4326");
        crsLambertZ1 = CRS.decode("EPSG:27571");
        crsLambertZ2 = CRS.decode("EPSG:27572");
        crs84 = CRS.decode("CRS:84");
        envelop = new GeneralEnvelope(crsGeo);
        envelop.setRange(0, 0, 50);
        envelop.setRange(1, 40, 50);

    }

    /**
     * Test about "EPSG:27571" {@code CoordinateReferenceSystem}.
     * Test at : 2*10E-6 meters precision.
     * 
     * @throws FactoryException
     * @throws TransformException
     */
    @Test
    public void testGetAppropriateCRS1() throws TransformException, FactoryException {
        final List<CoordinateReferenceSystem> listCrs = new ArrayList<CoordinateReferenceSystem>();
        Hints.putSystemDefault(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);
        listCrs.add(crs1);
        listCrs.add(crs2);
        listCrs.add(crs3);
        listCrs.add(crsGeo);
        listCrs.add(crsLambertZ2);
        final Envelope env = Envelopes.transform(envelop, crsLambertZ1);
        final double xMin = env.getMinimum(0);
        final double yMin = env.getMinimum(1);
        final double xMax = env.getMaximum(0);
        final double yMax = env.getMaximum(1);

        final GeneralEnvelope env2 = new GeneralEnvelope(crsLambertZ1);
        env2.setRange(0, xMin, xMax);
        env2.setRange(1, yMin, yMax);
        assertTrue(crsLambertZ2.equals(WMTSUtilities.getAppropriateCRS(env2, listCrs)));

        int iteration = 1000;
        final double demiNorm = 0.001;
        final double pas = demiNorm / iteration;
        final double xE = (xMin + xMax) / 2;
        final double yE = (yMin + yMax) / 2;
        for (; iteration > 1; iteration--) {
            env2.setRange(0, xE - iteration * pas, xE + iteration * pas);
            env2.setRange(1, yE - iteration * pas, yE + iteration * pas);
            final CoordinateReferenceSystem result = WMTSUtilities.getAppropriateCRS(env2, listCrs);
            assertTrue(crsLambertZ2.equals(result));
        }

    }

    /**
     * Test about "WGS84" {@code CoordinateReferenceSystem}. 
     * Test at : 2*10E-14 ° precision.
     * 
     * @throws FactoryException
     * @throws TransformException
     */
    @Test
    public void testGetAppropriateCRS2() throws FactoryException, TransformException {
        final List<CoordinateReferenceSystem> listCrs = new ArrayList<CoordinateReferenceSystem>();
        Hints.putSystemDefault(Hints.LENIENT_DATUM_SHIFT, Boolean.TRUE);
        listCrs.add(crs1);
        listCrs.add(crs2);
        listCrs.add(crs3);
        listCrs.add(crsLambertZ2);
        listCrs.add(crsLambertZ1);
        final Envelope env = Envelopes.transform(envelop, crs84);
        final double xMin = env.getMinimum(0);
        final double yMin = env.getMinimum(1);
        final double xMax = env.getMaximum(0);
        final double yMax = env.getMaximum(1);

        final GeneralEnvelope env2 = new GeneralEnvelope(crs84);
        env2.setRange(0, xMin, xMax);
        env2.setRange(1, yMin, yMax);
        assertTrue(crs1.equals(WMTSUtilities.getAppropriateCRS(env2, listCrs)));

        int iteration = 1000;
        final double demiNorm = 1E-11;
        final double pas = demiNorm / iteration;
        final double xE = (xMin + xMax) / 2;
        final double yE = (yMin + yMax) / 2;
        for (; iteration > 0; iteration--) {
            env2.setRange(0, xE - iteration * pas, xE + iteration * pas);
            env2.setRange(1, yE - iteration * pas, yE + iteration * pas);
            final CoordinateReferenceSystem result = WMTSUtilities.getAppropriateCRS(env2, listCrs);
            assertTrue(crs1.equals(result));
        }
    }
}