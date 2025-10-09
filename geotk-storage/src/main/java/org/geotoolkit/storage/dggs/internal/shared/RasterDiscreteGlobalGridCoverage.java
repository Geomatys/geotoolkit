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
package org.geotoolkit.storage.dggs.internal.shared;

import java.awt.Point;
import java.awt.image.RenderedImage;
import java.util.List;
import java.util.function.Function;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.image.PixelIterator;
import org.apache.sis.image.WritablePixelIterator;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.dggs.DiscreteGlobalGridGeometry;
import org.geotoolkit.referencing.dggs.DiscreteGlobalGridReferenceSystem;
import org.geotoolkit.referencing.dggs.Zone;
import org.geotoolkit.storage.rs.internal.shared.BandedCodeIterator;
import org.geotoolkit.storage.rs.internal.shared.CodedCoverageAsFeatureSet;
import org.geotoolkit.storage.rs.internal.shared.WritableBandedCodeIterator;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.GenericName;

/**
 * Discrete global grid coverage with data stored in a raster image.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class RasterDiscreteGlobalGridCoverage extends AbstractDiscreteGlobalGridCoverage{

    private final GenericName name;
    private final List<SampleDimension> sampleDimensions;
    private final RenderedImage samples;
    private final Function<Object,Point> zoneToGrid;
    /**
     * @todo not used yet, but will be.
     */
    private final Function<Point,Object> gridToZone;

    //cached
    private FeatureType type;
    private String[] mapping;

    public RasterDiscreteGlobalGridCoverage(
            GenericName name,
            DiscreteGlobalGridGeometry geometry,
            List<SampleDimension> sampleDimensions,
            RenderedImage samples,
            Function<Object,Point> zoneToGrid,
            Function<Point,Object> gridToZone) {
        super(geometry);
        this.name = name;
        this.sampleDimensions = sampleDimensions;
        this.samples = samples;
        this.zoneToGrid = zoneToGrid;
        this.gridToZone = gridToZone;

        if (samples.getWidth() * samples.getHeight() != zones.size()) {
            throw new IllegalArgumentException("Number of image pixel do not match number of cells");
        }

    }

    @Override
    public BandedCodeIterator createIterator() {
        return new Iterator(getSampleType(), mapping, false);
    }

    @Override
    public WritableBandedCodeIterator createWritableIterator() {
        return new Iterator(getSampleType(), mapping, true);
    }

    @Override
    public List<SampleDimension> getSampleDimensions() {
        return sampleDimensions;
    }

    @Override
    public synchronized FeatureType getSampleType() {
        if (type != null) return type;

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);
        final AttributeTypeBuilder<?>[] created = CodedCoverageAsFeatureSet.toFeatureType(ftb, getSampleDimensions());
        mapping = new String[created.length];
        for (int i = 0; i < created.length; i++) {
            mapping[i] = created[i].getName().toString();
        }
        return ftb.build();
    }

    private final class Iterator extends WritableBandedCodeIterator {

        private int position = -1;

        private final PixelIterator cursor;
        private final DiscreteGlobalGridReferenceSystem.Coder coder;

        public Iterator(FeatureType type, String[] mapping, boolean writable) {
            super(type, mapping);
            coder = dggrs.createCoder();
            if (writable) {
                cursor = WritablePixelIterator.create(samples);
            } else {
                cursor = PixelIterator.create(samples);
            }
        }

        @Override
        public void setSample(int band, double value) {
            final Point pt = zoneToGrid.apply(zones.get(position));
            cursor.moveTo(pt.x, pt.y);
            ((WritablePixelIterator)cursor).setSample(band, value);
        }

        @Override
        public int getNumBands() {
            return cursor.getNumBands();
        }

        @Override
        public int[] getPosition() {
            return new int[]{position};
        }

        public Object getZoneId() {
            return zones.get(position);
        }

        public Zone getZone() throws TransformException {
            return coder.decode(zones.get(position));
        }

        public void moveTo(Object zid) {
            Integer idx = index.get(zid);
            if (idx == null) {
                throw new IllegalArgumentException("Zone " + zid +" is not part of this coverage");
            }
            position = idx;
        }

        @Override
        public void moveTo(int[] zid) {
            if (zid[0] >= 0 && zid[0] < zones.size()) {
                position = zid[0];
            } else {
                throw new IllegalArgumentException("Zone " + zid[0] +" is not part of this coverage");
            }
        }

        @Override
        public boolean next() {
            if (position < zones.size()-1) {
                position ++;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public double getSampleDouble(int band) {
            final Point pt = zoneToGrid.apply(zones.get(position));
            cursor.moveTo(pt.x, pt.y);
            return cursor.getSampleDouble(band);
        }

        @Override
        public void rewind() {
            position = -1;
        }

        @Override
        public void close() throws DataStoreException {
        }

    }

}
