/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2023, Geomatys
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

import java.awt.image.RenderedImage;
import java.awt.image.WritableRenderedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.measure.IncommensurableException;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridClippingMode;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageBuilder;
import org.apache.sis.coverage.grid.GridExtent;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.coverage.grid.GridRoundingMode;
import org.apache.sis.coverage.grid.IllegalGridGeometryException;
import org.apache.sis.coverage.CoverageCombiner;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.NoSuchDataException;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.opengis.referencing.operation.TransformException;


/**
 * View a aggregation of GridCoverageResource as single continous coverage.
 * Coverages must have the same sample dimensions and aligned grid geometries.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class AlignedCoverageResources extends AbstractGridCoverageResource {

    private final GridGeometry gridGeometry;
    private final List<SampleDimension> sampleDimensions;
    private final List<GridCoverageResource> resources;

    /**
     * @param resources not null
     * @throws DataStoreException if coverages have different sample dimensions or could not be aligned on a shared grid.
     */
    public static GridCoverageResource create(GridCoverageResource ... resources) throws DataStoreException {
        ArgumentChecks.ensureNonNull("resources", resources);
        ArgumentChecks.ensureStrictlyPositive("resources count", resources.length);
        if (resources.length == 1) {
            return resources[0];
        } else {
            return new AlignedCoverageResources(resources);
        }
    }

    private AlignedCoverageResources(GridCoverageResource ... resources) throws DataStoreException {
        super(null, false);

        GridGeometry grid = resources[0].getGridGeometry();
        this.sampleDimensions = resources[0].getSampleDimensions();

        //check sample dimensions match and grid geometries can be grouped
        for (int i = 1; i < resources.length; i++) {
            if (!sampleDimensions.equals(resources[i].getSampleDimensions())) {
                throw new DataStoreException("Resource sample dimensions do not match");
            }
            grid = CoverageUtilities.tryAggregate(grid, resources[i].getGridGeometry()).orElseThrow(() -> new DataStoreException("Grid geometries could not be merged"));
        }
        this.gridGeometry = grid;
        this.resources = List.of(resources);
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return gridGeometry;
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        return sampleDimensions;
    }

    @Override
    public GridCoverage read(GridGeometry gg, int... range) throws DataStoreException {
        try {
            final GridGeometry intersection;
            if (gg == null) {
                intersection = gridGeometry;
            } else {
                intersection = gridGeometry.derive().clipping(GridClippingMode.STRICT).rounding(GridRoundingMode.ENCLOSING).subgrid(gg).build();
            }

            GridCoverage result = null;
            final GridCoverage[] array = new GridCoverage[resources.size()];
            IntStream.range(0, array.length).parallel().forEach(new IntConsumer() {
                @Override
                public void accept(int index) {
                    try {
                        array[index] = resources.get(index).read(intersection, range);
                    } catch (NoSuchDataException ex) {
                        //continue
                    } catch (DataStoreException ex) {
                        //continue
                    }
                }
            });

            final List<GridCoverage> coverages = Arrays.asList(array).stream().filter(Objects::nonNull).collect(Collectors.toList());
            if (coverages.isEmpty()) {
                throw new NoSuchDataException();
            }

            final List<SampleDimension> sampleDimensions = coverages.get(0).getSampleDimensions();
            final double[] fill = new double[sampleDimensions.size()];
            Arrays.fill(fill, Double.NaN);

            //TODO works for 2d only
            final GridExtent extent = intersection.getExtent();
            final RenderedImage template = coverages.get(0).render(null);
            final WritableRenderedImage target = BufferedImages.createImage(Math.toIntExact(extent.getSize(0)), Math.toIntExact(extent.getSize(1)), template);
            BufferedImages.setAll(target, fill);

            final GridCoverageBuilder gcb = new GridCoverageBuilder();
            gcb.setDomain(intersection);
            gcb.setRanges(sampleDimensions);
            gcb.setValues(target);
            result = gcb.build();

            CoverageCombiner combiner = new CoverageCombiner(result);
            combiner.acceptAll(coverages.toArray(GridCoverage[]::new));

            return result;
        } catch (TransformException | IncommensurableException ex) {
            throw new DataStoreException("Faild to combine coverages", ex);
        } catch (IllegalGridGeometryException ex) {
            throw new NoSuchDataException();
        }
    }

}
