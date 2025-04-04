/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013-2019, Geomatys
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
package org.geotoolkit.data.nmea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.OffsetTime;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.measure.Unit;

import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.measure.Units;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.storage.StorageConnector;

import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.util.NamesExt;

import org.locationtech.jts.geom.Point;

/**
 * A feature store for GPS measures of NMEA standard.
 * Note that for the moment, only reading is supported. Two readings are possible :
 * - From a text file containing a list of measures.
 * - Directly by getting messages sent by a GPS device on serial port.
 *
 * @author Alexis Manin (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class NMEAStore extends DataStore implements FeatureSet {

    static final Logger LOGGER = Logger.getLogger("org.geotoolkit.data.nmea");

    // INTERNAL USE OPTIMISATION: avoid calling synchronized "toString" method on local names.
    public final static String RAW_TYPE_NAME  = "NMEA POINT";
    public final static String RAW_GEOM_NAME  = "Location";
    public final static String RAW_ALT_NAME   = "Altitude";
    public final static String RAW_DEPTH_NAME = "Sea-depth";
    public final static String RAW_DATE_NAME  = "Date";
    public final static String RAW_TIME_NAME  = "Time";
    public final static String RAW_SPEED_NAME = "Speed";
    public final static String RAW_HEADING_NAME = "Heading";
    public final static String RAW_IS_TRUE_NORTH_NAME = "IsTrueNorth";

    public final static GenericName TYPE_NAME  = NamesExt.create(null, RAW_TYPE_NAME);
    public final static GenericName GEOM_NAME  = NamesExt.create(null, RAW_GEOM_NAME);
    public final static GenericName ALT_NAME   = NamesExt.create(null, RAW_ALT_NAME);
    public final static GenericName DEPTH_NAME = NamesExt.create(null, RAW_DEPTH_NAME);
    public final static GenericName DATE_NAME  = NamesExt.create(null, RAW_DATE_NAME);
    public final static GenericName TIME_NAME  = NamesExt.create(null, RAW_TIME_NAME);
    public final static GenericName SPEED_NAME = NamesExt.create(null, RAW_SPEED_NAME);
    /**
     * Heading attribute, in degree, origin North and direction clockwise.
     */
    public final static GenericName HEADING_NAME = NamesExt.create(null, RAW_HEADING_NAME);
    /**
     * True if {@link #HEADING_NAME heading attribute} origin is true north, false if it is magnetic north.
     */
    public final static GenericName IS_TRUE_NORTH_NAME = NamesExt.create(null, RAW_IS_TRUE_NORTH_NAME);

    /**
     * Feature type to use for nmea data encapsulation. Only a subset of available signals are transcripted for now:
     * <ul>
     *     <li>Position (OGC:CRS:84)</li>
     *     <li>Altitude above mean sea level in meter</li>
     *     <li>Date and time of the measure</li>
     *     <li>Speed Over Ground in knots</li>
     * </ul>
     *
     */
    public static final FeatureType NMEA_TYPE;
    static {
        final FeatureTypeBuilder builder = new FeatureTypeBuilder();
        builder.setName(TYPE_NAME);
        builder.addAttribute(Point.class).setName(GEOM_NAME).setCRS(CommonCRS.WGS84.normalizedGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        builder.addAttribute(Double.class).setName(ALT_NAME).setCRS(CommonCRS.Vertical.MEAN_SEA_LEVEL.crs());
        builder.addAttribute(Double.class).setName(DEPTH_NAME);
        builder.addAttribute(LocalDate.class).setName(DATE_NAME);
        builder.addAttribute(OffsetTime.class).setName(TIME_NAME);
        final Unit<?> knots = Units.NAUTICAL_MILE.divide(Units.HOUR);
        builder.addAttribute(Double.class).setName(SPEED_NAME).addCharacteristic(Unit.class).setName("unit").setDefaultValue(knots);
        builder.addAttribute(Double.class).setName(HEADING_NAME)
                .setDescription("Heading angle in degrees, from 0 to 360. North origin, clowckwise.")
                .addCharacteristic(Unit.class).setName("unit").setDefaultValue(Units.DEGREE);
        builder.addAttribute(Boolean.class).setName(IS_TRUE_NORTH_NAME)
                .setDescription("True if heading origin represents True North, False if it is magnetic north.");
        NMEA_TYPE = builder.build();
    }

    private final Parameters parameters;
    private final Path file;

    public NMEAStore(final ParameterValueGroup params) throws DataStoreException {
        super(DataStores.getProviderById(NMEAProvider.NAME), new StorageConnector(Parameters.castOrWrap(params).getMandatoryValue(NMEAProvider.PATH)));
        parameters = Parameters.unmodifiable(params);
        final URI uri = parameters.getMandatoryValue(NMEAProvider.PATH);
        try {
            this.file = IOUtilities.toPath(uri);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
    }

    public NMEAStore(final URI sourceFile) throws DataStoreException {
        this(toParameters(sourceFile));
    }

    private static ParameterValueGroup toParameters(final URI sourceFile) {
        final Parameters openParam = Parameters.castOrWrap(NMEAProvider.PARAMETERS_DESCRIPTOR.createValue());
        openParam.getOrCreate(NMEAProvider.PATH).setValue(sourceFile);
        return openParam;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        return Optional.of(getType().getName());
    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        return Optional.of(parameters);
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        final DefaultMetadata meta = new DefaultMetadata();
        return meta;
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return Optional.empty();
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        return NMEA_TYPE;
    }

    @Override
    public Stream<Feature> features(boolean bln) {
        return Stream.of(file)
                .flatMap(file -> {
                    try {
                        final BufferedReader fileReader = Files.newBufferedReader(file);
                        return Stream.of(fileReader).onClose(() -> {
                            try {
                                fileReader.close();
                            } catch (IOException e) {
                                LOGGER.log(Level.WARNING, "Cannot free file handle", e);
                            }
                        });
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                })
                .flatMap(reader -> StreamSupport.stream(new FileSpliterator(reader), false));
    }

    @Override
    public Optional<FileSet> getFileSet() {
        return Optional.of(new FileSet(file));
    }

    @Override
    public void close() throws DataStoreException {
    }

    private static class FileSpliterator implements Spliterator<Feature> {

        final BufferedReader input;

        final FeatureProcessor processor;

        private FileSpliterator(BufferedReader input) {
            this.input = input;
            this.processor = new FeatureProcessor();
        }

        @Override
        public boolean tryAdvance(Consumer<? super Feature> action) {
            String line;
            while ((line = nextLine()) != null) {
                final boolean update = FeatureProcessor.read(line)
                        .map(processor::update)
                        .orElse(false);

                if (update && processor.isSpaceTimeSet()) {
                    action.accept(processor.getSnapshot());
                    return true;
                }
            }

            return false;
        }

        private String nextLine() {
            try {
                return input.readLine();
            } catch (IOException e) {
                throw new UncheckedIOException("Failed reading line from NMEA file", e);
            }
        }

        @Override
        public Spliterator<Feature> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            return ORDERED | NONNULL;
        }
    }
}
