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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
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
import org.geotoolkit.internal.image.io.CheckedImageInputStream;
import org.geotoolkit.internal.image.io.CheckedImageOutputStream;
import org.geotoolkit.lang.Static;
import org.geotoolkit.resources.Errors;


/**
 * Convenience methods for reading or writing a coverage. The method in this class creates
 * instances of {@link GridCoverageReader} or {@link GridCoverageWriter} for performing the
 * actual work. This is similar to the standard {@link javax.imageio.ImageIO} class and the
 * {@link org.geotoolkit.image.io.XImageIO} class, but applied to coverages.
 *
 * {@section Readers}
 * In the simplest case, this class just creates an {@link ImageCoverageReader} instance with
 * the input set to the given object (typically a {@link File} or {@link URL}). However if the
 * image is very large and is not encoded in a format that support natively tiling, it may be
 * more efficient to create a mosaic of tiles first. The {@link #writeOrReuseMosaic(File)}
 * method is provided for this purpose.
 *
 * {@section Writers}
 * This class delegates the actual work to the {@link ImageCoverageWriter} class.
 *
 * @author Johann Sorel (Geomatys)
 * @author Martin Desruisseaux (Geomatys)
 * @version 3.20
 *
 * @since 3.18
 * @module
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
        } catch (UnsupportedStorageException e) {
            // Ignore, will try fallback below.
        }
        final ImageCoverageReader reader = createSimpleReader(input);
        try {
            return reader.read(null);
        } finally {
            reader.dispose();
        }
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
                wr.add(new MemoryGridResource(null, coverage, null));
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
     *
     * @since 3.20
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

    /**
     * Creates a simple reader which does not use any pyramid or mosaic tiling.
     * This reader is appropriate if the image is known to be small.
     * <p>
     * The input is typically a {@link File}, {@link java.nio.file.Path}, {@link URL} or {@link String} object, but other types
     * (especially {@link javax.imageio.stream.ImageInputStream}) may be accepted as well depending
     * on the image format. The given input can also be an {@link javax.imageio.ImageReader} instance
     * with its input initialized.
     *
     * @param  input The input to read (typically a {@link File} or a {@link java.nio.file.Path}).
     * @return A coverage reader for the given input.
     * @throws DataStoreException If the reader can not be created for the given file.
     */
    public static ImageCoverageReader createSimpleReader(final Object input) throws DataStoreException {
        ensureNonNull("input", input);
        final ImageCoverageReader reader = new ImageCoverageReader();
        reader.setInput(input);
        return reader;
    }

    /**
     * Creates a simple writer which does not perform any pyramid or mosaic tiling.
     * This writer is appropriate if the image is known to be small.
     * <p>
     * The output is typically a {@link File}, {@link java.nio.file.Path}, {@link URL} or {@link String} object, but other types
     * (especially {@link javax.imageio.stream.ImageOutputStream}) may be accepted as well depending
     * on the image format. The given output can also be an {@link javax.imageio.ImageWriter} instance
     * with its output initialized.
     *
     * @param  output The output where to write (typically a {@link File} or a {@link java.nio.file.Path}).
     * @return A coverage writer for the given output.
     * @throws DataStoreException If the writer can not be created for the given file.
     *
     * @since 3.20
     */
    public static ImageCoverageWriter createSimpleWriter(final Object output) throws DataStoreException {
        ensureNonNull("output", output);
        final ImageCoverageWriter writer = new ImageCoverageWriter();
        writer.setOutput(output);
        return writer;
    }

    /**
     * Wraps input {@link ImageInputStream} in a {@link CheckedImageInputStream} if assertions are enabled.
     *
     * @param  input The input ImageInputStream to wrap
     * @return The image input stream wrapped
     */
    private static ImageInputStream wrapImageInputStream(final ImageInputStream input) {
        ImageInputStream wrapped = input;
        assert CheckedImageInputStream.isValid(wrapped = // Intentional side effect.
                CheckedImageInputStream.wrap(wrapped));
        return wrapped;
    }

    /**
     * Try to create an {@link ImageInputStream} from an object. This input object is usually an instance of
     * {@link Path}, {@link File}, {@link String}, {@link URL} or {@link java.io.InputStream}.
     *
     * If assertions are enabled returned {@link ImageInputStream} is wrapped in a {@link CheckedImageInputStream}.
     *
     * @param input object usually an instance of {@link Path}, {@link File}, {@link String}, {@link URL}
     *              or {@link java.io.InputStream}.
     * @return ImageInputStream of input object.
     * @throws IOException if an error occurred while creating the input stream.
     */
    public static ImageInputStream createImageInputStream(Object input) throws IOException {

        //most of the cases
        ImageInputStream iis = ImageIO.createImageInputStream(input);
        if (iis != null) {
            return wrapImageInputStream(iis);
        }

        /*
         * We tried the input directly in case the user provided some SPI for String
         * objects. If we have not been able to create a stream from a plain string,
         * create a URL or a File object from the string and try again.
         */
        if (input instanceof CharSequence) {
            final String path = input.toString();
            final Object url;
            if (path.indexOf("://") >= 1) {
                url = new URL(path);
            } else {
                url = new File(path);
            }
            iis = ImageIO.createImageInputStream(url);
            if (iis != null) {
                return wrapImageInputStream(iis);
            }
        }

        /*
         * In theory ImageIO.createImageInputStream(Object) should have accepted a File input,
         * so the following check is useless. However if ImageIO.createImageInputStream(Object)
         * failed, it just returns null; we have no idea why it failed. One possible cause is
         * "Too many open files", in which case throwing a FileNotFoundException is misleading.
         * So we try here to create a FileImageInputStream directly, which is likely to fail as
         * well but this time with a more accurate error message.
         */
        if (input instanceof File) {
            return wrapImageInputStream(new FileImageInputStream((File) input));
        }

        throw new IOException("Can't create ImageInputStream from input "+input.toString());
    }

    /**
     * Wraps input {@link ImageInputStream} in a {@link CheckedImageInputStream} if assertions are enabled.
     *
     * @param  output The input ImageInputStream to wrap
     * @return The image input stream wrapped
     */
    private static ImageOutputStream wrapImageOutputStream(final ImageOutputStream output) {
        ImageOutputStream wrapped = output;
        assert CheckedImageOutputStream.isValid(wrapped = // Intentional side effect.
                CheckedImageOutputStream.wrap(wrapped));
        return wrapped;
    }

    /**
     * Try to create an {@link ImageOutputStream} from an object. This input object is usually an instance of
     * {@link Path}, {@link File}, {@link String}, {@link URL} or {@link java.io.InputStream}.
     *
     * @param output object usually an instance of {@link Path}, {@link File}, {@link String}, {@link URL}
     *              or {@link java.io.InputStream}.
     * @return ImageOutputStream of input object, not wrapped.
     * @throws IOException if an error occurred while creating the input stream.
     * @throws FileNotFoundException if input is not supported
     */
    public static ImageOutputStream createImageOutputStream(Object output) throws IOException{

        //most of the cases
        ImageOutputStream ios = ImageIO.createImageOutputStream(output);
        if (ios != null) {
            return wrapImageOutputStream(ios);
        }

        /*
         * We tried the input directly in case the user provided some SPI for String
         * objects. If we have not been able to create a stream from a plain string,
         * create a URL or a File object from the string and try again.
         */
        if (output instanceof CharSequence) {
            final String path = output.toString();
            final Object url;
            if (path.indexOf("://") >= 1) {
                url = new URL(path);
            } else {
                url = new File(path);
            }
            ios = ImageIO.createImageOutputStream(url);
            if (ios != null) {
                return wrapImageOutputStream(ios);
            }
        }

        /*
         * In theory ImageIO.createImageInputStream(Object) should have accepted a File input,
         * so the following check is useless. However if ImageIO.createImageInputStream(Object)
         * failed, it just returns null; we have no idea why it failed. One possible cause is
         * "Too many open files", in which case throwing a FileNotFoundException is misleading.
         * So we try here to create a FileImageInputStream directly, which is likely to fail as
         * well but this time with a more accurate error message.
         */
        if (output instanceof File) {
            return wrapImageOutputStream(new FileImageOutputStream((File) output));
        }

        throw new FileNotFoundException(Errors.format(
                Errors.Keys.FileDoesNotExist_1, output));
    }

}
