/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010-2014, Geomatys
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

import com.vividsolutions.jts.geom.Geometry;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.apache.sis.feature.FeatureExt;

import org.geotoolkit.data.*;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.parameter.Parameters;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataFileStore;

import static java.nio.file.StandardOpenOption.*;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;

import org.opengis.util.GenericName;
import org.geotoolkit.storage.DataStores;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.apache.sis.internal.feature.AttributeConvention;

/**
 * CSV DataStore, holds a single feature type which name match the file name.
 *
 * Specification :
 * https://www.ietf.org/rfc/rfc4180.txt
 *
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
 */
public class CSVFeatureStore extends AbstractFeatureStore implements DataFileStore {

    public static final Charset UTF8_ENCODING = Charset.forName("UTF-8");
    protected final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    static final String BUNDLE_PATH = "org/geotoolkit/csv/bundle";

    public static final String COMMENT_STRING = "#";
    private static final Pattern ESCAPE_PATTERN = Pattern.compile("\"");

    private final ReadWriteLock fileLock = new ReentrantReadWriteLock();

    private final Path file;
    private String name;
    private final char separator;

    private FeatureType featureType;


    /**
     * @deprecated use {@link #CSVFeatureStore(Path, String, char)} instead
     */
    public CSVFeatureStore(final File f, final String namespace, final char separator) throws MalformedURLException, DataStoreException{
        this(f.toPath(),namespace,separator,null);
    }

    /**
     * @deprecated use {@link #CSVFeatureStore(Path, String, char, FeatureType)} instead
     */
    public CSVFeatureStore(final File f, final String namespace, final char separator, FeatureType ft) throws MalformedURLException, DataStoreException{
        this(f.toPath(), namespace, separator, ft);
    }

    public CSVFeatureStore(final Path f, final String namespace, final char separator) throws MalformedURLException, DataStoreException{
        this(f,namespace,separator,null);
    }

    /**
     * Constructor forcing feature type, if the CSV does not have any header.
     *
     */
    public CSVFeatureStore(final Path f, final String namespace, final char separator, FeatureType ft) throws MalformedURLException, DataStoreException{
        this(toParameters(f, namespace, separator));
        if(ft!=null){
            this.featureType = ft;
            name = featureType.getName().tip().toString();
        }
    }

    public CSVFeatureStore(final ParameterValueGroup params) throws DataStoreException {
        super(params);

        final URI uri = (URI) params.parameter(CSVFeatureStoreFactory.PATH.getName().toString()).getValue();
        try {
            this.file = IOUtilities.toPath(uri);
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }
        this.separator = (Character) params.parameter(CSVFeatureStoreFactory.SEPARATOR.getName().toString()).getValue();

        final String path = uri.toString();
        final int slash = Math.max(0, path.lastIndexOf('/') + 1);
        int dot = path.indexOf('.', slash);
        if (dot < 0) {
            dot = path.length();
        }
        this.name = path.substring(slash, dot);

    }

    private static ParameterValueGroup toParameters(final Path f,
            final String namespace, final Character separator) throws MalformedURLException{
        final ParameterValueGroup params = CSVFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(CSVFeatureStoreFactory.PATH, params).setValue(f.toUri());
        Parameters.getOrCreate(CSVFeatureStoreFactory.NAMESPACE, params).setValue(namespace);
        Parameters.getOrCreate(CSVFeatureStoreFactory.SEPARATOR, params).setValue(separator);
        return params;
    }

    @Override
    public FeatureStoreFactory getFactory() {
        return (FeatureStoreFactory) DataStores.getFactoryById(CSVFeatureStoreFactory.NAME);
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
            getLogger().log(Level.INFO, ex.getLocalizedMessage());
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
        final String ns = getDefaultNamespace();
        if (ns != null) {
            ftb.setName(ns, name);
        } else {
            ftb.setName(name);
        }
        ftb.addAttribute(String.class).setName(AttributeConvention.IDENTIFIER_PROPERTY);

        GenericName defaultGeometryFieldName = null;
        final List<AttributeType> atts = new ArrayList<>();

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
                fieldName = NamesExt.create(ns, field.substring(0, dep));
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
                                if(defaultGeometryFieldName==null){
                                    //store first geometry as default
                                    defaultGeometryFieldName = fieldName;
                                }
                            } catch (NoSuchAuthorityCodeException ex) {
                                getLogger().log(Level.SEVERE, null, ex);
                            } catch (FactoryException ex) {
                                getLogger().log(Level.SEVERE, null, ex);
                            }
                        }else{
                            type = String.class;
                        }
                    }
                }
            } else {
                fieldName = NamesExt.create(getDefaultNamespace(), field);
                type = String.class;
            }

            atb.setName(fieldName);
            atb.setValueClass(type);
        }

        return ftb.build();
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
                try {
                    sb.append(IdentifiedObjects.lookupIdentifier(FeatureExt.getCRS(desc), true));
                } catch (FactoryException ex) {
                    throw new DataStoreException(ex);
                }
            }else{
                //unsuported, output it as text
                sb.append("String");
            }

            sb.append(')');
        }
        return sb.toString();
    }

    private void writeType(final FeatureType type) throws DataStoreException {
        defaultNamespace = NamesExt.getNamespace(type.getName());
        Parameters.getOrCreate(CSVFeatureStoreFactory.NAMESPACE, parameters).setValue(defaultNamespace);
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
    public long getCount(final Query query) throws DataStoreException {
        if(QueryUtilities.queryAll(query)) {
            //Neither filter nor start index, just count number of lines to avoid reading features.
            fileLock.readLock().lock();
            try (final BufferedReader reader = Files.newBufferedReader(file, UTF8_ENCODING)) {
                long cnt = -1; //avoid counting the header line
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty() && !line.startsWith(COMMENT_STRING)) {
                        //avoid potential empty or commented lines
                        cnt++;
                    }
                }
                return cnt;
            } catch (IOException ex) {
                throw new DataStoreException(ex);
            } finally {
                fileLock.readLock().unlock();
            }
        }

        return super.getCount(query);
    }

    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        checkExist();
        if(featureType != null){
            return Collections.singleton(featureType.getName());
        }else{
            return Collections.emptySet();
        }
    }

    @Override
    public void createFeatureType(final FeatureType featureType) throws DataStoreException {
        checkExist();
        if (this.featureType != null) {
            throw new DataStoreException("Can only have one feature type in CSV dataStore.");
        }

        if(!featureType.isSimple()){
            throw new DataStoreException("Feature type must be simple.");
        }

        try {
            fileLock.writeLock().lock();
            writeType(featureType);
        } finally {
            fileLock.writeLock().unlock();
        }

        checkExist();
        fireSchemaAdded(featureType.getName(), featureType);
    }

    @Override
    public void deleteFeatureType(final String typeName) throws DataStoreException {
        typeCheck(typeName); //raise error is type doesnt exist

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
        fireSchemaDeleted(oldSchema.getName(), oldSchema);
    }

    @Override
    public void updateFeatureType(final FeatureType featureType) throws DataStoreException {
        typeCheck(featureType.getName().toString()); //raise error if type doesn't exist
        deleteFeatureType(featureType.getName().toString());
        createFeatureType(featureType);
    }

    @Override
    public FeatureType getFeatureType(final String typeName) throws DataStoreException {
        typeCheck(typeName); //raise error is type doesnt exist
        return featureType;
    }

    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        typeCheck(query.getTypeName()); //raise error is type doesnt exist

        final Hints hints = query.getHints();
        final Boolean detached = (hints == null) ? null : (Boolean) hints.get(HintsPending.FEATURE_DETACHED);

        final FeatureReader fr = new CSVFeatureReader(this,featureType,detached != null && !detached,fileLock);
        return handleRemaining(fr, query);
    }

    @Override
    public FeatureWriter getFeatureWriter(Query query) throws DataStoreException {
        typeCheck(query.getTypeName()); //raise error is type doesnt exist
        final FeatureWriter fw = new CSVFeatureWriter(this,featureType,fileLock);
        return handleRemaining(fw, query.getFilter());
    }

    @Override
    public boolean isWritable(String typeName) throws DataStoreException {
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////
    // FALLTHROUGHT OR NOT IMPLEMENTED /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return new DefaultQueryCapabilities(false, false);
    }

    @Override
    public List<FeatureId> addFeatures(final String groupName, final Collection<? extends Feature> newFeatures,
            final Hints hints) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures,hints);
    }

    @Override
    public void updateFeatures(final String groupName, final Filter filter, final Map<String, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    @Override
    public void removeFeatures(final String groupName, final Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    @Override
    public Path[] getDataFiles() throws DataStoreException {
        return new Path[] { this.file };
    }

    void fireDataChangeEvents(Set<Identifier> addedIds,Set<Identifier> updatedIds,Set<Identifier> deletedIds) {
        if (!addedIds.isEmpty()) {
            final FeatureStoreContentEvent event = new FeatureStoreContentEvent(this, FeatureStoreContentEvent.Type.ADD, featureType.getName(), FF.id(addedIds));
            forwardContentEvent(event);
        }

        if (!updatedIds.isEmpty()) {
            final FeatureStoreContentEvent event = new FeatureStoreContentEvent(this, FeatureStoreContentEvent.Type.UPDATE, featureType.getName(), FF.id(updatedIds));
            forwardContentEvent(event);
        }

        if (!deletedIds.isEmpty()) {
            final FeatureStoreContentEvent event = new FeatureStoreContentEvent(this, FeatureStoreContentEvent.Type.DELETE, featureType.getName(), FF.id(deletedIds));
            forwardContentEvent(event);
        }
    }

    @Override
    public void refreshMetaModel() {
        featureType=null;
    }

}
