

package org.geotoolkit.data.memory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geotoolkit.data.AbstractDataStore;
import org.geotoolkit.data.DataUtilities;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.FeatureWriter;
import org.geotoolkit.data.query.Query;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;

/**
 * @todo : make this concurrent
 * @author Johann Sorel (Geomatys)
 */
public class MemoryDataStore extends AbstractDataStore{

    private final Map<Name,FeatureType> types = new HashMap<Name, FeatureType>();
    private final Map<Name,List<Feature>> features = new HashMap<Name, List<Feature>>();
    private Set<Name> nameCache = null;

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized Set<Name> getNames() throws IOException {
        if(nameCache == null){
            nameCache = new HashSet<Name>(types.keySet());
        }
        return nameCache;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureType getSchema(Name name) throws IOException {
        return types.get(name);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void createSchema(Name name, FeatureType featureType) throws IOException {
        if(featureType == null){
            throw new NullPointerException("Feature type can not be null.");
        }
        if(name == null){
            throw new NullPointerException("Name can not be null.");
        }

        if(getSchema(name) != null){
            throw new IllegalArgumentException("FeatureType with name : " + featureType.getName() + " already exist.");
        }

        types.put(name, featureType);
        features.put(name, new ArrayList<Feature>());

        //clear name cache
        nameCache = null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void updateSchema(Name typeName, FeatureType featureType) throws IOException {
        //todo must do it a way to avoid destroying all features.
        deleteSchema(typeName);
        createSchema(typeName,featureType);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public synchronized void deleteSchema(Name typeName) throws IOException {
        final FeatureType type = types.remove(typeName);

        if(type == null){
            throw new IllegalArgumentException("No featureType for name : " + typeName);
        }

        features.remove(typeName);

        //clear name cache
        nameCache = null;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object getQueryCapabilities() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public long getCount(Query query) throws IOException {
        final List<Feature> lst = features.get(query.getTypeName());

        if(lst == null){
            throw new IllegalArgumentException("No featureType for name : " + query.getTypeName());
        }

        final Integer max = query.getMaxFeatures();
        final Filter filter = query.getFilter();

        //filter should never be null in the query
        if(filter == Filter.INCLUDE){
            if(max != null){
                return Math.max(lst.size(), max);
            }else{
                return lst.size();
            }
        }else if(filter == Filter.EXCLUDE){
            return 0;
        }else{
            int count = 0;

            if(max != null){
                for(int index=0; index <= max; index++){
                    if(filter.evaluate(lst.get(index))) count++;
                }
            }else{
                for(final Feature f :lst){
                    if(filter.evaluate(f)) count++;
                }
            }
            
            return count;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureReader getFeatureReader(Query query) throws IOException {
        final FeatureType type = getSchema(query.getTypeName());
        final List<Feature> lst = features.get(query.getTypeName());

        if(lst == null){
            throw new IllegalArgumentException("No featureType for name : " + query.getTypeName());
        }

        //fall back on generic parameter handling.
        //todo we should handle at least spatial filter here by using a quadtree.
        return handleRemaining(DataUtilities.reader(lst.iterator(), type), query);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriter(Name typeName, Filter filter) throws IOException {

        //todo make a writer
        //return handleRemaining(writer, filter);

        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureWriter getFeatureWriterAppend(Name typeName) throws IOException {

        //todo make a writer append
        //make a generic iterator wish append to a callable ?

        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
        super.dispose();
        types.clear();
        features.clear();
    }

}
