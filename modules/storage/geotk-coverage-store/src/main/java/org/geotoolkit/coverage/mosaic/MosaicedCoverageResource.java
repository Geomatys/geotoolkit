/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.coverage.mosaic;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.DisjointExtentException;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.geotoolkit.coverage.grid.GridCoverageBuilder;
import org.geotoolkit.coverage.io.DisjointCoverageDomainException;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.geotoolkit.image.interpolation.Resample;
import org.geotoolkit.image.interpolation.ResampleBorderComportement;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.locationtech.jts.index.quadtree.Quadtree;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.PixelInCell;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class MosaicedCoverageResource implements GridCoverageResource {

    private final GridGeometry gridGeometry;
    private final Tile[] tiles;
    private final Quadtree tree = new Quadtree();

    private MosaicedCoverageResource(GridGeometry gridGeometry, Tile[] tiles) throws DataStoreException {
        this.gridGeometry = gridGeometry;
        this.tiles = tiles;

        for (Tile tile : tiles) {
            GridCoverageResource resource = tile.getResource();
            Envelope envelope = resource.getGridGeometry().getEnvelope();
            tree.insert(new JTSEnvelope2D(envelope), tile);
        }
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.empty();
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        return new DefaultMetadata();
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return gridGeometry;
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return tiles[0].getResource().getSampleDimensions();
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return Optional.of(getGridGeometry().getEnvelope());
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {

        GridGeometry canvas = gridGeometry;
        if (domain != null) {
            try {
            canvas = canvas.derive()
                    .margin(3)
                    .rounding(GridRoundingMode.ENCLOSING)
                    .subgrid(domain)
                    .build();
            } catch (DisjointExtentException ex) {
                throw new DisjointCoverageDomainException(ex.getMessage(), ex);
            }
        } else {
            domain = canvas;
        }
        canvas = CoverageUtilities.forceLowerToZero(canvas);

        final Envelope envelope = canvas.getEnvelope();
        final List<Tile> results = tree.query(new JTSEnvelope2D(envelope));

        //single result
        if (results.size() == 1) {
            GridCoverageResource resource = results.get(0).getResource();
            return resource.read(canvas, range);
        }

        //aggregate tiles
        BufferedImage buffer = null;
        List<SampleDimension> sampleDimensions = null;
        for (Tile tile : results) {
            final GridCoverageResource resource = tile.getResource();
            try {
                final GridCoverage coverage = resource.read(domain, range);
                sampleDimensions = coverage.getSampleDimensions();
                final RenderedImage tileImage = coverage.render(null);

                if (buffer == null) {
                    //create result image
                    GridExtent extent = canvas.getExtent();
                    buffer = BufferedImages.createImage(
                            Math.toIntExact(extent.getSize(0)),
                            Math.toIntExact(extent.getSize(1)),
                            tileImage);
                }

                MathTransform tileToTileCrs = coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER).inverse();
                MathTransform crsToCrs = CRS.findOperation(
                        canvas.getCoordinateReferenceSystem(),
                        coverage.getGridGeometry().getCoordinateReferenceSystem(),
                        null).getMathTransform();
                MathTransform canvasToCrs = canvas.getGridToCRS(PixelInCell.CELL_CENTER);

                final MathTransform targetToSource = MathTransforms.concatenate(canvasToCrs, crsToCrs, tileToTileCrs);

                final Resample resample = new Resample(targetToSource, buffer, tileImage,
                        InterpolationCase.NEIGHBOR, ResampleBorderComportement.FILL_VALUE, null);
                resample.fillImage(true);

            } catch (DisjointCoverageDomainException ex) {
                //may happen, envelepe is larger then data
                //quad tree may also return more results
            } catch (FactoryException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            } catch (TransformException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }

        if (buffer == null) {
            throw new DisjointCoverageDomainException();
        }

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName("Mosaic");
        gcb.setGridGeometry(canvas);
        gcb.setSampleDimensions(sampleDimensions);
        gcb.setRenderedImage(buffer);
        return gcb.build();
    }

    @Override
    public <T extends StoreEvent> void addListener(Class<T> eventType, StoreListener<? super T> listener) {
    }

    @Override
    public <T extends StoreEvent> void removeListener(Class<T> eventType, StoreListener<? super T> listener) {
    }

    public static List<GridCoverageResource> create(GridCoverageResource ... resources) throws DataStoreException {

        final RegionCalculator calculator = new RegionCalculator();

        for (GridCoverageResource resource : resources) {
            append(resource, calculator);
        }

        final List<GridCoverageResource> mosaics = new ArrayList<>();
        for (Entry<GridGeometry,Tile[]> entry : calculator.tiles().entrySet()) {

            final Tile[] tiles = entry.getValue();
            GridGeometry grid = entry.getKey();

            if (tiles.length == 1) {
                mosaics.add(tiles[0].getResource());
            } else {
                //set the crs which is missing from the grid geometry
                CoordinateReferenceSystem crs = entry.getValue()[0].getResource().getGridGeometry().getCoordinateReferenceSystem();
                grid = new GridGeometry(grid.getExtent(), PixelInCell.CELL_CENTER, grid.getGridToCRS(PixelInCell.CELL_CENTER), crs);

                mosaics.add(new MosaicedCoverageResource(grid, entry.getValue()));
            }
        }
        return mosaics;
    }

    private static void append(GridCoverageResource r, RegionCalculator c) throws DataStoreException {
        if (r instanceof MosaicedCoverageResource) {
            MosaicedCoverageResource mcr = (MosaicedCoverageResource) r;
            for (Tile t : mcr.tiles) {
                c.add(new Tile(t.getResource()));
            }
        } else {
            c.add(new Tile(r));
        }
    }

}
