/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2019, Geomatys
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

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import org.apache.sis.internal.storage.Capability;
import org.apache.sis.internal.storage.StoreMetadata;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.DataStoreProvider;
import org.apache.sis.storage.GridCoverageResource;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.storage.UnsupportedStorageException;
import org.geotoolkit.image.io.SpatialImageReader;
import org.geotoolkit.image.io.WarningProducer;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.internal.image.io.SupportFiles;
import org.geotoolkit.storage.Bundle;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.geotoolkit.storage.StrictStorageConnector;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Coverage Store which rely on standard java readers and writers.
 *
 * @author Johann Sorel (Geomatys)
 */
@StoreMetadata(
        formatName = FileCoverageProvider.NAME,
        capabilities = {Capability.READ, Capability.WRITE, Capability.CREATE},
        resourceTypes = GridCoverageResource.class)
@StoreMetadataExt(resourceTypes = ResourceType.GRID)
public class FileCoverageProvider extends DataStoreProvider {

    /** factory identification **/
    public static final String NAME = "coverage-file";

    /**
     * Mandatory - the folder path
     */
    public static final ParameterDescriptor<URI> PATH = new ParameterBuilder()
            .addName(LOCATION).addName("path")
            .addName(Bundle.formatInternational(Bundle.Keys.path))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.path_remarks))
            .setRequired(true)
            .create(URI.class, null);

    /**
     * Mandatory - the image reader type.
     * Use AUTO if type should be detected automatically.
     */
    public static final ParameterDescriptor<String> TYPE;
    static{
        final LinkedList<String> validValues = new LinkedList<>(getReaderTypeList());
        validValues.add("AUTO");
        Collections.sort(validValues);

        TYPE = new ParameterBuilder()
            .addName("type")
            .addName(Bundle.formatInternational(Bundle.Keys.type))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.type_remarks))
            .setRequired(true)
            .createEnumerated(String.class, validValues.toArray(new String[validValues.size()]), "AUTO");
    }

    public static final ParameterDescriptor<String> PATH_SEPARATOR = new ParameterBuilder()
            .addName("pathSeparator")
            .addName(Bundle.formatInternational(Bundle.Keys.pathSeparator))
            .setRemarks(Bundle.formatInternational(Bundle.Keys.pathSeparator_remarks))
            .setRequired(false)
            .create(String.class, null);

    public static final ParameterDescriptorGroup PARAMETERS_DESCRIPTOR =
            new ParameterBuilder().addName(NAME).addName("FileCoverageStoreParameters").createGroup(
                PATH, TYPE, PATH_SEPARATOR);

    static final Map<ImageReaderSpi,Boolean> SPIS = new HashMap<>();
    static {
        //several SPI are registered under different names
        for (String name : getReaderTypeList()) {
            final ImageReaderSpi spi = XImageIO.getReaderSpiByFormatName(name);
            if (!SPIS.containsKey(spi)) {
                boolean worldFile = true;
                try {
                    ImageReader reader = spi.createReaderInstance();
                    if (reader instanceof SpatialImageReader) {
                        worldFile = false;
                    }
                    reader.dispose();
                } catch (IOException ex) {}
                SPIS.put(spi,worldFile);
            }

        }
    }

    private static FileCoverageProvider INSTANCE;
    /**
     * Get singleton instance of file coverage provider.
     *
     * <p>
     * Note : this method is named after Java 9 service loader provider method.
     * {@link https://docs.oracle.com/javase/9/docs/api/java/util/ServiceLoader.html}
     * </p>
     *
     * @return singleton instance of FileCoverageProvider
     */
    public static synchronized FileCoverageProvider provider() {
        if (INSTANCE == null) INSTANCE = new FileCoverageProvider();
        return INSTANCE;
    }

    @Override
    public String getShortName() {
        return NAME;
    }

    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.coverageFileDescription);
    }

    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.coverageFileTitle);
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public DataStore open(ParameterValueGroup params) throws DataStoreException {
        return new FileCoverageStore(params);
    }

    @Override
    public DataStore open(StorageConnector connector) throws DataStoreException {
        final URI path = connector.commit(URI.class, NAME);
        return new FileCoverageStore(path, null);
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {
        try {
            return probeContent(new StrictStorageConnector(connector));
        } catch (IOException e) {
            throw new DataStoreException("Strict storage support verification failed", e);
        }
    }

    public ProbeResult probeContent(StrictStorageConnector conn) throws IOException, DataStoreException {
        try {
            conn.useAsImageInputStream(in -> true);
        } catch (UnsupportedStorageException | UnsupportedOperationException e) {
            // Any image source should be usable through ImageIO API
            return ProbeResult.UNSUPPORTED_STORAGE;
        }

        final Path path = conn.getPath().orElse(null);
        boolean hasPrj = path != null;
        if (hasPrj) {
            try {
                final Object prj = SupportFiles.changeExtension(path, "prj");
                if (prj == null || !Files.exists((Path) prj)) hasPrj = false;
                final Object tfw = SupportFiles.changeExtension(path, "tfw");
                if (tfw == null || !Files.exists((Path) tfw)) hasPrj = false;
            } catch (UnsupportedOperationException e) {
                // Can happen on third-party file-systems
                WarningProducer.LOGGER.log(Level.FINEST, "Cannot: explore path siblings", e);
            }
        }

        for (Entry<ImageReaderSpi,Boolean> entry : SPIS.entrySet()) {
            // Bypass world-file readers if neither prj nor tfw file is found.
            if (!hasPrj && entry.getValue()) {
                continue;
            }
            final ImageReaderSpi spi = entry.getKey();
            final String spiName = spi.getClass().getName();
            try {
                //Special case for JP2K, this decoder is close to useless
                //it work on a very limited number of cases
                //this decoder is not present by default, added by third-party jars.
                if (spiName.contains("J2KImageReaderSpi")) {
                    continue;
                }

                final Class<?> baseClass;
                //Special case for TextImageReaders, waiting for fix : GEOTK-688
                if (spiName.contains("Text") || spiName.contains("AsciiGrid")) {
                    baseClass = File.class;
                } else baseClass = ImageInputStream.class;

                if (canDecode(spi, conn, baseClass)) {
                    final String mime = getMime(spi)
                            .orElse("image");
                    return new ProbeResult(true, mime, null);
                }
            } catch (EOFException ex) {
                //reached an EOF while testing file, test next spi
                WarningProducer.LOGGER.log(Level.FINEST, ex, () -> "Reached end of file while testing image spi: "+spiName);
            } catch (UnsupportedOperationException e) {
                WarningProducer.LOGGER.log(Level.FINE, e, () -> "Encountered unsupported operation while testing image spi: "+spiName);
            }
        }
        return ProbeResult.UNSUPPORTED_STORAGE;
    }

    private static Optional<String> getMime(final ImageReaderSpi spi) {
        final String[] mimeTypes = spi.getMIMETypes();
        if (mimeTypes == null || mimeTypes.length < 1) return Optional.empty();
        return Arrays.stream(mimeTypes)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .findFirst();
    }

    /**
     * Verify if given image reader service declares provided storage as supported (can read).
     *
     * @param spi Image reader to test support for
     * @param conn The storage connection to use for tests
     * @param baseStorageClass primary storage interface to test. That's a slight optimisation to try an reuse already
     *                         opened streams.
     * @return True if input SPI has successfully tested input storage and marked it a supported, false otherwise.
     * @throws IOException If an error occurs while accessing provided storage.
     * @throws DataStoreException If an error occurs while accessing provided storage.
     */
    private static boolean canDecode(final ImageReaderSpi spi, final StrictStorageConnector conn, final Class baseStorageClass) throws IOException, DataStoreException {
        boolean support;
        try {
            support = conn.useAs(baseStorageClass, spi::canDecodeInput);
        } catch (UnsupportedStorageException e) {
            support = false;
        }

        if (!support) {
            //check other input types
            //example : HGT only support File or Path
            for (Class type : spi.getInputTypes()) {
                if (baseStorageClass.isAssignableFrom(type)) continue;
                try {
                    support = conn.useAs(type, spi::canDecodeInput);
                    // A single valid result should be enough
                    break;
                } catch (IllegalArgumentException | UnsupportedStorageException | FileSystemNotFoundException ex) {
                    WarningProducer.LOGGER.log(Level.FINER, "image reader support test failed", ex);
                } catch (IOException ex) {
                    if (ex.getCause() instanceof FileSystemNotFoundException) {
                        WarningProducer.LOGGER.log(Level.FINER, "image reader support test failed", ex);
                    } else {
                        throw ex;
                    }
                }
            }
        }

        return support;
    }

    /**
     * ONLY FOR INTERNAL USE.
     *
     * List all available formats.
     */
    public static LinkedList<String> getReaderTypeList() {
        ImageIO.scanForPlugins();
        final LinkedList<String> formatsDone = new LinkedList<>();
        for (String format : ImageIO.getReaderFormatNames()) {
            formatsDone.add(format);
        }

        return formatsDone;
    }

    public static GridCoverageResource open(Path file, String format) throws DataStoreException {
        if (Files.isRegularFile(file)) {
            FileCoverageStore store = new FileCoverageStore(file, format);
            Collection<GridCoverageResource> list = DataStores.flatten(store, true, GridCoverageResource.class);
            if (list.isEmpty()) {
                throw new DataStoreException(file.toUri() + " contains no coverage resource.");
            } else {
                return list.iterator().next();
            }
        } else {
            throw new DataStoreException(file.toUri() + " is not a regular file.");
        }
    }
}
