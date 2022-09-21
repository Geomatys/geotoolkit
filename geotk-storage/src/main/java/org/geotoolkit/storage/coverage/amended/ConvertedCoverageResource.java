/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2022, Geomatys
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
package org.geotoolkit.storage.coverage.amended;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.apache.sis.coverage.SampleDimension;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridCoverageProcessor;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.storage.AbstractGridCoverageResource;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.RasterLoadingStrategy;
import org.apache.sis.storage.UnsupportedQueryException;
import org.apache.sis.util.ArgumentChecks;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.util.GenericName;

/**
 * Create a coverage resource with sample values converted by the given functions.
 * The number of sample dimensions in the returned coverage is the length of the {@code converters} array,
 * which must be greater than 0 and not greater than the number of sample dimensions in the source coverage.
 * If the {@code converters} array length is less than the number of source sample dimensions,
 * then all sample dimensions at index ≥ {@code converters.length} will be ignored.
 *
 * <h4>Sample dimensions customization</h4>
 * By default, this method creates new sample dimensions with the same names and categories than in the
 * previous coverage, but with {@linkplain org.apache.sis.coverage.Category#getSampleRange() sample ranges}
 * converted using the given converters and with {@linkplain SampleDimension#getUnits() units of measurement}
 * omitted. This behavior can be modified by specifying a non-null {@code sampleDimensionModifier} function.
 * If non-null, that function will be invoked with, as input, a pre-configured sample dimension builder.
 * The {@code sampleDimensionModifier} function can {@linkplain SampleDimension.Builder#setName(CharSequence)
 * change the sample dimension name} or {@linkplain SampleDimension.Builder#categories() rebuild the categories}.
 *
 * <h4>Result relationship with source</h4>
 * If the source coverage is backed by a {@link java.awt.image.WritableRenderedImage},
 * then changes in the source coverage are reflected in the returned coverage and conversely.
 *
 * @see GridCoverageProcessor#convert(org.apache.sis.coverage.grid.GridCoverage, org.opengis.referencing.operation.MathTransform1D[], java.util.function.Function)
 * @see ImageProcessor#convert(RenderedImage, NumberRange<?>[], MathTransform1D[], DataType, ColorModel)
 *
 * @author Johann Sorel (Geomatys)
 */
public final class ConvertedCoverageResource extends AbstractGridCoverageResource {

    private final GridCoverageResource base;
    private final MathTransform1D[] converters;
    private final Function<SampleDimension.Builder, SampleDimension> sampleDimensionModifier;
    private List<SampleDimension> sampleDimensions;

    /**
     *
     * @param source the coverage for which to convert sample values.
     * @param converters the transfer functions to apply on each sample dimension of the source coverage.
     * @param sampleDimensionModifier a callback for modifying the {@link SampleDimension.Builder} default
     *         configuration for each sample dimension of the target coverage, or {@code null} if none.
     */
    public ConvertedCoverageResource(GridCoverageResource source, MathTransform1D[] converters,
            Function<SampleDimension.Builder, SampleDimension> sampleDimensionModifier) {
        super(null, false);
        ArgumentChecks.ensureNonNull("base", source);
        ArgumentChecks.ensureNonNull("converters", converters);
        this.base = source;
        this.converters = converters;
        this.sampleDimensionModifier = sampleDimensionModifier;
    }

    @Override
    public GridGeometry getGridGeometry() throws DataStoreException {
        return base.getGridGeometry();
    }

    @Override
    public synchronized List<SampleDimension> getSampleDimensions() throws DataStoreException {
        if (sampleDimensions == null) {
            sampleDimensions = new ArrayList<>(base.getSampleDimensions());
            if (this.sampleDimensionModifier != null) {
                for (int i = 0; i < converters.length; i++) {
                    final SampleDimension.Builder builder = new SampleDimension.Builder();
                    final SampleDimension band = sampleDimensions.get(i);
                    final MathTransform1D converter = converters[i];
                    band.getBackground().ifPresent(builder::setBackground);
                    band.getCategories().forEach((category) -> {
                        if (category.isQuantitative()) {
                            // Unit is assumed different as a result of conversion.
                            builder.addQuantitative(category.getName(), category.getSampleRange(), converter, null);
                        } else {
                            builder.addQualitative(category.getName(), category.getSampleRange());
                        }
                    });
                    builder.setName(band.getName());
                    sampleDimensions.set(i, sampleDimensionModifier.apply(builder));
                }
            }
            sampleDimensions = Collections.unmodifiableList(sampleDimensions);
        }
        return sampleDimensions;
    }

    @Override
    public List<double[]> getResolutions() throws DataStoreException {
        return base.getResolutions();
    }

    @Override
    public GridCoverageResource subset(Query query) throws UnsupportedQueryException, DataStoreException {
        final GridCoverageResource subset = base.subset(query);
        return new ConvertedCoverageResource(subset, converters, sampleDimensionModifier);
    }

    @Override
    public GridCoverage read(GridGeometry domain, int... range) throws DataStoreException {
        final GridCoverage coverage = base.read(domain, range);
        final MathTransform1D[] trs;
        if (range != null) {
            trs = new MathTransform1D[range.length];
            for (int i = 0; i < trs.length; i++) {
                trs[i] = converters[range[i]];
            }
        } else {
            trs = converters.clone();
        }
        return new GridCoverageProcessor().convert(coverage, trs, sampleDimensionModifier);
    }

    @Override
    public RasterLoadingStrategy getLoadingStrategy() throws DataStoreException {
        return base.getLoadingStrategy();
    }

    @Override
    public boolean setLoadingStrategy(RasterLoadingStrategy strategy) throws DataStoreException {
        return base.setLoadingStrategy(strategy);
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return base.getEnvelope();
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return base.getIdentifier();
    }

}
