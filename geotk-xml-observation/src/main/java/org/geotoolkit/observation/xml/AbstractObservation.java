/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.observation.xml;

import java.util.Date;
import java.util.List;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.BoundingShape;
import org.geotoolkit.gml.xml.FeatureProperty;
import org.geotoolkit.swe.xml.PhenomenonProperty;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.quality.Element;
import org.opengis.observation.Observation;
import org.opengis.observation.Phenomenon;
import org.opengis.observation.sampling.SamplingFeature;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalPrimitive;

/**
 *
 * @author Guilhem Legal (Geomatys).
 */
public interface AbstractObservation extends Observation {

    @Override
    Process getProcedure();

    String getId();

    BoundingShape getBoundedBy();

    void setId(final String id);

    void setName(final Identifier name);

    void setProcedure(final String procedureID);

    void setResult(final Object result);

    void emptySamplingTime();

    void setSamplingTimePeriod(final Period period);

    void extendSamplingTime(final Date newEndBound);

    void extendBoundingShape(final AbstractGeometry newGeom);

    PhenomenonProperty getPropertyObservedProperty();

    FeatureProperty getPropertyFeatureOfInterest();

    void setFullObservedProperty(Phenomenon observedProperty);

    void setFullFeatureOfInterest(SamplingFeature foi);

    AbstractObservation getTemporaryTemplate(final String temporaryName, TemporalPrimitive time);

    List<Element> getResultQuality();

    String getObservationType();
}
