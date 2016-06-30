/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.referencing;

import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.referencing.CRS;
import org.apache.sis.test.DependsOn;
import org.geotoolkit.test.TestBase;
import org.apache.sis.geometry.Envelopes;
import org.junit.*;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;


/**
 * Tests the combination of EPSG database with grids like NADCON.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
@DependsOn(CRS_WithEpsgTest.class)
public final strictfp class CRS_WithGridTest extends TestBase {
    /**
     * Tests transformation NADCON grids.
     *
     * @throws Exception Should not happen.
     */
    @Test
    @Ignore
    public void testNADCON() throws Exception {
        assumeTrue(false /*Files.isDirectory(Installation.NADCON.directory(true))*/);
        assumeTrue(false /*isEpsgFactoryAvailable()*/);

        final MathTransform tr;
        final DirectPosition2D sourcePt, targetPt;
        final CoordinateReferenceSystem sourceCRS, targetCRS;

        sourceCRS = CRS.forCode("EPSG:26769"); // NAD27 Idaho, in feets.
        targetCRS = CRS.forCode("EPSG:26969"); // NAD83 Idaho, in metres.
        sourcePt  = new DirectPosition2D(30000.0, 40000.0);
        targetPt  = new DirectPosition2D();
        tr = CRS.findOperation(sourceCRS, targetCRS, null).getMathTransform();
        assertSame(targetPt, tr.transform(sourcePt, targetPt));

        assertEquals(356671.38, targetPt.x, 1E-2);
        assertEquals( 12183.11, targetPt.y, 1E-2);
    }

    /**
     * Tests a transform from "<cite>Réseau Géodésique Français 1993</cite>" to
     * "<cite>Nouvelle Triangulation Française (Paris)</cite>". This transform uses
     * the inverse of a datum shift grid.
     *
     * @throws Exception Should not happen.
     *
     * @since 3.20
     */
    @Test
    @Ignore
    public void testNTF() throws Exception {
        assumeTrue(false /*Files.isDirectory(Installation.NADCON.directory(true))*/);
        assumeTrue(false /*isEpsgFactoryAvailable()*/);

        final CoordinateReferenceSystem sourceCRS = CRS.forCode("EPSG:2154");  // Réseau Géodésique Français 1993
        final CoordinateReferenceSystem targetCRS = CRS.forCode("EPSG:27582"); // Nouvelle Triangulation Française (Paris)
        final GeneralEnvelope source = new GeneralEnvelope("BOX(-2000000 4000000, 2000000 4000000)");
        source.setCoordinateReferenceSystem(sourceCRS);
        final Envelope target = Envelopes.transform(source, targetCRS);

        assertEquals(-2033792.23, target.getMinimum(0), 1E-2);
        assertEquals( 1976167.67, target.getMaximum(0), 1E-2);
        assertEquals( -458155.31, target.getMinimum(1), 1E-2);
        assertEquals( -426020.22, target.getMaximum(1), 1E-2);
    }
}
