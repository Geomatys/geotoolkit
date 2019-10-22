/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.display2d.style;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.sis.measure.Units;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.storage.feature.FeatureStoreUtilities;
import org.geotoolkit.display.PortrayalException;
import org.geotoolkit.display2d.GO2Utilities;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.service.ViewDef;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.apache.sis.referencing.CRS;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import static org.geotoolkit.style.StyleConstants.*;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.style.Fill;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.Stroke;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class GeometryExpressionTest extends org.geotoolkit.test.TestBase {

    private static final FilterFactory2 FF = GO2Utilities.FILTER_FACTORY;
    private static final MutableStyleFactory SF = GO2Utilities.STYLE_FACTORY;

    /**
     * Test a buffer expression around geometry.
     */
    @Test
    public void bufferTest() throws PortrayalException, IOException{

        final CoordinateReferenceSystem crs = CommonCRS.WGS84.normalizedGeographic();

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(Point.class).setName("geom").setCRS(crs).addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType type = ftb.build();


        final Point point = GO2Utilities.JTS_FACTORY.createPoint(new Coordinate(0, 0));
        JTS.setCRS(point, crs);
        final Feature feature = type.newInstance();
        feature.setPropertyValue("geom", point);

        final Expression geomExp = FF.function("buffer", FF.property("geom"),FF.literal(10));
        final Fill fill = SF.fill(Color.RED);
        final PolygonSymbolizer symbolizer = SF.polygonSymbolizer(
                "", geomExp, DEFAULT_DESCRIPTION, DEFAULT_UOM, null, fill, DEFAULT_DISPLACEMENT, LITERAL_ZERO_FLOAT);
        final MutableStyle style = SF.style(symbolizer);

        final MapContext context = MapBuilder.createContext();
        final FeatureMapLayer fml = MapBuilder.createFeatureLayer(FeatureStoreUtilities.collection(feature), style);
        context.layers().add(fml);

        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, -20, +20);
        env.setRange(1, -20, +20);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(40, 40), Color.WHITE);
        final SceneDef sceneDef = new SceneDef(context);

        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);

        //we must obtain a red point of 10pixel width at image center
        final int red = Color.RED.getRGB();
        final int white = Color.WHITE.getRGB();

        Assert.assertEquals(white, image.getRGB(20, 9));
        Assert.assertEquals(red, image.getRGB(20, 11));
        Assert.assertEquals(red, image.getRGB(20, 20));
        Assert.assertEquals(red, image.getRGB(20, 29));
        Assert.assertEquals(white, image.getRGB(20, 31));

        Assert.assertEquals(white, image.getRGB(9, 20));
        Assert.assertEquals(red, image.getRGB(11, 20));
        Assert.assertEquals(red, image.getRGB(20, 20));
        Assert.assertEquals(red, image.getRGB(29, 20));
        Assert.assertEquals(white, image.getRGB(31, 20));

    }

    /**
     * Test a geo buffer expression around geometry.
     *
     * TODO : make a real test case
     */
    @Ignore
    @Test
    public void bufferGeoTest() throws Exception{

        final CoordinateReferenceSystem crs2154 = CRS.forCode("EPSG:2154");
        final CoordinateReferenceSystem crs3857 = CRS.forCode("EPSG:3857");

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(Point.class).setName("geom").setCRS(crs2154);
        final FeatureType type = ftb.build();


        final Point point = GO2Utilities.JTS_FACTORY.createPoint(new Coordinate(0, 0));
        JTS.setCRS(point, crs2154);
        final Feature feature = type.newInstance();
        feature.setPropertyValue("geom", point);

        final Expression geomExp = FF.function("bufferGeo", FF.property("geom"), FF.literal(100), FF.literal("m"));
        final Stroke stroke = SF.stroke(Color.BLACK,10);
        final Fill fill = SF.fill(Color.RED);
        final PolygonSymbolizer symbolizer = SF.polygonSymbolizer(
                "", geomExp, DEFAULT_DESCRIPTION, Units.METRE, stroke, fill, DEFAULT_DISPLACEMENT, LITERAL_ZERO_FLOAT);
        final MutableStyle style = SF.style(symbolizer);

        final MapContext context = MapBuilder.createContext();
        final FeatureMapLayer fml = MapBuilder.createFeatureLayer(FeatureStoreUtilities.collection(feature), style);
        context.layers().add(fml);

        final GeneralEnvelope env = new GeneralEnvelope(crs3857);
        final Point pt = (Point) JTS.transform(point, CRS.findOperation(crs2154, crs3857, null).getMathTransform());
        env.setRange(0, pt.getX()-500, pt.getX()+500);
        env.setRange(1, pt.getY()-500, pt.getY()+500);
        final ViewDef viewDef = new ViewDef(env);
        final CanvasDef canvasDef = new CanvasDef(new Dimension(100, 100), Color.WHITE);
        final SceneDef sceneDef = new SceneDef(context);

        final BufferedImage image = DefaultPortrayalService.portray(canvasDef, sceneDef, viewDef);
        ImageIO.write(image, "png", new File("test.png"));

        //we must obtain a red point of 20pixel width at image center
        final int red = Color.RED.getRGB();
        final int white = Color.WHITE.getRGB();

        Assert.assertEquals(white, image.getRGB(20, 9));
        Assert.assertEquals(red, image.getRGB(20, 11));
        Assert.assertEquals(red, image.getRGB(20, 20));
        Assert.assertEquals(red, image.getRGB(20, 29));
        Assert.assertEquals(white, image.getRGB(20, 31));

        Assert.assertEquals(white, image.getRGB(9, 20));
        Assert.assertEquals(red, image.getRGB(11, 20));
        Assert.assertEquals(red, image.getRGB(20, 20));
        Assert.assertEquals(red, image.getRGB(29, 20));
        Assert.assertEquals(white, image.getRGB(31, 20));

    }


}
