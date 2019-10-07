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
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.DisjointExtentException;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.geometry.Envelopes;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.operation.transform.MathTransforms;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.storage.event.StoreListener;
import org.apache.sis.util.collection.FrequencySortedSet;
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
public final class AggregatedCoverageResource implements GridCoverageResource {

    private final List<GridCoverageResource> resources;
    private final Quadtree tree = new Quadtree();
    private final GridGeometry gridGeometry;

    public static GridCoverageResource create(CoordinateReferenceSystem resultCrs, GridCoverageResource ... resources) throws DataStoreException, TransformException {
        if (resources.length == 0) {
            throw new DataStoreException("No resources to aggregate");
        } else if (resources.length == 1) {
            return resources[0];
        } else {
            return new AggregatedCoverageResource(Arrays.asList(resources), resultCrs);
        }
    }

    private AggregatedCoverageResource(List<GridCoverageResource> resources, CoordinateReferenceSystem resultCrs) throws DataStoreException, TransformException {
        this.resources = resources;

        if (resultCrs == null) {
            //use most common crs
            //TODO find a better approach to determinate a common crs
            final FrequencySortedSet<CoordinateReferenceSystem> map = new FrequencySortedSet<>();
            for (GridCoverageResource resource : resources) {
                map.add(resource.getGridGeometry().getCoordinateReferenceSystem());
            }
            resultCrs = map.last();
        }

        GeneralEnvelope env = new GeneralEnvelope(resultCrs);
        env.setToNaN();
        int index = 0;
        for (GridCoverageResource resource : resources) {
            Envelope envelope = resource.getGridGeometry().getEnvelope();
            envelope = Envelopes.transform(envelope, resultCrs);
            tree.insert(new JTSEnvelope2D(envelope), new AbstractMap.SimpleImmutableEntry<>(index++,resource));

            if (env.isAllNaN()) {
                env.setEnvelope(envelope);
            } else {
                env.add(envelope);
            }
        }

        gridGeometry = new GridGeometry(null, env);

        //TODO need to check SampleDimension match and convert compatible units when needed
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
        return resources.get(0).getSampleDimensions();
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return Optional.of(getGridGeometry().getEnvelope());
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {

        GridGeometry canvas = domain;
        canvas = CoverageUtilities.forceLowerToZero(canvas);

        final Envelope envelope = domain.getEnvelope();
        final List<Map.Entry<Integer,GridCoverageResource>> results = tree.query(new JTSEnvelope2D(envelope));

        //single result
        if (results.size() == 1) {
            GridCoverageResource resource = results.get(0).getValue();
            return resource.read(canvas, range);
        }

        Collections.sort(results, (Map.Entry<Integer, GridCoverageResource> o1, Map.Entry<Integer, GridCoverageResource> o2) -> o1.getKey().compareTo(o2.getKey()));


        //aggregate tiles
        BufferedImage result = null;
        BufferedImage intermediate = null;
        double[] fillValue = null;
        List<SampleDimension> sampleDimensions = null;
        for (Map.Entry<Integer,GridCoverageResource> entry : results) {
            GridCoverageResource resource = entry.getValue();
            try {
                //expend grid geometry a little for interpolation
                GridGeometry readGridGeom;
                GridGeometry coverageGridGeometry = resource.getGridGeometry();
                if (coverageGridGeometry.isDefined(GridGeometry.EXTENT)) {
                    readGridGeom = coverageGridGeometry.derive().margin(5,5).subgrid(canvas).build();
                } else {
                    readGridGeom = canvas.derive().margin(5,5).build();
                }

                final GridCoverage coverage = resource.read(readGridGeom, range).forConvertedValues(true);
                sampleDimensions = coverage.getSampleDimensions();
                final RenderedImage tileImage = coverage.render(null);

                final BufferedImage workImage;
                if (result == null) {
                    //create result image
                    GridExtent extent = canvas.getExtent();
                    int sizeX = Math.toIntExact(extent.getSize(0));
                    int sizeY = Math.toIntExact(extent.getSize(1));
                    result = BufferedImages.createImage(sizeX, sizeY, tileImage);
                    workImage = result;
                    fillValue = new double[result.getSampleModel().getNumBands()];
                    Arrays.fill(fillValue, Double.NaN);
                    BufferedImages.setAll(result, fillValue);
                } else {
                    if (intermediate == null) {
                        intermediate = BufferedImages.createImage(result.getWidth(), result.getHeight(), result);
                    }
                    workImage = intermediate;
                    BufferedImages.setAll(intermediate, fillValue);
                }

                //resample coverage
                MathTransform tileToTileCrs = coverage.getGridGeometry().getGridToCRS(PixelInCell.CELL_CENTER).inverse();
                MathTransform crsToCrs = CRS.findOperation(
                        canvas.getCoordinateReferenceSystem(),
                        coverage.getGridGeometry().getCoordinateReferenceSystem(),
                        null).getMathTransform();
                MathTransform canvasToCrs = canvas.getGridToCRS(PixelInCell.CELL_CENTER);

                final MathTransform targetToSource = MathTransforms.concatenate(canvasToCrs, crsToCrs, tileToTileCrs);

                final Resample resample = new Resample(targetToSource, workImage, tileImage,
                        InterpolationCase.BILINEAR, ResampleBorderComportement.FILL_VALUE, null);
                resample.fillImage(true);

                if (workImage != result) {
                    //we need to merge image, replacing only not-NaN values
                    PixelIterator read = PixelIterator.create(workImage);
                    WritablePixelIterator write = WritablePixelIterator.create(result);
                    while (read.next() & write.next()) {
                        double r = read.getSampleDouble(0);
                        if (Double.isNaN(r)) continue;
                        double w = write.getSampleDouble(0);
                        if (Double.isNaN(w)) write.setSample(0, r);
                    }
                }

            } catch (DisjointCoverageDomainException | DisjointExtentException ex) {
                //may happen, envelepe is larger then data
                //quad tree may also return more results
            } catch (FactoryException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            } catch (TransformException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }

        if (result == null) {
            throw new DisjointCoverageDomainException();
        }

        final GridCoverageBuilder gcb = new GridCoverageBuilder();
        gcb.setName("Aggregated");
        gcb.setGridGeometry(canvas);
        gcb.setSampleDimensions(sampleDimensions);
        gcb.setRenderedImage(result);
        return gcb.build();
    }

    @Override
    public <T extends StoreEvent> void addListener(Class<T> eventType, StoreListener<? super T> listener) {
    }

    @Override
    public <T extends StoreEvent> void removeListener(Class<T> eventType, StoreListener<? super T> listener) {
    }
}
