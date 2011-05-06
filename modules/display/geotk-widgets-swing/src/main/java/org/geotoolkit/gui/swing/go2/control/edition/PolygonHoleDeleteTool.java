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

package org.geotoolkit.gui.swing.go2.control.edition;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import org.geotoolkit.gui.swing.go2.JMap2D;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.util.SimpleInternationalString;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;

/**
 * Edition tool to remove holes in polygons.
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class PolygonHoleDeleteTool extends AbstractEditionTool {

    public PolygonHoleDeleteTool() {
        super(1260,"polygonHoleDelete", MessageBundle.getI18NString("removeHole"),
             new SimpleInternationalString("Tool to remove polygon holes."), FeatureMapLayer.class);
    }

    @Override
    public boolean canHandle(final Object candidate) {
        if(!super.canHandle(candidate)){
            return false;
        }

        //check the geometry type is type Point
        final FeatureMapLayer layer = (FeatureMapLayer) candidate;
        final FeatureType ft = layer.getCollection().getFeatureType();
        final GeometryDescriptor desc = ft.getGeometryDescriptor();

        if(desc == null){
            return false;
        }

        return MultiPolygon.class.isAssignableFrom(desc.getType().getBinding()) ||
               Polygon.class.isAssignableFrom(desc.getType().getBinding());
    }

    @Override
    public EditionDelegate createDelegate(final JMap2D map, final Object candidate) {
        return new PolygonHoleDeleteDelegate(map, (FeatureMapLayer) candidate);
    }

}
