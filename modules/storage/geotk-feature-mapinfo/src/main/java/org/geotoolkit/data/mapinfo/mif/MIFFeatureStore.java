/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2013, Geomatys
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
package org.geotoolkit.data.mapinfo.mif;

import java.io.IOException;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.*;
import org.geotoolkit.data.mapinfo.ProjectionUtils;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.opengis.util.GenericName;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.apache.sis.parameter.Parameters;
import org.geotoolkit.feature.ReprojectFeatureType;
import org.apache.sis.storage.IllegalNameException;
import org.geotoolkit.data.FeatureStreams;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.storage.DataStores;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;

/**
 * A featureStore for MapInfo exchange format MIF-MID.
 *
 * @author Alexis Manin (Geomatys)
 *         Date : 21/02/13
 */
public class MIFFeatureStore extends AbstractFeatureStore {

    private final MIFManager manager;

    private final QueryCapabilities queryCapabilities = new DefaultQueryCapabilities(false);

    /**
     * Creates a new instance of MIFFeatureStore.
     *
     * @param uri The URL of the MIF file to use for this DataStore.
     *
     * @throws DataStoreException If we got a problem getting needed files.
     */
    public MIFFeatureStore(final URI uri) throws DataStoreException {
        this(toParameter(uri));
    }

    public MIFFeatureStore(final ParameterValueGroup params) throws DataStoreException {
        super(params);

        final URI filePath = (URI) params.parameter(MIFFeatureStoreFactory.PATH.getName().toString()).getValue();
        try {
            manager = new MIFManager(filePath);
        } catch (Exception e) {
            throw new DataStoreException("Datastore can't reach target data.", e);
        }
    }

    private static ParameterValueGroup toParameter(final URI uri) {
        final Parameters params = Parameters.castOrWrap(MIFFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue());
        params.getOrCreate(MIFFeatureStoreFactory.PATH).setValue(uri);
        return params;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureStoreFactory getFactory() {
        return (FeatureStoreFactory) DataStores.getFactoryById(MIFFeatureStoreFactory.NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<GenericName> getNames() throws DataStoreException {
        return manager.getTypeNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createFeatureType(FeatureType featureType) throws DataStoreException {
        try {
            manager.addSchema(featureType.getName(), featureType);
        } catch (URISyntaxException | IOException e) {
            throw new DataStoreException("We're unable to add a schema because we can't access source files.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateFeatureType(FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Can not update MIF schema.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteFeatureType(String typeName) throws DataStoreException {
        manager.deleteSchema(typeName);
        removeFeatures(typeName, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureType getFeatureType(String typeName) throws DataStoreException {
        return manager.getType(typeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryCapabilities getQueryCapabilities() {
        return queryCapabilities;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FeatureId> addFeatures(String groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        final List<FeatureId> addedFeatures;
        try(final FeatureWriter writer = getFeatureWriter(QueryBuilder.all(groupName))) {

            // We remove the features as we get them. We don't need to write them as the default writing behaviour is append mode.
            while (writer.hasNext()) {
                writer.next();
                writer.remove();
            }

            if(manager.getWrittenCRS() != null) {
                final FeatureCollection toWrite;
                final FeatureType type = ((FeatureCollection)newFeatures).getType();
                if(newFeatures instanceof FeatureCollection) {
                    toWrite = FeatureStreams.decorate((FeatureCollection) newFeatures, new ReprojectFeatureType(type, manager.getWrittenCRS()));
                } else {
                    toWrite = FeatureStreams.decorate(
                            FeatureStoreUtilities.collection(newFeatures.toArray(new Feature[newFeatures.size()])),
                            new ReprojectFeatureType(type, manager.getWrittenCRS()));
                }
                addedFeatures = FeatureStoreUtilities.write(writer, toWrite);
            } else {
                addedFeatures = FeatureStoreUtilities.write(writer, newFeatures);
            }
        }
        return addedFeatures;
    }

    /**
     * The update operation is not supported for now, because of the behaviour of the writer, which have to manage
     * multiple feature types.
     */
    @Override
    public void updateFeatures(String groupName, Filter filter, Map<String, ? extends Object> values) throws DataStoreException {
        //handleUpdateWithFeatureWriter(groupName, filter, values);
        throw new UnsupportedOperationException("Update operation is not supported now");
    }

    /**
     * The remove operation is not supported for now, because of the behaviour of the writer, which have to manage
     * multiple feature types.
     */
    @Override
    public void removeFeatures(String groupName, Filter filter) throws DataStoreException {
        //handleRemoveWithFeatureWriter(groupName, filter);
        throw new UnsupportedOperationException("Remove operation is not supported now");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        typeCheck(query.getTypeName());

        FeatureType ft = getFeatureType(query.getTypeName());

        /* We analyze input query to extract queried properties. We do it because
         * we're capable of filtering properties at read, so we'll handle this part
         * of the query.
         */
        if (query.getPropertyNames() != null) {
            final FeatureTypeBuilder builder = new FeatureTypeBuilder(ft);
            final Iterator<PropertyTypeBuilder> it = builder.properties().iterator();
            final String[] props = Arrays.copyOf(query.getPropertyNames(), query.getPropertyNames().length);
            Arrays.sort(props);
            while (it.hasNext()) {
                final GenericName pName = it.next().getName();

                if (Arrays.binarySearch(props, pName.toString()) < 0 || Arrays.binarySearch(props, pName.tip().toString()) < 0) {
                    it.remove();
                }
            }

            ft = builder.build();
            final QueryBuilder qb = new QueryBuilder(query);
            qb.setProperties(null);
            query = qb.buildQuery();
        }

        return FeatureStreams.subset(new MIFFeatureReader(manager, ft), query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureWriter getFeatureWriter(Query query) throws DataStoreException {
        typeCheck(query.getTypeName());
        final FeatureReader reader = getFeatureReader(query);
        final MIFFeatureWriter writer = new MIFFeatureWriter(manager, reader);
        return  writer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshMetaModel() throws IllegalNameException {
        manager.refreshMetaModel();
    }


    /**
     * MIF file defines a delimiter character to separate values into the MID file. This function allows user to redefine it.
     * @param newDelimiter The new delimiter to use for MID value separation.
     */
    public void setDelimiter(char newDelimiter) {
        manager.setDelimiter(newDelimiter);
    }

    public static boolean isCompatibleCRS(CoordinateReferenceSystem source) {
        boolean isCompatible = false;
        try {
            final String mifCRS = ProjectionUtils.crsToMIFSyntax(source);
            if (mifCRS != null && !mifCRS.isEmpty()) {
                isCompatible = true;
            }
        } catch (Exception e) {
            // Nothing to do here, if we get an exception, we just get an incompatible CRS.
        }
        return isCompatible;
    }
}
