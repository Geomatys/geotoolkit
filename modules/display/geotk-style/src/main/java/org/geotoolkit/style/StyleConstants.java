/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.style;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.util.SimpleInternationalString;

import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Literal;
import org.opengis.style.AnchorPoint;
import org.opengis.style.ChannelSelection;
import org.opengis.style.ColorMap;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.ContrastMethod;
import org.opengis.style.Description;
import org.opengis.style.Displacement;
import org.opengis.style.Fill;
import org.opengis.style.Font;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.Halo;
import org.opengis.style.LinePlacement;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Mark;
import org.opengis.style.OverlapBehavior;
import org.opengis.style.PointPlacement;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.SelectedChannelType;
import org.opengis.style.ShadedRelief;
import org.opengis.style.Stroke;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;

/**
 * Default values used for styles.
 * 
 * @author Johann Sorel (Geomatys)
 */
public final class StyleConstants {

    public static final Literal     DEFAULT_ANCHOR_POINT_X;
    public static final Literal     DEFAULT_ANCHOR_POINT_Y;
    public static final AnchorPoint DEFAULT_ANCHOR_POINT;
    
    public static final Literal             DEFAULT_CONTRAST_ENHANCEMENT_GAMMA;
    public static final ContrastEnhancement DEFAULT_CONTRAST_ENHANCEMENT;
    
    public static final Literal      DEFAULT_DISPLACEMENT_X;
    public static final Literal      DEFAULT_DISPLACEMENT_Y;
    public static final Displacement DEFAULT_DISPLACEMENT; 
    
    public static final Literal DEFAULT_FILL_COLOR;
    public static final Literal DEFAULT_FILL_OPACITY;
    public static final Fill    DEFAULT_FILL;
    
    public static final Literal STROKE_JOIN_MITRE;
    public static final Literal STROKE_JOIN_ROUND;
    public static final Literal STROKE_JOIN_BEVEL;
    public static final Literal STROKE_CAP_BUTT;
    public static final Literal STROKE_CAP_ROUND;
    public static final Literal STROKE_CAP_SQUARE;
    public static final Literal DEFAULT_STROKE_COLOR;
    public static final Literal DEFAULT_STROKE_OPACITY;
    public static final Literal DEFAULT_STROKE_WIDTH;
    public static final Literal DEFAULT_STROKE_JOIN;
    public static final Literal DEFAULT_STROKE_CAP;
    public static final Literal DEFAULT_STROKE_OFFSET;
    public static final Stroke     DEFAULT_STROKE;
    
    public static final Literal FONT_STYLE_NORMAL;
    public static final Literal FONT_STYLE_ITALIC;
    public static final Literal FONT_STYLE_OBLIQUE;
    public static final Literal FONT_WEIGHT_NORMAL;
    public static final Literal FONT_WEIGHT_BOLD;
    public static final Literal DEFAULT_FONT_STYLE;
    public static final Literal DEFAULT_FONT_WEIGHT;
    public static final Literal DEFAULT_FONT_SIZE;
    public static final Font    DEFAULT_FONT;
    
    public static final Literal DEFAULT_GRAPHIC_OPACITY;
    public static final Literal DEFAULT_GRAPHIC_ROTATION;
    public static final Literal DEFAULT_GRAPHIC_SIZE;
    public static final Graphic DEFAULT_GRAPHIC;
    public static final Mark    DEFAULT_GRAPHICAL_SYMBOL;
    public static final Literal DEFAULT_GRAPHIC_STROKE_INITIAL_GAP;
    public static final Literal DEFAULT_GRAPHIC_STROKE_GAP;
            
    public static final Fill       DEFAULT_HALO_FILL;
    public static final Literal    DEFAULT_HALO_RADIUS;
    public static final Halo       DEFAULT_HALO;
    
    public static final Literal       DEFAULT_LINEPLACEMENT_OFFSET;
    public static final Literal       DEFAULT_LINEPLACEMENT_INITIAL_GAP;
    public static final Literal       DEFAULT_LINEPLACEMENT_GAP;
    public static final boolean       DEFAULT_LINEPLACEMENT_ALIGNED;
    public static final boolean       DEFAULT_LINEPLACEMENT_REPEATED;
    public static final boolean       DEFAULT_LINEPLACEMENT_GENERALIZE;
    public static final LinePlacement DEFAULT_LINEPLACEMENT;
    
    public static final Literal     DEFAULT_POINTPLACEMENT_ROTATION;
    public static final PointPlacement DEFAULT_POINTPLACEMENT;
        
    public static final Literal MARK_SQUARE;
    public static final Literal MARK_CIRCLE;
    public static final Literal MARK_TRIANGLE;
    public static final Literal MARK_STAR;
    public static final Literal MARK_CROSS;
    public static final Literal MARK_X;
    public static final Literal DEFAULT_MARK_WKN;
    
    public static final Literal   DEFAULT_SHADED_RELIEF_FACTOR;
    public static final ShadedRelief DEFAULT_SHADED_RELIEF;
    
    public static final Literal DEFAULT_LINE_OFFSET;
    
    public static final Literal DEFAULT_POLYGON_OFFSET;
    
    public static final Literal       DEFAULT_RASTER_OPACITY;
    public static final ChannelSelection DEFAULT_RASTER_CHANNEL_RGB;
    public static final ChannelSelection DEFAULT_RASTER_CHANNEL_GRAY;
    public static final OverlapBehavior  DEFAULT_RASTER_OVERLAP;
    public static final ColorMap         DEFAULT_RASTER_COLORMAP;
    public static final Symbolizer       DEFAULT_RASTER_OUTLINE;
    
    public static final Literal     DEFAULT_FALLBACK;
    public static final Literal     DEFAULT_CATEGORIZE_LOOKUP;
    public static final Literal     CATEGORIZE_LESS_INFINITY;
    
    public static final Unit        DEFAULT_UOM;
    public static final String      DEFAULT_GEOM;
    public static final Description DEFAULT_DESCRIPTION;
    
    public static final PointSymbolizer     DEFAULT_POINT_SYMBOLIZER;
    public static final LineSymbolizer      DEFAULT_LINE_SYMBOLIZER;
    public static final PolygonSymbolizer   DEFAULT_POLYGON_SYMBOLIZER;
    public static final TextSymbolizer      DEFAULT_TEXT_SYMBOLIZER;
    public static final RasterSymbolizer    DEFAULT_RASTER_SYMBOLIZER;
    
    static {
        final MutableStyleFactory SF = new DefaultStyleFactory();
        final FilterFactory FF = FactoryFinder.getFilterFactory(null);
        final Literal zeroExp = FF.literal(0);
        final Literal halfExp = FF.literal(0.5f);
        final Literal oneExp = FF.literal(1);
        
        DEFAULT_UOM = NonSI.PIXEL;
        DEFAULT_GEOM = null;        
        DEFAULT_DESCRIPTION = new DefaultDescription(
                new SimpleInternationalString("Title"), 
                new SimpleInternationalString("Description"));
        
        DEFAULT_ANCHOR_POINT_X = halfExp;
        DEFAULT_ANCHOR_POINT_Y = halfExp;
        DEFAULT_ANCHOR_POINT = new DefaultAnchorPoint(DEFAULT_ANCHOR_POINT_X,DEFAULT_ANCHOR_POINT_Y);
        
        DEFAULT_CONTRAST_ENHANCEMENT_GAMMA = oneExp;
        DEFAULT_CONTRAST_ENHANCEMENT = new DefaultContrastEnhancement(ContrastMethod.NONE,oneExp);
        
        DEFAULT_DISPLACEMENT_X = zeroExp;
        DEFAULT_DISPLACEMENT_Y = zeroExp;
        DEFAULT_DISPLACEMENT = new DefaultDisplacement(DEFAULT_DISPLACEMENT_X,DEFAULT_DISPLACEMENT_Y);
        
        DEFAULT_FILL_COLOR = SF.literal(Color.GRAY);
        DEFAULT_FILL_OPACITY = oneExp;
        DEFAULT_FILL = new DefaultFill(null, DEFAULT_FILL_COLOR, DEFAULT_FILL_OPACITY);
        
        STROKE_JOIN_MITRE = FF.literal("mitre");
        STROKE_JOIN_ROUND = FF.literal("round");
        STROKE_JOIN_BEVEL = FF.literal("bevel");
        STROKE_CAP_BUTT = FF.literal("butt");
        STROKE_CAP_ROUND = FF.literal("round");
        STROKE_CAP_SQUARE = FF.literal("square");
        DEFAULT_STROKE_COLOR = SF.literal(Color.BLACK);
        DEFAULT_STROKE_OPACITY = oneExp;
        DEFAULT_STROKE_WIDTH = oneExp;
        DEFAULT_STROKE_JOIN = STROKE_JOIN_BEVEL;
        DEFAULT_STROKE_CAP = STROKE_CAP_SQUARE;
        DEFAULT_STROKE_OFFSET = zeroExp;
        DEFAULT_STROKE = new DefaultStroke(DEFAULT_STROKE_COLOR, DEFAULT_STROKE_OPACITY, 
                DEFAULT_STROKE_WIDTH, DEFAULT_STROKE_JOIN, DEFAULT_STROKE_CAP, null, DEFAULT_STROKE_OFFSET);
                
        FONT_STYLE_NORMAL = FF.literal("normal");
        FONT_STYLE_ITALIC = FF.literal("italic");
        FONT_STYLE_OBLIQUE = FF.literal("oblique");
        FONT_WEIGHT_NORMAL = FF.literal("normal");
        FONT_WEIGHT_BOLD = FF.literal("bold");
        DEFAULT_FONT_STYLE = FONT_STYLE_NORMAL;
        DEFAULT_FONT_WEIGHT = FONT_WEIGHT_NORMAL;
        DEFAULT_FONT_SIZE = FF.literal(10);
        DEFAULT_FONT = new DefaultFont(null, DEFAULT_FONT_STYLE, DEFAULT_FONT_WEIGHT, DEFAULT_FONT_SIZE);
        
        DEFAULT_HALO_RADIUS = oneExp;
        DEFAULT_HALO_FILL = new DefaultFill(null, SF.literal(Color.WHITE), DEFAULT_FILL_OPACITY);
        DEFAULT_HALO = new DefaultHalo(DEFAULT_HALO_FILL, DEFAULT_HALO_RADIUS);
        
        MARK_SQUARE = FF.literal("square");
        MARK_CIRCLE = FF.literal("circle");
        MARK_TRIANGLE = FF.literal("triangle");
        MARK_STAR = FF.literal("star");
        MARK_CROSS = FF.literal("cross");
        MARK_X = FF.literal("x");
        DEFAULT_MARK_WKN = MARK_SQUARE;
        
        DEFAULT_GRAPHICAL_SYMBOL = new DefaultMark(MARK_SQUARE, DEFAULT_FILL, DEFAULT_STROKE);
        DEFAULT_GRAPHIC_STROKE_INITIAL_GAP = FF.literal(zeroExp);
        DEFAULT_GRAPHIC_STROKE_GAP = FF.literal(zeroExp);
                
        DEFAULT_GRAPHIC_OPACITY = oneExp;
        DEFAULT_GRAPHIC_ROTATION = zeroExp;
        DEFAULT_GRAPHIC_SIZE = FF.literal(6);
        
        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        symbols.add(DEFAULT_GRAPHICAL_SYMBOL);
        DEFAULT_GRAPHIC = new DefaultGraphic(symbols, DEFAULT_GRAPHIC_OPACITY, 
                DEFAULT_GRAPHIC_SIZE, DEFAULT_GRAPHIC_ROTATION, DEFAULT_ANCHOR_POINT, DEFAULT_DISPLACEMENT);
        
        DEFAULT_LINEPLACEMENT_OFFSET = zeroExp;
        DEFAULT_LINEPLACEMENT_INITIAL_GAP = zeroExp;
        DEFAULT_LINEPLACEMENT_GAP = zeroExp;
        DEFAULT_LINEPLACEMENT_ALIGNED = true;
        DEFAULT_LINEPLACEMENT_REPEATED = false;
        DEFAULT_LINEPLACEMENT_GENERALIZE = false;
        DEFAULT_LINEPLACEMENT = new DefaultLinePlacement(DEFAULT_LINEPLACEMENT_OFFSET, 
                DEFAULT_LINEPLACEMENT_INITIAL_GAP, DEFAULT_LINEPLACEMENT_GAP, DEFAULT_LINEPLACEMENT_REPEATED, 
                DEFAULT_LINEPLACEMENT_ALIGNED, DEFAULT_LINEPLACEMENT_GENERALIZE);
        
        DEFAULT_POINTPLACEMENT_ROTATION = zeroExp;
        DEFAULT_POINTPLACEMENT = new DefaultPointPlacement(DEFAULT_ANCHOR_POINT, 
                DEFAULT_DISPLACEMENT, DEFAULT_POINTPLACEMENT_ROTATION);
                
        DEFAULT_SHADED_RELIEF_FACTOR = FF.literal(0);
        DEFAULT_SHADED_RELIEF = new DefaultShadedRelief(false, DEFAULT_SHADED_RELIEF_FACTOR);
        
        DEFAULT_LINE_OFFSET = zeroExp;
        
        DEFAULT_POLYGON_OFFSET = zeroExp;
        
        DEFAULT_RASTER_OPACITY = oneExp;
        final SelectedChannelType red = new DefaultSelectedChannelType("0", DEFAULT_CONTRAST_ENHANCEMENT);
        final SelectedChannelType green = new DefaultSelectedChannelType("1", DEFAULT_CONTRAST_ENHANCEMENT);
        final SelectedChannelType blue = new DefaultSelectedChannelType("2", DEFAULT_CONTRAST_ENHANCEMENT);
        DEFAULT_RASTER_CHANNEL_RGB = new DefaultChannelSelection(red,green,blue);
        final SelectedChannelType gray = new DefaultSelectedChannelType("0", DEFAULT_CONTRAST_ENHANCEMENT);
        DEFAULT_RASTER_CHANNEL_GRAY = new DefaultChannelSelection(gray);
        DEFAULT_RASTER_OVERLAP = OverlapBehavior.LATEST_ON_TOP;
        DEFAULT_RASTER_COLORMAP = new DefaultColorMap( null);
        DEFAULT_RASTER_OUTLINE = null;
        
        DEFAULT_FALLBACK = SF.literal(Color.RED);
        DEFAULT_CATEGORIZE_LOOKUP = FF.literal("RASTER_DATA");
        CATEGORIZE_LESS_INFINITY = FF.literal("CATEGORIZE_LESS_INFINITY");
        
        DEFAULT_POINT_SYMBOLIZER = new DefaultPointSymbolizer(
                DEFAULT_GRAPHIC, 
                DEFAULT_UOM, 
                null, 
                null, 
                DEFAULT_DESCRIPTION);
        
        DEFAULT_LINE_SYMBOLIZER = new DefaultLineSymbolizer(
                DEFAULT_STROKE, 
                DEFAULT_STROKE_WIDTH, 
                DEFAULT_UOM, 
                null, 
                null, 
                DEFAULT_DESCRIPTION);
        
        DEFAULT_POLYGON_SYMBOLIZER = new DefaultPolygonSymbolizer(
                DEFAULT_STROKE, 
                DEFAULT_FILL, 
                DEFAULT_DISPLACEMENT, 
                DEFAULT_POLYGON_OFFSET, 
                DEFAULT_UOM, 
                null, 
                null, 
                DEFAULT_DESCRIPTION);
        
        DEFAULT_TEXT_SYMBOLIZER = new DefaultTextSymbolizer(
                FF.literal("Label"),
                DEFAULT_FONT, 
                DEFAULT_POINTPLACEMENT, 
                DEFAULT_HALO, 
                SF.fill(Color.BLACK),
                DEFAULT_UOM, 
                null, 
                null, 
                DEFAULT_DESCRIPTION);
    
        DEFAULT_RASTER_SYMBOLIZER = new DefaultRasterSymbolizer(
                DEFAULT_RASTER_OPACITY,
                DEFAULT_RASTER_CHANNEL_RGB,
                DEFAULT_RASTER_OVERLAP,
                DEFAULT_RASTER_COLORMAP,
                DEFAULT_CONTRAST_ENHANCEMENT,
                DEFAULT_SHADED_RELIEF,
                DEFAULT_RASTER_OUTLINE,
                DEFAULT_UOM,
                null,
                null,
                DEFAULT_DESCRIPTION);
        
    }
    
}
