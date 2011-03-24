/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.process.vector.intersect;

import com.vividsolutions.jts.geom.Geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.geotoolkit.data.DataStoreRuntimeException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.memory.GenericFilterFeatureIterator;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.process.vector.VectorFeatureCollection;

import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

/**
 * FeatureCollection for Intersect process
 * @author Quentin Boileau
 * @module pending
 */
public class IntersectFeatureCollection extends VectorFeatureCollection {

    private final FeatureType newFeatureType;
    private final Geometry interGeom;
    private static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(
            new Hints(Hints.FILTER_FACTORY, FilterFactory2.class));

    /**
     * Connect to the original FeatureConnection with an intersection filter
     * @param originalFC FeatureCollection
     * @param interGeom
     */
    public IntersectFeatureCollection(final FeatureCollection<Feature> originalFC, final Geometry interGeom) {
        super(originalFC);
        this.interGeom = interGeom;
        this.newFeatureType = super.getFeatureType();

    }

    /**
     * Return the new FeatureType
     * @return FeatureType
     */
    @Override
    public FeatureType getFeatureType() {
        return newFeatureType;
    }

    /**
     * Return the new FeatureIterator that which apply intersection filter
     * @param hints
     * @return the FeatureIterator
     * @throws DataStoreRuntimeException
     */
    @Override
    public FeatureIterator<Feature> iterator(Hints hints) throws DataStoreRuntimeException {
        return (FeatureIterator<Feature>) GenericFilterFeatureIterator.wrap(getOriginalFeatureCollection().iterator(null), createFilter());
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected Feature modify(final Feature original) {
        return null;
    }

    /**
     * Create an intersect filter between the intersection Geometry
     * and feature geometries
     * @return the intersect filter
     */
    private Filter createFilter() {

        List<Filter> filterList = new ArrayList<Filter>();
        Collection<PropertyDescriptor> descList = this.newFeatureType.getDescriptors();
        Iterator<PropertyDescriptor> descIter = descList.iterator();

        while (descIter.hasNext()) {
            PropertyDescriptor property = descIter.next();
            if (property instanceof GeometryDescriptor) {

                final Filter filter = FF.intersects(FF.property(property.getName()), FF.literal(interGeom));
                filterList.add(filter);
            }
        }

        Filter resultFilter = FF.or(filterList);

        return resultFilter;
    }
}
