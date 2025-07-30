/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009-2012, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.coverage.io;

import java.io.File;
import java.net.URL;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import org.apache.sis.coverage.grid.GridCoverage;
import org.apache.sis.storage.base.MemoryGridResource;
import org.apache.sis.storage.image.WorldFileStoreProvider;
import org.apache.sis.setup.OptionKey;
import org.apache.sis.storage.Aggregate;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStores;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.storage.UnsupportedStorageException;
import org.apache.sis.storage.WritableAggregate;
import static org.apache.sis.util.ArgumentChecks.*;
import org.geotoolkit.lang.Static;


/**
 * Convenience methods for reading or writing a coverage. The method in this class creates
 * instances of {@link GridCoverageReader} or {@link GridCoverageWriter} for performing the
 * actual work. This is similar to the standard {@link javax.imageio.ImageIO} class and the
 * {@link org.geotoolkit.image.io.XImageIO} class, but applied to coverages.
 *
 * {@section Writers}
 * This class delegates the actual work to the {@link ImageCoverageWriter} class.
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 */
public final class CoverageIO extends Static {
    /**
     * Do not allow instantiation of this class.
     */
    private CoverageIO() {
    }

    /**
     * Convenience method reading a coverage from the given input. The input is typically
     * a {@link File}, {@link URL} or {@link String} object, but other types (especially
     * {@link javax.imageio.stream.ImageInputStream}) may be accepted as well depending
     * on the image format. The given input can also be an {@link javax.imageio.ImageReader}
     * instance with its input initialized.
     *
     * @param  input The input to read (typically a {@link File}).
     * @return A coverage read from the given input.
     * @throws DataStoreException If the coverage can not be read.
     */
    public static GridCoverage read(final Object input) throws DataStoreException {
        try (DataStore ds = DataStores.open(input)) {
            if (ds instanceof GridCoverageResource) {
                return ((GridCoverageResource) ds).read(null, null);
            }
            if (ds instanceof Aggregate) {
                for (Resource r : ((Aggregate) ds).components()) {
                    if (r instanceof GridCoverageResource) {
                        return ((GridCoverageResource) r).read(null, null);
                    }
                }
            }
        }
        throw new UnsupportedStorageException();
    }

    /**
     * Convenience method writing a coverage to the given output. The output is typically
     * a {@link File}, a {@link java.nio.file.Path} or {@link String} object, but other types (especially
     * {@link javax.imageio.stream.ImageOutputStream}) may be accepted as well depending
     * on the image format. The given input can also be an {@link javax.imageio.ImageWriter}
     * instance with its output initialized.
     *
     * @param coverage   The coverage to write.
     * @param formatName The image format as one of the Image I/O plugin name (e.g. {@code "png"}),
     *                   or {@code null} for auto-detection from the output file suffix.
     * @param output     The output where to write the image (typically a {@link File} or a {@link java.nio.file.Path}).
     * @throws DataStoreException If the coverage can not be written.
     */
    public static void write(final GridCoverage coverage, final String formatName, final Object output)
            throws DataStoreException
    {
        ensureNonNull("coverage", coverage);
        if (formatName != null && formatName.endsWith("-wf")) {
            final StorageConnector c = new StorageConnector(output);
            c.setOption(OptionKey.OPEN_OPTIONS, new StandardOpenOption[] {
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            });
            try (DataStore ds = new WorldFileStoreProvider().open(c)) {
                WritableAggregate wr = (WritableAggregate) ds;
                wr.add(new MemoryGridResource(null, null, coverage, null));
            }
            return;
        }
        write(Collections.singleton(coverage), formatName, output);
    }

    /**
     * Convenience method writing one of many coverages to the given output. The output
     * is typically a {@link File}, a {@link java.nio.file.Path} or {@link String} object, but other types (especially
     * {@link javax.imageio.stream.ImageOutputStream}) may be accepted as well depending
     * on the image format. The given input can also be an {@link javax.imageio.ImageWriter}
     * instance with its output initialized.
     *
     * @param coverages  The coverages to write.
     * @param formatName The image format as one of the Image I/O plugin name (e.g. {@code "png"}),
     *                   or {@code null} for auto-detection from the output file suffix.
     * @param output     The output where to write the image (typically a {@link File} or a {@link java.nio.file.Path}).
     * @throws DataStoreException If the coverages can not be written.
     */
    public static void write(final Iterable<? extends GridCoverage> coverages,
            final String formatName, final Object output) throws DataStoreException
    {
        ensureNonNull("coverages", coverages);
        ensureNonNull("output", output);
        GridCoverageWriteParam param = null;
        if (formatName != null) {
            param = new GridCoverageWriteParam();
            param.setFormatName(formatName);
        }
        final ImageCoverageWriter writer = new ImageCoverageWriter();
        try {
            writer.setOutput(output);
            writer.write(coverages, param);
        } finally {
            writer.dispose();
        }
    }
}
