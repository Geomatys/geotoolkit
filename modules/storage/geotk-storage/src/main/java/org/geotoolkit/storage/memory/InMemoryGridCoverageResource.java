/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
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
package org.geotoolkit.storage.memory;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.coverage.grid.GridCoverage2D;
import org.apache.sis.internal.storage.AbstractGridResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.WritableGridCoverageResource;
import org.geotoolkit.image.BufferedImages;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.util.NamesExt;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class InMemoryGridCoverageResource extends AbstractGridResource implements WritableGridCoverageResource {

    private final GenericName name;
    private GridCoverage coverage;

    public InMemoryGridCoverageResource() {
        super(null);
        this.name = null;
    }

    public InMemoryGridCoverageResource(GenericName name) {
        super(null);
        this.name = name;
    }

    public InMemoryGridCoverageResource(GridCoverage coverage) {
        this(null, coverage);
    }

    public InMemoryGridCoverageResource(GenericName name, GridCoverage coverage) {
        super(null);
        if (name == null) {
            InternationalString in = CoverageUtilities.getName(coverage);
            this.name = (in == null) ? null : NamesExt.create(in.toString());
        } else {
            this.name = name;
        }
        this.coverage = coverage;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.ofNullable(name);
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        if (coverage == null) throw new DataStoreException("Coverage is undefined");
        return coverage.getGridGeometry();
    }

    @Override
    public List<SampleDimension> getSampleDimensions() throws DataStoreException {
        if (coverage == null) throw new DataStoreException("Coverage is undefined");
        return coverage.getSampleDimensions();
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        if (coverage == null) throw new DataStoreException("Coverage is undefined");

        if (range != null && range.length != 0) {
            final RenderedImage image = coverage.render(null);
            final GridGeometry grid = CoverageUtilities.forceLowerToZero(coverage.getGridGeometry());
            final BufferedImage newImage = BufferedImages.createImage(image, null, null, range.length, null);

            final WritablePixelIterator wite = WritablePixelIterator.create(newImage);
            final PixelIterator rite = PixelIterator.create(image);
            while (wite.next()) {
                final Point position = wite.getPosition();
                rite.moveTo(position.x, position.y);
                for (int i=0;i<range.length;i++) {
                    wite.setSample(i, rite.getSampleDouble(range[i]));
                }
            }
            final List<SampleDimension> sampleDimensions = coverage.getSampleDimensions();
            final List<SampleDimension> sds = new ArrayList<>(range.length);
            for (int i = 0; i < range.length; i++) {
                sds.add(sampleDimensions.get(i));
            }
            return new GridCoverage2D(grid, sds, newImage);
        }
        return coverage;
    }

    @Override
    public void write(GridCoverage coverage, WritableGridCoverageResource.Option... options) throws DataStoreException {
        this.coverage = coverage;
    }

}
