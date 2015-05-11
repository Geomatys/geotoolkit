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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.Feature;
import org.geotoolkit.feature.FeatureFactory;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.feature.type.ComplexType;
import org.geotoolkit.feature.type.FeatureType;
import org.geotoolkit.feature.type.Name;
import org.geotoolkit.feature.type.PropertyDescriptor;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureWriter;
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
    private FeatureType featureType;
    private final Map<String,String> schemaLocations = new HashMap<>();
    private String gmlVersion = "3.2.1";

    public GMLSparseFeatureStore(final File f) throws MalformedURLException, DataStoreException{
        this(f,null,null);
    }

    public GMLSparseFeatureStore(final File f,String xsd, String typeName) throws MalformedURLException, DataStoreException{
        this(toParameters(f,xsd,typeName));
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

    }

    private static ParameterValueGroup toParameters(final File f,String xsd, String typeName) throws MalformedURLException{
        final ParameterValueGroup params = GMLFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(GMLFeatureStoreFactory.URLP, params).setValue(f.toURL());
        Parameters.getOrCreate(GMLFeatureStoreFactory.SPARSE, params).setValue(true);
        if(xsd!=null) Parameters.getOrCreate(GMLFeatureStoreFactory.XSD, params).setValue(xsd);
        if(typeName!=null) Parameters.getOrCreate(GMLFeatureStoreFactory.XSD_TYPE_NAME, params).setValue(typeName);
        return params;
    }

    @Override
    public FeatureStoreFactory getFactory() {
        return FeatureStoreFinder.getFactoryById(GMLFeatureStoreFactory.NAME);
    }

    @Override
    public synchronized Set<Name> getNames() throws DataStoreException {
        if(featureType==null){
            final String xsd = (String) parameters.parameter(GMLFeatureStoreFactory.XSD.getName().toString()).getValue();
            final String xsdTypeName = (String) parameters.parameter(GMLFeatureStoreFactory.XSD_TYPE_NAME.getName().toString()).getValue();

            if(xsd!=null){
                //read types from XSD file
                final JAXBFeatureTypeReader reader = new JAXBFeatureTypeReader();
                try{
                    for(FeatureType ft : reader.read(new URL(xsd))){
                        if(ft.getName().getLocalPart().equalsIgnoreCase(xsdTypeName)){
                            featureType = ft;
                        }
                    }
                    if(featureType==null){
                        throw new DataStoreException("Type for name "+xsdTypeName+" not found in xsd.");
                    }

                    schemaLocations.put(reader.getTargetNamespace(),xsd);
                }catch(MalformedURLException | JAXBException ex){
                    throw new DataStoreException(ex.getMessage(),ex);
                }
            }else{
                //read type in the first gml file
                final JAXPStreamFeatureReader reader = new JAXPStreamFeatureReader();
                reader.setReadEmbeddedFeatureType(true);
                try {
                    FeatureReader ite = reader.readAsStream(file.listFiles(new GMLFolderFeatureStoreFactory.ExtentionFileNameFilter(".gml"))[0]);
                    featureType = ite.getFeatureType();
                    schemaLocations.putAll(reader.getSchemaLocations());
                } catch (IOException | XMLStreamException ex) {
                    throw new DataStoreException(ex.getMessage(),ex);
                } finally{
                    reader.dispose();
                }
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
    public boolean isWritable(Name typeName) throws DataStoreException {
        typeCheck(typeName);
        return true;
    }

    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        typeCheck(query.getTypeName());
        final ReadIterator ite = new ReadIterator(featureType, file);
        return handleRemaining(ite, query);
    }

    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter, Hints hints) throws DataStoreException {
        typeCheck(typeName);
        final WriterIterator ite = new WriterIterator(featureType, file);
        return handleRemaining(ite, filter);
    }

    @Override
    public List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures, hints);
    }

    @Override
    public void updateFeatures(Name groupName, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    @Override
    public void removeFeatures(Name groupName, Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    // TYPE CREATE/UPDATE NOT SUPPORTED ////////////////////////////////////////

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

    private class ReadIterator implements FeatureReader{

        protected final FeatureType type;
        protected final JAXPStreamFeatureReader xmlReader;
        protected FeatureReader featureReader;
        protected Feature currentFeature = null;
        protected File currentFile = null;
        protected Feature nextFeature = null;
        protected final File[] files;
        protected int index=-1;

        public ReadIterator(FeatureType type, File folder) {
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
            currentFeature = nextFeature;
            if(currentFeature==null){
                currentFile = null;
                throw new NoSuchElementException("No more features");
            }
            currentFile = files[index];
            nextFeature = null;
            return currentFeature;
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
            return nextFeature != null;
        }

        private void findNext() throws IOException, XMLStreamException{
            if(nextFeature!=null) return;

            while(nextFeature==null){
                if(featureReader==null){
                    //get the next file
                    index++;
                    if(index>=files.length){
                        return;
                    }
                    xmlReader.reset();
                    featureReader = xmlReader.readAsStream(files[index]);
                }

                if(featureReader.hasNext()){
                    nextFeature = featureReader.next();
                }else{
                    featureReader = null;
                }
            }
        }

        @Override
        public void close() {
            xmlReader.dispose();
        }

    }

    private class WriterIterator extends ReadIterator implements FeatureWriter{

        public WriterIterator(FeatureType type, File folder) {
            super(type, folder);
        }

        @Override
        public Feature next() throws FeatureStoreRuntimeException {
            if(hasNext()){
                return super.next();
            }else{
                //append mode
                currentFeature = FeatureFactory.LENIENT.createFeature(new ArrayList(), type, FeatureUtilities.createDefaultFeatureId());
                currentFile = null;
                return currentFeature;
            }
        }

        @Override
        public void remove() throws FeatureStoreRuntimeException {
            if(currentFile==null){
                throw new IllegalStateException("No current feature to remove.");
            }
            currentFile.delete();
        }

        @Override
        public void write() throws FeatureStoreRuntimeException {
            if(currentFeature==null){
                throw new IllegalStateException("No current feature to write.");
            }

            if(currentFile==null){
                //append mode
                currentFile = new File(file,currentFeature.getIdentifier().getID()+".gml");
            }

            //write feature
            final JAXPStreamFeatureWriter writer = new JAXPStreamFeatureWriter(gmlVersion,"2.0.0",schemaLocations);
            try{
                writer.write(currentFeature, currentFile);
            }catch(IOException | XMLStreamException | DataStoreException ex){
                throw new FeatureStoreRuntimeException(ex.getMessage(),ex);
            } finally{
                try {
                    writer.dispose();
                } catch (IOException | XMLStreamException ex) {
                    throw new FeatureStoreRuntimeException(ex.getMessage(),ex);
                }
            }
        }
    }

}
