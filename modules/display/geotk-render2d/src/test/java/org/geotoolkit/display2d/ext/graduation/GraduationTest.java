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
package org.geotoolkit.display2d.ext.graduation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.feature.AttributeConvention;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.service.ViewDef;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.style.MutableStyle;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GraduationTest extends org.geotoolkit.test.TestBase {

    /**
     * Sanity test, only ensure the rendering is successfull without errors not the final result.
     */
    @Test
    public void renderGraduationTest() throws PortrayalException, FactoryException{

        final CoordinateReferenceSystem crs = CRS.forCode("EPSG:2154");

        final GraduationSymbolizer gs = new GraduationSymbolizer();
        final GraduationSymbolizer.Graduation gra = new GraduationSymbolizer.Graduation();
        gs.getGraduations().add(gra);

        final MutableStyle style = GO2Utilities.STYLE_FACTORY.style(gs);

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(String.class).setName("id").addRole(AttributeRole.IDENTIFIER_COMPONENT);
        ftb.addAttribute(LineString.class).setName("geom").setCRS(crs);
        final FeatureType type = ftb.build();

        final Feature f = type.newInstance();
        f.setPropertyValue("id", "id-0");
        f.setPropertyValue("geom", new GeometryFactory().createLineString(new Coordinate[]{new Coordinate(0, 0), new Coordinate(100, 0)}));


        final MapLayer layer = MapBuilder.createFeatureLayer(FeatureStoreUtilities.collection(f));
        layer.setStyle(style);
        final MapContext context = MapBuilder.createContext();
        context.layers().add(layer);

        final SceneDef sdef = new SceneDef(context);
        final CanvasDef cdef = new CanvasDef(new Dimension(100, 100), Color.darkGray);
        final ViewDef vdef = new ViewDef(CRS.getDomainOfValidity(crs));

        final BufferedImage img = DefaultPortrayalService.portray(cdef, sdef, vdef);
        Assert.assertNotNull(img);

    }

}
