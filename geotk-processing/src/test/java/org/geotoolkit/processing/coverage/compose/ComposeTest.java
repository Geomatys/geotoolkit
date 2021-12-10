/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2017, Geomatys
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
package org.geotoolkit.processing.coverage.compose;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.process.ProcessException;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Jean-Loup Amiot (Geomatys)
 */
public class ComposeTest {

    public static final GeometryFactory GF = JTS.getFactory();

    @Test
    public void simpleImageTest() throws ProcessException, MismatchedDimensionException, TransformException {

        final List<Map.Entry<GridCoverage,Geometry[]>> inputs = new ArrayList<>();

        final CoordinateReferenceSystem crs = CommonCRS.WGS84. normalizedGeographic();
        final GeneralEnvelope envelope = new GeneralEnvelope(crs);
        envelope.setRange(0, 0, 2);
        envelope.setRange(1, 0, 2);

        {
            final BufferedImage image = BufferedImages.createImage(2, 2, 1, DataBuffer.TYPE_INT);
            image.getRaster().setSample(0, 0, 0, 1);
            image.getRaster().setSample(1, 0, 0, 1);
            image.getRaster().setSample(0, 1, 0, 1);
            image.getRaster().setSample(1, 1, 0, 1);

            final GridCoverage gridCoverage2d = new GridCoverageBuilder()
                    .setValues(image)
                    .setDomain(envelope)
                    .flipGridAxis(1)
                    .build();

            Geometry geometry = GF.createPolygon(
                new Coordinate[] {
                    new Coordinate(0, 0),
                    new Coordinate(2, 0),
                    new Coordinate(2, 1),
                    new Coordinate(0, 1),
                    new Coordinate(0, 0)
                }
            );
            geometry = JTS.transform(geometry, gridCoverage2d.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER));

            inputs.add(new AbstractMap.SimpleImmutableEntry<>(gridCoverage2d,new Geometry[]{geometry,null}));
        }

        {
            final BufferedImage image = BufferedImages.createImage(2, 2, 1, DataBuffer.TYPE_INT);
            image.getRaster().setSample(0, 0, 0, 2);
            image.getRaster().setSample(1, 0, 0, 2);
            image.getRaster().setSample(0, 1, 0, 2);
            image.getRaster().setSample(1, 1, 0, 2);

            final GridCoverage gridCoverage2d = new GridCoverageBuilder()
                    .setValues(image)
                    .setDomain(envelope)
                    .flipGridAxis(1)
                    .build();

            Geometry geometry = GF.createPolygon(
                new Coordinate[] {
                    new Coordinate(0, 0),
                    new Coordinate(1, 0),
                    new Coordinate(1, 2),
                    new Coordinate(0, 2),
                    new Coordinate(0, 0)
                }
            );
            geometry = JTS.transform(geometry, gridCoverage2d.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER));

            inputs.add(new AbstractMap.SimpleImmutableEntry<>(gridCoverage2d,new Geometry[]{geometry,null}));
        }

        final GridCoverage gridCoverage = new Compose(inputs,null).executeNow();
        final WritableRaster raster = ((BufferedImage) gridCoverage.render(null)).getRaster();
        assertEquals(1, raster.getSample(0, 0, 0));
        assertEquals(1, raster.getSample(1, 0, 0));
        assertEquals(2, raster.getSample(0, 1, 0));
        assertEquals(0, raster.getSample(1, 1, 0));
    }


    @Test
    public void multipleImageTest() throws ProcessException, MismatchedDimensionException, TransformException {

        final List<Map.Entry<GridCoverage,Geometry[]>> inputs = new ArrayList<>();

        final CoordinateReferenceSystem crs = CommonCRS.WGS84. normalizedGeographic();
        final GeneralEnvelope envelope = new GeneralEnvelope(crs);
        envelope.setRange(0, 0, 2);
        envelope.setRange(1, 0, 2);

        {
            final BufferedImage image = BufferedImages.createImage(2, 2, 1, DataBuffer.TYPE_INT);
            image.getRaster().setSample(0, 0, 0, 1);
            image.getRaster().setSample(1, 0, 0, 1);
            image.getRaster().setSample(0, 1, 0, 1);
            image.getRaster().setSample(1, 1, 0, 1);

            final GridCoverage gridCoverage2d = new GridCoverageBuilder()
                    .setValues(image)
                    .setDomain(envelope)
                    .flipGridAxis(1)
                    .build();

            Geometry geometry = GF.createPolygon(
                new Coordinate[] {
                    new Coordinate(0, 0),
                    new Coordinate(1.9, 0),
                    new Coordinate(1.9, 0.9),
                    new Coordinate(0, 0.9),
                    new Coordinate(0, 0)
                }
            );
            geometry = JTS.transform(geometry, gridCoverage2d.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER));

            inputs.add(new AbstractMap.SimpleImmutableEntry<>(gridCoverage2d,new Geometry[]{geometry,null}));
        }

        {
            final BufferedImage image = BufferedImages.createImage(2, 2, 1, DataBuffer.TYPE_INT);
            image.getRaster().setSample(0, 0, 0, 2);
            image.getRaster().setSample(1, 0, 0, 2);
            image.getRaster().setSample(0, 1, 0, 2);
            image.getRaster().setSample(1, 1, 0, 2);

            final GridCoverage gridCoverage2d = new GridCoverageBuilder()
                    .setValues(image)
                    .setDomain(envelope)
                    .flipGridAxis(1)
                    .build();

            Geometry geometry = GF.createPolygon(
                new Coordinate[] {
                    new Coordinate(0, 0),
                    new Coordinate(0.9, 0),
                    new Coordinate(0.9, 1.9),
                    new Coordinate(0, 1.9),
                    new Coordinate(0, 0)
                }
            );
            geometry = JTS.transform(geometry, gridCoverage2d.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER));

            inputs.add(new AbstractMap.SimpleImmutableEntry<>(gridCoverage2d,new Geometry[]{geometry,null}));
        }

        {
            final BufferedImage image = BufferedImages.createImage(2, 2, 1, DataBuffer.TYPE_INT);
            image.getRaster().setSample(0, 0, 0, 3);
            image.getRaster().setSample(1, 0, 0, 3);
            image.getRaster().setSample(0, 1, 0, 3);
            image.getRaster().setSample(1, 1, 0, 3);

            final GridCoverage gridCoverage2d = new GridCoverageBuilder()
                    .setValues(image)
                    .setDomain(envelope)
                    .flipGridAxis(1)
                    .build();

            Geometry geometry = GF.createPolygon(
                new Coordinate[] {
                    new Coordinate(0, 1),
                    new Coordinate(1.9, 1),
                    new Coordinate(1.9, 1.9),
                    new Coordinate(0, 1.9),
                    new Coordinate(0, 1)
                }
            );
            geometry = JTS.transform(geometry, gridCoverage2d.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER));

            inputs.add(new AbstractMap.SimpleImmutableEntry<>(gridCoverage2d,new Geometry[]{geometry,null}));
        }

        final GridCoverage gridCoverage = new Compose(inputs,null).executeNow();
        final WritableRaster raster = ((BufferedImage)gridCoverage.render(null)).getRaster();

        assertEquals(1, raster.getSample(0, 0, 0));
        assertEquals(1, raster.getSample(1, 0, 0));
        assertEquals(2, raster.getSample(0, 1, 0));
        assertEquals(3, raster.getSample(1, 1, 0));
    }

    @Test
    public void simpleOffsetTest() throws MismatchedDimensionException, TransformException, ProcessException {

        final List<Map.Entry<GridCoverage,Geometry[]>> inputs = new ArrayList<>();

        {
            final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
            final GeneralEnvelope envelope = new GeneralEnvelope(crs);
            envelope.setRange(0, 0, 2);
            envelope.setRange(1, 0, 2);

            final BufferedImage image = BufferedImages.createImage(2, 2, 1, DataBuffer.TYPE_INT);
            image.getRaster().setSample(0, 0, 0, 1);
            image.getRaster().setSample(1, 0, 0, 1);
            image.getRaster().setSample(0, 1, 0, 1);
            image.getRaster().setSample(1, 1, 0, 1);

            final GridCoverage gridCoverage2d = new GridCoverageBuilder()
                    .setValues(image)
                    .setDomain(envelope)
                    .flipGridAxis(1)
                    .build();

            Geometry geometry = GF.createPolygon(
                new Coordinate[] {
                    new Coordinate(0, 0),
                    new Coordinate(2, 0),
                    new Coordinate(2, 2),
                    new Coordinate(0, 2),
                    new Coordinate(0, 0)
                }
            );
            geometry = JTS.transform(geometry, gridCoverage2d.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER));

            inputs.add(new AbstractMap.SimpleImmutableEntry<>(gridCoverage2d,new Geometry[]{geometry,null}));
        }

        {
            final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
            final GeneralEnvelope envelope = new GeneralEnvelope(crs);
            envelope.setRange(0, 1, 3);
            envelope.setRange(1, 1, 3);

            final BufferedImage image = BufferedImages.createImage(2, 2, 1, DataBuffer.TYPE_INT);
            image.getRaster().setSample(0, 0, 0, 2);
            image.getRaster().setSample(1, 0, 0, 2);
            image.getRaster().setSample(0, 1, 0, 2);
            image.getRaster().setSample(1, 1, 0, 2);

            final GridCoverage gridCoverage2d = new GridCoverageBuilder()
                    .setValues(image)
                    .setDomain(envelope)
                    .flipGridAxis(1)
                    .build();

            Geometry geometry = GF.createPolygon(
                new Coordinate[] {
                    new Coordinate(0, 0),
                    new Coordinate(2, 0),
                    new Coordinate(2, 2),
                    new Coordinate(0, 2),
                    new Coordinate(0, 0)
                }
            );
            geometry = JTS.transform(geometry, gridCoverage2d.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER));

            inputs.add(new AbstractMap.SimpleImmutableEntry<>(gridCoverage2d,new Geometry[]{geometry,null}));
        }

        final GridCoverage gridCoverage = new Compose(inputs,null).executeNow();
        final WritableRaster raster = ((BufferedImage)gridCoverage.render(null)).getRaster();

        assertEquals(0, raster.getSample(0, 0, 0));
        assertEquals(1, raster.getSample(0, 1, 0));
        assertEquals(1, raster.getSample(0, 2, 0));

        assertEquals(2, raster.getSample(1, 0, 0));
        assertEquals(1, raster.getSample(1, 1, 0));
        assertEquals(1, raster.getSample(1, 2, 0));

        assertEquals(2, raster.getSample(2, 0, 0));
        assertEquals(2, raster.getSample(2, 1, 0));
        assertEquals(0, raster.getSample(2, 2, 0));

    }

    @Test
    public void offsetWithGeometryTest() throws MismatchedDimensionException, TransformException, ProcessException {

        final List<Map.Entry<GridCoverage,Geometry[]>> inputs = new ArrayList<>();

        {
            final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
            final GeneralEnvelope envelope = new GeneralEnvelope(crs);
            envelope.setRange(0, 0, 2);
            envelope.setRange(1, 0, 2);

            final BufferedImage image = BufferedImages.createImage(2, 2, 1, DataBuffer.TYPE_INT);
            image.getRaster().setSample(0, 0, 0, 1);
            image.getRaster().setSample(1, 0, 0, 1);
            image.getRaster().setSample(0, 1, 0, 1);
            image.getRaster().setSample(1, 1, 0, 1);

            final GridCoverage gridCoverage2d = new GridCoverageBuilder()
                    .setValues(image)
                    .setDomain(envelope)
                    .flipGridAxis(1)
                    .build();

            Geometry geometry = GF.createPolygon(
                new Coordinate[] {
                    new Coordinate(0, 0),
                    new Coordinate(1, 0),
                    new Coordinate(1, 2),
                    new Coordinate(0, 2),
                    new Coordinate(0, 0)
                }
            );
            geometry = JTS.transform(geometry, gridCoverage2d.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER));

            inputs.add(new AbstractMap.SimpleImmutableEntry<>(gridCoverage2d,new Geometry[]{geometry,null}));
        }

        {
            final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
            final GeneralEnvelope envelope = new GeneralEnvelope(crs);
            envelope.setRange(0, 1, 3);
            envelope.setRange(1, 0, 2);

            final BufferedImage image = BufferedImages.createImage(2, 2, 1, DataBuffer.TYPE_INT);
            image.getRaster().setSample(0, 0, 0, 2);
            image.getRaster().setSample(1, 0, 0, 2);
            image.getRaster().setSample(0, 1, 0, 2);
            image.getRaster().setSample(1, 1, 0, 2);

            final GridCoverage gridCoverage2d = new GridCoverageBuilder()
                    .setValues(image)
                    .setDomain(envelope)
                    .flipGridAxis(1)
                    .build();

            Geometry geometry = GF.createPolygon(
                new Coordinate[] {
                    new Coordinate(1, 0),
                    new Coordinate(2, 0),
                    new Coordinate(2, 2),
                    new Coordinate(1, 2),
                    new Coordinate(1, 0)
                }
            );
            geometry = JTS.transform(geometry, gridCoverage2d.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER));

            inputs.add(new AbstractMap.SimpleImmutableEntry<>(gridCoverage2d,new Geometry[]{geometry,null}));
        }

        final GridCoverage gridCoverage = new Compose(inputs,null).executeNow();
        final WritableRaster raster = ((BufferedImage)gridCoverage.render(null)).getRaster();

        assertEquals(1, raster.getSample(0, 0, 0));
        assertEquals(1, raster.getSample(0, 1, 0));
        assertEquals(0, raster.getSample(1, 0, 0));
        assertEquals(0, raster.getSample(1, 1, 0));
        assertEquals(2, raster.getSample(2, 0, 0));
        assertEquals(2, raster.getSample(2, 1, 0));
    }

    @Test
    public void offsetWithHoleTest() throws MismatchedDimensionException, TransformException, ProcessException {

        final List<Map.Entry<GridCoverage,Geometry[]>> inputs = new ArrayList<>();

        {
            final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
            final GeneralEnvelope envelope = new GeneralEnvelope(crs);
            envelope.setRange(0, 0, 2);
            envelope.setRange(1, 0, 2);

            final BufferedImage image = BufferedImages.createImage(2, 2, 1, DataBuffer.TYPE_INT);
            image.getRaster().setSample(0, 0, 0, 1);
            image.getRaster().setSample(1, 0, 0, 1);
            image.getRaster().setSample(0, 1, 0, 1);
            image.getRaster().setSample(1, 1, 0, 1);

            final GridCoverage gridCoverage2d = new GridCoverageBuilder()
                    .setValues(image)
                    .setDomain(envelope)
                    .flipGridAxis(1)
                    .build();

            Geometry geometry = GF.createPolygon(
                new Coordinate[] {
                    new Coordinate(0, 0),
                    new Coordinate(2, 0),
                    new Coordinate(2, 2),
                    new Coordinate(0, 2),
                    new Coordinate(0, 0)
                }
            );
            geometry = JTS.transform(geometry, gridCoverage2d.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER));

            inputs.add(new AbstractMap.SimpleImmutableEntry<>(gridCoverage2d,new Geometry[]{geometry,null}));
        }

        {
            final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();
            final GeneralEnvelope envelope = new GeneralEnvelope(crs);
            envelope.setRange(0, 4, 6);
            envelope.setRange(1, 0, 2);

            final BufferedImage image = BufferedImages.createImage(2, 2, 1, DataBuffer.TYPE_INT);
            image.getRaster().setSample(0, 0, 0, 2);
            image.getRaster().setSample(1, 0, 0, 2);
            image.getRaster().setSample(0, 1, 0, 2);
            image.getRaster().setSample(1, 1, 0, 2);

            final GridCoverage gridCoverage2d = new GridCoverageBuilder()
                    .setValues(image)
                    .setDomain(envelope)
                    .flipGridAxis(1)
                    .build();

            Geometry geometry = GF.createPolygon(
                new Coordinate[] {
                    new Coordinate(0, 0),
                    new Coordinate(2, 0),
                    new Coordinate(2, 2),
                    new Coordinate(0, 2),
                    new Coordinate(0, 0)
                }
            );
            geometry = JTS.transform(geometry, gridCoverage2d.getGridGeometry().getGridToCRS(PixelInCell.CELL_CORNER));

            inputs.add(new AbstractMap.SimpleImmutableEntry<>(gridCoverage2d,new Geometry[]{geometry,null}));
        }

        final GridCoverage gridCoverage = new Compose(inputs,null).executeNow();
        final WritableRaster raster = ((BufferedImage)gridCoverage.render(null)).getRaster();

        assertEquals(1, raster.getSample(0, 0, 0));
        assertEquals(1, raster.getSample(0, 1, 0));
        assertEquals(1, raster.getSample(1, 0, 0));
        assertEquals(1, raster.getSample(1, 1, 0));

        assertEquals(0, raster.getSample(2, 0, 0));
        assertEquals(0, raster.getSample(2, 1, 0));
        assertEquals(0, raster.getSample(3, 0, 0));
        assertEquals(0, raster.getSample(3, 1, 0));

        assertEquals(2, raster.getSample(4, 0, 0));
        assertEquals(2, raster.getSample(4, 1, 0));
        assertEquals(2, raster.getSample(5, 0, 0));
        assertEquals(2, raster.getSample(5, 1, 0));

    }
}
