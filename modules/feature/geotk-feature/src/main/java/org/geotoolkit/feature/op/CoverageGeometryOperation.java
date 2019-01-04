/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2018, Geomatys
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
package org.geotoolkit.feature.op;

import org.locationtech.jts.geom.Geometry;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.apache.sis.feature.AbstractOperation;
import org.apache.sis.feature.DefaultAttributeType;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.geometry.GeometricUtilities;
import org.geotoolkit.geometry.GeometricUtilities.WrapResolution;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.util.NamesExt;
import org.geotoolkit.coverage.Coverage;
import org.opengis.feature.Attribute;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureOperationException;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.Property;
import org.opengis.geometry.Envelope;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class CoverageGeometryOperation extends AbstractOperation {

    private static final ParameterDescriptorGroup EMPTY_PARAMS = CalculateLineStringOperation.parameters("CoverageGeometry", 1);

    private static final AttributeType<Geometry> TYPE = new DefaultAttributeType<>(
            Collections.singletonMap(NAME_KEY, NamesExt.create("Contour")),Geometry.class,1,1,null);

    private final String referentName;

    public CoverageGeometryOperation(GenericName name, String propertyName) {
        this(Collections.singletonMap(DefaultAttributeType.NAME_KEY, name),propertyName);
    }

    public CoverageGeometryOperation(Map<String, ?> identification, String propertyName) {
        super(identification);
        this.referentName = propertyName;
    }

    @Override
    public ParameterDescriptorGroup getParameters() {
        return EMPTY_PARAMS;
    }

    @Override
    public IdentifiedType getResult() {
        return TYPE;
    }

    @Override
    public Set<String> getDependencies() {
        return Collections.singleton(referentName);
    }

    @Override
    public Property apply(Feature ftr, ParameterValueGroup pvg) throws FeatureOperationException {
        final Attribute<Geometry> att = TYPE.newInstance();
        final Object value = ftr.getPropertyValue(referentName);
        if (value instanceof Coverage) {
            final Envelope envelope = ((Coverage)value).getEnvelope();
            final Geometry geometry = GeometricUtilities.toJTSGeometry(envelope, WrapResolution.NONE);
            JTS.setCRS(geometry, CRS.getHorizontalComponent(envelope.getCoordinateReferenceSystem()));
            att.setValue(geometry);
        }
        return att;
    }

}
