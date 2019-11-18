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
package org.geotoolkit.processing.coverage.resample;

import java.awt.image.WritableRenderedImage;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.coverage.grid.GridGeometry;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.GridCoverageResource;
import org.geotoolkit.image.interpolation.InterpolationCase;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Proposal of API to coverage resampling operations.
 *
 * @author Johann Sorel (Geomatys)
 * @version 1.1
 * @since   1.1
 * @module
 */
public final class ResampleBuilder {


    //global configuration
    private InterpolationCase interpolation = InterpolationCase.NEIGHBOR;
    private boolean noDataLoss = true;

    //input configuration
    private GridCoverageResource inputResource;
    private GridCoverage inputCoverage;
    //output configuration
    private CoordinateReferenceSystem resultCrs;
    private GridGeometry resultGridGeometry;
    private WritableRenderedImage resultImage;
    private double[] prefillValue;


    public ResampleBuilder() {
    }

    public ResampleBuilder interpolation(InterpolationCase interpolation) {
        this.interpolation = interpolation;
        return this;
    }

    /**
     * <p>
     * This parameter if set to true ensure that if not enough datas are present
     * to make a proper interpolation, a lower interpolation or nearest neighbor
     * fallback will use used.
     * </p>
     * <p>
     * This behavior avoids data progressive shrinking when multiple resample occurs
     * and the white/blak/NaN lines which may appear between several side by side coverages.
     * </p>
     *
     * NOTE : similar to a border extender from JAI
     *
     * @param noDataLoss
     */
    public ResampleBuilder interpolationNoDataLoss(boolean noDataLoss) {
        this.noDataLoss = noDataLoss;
        return this;
    }

    public ResampleBuilder inputResource(GridCoverageResource resource) {
        this.inputResource = resource;
        return this;
    }

    public ResampleBuilder inputCoverage(GridCoverage coverage) {
        this.inputCoverage = coverage;
        return this;
    }

    public ResampleBuilder resultCrs(CoordinateReferenceSystem crs) {
        this.resultCrs = crs;
        return this;
    }

    public ResampleBuilder resultGrid(GridGeometry gridGeometry) {
        this.resultGridGeometry = gridGeometry;
        return this;
    }

    public ResampleBuilder resultImage(WritableRenderedImage image) {
        this.resultImage = image;
        return this;
    }

    /**
     * The pre-fill value is used to fill the target image before resampling.
     * <p>
     * If the pre-fill value is undefined, the background or no-data value
     * from source coverage sample dimensions will be used. Otherwise default
     * fill value (zero) is used.
     * </p>
     *
     * @param prefillValue
     */
    public ResampleBuilder preFillValue(double[] prefillValue) {
        this.prefillValue = prefillValue;
        return this;
    }

    public GridCoverage buildCoverage() {
        throw new UnsupportedOperationException("unsupported");
    }

    public static void main(String[] args) {
        GridCoverageResource resource = null;
        GridCoverage coverage = new ResampleBuilder()
                .inputResource(resource)
                .interpolation(InterpolationCase.BICUBIC)
                .resultCrs(CommonCRS.WGS84.geographic())
                .buildCoverage();
    }

}
