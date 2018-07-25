/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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
package org.geotoolkit.coverage.filestore;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.parameter.ParameterBuilder;
import org.apache.sis.storage.ProbeResult;
import org.apache.sis.storage.StorageConnector;
import org.geotoolkit.image.io.SpatialImageReader;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.internal.image.io.SupportFiles;
import org.geotoolkit.storage.DataStore;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.ResourceType;
import org.geotoolkit.storage.StoreMetadataExt;
import org.geotoolkit.storage.coverage.Bundle;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * Coverage Store which rely on standard java readers and writers.
 *
 * @author Johann Sorel (Geomatys)
 */
@StoreMetadataExt(resourceTypes = ResourceType.GRID, canCreate = true, canWrite = true)
public class FileCoverageProvider extends DataStoreFactory {

    /** factory identification **/
    public static final String NAME = "coverage-file";

    public static final ParameterDescriptor<String> IDENTIFIER = createFixedIdentifier(NAME);

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
                IDENTIFIER, PATH, TYPE, PATH_SEPARATOR);

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
    public CharSequence getDescription() {
        return Bundle.formatInternational(Bundle.Keys.coverageFileDescription);
    }

    @Override
    public CharSequence getDisplayName() {
        return Bundle.formatInternational(Bundle.Keys.coverageFileTitle);
    }

    @Override
    public ParameterDescriptorGroup getOpenParameters() {
        return PARAMETERS_DESCRIPTOR;
    }

    @Override
    public DataStore open(ParameterValueGroup params) throws DataStoreException {
        if(!canProcess(params)){
            throw new DataStoreException("Can not process parameters.");
        }
        try {
            return new FileCoverageStore(params);
        } catch (IOException | URISyntaxException ex) {
            throw new DataStoreException(ex);
        }
    }

    @Override
    public ProbeResult probeContent(StorageConnector connector) throws DataStoreException {

        final ImageInputStream in = connector.getStorageAs(ImageInputStream.class);
        if (in == null) return ProbeResult.UNSUPPORTED_STORAGE;

        for (Entry<ImageReaderSpi,Boolean> entry : SPIS.entrySet()) {
            final ImageReaderSpi spi = entry.getKey();
            try {
                //Special case for TextImageReaders, waiting for fix : GEOTK-688
                Object input = in;
                if (spi.getClass().getName().contains("Text") || spi.getClass().getName().contains("AsciiGrid")) {
                    input = connector.getStorageAs(File.class);
                    if (input == null) continue;
                }
                //Special case for JP2K, this decoder is close to useless
                //it work on a very limited number of cases
                //this decoder is not present by default, added by third-party jars.
                if (spi.getClass().getName().contains("J2KImageReaderSpi")) {
                    continue;
                }

                if (canDecode(spi, connector, input)) {
                    if (Boolean.TRUE.equals(entry.getValue())) {
                        //special case for world files, verify tfw and prj files
                        final Path path = connector.getStorageAs(Path.class);
                        if (path == null) continue;
                        Object prj = SupportFiles.changeExtension(path, "prj");
                        if (prj == null || !Files.exists((Path)prj)) continue;
                        Object tfw = SupportFiles.changeExtension(path, "tfw");
                        if (tfw == null || !Files.exists((Path)tfw)) continue;
                    }

                    final String[] mimeTypes = spi.getMIMETypes();
                    if (mimeTypes != null) {
                        return new ProbeResult(true, mimeTypes[0], null);
                    } else {
                        //no defined mime-type
                        return new ProbeResult(true, "image", null);
                    }
                }
            } catch (IOException ex) {
                throw new DataStoreException(ex.getMessage(), ex);
            }
        }
        return ProbeResult.UNSUPPORTED_STORAGE;
    }

    /**
     * Test if an image spi can decode provided input.
     * @param spi SPI to test
     * @param connector input connector
     * @param in base image input stream
     * @return
     * @throws IOException
     */
    private boolean canDecode(ImageReaderSpi spi, StorageConnector connector, Object in) throws IOException {
        if (spi.canDecodeInput(in)) return true;

        //check other input types
        //example : HGT only support File or Path
        for (Class type : spi.getInputTypes()) {
            try {
                Object tin = connector.getStorageAs(type);
                if (spi.canDecodeInput(tin)) {
                    return true;
                } else {
                    //all other types are expected to return false too
                    return false;
                }
            } catch (IllegalArgumentException | DataStoreException ex) {
                //don't log, it would spam the logs
            }
        }
        return false;
    }

    @Override
    public DataStore create(ParameterValueGroup params) throws DataStoreException {
        return open(params);
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
}
