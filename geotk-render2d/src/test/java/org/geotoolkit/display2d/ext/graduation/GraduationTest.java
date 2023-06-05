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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import jakarta.xml.bind.JAXBException;
import java.util.Arrays;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.internal.storage.MemoryFeatureSet;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.portrayal.MapLayers;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.nio.IOUtilities;
import org.geotoolkit.sld.xml.Specification;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.geotoolkit.style.MutableStyle;
import org.junit.Assert;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
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

        final LineString geom = org.geotoolkit.geometry.jts.JTS.getFactory().createLineString(new Coordinate[]{new Coordinate(0, 0), new Coordinate(100, 0)});
        geom.setUserData(crs);
        final Feature f = type.newInstance();
        f.setPropertyValue("id", "id-0");
        f.setPropertyValue("geom", geom);


        final MapLayer layer = MapBuilder.createLayer(new MemoryFeatureSet(null, type, Arrays.asList(f)));
        layer.setStyle(style);
        final MapLayers context = MapBuilder.createContext();
        context.getComponents().add(layer);

        final SceneDef sdef = new SceneDef(context);
        final CanvasDef cdef = new CanvasDef();
        cdef.setDimension(new Dimension(100, 100));
        cdef.setBackground(Color.darkGray);
        cdef.setEnvelope(CRS.getDomainOfValidity(crs));

        final RenderedImage img = DefaultPortrayalService.portray(cdef, sdef);
        Assert.assertNotNull(img);
    }

    /**
     * Test Jaxb xml support.
     */
    @Test
    public void testXml() throws JAXBException, IOException {
        final GraduationSymbolizer ps = new GraduationSymbolizer();
        final MutableStyle style = GO2Utilities.STYLE_FACTORY.style(ps);

        final Path path = Files.createTempFile("xml", ".xml");
        IOUtilities.deleteOnExit(path);
        new StyleXmlIO().writeStyle(path, style, Specification.StyledLayerDescriptor.V_1_1_0);
    }

}
