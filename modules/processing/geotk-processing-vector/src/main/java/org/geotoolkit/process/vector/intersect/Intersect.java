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

import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.parameter.Parameters;
import org.geotoolkit.process.AbstractProcess;
import org.geotoolkit.process.ProcessEvent;
import org.geotoolkit.process.vector.VectorDescriptor;

import org.opengis.feature.Feature;
import org.opengis.parameter.ParameterValueGroup;

/**
 * This process return all Features from a FeatureCollection that intersect a geometry.
 * @author Quentin Boileau
 * @module pending
 */
public class Intersect extends AbstractProcess {

    /**
     * Default constructor
     */
    public Intersect() {
        super(IntersectDescriptor.INSTANCE);
    }

    /**
     *  {@inheritDoc }
     */
    @Override
    public void run() {
        getMonitor().started(new ProcessEvent(this,0,null,null));
        final FeatureCollection<Feature> inputFeatureList = Parameters.value(IntersectDescriptor.FEATURE_IN, inputParameters);
        final Geometry interGeom = Parameters.value(IntersectDescriptor.GEOMETRY_IN, inputParameters);

        final FeatureCollection resultFeatureList = new IntersectFeatureCollection(inputFeatureList, interGeom);

        final ParameterValueGroup result = getOutput();
        result.parameter(VectorDescriptor.FEATURE_OUT.getName().getCode()).setValue(resultFeatureList);
        getMonitor().ended(new ProcessEvent(this,100,null,null));
    }
}
