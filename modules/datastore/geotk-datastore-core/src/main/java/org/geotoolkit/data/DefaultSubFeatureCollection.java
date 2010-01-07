/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

import java.io.IOException;
import java.util.Map;

import org.geotoolkit.data.memory.GenericEmptyFeatureIterator;
import org.geotoolkit.data.memory.GenericFilterFeatureIterator;
import org.geotoolkit.data.memory.GenericMaxFeatureIterator;
import org.geotoolkit.data.memory.GenericReprojectFeatureIterator;
import org.geotoolkit.data.memory.GenericRetypeFeatureIterator;
import org.geotoolkit.data.memory.GenericSortByFeatureIterator;
import org.geotoolkit.data.memory.GenericStartIndexFeatureIterator;
import org.geotoolkit.data.memory.GenericWrapFeatureIterator;
import org.geotoolkit.data.query.Query;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureTypeUtilities;
import org.geotoolkit.feature.SchemaException;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.sort.SortBy;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Encapsulate a FeatureCollection with a query.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class DefaultSubFeatureCollection<F extends Feature> extends AbstractFeatureCollection<F>  {

    private final FeatureCollection<F> original;
    private final Query query;

    public DefaultSubFeatureCollection(FeatureCollection<F> original, Query query) throws SchemaException{
        super(original.getID(),expectingType(original.getFeatureType(),query));
        this.original = original;
        this.query = query;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public FeatureIterator<F> iterator() throws DataStoreRuntimeException{
        FeatureIterator iterator = original.iterator();

        final Integer start = query.getStartIndex();
        final Integer max = query.getMaxFeatures();
        final Filter filter = query.getFilter();
        final Name[] properties = query.getPropertyNames();
        final SortBy[] sorts = query.getSortBy();
        final CoordinateReferenceSystem crs = query.getCoordinateSystemReproject();

        //we should take care of wrapping the reader in a correct order to avoid
        //unnecessary calculations. fast and reducing number wrapper should be placed first.
        //but we must not take misunderstanding assumptions neither.
        //exemple : filter is slower than startIndex and MaxFeature but must be placed before
        //          otherwise the result will be illogic.


        //wrap sort by ---------------------------------------------------------
        //This can be really expensive, and force the us to read the full iterator.
        //that may cause out of memory errors.
        if(sorts != null && sorts.length != 0){
            iterator = GenericSortByFeatureIterator.wrap(iterator, sorts);
        }

        //wrap filter ----------------------------------------------------------
        //we must keep the filter first since it impacts the start index and max feature
        if(filter != null && filter != Filter.INCLUDE){
            if(filter == Filter.EXCLUDE){
                //filter that exclude everything, use optimzed reader
                iterator = GenericEmptyFeatureIterator.createIterator();
            }else{
                iterator = GenericFilterFeatureIterator.wrap(iterator, filter);
            }
        }

        //wrap start index -----------------------------------------------------
        if(start != null && start > 0){
            iterator = GenericStartIndexFeatureIterator.wrap(iterator, start);
        }

        //wrap max -------------------------------------------------------------
        if(max != null){
            if(max == 0){
                //use an optimized reader
                iterator = GenericEmptyFeatureIterator.createIterator();
            }else{
                iterator = GenericMaxFeatureIterator.wrap(iterator, max);
            }
        }


        if(properties == null && crs == null){
            return iterator;
        }


        //change to a reader to get information about the type
        FeatureReader reader = GenericWrapFeatureIterator.wrapToReader(iterator, original.getFeatureType());

        //wrap properties ------------------------------------------------------
        if(properties != null){
            try{
                final FeatureType mask = FeatureTypeUtilities.createSubType((SimpleFeatureType) reader.getFeatureType(), properties);
                reader = GenericRetypeFeatureIterator.wrap(reader, mask);
            }catch(IOException ex){
                throw new DataStoreRuntimeException(ex);
            }
        }

        //wrap reprojection ----------------------------------------------------
        if(crs != null){
            try {
                reader = GenericReprojectFeatureIterator.wrap(reader, crs);
            } catch (FactoryException ex) {
                throw new DataStoreRuntimeException(ex);
            } catch (IOException ex) {
                throw new DataStoreRuntimeException(ex);
            }
        }

        return reader;

    }

    /**
     * Obtain the resulting featuretype that should be after the query.
     * 
     * @param type
     * @param query
     * @return FeatureType
     */
    private static FeatureType expectingType(FeatureType type, Query query) throws SchemaException{
        final Name[] properties = query.getPropertyNames();
        final CoordinateReferenceSystem crs = query.getCoordinateSystemReproject();

        if(properties != null){
            type = FeatureTypeUtilities.createSubType((SimpleFeatureType) type, properties);
        }

        if(crs != null){
            type = FeatureTypeUtilities.transform((SimpleFeatureType) type, crs);
        }

        return type;
    }

    @Override
    public boolean isWritable(){
        if(type.equals(original.getFeatureType())){
            return original.isWritable();
        }else{
            return false;
        }
    }

    @Override
    public void update(Filter filter, Map<? extends AttributeDescriptor, ? extends Object> values) throws DataStoreException {
        if(filter == Filter.INCLUDE){
            original.update(query.getFilter(),values);
        }else{
            original.update(FactoryFinder.getFilterFactory(null).and(query.getFilter(), filter),values);
        }
    }

    @Override
    public void remove(Filter filter) throws DataStoreException {
        if(filter == Filter.INCLUDE){
            original.remove(query.getFilter());
        }else{
            original.remove(FactoryFinder.getFilterFactory(null).and(query.getFilter(), filter));
        }
    }

    @Override
    public Session getSession() {
        return original.getSession();
    }

}
