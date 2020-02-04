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
package org.geotoolkit.style.function;

import java.awt.Color;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.sis.internal.coverage.j2d.ColorModelFactory;
import org.apache.sis.internal.util.UnmodifiableArrayList;
import org.apache.sis.util.ObjectConverters;
import org.geotoolkit.filter.AbstractExpression;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.image.RecolorRenderedImage;
import org.geotoolkit.internal.coverage.CoverageUtilities;
import static org.geotoolkit.style.StyleConstants.*;
import org.opengis.feature.Feature;
import org.opengis.filter.capability.FunctionName;
import org.opengis.filter.expression.Expression;
import static org.opengis.filter.expression.Expression.*;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Literal;

/**
 *
 * Implementation of "Interpolation" as a normal function.
 * <p>
 * This implementation is compatible with the Function
 * interface; the parameter list can be used to set the
 * threshold values etc...
 * <p>
 *
 * This function expects:
 * <ol>
 * <li>PropertyName; use "Rasterdata" to indicate this is a colour map
 * <li>Literal: lookup value
 * <li>Literal: InterpolationPoint : data 1
 * <li>Literal: InterpolationPoint : value 1
 * <li>Literal: InterpolationPoint : data 2
 * <li>Literal: InterpolationPoint : value 2
 * <li>Literal: Mode
 * <li>Literal: Method
 * </ol>
 * In reality any expression will do.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class DefaultInterpolate extends AbstractExpression implements Interpolate {

    private final Expression lookup;
    private final InterpolationPoint[] points;
    private final Method method;
    private final Mode mode;
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
            return -2; // indicating unbounded, 2 minimum
        }

        @Override
        public List<String> getArgumentNames() {
            return Arrays.asList(new String[]{
                        "LookupValue",
                        "Data 1", "Value 1",
                        "Data 2", "Value 2",
                        "linear, cosine or cubic",
                        "numeric or color"
                    });
        }

        @Override
        public String getName() {
            return "Interpolate";
        }
    };


    public DefaultInterpolate(final Expression ... expressions){
        lookup = expressions[0];
        final List<InterpolationPoint> points = new ArrayList<InterpolationPoint>();
        for(int i=1;i<expressions.length-2;i+=2){
            final InterpolationPoint ip = new DefaultInterpolationPoint(
                    expressions[i].evaluate(null, Number.class), expressions[i+1]);
            points.add(ip);
        }
        this.points = points.toArray(new InterpolationPoint[points.size()]);

        final Method me = Method.parse(expressions[expressions.length-2].evaluate(null, String.class));
        final Mode mo = Mode.parse(expressions[expressions.length-1].evaluate(null, String.class));
        this.method = (me==null) ? Method.COLOR : me;
        this.mode = (mo == null) ? Mode.LINEAR : mo;
        this.fallback = DEFAULT_FALLBACK;
    }

    public DefaultInterpolate(final Expression LookUpValue, List<InterpolationPoint> values,
           final Method method, final Mode mode,final Literal fallback){

        if(values == null ){
            values = Collections.emptyList();
        }

        this.lookup = (LookUpValue == null || LookUpValue == NIL) ?  DEFAULT_CATEGORIZE_LOOKUP : LookUpValue;
        this.points = values.toArray(new InterpolationPoint[values.size()]);

        Arrays.sort(points, new Comparator<InterpolationPoint>(){
            @Override
            public int compare(InterpolationPoint t1, InterpolationPoint t2) {
                final Number v1 = t1.getData();
                final Number v2 = t2.getData();
                if(v1 instanceof Float && Float.isNaN(v1.floatValue())){
                    return -1;
                }else if(v1 instanceof Double && Double.isNaN(v1.doubleValue())){
                    return -1;
                }else if(v2 instanceof Float && Float.isNaN(v2.floatValue())){
                    return +1;
                }else if(v2 instanceof Double && Double.isNaN(v2.doubleValue())){
                    return +1;
                }

                final double diff = v1.doubleValue() - v2.doubleValue();
                if(diff < 0){
                    return -1;
                }else if(diff > 0){
                    return +1;
                }else{
                    return 0;
                }
            }
        });


        this.method = (method == null) ? Method.COLOR : method;
        this.mode = (mode == null) ? Mode.LINEAR : mode;
        this.fallback = (fallback == null) ? DEFAULT_FALLBACK : fallback;
    }


    @Override
    public String getName() {
        return "Interpolate";
    }

    @Override
    public List<Expression> getParameters() {
        final List<Expression> params = new ArrayList<Expression>();
        params.add(lookup);
        for(InterpolationPoint ip : points){
            params.add(new DefaultLiteral(ip.getData()));
            params.add(ip.getValue());
        }
        params.add(new DefaultLiteral(method.name().toLowerCase()));
        params.add(new DefaultLiteral(mode.name().toLowerCase()));
        return params;
    }

    @Override
    public Object accept(final ExpressionVisitor visitor, final Object extraData) {
        return visitor.visit(this, extraData);
    }

    @Override
    public Object evaluate(final Object object) {

        if (object instanceof RenderedImage) {
            return evaluateImage((RenderedImage) object);
        }

        return evaluate(object, Object.class);
    }

    @Override
    public Object evaluate(final Object object, final Class c) {
        if (object instanceof RenderedImage && RenderedImage.class.isAssignableFrom(c))
            return evaluateImage((RenderedImage) object);

        final Number value;
        if(object instanceof Feature){
            value = lookup.evaluate(object,Number.class);
        }else if(object instanceof Number){
            value = (Number)object;
        }else{
            return fallback.evaluate(object,c);
        }

        final double dval = value.doubleValue();

        InterpolationPoint before = null;
        InterpolationPoint after = null;
        for(InterpolationPoint ip : points){
            final Number ipnum = ip.getData();
            final double ipdnum = ipnum.doubleValue();
            final double ipval;

            if(Double.isNaN(ipdnum)){
                ipval = ipdnum;
                //if we want exact NaN match use doubleToRawLongBits
                if(Double.doubleToLongBits(ipval) == Double.doubleToLongBits(dval)){
                    before = ip;
                    break;
                }else{
                    continue;
                }
            }else{
                ipval = ipnum.doubleValue();
            }


            if(ipval < dval){
                before = ip;
            }else if(ipval > dval){
                after = ip;
                break;
            }else{
                //exact match
                return ip.getValue().evaluate(object,c);
            }
        }

        if(before == null && after == null){
            //no value associated, surely an NaN value
            //return a translucent color
            return ObjectConverters.convert( new Color(0,0,0,0) , c);

        }else if(before == null){
            //only have an over value
            return after.getValue().evaluate(object,c);
        }else if(after == null){
            //only have an under value
            return before.getValue().evaluate(object,c);
        }else{
            //must interpolate
            final double d1 = before.getData().doubleValue();
            final double d2 = after.getData().doubleValue();
            final double pourcent = (dval - d1)/ (d2 - d1);

            final Object o1 = before.getValue().evaluate(object,c);
            final Object o2 = after.getValue().evaluate(object,c);
            if(o1 instanceof Color && o2 instanceof Color){
                //datas are not numbers, looks like we deal with colors
                final Color c1 = before.getValue().evaluate(object,Color.class);
                final Color c2 = after.getValue().evaluate(object,Color.class);
                final Color in = interpolate(c1, c2, pourcent);
                return ObjectConverters.convert( in , c);
            }else{
                final Double n1 = before.getValue().evaluate(object,Double.class);
                final Double n2 = after.getValue().evaluate(object,Double.class);
                return ObjectConverters.convert( (n1 + pourcent*(n2-n1)) , c);
            }
        }
    }

    /**
     * Recolor image
     * @return recolored image
     */
    private RenderedImage evaluateImage (final RenderedImage image) {
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

        // As index color model cannot manage negative values, we must use our own in this case.
        if (points.length == 0 || (points[0].getData().doubleValue() < 0 || candidate==null)) {
            final int pixelSize;
            if(candidate!=null){
                pixelSize = candidate.getPixelSize();
            }else{
                pixelSize = 16;
            }
            model = new CompatibleColorModel(pixelSize, this);

        } else if (candidate instanceof IndexColorModel) {
            final IndexColorModel colors = (IndexColorModel) candidate;
            final int mapSize = colors.getMapSize();
            ARGB = new int[mapSize];
            colors.getRGBs(ARGB);

            transformColormap(ARGB);
            model = ColorModelFactory.createIndexColorModel(ARGB, 1, visibleBand, -1);

        } else if (candidate instanceof ComponentColorModel) {
            final ComponentColorModel colors = (ComponentColorModel) candidate;
            final int nbbit = colors.getPixelSize();
            final int type = image.getSampleModel().getDataType();

            if ((type == DataBuffer.TYPE_BYTE || type == DataBuffer.TYPE_USHORT) && nbbit <= 16) {
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
                model = ColorModelFactory.createIndexColorModel(ARGB, 1, visibleBand, -1);

            } else {
                //we can't handle a index color model when values exceed int max value
                model = new CompatibleColorModel(nbbit, this);
            }

        } else if (candidate instanceof DirectColorModel) {
            final DirectColorModel colors = (DirectColorModel) candidate;
            final int nbbit = colors.getPixelSize();
            final int type = image.getSampleModel().getDataType();

            if ((type == DataBuffer.TYPE_BYTE || type == DataBuffer.TYPE_USHORT) && nbbit <= 16)  {
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
                model = ColorModelFactory.createIndexColorModel(ARGB, 1, visibleBand, -1);

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

        return new RecolorRenderedImage(image, model);
    }

    private int[] transformColormap(final int[] ARGB) {
        final List<InterpolationPoint> points = getInterpolationPoints();
        final double[] SE_VALUES = new double[points.size()];
        final int[] SE_ARGB = new int[points.size()];
        for (int i = 0, n = points.size(); i < n; i++) {
            final InterpolationPoint point = points.get(i);
            SE_VALUES[i] = point.getData().doubleValue();
            final Color evaluate = point.getValue().evaluate(null, Color.class);
            SE_ARGB[i] = (evaluate != null) ? evaluate.getRGB() : 0;
        }

        int lastStep = -1;
        int lastColor = -1;
        for (int k = 0; k < SE_VALUES.length; k++) {
            final double geoValue = SE_VALUES[k];
            final int currentColor = SE_ARGB[k];
            final int currentStep = (int) geoValue;

            //first element, dont interpolate colors
            if (k == 0) {
                lastColor = currentColor;
                lastStep = -1;
            }

            final int stepInterval = currentStep - lastStep;
            final int lastAlpha = (lastColor >>> 24) & 0xFF;
            final int lastRed = (lastColor >>> 16) & 0xFF;
            final int lastGreen = (lastColor >>> 8) & 0xFF;
            final int lastBlue = (lastColor >>> 0) & 0xFF;
            final int alphaInterval = ((currentColor >>> 24) & 0xFF) - lastAlpha;
            final int redInterval = ((currentColor >>> 16) & 0xFF) - lastRed;
            final int greenInterval = ((currentColor >>> 8) & 0xFF) - lastGreen;
            final int blueInterval = ((currentColor >>> 0) & 0xFF) - lastBlue;
            for (int i = lastStep + 1; (i <= currentStep && i < ARGB.length); i++) {
                //calculate interpolated color
                final int relativePosition = i - lastStep;
                final double pourcent = (double) ((double) relativePosition / (double) stepInterval);
                int a = lastAlpha + (int) (pourcent * alphaInterval);
                int r = lastRed + (int) (pourcent * redInterval);
                int g = lastGreen + (int) (pourcent * greenInterval);
                int b = lastBlue + (int) (pourcent * blueInterval);
                a <<= 24;
                r <<= 16;
                g <<= 8;
                b <<= 0;
                ARGB[i] = a | r | g | b;
            }

            lastStep = (int) currentStep;
            lastColor = currentColor;

            //last element, fill the remaining cell with the color
            if (k == SE_VALUES.length - 1) {
                for (int i = lastStep; i < ARGB.length; i++) {
                    ARGB[i] = currentColor;
                }
            }

        }
        return ARGB;
    }

    @Override
    public Literal getFallbackValue() {
        return fallback;
    }

    @Override
    public Expression getLookupValue() {
        return lookup;
    }

    @Override
    public List<InterpolationPoint> getInterpolationPoints() {
        return UnmodifiableArrayList.wrap(points);
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    public static Color interpolate(Color c1, Color c2, double pourcent){
        final int argb1 = c1.getRGB();
        final int argb2 = c2.getRGB();

        final int lastAlpha     = (argb1>>>24) & 0xFF;
        final int lastRed       = (argb1>>>16) & 0xFF;
        final int lastGreen     = (argb1>>> 8) & 0xFF;
        final int lastBlue      = (argb1>>> 0) & 0xFF;
        final int alphaInterval = ((argb2>>>24) & 0xFF) - lastAlpha;
        final int redInterval   = ((argb2>>>16) & 0xFF) - lastRed;
        final int greenInterval = ((argb2>>> 8) & 0xFF) - lastGreen;
        final int blueInterval  = ((argb2>>> 0) & 0xFF) - lastBlue;

        //calculate interpolated color
        int a = lastAlpha + (int)(pourcent*alphaInterval);
        int r = lastRed   + (int)(pourcent*redInterval);
        int g = lastGreen + (int)(pourcent*greenInterval);
        int b = lastBlue  + (int)(pourcent*blueInterval);
        return new Color(r, g, b, a) ;
    }

}
