/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.referencing.rs;

import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.referencing.gazetteer.GazetteerException;
import org.apache.sis.referencing.gazetteer.GeohashReferenceSystem;
import org.apache.sis.referencing.gazetteer.MilitaryGridReferenceSystem;
import org.geotoolkit.referencing.rs.internal.shared.CodeOperations;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.operation.NoninvertibleTransformException;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CodeOperationTest {

    private final MilitaryGridReferenceSystem MGRS = new MilitaryGridReferenceSystem();
    private final TemporalCRS TIME_JAVA = CommonCRS.Temporal.JAVA.crs();
    private final TemporalCRS TIME_JULIAN = CommonCRS.Temporal.JULIAN.crs();
    private final GeohashReferenceSystem GEOHASH;

    public CodeOperationTest() throws GazetteerException {
        GEOHASH = new GeohashReferenceSystem(GeohashReferenceSystem.Format.BASE32, CommonCRS.WGS84.normalizedGeographic());
    }

    @Test
    public void MGRStoGeoHash84() throws FactoryException, TransformException {

        final CodeOperation operation = ReferenceSystems.findOperation(MGRS, GEOHASH, null);
        Assert.assertTrue(operation instanceof CodeOperations.Concatenate);
        final CodeOperations.Concatenate co = (CodeOperations.Concatenate) operation;
        Assert.assertTrue(co.getOperation1() instanceof CodeOperations.RbiToCrs);
        Assert.assertTrue(co.getOperation2() instanceof CodeOperations.CrsToRbi);
        final CodeOperations.RbiToCrs op1 = (CodeOperations.RbiToCrs) co.getOperation1();
        final CodeOperations.CrsToRbi op2 = (CodeOperations.CrsToRbi) co.getOperation2();
        Assert.assertEquals(op1.getSourceRS(), MGRS);
        Assert.assertEquals(op1.getTargetRS(), CommonCRS.WGS84.normalizedGeographic());
        Assert.assertEquals(op2.getSourceRS(), CommonCRS.WGS84.normalizedGeographic());
        Assert.assertEquals(op2.getTargetRS(), GEOHASH);

        //test conversion
        final Code code = operation.transform(new Code(MGRS, "4Q FJ 12 67"), null);
        Assert.assertEquals("87z9y8fhdbff", code.getOrdinate(0));

    }

    @Test
    public void MGRS3DtoGeoHash() throws FactoryException {

        {
            final ReferenceSystem mrgs3d = ReferenceSystems.createCompound(MGRS, TIME_JAVA);
            final CodeOperation operation = ReferenceSystems.findOperation(mrgs3d, GEOHASH, null);

            Assert.assertTrue(operation instanceof CodeOperations.Concatenate);
            final CodeOperations.Concatenate concat1 = (CodeOperations.Concatenate) operation;
            Assert.assertTrue(concat1.getOperation1() instanceof CodeOperations.Reorder);
            Assert.assertTrue(concat1.getOperation2() instanceof CodeOperations.Concatenate);
            final CodeOperations.Reorder op1 = (CodeOperations.Reorder) concat1.getOperation1();
            final CodeOperations.Concatenate op2 = (CodeOperations.Concatenate) concat1.getOperation2();

            Assert.assertArrayEquals(new int[]{0},op1.getTargetMapping());

            Assert.assertTrue(op2.getOperation1() instanceof CodeOperations.RbiToCrs);
            Assert.assertTrue(op2.getOperation2() instanceof CodeOperations.CrsToRbi);
            final CodeOperations.RbiToCrs op11 = (CodeOperations.RbiToCrs) op2.getOperation1();
            final CodeOperations.CrsToRbi op12 = (CodeOperations.CrsToRbi) op2.getOperation2();
            Assert.assertEquals(op11.getSourceRS(), MGRS);
            Assert.assertEquals(op11.getTargetRS(), CommonCRS.WGS84.normalizedGeographic());
            Assert.assertEquals(op12.getSourceRS(), CommonCRS.WGS84.normalizedGeographic());
            Assert.assertEquals(op12.getTargetRS(), GEOHASH);

            try {
                operation.inverse();
                Assert.fail("Operation can not be inverted");
            } catch (NoninvertibleTransformException e) {
                //ok
            }
        }

        {
            final ReferenceSystem mrgs3d = ReferenceSystems.createCompound(TIME_JAVA, MGRS);
            final CodeOperation operation = ReferenceSystems.findOperation(mrgs3d, GEOHASH, null);

            Assert.assertTrue(operation instanceof CodeOperations.Concatenate);
            final CodeOperations.Concatenate concat1 = (CodeOperations.Concatenate) operation;
            Assert.assertTrue(concat1.getOperation1() instanceof CodeOperations.Reorder);
            Assert.assertTrue(concat1.getOperation2() instanceof CodeOperations.Concatenate);
            final CodeOperations.Reorder op1 = (CodeOperations.Reorder) concat1.getOperation1();
            final CodeOperations.Concatenate op2 = (CodeOperations.Concatenate) concat1.getOperation2();

            Assert.assertArrayEquals(new int[]{1},op1.getTargetMapping());

            Assert.assertTrue(op2.getOperation1() instanceof CodeOperations.RbiToCrs);
            Assert.assertTrue(op2.getOperation2() instanceof CodeOperations.CrsToRbi);
            final CodeOperations.RbiToCrs op11 = (CodeOperations.RbiToCrs) op2.getOperation1();
            final CodeOperations.CrsToRbi op12 = (CodeOperations.CrsToRbi) op2.getOperation2();
            Assert.assertEquals(op11.getSourceRS(), MGRS);
            Assert.assertEquals(op11.getTargetRS(), CommonCRS.WGS84.normalizedGeographic());
            Assert.assertEquals(op12.getSourceRS(), CommonCRS.WGS84.normalizedGeographic());
            Assert.assertEquals(op12.getTargetRS(), GEOHASH);

            try {
                operation.inverse();
                Assert.fail("Operation can not be inverted");
            } catch(NoninvertibleTransformException e) {
                //ok
            }
        }

        {
            try {
                final ReferenceSystem mrgs3d = ReferenceSystems.createCompound(TIME_JAVA, MGRS);
                final CodeOperation operation = ReferenceSystems.findOperation(GEOHASH, mrgs3d, null);
                Assert.fail("Operation should not be possible");
            } catch (FactoryException e) {
                //ok
            }
        }
    }

    @Test
    public void MGRS3DtoGeoHash3D() throws FactoryException {

        {
            final ReferenceSystem mrgs3d = ReferenceSystems.createCompound(MGRS, TIME_JAVA);
            final ReferenceSystem geohash3d = ReferenceSystems.createCompound(GEOHASH, TIME_JULIAN);
            final CodeOperation operation = ReferenceSystems.findOperation(mrgs3d, geohash3d, null);

            Assert.assertTrue(operation instanceof CodeOperations.Compound);
            final CodeOperations.Compound compound = (CodeOperations.Compound) operation;
            Assert.assertTrue(compound.getOperation1() instanceof CodeOperations.Concatenate);
            Assert.assertTrue(compound.getOperation2() instanceof CodeOperations.CrsToCrs);
            final CodeOperations.Concatenate op1 = (CodeOperations.Concatenate) compound.getOperation1();
            final CodeOperations.CrsToCrs op2 = (CodeOperations.CrsToCrs) compound.getOperation2();

            Assert.assertTrue(op1 instanceof CodeOperations.Concatenate);
            final CodeOperations.Concatenate co = (CodeOperations.Concatenate) op1;
            Assert.assertTrue(co.getOperation1() instanceof CodeOperations.RbiToCrs);
            Assert.assertTrue(co.getOperation2() instanceof CodeOperations.CrsToRbi);
            final CodeOperations.RbiToCrs op11 = (CodeOperations.RbiToCrs) co.getOperation1();
            final CodeOperations.CrsToRbi op12 = (CodeOperations.CrsToRbi) co.getOperation2();
            Assert.assertEquals(op11.getSourceRS(), MGRS);
            Assert.assertEquals(op11.getTargetRS(), CommonCRS.WGS84.normalizedGeographic());
            Assert.assertEquals(op12.getSourceRS(), CommonCRS.WGS84.normalizedGeographic());
            Assert.assertEquals(op12.getTargetRS(), GEOHASH);

            Assert.assertEquals(op2.getSourceRS(), TIME_JAVA);
            Assert.assertEquals(op2.getTargetRS(), TIME_JULIAN);

        }

    }
}
