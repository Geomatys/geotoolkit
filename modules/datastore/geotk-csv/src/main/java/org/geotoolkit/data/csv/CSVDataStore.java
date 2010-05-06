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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.storage.DataStoreException;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
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

    private final File file;
    private final String namespace;
    private final String name;
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
              throw new DataStoreException("File doesnt contain a first line describing the schema.");
          }
        }finally {
          scanner.close();
        }
    }

    private void writeType(SimpleFeatureType type) throws DataStoreException {
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

        Writer output = null;
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            output = new BufferedWriter(new FileWriter(file));
            output.write(sb.toString());
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
    }

    @Override
    public void deleteSchema(Name typeName) throws DataStoreException {
        typeCheck(typeName); //raise error is type doesnt exist

        try{
            RWLock.writeLock().lock();
            if(file.exists()){
                file.delete();
                featureType = null;
            }
        }finally{
            RWLock.writeLock().unlock();
        }
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter) throws DataStoreException {
        throw new UnsupportedOperationException("Not supported yet.");
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

}
