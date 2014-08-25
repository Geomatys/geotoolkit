/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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

package org.geotoolkit.data.csv;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.*;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.simple.DefaultSimpleFeature;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.IdentifiedObjects;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.storage.DataFileStore;
import org.geotoolkit.temporal.object.TemporalUtilities;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.util.StringUtilities;

import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.Property;
import org.geotoolkit.feature.simple.SimpleFeature;
import org.geotoolkit.feature.simple.SimpleFeatureType;
import org.geotoolkit.feature.type.AttributeDescriptor;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.GeometryDescriptor;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * CSV DataStore, holds a single feature type which name match the file name.
 *
 * @author Johann Sorel (Geomatys)
 * @author Alexis Manin (Geomatys)
 */
public class CSVFeatureStore extends AbstractFeatureStore implements DataFileStore {

    private static final Logger LOGGER = Logging.getLogger(CSVFeatureStore.class);

    protected final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    static final String BUNDLE_PATH = "org/geotoolkit/csv/bundle";

    public static final String COMMENT_STRING = "#";

    private final ReadWriteLock fileLock = new ReentrantReadWriteLock();

    private final File file;
    private String name;
    private final char separator;

    private SimpleFeatureType featureType;

    protected final Set<Identifier> deletedIds = new HashSet<>();
    protected final Set<Identifier> updatedIds = new HashSet<>();
    protected final Set<Identifier> addedIds   = new HashSet<>();

    public CSVFeatureStore(final File f, final String namespace, final char separator) throws MalformedURLException, DataStoreException{
        this(f,namespace,separator,null);
    }

    /**
     * Constructor forcing feature type, if the CSV does not have any header.
     *
     */
    public CSVFeatureStore(final File f, final String namespace, final char separator, SimpleFeatureType ft) throws MalformedURLException, DataStoreException{
        this(toParameters(f, namespace, separator));
        if(ft!=null){
            this.featureType = ft;
            name = featureType.getName().getLocalPart();
        }
    }

    public CSVFeatureStore(final ParameterValueGroup params) throws DataStoreException {
        super(params);

        final URL url = (URL) params.parameter(CSVFeatureStoreFactory.URLP.getName().toString()).getValue();
        try {
            this.file = new File(url.toURI());
        } catch (URISyntaxException ex) {
            throw new DataStoreException(ex);
        }
        this.separator = (Character) params.parameter(CSVFeatureStoreFactory.SEPARATOR.getName().toString()).getValue();

        final String path = url.toString();
        final int slash = Math.max(0, path.lastIndexOf('/') + 1);
        int dot = path.indexOf('.', slash);
        if (dot < 0) {
            dot = path.length();
        }
        this.name = path.substring(slash, dot);

    }

    private static ParameterValueGroup toParameters(final File f,
            final String namespace, final Character separator) throws MalformedURLException{
        final ParameterValueGroup params = CSVFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(CSVFeatureStoreFactory.URLP, params).setValue(f.toURL());
        Parameters.getOrCreate(CSVFeatureStoreFactory.NAMESPACE, params).setValue(namespace);
        Parameters.getOrCreate(CSVFeatureStoreFactory.SEPARATOR, params).setValue(separator);
        return params;
    }

    @Override
    public FeatureStoreFactory getFactory() {
        return FeatureStoreFinder.getFactoryById(CSVFeatureStoreFactory.NAME);
    }

    private synchronized void checkExist() throws DataStoreException{
        if(featureType == null) featureType = readType();
    }

    private File createWriteFile() throws MalformedURLException{
        return (File) IOUtilities.changeExtension(file, "wcsv");
    }

    private SimpleFeatureType readType() throws DataStoreException {
        final String line;
        fileLock.readLock().lock();
        try (final Scanner scanner = new Scanner(file)) {
            line = getNextLine(scanner);
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.INFO, ex.getLocalizedMessage());
            // File does not exists.
            return null;
        } finally {
            fileLock.readLock().unlock();
        }

        if (line == null) {
            return null;
        }

        final String[] fields = line.split("" + separator);
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName(getDefaultNamespace(), name);
        for (String field : fields) {
            final int dep = field.indexOf('(');
            final int fin = field.lastIndexOf(')');

            final Name fieldName;
            Class type = String.class;
            CoordinateReferenceSystem crs = null;
            // Check non-empty parenthesis
            if (dep > 0 && fin > dep + 1) {
                fieldName = new DefaultName(getDefaultNamespace(), field.substring(0, dep));
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
                    } else if ("double".equalsIgnoreCase(name)) {
                        type = Double.class;
                    } else if ("string".equalsIgnoreCase(name)) {
                        type = String.class;
                    } else if ("date".equalsIgnoreCase(name)) {
                        type = Date.class;
                    } else if ("boolean".equalsIgnoreCase(name)) {
                        type = Boolean.class;
                    } else {
                        try {
                            //check if it's a geometry type
                            crs = CRS.decode(name);
                            type = Geometry.class;
                        } catch (NoSuchAuthorityCodeException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        } catch (FactoryException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } else {
                fieldName = new DefaultName(getDefaultNamespace(), field);
                type = String.class;
            }

            if (crs == null) {
                //normal field
                ftb.add(fieldName, type);
            } else {
                //geometry field
                ftb.add(fieldName, type, crs);
            }
        }
        return ftb.buildSimpleFeatureType();
    }

    private String createHeader(final SimpleFeatureType type) throws DataStoreException{
        final StringBuilder sb = new StringBuilder();
        boolean first = true;
        for(PropertyDescriptor desc : type.getDescriptors()){
            if(first){
                first = false;
            }else{
                sb.append(separator);
            }

            sb.append(desc.getName().getLocalPart());
            sb.append('(');
            final Class clazz = desc.getType().getBinding();
            if(Number.class.isAssignableFrom(clazz)) {
                sb.append(clazz.getSimpleName());
            }else if(clazz.equals(String.class)){
                sb.append("String");
            }else if(clazz.equals(Date.class)){
                sb.append("Date");
            }else if(clazz.equals(Boolean.class)){
                sb.append("boolean");
            }else if(Geometry.class.isAssignableFrom(clazz)){
                GeometryDescriptor gd = (GeometryDescriptor) desc;
                try {
                    sb.append(IdentifiedObjects.lookupIdentifier(gd.getCoordinateReferenceSystem(), true));
                } catch (FactoryException ex) {
                    throw new DataStoreException(ex);
                }
            }else{
                throw new DataStoreException("Unexpected property type :"+ clazz);
            }

            sb.append(')');
        }
        return sb.toString();
    }

    private void writeType(final SimpleFeatureType type) throws DataStoreException {
        defaultNamespace = type.getName().getNamespaceURI();
        Parameters.getOrCreate(CSVFeatureStoreFactory.NAMESPACE, parameters).setValue(defaultNamespace);
        name = type.getName().getLocalPart();

        fileLock.writeLock().lock();
        try (final Writer output = new BufferedWriter(new FileWriter(file))) {

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
            try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
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
    public Set<Name> getNames() throws DataStoreException {
        checkExist();
        if(featureType != null){
            return Collections.singleton(featureType.getName());
        }else{
            return Collections.emptySet();
        }
    }

    @Override
    public void createFeatureType(final Name typeName, final FeatureType featureType) throws DataStoreException {
        checkExist();
        if (this.featureType != null) {
            throw new DataStoreException("Can only have one feature type in CSV dataStore.");
        }

        if (!(featureType instanceof SimpleFeatureType)) {
            throw new DataStoreException("Feature type must be simple.");
        }

        final SimpleFeatureType sft = (SimpleFeatureType) featureType;

        try {
            fileLock.writeLock().lock();
            writeType(sft);
        } finally {
            fileLock.writeLock().unlock();
        }

        checkExist();
        fireSchemaAdded(typeName, featureType);
    }

    @Override
    public void deleteFeatureType(final Name typeName) throws DataStoreException {
        typeCheck(typeName); //raise error is type doesnt exist

        final SimpleFeatureType oldSchema = featureType;

        try {
            fileLock.writeLock().lock();
            if (file.exists()) {
                file.delete();
                featureType = null;
            }
        } finally {
            fileLock.writeLock().unlock();
        }
        fireSchemaDeleted(typeName, oldSchema);
    }

    @Override
    public void updateFeatureType(final Name typeName, final FeatureType featureType) throws DataStoreException {
        typeCheck(typeName); //raise error if type doesn't exist
        deleteFeatureType(typeName);
        createFeatureType(typeName, featureType);
    }

    @Override
    public FeatureType getFeatureType(final Name typeName) throws DataStoreException {
        typeCheck(typeName); //raise error is type doesnt exist
        return featureType;
    }

    @Override
    public FeatureReader getFeatureReader(final Query query) throws DataStoreException {
        typeCheck(query.getTypeName()); //raise error is type doesnt exist

        final Hints hints = query.getHints();
        final Boolean detached = (hints == null) ? null : (Boolean) hints.get(HintsPending.FEATURE_DETACHED);

        final FeatureReader fr = new CSVFeatureReader(detached != null && !detached);
        return handleRemaining(fr, query);
    }

    @Override
    public FeatureWriter getFeatureWriter(final Name typeName, final Filter filter,
            final Hints hints) throws DataStoreException {
        typeCheck(typeName); //raise error is type doesnt exist
        final FeatureWriter fw = new CSVFeatureWriter();
        return handleRemaining(fw, filter);
    }

    @Override
    public boolean isWritable(Name typeName) throws DataStoreException {
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
    public List<FeatureId> addFeatures(final Name groupName, final Collection<? extends Feature> newFeatures,
            final Hints hints) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures,hints);
    }

    @Override
    public void updateFeatures(final Name groupName, final Filter filter, final Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    @Override
    public void removeFeatures(final Name groupName, final Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    @Override
    public File[] getDataFiles() throws DataStoreException {
        return new File[] { this.file };
    }

    private class CSVFeatureReader implements FeatureReader<FeatureType, Feature>{

        private final WKTReader reader = new WKTReader();
        private final Scanner scanner;
        protected final SimpleFeatureBuilder sfb;
        protected final DefaultSimpleFeature reuse;
        protected SimpleFeature current = null;
        protected int inc = 0;

        private CSVFeatureReader(final boolean reuseFeature) throws DataStoreException{
            fileLock.readLock().lock();
            sfb = new SimpleFeatureBuilder(featureType);
            if(reuseFeature){
                reuse = new DefaultSimpleFeature(featureType, null, new Object[featureType.getAttributeCount()], false);
            }else{
                reuse = null;
            }

            try {
                scanner = new Scanner(file);
                //skip the type line
                scanner.nextLine();
            } catch (FileNotFoundException ex) {
                throw new DataStoreException(ex);
            }
        }

        @Override
        public FeatureType getFeatureType() {
            return featureType;
        }

        @Override
        public SimpleFeature next() throws FeatureStoreRuntimeException {
            read();
            final SimpleFeature ob = current;
            current = null;
            if(ob == null){
                throw new FeatureStoreRuntimeException("No more records.");
            }
            return ob;
        }

        @Override
        public boolean hasNext() throws FeatureStoreRuntimeException {
            read();
            return current != null;
        }

        private void read() throws FeatureStoreRuntimeException {
            if (current != null) return;

            final String line = getNextLine(scanner);
            if (line != null) {
                final List<String> fields = StringUtilities.toStringList(line, separator);

                if (reuse == null) {
                    sfb.reset();
                }

                final List<AttributeDescriptor> atts = featureType.getAttributeDescriptors();
                final int fieldSize = fields.size();
                for (int i = 0, n = atts.size(); i < n; i++) {
                    final AttributeDescriptor att = atts.get(i);
                    final Object value;
                    if (i >= fieldSize) {
                        value = null;
                    } else if (att instanceof GeometryDescriptor) {
                        if (fields.get(i).trim().isEmpty()) {
                            value = null;
                        } else {
                            try {
                                value = reader.read(fields.get(i));
                            } catch (ParseException ex) {
                                throw new FeatureStoreRuntimeException(ex);
                            }
                        }
                    } else {
                        value = ObjectConverters.convert(fields.get(i), att.getType().getBinding());
                    }

                    if (reuse == null) {
                        sfb.set(att.getName(), value);
                    } else {
                        reuse.setAttribute(att.getName(), value);
                    }
                }

                if (reuse == null) {
                    current = sfb.buildFeature(Integer.toString(inc++));
                } else {
                    reuse.setId(Integer.toString(inc++));
                    current = reuse;
                }
            }
        }

        @Override
        public void close() {
            fileLock.readLock().unlock();
            scanner.close();
        }

        @Override
        public void remove() {
            throw new FeatureStoreRuntimeException("Not supported on reader.");
        }

    }

    private class CSVFeatureWriter extends CSVFeatureReader implements FeatureWriter<FeatureType, Feature> {

        private final WKTWriter wktWriter = new WKTWriter(2);
        private final Writer writer;
        private final File writeFile;
        private SimpleFeature edited = null;
        private SimpleFeature lastWritten = null;
        private boolean appendMode = false;

        private final ReadWriteLock tempLock = new ReentrantReadWriteLock();

        private CSVFeatureWriter() throws DataStoreException{
            super(false);

            tempLock.writeLock().lock();
            try{
                writeFile = createWriteFile();
                if (!writeFile.exists()) {
                    writeFile.createNewFile();
                }
                writer = new BufferedWriter(new FileWriter(writeFile));
                final String firstLine = createHeader(featureType);
                writer.write(firstLine);
                writer.write('\n');
                writer.flush();
            }catch(IOException ex){
                throw new DataStoreException(ex);
            } finally {
                tempLock.writeLock().unlock();
            }
        }

        @Override
        public SimpleFeature next() throws FeatureStoreRuntimeException {
            try {
                write();
                edited = super.next();
                appendMode = false;
            } catch (FeatureStoreRuntimeException ex) {
                //we reach append mode
                appendMode = true;
                sfb.reset();
                edited = sfb.buildFeature(Integer.toString(inc++));
                for (Property prop : edited.getProperties()) {
                    try {
                        prop.setValue(FeatureUtilities.defaultValue(prop.getType().getBinding()));
                    } catch (IllegalArgumentException e) { /*ignore this error*/}
                }

            }
            return edited;
        }

        @Override
        public void remove() {
            if (edited == null) {
                throw new FeatureStoreRuntimeException("No feature selected.");
            }

            deletedIds.add(edited.getIdentifier());
            // mark the current feature as null, this will result in it not
            // being rewritten to the stream
            edited = null;
        }

        @Override
        public void write() throws FeatureStoreRuntimeException {
            if(edited == null || lastWritten == edited) return;
            lastWritten = edited;

            final List<AttributeDescriptor> atts = featureType.getAttributeDescriptors();
            tempLock.writeLock().lock();
            try {
                for (int i=0,n=atts.size() ; i<n; i++) {
                    final AttributeDescriptor att = atts.get(i);
                    final Object value = edited.getAttribute(att.getName());

                    final String str;
                    if(value == null){
                        str = "";
                    }else if(att instanceof GeometryDescriptor){
                        str = wktWriter.write((Geometry) value);
                    }else{
                        if(value instanceof Date){
                            str = TemporalUtilities.toISO8601((Date)value);
                        }else if(value instanceof Boolean){
                            str = value.toString();
                        }else{
                            str = ObjectConverters.convert(value, String.class);
                        }
                    }
                    writer.append(str);
                    if (i != n-1) {
                        writer.append(separator);
                    }
                }
                writer.append('\n');
                writer.flush();

                if (appendMode) {
                    addedIds.add(edited.getIdentifier());
                } else {
                    updatedIds.add(edited.getIdentifier());
                }
            } catch (IOException ex) {
                throw new FeatureStoreRuntimeException(ex);
            } finally {
                tempLock.writeLock().unlock();
            }
        }

        @Override
        public void close() {
            try {
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }

            //close read iterator
            super.close();

            //flip files
            fileLock.writeLock().lock();
            tempLock.writeLock().lock();
            try {
                file.delete();
                writeFile.renameTo(file);
            } finally {
                fileLock.writeLock().unlock();
                tempLock.writeLock().unlock();
            }
            // Fire content change events only if we succeed replacing original file.
            fireDataChangeEvents();
        }
    }

    private void fireDataChangeEvents() {
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

    /**
     * Read lines from input {@link java.util.Scanner} until it finds a non-commented line, then send it back.
     * @param source The scanner to read lines from.
     * @return The first non-commented line found, or null if scanner is empty or contains only comments.
     */
    private static String getNextLine(final Scanner source) {
        String line;
        while (source.hasNextLine()) {
            line = source.nextLine();
            if (line.startsWith(COMMENT_STRING)) {
                continue;
            } else {
                final int commentIndex = line.indexOf(COMMENT_STRING);
                if (commentIndex < 0) {
                    return line;
                } else {
                    return line.substring(0, commentIndex);
                }
            }
        }
        return null;
    }

}
