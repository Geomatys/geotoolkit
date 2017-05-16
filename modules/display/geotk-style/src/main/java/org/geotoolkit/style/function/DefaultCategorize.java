/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2010, Geomatys
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
package org.geotoolkit.style.function;

import java.awt.Color;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import javax.media.jai.ImageLayout;
import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;

import org.geotoolkit.filter.AbstractExpression;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import org.geotoolkit.style.StyleConstants;

import org.geotoolkit.image.color.ColorUtilities;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Literal;

import static org.geotoolkit.style.StyleConstants.*;
import org.opengis.feature.Feature;
import static org.opengis.filter.expression.Expression.*;

/**
 * Implementation of "Categorize" as a normal function.
 * <p>
 * This implementation is compatible with the Function
 * interface; the parameter list can be used to set the
 * threshold values etc...
 * <p>
 * This function expects:
 * <ol>
 * <li>PropertyName; use "Rasterdata" to indicate this is a color map
 * <li>Literal: lookup value
 * <li>Literal: value 0
 * <li>Literal: threshold 1
 * <li>Literal: value 1
 * <li>Literal: threshold 2
 * <li>Literal: value 2
 * <li>Literal: (Optional) succeeding or preceding
 * </ol>
 * In reality any expression will do.
 * @author Jody Garnett
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultCategorize extends AbstractExpression implements Categorize {

    private static final Object NEG_INF = StyleConstants.CATEGORIZE_LESS_INFINITY.getValue();
    private static final Comparator<Expression> COMPARATOR = new Comparator<Expression>() {

        @Override
        public int compare(Expression exp1, Expression exp2) {
            if(exp1 instanceof Literal && ((Literal)exp1).getValue().equals(NEG_INF)){
                //categorize less is always first
                return -1;
            }else if(exp2 instanceof Literal && ((Literal)exp2).getValue().equals(NEG_INF)){
                //categorize less is always first
                return +1;
            }else{
                final double d1 = exp1.evaluate(null, Double.class);
                final double d2 = exp2.evaluate(null, Double.class);

                //put NaN at the end
                if(Double.isNaN(d1)) return +1;
                if(Double.isNaN(d2)) return -1;

                final double diff = d1-d2;

                if(diff < 0){
                    return -1;
                }else if(diff > 0){
                    return +1;
                }else{
                    return 0;
                }
            }
        }
    };

    private final Expression lookup;
    private final TreeMap<Expression,Expression> values = new TreeMap<>(COMPARATOR);
    private final ThreshholdsBelongTo belongTo;
    private final Literal fallback;


    /**
     * Make the instance of FunctionName available in
     * a consistent spot.
     */
    public static final FunctionName NAME = new Name();

    /**
     * Describe how this function works.
     * (should be available via FactoryFinder lookup...)
     */
    public static class Name implements FunctionName {

        @Override
        public int getArgumentCount() {
            return 2; // indicating unbounded, 2 minimum
        }

        @Override
        public List<String> getArgumentNames() {
            return Arrays.asList(new String[]{
                        "LookupValue",
                        "Value",
                        "Threshold 1", "Value 1",
                        "Threshold 2", "Value 2",
                        "succeeding or preceding"
                    });
        }

        @Override
        public String getName() {
            return "Categorize";
        }
    };

    public DefaultCategorize(final Expression ... expressions){

        lookup = expressions[0];
        this.values.put(CATEGORIZE_LESS_INFINITY, expressions[1]);

        if(expressions.length%2 == 0){
            for(int i=2;i<expressions.length;i+=2){
                this.values.put(expressions[i], expressions[i+1]);
            }
            this.belongTo = ThreshholdsBelongTo.SUCCEEDING;

        }else{
            for(int i=2;i<expressions.length-1;i+=2){
                this.values.put(expressions[i], expressions[i+1]);
            }
            final ThreshholdsBelongTo to = ThreshholdsBelongTo.parse(expressions[expressions.length-1].evaluate(null, String.class));
            this.belongTo = (to==null) ? ThreshholdsBelongTo.SUCCEEDING : to;
        }

        this.fallback = DEFAULT_FALLBACK;

        if(this.values.keySet().iterator().next() != CATEGORIZE_LESS_INFINITY){
            throw new  IllegalArgumentException("Values must hold at least one key : CATEGORIZE_LESS_INFINITY");
        }

    }

    /**
     *
     * @param LookUpValue
     * @param values map with threadholds keys.
     * @param belongs
     * @param fallback
     */
    public DefaultCategorize(final Expression LookUpValue, final Map<Expression,Expression> values,
            final ThreshholdsBelongTo belongs, final Literal fallback){

        if(values == null || values.isEmpty()){
            throw new IllegalArgumentException("Values can't be empty");
        }

        this.lookup = (LookUpValue == null || LookUpValue == NIL) ?  DEFAULT_CATEGORIZE_LOOKUP : LookUpValue;
        this.values.putAll(values);
        this.belongTo = (belongs == null) ? ThreshholdsBelongTo.SUCCEEDING :belongs;
        this.fallback = (fallback == null) ? DEFAULT_FALLBACK : fallback;

        if(this.values.keySet().iterator().next() != CATEGORIZE_LESS_INFINITY){
            throw new  IllegalArgumentException("Values must hold at least one key : CATEGORIZE_LESS_INFINITY");
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Expression getLookupValue(){
        return lookup;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Map<Expression,Expression> getThresholds() {
        return Collections.unmodifiableMap(values);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public ThreshholdsBelongTo getBelongTo(){
        return belongTo;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String getName() {
        return NAME.getName();
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Expression> getParameters() {
        final List<Expression> params = new ArrayList<Expression>();
        params.add(lookup);
        int i=0;
        for(Entry<Expression,Expression> entry : values.entrySet()){
            if(i==0){
                params.add(entry.getValue());
            }else{
                params.add(entry.getKey());
                params.add(entry.getValue());
            }
            i++;
        }
        params.add(new DefaultLiteral(belongTo.name().toLowerCase()));
        return params;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object accept(final ExpressionVisitor visitor, final Object extraData) {
        return visitor.visit(this, extraData);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Object evaluate(final Object object) {
        return evaluate(object, Object.class);
    }

    @Override
    public Object evaluate(final Object object, final Class c) {

        final Object candidate;
        final Double value;
        if(object instanceof Feature){

            candidate = (Feature)object;
            value = lookup.evaluate(candidate,Double.class);
            final Expression exp = new DefaultLiteral<>(value);

            final boolean b = this.belongTo == ThreshholdsBelongTo.SUCCEEDING;

            final Expression closest = values.headMap(exp,!b).lastEntry().getValue();
            return closest.evaluate(candidate,c);

        } else if (object instanceof RenderedImage) {
            return evaluateImage((RenderedImage) object);
        }else if(object instanceof Number){
            candidate = null;
            value = ((Number)object).doubleValue();
        }else if(fallback!=null){
            return fallback.evaluate(object,c);
        }else{
            return null;
        }

        final Expression exp = new DefaultLiteral<>(value);

        final boolean b = this.belongTo == ThreshholdsBelongTo.SUCCEEDING;

        final Expression closest = values.headMap(exp,!b).lastEntry().getValue();
        return closest.evaluate(candidate,c);
    }

    /**
     * Recolor image
     * @param image
     * @return recolored image
     */
    private RenderedImage evaluateImage(final RenderedImage image) {
        final int visibleBand = CoverageUtilities.getVisibleBand(image);
        final ColorModel candidate = image.getColorModel();

        //TODO : this should be used when the index color model can not handle signed values
        //
        //final SampleModel sm = image.getSampleModel();
        //final int datatype = sm.getDataType();
        //if(datatype == DataBuffer.TYPE_SHORT){
        //    final ColorModel model = new CompatibleColorModel(16, function);
        //    final ImageLayout layout = new ImageLayout().setColorModel(model);
        //    return new NullOpImage(image, layout, null, OpImage.OP_COMPUTE_BOUND);
        //}


        /*
         * Extracts the ARGB codes from the ColorModel and invokes the
         * transformColormap(...) method.
         */
        final int[] ARGB;
        final ColorModel model;
        if (candidate instanceof IndexColorModel) {
            final IndexColorModel colors = (IndexColorModel) candidate;
            final int mapSize = colors.getMapSize();
            ARGB = new int[mapSize];
            colors.getRGBs(ARGB);

            transformColormap(ARGB);
            model = ColorUtilities.getIndexColorModel(ARGB, 1, visibleBand, -1);

        } else if (candidate instanceof ComponentColorModel) {
            final ComponentColorModel colors = (ComponentColorModel) candidate;
            final int nbbit = colors.getPixelSize();
            final int type = image.getSampleModel().getDataType();

            if (type == DataBuffer.TYPE_BYTE || type == DataBuffer.TYPE_USHORT) {
                final int mapSize = 1 << nbbit;
                ARGB = new int[mapSize];

                for (int j = 0; j < mapSize; j++) {
                    int v = j * 255 / mapSize;
                    int a = 255 << 24;
                    int r = v << 16;
                    int g = v << 8;
                    int b = v << 0;
                    ARGB[j] = a | r | g | b;
                }

                transformColormap(ARGB);
                model = ColorUtilities.getIndexColorModel(ARGB, 1, visibleBand, -1);

            } else {
                //we can't handle a index color model when values exceed int max value
                model = new CompatibleColorModel(nbbit, this);
            }

        }else if(candidate instanceof DirectColorModel) {
            final DirectColorModel colors = (DirectColorModel) candidate;
            final int nbbit = colors.getPixelSize();
            final int type = image.getSampleModel().getDataType();

            if(type == DataBuffer.TYPE_BYTE || type == DataBuffer.TYPE_USHORT){
                final int mapSize = 1 << nbbit;
                ARGB = new int[mapSize];

                for(int j=0; j<mapSize;j++){
                    int v = j*255/mapSize;
                    int a = 255 << 24;
                    int r = v << 16;
                    int g = v <<  8;
                    int b = v <<  0;
                    ARGB[j] = a|r|g|b;
                }

                transformColormap(ARGB);
                model = ColorUtilities.getIndexColorModel(ARGB, 1, visibleBand, -1);

            } else {
                //we can't handle a index color model when values exceed int max value
                model = new CompatibleColorModel(nbbit, this);
            }

        } else {
            model = new CompatibleColorModel(candidate.getPixelSize(), this);
        }

        /*
         * Gives the color model to the image layout and creates a new image using the Null
         * operation, which merely propagates its first source along the operation chain
         * unmodified (except for the ColorModel given in the layout in this case).
         */
        final ImageLayout layout = new ImageLayout().setColorModel(model);
        return new NullOpImage(image, layout, null, OpImage.OP_COMPUTE_BOUND);
    }

    /**
     *
     * @param ARGB array of <code>int</code>
     * @return an array of <code>int</code>
     */
    private int[] transformColormap(final int[] ARGB) {

        final Map<Expression,Expression> categorizes = getThresholds();
        final List<Expression> keys = new ArrayList<>(categorizes.keySet());
        final double[] SE_VALUES = new double[keys.size()];
        final int[] SE_ARGB = new int[keys.size()];

        final Set<Map.Entry<Expression,Expression>> entries = categorizes.entrySet();

        int l=0;
        for(Map.Entry<Expression,Expression> entry : entries){
            if(l==0){
                SE_VALUES[0] = Double.NEGATIVE_INFINITY;
                SE_ARGB[0] = entry.getValue().evaluate(null, Color.class).getRGB();
            }else{
                // CATEGORIZE LESS INFINITY CASE
                try {
                    SE_VALUES[l] = entry.getKey().evaluate(null, Double.class);
                } catch (Exception e) {
                    SE_VALUES[l] = Double.NEGATIVE_INFINITY;
                }
                SE_ARGB[l] = entry.getValue().evaluate(null, Color.class).getRGB();
            }
            l++;
        }

        int step = 0;
        for(int k=0;k<SE_VALUES.length-1;k++){
            final double geoValue = SE_VALUES[k+1];
            int color = SE_ARGB[k];
            int sampleValue = (int)(Double.isNaN(geoValue)?Integer.MAX_VALUE:geoValue);

            for(int i=step ; (i<sampleValue && i<ARGB.length) ; i++){
                ARGB[i] = color;
            }

            step = (int) sampleValue;
            if(step < 0) step = 0;

            //we are on the last element, fill the remaining cell with the color
            if(k == SE_VALUES.length-2){
                color = SE_ARGB[k+1];
                for(int i=step ; i<ARGB.length ; i++){
                    ARGB[i] = color;
                }
            }
        }
        return ARGB;
    }


    /**
     * {@inheritDoc }
     */
    @Override
    public Literal getFallbackValue() {
        return fallback;
    }

}
