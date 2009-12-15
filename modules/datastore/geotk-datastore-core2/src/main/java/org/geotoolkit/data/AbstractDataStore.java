

package org.geotoolkit.data;

import java.io.IOException;

import org.geotoolkit.data.memory.EmptyFeatureReader;
import org.geotoolkit.data.memory.EmptyFeatureWriter;
import org.geotoolkit.data.memory.GenericFilterFeatureIterator;
import org.geotoolkit.data.memory.GenericMaxFeatureIterator;
import org.geotoolkit.data.memory.GenericReprojectFeatureIterator;
import org.geotoolkit.data.memory.GenericRetypeFeatureIterator;
import org.geotoolkit.data.memory.GenericStartIndexFeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.session.DefaultSession;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.geometry.DefaultBoundingBox;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public abstract class AbstractDataStore implements DataStore{

    /**
     * {@inheritDoc }
     */
    @Override
    public Session createSession() {
        return new DefaultSession(this);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isWriteable(Name typeName) throws IOException {
        //while raise an error if type doesnt exist
        getSchema(typeName);
        return false;
    }

    /**
     * {@inheritDoc }
     *
     * This implementation will aquiere a reader and iterate to count.
     * Subclasses should override this method if they have a faster way to
     * calculate count.
     */
    @Override
    public long getCount(Query query) throws IOException {
        long count = 0;

        final FeatureReader reader = getFeatureReader(query);
        try{
            while(reader.hasNext()){
                reader.next();
                count++;
            }
        }finally{
            reader.close();
        }

        return count;
    }

    /**
     * {@inheritDoc }
     *
     * This implementation will aquiere a reader and iterate to expend an envelope.
     * Subclasses should override this method if they have a faster way to
     * calculate envelope.
     */
    @Override
    public Envelope getEnvelope(Query query) throws IOException {
        BoundingBox env = null;

        final FeatureReader reader = getFeatureReader(query);
        try{
            while(reader.hasNext()){
                final Feature f = reader.next();
                final BoundingBox bbox = f.getBounds();
                if(!bbox.isEmpty()){
                    if(env != null){
                        env.include(bbox);
                    }else{
                        env = new DefaultBoundingBox(bbox, bbox.getCoordinateReferenceSystem());
                    }
                }
            }
        }finally{
            reader.close();
        }

        return env;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void dispose() {
    }

    ////////////////////////////////////////////////////////////////////////////
    // useful methods for datastore that doesn't implement all query parameters/
    ////////////////////////////////////////////////////////////////////////////

    protected FeatureReader handleRemaining(FeatureReader reader, Query remainingParameters) throws IOException{

        final Integer start = remainingParameters.getStartIndex();
        final Integer max = remainingParameters.getMaxFeatures();
        final Filter filter = remainingParameters.getFilter();
        final String[] properties = remainingParameters.getPropertyNames();
        final SortBy[] sorts = remainingParameters.getSortBy();
        final CoordinateReferenceSystem crs = remainingParameters.getCoordinateSystemReproject();

        //we should take care of wrapping the reader in a correct order to avoid
        //unnecessary calculations. fast and reducing number wrapper should be placed first.
        //but we must not take misunderstanding assumptions neither.
        //exemple : filter is slow than startIndex and MaxFeature but must be placed before
        //          otherwise the result will be illogic.


        //wrap sort by ---------------------------------------------------------
        //This can be really expensive, and force the us to read the full iterator.
        //that may cause out of memory errors. we can not handle this here.

        //wrap filter ----------------------------------------------------------
        //we must keep the filter first since it impacts the start index and max feature
        if(filter != null && filter != Filter.INCLUDE){
            if(filter == Filter.EXCLUDE){
                //filter that exclude everything, use optimzed reader
                reader = new EmptyFeatureReader(reader.getFeatureType());
            }else{
                reader = GenericFilterFeatureIterator.wrap(reader, filter);
            }
        }

        //wrap start index -----------------------------------------------------
        if(start != null && start > 0){
            reader = GenericStartIndexFeatureIterator.wrap(reader, start);
        }
        
        //wrap max -------------------------------------------------------------
        if(max != null){
            if(max == 0){
                //use an optimized reader
                reader = new EmptyFeatureReader(reader.getFeatureType());
            }else{
                reader = GenericMaxFeatureIterator.wrap(reader, max);
            }
        }

        //wrap properties ------------------------------------------------------
        if(properties != null){
            final FeatureType mask = FeatureTypeUtilities.createSubType((SimpleFeatureType) reader.getFeatureType(), properties);
            reader = GenericRetypeFeatureIterator.wrap(reader, mask);
        }

        //wrap reprojection ----------------------------------------------------
        if(crs != null){
            try {
                reader = GenericReprojectFeatureIterator.wrap(reader, crs);
            } catch (FactoryException ex) {
                throw new IOException(ex);
            }
        }

        return reader;
    }


    protected FeatureWriter handleRemaining(FeatureWriter writer, Filter filter) throws IOException{

        //wrap filter ----------------------------------------------------------
        if(filter != null && filter != Filter.INCLUDE){
            if(filter == Filter.EXCLUDE){
                //filter that exclude everything, use optimzed writer
                writer = new EmptyFeatureWriter(writer.getFeatureType());
            }else{
                writer = GenericFilterFeatureIterator.wrap(writer, filter);
            }
        }

        return writer;
    }

}
