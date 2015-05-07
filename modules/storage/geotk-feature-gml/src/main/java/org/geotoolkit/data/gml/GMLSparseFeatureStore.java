/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.geotoolkit.data.gml;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.memory.GenericWrapFeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.storage.DataFileStore;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.ParameterValueGroup;

/**
 * GML feature store.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GMLSparseFeatureStore extends AbstractFeatureStore implements DataFileStore {

    private final File file;
    private String name;
    private FeatureType featureType;

    //all types
    private final Map<Name, Object> cache = new HashMap<>();

    public GMLSparseFeatureStore(final File f) throws MalformedURLException, DataStoreException{
        this(toParameters(f));
    }

    public GMLSparseFeatureStore(final ParameterValueGroup params) throws DataStoreException {
        super(params);

        final URL url = (URL) params.parameter(GMLFeatureStoreFactory.URLP.getName().toString()).getValue();
        try {
            this.file = new File(url.toURI());
        } catch (URISyntaxException ex) {
            throw new DataStoreException(ex);
        }

        final String path = url.toString();
        final int slash = Math.max(0, path.lastIndexOf('/') + 1);
        int dot = path.indexOf('.', slash);
        if (dot < 0) {
            dot = path.length();
        }
        this.name = path.substring(slash, dot);
    }

    private static ParameterValueGroup toParameters(final File f) throws MalformedURLException{
        final ParameterValueGroup params = GMLFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(GMLFeatureStoreFactory.URLP, params).setValue(f.toURL());
        Parameters.getOrCreate(GMLFeatureStoreFactory.SPARSE, params).setValue(true);
        return params;
    }

    @Override
    public FeatureStoreFactory getFactory() {
        return FeatureStoreFinder.getFactoryById(GMLFeatureStoreFactory.NAME);
    }

    @Override
    public synchronized Set<Name> getNames() throws DataStoreException {
        if(featureType==null){
            final JAXPStreamFeatureReader reader = new JAXPStreamFeatureReader();
            reader.setReadEmbeddedFeatureType(true);
            try {
                FeatureReader ite = reader.readAsStream(file.listFiles(new GMLFolderFeatureStoreFactory.ExtentionFileNameFilter(".gml"))[0]);
                featureType = ite.getFeatureType();
            } catch (IOException | XMLStreamException ex) {
                throw new DataStoreException(ex.getMessage(),ex);
            } finally{
                reader.dispose();
            }
        }
        return Collections.singleton(featureType.getName());
    }

    @Override
    public FeatureType getFeatureType(Name typeName) throws DataStoreException {
        typeCheck(typeName);
        return featureType;
    }

    @Override
    public List<ComplexType> getFeatureTypeHierarchy(Name typeName) throws DataStoreException {
        return super.getFeatureTypeHierarchy(typeName);
    }

    @Override
    public QueryCapabilities getQueryCapabilities() {
        return GMLFeatureStore.CAPABILITIES;
    }

    @Override
    public void refreshMetaModel() {
    }

    @Override
    public File[] getDataFiles() throws DataStoreException {
        return new File[]{file};
    }

    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        typeCheck(query.getTypeName());

        final Iterator ite = new Iterator(featureType, file);
        return handleRemaining(ite, query);
    }

    // WRITING SUPPORT : TODO //////////////////////////////////////////////////

    @Override
    public void createFeatureType(Name typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Writing not supported");
    }

    @Override
    public void updateFeatureType(Name typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Writing not supported");
    }

    @Override
    public void deleteFeatureType(Name typeName) throws DataStoreException {
        throw new DataStoreException("Writing not supported");
    }

    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter, Hints hints) throws DataStoreException {
        throw new DataStoreException("Writing not supported");
    }

    @Override
    public List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        throw new DataStoreException("Writing not supported");
    }

    @Override
    public void updateFeatures(Name groupName, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        throw new DataStoreException("Writing not supported");
    }

    @Override
    public void removeFeatures(Name groupName, Filter filter) throws DataStoreException {
        throw new DataStoreException("Writing not supported");
    }

    private static final class Iterator implements FeatureReader{

        private final FeatureType type;
        private final JAXPStreamFeatureReader xmlReader;
        private FeatureReader reader;
        private Feature next = null;
        private final File[] files;
        private int index=-1;

        public Iterator(FeatureType type, File folder) {
            this.type = type;
            this.xmlReader = new JAXPStreamFeatureReader(type);
            this.files = folder.listFiles(new GMLFolderFeatureStoreFactory.ExtentionFileNameFilter(".gml"));
        }

        @Override
        public FeatureType getFeatureType() {
            return type;
        }

        @Override
        public Feature next() throws FeatureStoreRuntimeException {
            try {
                findNext();
            } catch (IOException | XMLStreamException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
            Feature temp = next;
            if(temp==null) throw new NoSuchElementException("No more features");
            next = null;
            return temp;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("No supported yet");
        }

        @Override
        public boolean hasNext() throws FeatureStoreRuntimeException {
            try {
                findNext();
            } catch (IOException | XMLStreamException ex) {
                throw new FeatureStoreRuntimeException(ex);
            }
            return next != null;
        }

        private void findNext() throws IOException, XMLStreamException{
            if(next!=null) return;

            while(next==null){
                if(reader==null){
                    //get the next file
                    index++;
                    if(index>=files.length){
                        return;
                    }
                    xmlReader.reset();
                    reader = xmlReader.readAsStream(files[index]);
                }

                if(reader.hasNext()){
                    next = reader.next();
                }else{
                    reader = null;
                }
            }
        }

        @Override
        public void close() {
            xmlReader.dispose();
        }

    }

}
