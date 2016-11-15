/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2015, Geomatys
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
package org.apache.sis.feature.op;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.sis.feature.AbstractOperation;
import org.apache.sis.feature.DefaultAttributeType;
import org.geotoolkit.util.NamesExt;
import org.opengis.feature.Attribute;
import org.opengis.feature.AttributeType;
import org.opengis.feature.Feature;
import org.opengis.feature.IdentifiedType;
import org.opengis.feature.Property;
import org.opengis.metadata.Identifier;
import org.opengis.parameter.ParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.util.GenericName;
import org.apache.sis.metadata.iso.citation.Citations;
import org.apache.sis.parameter.DefaultParameterDescriptorGroup;

/**
 * A calculated attribut that define a LineString geometry calculated
 * from other attributs of the feature.
 * For exemple : a boat that record it's position every hour.
 * each record is available in a 0-N complex attribut.
 * This class while extract each position and create a line as a new attribut.
 * Any change applied to the positions will be visible on the line.
 *
 * @author Johann Sorel (Geomatys)
 */
final class CalculateLineStringOperation extends AbstractOperation {
    /**
     * @todo this is a copy of {@code LinkOperation} package private method.
     */
    static ParameterDescriptorGroup parameters(final String name, final int minimumOccurs,
            final ParameterDescriptor<?>... parameters)
    {
        final Map<String,Object> properties = new HashMap<>(4);
        properties.put(ParameterDescriptorGroup.NAME_KEY, name);
        properties.put(Identifier.AUTHORITY_KEY, Citations.SIS);
        return new DefaultParameterDescriptorGroup(properties, minimumOccurs, 1);
    }

    private static final ParameterDescriptorGroup EMPTY_PARAMS = parameters("CalculateLineString", 1);

    private static final GeometryFactory GF = new GeometryFactory();
    private static final AttributeType<LineString> TYPE = new DefaultAttributeType<>(
            Collections.singletonMap(NAME_KEY, NamesExt.create("LineString")),LineString.class,1,1,null);

    private final GenericName[] path;

    public CalculateLineStringOperation(GenericName name, GenericName ... attributePath) {
        this(Collections.singletonMap(DefaultAttributeType.NAME_KEY, name),attributePath);
    }

    public CalculateLineStringOperation(Map<String, ?> identification, GenericName ... attributePath) {
        super(identification);
        this.path = attributePath;
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
    public Property apply(Feature feature, ParameterValueGroup parameters) {
        final List<Coordinate> coords = new ArrayList<>();
        explore(feature,0,coords);
        final LineString geom = GF.createLineString(coords.toArray(new Coordinate[coords.size()]));
        final Attribute<LineString> att = TYPE.newInstance();
        att.setValue(geom);
        return att;
    }

    private void explore(final Feature att, final int depth, final List<Coordinate> coords){
        if(depth == path.length-1){
            //we are on the field that hold the geometry points
            for (final Object propVal : asCollection(att, path[depth])) {
                coords.add(((Point)propVal).getCoordinate());
            }
        }else{
            //explore childs
            int d = depth+1;
            for (final Object prop : asCollection(att,path[depth])) {
                final Feature child = (Feature) prop;
                explore(child, d, coords);
            }
        }
    }

    private static Collection asCollection(Feature att, GenericName property){
        final Object value = att.getPropertyValue(property.toString());
        if(value == null) return Collections.EMPTY_LIST;
        if(value instanceof Collection) return (Collection) value;
        return Collections.singletonList(value);
    }

}
