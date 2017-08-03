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
import java.net.URI;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreRuntimeException;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.nio.PosixDirectoryFilter;
import org.opengis.util.GenericName;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureWriter;
import org.geotoolkit.storage.DataFileStore;
import org.geotoolkit.storage.DataStores;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.ParameterValueGroup;

/**
 * GML feature store.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GMLSparseFeatureStore extends AbstractFeatureStore implements DataFileStore {

    private final Path file;
    private FeatureType featureType;
    private final Map<String,String> schemaLocations = new HashMap<>();
    private String gmlVersion = "3.2.1";

    //all types
    private final Map<GenericName, Object> cache = new HashMap<>();
    private Boolean longitudeFirst;

    public GMLSparseFeatureStore(final File f) throws MalformedURLException, DataStoreException{
        this(f.toPath(),null,null);
    }

    @Deprecated
    public GMLSparseFeatureStore(final File f,String xsd, String typeName) throws MalformedURLException, DataStoreException{
        this(toParameters(f.toPath(),xsd,typeName));
    }

    public GMLSparseFeatureStore(final Path f,String xsd, String typeName) throws MalformedURLException, DataStoreException{
        this(toParameters(f,xsd,typeName));
    }

    public GMLSparseFeatureStore(final ParameterValueGroup params) throws DataStoreException {
        super(params);

        final URI uri = (URI) params.parameter(GMLFeatureStoreFactory.PATH.getName().toString()).getValue();
        this.file = Paths.get(uri);
        this.longitudeFirst = (Boolean) params.parameter(GMLFeatureStoreFactory.LONGITUDE_FIRST.getName().toString()).getValue();
    }

    private static ParameterValueGroup toParameters(final Path f,String xsd, String typeName) throws MalformedURLException{
        final Parameters params = Parameters.castOrWrap(GMLFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(GMLFeatureStoreFactory.PATH).setValue(f.toUri());
        params.getOrCreate(GMLFeatureStoreFactory.SPARSE).setValue(true);
        if(xsd!=null) params.getOrCreate(GMLFeatureStoreFactory.XSD).setValue(xsd);
        if(typeName!=null) params.getOrCreate(GMLFeatureStoreFactory.XSD_TYPE_NAME).setValue(typeName);
        return params;
    }

    @Override
    public FeatureStoreFactory getFactory() {
        return (FeatureStoreFactory) DataStores.getFactoryById(GMLFeatureStoreFactory.NAME);
    }

    @Override
    public synchronized Set<GenericName> getNames() throws DataStoreException {
        if(featureType==null){
            final String xsd = (String) parameters.parameter(GMLFeatureStoreFactory.XSD.getName().toString()).getValue();
            final String xsdTypeName = (String) parameters.parameter(GMLFeatureStoreFactory.XSD_TYPE_NAME.getName().toString()).getValue();

            if(xsd!=null){
                //read types from XSD file
                final JAXBFeatureTypeReader reader = new JAXBFeatureTypeReader();
                try{
                    for(FeatureType ft : reader.read(new URL(xsd))){
                        if(ft.getName().tip().toString().equalsIgnoreCase(xsdTypeName)){
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
                reader.getProperties().put(JAXPStreamFeatureReader.LONGITUDE_FIRST, longitudeFirst);
                reader.setReadEmbeddedFeatureType(true);
                try {
                    if (Files.isDirectory(file)) {
                        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(file, new PosixDirectoryFilter("*.gml", true))) {
                            final Iterator<Path> gmlPaths = directoryStream.iterator();
                            // get first gml file only
                            if (gmlPaths.hasNext()) {
                                final Path gmlPath = gmlPaths.next();
                                FeatureReader ite = reader.readAsStream(gmlPath);
                                featureType = ite.getFeatureType();
                                schemaLocations.putAll(reader.getSchemaLocations());
                            }
                        }
                    }
                } catch (IOException | XMLStreamException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                } finally {
                    reader.dispose();
                }
            }
        }
        return Collections.singleton(featureType.getName());
    }

    @Override
    public FeatureType getFeatureType(String typeName) throws DataStoreException {
        typeCheck(typeName);
        return featureType;
    }

    @Override
    public List<FeatureType> getFeatureTypeHierarchy(String typeName) throws DataStoreException {
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
    public Path[] getDataFiles() throws DataStoreException {
        return new Path[]{file};
    }

    @Override
    public boolean isWritable(String typeName) throws DataStoreException {
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
    public FeatureWriter getFeatureWriter(Query query) throws DataStoreException {
        typeCheck(query.getTypeName());
        final WriterIterator ite = new WriterIterator(featureType, file);
        return handleRemaining(ite, query.getFilter());
    }

    @Override
    public List<FeatureId> addFeatures(String groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        return handleAddWithFeatureWriter(groupName, newFeatures, hints);
    }

    @Override
    public void updateFeatures(String groupName, Filter filter, Map<String, ? extends Object> values) throws DataStoreException {
        handleUpdateWithFeatureWriter(groupName, filter, values);
    }

    @Override
    public void removeFeatures(String groupName, Filter filter) throws DataStoreException {
        handleRemoveWithFeatureWriter(groupName, filter);
    }

    // TYPE CREATE/UPDATE NOT SUPPORTED ////////////////////////////////////////

    @Override
    public void createFeatureType(FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Writing not supported");
    }

    @Override
    public void updateFeatureType(FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Writing not supported");
    }

    @Override
    public void deleteFeatureType(String typeName) throws DataStoreException {
        throw new DataStoreException("Writing not supported");
    }

    private class ReadIterator implements FeatureReader{

        protected final FeatureType type;
        protected final JAXPStreamFeatureReader xmlReader;
        protected FeatureReader featureReader;
        protected Feature currentFeature = null;
        protected Path currentFile = null;
        protected Feature nextFeature = null;
        protected final List<Path> files = new LinkedList<>();
        protected int index=-1;
        /**
         * @deprecated
         */
        @Deprecated
        public ReadIterator(FeatureType type, File folder) throws DataStoreException {
            this(type, folder.toPath());
        }

        public ReadIterator(FeatureType type, Path folder) throws DataStoreException {
            this.type = type;
            this.xmlReader = new JAXPStreamFeatureReader(type);
            this.xmlReader.getProperties().put(JAXPStreamFeatureReader.LONGITUDE_FIRST, longitudeFirst);
            try {
                this.files.addAll(IOUtilities.listChildren(folder, "*.gml"));
            } catch (IOException e) {
                throw new DataStoreException(e.getLocalizedMessage(), e);
            }
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
            currentFile = files.get(index);
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
                    if(index >= files.size()){
                        return;
                    }
                    xmlReader.reset();
                    featureReader = xmlReader.readAsStream(files.get(index));
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
        /**
         * @deprecated
         */
        @Deprecated
        public WriterIterator(FeatureType type, File folder) throws DataStoreException {
            super(type, folder.toPath());
        }

        public WriterIterator(FeatureType type, Path folder) throws DataStoreException {
            super(type, folder);
        }

        @Override
        public Feature next() throws FeatureStoreRuntimeException {
            if(hasNext()){
                return super.next();
            }else{
                //append mode
                currentFeature = type.newInstance();
                currentFeature.setPropertyValue(AttributeConvention.IDENTIFIER_PROPERTY.toString(), FeatureExt.createDefaultFeatureId());
                currentFile = null;
                return currentFeature;
            }
        }

        @Override
        public void remove() throws FeatureStoreRuntimeException {
            if(currentFile==null){
                throw new IllegalStateException("No current feature to remove.");
            }
            try {
                Files.delete(currentFile);
            } catch (IOException e) {
                throw new FeatureStoreRuntimeException("Unable to delete GML file "+currentFile.toAbsolutePath().toString(), e);
            }
        }

        @Override
        public void write() throws FeatureStoreRuntimeException {
            if(currentFeature==null){
                throw new IllegalStateException("No current feature to write.");
            }

            if(currentFile==null){
                //append mode
                currentFile = file.resolve(FeatureExt.getId(currentFeature).getID()+".gml");
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
