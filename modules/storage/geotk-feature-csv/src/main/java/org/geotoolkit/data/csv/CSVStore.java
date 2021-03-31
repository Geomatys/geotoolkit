/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2019, Geomatys
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

package org.geotoolkit.data.csv;

import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.*;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.apache.sis.referencing.NamedIdentifier;
import org.apache.sis.storage.DataStore;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.StorageConnector;
import org.apache.sis.storage.WritableFeatureSet;
import org.apache.sis.storage.event.StoreEvent;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.storage.event.FeatureStoreContentEvent;
import org.geotoolkit.storage.event.FeatureStoreManagementEvent;
import org.geotoolkit.feature.FeatureExt;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.util.NamesExt;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.ResourceId;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.Metadata;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;
import org.opengis.util.GenericName;

/**
 * CSV DataStore, holds a single feature type which name match the file name.
 *
 * Specification :
 * https://www.ietf.org/rfc/rfc4180.txt
 *
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
 */
public class CSVStore extends DataStore implements WritableFeatureSet, ResourceOnFileSystem {

    public static final String COMMENT_STRING = "#";
    public static final Charset UTF8_ENCODING = Charset.forName("UTF-8");

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.csv");

    protected final FilterFactory FF = FilterUtilities.FF;

    static final String BUNDLE_PATH = "org/geotoolkit/csv/bundle";

    private static final Pattern ESCAPE_PATTERN = Pattern.compile("\"");

    private final ReadWriteLock fileLock = new ReentrantReadWriteLock();

    private final Parameters parameters;
    private final Path file;
    private String name;
    private final char separator;

    private FeatureType featureType;


    public CSVStore(final Path f, final char separator) throws MalformedURLException, DataStoreException{
        this(f,separator,null);
    }

    /**
     * Constructor forcing feature type, if the CSV does not have any header.
     *
     */
    public CSVStore(final Path f, final char separator, FeatureType ft) throws MalformedURLException, DataStoreException{
        this(toParameters(f, separator));
        if (ft != null) {
            this.featureType = ft;
            name = featureType.getName().tip().toString();
        }
    }

    public CSVStore(final ParameterValueGroup params) throws DataStoreException {
        super(DataStores.getProviderById(CSVProvider.NAME), new StorageConnector(Parameters.castOrWrap(params).getMandatoryValue(CSVProvider.PATH)));
        parameters = Parameters.unmodifiable(params);
        final URI uri = parameters.getMandatoryValue(CSVProvider.PATH);
        try {
            this.file = IOUtilities.toPath(uri);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
        this.separator = parameters.getValue(CSVProvider.SEPARATOR);

        final String path = uri.toString();
        final int slash = Math.max(0, path.lastIndexOf('/') + 1);
        int dot = path.indexOf('.', slash);
        if (dot < 0) {
            dot = path.length();
        }
        this.name = path.substring(slash, dot);
    }

    private static ParameterValueGroup toParameters(final Path f, final Character separator) throws MalformedURLException {
        final Parameters params = Parameters.castOrWrap(CSVProvider.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(CSVProvider.PATH).setValue(f.toUri());
        params.getOrCreate(CSVProvider.SEPARATOR).setValue(separator);
        return params;
    }

    @Override
    public Optional<GenericName> getIdentifier() throws DataStoreException {
        checkExist();
        return Optional.of(featureType.getName());
    }

    Path getFile() {
        return file;
    }

    char getSeparator() {
        return separator;
    }

    private synchronized void checkExist() throws DataStoreException{
        if(featureType == null) featureType = readType();
    }

    Path createWriteFile() throws MalformedURLException{
        return (Path) IOUtilities.changeExtension(file, "wcsv");
    }

    private FeatureType readType() throws DataStoreException {
        final String line;
        fileLock.readLock().lock();
        try (final Scanner scanner = new Scanner(file)) {
            line = CSVUtils.getNextLine(scanner);
        } catch (IOException ex) {
            LOGGER.log(Level.INFO, ex.getLocalizedMessage());
            // File does not exists.
            return null;
        } finally {
            fileLock.readLock().unlock();
        }

        if (line == null) {
            return null;
        }

        int unnamed = 0;
        final String[] fields = line.split("" + separator, -1);
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(name);

        ftb.addAttribute(Integer.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);

        GenericName defaultGeometryFieldName = null;

        for (String field : fields) {

            field = field.trim();
            if(field.isEmpty()){
                field = "unamed"+(unnamed++);
            }

            final int dep = field.indexOf('(');
            final int fin = field.lastIndexOf(')');

            final GenericName fieldName;
            Class type = String.class;

            AttributeTypeBuilder atb = ftb.addAttribute(Object.class);
            // Check non-empty parenthesis
            if (dep > 0 && fin > dep + 1) {
                fieldName = NamesExt.create(field.substring(0, dep));
                //there is a defined type
                final String name = field.substring(dep + 1, fin);
                /* Check if it's a java lang class (number, string, etc.). If it's a fail, maybe it's just
                 * because of the case, so we'll try to identify object manually.
                 */
                try {
                    type = Class.forName("java.lang." + name);
                } catch (Exception e) {
                    if ("integer".equalsIgnoreCase(name)) {
                        type = Integer.class;
                    } else if ("float".equalsIgnoreCase(name)) {
                        type = Float.class;
                    } else if ("double".equalsIgnoreCase(name)) {
                        type = Double.class;
                    } else if ("string".equalsIgnoreCase(name)) {
                        type = String.class;
                    } else if ("date".equalsIgnoreCase(name)) {
                        type = Date.class;
                    } else if ("boolean".equalsIgnoreCase(name)) {
                        type = Boolean.class;
                    } else {
                        if(name.contains(":")){
                            try {
                                //check if it's a geometry type
                                atb.setCRS(CRS.forCode(name));
                                type = Geometry.class;
                                if (defaultGeometryFieldName == null) {
                                    //store first geometry as default
                                    defaultGeometryFieldName = fieldName;
                                }
                            } catch (NoSuchAuthorityCodeException ex) {
                                LOGGER.log(Level.SEVERE, null, ex);
                            } catch (FactoryException ex) {
                                LOGGER.log(Level.SEVERE, null, ex);
                            }
                        }else{
                            type = String.class;
                        }
                    }
                }
            } else {
                fieldName = NamesExt.create(field);
                type = String.class;
            }

            atb.setName(fieldName);
            atb = atb.setValueClass(type);
            if (fieldName == defaultGeometryFieldName) {
                atb.addRole(AttributeRole.DEFAULT_GEOMETRY);
            }
        }

        return ftb.build();
    }

    @Override
    public Optional<ParameterValueGroup> getOpenParameters() {
        return Optional.of(parameters);
    }

    @Override
    public Metadata getMetadata() throws DataStoreException {
        final DefaultMetadata metadata = new DefaultMetadata();
        final DefaultDataIdentification idf = new DefaultDataIdentification();
        final DefaultCitation citation = new DefaultCitation();
        citation.getIdentifiers().add(NamedIdentifier.castOrCopy(getIdentifier().get()));
        idf.setCitation(citation);
        metadata.setIdentificationInfo(Arrays.asList(idf));
        return metadata;
    }

    @Override
    public Optional<Envelope> getEnvelope() throws DataStoreException {
        return Optional.empty();
    }

    @Override
    public FeatureType getType() throws DataStoreException {
        checkExist();
        return featureType;
    }

    @Override
    public Path[] getComponentFiles() throws DataStoreException {
        return new Path[] { this.file };
    }

    @Override
    public Stream<Feature> features(boolean parallel) throws DataStoreException {
        final CSVReader reader = new CSVReader(this, getType(), fileLock);
        final Stream<Feature> stream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(reader, Spliterator.ORDERED), false);
        stream.onClose(reader::close);
        return stream;
    }

    /**
     * Forward event to all listeners.
     * @param event , event to send to listeners.
     *
     * @todo should specify the event type.
     */
    protected void sendEvent(final StoreEvent event) {
        listeners.fire(event, StoreEvent.class);
    }

    @Override
    public void close() throws DataStoreException {
    }



    @Override
    public void updateType(FeatureType newType) throws DataStoreException {
        if (!newType.isSimple()) {
            throw new DataStoreException("Feature type must be simple.");
        }

        //Delete old type
        final FeatureType oldSchema = featureType;
        try {
            fileLock.writeLock().lock();
            Files.deleteIfExists(file);
            featureType = null;
        } catch (IOException e) {
            throw new DataStoreException(e.getLocalizedMessage(), e);
        } finally {
            fileLock.writeLock().unlock();
        }

        //Create new one
        featureType = newType;
        try {
            fileLock.writeLock().lock();
            writeType(featureType);
        } finally {
            fileLock.writeLock().unlock();
        }

        sendEvent(FeatureStoreManagementEvent.createUpdateEvent(this, newType.getName(), oldSchema, newType));
    }

    String createHeader(final FeatureType type) throws DataStoreException{
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (PropertyType desc : type.getProperties(true)) {
            if (AttributeConvention.contains(desc.getName())) continue;

            if(first){
                first = false;
            }else{
                sb.append(separator);
            }

            sb.append(desc.getName().tip());
            sb.append('(');
            final Class clazz = ((AttributeType)desc).getValueClass();
            if(Number.class.isAssignableFrom(clazz) || float.class.equals(clazz)
                    || double.class.equals(clazz) || int.class.equals(clazz)
                    || short.class.equals(clazz) || byte.class.equals(clazz)) {
                sb.append(clazz.getSimpleName());
            }else if(clazz.equals(String.class)){
                sb.append("String");
            }else if(clazz.equals(Date.class)){
                sb.append("Date");
            }else if(clazz.equals(Boolean.class)){
                sb.append("boolean");
            }else if(Geometry.class.isAssignableFrom(clazz)){
                sb.append(IdentifiedObjects.toString(IdentifiedObjects.getIdentifier(FeatureExt.getCRS(desc), null)));
            }else{
                //unsuported, output it as text
                sb.append("String");
            }

            sb.append(')');
        }
        return sb.toString();
    }

    private void writeType(final FeatureType type) throws DataStoreException {
        name = type.getName().tip().toString();

        fileLock.writeLock().lock();
        try (final Writer output = Files.newBufferedWriter(file, UTF8_ENCODING, CREATE, WRITE)) {
            output.write(createHeader(type));
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        } finally {
            fileLock.writeLock().unlock();
        }
    }

    @Override
    public void add(Iterator<? extends Feature> features) throws DataStoreException {

        try (final CSVWriter writer = new CSVWriter(this, featureType, fileLock)) {
            //skip to end of records
            while (writer.hasNext()) writer.next();

            RuntimeException error = null;
            try {
                while (features.hasNext()) {
                    final Feature f = features.next();
                    final Feature candidate = writer.next();
                    FeatureExt.copy(f, candidate, false);
                    writer.write();
                }
            } catch (RuntimeException e) {
                error = e;
                throw error;
            }
        }
    }

    @Override
    public boolean removeIf(Predicate<? super Feature> filter) throws DataStoreException {
        boolean changed = false;
        try (final CSVWriter writer = new CSVWriter(this, featureType, fileLock)) {
            while (writer.hasNext()) {
                Feature candidate = writer.next();
                if (filter.test(candidate)) {
                    changed = true;
                    writer.remove();
                }
            }
        }

        return changed;
    }

    @Override
    public void replaceIf(Predicate<? super Feature> filter, UnaryOperator<Feature> updater) throws DataStoreException {
        try (final CSVWriter writer = new CSVWriter(this, featureType, fileLock)) {
            while (writer.hasNext()) {
                Feature candidate = writer.next();
                if (filter.test(candidate)) {
                    Feature res = updater.apply(candidate);
                    if (res != candidate) {
                        FeatureExt.copy(res, candidate, false);
                    }
                    writer.write();
                }
            }
        }
    }

    void fireDataChangeEvents(Set<ResourceId> addedIds, Set<ResourceId> updatedIds, Set<ResourceId> deletedIds) {
        if (!addedIds.isEmpty()) {
            sendEvent(new FeatureStoreContentEvent(this, FeatureStoreContentEvent.Type.ADD, featureType.getName(), addedIds));
        }

        if (!updatedIds.isEmpty()) {
            sendEvent(new FeatureStoreContentEvent(this, FeatureStoreContentEvent.Type.UPDATE, featureType.getName(), updatedIds));
        }

        if (!deletedIds.isEmpty()) {
            sendEvent(new FeatureStoreContentEvent(this, FeatureStoreContentEvent.Type.DELETE, featureType.getName(), deletedIds));
        }
    }
}
