package org.geotoolkit.data.mapinfo.mif;

import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.*;
import org.geotoolkit.data.mapinfo.ProjectionUtils;
import org.geotoolkit.data.memory.GenericReprojectFeatureIterator;
import org.geotoolkit.data.query.DefaultQueryCapabilities;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.query.QueryCapabilities;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.parameter.Parameters;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
     * @param url The URL of the MIF file to use for this DataStore.
     *
     * @throws DataStoreException If we got a problem getting needed files.
     */
    public MIFFeatureStore(final URL url) throws DataStoreException {
        this(url, null);
    }

    /**
     * This sets the datastore's namespace during construction (so the schema -
     * FeatureType - will have the correct value) You can call this with
     * namespace = null, but I suggest you give it an actual namespace.
     *
     * @param url
     * @param namespace
     */
    public MIFFeatureStore(final URL url, final String namespace) throws DataStoreException {
        this(toParameter(url, namespace));
    }

    public MIFFeatureStore(final ParameterValueGroup params) throws DataStoreException {
        super(params);

        final URL filePath = (URL) params.parameter(MIFFeatureStoreFactory.URLP.getName().toString()).getValue();
        try {
            manager = new MIFManager(filePath);
        } catch (Exception e) {
            throw new DataStoreException("Datastore can't reach target data.", e);
        }
    }

    private static ParameterValueGroup toParameter(final URL url, final String namespace) {
        final ParameterValueGroup params = MIFFeatureStoreFactory.PARAMETERS_DESCRIPTOR.createValue();
        Parameters.getOrCreate(MIFFeatureStoreFactory.URLP, params).setValue(url);
        Parameters.getOrCreate(MIFFeatureStoreFactory.NAMESPACE, params).setValue(namespace);
        return params;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureStoreFactory getFactory() {
        return FeatureStoreFinder.getFactoryById(MIFFeatureStoreFactory.NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Name> getNames() throws DataStoreException {
        return manager.getTypeNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createFeatureType(Name typeName, FeatureType featureType) throws DataStoreException {
        try {
            manager.addSchema(typeName, featureType);
        } catch (URISyntaxException e) {
            throw new DataStoreException("We're unable to add a schema because we can't access source files.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateFeatureType(Name typeName, FeatureType featureType) throws DataStoreException {
        throw new DataStoreException("Can not update MIF schema.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteFeatureType(Name typeName) throws DataStoreException {
        manager.deleteSchema(typeName);
        removeFeatures(typeName, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureType getFeatureType(Name typeName) throws DataStoreException {
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
    public List<FeatureId> addFeatures(Name groupName, Collection<? extends Feature> newFeatures, Hints hints) throws DataStoreException {
        final FeatureWriter writer = getFeatureWriter(groupName, null, null);

        // We remove the features as we get them. We don't need to write them as the default writing behaviour is append mode.
        while (writer.hasNext()) {
            writer.next();
            writer.remove();
        }

        List<FeatureId> addedFeatures = null;
        if(manager.getWrittenCRS() != null) {
            final FeatureCollection toWrite;
            if(newFeatures instanceof FeatureCollection) {
                toWrite = GenericReprojectFeatureIterator.wrap( (FeatureCollection) newFeatures, manager.getWrittenCRS());
            } else {
                toWrite = GenericReprojectFeatureIterator.wrap(
                        FeatureStoreUtilities.collection(newFeatures.toArray(new Feature[newFeatures.size()])),
                        manager.getWrittenCRS());
            }
            addedFeatures = FeatureStoreUtilities.write(writer, toWrite);
        } else {
            addedFeatures = FeatureStoreUtilities.write(writer, newFeatures);
        }
        return addedFeatures;
    }

    /**
     * The update operation is not supported for now, because of the behaviour of the writer, which have to manage
     * multiple feature types.
     */
    @Override
    public void updateFeatures(Name groupName, Filter filter, Map<? extends PropertyDescriptor, ? extends Object> values) throws DataStoreException {
        //handleUpdateWithFeatureWriter(groupName, filter, values);
        throw new UnsupportedOperationException("Update operation is not supported now");
    }

    /**
     * The remove operation is not supported for now, because of the behaviour of the writer, which have to manage
     * multiple feature types.
     */
    @Override
    public void removeFeatures(Name groupName, Filter filter) throws DataStoreException {
        //handleRemoveWithFeatureWriter(groupName, filter);
        throw new UnsupportedOperationException("Remove operation is not supported now");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureReader getFeatureReader(Query query) throws DataStoreException {
        typeCheck(query.getTypeName());
        return handleRemaining(new MIFFeatureReader(manager, query.getTypeName()), query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter, Hints hints) throws DataStoreException {
        typeCheck(typeName);
        final MIFFeatureReader reader = new MIFFeatureReader(manager, typeName);
        final MIFFeatureWriter writer = new MIFFeatureWriter(manager, reader);
        return  writer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshMetaModel() {
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
                if(mifCRS != null && ! mifCRS.isEmpty()) {
                    isCompatible = true;
                }
            } catch(Exception e) {
                // Nothing to do here, if we get an exception, we just get an incompatible CRS.
            }
            return isCompatible;
        }

}
