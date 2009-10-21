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
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.measure.unit.Unit;

import org.geotoolkit.style.function.Categorize;
import org.geotoolkit.style.function.Interpolate;
import org.geotoolkit.style.function.InterpolationPoint;
import org.geotoolkit.style.function.Method;
import org.geotoolkit.style.function.Mode;
import org.geotoolkit.style.function.ThreshholdsBelongTo;

import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.Id;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.metadata.citation.OnLineResource;
import org.opengis.style.AnchorPoint;
import org.opengis.style.ChannelSelection;
import org.opengis.style.ColorMap;
import org.opengis.style.ColorReplacement;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.Description;
import org.opengis.style.Displacement;
import org.opengis.style.ExternalGraphic;
import org.opengis.style.FeatureTypeStyle;
import org.opengis.style.Fill;
import org.opengis.style.Font;
import org.opengis.style.Graphic;
import org.opengis.style.GraphicFill;
import org.opengis.style.GraphicLegend;
import org.opengis.style.GraphicStroke;
import org.opengis.style.Halo;
import org.opengis.style.LabelPlacement;
import org.opengis.style.LinePlacement;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Mark;
import org.opengis.style.OverlapBehavior;
import org.opengis.style.PointPlacement;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.Rule;
import org.opengis.style.SelectedChannelType;
import org.opengis.style.SemanticType;
import org.opengis.style.ShadedRelief;
import org.opengis.style.Stroke;
import org.opengis.style.Symbolizer;
import org.opengis.style.TextSymbolizer;


/**
 * 
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public interface MutableStyleFactory extends org.opengis.style.StyleFactory {


    //TEMPORARY FIX ////////////////////////////////////////////////////////////

    LineSymbolizer lineSymbolizer(String name, String geometry,
            Description description, Unit<?> unit, Stroke stroke, Expression offset);

    PointSymbolizer pointSymbolizer(String name, String geometry,
            Description description, Unit<?> unit, Graphic graphic);

    PolygonSymbolizer polygonSymbolizer(String name, String geometry,
            Description description, Unit<?> unit, Stroke stroke, Fill fill,
            Displacement displacement, Expression offset);

    RasterSymbolizer rasterSymbolizer(String name, String geometry,
            Description description, Unit<?> unit, Expression opacity,
            ChannelSelection channelSelection,
            OverlapBehavior overlapsBehaviour, ColorMap colorMap,
            ContrastEnhancement contrast, ShadedRelief shaded,
            Symbolizer outline);

    TextSymbolizer textSymbolizer(String name, String geometry,
            Description description, Unit<?> unit, Expression label, Font font,
            LabelPlacement placement, Halo halo, Fill fill);
    
    /**
     * convert an awt color in to a literal expression representing the color
     *
     * @param color the color to encode
     * @return the expression
     */
    Literal literal(Color color);

    //--------------------------------------------------------------------------
    // Style creation methods --------------------------------------------------
    //--------------------------------------------------------------------------
            
    MutableStyle style();
    
    MutableStyle style(Symbolizer symbol);
    
    MutableStyle style(Symbolizer[] symbols);

    //change return type
    @Override
    MutableStyle style(String name, Description description, boolean isDefault,
            List<FeatureTypeStyle> featureTypeStyles,
            Symbolizer defaultSymbolizer);
    
    MutableFeatureTypeStyle featureTypeStyle();
    
    MutableFeatureTypeStyle featureTypeStyle(Symbolizer symbol);
    
    MutableFeatureTypeStyle featureTypeStyle(Symbolizer[] symbol);

    //change return type
    @Override
    MutableFeatureTypeStyle featureTypeStyle(String name,
            Description description, Id definedFor, Set<Name> featureTypeNames,
            Set<SemanticType> types, List<Rule> rules);
    
    MutableRule rule();
    
    MutableRule rule(Symbolizer symbol);
    
    MutableRule rule(Symbolizer[] symbol);

    //change return type
    @Override
    MutableRule rule(String name, Description description, GraphicLegend legend,
            double min, double max, List<Symbolizer> symbolizers, Filter filter);
    
    
    //--------------------------------------------------------------------------
    // Symbolizer creation methods ---------------------------------------------
    //--------------------------------------------------------------------------
        
    PointSymbolizer pointSymbolizer();
    
    PointSymbolizer pointSymbolizer(Graphic graphic, String geometryPropertyName);
    
    LineSymbolizer lineSymbolizer();
    
    LineSymbolizer lineSymbolizer(Stroke stroke, String geometryPropertyName);
    
    PolygonSymbolizer polygonSymbolizer();
    
    PolygonSymbolizer polygonSymbolizer(Stroke stroke, Fill fill,
        String geometryPropertyName);
    
    TextSymbolizer textSymbolizer();
    
    TextSymbolizer textSymbolizer(Fill fill, Font font, Halo halo,
        Expression label, LabelPlacement labelPlacement, String geometryPropertyName);
        
    RasterSymbolizer rasterSymbolizer();
    
    RasterSymbolizer rasterSymbolizer(String geometryPropertyName, Expression opacity,
        ChannelSelection channel, OverlapBehavior overlap, ColorMap colorMap, ContrastEnhancement ce,
        ShadedRelief relief, Symbolizer outline);
        
    
    //--------------------------------------------------------------------------
    // Underneath Immutable objects creation methods ---------------------------
    //--------------------------------------------------------------------------
    
    AnchorPoint     anchorPoint();
    
    AnchorPoint     anchorPoint(double x, double y);
    
    ColorMap colorMap();

    ColorMap colorMap(Function function);

    ColorReplacement colorReplacement(Function recode);

    ContrastEnhancement contrastEnhancement();

    ContrastEnhancement contrastEnhancement(Expression gammaValue);

    Description     description();
    
    Description     description(String title, String abs);
        
    Displacement    displacement();
    
    Displacement    displacement(double x, double y);

    ExternalGraphic externalGraphic(URL url, String format);

    ExternalGraphic externalGraphic(String uri, String format);

    Fill fill();

    Fill fill(Color color);

    Fill fill(Expression color);

    Fill fill(Expression color, Expression opacity);

    Font font();

    Font font(int size);

    Font font(Expression fontFamily, Expression fontStyle, Expression fontWeight,
        Expression fontSize);

    Graphic graphic();

    GraphicFill graphicFill(Graphic graphic);

    GraphicLegend graphicLegend(Graphic graphic);

    GraphicStroke graphicStroke(Graphic graphic);

    GraphicStroke graphicStroke(Graphic graphic, Expression gap, Expression initialGap);

    Halo halo(Color color, double width);

    LabelPlacement labelPlacement();

    LinePlacement linePlacement(Expression offset);
    
    Mark mark();

    Mark mark(Expression wellKnownName, Stroke stroke, Fill fill);

    OnLineResource onlineResource(URI uri);

    PointPlacement pointPlacement();

    SelectedChannelType selectedChannelType(String name, Expression gammaValue);

    ShadedRelief shadedRelief(Expression reliefFactor);

    Stroke stroke();

    Stroke stroke(Color color, double width);

    Stroke stroke(Color color, double width, float[] dashes);

    Stroke stroke(Expression color, Expression width);

    Stroke stroke(Expression color, Expression width, Expression opacity);
    
    Mark getCircleMark();

    Mark getXMark();

    Mark getStarMark();

    Mark getSquareMark();

    Mark getCrossMark();

    Mark getTriangleMark();
               
        
    //Functions-----------------------------------------------------------------
    public Categorize categorizeFunction(Expression lookup, Map<Expression,Expression> values,
            ThreshholdsBelongTo belongs, Literal fallback);

    public Interpolate interpolateFunction(Expression lookup, List<InterpolationPoint> values,
            Method method, Mode mode, Literal fallback);
    
    public InterpolationPoint interpolationPoint(Expression value, double data);
        
}
