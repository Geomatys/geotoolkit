/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
package org.geotoolkit.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.storage.DataStoreException;
import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.feature.simple.AbstractSimpleFeature;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.jdbc.fid.PrimaryKey;
import org.geotoolkit.util.Converters;
import org.geotoolkit.util.logging.Logging;

import org.opengis.feature.FeatureFactory;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.BoundingBox;

import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;


/**
 * Reader for jdbc datastore
 *
 * @author Justin Deoliveira, The Open Plannign Project.
 *
 * @module pending
 */
public class JDBCFeatureReader implements  FeatureReader<SimpleFeatureType, SimpleFeature> {

    protected static final Logger LOGGER = Logging.getLogger(JDBCFeatureReader.class);

    /**
     * Stores the creation stack trace if assertion are enable.
     */
    protected Throwable creationStack;

    /**
     * the datastore
     */
    protected DefaultJDBCDataStore dataStore;
    /**
     * schema of features
     */
    protected SimpleFeatureType featureType;
    /**
     * geometry factory used to create geometry objects
     */
    protected GeometryFactory geometryFactory;
    /**
     * hints
     */
    protected Hints hints;
    /**
     * flag indicating if the iterator has another feature
     */
    protected Boolean next;
    /**
     * feature builder
     */
    protected SimpleFeatureBuilder builder;
    /**
     * The primary key
     */
    protected PrimaryKey pkey;

    /**
     * statement,result set that is being worked from.
     */
    protected Statement st;
    protected ResultSet rs;
    protected Connection cx;
    protected String[] columnNames;

    public JDBCFeatureReader(final String sql, final Connection cx, final JDBCDataStore store, 
            final Name groupName, final SimpleFeatureType featureType, final PrimaryKey pkey, final Hints hints )
            throws SQLException, IOException, DataStoreException{
        init( store, groupName, featureType, pkey, hints );

        //create the result set
        this.cx = cx;
        st = cx.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        st.setFetchSize(store.getFetchSize());
        rs = st.executeQuery(sql);
    }

    public JDBCFeatureReader(final PreparedStatement st, final Connection cx, final JDBCDataStore store, 
            final Name groupName, final SimpleFeatureType featureType, final PrimaryKey pkey, final Hints hints)
            throws SQLException, IOException, DataStoreException{

        init( store, groupName, featureType, pkey, hints );

        //create the result set
        this.cx = cx;
        this.st = st;
        rs = st.executeQuery();
    }

    protected void init(final JDBCDataStore store, final Name typeName, final SimpleFeatureType featureType, final PrimaryKey pkey,
                        final Hints chints) throws IOException, DataStoreException{
        // init the tracer if we need to debug a connection leak
        assert(creationStack = new IllegalStateException().fillInStackTrace()) != null;

        // init base fields
        this.dataStore = (DefaultJDBCDataStore) store;
        this.featureType = featureType;
        this.hints = (chints == null) ? new Hints() : chints;

        //grab a geometry factory... check for a special hint
        geometryFactory = (GeometryFactory) hints.get(HintsPending.JTS_GEOMETRY_FACTORY);
        if (geometryFactory == null) {
            // look for a coordinate sequence factory
            final CoordinateSequenceFactory csFactory =
                (CoordinateSequenceFactory) hints.get(HintsPending.JTS_COORDINATE_SEQUENCE_FACTORY);

            if (csFactory != null) {
                geometryFactory = new GeometryFactory(csFactory);
            }
        }

        if (geometryFactory == null) {
            // fall back on one privided by datastore
            geometryFactory = dataStore.getGeometryFactory();
        }

        // create a feature builder using the factory hinted or the one coming
        // from the datastore
        FeatureFactory ff = (FeatureFactory) hints.get(HintsPending.FEATURE_FACTORY);
        if (ff == null) {
            ff = dataStore.getFeatureFactory();
        }
        builder = new SimpleFeatureBuilder(featureType, ff);

        // find the primary key
        this.pkey = pkey;
    }

    public JDBCFeatureReader(final JDBCFeatureReader other) {
        this.featureType = other.featureType;
        this.dataStore = other.dataStore;
        this.hints = other.hints;
        this.geometryFactory = other.geometryFactory;
        this.builder = other.builder;
        this.st = other.st;
        this.rs = other.rs;
        this.pkey = other.pkey;
    }

    @Override
    public SimpleFeatureType getFeatureType() {
        return featureType;
    }

    @Override
    public boolean hasNext() throws DataStoreRuntimeException {
        ensureOpen();

        if (next == null) {
            try {
                next = Boolean.valueOf(rs.next());
            } catch (SQLException e) {
                throw new DataStoreRuntimeException(e);
            }
        }

        return next.booleanValue();
    }

    protected void ensureNext() {
        if (next == null) {
            throw new IllegalStateException("Must call hasNext before calling next");
        }
    }

    protected void ensureOpen() throws DataStoreRuntimeException {
        if ( rs == null ) {
            throw new DataStoreRuntimeException( "reader already closed" );
        }
    }

    @Override
    public SimpleFeature next() throws DataStoreRuntimeException, IllegalArgumentException, NoSuchElementException {
        try {
            ensureOpen();
            ensureNext();

            //grab the connection
            final Connection cx;
            try {
                cx = st.getConnection();
            }catch (SQLException e) {
                throw (DataStoreRuntimeException) new DataStoreRuntimeException().initCause(e);
            }

            // figure out the fid
            final String fid;
            try {
                // wrap the fid in the type name
                fid = featureType.getTypeName() + "." + PrimaryKey.encodeFID(pkey,rs);
            } catch (SQLException e) {
                throw new DataStoreRuntimeException("Could not determine fid from primary key", e);
            }

            // round up attributes
            final int attributeCount = featureType.getAttributeCount();
            for(int i = 0; i < attributeCount; i++) {
                final AttributeDescriptor type = featureType.getDescriptor(i);

                try {
                    Object value = null;

                    // is this a geometry?
                    if (type instanceof GeometryDescriptor) {
                        final GeometryDescriptor gatt = (GeometryDescriptor) type;

                        //read the geometry
                        try {
                            value = dataStore.getDialect().decodeGeometryValue(
                                    gatt, rs, i + 1, geometryFactory, cx);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        if (value != null) {
                            //check to see if a crs was set
                            Geometry geometry = (Geometry) value;
                            if ( geometry.getUserData() == null ) {
                                //if not set, set from descriptor
                                geometry.setUserData( gatt.getCoordinateReferenceSystem() );
                            }
                        }
                    } else {
                        value = rs.getObject(i + 1);
                    }

                    // they value may need conversion. We let converters chew the initial
                    // value towards the target type, if the result is not the same as the
                    // original, then a conversion happened and we may want to report it to the
                    // user (being the feature type reverse engineerd, it's unlikely a true
                    // conversion will be needed)
                    if (value != null) {
                        final Class binding = type.getType().getBinding();
                        final Object converted = Converters.convert(value, binding);
                        if (converted != null && converted != value) {
                            value = converted;
                            if (dataStore.getLogger().isLoggable(Level.FINER)) {
                                String msg = value + " is not of type " + binding.getName()
                                    + ", attempting conversion";
                                dataStore.getLogger().finer(msg);
                            }
                        }
                    }

                    builder.add(value);
                } catch (SQLException e) {
                    throw new DataStoreRuntimeException(e);
                }
            }

            // create the feature
            return builder.buildFeature(fid);

        } finally {
            // reset the next flag. We do this in a finally block to make sure we
            // move to the next record no matter what, if the current one could
            // not be read there is no salvation for it anyways
            next = null;
        }
    }

    @Override
    public void close() throws DataStoreRuntimeException {
        if( dataStore != null ) {
            //clean up
            dataStore.closeSafe(rs);
            dataStore.closeSafe(st);
            dataStore.closeSafe(cx);
        }else {
            //means we are already closed... should we throw an exception?
        }
        cleanup();
    }

    /**
     * Cleans up the reader state without closing the accessory resultset, statement
     * and connection. Use only if the above are shared with another object that will
     * take care of closing them.
     */
    protected void cleanup() {
        //throw away state
        rs = null;
        st = null;
        dataStore = null;
        featureType = null;
        geometryFactory = null;
        hints = null;
        next = null;
        builder = null;
        creationStack = null;
    }

    @Override
    protected void finalize() throws Throwable {
        if (dataStore != null) {
            LOGGER.warning(
                "UNCLOSED ITERATOR : There is code leaving JDBC feature reader/writer open, " +
                "this may cause memory leaks or data integrity problems !");
            if(creationStack != null) {
                LOGGER.log(Level.WARNING,
                    "The unclosed reader originated on this stack trace", creationStack);
            }            
            close();
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Feature wrapper around a result set.
     */
    protected class ResultSetFeature extends AbstractSimpleFeature {
        /**
         * result set
         */
        ResultSet rs;
        /**
         * connection
         */
        Connection cx;
        /**
         * primary key
         */
        final PrimaryKey key;

        /**
         * updated values
         * */
        final Object[] values;

        /**
         * fid
         */
        FeatureId fid;

        /**
         * dirty flags
         */
        final boolean[] dirty;

        /**
         * Marks this feature as "new", about to be inserted
         */
        boolean newFeature;

        /**
         * name index
         */
        final HashMap<Object, Integer> index;
        /**
         * user data
         */
        final HashMap<Object, Object> userData = new HashMap<Object, Object>();

        /**
         * The set of user data attached to each attribute (lazily created)
         */
        private Map<Object, Object>[] attributeUserData;

        ResultSetFeature(final ResultSet rs, final Connection cx) throws SQLException, DataStoreException {
            this.rs = rs;
            this.cx = cx;

            //get the result set metadata
            final ResultSetMetaData md = rs.getMetaData();

            //get the primary key
            key = dataStore.getPrimaryKey(featureType);
            int count = md.getColumnCount();
            columnNames = new String[count];

            //set up values
            values = new Object[count];
            dirty = new boolean[values.length];

            //set up name lookup
            index = new HashMap<Object, Integer>();

            loop:
            for (int i = 0; i < count; i++) {

                //must store all possible name writing combinaison
                final String localpart = md.getColumnName(i + 1);
                final Name name = featureType.getDescriptor(localpart).getName();
                final int attIndex = i;
                index.put(name, attIndex);
                index.put(new DefaultName(name.getLocalPart()), attIndex);
                index.put(name.getLocalPart(), attIndex);
                index.put(DefaultName.toJCRExtendedForm(name), attIndex);
                index.put(DefaultName.toExtendedForm(name), attIndex);
            }
        }

        public void init(final String fid) {
            // mark as new according to the fid
            newFeature = fid == null;

            //clear values
            for (int i = 0; i < values.length; i++) {
                values[i] = null;
                dirty[i] = false;
            }

            this.fid = SimpleFeatureBuilder.createDefaultFeatureIdentifier(fid);
        }

        public void init() throws SQLException, IOException {
            //get fid
            //PrimaryKey pkey = dataStore.getPrimaryKey(featureType);

            //TODO: factory fid prefixing out
            init(featureType.getTypeName() + "." + PrimaryKey.encodeFID( key, rs ));
        }

        @Override
        public SimpleFeatureType getType() {
            return featureType;
        }

        @Override
        public FeatureId getIdentifier() {
        	return fid;
        }

        public void setID(final String id) {
            fid = new DefaultFeatureId(id);
        }

        @Override
        public Object getAttribute(final String name) {
            return getAttribute(index.get(name));
        }

        @Override
        public Object getAttribute(final Name name) {
            return getAttribute(DefaultName.toExtendedForm(name));
        }

        @Override
        public Object getAttribute(final int index) throws IndexOutOfBoundsException {
            return getAttributeInternal( index, mapToResultSetIndex(index) );
        }

        private int mapToResultSetIndex(final int index) {
            //map the index to result set
            return index+1;
        }

        private Object getAttributeInternal(final int index, final int rsindex) {
            if (!newFeature && values[index] == null && !dirty[index]) {
                synchronized (this) {
                    try {
                        if (!newFeature && values[index] == null && !dirty[index]) {
                            //load the value from the result set, check the case
                            // in which its a geometry, this case the dialect needs
                            // to read it

                            final AttributeDescriptor att = featureType.getDescriptor(index);
                            if (att instanceof GeometryDescriptor) {
                                GeometryDescriptor gatt = (GeometryDescriptor) att;
                                try {
                                    values[index] = dataStore.getDialect().decodeGeometryValue(
                                            gatt, rs, rsindex, dataStore.getGeometryFactory(), st.getConnection());
                                } catch (IOException ex) {
                                    throw  new RuntimeException(ex);
                                }
                            } else {
                                values[index] = rs.getObject(rsindex);
                            }
                        }
                     } catch (SQLException e) {
                         //do not throw exception because of insert mode
                         //TODO: set a flag for insert vs update
                         //throw new RuntimeException( e );
                         values[index] = null;
                     }
                }
            }
            return values[index];
        }

        @Override
        public void setAttribute(String name, Object value) {
            dataStore.getLogger().fine("Setting " + name + " to " + value);

            final int i = index.get(name);
            setAttribute(i, value);
        }

        @Override
        public void setAttribute(Name name, Object value) {
            setAttribute(DefaultName.toExtendedForm(name), value);
        }

        @Override
        public void setAttribute(int index, Object value) throws IndexOutOfBoundsException {
            dataStore.getLogger().fine("Setting " + index + " to " + value);
            values[index] = value;
            dirty[index] = true;
        }

        @Override
        public void setAttributes(final List<Object> values) {
            for (int i = 0; i < values.size(); i++) {
                setAttribute(i, values.get(i));
            }
        }

        @Override
        public int getAttributeCount() {
            return values.length;
        }

        public boolean isDirty(final int index) {
            return dirty[index];
        }

        public boolean isDirrty(final String name) {
            return isDirty(index.get(name));
        }

        public void close() {
            rs = null;
            cx = null;
            columnNames=null;
        }


        @Override
        public Object getDefaultGeometry() {
            final GeometryDescriptor geomDesc = featureType.getGeometryDescriptor();
            return getAttribute(geomDesc.getName());
        }

        @Override
        public void setDefaultGeometry(Object defaultGeometry) {
            final GeometryDescriptor descriptor = featureType.getGeometryDescriptor();
            setAttribute(descriptor.getName(), defaultGeometry );
        }

        @Override
        public BoundingBox getBounds() {
            final Object obj = getDefaultGeometry();
            if (obj instanceof Geometry) {
                final Geometry geometry = (Geometry) obj;
                return new JTSEnvelope2D(geometry.getEnvelopeInternal(), featureType.getCoordinateReferenceSystem());
            }
            return new JTSEnvelope2D(featureType.getCoordinateReferenceSystem());
        }

        @Override
        public Map<Object, Object> getUserData() {
            return userData;
        }

        @Override
        public void validate() {
        }

        @Override
        protected boolean isValidating() {
            return false;
        }

        @Override
        protected Map<Object, Integer> getIndex() {
            return index;
        }

        @Override
        protected Object[] getValues() {
            return values;
        }

        @Override
        protected Map<Object, Object>[] getAttributUserData() {
            if(attributeUserData == null){
                attributeUserData = new HashMap[values.length];
            }
            return attributeUserData;
        }
    }

}
