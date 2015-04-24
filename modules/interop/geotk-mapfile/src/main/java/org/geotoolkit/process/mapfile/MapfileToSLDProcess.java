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
package org.geotoolkit.process.mapfile;

import java.awt.Color;
import java.util.List;
import java.util.Collection;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import javax.xml.bind.JAXBException;
import javax.measure.Unit;

import org.geotoolkit.filter.DefaultFilterFactory2;
import org.geotoolkit.processing.AbstractProcess;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.geotoolkit.sld.DefaultSLDFactory;
import org.geotoolkit.sld.MutableNamedLayer;
import org.geotoolkit.sld.MutableSLDFactory;
import org.geotoolkit.sld.MutableStyledLayerDescriptor;
import org.geotoolkit.sld.xml.Specification.StyledLayerDescriptor;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.geotoolkit.style.DefaultStyleFactory;
import org.geotoolkit.style.MutableFeatureTypeStyle;
import org.geotoolkit.style.MutableRule;
import org.geotoolkit.style.MutableStyle;
import org.geotoolkit.style.MutableStyleFactory;

import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.style.PointSymbolizer;
import org.opengis.style.Graphic;
import org.opengis.style.Mark;
import org.opengis.style.GraphicalSymbol;
import org.opengis.style.AnchorPoint;
import org.opengis.style.TextSymbolizer;
import org.opengis.style.Font;
import org.opengis.style.Halo;
import org.opengis.style.LabelPlacement;
import org.opengis.style.Description;
import org.opengis.style.Displacement;
import org.opengis.style.Fill;
import org.opengis.style.PolygonSymbolizer;
import org.opengis.style.Stroke;
import org.opengis.style.LineSymbolizer;
import org.opengis.style.Symbolizer;
import org.opengis.filter.expression.Literal;
import org.opengis.parameter.ParameterValueGroup;
import org.apache.sis.measure.Units;

import static org.geotoolkit.process.mapfile.MapfileToSLDDescriptor.*;
import static org.geotoolkit.parameter.Parameters.*;
import static org.geotoolkit.process.mapfile.MapfileTypes.*;
import static org.geotoolkit.style.StyleConstants.*;
import org.opengis.feature.Feature;

/**
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MapfileToSLDProcess extends AbstractProcess{

    private static final MutableSLDFactory SLDF = new DefaultSLDFactory();
    private static final MutableStyleFactory SF = new DefaultStyleFactory();
    private static final FilterFactory FF = new DefaultFilterFactory2();

    private Feature mapfileFeature = null;

    public MapfileToSLDProcess(final ParameterValueGroup input){
        super(INSTANCE, input);
    }

    @Override
    protected void execute() throws ProcessException {

        final File mapfile  = value(IN_FILE, inputParameters);
        final File sldfile  = value(IN_OUTPUT, inputParameters);

        final MapfileReader reader = new MapfileReader();
        reader.setInput(mapfile);
        try {
            mapfileFeature = reader.read();
            final MutableStyledLayerDescriptor sld = SLDF.createSLD();

            //convert it
            convert(sld, mapfileFeature);

            //avoid memory leak
            mapfileFeature = null;

            //write the sld
            final StyleXmlIO utils = new StyleXmlIO();
            utils.writeSLD(sldfile, sld, StyledLayerDescriptor.V_1_1_0);
        } catch (IOException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        } catch (JAXBException ex) {
            throw new ProcessException(ex.getMessage(), this, ex);
        }
    }

    private void convert(final MutableStyledLayerDescriptor sld, final Feature feature) throws ProcessException{

        final Collection<Feature> layers = (Collection<Feature>) feature.getPropertyValue(MAP_LAYER.toString());

        for(final Feature mflayer : layers){
            //create an sld layer
            final MutableNamedLayer sldLayer = SLDF.createNamedLayer();
            sld.layers().add(sldLayer);

            final String name = String.valueOf(mflayer.getPropertyValue(LAYER_NAME.toString()));
            sldLayer.setName(name);
            sldLayer.setDescription(SF.description(name, name));

            //create the style
            final MutableStyle sldStyle = SF.style();
            sldLayer.styles().add(sldStyle);
            final MutableFeatureTypeStyle fts = SF.featureTypeStyle();
            sldStyle.featureTypeStyles().add(fts);

            final Double minscale = (Double)mflayer.getPropertyValue(LAYER_MINSCALEDENOM.toString());
            final Double maxscale = (Double)mflayer.getPropertyValue(LAYER_MAXSCALEDENOM.toString());
            final Collection<Feature> classes = (Collection<Feature>) mflayer.getPropertyValue(LAYER_CLASS.toString());

            for(final Feature clazz : classes){
                final MutableRule rule = createRule(mflayer, minscale, maxscale, clazz);
                fts.rules().add(rule);
            }

        }

    }


    private MutableRule createRule(final Feature mflayer,
            final Double minScale, final Double maxscale, final Feature clazz) throws ProcessException{

        //mapfile type is similar to se symbolizer type
        final String type = (String) mflayer.getPropertyValue(LAYER_TYPE.toString());
        final MutableRule rule = SF.rule();

        final StringBuilder name = new StringBuilder("[");
        if(minScale != null){
            rule.setMinScaleDenominator(minScale);
            name.append(minScale);
        }else{
            name.append(0);
        }
        name.append(" ↔ ");

        if(maxscale != null){
            rule.setMaxScaleDenominator(maxscale);
            name.append(maxscale);
        }else{
            name.append(Double.POSITIVE_INFINITY);
        }
        name.append("]");

        rule.setDescription(SF.description(name.toString(), name.toString()));

        // Class can act as filter, the classItem is the propertyname on which the class
        // Expression is evaluated
        final PropertyName classItem = (PropertyName) mflayer.getPropertyValue(LAYER_CLASSITEM.toString());
        final String classExpression = (String) clazz.getPropertyValue(CLASS_EXPRESSION.toString());
        if(classExpression != null){
            // equivalant to OGC filter : PropertyEquals(name,value)
            final Filter filter = toFilter(classItem, classExpression);
            rule.setFilter(filter);
        }else{
            //filter
            //not handle yet
            rule.setElseFilter(true);
        }

        final Collection<Feature> styles = (Collection<Feature>) clazz.getPropertyValue(CLASS_STYLE.toString());
        final Collection<Feature> labels = (Collection<Feature>) clazz.getPropertyValue(CLASS_LABEL.toString());

        for(final Feature style : styles){

            if("POLYGON".equalsIgnoreCase(type)){
                rule.symbolizers().addAll(createPolygonSymbolizer(style));
            }else if("LINE".equalsIgnoreCase(type)){
                rule.symbolizers().addAll(createLineSymbolizer(style));
            }else if("ANNOTATION".equalsIgnoreCase(type)){
                rule.symbolizers().addAll(createPointSymbolizer(style));
            }
        }

        for(final Feature label : labels){

            //this property contain the label to place in the text symbolizer
            Expression labelProp = (Expression) mflayer.getPropertyValue(LAYER_LABELITEM.toString());
            Expression labelOverride = (Expression) mflayer.getPropertyValue(CLASS_TEXT.toString());
            if(labelProp == null || labelOverride != null){
                //Class Text take priority over label item
                labelProp = labelOverride;
            }

            rule.symbolizers().addAll(createTextSymbolizer(labelProp,label));
        }

        return rule;
    }

    private List<Symbolizer> createPolygonSymbolizer(final Feature style){

        Expression expColor = (Expression) style.getPropertyValue(STYLE_COLOR.toString());
        Expression expOpacity = (Expression) style.getPropertyValue(STYLE_OPACITY.toString());

        if(expOpacity == null){
            expOpacity = DEFAULT_FILL_OPACITY;
        }else{
            //mapfile opacity is expressed in %, SE is in 0-1
            if(expOpacity instanceof Literal){
                Double d= expOpacity.evaluate(null, Double.class);
                d /= 100d;
                expOpacity = FF.literal(d);
            }else{
                expOpacity = FF.divide(expOpacity, FF.literal(100));
            }
        }
        if(expColor == null){
            expColor = DEFAULT_FILL_COLOR;
        }

        Fill fill = null;
        Stroke stroke = null;
        fill = SF.fill(expColor,expOpacity);

        final List<Symbolizer> symbolizers = new ArrayList<Symbolizer>();

        //general informations
        final String name = "";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;
        final Displacement disp = DEFAULT_DISPLACEMENT;
        final Expression offset = LITERAL_ZERO_FLOAT;

        //stroke element
        //final Expression color = SF.literal(Color.BLUE);
        //final Expression width = FF.literal(4);
        //final Expression opacity = LITERAL_ONE_FLOAT;
        //final Stroke stroke = SF.stroke(color,width,opacity);


        final PolygonSymbolizer symbolizer = SF.polygonSymbolizer(name,geometry,desc,unit,stroke,fill,disp,offset);
        symbolizers.add(symbolizer);

        return symbolizers;
    }

    private List<Symbolizer> createLineSymbolizer(final Feature style){

        Expression expColor = (Expression) style.getPropertyValue(STYLE_COLOR.toString());
        Expression expWidth = (Expression) style.getPropertyValue(STYLE_WIDTH.toString());
        Expression expOpacity = (Expression) style.getPropertyValue(STYLE_OPACITY.toString());
        float[] dashes = (float[]) style.getPropertyValue(STYLE_PATTERN.toString());
        Literal explinecap = (Literal) style.getPropertyValue(STYLE_LINECAP.toString());
        Literal explinejoin = (Literal) style.getPropertyValue(STYLE_LINEJOIN.toString());

        Expression expOutlineColor = (Expression) style.getPropertyValue(STYLE_OUTLINECOLOR.toString());
        Expression expOutlineWidth = (Expression) style.getPropertyValue(STYLE_OUTLINEWIDTH.toString());

        if(expOpacity == null){
            expOpacity = DEFAULT_STROKE_OPACITY;
        }else{
            //mapfile opacity is expressed in %, SE is in 0-1
            if(expOpacity instanceof Literal){
                Double d= expOpacity.evaluate(null, Double.class);
                d /= 100d;
                expOpacity = FF.literal(d);
            }else{
                expOpacity = FF.divide(expOpacity, FF.literal(100));
            }
        }
        if(expWidth == null){
            expWidth = DEFAULT_STROKE_WIDTH;
        }
        if(explinecap == null){
            explinecap = STROKE_CAP_ROUND;
        }
        if(explinejoin == null){
            explinejoin = DEFAULT_STROKE_JOIN;
        }else{
            //mapfile write 'miter' not 'mitre' like in sld/se
            if("miter".equalsIgnoreCase(String.valueOf(explinejoin.getValue()))){
                explinejoin = STROKE_JOIN_MITRE;
            }
        }


        final List<Symbolizer> symbolizers = new ArrayList<>();

        //Check if it's an outline
        //Mapfile outline , is similar to line symbolizer placed under the main one
        //this produce a line border effect
        if(expOutlineColor != null && expOutlineWidth != null){
            final Expression width = FF.add(expWidth, FF.multiply(expOutlineWidth,FF.literal(2)));
            final Stroke stroke = SF.stroke(expOutlineColor,expOpacity,width,explinejoin,explinecap,null,LITERAL_ZERO_FLOAT);
            final LineSymbolizer outline = SF.lineSymbolizer(
                    "",(String)null,DEFAULT_DESCRIPTION,Units.POINT,stroke,LITERAL_ZERO_FLOAT);
            symbolizers.add(outline);
        }

         if(expColor != null){
            //general informations
            final String name = "";
            final Description desc = DEFAULT_DESCRIPTION;
            final String geometry = null; //use the default geometry of the feature
            final Unit unit = Units.POINT;
            final Expression offset = LITERAL_ZERO_FLOAT;

            //the visual element
            final Expression dashOffset = LITERAL_ZERO_FLOAT;
            final Stroke stroke = SF.stroke(expColor,expOpacity,expWidth,explinejoin,explinecap,dashes,dashOffset);

            final LineSymbolizer symbolizer = SF.lineSymbolizer(name,geometry,desc,unit,stroke,offset);
            symbolizers.add(symbolizer);
        }

        return symbolizers;
    }

    private List<Symbolizer> createTextSymbolizer(final Expression label, final Feature lblStyle){

        Expression expLabelColor = (Expression) lblStyle.getPropertyValue(LABEL_COLOR.toString());
        Expression expLabelSize  = (Expression) lblStyle.getPropertyValue(LABEL_SIZE.toString());

        Expression expHaloColor = (Expression) lblStyle.getPropertyValue(LABEL_OUTLINECOLOR.toString());
        Integer valHaloWidth = (Integer) lblStyle.getPropertyValue(LABEL_OUTLINEWIDTH.toString());
        String valAngle = (String) lblStyle.getPropertyValue(LABEL_ANGLE.toString());

        if(expLabelColor == null){
            expLabelColor = SF.literal(Color.BLACK);
        }
        if(expLabelSize == null){
            expLabelSize = DEFAULT_FONT_SIZE;
        }
        if(expHaloColor == null){
            expHaloColor = SF.literal(Color.WHITE);
        }
        if(valHaloWidth == null){
            valHaloWidth = 0;
        }

        Expression expHaloWidth = FF.literal(valHaloWidth);

        final List<Symbolizer> symbolizers = new ArrayList<Symbolizer>();


        LabelPlacement placement = SF.pointPlacement();
        if(valAngle != null){
            if("FOLLOW".equalsIgnoreCase(valAngle) || "AUTO".equalsIgnoreCase(valAngle)){
                final Expression offset = FF.divide(expLabelSize, FF.literal(-2));
                final Expression initial = FF.literal(20);
                Expression gap = LITERAL_ZERO_FLOAT;
                boolean repeated = false;
                final boolean aligned = false;
                final boolean generalize = false;

                Integer minDistance = (Integer) lblStyle.getPropertyValue(LABEL_MINDISTANCE.toString());
                if(minDistance != null){
                    repeated = true;
                    gap = FF.literal(minDistance);
                }

                placement = SF.linePlacement(offset,initial,gap,repeated,aligned,generalize);
            }else{
                Expression rotation = LITERAL_ZERO_FLOAT;
                //try if it's a number
                try{
                    double d = Double.valueOf(valAngle);
                    rotation = FF.literal(d);
                }catch(Exception ex){
                }

                placement = SF.pointPlacement(DEFAULT_ANCHOR_POINT, DEFAULT_DISPLACEMENT, rotation);
            }
        }

        //general informations
        final String name = "";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;
        final Font font = SF.font(
                FF.literal("Arial"),
                FONT_STYLE_NORMAL,
                FONT_WEIGHT_NORMAL,
                expLabelSize);

        final Halo halo = SF.halo(SF.fill(expHaloColor), expHaloWidth);
        final Fill fill = SF.fill(expLabelColor);

        final TextSymbolizer symbol = SF.textSymbolizer(name, geometry, desc, unit, label, font, placement, halo, fill);
        symbolizers.add(symbol);

        return symbolizers;
    }

    private List<Symbolizer> createPointSymbolizer(final Feature style){

        final String symbolName = (String) style.getPropertyValue(STYLE_SYMBOL.toString());
        Expression expSize = (Expression) style.getPropertyValue(STYLE_SIZE.toString());
        Expression expOpacity = (Expression) style.getPropertyValue(STYLE_OPACITY.toString());
        Expression expFillColor = (Expression) style.getPropertyValue(STYLE_COLOR.toString());
        Expression expStrokeColor = (Expression) style.getPropertyValue(STYLE_OUTLINECOLOR.toString());
        Expression expStrokeWidth = (Expression) style.getPropertyValue(STYLE_WIDTH.toString());

        if(expFillColor == null){
            expFillColor = DEFAULT_FILL_COLOR;
        }
        if(expStrokeColor == null){
            expStrokeColor = DEFAULT_STROKE_COLOR;
        }
        if(expStrokeWidth == null){
            expStrokeWidth = FF.literal(0);
        }
        if(expOpacity == null){
            expOpacity = DEFAULT_GRAPHIC_OPACITY;
        }
        if(expSize == null){
            expSize = DEFAULT_GRAPHIC_SIZE;
        }

        final List<Symbolizer> symbolizers = new ArrayList<Symbolizer>();

        final Feature symbol = getSymbol(symbolName);
        if(symbol == null){
            //no symbol found for this name
            return symbolizers;
        }

        final Stroke stroke = SF.stroke(expStrokeColor, expStrokeWidth);
        final Fill fill = SF.fill(expFillColor);

        final String symbolTypeName = (String) symbol.getPropertyValue(SYMBOL_TYPE.toString());

        final Mark mark;
        if("ellipse".equals(symbolTypeName)){
            mark = SF.mark(MARK_CIRCLE, fill, stroke);
        }else if("hatch".equals(symbolTypeName)){
            //TODO
            mark = SF.mark(MARK_SQUARE, fill, stroke);
        }else if("pixmap".equals(symbolTypeName)){
            //TODO
            mark = SF.mark(MARK_SQUARE, fill, stroke);
        }else if("simple".equals(symbolTypeName)){
            //TODO
            mark = SF.mark(MARK_SQUARE, fill, stroke);
        }else if("truetype".equals(symbolTypeName)){
            //TODO
            mark = SF.mark(MARK_SQUARE, fill, stroke);
        }else if("vector".equals(symbolTypeName)){
            //TODO
            mark = SF.mark(MARK_SQUARE, fill, stroke);
        }else{
            //can not build symbol
            return symbolizers;
        }


        //general informations
        final String name = "";
        final Description desc = DEFAULT_DESCRIPTION;
        final String geometry = null; //use the default geometry of the feature
        final Unit unit = Units.POINT;

        //the visual element
        final Expression opacity = LITERAL_ONE_FLOAT;
        final Expression rotation = LITERAL_ZERO_FLOAT;
        final AnchorPoint anchor = DEFAULT_ANCHOR_POINT;
        final Displacement disp = DEFAULT_DISPLACEMENT;

        final List<GraphicalSymbol> symbols = new ArrayList<GraphicalSymbol>();
        symbols.add(mark);
        final Graphic graphic = SF.graphic(symbols, opacity, expSize, rotation, anchor, disp);

        final PointSymbolizer symbolizer = SF.pointSymbolizer(name,geometry,desc,unit, graphic);
        symbolizers.add(symbolizer);

        return symbolizers;
    }

    /**
     *
     * @param name : symbol name
     * @return the symbol which has the given name
     */
    private Feature getSymbol(final String name){

        if(name == null){
            return null;
        }

        final Collection<Feature> symbols = (Collection<Feature>) mapfileFeature.getPropertyValue(MAP_SYMBOL.toString());

        for(final Feature ca : symbols){
            if(name.equals(ca.getPropertyValue(SYMBOL_NAME.toString()))){
                return ca;
            }
        }

        return null;
    }

    private static Filter toFilter(final Expression ref, final String text) throws ProcessException{
        final ProcessDescriptor desc = MapfileFilterToOGCFilterDescriptor.INSTANCE;
        final ParameterValueGroup input = desc.getInputDescriptor().createValue();
        getOrCreate(MapfileFilterToOGCFilterDescriptor.IN_TEXT, input).setValue(text);
        getOrCreate(MapfileFilterToOGCFilterDescriptor.IN_REFERENCE, input).setValue(ref);

        final org.geotoolkit.process.Process process = desc.createProcess(input);
        final ParameterValueGroup output = process.call();
        final Filter result = (Filter) value(MapfileFilterToOGCFilterDescriptor.OUT_OGC, output);
        return result;
    }
}
