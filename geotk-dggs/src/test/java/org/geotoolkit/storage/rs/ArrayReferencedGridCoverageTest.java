/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.storage.rs;

import org.geotoolkit.referencing.rs.Code;
import org.geotoolkit.referencing.rs.ReferenceSystems;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.BufferedGridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridOrientation;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.DataType;
import org.apache.sis.image.internal.shared.RasterFactory;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.MemoryGridCoverageResource;
import org.apache.sis.util.iso.Names;
import org.geotoolkit.dggs.healpix.HealpixDggrs;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.storage.rs.internal.shared.BandedCodeIterator;
import org.geotoolkit.storage.rs.internal.shared.CodeTransforms;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class ArrayReferencedGridCoverageTest {

    @Test
    public void readTest() throws Exception {

        final DiscreteGlobalGridReferenceSystem dggrs = new HealpixDggrs();
        final GridCoverageResource source = create4D();
        final CodedResource resource = CodedCoverages.viewAsDggrs(Names.createLocalName(null, null, "coverage4d"), source, dggrs);

        final CodedGeometry query;
        { //prepare the query
            final List<Object> rootZoneIds = dggrs.getGridSystem().getHierarchy().getGrids().get(0).getZones().map(Zone::getIdentifier).toList();

            final GeneralEnvelope verticalEnv = new GeneralEnvelope(CommonCRS.Vertical.ELLIPSOIDAL.crs());
            verticalEnv.setRange(0, 100, 300);
            final GeneralEnvelope temporalEnv = new GeneralEnvelope(CommonCRS.Temporal.JAVA.crs());
            temporalEnv.setRange(0,
                    Instant.parse("2000-06-10T10:00:00Z").toEpochMilli(),
                    Instant.parse("2000-06-10T12:00:00Z").toEpochMilli());
            final GridGeometry vertivalGrid = new GridGeometry(new GridExtent(null, 0, 2, true), verticalEnv, GridOrientation.HOMOTHETY);
            final GridGeometry temporalGrid = new GridGeometry(new GridExtent(null, 0, 2, true), temporalEnv, GridOrientation.HOMOTHETY);

            final ReferenceSystem rs = ReferenceSystems.createCompound(dggrs, verticalEnv.getCoordinateReferenceSystem(), temporalEnv.getCoordinateReferenceSystem());
            final GridExtent extent = new GridExtent(null, new long[]{0,0,0}, new long[]{rootZoneIds.size(), 3, 3}, false);
            final CodeTransform gridToRS = CodeTransforms.compound(
                    CodeTransforms.toTransform(dggrs, rootZoneIds),
                    CodeTransforms.toTransform(vertivalGrid),
                    CodeTransforms.toTransform(temporalGrid)
                );

            query = new CodedGeometry(rs, extent, gridToRS, null);
        }


        final CodedCoverage coverage = resource.read(query);
        final CodeTransform gridToRS = coverage.getGeometry().getGridToRS();
        final BandedCodeIterator iterator = (BandedCodeIterator) coverage.createIterator();

        while (iterator.next()) {
            final int[] gridPosition = iterator.getPosition();
            Code code = gridToRS.toCode(gridPosition);
            Assert.assertNotNull(code);
            double[] cell = iterator.getCell((double[])null);
            double v = cell[0];

            switch (gridPosition[2]) { //time axis
                case 0 :
                    switch (gridPosition[1]) { //vertical axis
                        case 0: Assert.assertEquals(1.0, v, 0.0); break;
                        case 1: Assert.assertEquals(2.0, v, 0.0); break;
                        case 2: Assert.assertEquals(3.0, v, 0.0); break;
                        default : Assert.fail();
                    } break;
                case 1 :
                    switch (gridPosition[1]) { //vertical axis
                        case 0: Assert.assertEquals(4.0, v, 0.0); break;
                        case 1: Assert.assertEquals(5.0, v, 0.0); break;
                        case 2: Assert.assertEquals(6.0, v, 0.0); break;
                        default : Assert.fail();
                    } break;
                case 2 :
                    switch (gridPosition[1]) { //vertical axis
                        case 0: Assert.assertEquals(7.0, v, 0.0); break;
                        case 1: Assert.assertEquals(8.0, v, 0.0); break;
                        case 2: Assert.assertEquals(9.0, v, 0.0); break;
                        default : Assert.fail();
                    } break;
                default : Assert.fail();
            }
        }
    }

    private static GridCoverageResource create4D() throws Exception {
        final GeneralEnvelope dataEnv = new GeneralEnvelope(CRS.compound(
                CommonCRS.WGS84.normalizedGeographic(),
                CommonCRS.Vertical.ELLIPSOIDAL.crs(),
                CommonCRS.Temporal.JAVA.crs()
        ));
        dataEnv.setRange(0, -180, 180);
        dataEnv.setRange(1, -90, 90);
        dataEnv.setRange(2, 100, 300);
        dataEnv.setRange(3,
                Instant.parse("2000-06-10T10:00:00Z").toEpochMilli(),
                Instant.parse("2000-06-10T12:00:00Z").toEpochMilli());
        final GridGeometry dataGrid = new GridGeometry(
                new GridExtent(null, new long[4], new long[] { 2, 2, 3, 3 }, false),
                dataEnv,
                GridOrientation.DISPLAY
        );

        final ByteBuffer dataBuffer = ByteBuffer.wrap(new byte[] {
                // v0 t0
                1, 1,
                1, 1,
                // v1 t0
                2, 2,
                2, 2,
                // v2 t0
                3, 3,
                3, 3,
                // v0 t1
                4, 4,
                4, 4,
                // v1 t1
                5, 5,
                5, 5,
                // v2 t1
                6, 6,
                6, 6,
                // v0 t2
                7, 7,
                7, 7,
                // v1 t2
                8, 8,
                8, 8,
                // v2 t2
                9, 9,
                9, 9
        });

        final BufferedGridCoverage coverage = new BufferedGridCoverage(
                dataGrid,
                (List)List.of(new SampleDimension.Builder().setName("test").build()),
                RasterFactory.wrap(DataType.BYTE, dataBuffer)
        );
        return new MemoryGridCoverageResource(null, coverage, null) {
            @Override
            public Optional<GenericName> getIdentifier() {
                return Optional.of(Names.createLocalName(null, null, "test"));
            }
        };
    }
}
