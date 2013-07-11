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

import com.vividsolutions.jts.geom.Coordinate;
import java.awt.image.Raster;
import org.geotoolkit.style.MutableStyle;
import org.opengis.style.TextSymbolizer;
import org.opengis.style.Fill;
import org.opengis.style.Halo;
import java.awt.Color;
import org.opengis.style.LabelPlacement;
import org.opengis.style.Font;
import org.opengis.filter.FilterFactory;
import org.geotoolkit.factory.FactoryFinder;
import org.opengis.filter.expression.Expression;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.Arrays;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import org.geotoolkit.data.FeatureStoreUtilities;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.display2d.service.CanvasDef;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.SceneDef;
import org.geotoolkit.display2d.service.ViewDef;
import org.geotoolkit.factory.Hints;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableStyleFactory;
import org.junit.Test;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.style.Description;

import static org.junit.Assert.*;
import static org.geotoolkit.style.StyleConstants.*;

/**
 * Test that text symbolizer are properly rendered.
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class TextSymbolizerTest {
    
    private static final GeometryFactory GF = new GeometryFactory();
    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    protected static final FilterFactory FF = FactoryFinder.getFilterFactory(null);
    
    /**
     * Render a label at check it is correctly located in the image.
     */
    @Test
    public void pointLabelTest() throws Exception{
        
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("test");
        ftb.add("geom", Point.class, CRS.decode("CRS:84"));
        ftb.setDefaultGeometry("geom");
        final FeatureType type = ftb.buildFeatureType();
        final Feature feature = FeatureUtilities.defaultFeature(type, "1");
        feature.getProperty("geom").setValue(GF.createPoint(new Coordinate(0, 0)));
        
        final FeatureCollection collection = FeatureStoreUtilities.collection(feature);
        
        //text symbolizer style
        final String name = "mySymbol";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = NonSI.PIXEL;
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
        final FeatureMapLayer layer = MapBuilder.createFeatureLayer(collection, style);
        
        final MapContext context = MapBuilder.createContext();
        context.layers().add(layer);
        
        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("CRS:84"));
        env.setRange(0, -180, +180);
        env.setRange(1, -90, +90);
        
        final Hints hints = new Hints();
        hints.put(GO2Hints.KEY_COLOR_MODEL, ColorModel.getRGBdefault());
        
        final SceneDef scenedef = new SceneDef(context,hints);
        final ViewDef viewdef = new ViewDef(env);
        final CanvasDef canvasdef = new CanvasDef(new Dimension(360, 180), Color.WHITE);
        
        final BufferedImage buffer = DefaultPortrayalService.portray(canvasdef, scenedef, viewdef);
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
