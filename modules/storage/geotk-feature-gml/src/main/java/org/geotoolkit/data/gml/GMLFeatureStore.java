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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import org.apache.sis.internal.storage.ResourceOnFileSystem;
import org.apache.sis.parameter.Parameters;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.Query;
import org.apache.sis.storage.UnsupportedQueryException;
import org.geotoolkit.data.AbstractFeatureStore;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureStreams;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.feature.xml.jaxb.JAXBFeatureTypeReader;
import org.geotoolkit.feature.xml.jaxp.JAXPStreamFeatureReader;
import org.geotoolkit.storage.DataStoreFactory;
import org.geotoolkit.storage.DataStores;
import org.geotoolkit.util.collection.CloseableIterator;
import org.opengis.feature.FeatureType;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 * GML feature store.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GMLFeatureStore extends AbstractFeatureStore implements ResourceOnFileSystem {

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
    public GenericName getIdentifier() {
        return null;
    }

    @Override
    public DataStoreFactory getProvider() {
        return (DataStoreFactory) DataStores.getProviderById(GMLFeatureStoreFactory.NAME);
    }

    @Override
    public synchronized Set<GenericName> getNames() throws DataStoreException {
        if (featureType == null) {
            final String xsd = (String) parameters.parameter(GMLFeatureStoreFactory.XSD.getName().toString()).getValue();
            final String xsdTypeName = (String) parameters.parameter(GMLFeatureStoreFactory.XSD_TYPE_NAME.getName().toString()).getValue();
            if (xsd != null) {
                //read types from XSD file
                final JAXBFeatureTypeReader reader = new JAXBFeatureTypeReader();
                try {
                    for (FeatureType ft : reader.read(new URL(xsd))) {
                        if (ft.getName().tip().toString().equalsIgnoreCase(xsdTypeName)) {
                            featureType = ft;
                        }
                    }
                    if (featureType == null) {
                        throw new DataStoreException("Type for name " + xsdTypeName + " not found in xsd.");
                    }

                    // schemaLocations.put(reader.getTargetNamespace(),xsd); needed?
                } catch (MalformedURLException | JAXBException ex) {
                    throw new DataStoreException(ex.getMessage(), ex);
                }
            } else {
                final JAXPStreamFeatureReader reader = new JAXPStreamFeatureReader();
                reader.getProperties().put(JAXPStreamFeatureReader.LONGITUDE_FIRST, longitudeFirst);
                reader.setReadEmbeddedFeatureType(true);
                try {
                    FeatureReader ite = reader.readAsStream(file);
                    featureType = ite.getFeatureType();
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
        return CAPABILITIES;
    }

    @Override
    public void refreshMetaModel() {
    }

    @Override
    public Path[] getComponentFiles() throws DataStoreException {
        return new Path[]{file};
    }

    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        if (!(query instanceof org.geotoolkit.data.query.Query)) throw new UnsupportedQueryException();

        final org.geotoolkit.data.query.Query gquery = (org.geotoolkit.data.query.Query) query;
        typeCheck(gquery.getTypeName());

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
        return FeatureStreams.subset(freader, gquery);
    }

}
