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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.FeatureStreams;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.opengis.util.GenericName;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.geotoolkit.storage.DataFileStore;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.util.collection.CloseableIterator;
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
public class GMLFeatureStore extends AbstractFeatureStore implements DataFileStore {

    static final QueryCapabilities CAPABILITIES = new DefaultQueryCapabilities(false);

    private final Path file;
    private String name;
    private FeatureType featureType;
    private Boolean longitudeFirst;

    //all types
    private final Map<GenericName, Object> cache = new HashMap<>();

    /**
     * @deprecated use {@link #GMLFeatureStore(Path)} or {@link #GMLFeatureStore(ParameterValueGroup)} instead
     */
    @Deprecated
    public GMLFeatureStore(final File f) throws MalformedURLException, DataStoreException{
        this(f.toURI());
    }

    public GMLFeatureStore(final Path f) throws MalformedURLException, DataStoreException{
        this(f.toUri());
    }

    public GMLFeatureStore(final URI uri) throws MalformedURLException, DataStoreException{
        this(toParameters(uri));
    }

    public GMLFeatureStore(final ParameterValueGroup params) throws DataStoreException {
        super(params);

        final URI uri = (URI) params.parameter(GMLFeatureStoreFactory.PATH.getName().toString()).getValue();
        this.file = Paths.get(uri);

        final String path = uri.toString();
        final int slash = Math.max(0, path.lastIndexOf('/') + 1);
        int dot = path.indexOf('.', slash);
        if (dot < 0) {
            dot = path.length();
        }
        this.name = path.substring(slash, dot);
        this.longitudeFirst = (Boolean) params.parameter(GMLFeatureStoreFactory.LONGITUDE_FIRST.getName().toString()).getValue();
    }

    private static ParameterValueGroup toParameters(final URI uri) throws MalformedURLException{
        final Parameters params = Parameters.castOrWrap(GMLFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(GMLFeatureStoreFactory.PATH).setValue(uri);
        return params;
    }

    @Override
    public DataStoreFactory getProvider() {
        return DataStores.getFactoryById(GMLFeatureStoreFactory.NAME);
    }

    @Override
    public synchronized Set<GenericName> getNames() throws DataStoreException {
        if(featureType==null){
            final JAXPStreamFeatureReader reader = new JAXPStreamFeatureReader();
            reader.getProperties().put(JAXPStreamFeatureReader.LONGITUDE_FIRST, longitudeFirst);
            reader.setReadEmbeddedFeatureType(true);
            try {
                FeatureReader ite = reader.readAsStream(file);
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
        return CAPABILITIES;
    }

    @Override
    public void refreshMetaModel() {
    }

    @Override
    public Path[] getDataFiles() throws DataStoreException {
        return new Path[]{file};
    }

    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        typeCheck(query.getTypeName());

        final JAXPStreamFeatureReader reader = new JAXPStreamFeatureReader(featureType);
        reader.getProperties().put(JAXPStreamFeatureReader.LONGITUDE_FIRST, longitudeFirst);
        final CloseableIterator ite;
        try {
            ite = reader.readAsStream(file);
        } catch (IOException | XMLStreamException ex) {
            reader.dispose();
            throw new DataStoreException(ex.getMessage(),ex);
        } finally{
            //do not dispose, the iterator is closeable and will close the reader
            //reader.dispose();
        }

        final FeatureReader freader = FeatureStreams.asReader(ite,featureType);
        return FeatureStreams.subset(freader, query);
    }

    // WRITING SUPPORT : TODO //////////////////////////////////////////////////

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

    @Override
    public FeatureWriter getFeatureWriter(Query query) throws DataStoreException {
        throw new DataStoreException("Writing not supported");
    }

    @Override
    public List<FeatureId> addFeatures(String groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        throw new DataStoreException("Writing not supported");
    }

    @Override
    public void updateFeatures(String groupName, Filter filter, Map<String, ? extends Object> values) throws DataStoreException {
        throw new DataStoreException("Writing not supported");
    }

    @Override
    public void removeFeatures(String groupName, Filter filter) throws DataStoreException {
        throw new DataStoreException("Writing not supported");
    }

}
