/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
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
package org.geotoolkit.data;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureExt;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.feature.builder.PropertyTypeBuilder;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.memory.MemoryFeatureStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.internal.feature.AttributeConvention;
import static org.apache.sis.util.ArgumentChecks.*;
import org.geotoolkit.util.collection.CloseableIterator;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.data.memory.GenericMappingFeatureCollection;
import org.geotoolkit.data.memory.mapping.DefaultFeatureMapper;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.Operation;
import org.opengis.feature.PropertyNotFoundException;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.GenericName;

/**
 * Convinient methods to manipulate FeatureStore and FeatureCollection.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class FeatureStoreUtilities {

    static final Logger LOGGER = Logging.getLogger("org.geotoolkit.data");

    private FeatureStoreUtilities() {
    }

    public static FeatureCollection collection(final Feature ... features){
        final FeatureCollection col = collection("", features[0].getType());
        col.addAll(Arrays.asList(features));
        return col;
    }

    /**
     * Convinient method to create a featurecollection from a collection of features.
     * @param type
     * @param features
     * @return FeatureCollection
     */
    public static FeatureCollection collection(final FeatureType type, final Collection<? extends Feature> features){
        final FeatureCollection col = collection("", type);
        col.addAll(features);
        return col;
    }

    public static FeatureCollection collection(final String id, FeatureType type){
        if(type == null){
            //a collection with no defined type, make a generic abstract type
            //that is possible since feature collection may not always have a type.
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
            ftb.setName("null");
            ftb.setAbstract(true);
            type = ftb.build();
        }

        final MemoryFeatureStore ds = new MemoryFeatureStore(type, true);
        final Session session = ds.createSession(false);

        FeatureCollection col = session.getFeatureCollection(QueryBuilder.all(type.getName().toString()));
        ((AbstractFeatureCollection)col).setId(id);

        return col;
    }

    /**
     * Copy the features from the first collection to the second.
     * This method takes care of correctly closing interators if source collection
     * is a FeatureCollection.
     * @param source : source collection.
     * @param target : collection to copy features into.
     */
    public static Collection fill(final Collection source, final Collection target){
        if(target instanceof FeatureCollection){
            //we can safely use the addAll method.
            target.addAll(source);
        }else{
            //we are not sure that the given collection will take care of closing
            //the underlying iterator, we better do the iteration ourself.
            final Iterator ite = source.iterator();
            try{
                while(ite.hasNext()){
                    final Object f = ite.next();
                    target.add(f);
                }
            }finally{
                //todo must close safely both iterator
                if(ite instanceof Closeable){
                    try {
                        ((Closeable) ite).close();
                    } catch (IOException ex) {
                        throw new FeatureStoreRuntimeException(ex);
                    }
                }
            }
        }
        return target;
    }

    /**
     * Write the features from the given collection and return the list of generated FeatureID
     * send by the writer.
     *
     * @param writer, writer will not be closed
     * @param collection
     * @return List of generated FeatureId. Can be empty if output data type has
     * no identifier property.
     * @throws FeatureStoreRuntimeException
     */
    public static List<FeatureId> write(final FeatureWriter writer, final Collection<? extends Feature> collection)
            throws FeatureStoreRuntimeException {
        final List<FeatureId> ids = new ArrayList<>();
        boolean withId = false;
        // Check if there's identifiers to report.
        try {
            writer.getFeatureType().getProperty(AttributeConvention.IDENTIFIER_PROPERTY.toString());
            withId = true;
        } catch (PropertyNotFoundException e) {
            LOGGER.log(Level.FINE, "No identifier available at copy", e);
        }

        RuntimeException error = null;
        final Iterator<? extends Feature> ite = collection.iterator();
        try {
            while (ite.hasNext()) {
                final Feature f = ite.next();
                final Feature candidate = writer.next();
                FeatureExt.copy(f, candidate, false);
                writer.write();
                if (withId) {
                    ids.add(FeatureExt.getId(candidate));
                }
            }
        } catch (RuntimeException e) {
            error = e;
            throw error;
        } finally {
            if (ite instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) ite).close();
                } catch (Exception ex) {
                    if (error == null)
                        throw new FeatureStoreRuntimeException(ex);
                    else
                        error.addSuppressed(ex);
                }
            }
        }

        return ids;
    }

    /**
     * Iterate on the given iterator and calculate count.
     * @throws FeatureStoreRuntimeException
     */
    public static long calculateCount(final CloseableIterator reader) throws FeatureStoreRuntimeException{
        long count = 0;

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
     * Iterate on the given iterator and calculate the envelope.
     * @throws FeatureStoreRuntimeException
     */
    public static Envelope calculateEnvelope(final FeatureIterator iterator) throws FeatureStoreRuntimeException{
        ensureNonNull("iterator", iterator);

        GeneralEnvelope env = null;

        try{
            while(iterator.hasNext()){
                final Feature f = iterator.next();
                final Envelope bbox = FeatureExt.getEnvelope(f);
                if(bbox != null){
                    if(env != null){
                        env.add(bbox);
                    }else{
                        env = new GeneralEnvelope(bbox);
                    }
                }
            }
        }finally{
            iterator.close();
        }

        return env;
    }

    /**
     * Split the collection by geometry types.
     * Multiple feature store can only support a limited number of geometry types.
     * This method will split the content of the given collection in collections with a
     * simple geometry type.
     *
     * Collection datas are not copied, result collections are filtered collections
     *
     * @param col
     * @param geomClasses
     * @return splitted collections
     */
    public static FeatureCollection[] decomposeByGeometryType(FeatureCollection col, Class ... geomClasses) throws DataStoreException{
        return decomposeByGeometryType(col, FeatureExt.getDefaultGeometry(col.getType()).getName(), true, geomClasses);
    }

    /**
     * Split the collection by geometry types.
     * Multiple feature store can only support a limited number of geometry types.
     * This method will split the content of the given collection in collections with a
     * simple geometry type.
     *
     * Collection datas are not copied, result collections are filtered collections
     *
     * @param col
     * @param adaptType : ry to map types even if they do not match exactly.
     * list of adapt operations :
     * LineString -> MultiLineString
     * Polygon -> MultiPolygon
     * Point -> MultiPoint
     * @param geomClasses
     * @return splitted collections
     * @throws org.apache.sis.storage.DataStoreException
     */
    public static FeatureCollection[] decomposeByGeometryType(FeatureCollection col, GenericName geomPropName, boolean adaptType, Class ... geomClasses) throws DataStoreException{

        final FilterFactory FF = FactoryFinder.getFilterFactory(null);
        final FeatureType baseType = col.getType();
        final GenericName name = baseType.getName();
        final PropertyType geomDesc = baseType.getProperty(geomPropName.toString());
        boolean setDefaultGeometryRole = false;
        try {
            IdentifiedType defaultGeometry = baseType.getProperty(AttributeConvention.GEOMETRY_PROPERTY.toString());
            setDefaultGeometryRole = defaultGeometry.equals(geomDesc);
            while (setDefaultGeometryRole == false && defaultGeometry instanceof Operation) {
                defaultGeometry = ((Operation)defaultGeometry).getResult();
                setDefaultGeometryRole = defaultGeometry.equals(geomDesc);
            }
        } catch (PropertyNotFoundException e) {
            LOGGER.log(Level.FINEST, "No SIS convention found in given data type", e);
        }

        final CoordinateReferenceSystem crs = FeatureExt.getCRS(geomDesc);

        final List<Class> lstClasses = Arrays.asList(geomClasses);

        final FeatureCollection[] cols = new FeatureCollection[geomClasses.length];
        for(int i=0; i<geomClasses.length;i++){
            final Class geomClass = geomClasses[i];
            Filter filter = FF.equals(
                    FF.function("geometryType", FF.property(geomPropName.tip().toString())),
                    FF.literal(geomClass.getSimpleName()));

            //check if we need to map another type
            if(adaptType){
                if(geomClass == MultiPolygon.class && !lstClasses.contains(Polygon.class)){
                    filter = FF.or(filter,
                            FF.equals(
                                FF.function("geometryType", FF.property(geomPropName.tip().toString())),
                                FF.literal(Polygon.class.getSimpleName()))
                            );
                }else if(geomClass == MultiLineString.class && !lstClasses.contains(LineString.class)){
                    filter = FF.or(filter,
                            FF.equals(
                                FF.function("geometryType", FF.property(geomPropName.tip().toString())),
                                FF.literal(LineString.class.getSimpleName()))
                            );
                }else if(geomClass == MultiPoint.class && !lstClasses.contains(Point.class)){
                    filter = FF.or(filter,
                            FF.equals(
                                FF.function("geometryType", FF.property(geomPropName.tip().toString())),
                                FF.literal(Point.class.getSimpleName()))
                            );
                }
            }

            cols[i] = col.subCollection( QueryBuilder.filtered(name.toString(), filter) );

            //retype the collection
            final FeatureTypeBuilder ftb = new FeatureTypeBuilder(baseType);
            ftb.setName(NamesExt.create(NamesExt.getNamespace(name), name.tip().toString() + '_' + geomClass.getSimpleName()));
            PropertyTypeBuilder geometryBuilder = null;
            final Iterator<PropertyTypeBuilder> it = ftb.properties().iterator();
            while (geometryBuilder == null && it.hasNext()) {
                final PropertyTypeBuilder next = it.next();
                if (next.getName().equals(geomPropName)) {
                    geometryBuilder = next;
                    it.remove();
                }
            }
            final AttributeTypeBuilder geomAttr = ftb.addAttribute(geomClasses[i]).setCRS(crs);
            if (setDefaultGeometryRole)
                geomAttr.addRole(AttributeRole.DEFAULT_GEOMETRY);

            cols[i] = new GenericMappingFeatureCollection(cols[i],new DefaultFeatureMapper(baseType, ftb.build()));
        }

        return cols;
    }

}
