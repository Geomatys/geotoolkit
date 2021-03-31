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
package org.geotoolkit.processing.vector.intersect;

import java.util.ArrayList;
import java.util.List;
import org.apache.sis.internal.feature.AttributeConvention;
import org.apache.sis.internal.storage.query.SimpleQuery;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.processing.vector.VectorDescriptor;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.FeatureType;
import org.opengis.feature.PropertyType;
import org.opengis.filter.Filter;
import org.geotoolkit.filter.FilterFactory2;
import org.geotoolkit.filter.FilterUtilities;
import org.opengis.parameter.ParameterValueGroup;

/**
 * This process return all Features from a FeatureCollection that intersect a geometry.
 * @author Quentin Boileau
 * @module
 */
public class IntersectProcess extends AbstractProcess {

    private static final FilterFactory2 FF = FilterUtilities.FF;

    /**
     * Default constructor
     */
    public IntersectProcess(final ParameterValueGroup input) {
        super(IntersectDescriptor.INSTANCE,input);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    protected void execute() throws ProcessException {
        try {
            final FeatureSet inputFeatureList = inputParameters.getValue(VectorDescriptor.FEATURESET_IN);
            final Geometry interGeom          = inputParameters.getValue(IntersectDescriptor.GEOMETRY_IN);

            Filter filter = createFilter(inputFeatureList.getType(), interGeom);
            final SimpleQuery query = new SimpleQuery();
            query.setFilter(filter);
            final FeatureSet resultFeatureList = inputFeatureList.subset(query);

            outputParameters.getOrCreate(VectorDescriptor.FEATURESET_OUT).setValue(resultFeatureList);
        } catch (DataStoreException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }


    /**
     * Create an intersect filter between the intersection Geometry
     * and feature geometries
     * @return the intersect filter
     */
    private Filter createFilter(FeatureType ft, final Geometry interGeom) {
        final List<Filter<Object>> filterList = new ArrayList<>();
        for (final PropertyType property : ft.getProperties(true)) {
            if (AttributeConvention.isGeometryAttribute(property)) {
                final Filter filter = FF.intersects(FF.property(property.getName()), FF.literal(interGeom));
                filterList.add(filter);
            }
        }
        return FF.or(filterList);
    }
}
