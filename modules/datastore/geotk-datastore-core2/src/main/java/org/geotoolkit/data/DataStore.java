

package org.geotoolkit.data;

import java.io.IOException;
import java.util.Set;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.session.Session;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.geometry.Envelope;

public interface DataStore {


    Session createSession();

    Set<Name> getNames() throws IOException;

    void createSchema(Name typeName, FeatureType featureType) throws IOException;

    void updateSchema(Name typeName, FeatureType featureType) throws IOException;

    void deleteSchema(Name typeName) throws IOException;

    FeatureType getSchema(Name typeName) throws IOException;

    boolean isWriteable(Name typeName) throws IOException;

    long getCount(Query query) throws IOException;

    /**
     * Get the envelope of all features matching the given query.
     * 
     * @param query : features to query
     * @return Envelope or null if no features where found.
     * @throws IOException : error occured while reading
     */
    Envelope getEnvelope(Query query) throws IOException;

    FeatureReader getFeatureReader(Query query) throws IOException;

    FeatureWriter getFeatureWriter(Name typeName, Filter filter) throws IOException;

    FeatureWriter getFeatureWriterAppend(Name typeName) throws IOException;

    void dispose();
    
}
