/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2012, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2012, Geomatys
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
package org.geotoolkit.image.io.mosaic;

import java.io.File;
import java.awt.image.BufferedImageOp;
import javax.imageio.ImageWriteParam;

import static org.apache.sis.util.ArgumentChecks.*;


/**
 * The parameters for {@link MosaicImageWriter}.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.01
 *
 * @since 2.5
 * @module
 */
public class MosaicImageWriteParam extends ImageWriteParam {
    /**
     * The index of the {@linkplain TileManager tile manager} to use in the array returned by
     * {@link MosaicImageWriter#getOutput}.
     */
    private int outputIndex = 0;

    /**
     * Controls the way {@link MosaicImageWriter} writes the tiles.
     */
    private TileWritingPolicy policy = TileWritingPolicy.DEFAULT;

    /**
     * An optional operation to apply on source tiles before to create the mosaic.
     */
    private BufferedImageOp sourceTileFilter;

    /**
     * Constructs an empty set of parameters.
     */
    public MosaicImageWriteParam() {
    }

    /**
     * Creates a new set of parameters with the same mosaic-specific parameters than the given
     * one, and the default value for all other parameters.
     * <p>
     * This method is not public because for a public API, it would be cleaner to copy all
     * parameters (as we usually expect from a copy constructor), while {@link MosaicImageWriter}
     * really needs to copy only the mosaic-specific parameters and left the other ones to their
     * default value.
     *
     * @param copy The parameters to copy.
     */
    MosaicImageWriteParam(final MosaicImageWriteParam copy) {
        outputIndex = copy.outputIndex;
        policy      = copy.policy;
    }

    /**
     * Returns the index of the image to be written. This is the index of the
     * {@linkplain TileManager tile manager} to use in the array returned by
     * {@link MosaicImageWriter#getOutput}. The default value is 0.
     *
     * @return The index of the image to be written.
     */
    public int getOutputIndex() {
        return outputIndex;
    }

    /**
     * Sets the index of the image to be written. This is the index of the
     * {@linkplain TileManager tile manager} to use in the array returned by
     * {@link MosaicImageWriter#getOutput}. The default value is 0.
     *
     * @param index The index of the image to be written.
     */
    public void setOutputIndex(final int index) {
        ensureBetween("index", 0, Tile.MASK, index);
        outputIndex = index;
    }

    /**
     * Returns whatever existing {@linkplain File files} should be skipped or overwritten.
     * The default value is {@link TileWritingPolicy#OVERWRITE OVERWRITE}.
     *
     * @return The policy to apply when writing tiles.
     */
    public TileWritingPolicy getTileWritingPolicy() {
        return policy;
    }

    /**
     * Sets whatever existing {@linkplain File files} should be skipped. The default behavior
     * is to {@linkplain TileWritingPolicy#OVERWRITE overwrite} every files unconditionally.
     * Settings the policy to {@link TileWritingPolicy#WRITE_NEWS_ONLY WRITE_NEWS_ONLY} may
     * speedup {@link MosaicImageWriter} when the process of writing tiles is started again
     * after a previous partial failure, by skipping the tiles that were successfully generated
     * in the previous run.
     *
     * @param policy The policy to apply when writing tiles.
     */
    public void setTileWritingPolicy(final TileWritingPolicy policy) {
        ensureNonNull("policy", policy);
        this.policy = policy;
    }

    /**
     * Returns an optional operation to apply on source tiles before to create the mosaic.
     *
     * @return The operation to apply on source tiles, or {@code null} if none.
     *
     * @since 3.00
     */
    public BufferedImageOp getSourceTileFilter() {
        return sourceTileFilter;
    }

    /**
     * Sets the operation to apply on source tiles before to create the mosaic. If an operation
     * is given, then that operation will be applied on every source tiles and the result will
     * be saved as RAW images in temporary files. The temporary files will be removed when the
     * mosaic creation is finished.
     *
     * @param filter The operation to apply on source tiles, or {@code null} if none.
     *
     * @since 3.00
     */
    public void setSourceTileFilter(final BufferedImageOp filter) {
        sourceTileFilter = filter;
    }
}
