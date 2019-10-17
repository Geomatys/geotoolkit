/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2016, Geomatys
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
package org.geotoolkit.coverage.worldfile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataSet;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.IllegalNameException;
import org.apache.sis.storage.Resource;
import org.apache.sis.storage.WritableAggregate;
import org.geotoolkit.image.io.UnsupportedImageFormatException;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.internal.image.io.SupportFiles;
import org.geotoolkit.metadata.MetadataUtilities;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.coverage.AbstractCoverageStore;
import org.geotoolkit.storage.coverage.DefiningCoverageResource;
import org.geotoolkit.util.NamesExt;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * Coverage Store which rely on standard java readers and writers.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class FileCoverageStore extends AbstractCoverageStore implements ResourceOnFileSystem, WritableAggregate {

    private static final String REGEX_SEPARATOR;
    static {
        if (File.separatorChar == '\\') {
            REGEX_SEPARATOR = "\\\\";
        } else {
            REGEX_SEPARATOR = File.separator;
        }
    }

    private final Path root;
    private final String format;
    private final URI rootPath;

    private final String separator;

    //default spi
    final ImageReaderSpi spi;

    private List<Resource> resources = null;

    public FileCoverageStore(URL url, String format) throws URISyntaxException {
        this(toParameters(url.toURI(), format));
    }

    public FileCoverageStore(Path path, String format) {
        this(toParameters(path.toUri(), format));
    }

    public FileCoverageStore(URI uri, String format) {
        this(toParameters(uri, format));
    }

    public FileCoverageStore(ParameterValueGroup params) {
        super(params);
        Parameters p = Parameters.castOrWrap(params);
        rootPath = p.getValue(FileCoverageProvider.PATH);
        separator = p.getValue(FileCoverageProvider.PATH_SEPARATOR);
        format = p.getValue(FileCoverageProvider.TYPE);
        root = Paths.get(rootPath);

        if ("AUTO".equalsIgnoreCase(format)) {
            spi = null;
        } else {
            spi = XImageIO.getReaderSpiByFormatName(format);
        }

    }

    private static ParameterValueGroup toParameters(URI uri, String format) {
        final Parameters params = Parameters.castOrWrap(FileCoverageProvider.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(FileCoverageProvider.PATH).setValue(uri);
        if (format!=null) {
            params.getOrCreate(FileCoverageProvider.TYPE).setValue(format);
        }
        return params;
    }

    @Override
    public DataStoreProvider getProvider() {
        return DataStores.getProviderById(FileCoverageProvider.NAME);
    }

    @Override
    public synchronized Collection<Resource> components() throws DataStoreException {
        if (resources == null) {
            resources = new ArrayList<>();
            try {
                visit(root);
            } catch (DataStoreException ex) {
                throw ex;
            }catch (IOException ex) {
                throw new DataStoreException(ex.getMessage(),ex);
            }
        }
        return Collections.unmodifiableList(resources);
    }

    /**
     * Visit all files and directories contained in the directory specified.
     *
     * @param file
     */
    private void visit(final Path file) throws IOException, DataStoreException {

        if (Files.isRegularFile(root)) {
            //we opened a single file, we consider it as a real error
            try {
                test(file);
            } catch (Exception ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }

        } else {
            //explore as a folder, we only throw warnings for unsupported files.
            //this behavior ensure the store will be opened even if a few files are corrupted.
            Files.walkFileTree(file, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        test(file);
                    } catch (UnsupportedImageFormatException ex) {
                        // Tried to parse a incompatible file, not really an error.
                        final LogRecord rec = new LogRecord(Level.FINE, "Unsupported image format encoding or compression for file "+IOUtilities.filename(file)+" : "+ex.getMessage());
                        rec.setThrown(ex);
                        listeners.warning(rec);
                    } catch (Exception ex) {
                        //Exception type is not specified cause we can get IOException as IllegalArgumentException.
                        final LogRecord rec = new LogRecord(Level.WARNING, "Exception occured decoding file "+IOUtilities.filename(file)+" : "+ex.getMessage());
                        rec.setThrown(ex);
                        listeners.warning(rec);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    private String createLayerName(final Path candidate) {
        if (separator != null) {
            //TODO use relativize()
            final Path absRoot = root.toAbsolutePath();
            final Path abscandidate = candidate.toAbsolutePath();
            String fullName = abscandidate.toString().replace(absRoot.toString(), "");
            if (fullName.startsWith(File.separator)) {
                fullName = fullName.substring(1);
            }
            fullName = fullName.replaceAll(REGEX_SEPARATOR, separator);
            final int idx = fullName.lastIndexOf('.');
            return fullName.substring(0, idx);
        } else {
            return IOUtilities.filenameWithoutExtension(candidate);
        }
    }

    /**
     *
     * @param candidate Candidate to be a image file.
     */
    private void test(final Path candidate) throws Exception {
        if (!Files.isRegularFile(candidate)) {
            return;
        }
        ImageReader reader = null;
        try {
            //don't comment this block, This raise an error if no reader for the file can be found
            //this way we are sure that the file is an image.
            reader = createReader(candidate, spi);

            final String filename = createLayerName(candidate);

            final int nbImage = reader.getNumImages(true);

            for (int i = 0; i < nbImage; i++) {
                final GenericName name;
                if (nbImage == 1) {
                    //don't number it if there is only one
                    name = NamesExt.create(filename);
                } else {
                    name = NamesExt.create(filename + "." + i);
                }

                GridCoverageResource fcr = new FileCoverageResource(this, name, candidate);

                resources.add(fcr);
            }

        } finally {
            XImageIO.disposeSilently(reader);
        }
    }

    @Override
    public void close() {
    }

    /**
     * Create a reader for the given file.
     * Detect automatically the spi if type is set to 'AUTO'.
     *
     * @param candidate file to read
     * @param spi used to create ImageReader. If null, detect automatically from candidate file.
     * @return ImageReader, never null
     * @throws IOException if fail to create a reader.
     * @throws UnsupportedImageFormatException if spi is defined but can't decode candidate file
     */
    static ImageReader createReader(final Path candidate, ImageReaderSpi spi) throws IOException {
        final ImageReader reader;
        search:
        if (spi == null) {
            //we definitely can't trust ordering in image spi, this depends on so much initialization stuff
            //sometimes properly called, sometimes not.
            //Until total coverage api migration to SIS is finished NEVER EVER REMOVE THIS HACK
            //I've spend so much time debugging spi order, fixing it and it comes back later because environment changed
            //or whatever else. WorldFile must be first, ALWAYS tested before basic image readers.
            final IIORegistry registry = IIORegistry.getDefaultInstance();
            final Iterator<ImageReaderSpi> ite = registry.getServiceProviders(ImageReaderSpi.class, true);
            while (ite.hasNext()) {
                ImageReaderSpi sp = ite.next();
                if (sp instanceof WorldFileImageReader.Spi) {
                    if (sp.canDecodeInput(candidate)) {
                        reader = sp.createReaderInstance();
                        reader.setInput(candidate, false, false);
                        break search;
                    }
                }
            }

            reader = XImageIO.getReader(candidate, Boolean.FALSE, Boolean.FALSE);
        } else {
            Object in = XImageIO.toSupportedInput(spi, candidate);
            if (spi.canDecodeInput(in)) {
                reader = spi.createReaderInstance();
                reader.setInput(in);
            } else {
                throw new UnsupportedImageFormatException("Unsupported file input for spi "+spi.getPluginClassName());
            }
        }

        return reader;
    }

    /**
     * Create a writer for the given file.
     * Detect automatically the spi if type is set to 'AUTO'.
     *
     * @param candidate
     * @return ImageWriter, never null
     * @throws IOException if fail to create a writer.
     */
    ImageWriter createWriter(final Path candidate) throws IOException {
        if (Files.exists(candidate)) {
            final ImageReaderSpi readerSpi = createReader(candidate,spi).getOriginatingProvider();
            final String[] writerSpiNames = readerSpi.getImageWriterSpiNames();
            if(writerSpiNames == null || writerSpiNames.length == 0){
                throw new IOException("No writer for this format.");
            }

            return XImageIO.getWriterByFormatName(readerSpi.getFormatNames()[0], candidate, null);
        } else if (spi != null) {
            return XImageIO.getWriterByFormatName(spi.getFormatNames()[0], candidate, null);
        } else {
            final String extension = IOUtilities.extension(candidate);
            if ("tif".equalsIgnoreCase(extension) || "tiff".equalsIgnoreCase(extension)) {
                //take geotoolkit geotiff writer
                return XImageIO.getWriterByFormatName("geotiff", candidate, null);
            } else {
                return XImageIO.getWriterBySuffix(candidate, null);
            }
        }
    }

    @Override
    public Path[] getComponentFiles() throws DataStoreException {
        if (Files.isDirectory(root)) {
            return new Path[] {root};
        }

        // HACK : If the file is a single image with world-file metadata, we
        // force them to be part of the resource, as they could serve to
        // override bad referencing data in primary image.
        final List<Path> componentFiles = new ArrayList<>(3);
        componentFiles.add(root);
        final String tfwSuffix = SupportFiles.toSuffixTFW(root);
        Path tfwFile = IOUtilities.changeExtension(root, tfwSuffix);
        if (Files.isRegularFile(tfwFile)) {
            componentFiles.add(tfwFile);
        } else {
            tfwFile = IOUtilities.changeExtension(root, tfwSuffix.toUpperCase());
            if (Files.isRegularFile(tfwFile)) {
                componentFiles.add(tfwFile);
            }
        }

        Path prjFile = IOUtilities.changeExtension(root, "prj");
        if (Files.isRegularFile(prjFile)) {
            componentFiles.add(prjFile);
        } else {
            prjFile = IOUtilities.changeExtension(root, "PRJ");
            if (Files.isRegularFile(prjFile)) {
                componentFiles.add(prjFile);
            }
        }

        return componentFiles.toArray(new Path[componentFiles.size()]);
    }

    @Override
    public GridCoverageResource add(org.apache.sis.storage.Resource resource) throws DataStoreException {
        if (!(resource instanceof DefiningCoverageResource)) {
            throw new DataStoreException("Unsupported resource "+resource);
        }
        final DefiningCoverageResource cr = (DefiningCoverageResource) resource;
        final GenericName name = cr.getName();

        final Collection<GenericName> names = DataStores.getNames(this, true, DataSet.class);
        if (names.contains(name)) {
            throw new IllegalNameException("Coverage "+name+" already exist in this datastore.");
        }

        final String fileName = name.tip().toString();
        final URI filePath = rootPath.resolve(fileName+".tiff");

        final FileCoverageResource fcr = new FileCoverageResource(this, name, Paths.get(filePath));
        resources.add(fcr);

        return fcr;
    }

    @Override
    public void remove(org.apache.sis.storage.Resource resource) throws DataStoreException {
        if (!(resource instanceof GridCoverageResource)) {
            throw new DataStoreException("Unknown resource "+resource);
        }
        //TODO
        throw new DataStoreException("Remove operation not supported.");
    }

    /**
     * {@inheritDoc }
     * Note : add source file location in metadata.
     */
    @Override
    public Metadata getMetadata() throws DataStoreException {
        Metadata md = super.getMetadata();
        if (md == null)
            md = new DefaultMetadata();

        MetadataUtilities.addOnlineResource(md, rootPath);

        return md;
    }
}
