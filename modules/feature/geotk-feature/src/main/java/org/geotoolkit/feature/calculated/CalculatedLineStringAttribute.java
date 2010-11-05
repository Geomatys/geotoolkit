/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.feature.calculated;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.feature.DefaultGeometryAttribute;

import org.opengis.feature.ComplexAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.Name;

/**
 * A calculated attribut that define a LineString geometry calculated
 * from other attributs of the feature.
 * For exemple : a boat that record it's position every hour.
 * each record is available in a 0-N complex attribut.
 * This class while extract each position and create a line as a new attribut.
 * Any change applied to the positions will be visible on the line.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class CalculatedLineStringAttribute extends DefaultGeometryAttribute {

    private static final GeometryFactory GF = new GeometryFactory();

    private final Name[] path;
    private ComplexAttribute related;

    /**
     *
     * @param desc : the descriptor of this attribute
     * @param path : a path of names to the property that holds Point geometries.
     */
    public CalculatedLineStringAttribute(GeometryDescriptor desc, Name ... path) {
        super(null, desc, null);
        this.path = path;
    }
    
    public void setRelated(ComplexAttribute relatedFeature) {
        this.related = relatedFeature;
    }

    public ComplexAttribute getRelated() {
        return related;
    }

    @Override
    public Object getValue() {
        final List<Coordinate> coords = new ArrayList<Coordinate>();
        explore(related,0,coords);
        return GF.createLineString(coords.toArray(new Coordinate[coords.size()]));
    }

    private void explore(ComplexAttribute att, int depth, List<Coordinate> coords){
        if(depth == path.length-1){
            //we are on the field that hold the geometry points
            for (final Property prop : att.getProperties(path[depth])) {
                coords.add(((Point) prop.getValue()).getCoordinate());
            }
        }else{
            //explore childs
            int d = depth+1;
            for (final Property prop : related.getProperties(path[depth])) {
                final ComplexAttribute child = (ComplexAttribute) prop;
                explore(child, d, coords);
            }
        }
    }

}
