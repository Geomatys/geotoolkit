/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012, Geomatys
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.util.Arrays;
import javax.measure.Unit;
import org.apache.sis.feature.builder.AttributeRole;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.measure.Units;
import org.apache.sis.portrayal.MapLayer;
import org.apache.sis.portrayal.MapLayers;
import org.apache.sis.referencing.CommonCRS;
import org.apache.sis.storage.FeatureSet;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.storage.memory.InMemoryFeatureSet;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;
import static org.geotoolkit.style.StyleConstants.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Expression;
import org.opengis.style.Description;
import org.opengis.style.Fill;
import org.opengis.style.Font;
import org.opengis.style.Halo;
import org.opengis.style.LabelPlacement;
import org.opengis.style.TextSymbolizer;

/**
 * Test that text symbolizer are properly rendered.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class TextSymbolizerTest extends org.geotoolkit.test.TestBase {

    private static final GeometryFactory GF = new GeometryFactory();
    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    protected static final FilterFactory FF = FilterUtilities.FF;

    /**
     * Render a label at check it is correctly located in the image.
     */
    @Test
    public void pointLabelTest() throws Exception{

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.addAttribute(Point.class).setName("geom").setCRS(CommonCRS.defaultGeographic()).addRole(AttributeRole.DEFAULT_GEOMETRY);
        final FeatureType type = ftb.build();
        final Feature feature = type.newInstance();
        feature.setPropertyValue("geom",GF.createPoint(new Coordinate(0, 0)));

        final FeatureSet collection = new InMemoryFeatureSet(type, Arrays.asList(feature));

        //text symbolizer style
        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;
        final Expression label = FF.literal("LABEL");
        final Font font = SF.font(
                FF.literal("Arial"),
                FONT_STYLE_ITALIC,
                FONT_WEIGHT_BOLD,
                FF.literal(14));
        final LabelPlacement placement = SF.pointPlacement();
        final Halo halo = SF.halo(Color.WHITE, 0);
        final Fill fill = SF.fill(Color.BLUE);

        final TextSymbolizer symbol = SF.textSymbolizer(name, geometry, desc, unit, label, font, placement, halo, fill);
        final MutableStyle style = SF.style(symbol);
        final MapLayer layer = MapBuilder.createLayer(collection);
        layer.setStyle(style);

        final MapLayers context = MapBuilder.createContext();
        context.getComponents().add(layer);

        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.defaultGeographic());
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);

        final Hints hints = new Hints();
        hints.put(GO2Hints.KEY_COLOR_MODEL, ColorModel.getRGBdefault());

        final SceneDef scenedef = new SceneDef(context, hints);
        final CanvasDef canvasdef = new CanvasDef(new Dimension(360, 180), env);
        canvasdef.setBackground(Color.WHITE);

        final BufferedImage buffer = DefaultPortrayalService.portray(canvasdef, scenedef);
        //ImageIO.write(buffer, "PNG", new File("test.png"));

        //we expect to have a blue label at the center of the image
        final int[] pixel = new int[4];
        final int[] blue = new int[]{0,0,255,255};

        final Raster raster = buffer.getData();
        boolean found = false;
        for(int x=160; x<200;x++){
            //should be exactly at the center
            raster.getPixel(x, 90, pixel);

            if(Arrays.equals(blue, pixel)){
                found = true;
            }
        }

        assertTrue("label not found",found);

    }


}
