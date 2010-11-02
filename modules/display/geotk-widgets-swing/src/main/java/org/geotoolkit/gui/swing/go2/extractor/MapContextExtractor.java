/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
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
package org.geotoolkit.gui.swing.go2.extractor;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.Component;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;
import javax.measure.unit.Unit;

import org.geotoolkit.display2d.primitive.ProjectedCoverage;
import org.geotoolkit.display2d.primitive.ProjectedFeature;
import org.geotoolkit.display2d.canvas.AbstractGraphicVisitor;
import org.geotoolkit.display2d.primitive.GraphicJ2D;
import org.geotoolkit.map.CoverageMapLayer;

import org.opengis.display.primitive.Graphic;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MapContextExtractor extends AbstractGraphicVisitor {

    private final List<String> descriptions = new ArrayList<String>();

    /**
     * {@inheritDoc }
     */
    @Override
    public void startVisit() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void endVisit() {
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public boolean isStopRequested() {
        return false;
    }

    public boolean valid(Graphic graphic) {
        if(graphic instanceof GraphicJ2D){
            GraphicJ2D gra = (GraphicJ2D) graphic;
            Object userObj = gra.getUserObject();

            if(userObj == null) return false;
            return (userObj instanceof Feature) || (userObj instanceof CoverageMapLayer) ;
        }else{
            return false;
        }

    }

    public List<String> getDescriptions() {
        final List<String> copy = new ArrayList<String>(descriptions);
        descriptions.clear();
        return copy;
    }

    public Component getComponent(Graphic graphic) {
        return null;
    }

    @Override
    public void visit(ProjectedFeature projectedFeature, Shape queryArea) {
        final Feature feature = (Feature) projectedFeature.getFeature();
        final StringBuilder builder = new StringBuilder();

        for(final Property prop : feature.getProperties()){
            if( Geometry.class.isAssignableFrom( prop.getType().getBinding() )){
                builder.append("<b>").append(prop.getName()).append(" : </b>").append(prop.getType().getBinding().getSimpleName()).append("<br>");
            }else{
                builder.append("<b>").append(prop.getName()).append(" : </b>").append(prop.getValue()).append("<br>");
            }
        }

        descriptions.add(builder.toString());
    }

    @Override
    public void visit(ProjectedCoverage projectedCoverage, Shape queryArea) {
        final Object[][] results = getCoverageValues(projectedCoverage, queryArea);

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < results.length; i++) {
            final Object value = results[i][0];
            final Unit unit = (Unit) results[i][1];
            if (value == null) {
                continue;
            }
            builder.append(value);
            if (unit != null) {
                builder.append(" ").append(unit.toString());
            }
        }
        
        descriptions.add(builder.toString());
    }

}
