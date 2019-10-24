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
package org.geotoolkit.storage.coverage.mosaic;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import javax.measure.Unit;
import org.apache.sis.coverage.Category;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.DisjointExtentException;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.internal.referencing.j2d.AffineTransform2D;
import org.apache.sis.internal.storage.AbstractGridResource;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.NoSuchDataException;
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
public class MosaicedCoverageResource extends AbstractGridResource {

    private final GridGeometry gridGeometry;
    private final Tile[] tiles;
    private final Quadtree tree = new Quadtree();

    private List<SampleDimension> sampleDimensions;
    private boolean forceTransformedValues;
    private double[] noData;

    private MosaicedCoverageResource(GridGeometry gridGeometry, Tile[] tiles) throws DataStoreException {
        super(null);
        this.gridGeometry = gridGeometry;
        this.tiles = tiles;

        for (Tile tile : tiles) {
            GridCoverageResource resource = tile.getResource();
            Envelope envelope = resource.getGridGeometry().getEnvelope();
            tree.insert(new JTSEnvelope2D(envelope), tile);
        }

        //add a no-data category
        //the no-data is needed to fill possible gaps between coverages
        //TODO need to improve detection cases to avoid switching to transformed values all the time
        sampleDimensions = new ArrayList<>(tiles[0].getResource().getSampleDimensions());
        forceTransformedValues = false;
        noData = new double[sampleDimensions.size()];
        for (int i = 0,n = sampleDimensions.size(); i < n; i++) {
            SampleDimension baseDim = sampleDimensions.get(i);
            Set<Number> noData = baseDim.getNoDataValues();
            Optional<Number> background = baseDim.getBackground();
            if (noData.isEmpty() && !background.isPresent()) {
                baseDim = sampleDimensions.get(i).forConvertedValues(true);
                final SampleDimension.Builder builder = new SampleDimension.Builder();
                final Unit<?> unit = baseDim.getUnits().orElse(null);

                for (Category c : baseDim.getCategories()) {
                    if (c.isQuantitative()) {
                        builder.addQuantitative(c.getName(), c.getSampleRange(), c.getTransferFunction().orElse(null), unit);
                    } else {
                        builder.addQualitative(c.getName(), c.getSampleRange());
                    }
                }
                builder.setBackground(null, Double.NaN);
                baseDim = builder.build();
                noData = baseDim.getNoDataValues();
                background = baseDim.getBackground();
                sampleDimensions.set(i, baseDim);
                forceTransformedValues = true;
            }

            if (background.isPresent()) {
                this.noData[i] = background.get().doubleValue();
            } else {
                this.noData[i] = noData.iterator().next().doubleValue();
            }
        }

        if (forceTransformedValues) {
            for (int i = 0,n = sampleDimensions.size(); i < n; i++) {
                sampleDimensions.set(i, sampleDimensions.get(i).forConvertedValues(true));
            }
        }
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.empty();
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return gridGeometry;
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return Collections.unmodifiableList(sampleDimensions);
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
        for (Tile tile : results) {
            final GridCoverageResource resource = tile.getResource();
            try {
                GridCoverage coverage = resource.read(domain, range);
                RenderedImage tileImage = coverage.render(null);

                if (buffer == null) {
                    //create result image
                    GridExtent extent = canvas.getExtent();
                    if (forceTransformedValues) {
                        coverage = coverage.forConvertedValues(true);
                        tileImage = coverage.render(null);
                        buffer = BufferedImages.createImage(
                            Math.toIntExact(extent.getSize(0)),
                            Math.toIntExact(extent.getSize(1)),
                            noData.length, DataBuffer.TYPE_DOUBLE);
                        if (!MosaicedCoverageResource.isAllZero(noData)) BufferedImages.setAll(buffer, noData);
                    } else {
                        buffer = BufferedImages.createImage(
                            Math.toIntExact(extent.getSize(0)),
                            Math.toIntExact(extent.getSize(1)),
                            tileImage);
                        if (!MosaicedCoverageResource.isAllZero(noData)) BufferedImages.setAll(buffer, noData);
                    }
                }

                resample(coverage, tileImage, canvas, buffer);

            } catch (NoSuchDataException | DisjointExtentException ex) {
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
        gcb.setSampleDimensions(getSampleDimensions());
        gcb.setRenderedImage(buffer);
        return gcb.build();
    }

    static void resample(GridCoverage coverage, RenderedImage coverageImage, GridGeometry canvasGridGeometry, BufferedImage canvasImage) throws TransformException, FactoryException {

        final GridGeometry coverageGridGeometry = coverage.getGridGeometry();
        GridExtent sourceRendering = coverageGridGeometry.getExtent();

        final AffineTransform2D source = new AffineTransform2D(
                1, 0, 0, 1,
                sourceRendering.getLow(0),
                sourceRendering.getLow(1)
        );


        //MathTransform tileToTileCrs = coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER).inverse();
        MathTransform tileToTileCrs = MathTransforms.concatenate(source, coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER)).inverse();

        MathTransform crsToCrs = CRS.findOperation(
                canvasGridGeometry.getCoordinateReferenceSystem(),
                coverage.getGridGeometry().getCoordinateReferenceSystem(),
                null).getMathTransform();
        MathTransform canvasToCrs = canvasGridGeometry.getGridToCRS(PixelInCell.CELL_CENTER);

        final MathTransform targetToSource = MathTransforms.concatenate(canvasToCrs, crsToCrs, tileToTileCrs);

        final Resample resample = new Resample(targetToSource, canvasImage, coverageImage,
                InterpolationCase.NEIGHBOR, ResampleBorderComportement.FILL_VALUE, null);
        resample.fillImage(true);
    }

    static boolean isAllZero(double[] array) {
        for (double d : array) {
            if (d != 0) return false;
        }
        return true;
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

            //keep tiles with the same subsampling together
            //we consider different subsampling as different mosaics
            final Map<Dimension,List<GridCoverageResource>> groups = new HashMap<>();

            for (Tile tile : tiles) {
                List<GridCoverageResource> lst = groups.get(tile.getSubsampling());
                if (lst == null) {
                    lst = new ArrayList<>();
                    groups.put(tile.getSubsampling(), lst);
                }
                lst.add(tile.getResource());
            }

            for (List<GridCoverageResource> lst : groups.values()) {
//                if (lst.size() == 1) {
//                   mosaics.add(lst.get(0));
//                } else {

                    final RegionCalculator calculator2 = new RegionCalculator();
                    for (GridCoverageResource resource : lst) {
                        append(resource, calculator2);
                    }
                    Entry<GridGeometry, Tile[]> next = calculator2.tiles().entrySet().iterator().next();
                    GridGeometry grid = next.getKey();

                    //set the crs which is missing from the grid geometry
                    CoordinateReferenceSystem crs = next.getValue()[0].getResource().getGridGeometry().getCoordinateReferenceSystem();
                    grid = new GridGeometry(grid.getExtent(), PixelInCell.CELL_CENTER, grid.getGridToCRS(PixelInCell.CELL_CENTER), crs);

                    mosaics.add(new MosaicedCoverageResource(grid, next.getValue()));
//                }
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
