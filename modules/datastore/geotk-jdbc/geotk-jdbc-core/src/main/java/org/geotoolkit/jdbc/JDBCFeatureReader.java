/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.data.DefaultQuery;
import org.geotoolkit.data.FeatureReader;
import org.geotoolkit.data.concurrent.Transaction;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.factory.HintsPending;
import org.geotoolkit.feature.simple.SimpleFeatureBuilder;
import org.geotoolkit.feature.simple.SimpleFeatureTypeBuilder;
import org.geotoolkit.filter.identity.DefaultFeatureId;
import org.geotoolkit.geometry.jts.JTSEnvelope2D;
import org.geotoolkit.util.Converters;
import org.geotoolkit.util.logging.Logging;

import org.opengis.feature.Association;
import org.opengis.feature.FeatureFactory;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AssociationDescriptor;
import org.opengis.feature.type.AssociationType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureTypeFactory;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.BoundingBox;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequenceFactory;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;


/**
 * Reader for jdbc datastore
 *
 * @author Justin Deoliveira, The Open Plannign Project.
 *
 */
public class JDBCFeatureReader implements  FeatureReader<SimpleFeatureType, SimpleFeature> {
    protected static final Logger LOGGER = Logging.getLogger(JDBCFeatureReader.class);

    /**
     * When true, the stack trace that created a reader that wasn't closed is recorded and then
     * printed out when warning the user about this.
     */
    protected static final Boolean TRACE_ENABLED = "true".equalsIgnoreCase(System.getProperty("gt2.jdbc.trace"));

    /**
     * the feature source the reader originated from.
     */
    protected JDBCFeatureSource featureSource;
    /**
     * the datastore
     */
    protected JDBCDataStore dataStore;
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
     * current transaction
     */
    protected Transaction tx;
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
    protected Exception tracer;
    protected String[] columnNames;

    public JDBCFeatureReader(final String sql, final Connection cx, final JDBCFeatureSource featureSource,
                             final SimpleFeatureType featureType, final Hints hints ) throws SQLException, IOException
    {
        init( featureSource, featureType, hints );

        //create the result set
        this.cx = cx;
        st = cx.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        st.setFetchSize(featureSource.getDataStore().getFetchSize());
        rs = st.executeQuery(sql);
    }

    public JDBCFeatureReader(final PreparedStatement st, final Connection cx, final JDBCFeatureSource featureSource,
                             final SimpleFeatureType featureType, final Hints hints) throws SQLException, IOException
    {

        init( featureSource, featureType, hints );

        //create the result set
        this.cx = cx;
        this.st = st;
        rs = st.executeQuery();
    }

    protected void init(final JDBCFeatureSource featureSource, final SimpleFeatureType featureType,
                        final Hints hints) throws IOException
    {
        // init the tracer if we need to debug a connection leak
        if (TRACE_ENABLED) {
            tracer = new Exception();
            tracer.fillInStackTrace();
        }

        // init base fields
        this.featureSource = featureSource;
        this.dataStore = featureSource.getDataStore();
        this.featureType = featureType;
        this.tx = featureSource.getTransaction();
        this.hints = hints;

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
            ff = featureSource.getDataStore().getFeatureFactory();
        }
        builder = new SimpleFeatureBuilder(featureType, ff);

        // find the primary key
        pkey = dataStore.getPrimaryKey(featureType);
    }

    public JDBCFeatureReader(final JDBCFeatureReader other) {
        this.featureType = other.featureType;
        this.dataStore = other.dataStore;
        this.featureSource = other.featureSource;
        this.tx = other.tx;
        this.hints = other.hints;
        this.geometryFactory = other.geometryFactory;
        this.builder = other.builder;
        this.st = other.st;
        this.rs = other.rs;
    }

    @Override
    public SimpleFeatureType getFeatureType() {
        return featureType;
    }

    @Override
    public boolean hasNext() throws IOException {
        ensureOpen();

        if (next == null) {
            try {
                next = Boolean.valueOf(rs.next());
            } catch (SQLException e) {
                throw new IOException(e);
            }
        }

        return next.booleanValue();
    }

    protected void ensureNext() {
        if (next == null) {
            throw new IllegalStateException("Must call hasNext before calling next");
        }
    }

    protected void ensureOpen() throws IOException {
        if ( rs == null ) {
            throw new IOException( "reader already closed" );
        }
    }

    @Override
    public SimpleFeature next() throws IOException, IllegalArgumentException, NoSuchElementException {
        try {
            ensureOpen();
            ensureNext();

            //grab the connection
            Connection cx;
            try {
                cx = st.getConnection();
            }
            catch (SQLException e) {
                throw (IOException) new IOException().initCause(e);
            }

            // figure out the fid
            String fid;

            try {
                fid = dataStore.encodeFID(pkey,rs);

                // wrap the fid in the type name
                fid = featureType.getTypeName() + "." + fid;
            } catch (SQLException e) {
                throw new IOException("Could not determine fid from primary key", e);
            }

            // check for the association traversal depth hint, if not > 0 dont
            // resolve the associated feature or geometry
            Integer depth = (Integer) hints.get(HintsPending.ASSOCIATION_TRAVERSAL_DEPTH);

            if (depth == null) {
                depth = new Integer(0);
            }

            final PropertyName associationPropertyName =
                (PropertyName) hints.get(HintsPending.ASSOCIATION_PROPERTY);

            // round up attributes
            // List attributes = new ArrayList();
            final int pkeyCols = pkey.getColumns().size();
            final int attributeCount = featureType.getAttributeCount();
            for(int i = 0; i < attributeCount; i++) {
                final AttributeDescriptor type = featureType.getDescriptor(i);

                //figure out if any referenced attributes should be resolved
                boolean resolve = depth.intValue() > 0;

                if (resolve && (associationPropertyName != null)) {
                    AttributeDescriptor associationProperty = (AttributeDescriptor) associationPropertyName
                        .evaluate(featureType);
                    resolve = (associationProperty != null)
                        && associationProperty.getLocalName().equals(type.getLocalName());
                }

                try {
                    Object value = null;

                    // is this a geometry?
                    if (type instanceof GeometryDescriptor) {
                        GeometryDescriptor gatt = (GeometryDescriptor) type;

                        //read the geometry
                        try {
                            value = dataStore.getSQLDialect()
                                             .decodeGeometryValue(gatt, rs, i + pkeyCols + 1,
                                    geometryFactory, cx);
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
                        } else {
                            // check case where this is an associated geometry
                            if (dataStore.isAssociations()) {
                                try {
                                    dataStore.ensureAssociationTablesExist(st.getConnection());
                                } catch (SQLException e) {
                                    throw new IOException(e);
                                }

                                Statement select = null;
                                ResultSet gas = null;
                                try {
                                    if ( dataStore.getSQLDialect() instanceof PreparedStatementSQLDialect ) {
                                        select = dataStore.selectGeometryAssociationSQLPS(fid, null, gatt.getLocalName(), cx);
                                        gas = ((PreparedStatement)select).executeQuery();
                                    }
                                    else {
                                        final String sql = dataStore.selectGeometryAssociationSQL(fid, null,
                                                gatt.getLocalName());
                                        dataStore.getLogger().fine(sql);
                                        select = st.getConnection().createStatement();
                                        gas = select.executeQuery(sql.toString());
                                    }


                                    if (gas.next()) {
                                        final String gid = gas.getString("gid");
                                        final boolean ref = gas.getBoolean("ref");

                                        Geometry g = null;

                                        // if this is a "referenced" geometry,
                                        // do not
                                        // read it if the depth is <= 0
                                        if (ref && !resolve) {
                                            // use a stub
                                            g = geometryFactory.createPoint(new CoordinateArraySequence(
                                                        new Coordinate[] {  }));
                                            //g = new NullGeometry();
                                            dataStore.setGmlProperties(g, gid, null, null);
                                        } else {
                                            // read the geometry
                                            final ResultSet grs;
                                            if ( dataStore.getSQLDialect() instanceof PreparedStatementSQLDialect ) {
                                                dataStore.closeSafe( select );
                                                select = dataStore.selectGeometrySQLPS(gid, cx);

                                                grs = ((PreparedStatement) select).executeQuery();
                                            }
                                            else {
                                                String sql = dataStore.selectGeometrySQL(gid);
                                                dataStore.getLogger().fine(sql);

                                                grs = select.executeQuery(sql);
                                            }

                                            try {
                                                // should always be one
                                                if (!grs.next()) {
                                                    throw new SQLException("no entry for: " + gid
                                                        + " in " + JDBCDataStore.GEOMETRY_TABLE);
                                                }

                                                final String name = grs.getString("name");
                                                final String desc = grs.getString("description");

                                                if (grs.getObject("geometry") != null) {
                                                    //read the geometry
                                                    g = dataStore.getSQLDialect()
                                                                 .decodeGeometryValue(gatt, grs,
                                                            "geometry", geometryFactory, cx);
                                                } else {
                                                    //multi geometry?
                                                    final String gtype = grs.getString("type");

                                                    if ("MULTIPOINT".equals(gtype)
                                                            || "MULTILINESTRING".equals(gtype)
                                                            || "MULTIPOLYGON".equals(gtype)) {
                                                        final ResultSet mg;
                                                        if ( dataStore.getSQLDialect() instanceof PreparedStatementSQLDialect ) {
                                                            dataStore.closeSafe( select );
                                                            select = dataStore.selectMultiGeometrySQLPS(gid, cx);

                                                            mg = ((PreparedStatement)select).executeQuery();
                                                        }
                                                        else {
                                                            String sql = dataStore.selectMultiGeometrySQL(gid);
                                                            dataStore.getLogger().fine(sql);

                                                            mg = select.executeQuery(sql);
                                                        }

                                                        try {
                                                            final ArrayList members = new ArrayList();

                                                            while (mg.next()) {
                                                                final String mgid = mg.getString("mgid");
                                                                final boolean mref = mg.getBoolean("ref");

                                                                final Geometry member;
                                                                if ( !mref || resolve ) {
                                                                    final Statement select2;
                                                                    final ResultSet mgg;
                                                                    if ( dataStore.getSQLDialect() instanceof PreparedStatementSQLDialect ) {
                                                                        select2 = dataStore.selectGeometrySQLPS(mgid, cx);
                                                                        mgg = ((PreparedStatement)select2).executeQuery();
                                                                    }
                                                                    else {
                                                                        String sql = dataStore.selectGeometrySQL(mgid);
                                                                        dataStore.getLogger().fine(sql);

                                                                        select2 = st.getConnection()
                                                                                              .createStatement();
                                                                        mgg = select2.executeQuery(sql);
                                                                    }

                                                                    try {
                                                                        mgg.next();

                                                                        final String mname = mgg.getString(
                                                                                "name");
                                                                        final String mdesc = mgg.getString(
                                                                                "description");

                                                                        member = dataStore.getSQLDialect()
                                                                                                   .decodeGeometryValue(gatt,
                                                                                mgg, "geometry",
                                                                                geometryFactory, cx );

                                                                        dataStore.setGmlProperties(member, mgid,
                                                                            mname, mdesc);

                                                                    } finally {
                                                                        dataStore.closeSafe(mgg);
                                                                        dataStore.closeSafe(select2);
                                                                    }
                                                                }
                                                                else {
                                                                    //create a stub
                                                                    // use a stub
                                                                    member = geometryFactory.createPoint(new CoordinateArraySequence(
                                                                                new Coordinate[] {  }));
                                                                    dataStore.setGmlProperties(member, mgid, null, null);
                                                                }

                                                                members.add(member);
                                                            }

                                                            if ("MULTIPOINT".equals(gtype)) {
                                                                g = geometryFactory.createMultiPoint((Point[]) members
                                                                        .toArray(new Point[members
                                                                            .size()]));
                                                            } else if ("MULTILINESTRING".equals(
                                                                        gtype)) {
                                                                g = geometryFactory
                                                                    .createMultiLineString((LineString[]) members
                                                                        .toArray(new LineString[members
                                                                            .size()]));
                                                            } else if ("MULTIPOLYGON".equals(gtype)) {
                                                                g = geometryFactory
                                                                    .createMultiPolygon((Polygon[]) members
                                                                        .toArray(new Polygon[members
                                                                            .size()]));
                                                            } else {
                                                                g = geometryFactory
                                                                    .createGeometryCollection((Geometry[]) members
                                                                        .toArray(new Geometry[members
                                                                            .size()]));
                                                            }
                                                        } finally {
                                                            dataStore.closeSafe(mg);
                                                        }
                                                    }
                                                }

                                                dataStore.setGmlProperties(g, gid, name, desc);
                                            } finally {
                                                dataStore.closeSafe(grs);
                                            }
                                        }

                                        value = g;
                                    }

                                } finally {
                                    dataStore.closeSafe( gas );
                                    dataStore.closeSafe(select);
                                }
                            }
                        }
                    } else {
                        value = rs.getObject(i + pkeyCols + 1);
                    }

                    // is this an association?
                    if (dataStore.isAssociations()
                            && Association.class.equals(type.getType().getBinding()) && (value != null)) {

                        final Statement select;
                        final ResultSet associations;
                        if (dataStore.getSQLDialect() instanceof PreparedStatementSQLDialect ) {
                            select = dataStore.selectAssociationSQLPS(fid, cx);
                            associations = ((PreparedStatement)select).executeQuery();
                        }
                        else {
                            final String sql = dataStore.selectAssociationSQL(fid);
                            dataStore.getLogger().fine(sql);

                            select = st.getConnection().createStatement();
                            associations = select.executeQuery(sql);
                        }


                        try {
                            if (associations.next()) {
                                final String rtable = associations.getString("rtable");
                                final String rfid = associations.getString("rfid");

                                SimpleFeatureType associatedType = null;

                                try {
                                    associatedType = dataStore.getSchema(rtable);
                                } catch (IOException e) {
                                    //only log here, this means that the association
                                    // is probably bad... which we still want to
                                    // handle, and fail only when and if we actually
                                    // resolve the link
                                    String msg = "Could not load schema: " + rtable;
                                    dataStore.getLogger().log(Level.WARNING, msg, e);
                                }

                                // set the referenced id + typeName as user data
                                builder.userData("gml:id", rtable + "." + rfid);
                                builder.userData("gml:featureTypeName", rtable);

                                final FeatureTypeFactory tf = dataStore.getFeatureTypeFactory();

                                if (associatedType == null) {
                                    //means there was a problem with the link,
                                    // create a dummy type
                                    final SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder(tf);
                                    tb.setName(rtable);
                                    associatedType = tb.buildFeatureType();
                                }

                                //create an association
                                final AssociationType associationType = tf.createAssociationType(type
                                        .getName(), associatedType, false, Collections.EMPTY_LIST,
                                        null, null);
                                final AssociationDescriptor associationDescriptor = tf
                                    .createAssociationDescriptor(associationType, type.getName(),
                                        1, 1, true);

                                final FeatureFactory f = dataStore.getFeatureFactory();
                                final Association association = f.createAssociation(null,
                                        associationDescriptor);
                                association.getUserData().put("gml:id", rtable + "." + rfid);

                                if (resolve) {
                                    // use the value as an the identifier in a query against
                                    // the
                                    // referenced type
                                    final DefaultQuery query = new DefaultQuery(rtable);

                                    final Hints hints = new Hints(HintsPending.ASSOCIATION_TRAVERSAL_DEPTH,
                                            new Integer(depth.intValue() - 1));
                                   // query.setHints(hints);

                                    final FilterFactory ff = dataStore.getFilterFactory();
                                    final Id filter = ff.id(Collections.singleton(ff.featureId(
                                                    value.toString())));
                                    query.setFilter(filter);

                                    // grab a reader and get the feature, there should
                                    // only
                                    // be one
                                    final FeatureReader<SimpleFeatureType, SimpleFeature> r = dataStore.getFeatureReader(query, tx);
                                    try {
                                        r.hasNext();

                                        SimpleFeature associated = r.next();
                                        association.setValue(associated);
                                    } finally {
                                        r.close();
                                    }
                                }

                                // set the actual value to be the association
                                value = association;
                            }
                        } finally {
                            dataStore.closeSafe(associations);
                            dataStore.closeSafe(select);
                        }
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
                    throw new IOException(e);
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
    public void close() throws IOException {
        if ( dataStore != null ) {
            //clean up
            dataStore.closeSafe( rs );
            dataStore.closeSafe( st );

            dataStore.releaseConnection(cx, featureSource.getState() );
        }
        else {
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
        featureSource = null;
        featureType = null;
        geometryFactory = null;
        tx = null;
        hints = null;
        next = null;
        builder = null;
        tracer = null;
    }

    @Override
    protected void finalize() throws Throwable {
        if (dataStore != null) {
            LOGGER.warning("There is code leaving feature readers/iterators open, this is leaking statements and connections!");
            if(TRACE_ENABLED) {
                LOGGER.log(Level.WARNING, "The unclosed reader originated on this stack trace", tracer);
            }
            close();
        }
    }

    /**
     * Feature wrapper around a result set.
     */
    protected class ResultSetFeature implements SimpleFeature {
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
        PrimaryKey key;

        /**
         * updated values
         * */
        Object[] values;

        /**
         * fid
         */
        FeatureId fid;

        /**
         * dirty flags
         */
        boolean[] dirty;

        /**
         * Marks this feature as "new", about to be inserted
         */
        boolean newFeature;

        /**
         * name index
         */
        HashMap<String, Integer> index;
        /**
         * user data
         */
        HashMap<Object, Object> userData = new HashMap<Object, Object>();

        ResultSetFeature(final ResultSet rs, final Connection cx) throws SQLException, IOException {
            this.rs = rs;
            this.cx = cx;

            //get the result set metadata
            final ResultSetMetaData md = rs.getMetaData();

            //get the primary key, ensure its not contained in the values
            key = dataStore.getPrimaryKey(featureType);
            int count = md.getColumnCount();
            columnNames=new String[count];

            for (int i = 0; i < md.getColumnCount(); i++) {
            	String columnName =md.getColumnName(i + 1);
            	columnNames[i]=columnName;
                for ( PrimaryKeyColumn col : key.getColumns() ) {
                    if (col.getName().equals(columnName)) {
                        count--;
                        break;
                    }
                }

            }

            //set up values
            values = new Object[count];
            dirty = new boolean[values.length];

            //set up name lookup
            index = new HashMap<String, Integer>();

            int offset = 0;

         O: for (int i = 0; i < md.getColumnCount(); i++) {
                for( PrimaryKeyColumn col : key.getColumns() ) {
                    if ( col.getName().equals( md.getColumnName(i+1))) {
                        offset++;
                        continue O;
                    }
                }

                index.put(md.getColumnName(i + 1), i - offset);
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
            init(featureType.getTypeName() + "." + dataStore.encodeFID( key, rs ));
        }

        @Override
        public SimpleFeatureType getFeatureType() {
            return featureType;
        }

        @Override
        public SimpleFeatureType getType() {
            return featureType;
        }

        @Override
        public FeatureId getIdentifier() {
        	return fid;
        }
        @Override
        public String getID() {
            return fid.getID();
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
            return getAttribute(name.getLocalPart());
        }

        @Override
        public Object getAttribute(final int index) throws IndexOutOfBoundsException {
            return getAttributeInternal( index, mapToResultSetIndex(index) );
        }

        private int mapToResultSetIndex(final int index) {
            //map the index to result set
            int rsindex = index;

            for ( int i = 0; i <= index; i++ ) {
                for( PrimaryKeyColumn col : key.getColumns() ) {
                    if ( col.getName().equals( columnNames[i])) {
                        rsindex++;
                        break;
                    }
                }
            }

            rsindex++;
            return rsindex;
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
                                    values[index] = dataStore.getSQLDialect().decodeGeometryValue(
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
            setAttribute(name.getLocalPart(), value);
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
        public List<Object> getAttributes() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object getDefaultGeometry() {
            final GeometryDescriptor geomDesc = featureType.getGeometryDescriptor();
            return getAttribute(geomDesc.getName());
        }

        @Override
        public void setAttributes(Object[] object) {
            throw new UnsupportedOperationException();
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
        public GeometryAttribute getDefaultGeometryProperty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDefaultGeometryProperty(GeometryAttribute defaultGeometry) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Property> getProperties() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Property> getProperties(Name name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Property> getProperties(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Property getProperty(Name name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Property getProperty(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<?extends Property> getValue() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setValue(Collection<Property> value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public AttributeDescriptor getDescriptor() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Name getName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Map<Object, Object> getUserData() {
            return userData;
        }

        @Override
        public boolean isNillable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setValue(Object value) {
            throw new UnsupportedOperationException();
        }
        @Override
        public void validate() {
        }
    }

}
