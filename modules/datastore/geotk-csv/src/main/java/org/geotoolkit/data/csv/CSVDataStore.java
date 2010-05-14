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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.data.query.QueryUtilities;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.internal.io.IOUtilities;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.util.Converters;
import org.geotoolkit.util.StringUtilities;

import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * CSV DataStore, holds a single feature type which name match the file name.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CSVDataStore extends AbstractDataStore{

    private final ReadWriteLock RWLock = new ReentrantReadWriteLock();
    private final ReadWriteLock TempLock = new ReentrantReadWriteLock();

    private final File file;
    private String namespace;
    private String name;
    private final char separator;

    private SimpleFeatureType featureType;

    public CSVDataStore(File f, String namespace, String name, char separator){
        this.file = f;
        this.name = name;
        this.namespace = namespace;
        this.separator = separator;
    }

    private synchronized void checkExist() throws DataStoreException{
        if(featureType != null) return;

        try{
            RWLock.readLock().lock();
            if(file.exists()){
                featureType = readType();
            }
        }finally{
            RWLock.readLock().unlock();
        }
    }

    private File createWriteFile() throws MalformedURLException{
        return (File) IOUtilities.changeExtension(file, "wcsv");
    }

    private SimpleFeatureType readType() throws DataStoreException{
        final Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException ex) {
            throw new DataStoreException(ex);
        }

        try {
          //first use a Scanner to get each line
          if(scanner.hasNextLine()){
              final String line = scanner.nextLine();
              final String[] fields = line.split(""+separator);
              final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
              ftb.setName(namespace, name);
              for(String field : fields){
                  final int dep = field.indexOf('(');
                  final int fin = field.lastIndexOf(')');

                  final String fieldName;
                  Class type = String.class;
                  CoordinateReferenceSystem crs = null;
                  if(dep >0 && fin>dep){
                      fieldName = field.substring(0, dep);
                      //there is a defined type
                      final String name = field.substring(dep + 1, fin).toLowerCase();
                      if ("integer".equalsIgnoreCase(name)) {
                          type = Integer.class;
                      } else if ("double".equalsIgnoreCase(name)) {
                          type = Double.class;
                      } else if ("string".equalsIgnoreCase(name)) {
                          type = String.class;
                      }else{
                          try {
                              //check if it's a geometry type
                              crs = CRS.decode(name);
                              type = Geometry.class;
                          } catch (NoSuchAuthorityCodeException ex) {
                              java.util.logging.Logger.getLogger(CSVDataStore.class.getName()).log(Level.SEVERE, null, ex);
                          } catch (FactoryException ex) {
                              java.util.logging.Logger.getLogger(CSVDataStore.class.getName()).log(Level.SEVERE, null, ex);
                          }
                      }
                  }
                  else{
                    fieldName = field;
                    type = String.class;
                  }

                  if(crs == null){
                      //normal field
                      ftb.add(fieldName, type);
                  }else{
                      //geometry field
                      ftb.add(fieldName, type,crs);
                  }
              }
              return ftb.buildSimpleFeatureType();

          }else{
              return null;
          }
        }finally {
          scanner.close();
        }
    }

    private String createHeader(SimpleFeatureType type) throws DataStoreException{
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
            if(clazz.equals(Integer.class)){
                sb.append("Integer");
            }else if(clazz.equals(Double.class)){
                sb.append("Double");
            }else if(clazz.equals(String.class)){
                sb.append("String");
            }else if(clazz.equals(Geometry.class)){
                GeometryDescriptor gd = (GeometryDescriptor) desc;
                try {
                    sb.append(CRS.lookupIdentifier(gd.getCoordinateReferenceSystem(), true));
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

    private void writeType(SimpleFeatureType type) throws DataStoreException {
        
        Writer output = null;
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            output = new BufferedWriter(new FileWriter(file));
            namespace = type.getName().getNamespaceURI();
            name = type.getName().getLocalPart();
            output.write(createHeader(type));
        } catch (IOException ex) {
            throw new DataStoreException(ex);
        }finally{
            if(output != null){
                try{
                    output.flush();
                    output.close();
                }catch (IOException ex) {
                    throw new DataStoreException(ex);
                }
            }
        }
    }

    @Override
    public long getCount(Query query) throws DataStoreException {
        if(QueryUtilities.queryAll(query)) {
            //ni filter or start index, just count number of line avoid reading features.
            RWLock.readLock().lock();

            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                long cnt = -1; //avoid counting the header line
                String line;
                while ((line = reader.readLine()) != null) {
                    if(!line.isEmpty()){
                        //avoid potential last empty lines
                        cnt++;
                    }
                }
            } catch (IOException ex) {
                throw new DataStoreException(ex);
            } finally {
                RWLock.readLock().unlock();
                if(reader != null){
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        java.util.logging.Logger.getLogger(CSVDataStore.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
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
    public void createSchema(Name typeName, FeatureType featureType) throws DataStoreException {
        checkExist();
        if(this.featureType != null){
            throw new DataStoreException("Can only have one feature type in CSV dataStore.");
        }

        if(!(featureType instanceof SimpleFeatureType)){
            throw new DataStoreException("Feature type must be simple.");
        }

        final SimpleFeatureType sft = (SimpleFeatureType) featureType;

        try{
            RWLock.writeLock().lock();
            writeType(sft);
        }finally{
            RWLock.writeLock().unlock();
        }

        checkExist();
        fireSchemaAdded(typeName, featureType);
    }

    @Override
    public void deleteSchema(Name typeName) throws DataStoreException {
        typeCheck(typeName); //raise error is type doesnt exist

        final SimpleFeatureType oldSchema = featureType;

        try{
            RWLock.writeLock().lock();
            if(file.exists()){
                file.delete();
                featureType = null;
            }
        }finally{
            RWLock.writeLock().unlock();
        }
        fireSchemaDeleted(typeName, oldSchema);
    }

    @Override
    public void updateSchema(Name typeName, FeatureType featureType) throws DataStoreException {
        typeCheck(typeName); //raise error is type doesnt exist
        deleteSchema(typeName);
        createSchema(typeName, featureType);
    }

    @Override
    public FeatureType getFeatureType(Name typeName) throws DataStoreException {
        typeCheck(typeName); //raise error is type doesnt exist
        return featureType;
    }

    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        typeCheck(query.getTypeName()); //raise error is type doesnt exist
        final FeatureReader fr = new CSVFeatureReader();
        return handleRemaining(fr, query);
    }

    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter) throws DataStoreException {
        typeCheck(typeName); //raise error is type doesnt exist
        final FeatureWriter fw = new CSVFeatureWriter();
        return handleRemaining(fw, filter);
    }

    ////////////////////////////////////////////////////////////////////////////
    // FALLTHROUGHT OR NOT IMPLEMENTED /////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    @Override
    public QueryCapabilities getQueryCapabilities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures);
    }

    @Override
    public void updateFeatures(Name groupName, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    @Override
    public void removeFeatures(Name groupName, Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    private class CSVFeatureReader implements FeatureReader<FeatureType, Feature>{

        private final WKTReader reader = new WKTReader();
        private final Scanner scanner;
        protected final SimpleFeatureBuilder sfb;
        protected SimpleFeature current = null;
        protected int inc = 0;

        private CSVFeatureReader() throws DataStoreException{
            RWLock.readLock().lock();
            sfb = new SimpleFeatureBuilder(featureType);

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
        public SimpleFeature next() throws DataStoreRuntimeException {
            read();
            final SimpleFeature ob = current;
            current = null;
            if(ob == null){
                throw new DataStoreRuntimeException("No more records.");
            }
            return ob;
        }

        @Override
        public boolean hasNext() throws DataStoreRuntimeException {
            read();
            return current != null;
        }

        private void read() throws DataStoreRuntimeException{
            if(current != null) return;
            if(scanner.hasNextLine()){
                final String line = scanner.nextLine();
                final List<String> fields = StringUtilities.toStringList(line, separator);
                sfb.reset();
                final List<AttributeDescriptor> atts = featureType.getAttributeDescriptors();
                for(int i=0,n=atts.size() ; i<n; i++){
                    final AttributeDescriptor att = atts.get(i);
                    final Object value;
                    if(att instanceof GeometryDescriptor){
                        if(fields.get(i).trim().isEmpty()){
                            value = null;
                        }else{
                            try {
                                value = reader.read(fields.get(i));
                            } catch (ParseException ex) {
                                throw new DataStoreRuntimeException(ex);
                            }
                        }
                    }else{
                        value = Converters.convert(fields.get(i), att.getType().getBinding());
                    }
                    sfb.set(att.getName(), value);
                }
                current = sfb.buildFeature(Integer.toString(inc++));
            }
        }

        @Override
        public void close() {
            RWLock.readLock().unlock();
            scanner.close();
        }

        @Override
        public void remove() {
            throw new DataStoreRuntimeException("Not supported on reader.");
        }

    }

    private class CSVFeatureWriter extends CSVFeatureReader implements FeatureWriter<FeatureType, Feature>{

        private final WKTWriter wktWriter = new WKTWriter(2);
        private final Writer writer;
        private final File writeFile;
        private SimpleFeature edited = null;
        private SimpleFeature lastWritten = null;

        private CSVFeatureWriter() throws DataStoreException{
            super();
            TempLock.writeLock().lock();

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
            }
        }

        @Override
        public SimpleFeature next() throws DataStoreRuntimeException {
            try{
                write();
                edited = super.next();
            }catch(DataStoreRuntimeException ex){
                //we reach append mode
                sfb.reset();
                edited = sfb.buildFeature(Integer.toString(inc++));
                for(Property prop : edited.getProperties()){
                    try{prop.setValue(FeatureUtilities.defaultValue(prop.getType().getBinding()));
                    }catch(IllegalArgumentException e){ /*ignore this error*/}
                }

            }
            return edited;
        }

        @Override
        public void write() throws DataStoreRuntimeException {
            if(edited == null || lastWritten == edited) return;
            lastWritten = edited;

            final StringBuilder sb = new StringBuilder();
            final List<AttributeDescriptor> atts = featureType.getAttributeDescriptors();
            for(int i=0,n=atts.size() ; i<n; i++){
                final AttributeDescriptor att = atts.get(i);
                final Object value = edited.getAttribute(att.getName());

                final String str;
                if(value == null){
                    str = "";
                }else if(att instanceof GeometryDescriptor){
                    if(value != null){
                        str = wktWriter.write((Geometry) value);
                    }else{
                        str = "";
                    }
                }else{
                    str = Converters.convert(value, String.class);
                }
                sb.append(str).append(separator);
            }
            sb.setLength(sb.length()-1); //remove the last separator
            sb.append('\n');

            try {
                writer.write(sb.toString());
                writer.flush();
            } catch (IOException ex) {
                throw new DataStoreRuntimeException(ex);
            }
        }

        @Override
        public void close() {

            try {
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                throw new DataStoreRuntimeException(ex);
            }

            //close read iterator
            super.close();
            
            //flip files
            RWLock.writeLock().lock();
            file.delete();
            writeFile.renameTo(file);
            RWLock.writeLock().unlock();

            TempLock.writeLock().unlock();
        }

    }

}
