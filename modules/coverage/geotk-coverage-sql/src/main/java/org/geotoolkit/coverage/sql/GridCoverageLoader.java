/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2010, Geomatys
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
package org.geotoolkit.coverage.sql;

import java.util.List;
import java.util.Collections;
import java.sql.SQLException;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;

import org.geotoolkit.coverage.GridSampleDimension;
import org.geotoolkit.coverage.grid.GridGeometry2D;
import org.geotoolkit.coverage.io.ImageCoverageReader;
import org.geotoolkit.coverage.io.CoverageStoreException;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.image.io.mosaic.MosaicImageReader;
import org.geotoolkit.util.XArrays;


/**
 * A {@link GridCoverageReader} implementation loading an image defined by a
 * {@link GridCoverageEntry}. The input is set by invoking {@link #setEntry};
 * do not invoke {@link #setInput(Object)} directly.
 *
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.10
 *
 * @since 3.10
 * @module
 */
final class GridCoverageLoader extends ImageCoverageReader {
    /**
     * The entry for the grid coverage to be read.
     */
    private GridCoverageEntry entry;

    /**
     * Creates a new reader. This constructor sets {@link #ignoreMetadata} to
     * {@code true} because the required metadata are provided by the database.
     */
    public GridCoverageLoader() {
        seekForwardOnly = Boolean.TRUE;
        ignoreMetadata  = Boolean.TRUE;
    }

    /**
     * Sets the entry to use as the input.
     *
     * @param entry The new entry, or {@code null} if none.
     */
    public void setEntry(final GridCoverageEntry entry) throws CoverageStoreException {
        Object input = null;
        if (entry != null) try {
            input = entry.getInput();
        } catch (URISyntaxException e) {
            throw new CoverageStoreException(e);
        }
        this.entry = entry; // Must be done before the call to setInput(Object).
        setInput(input);
    }

    /**
     * Returns {@code true} if the given provider is suitable for the image format
     * expected by the current entry.
     */
    @Override
    protected boolean canReuseImageReader(final ImageReaderSpi provider, final Object input) throws IOException {
        return XArrays.containsIgnoreCase(provider.getFormatNames(), entry.getImageFormat());
    }

    /**
     * Creates an {@link ImageReader} that claim to be able to decode the given input.
     * This method is invoked automatically by {@link #setInput(Object)} for creating
     * a new {@linkplain #imageReader image reader}.
     */
    @Override
    protected ImageReader createImageReader(final Object input) throws IOException {
        if (MosaicImageReader.Spi.DEFAULT.canDecodeInput(input)) {
            return MosaicImageReader.Spi.DEFAULT.createReaderInstance();
        }
        return XImageIO.getReaderByFormatName(entry.getImageFormat(), input, seekForwardOnly, ignoreMetadata);
    }

    /**
     * Returns the name of the coverages to be read. This implementations
     * assumes that there is exactly one coverage per entry.
     */
    @Override
    public List<String> getCoverageNames() throws CoverageStoreException {
        return Collections.singletonList(entry.getName());
    }

    /**
     * Returns the grid geometry which is declared in the database.
     */
    @Override
    public GridGeometry2D getGridGeometry(int index) throws CoverageStoreException {
        if (index == 0) {
            return entry.getGridGeometry();
        }
        // Should not happen. But if it happen anyway, the
        // super-class work should be a raisonable fallback.
        return super.getGridGeometry(index);
    }

    /**
     * Returns the sample dimensions that are declared in the database.
     */
    @Override
    public List<GridSampleDimension> getSampleDimensions(int index) throws CoverageStoreException {
        if (index == 0) try {
            return entry.getIdentifier().series.format.getSampleDimensions();
        } catch (SQLException e) {
            throw new CoverageStoreException(e);
        }
        // Should not happen. But if it happen anyway, the
        // super-class work should be a raisonable fallback.
        return super.getSampleDimensions(index);
    }
}
