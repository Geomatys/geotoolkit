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
package org.geotoolkit.style.function;

import java.awt.Color;
import java.awt.image.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.filter.AbstractExpression;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.image.classification.Classification;
import static org.geotoolkit.style.StyleConstants.*;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

import javax.media.jai.ImageLayout;
import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;
import org.geotoolkit.image.palette.Palette;
import org.geotoolkit.image.palette.PaletteFactory;

/**
 * Jenks ColorMap function.
 * Analyse input RenderedImage, compute jenks classes and recolor output image using defined palette.
 *
 * @author Quentin Boileau (Geomatys).
 */
public class DefaultJenks extends AbstractExpression implements Jenks {

    private static final PaletteFactory PALETTE_FACTORY = PaletteFactory.getDefault();
    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.style.function");

    private Literal classNumber;
    private Literal paletteName;
    private Literal fallback;

    private double[] noData;
    private TreeMap<Double, Color> colorMap;

    public DefaultJenks() {
    }

    public DefaultJenks(final Literal classNumber, final Literal paletteName, final Literal fallback, List<Literal> noDataLiteral) {
        this.classNumber = (classNumber == null) ? new DefaultLiteral(10) : classNumber;
        this.paletteName = (paletteName == null) ? new DefaultLiteral("rainbow") : paletteName;
        this.fallback = (fallback == null) ? DEFAULT_FALLBACK : fallback;
        colorMap = new TreeMap<Double, Color>();

        if (noDataLiteral == null || noDataLiteral.isEmpty()) {
            noData = new double[] {Double.NaN};
        } else {
            noData = new double[noDataLiteral.size()];
            for (int i = 0; i < noDataLiteral.size(); i++) {
                noData[i] = (Double) noDataLiteral.get(i).getValue();
            }
            Arrays.sort(noData);
        }
    }

    @Override
    public Literal getClassNumber() {
        return classNumber;
    }

    @Override
    public Literal getPalette() {
        return paletteName;
    }

    @Override
    public double[] getNoData() {
        return noData;
    }

    @Override
    public String getName() {
        return "Jenks";
    }

    @Override
    public List<Expression> getParameters() {
        final List<Expression> params = new ArrayList<Expression>();
        params.add(classNumber);
        params.add(paletteName);
        for (int i = 0; i < noData.length; i++) {
            params.add(new DefaultLiteral(noData[i]));
        }
        return params;
    }

    @Override
    public Literal getFallbackValue() {
        return fallback;
    }

    @Override
    public Object evaluate(Object object) {
        return evaluate(object, Object.class);
    }

    @Override
    public Object evaluate(Object object, Class context) {

        if (object instanceof RenderedImage) {

            final RenderedImage image = (RenderedImage) object;
            final int dataType = image.getSampleModel().getDataType();
            final Raster data = image.getData();
            int classes = (Integer) this.classNumber.getValue();
            final int numBands = data.getNumBands();
            final int width = data.getWidth();
            final int height = data.getHeight();

            final Set<Double> values = new TreeSet<Double>();

            double[] pixel = new double[numBands];
            Double key = Double.NaN;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    data.getPixel(x, y, pixel);

                    //arbitrary only get the value ofthe first band
                    //TODO add bandIndex input parameter in Jenks function
                    key = Double.valueOf(pixel[0]);

                    //bypass noData values
                    if (Arrays.binarySearch(noData, key) < 0 && !values.contains(key)) {
                        values.add(key);
                    }
                }
            }

            //prevent classification errors if requested classes is superior to computable classe number.
            final int computableClasses = values.size();
            if (classes > computableClasses) {
                classes = computableClasses;
                LOGGER.log(Level.WARNING, "Not enough distinct data to compute the requested number of class. Jenks will be computed for {0} classes.", classes);
            }

            final double[] pixelValues = new double[values.size()];

            int index = 0;
            for (Double val : values) {
                pixelValues[index] = val.doubleValue();
                index++;
            }

            //compute classes
            final Classification classification = new Classification();
            classification.setData(pixelValues);
            classification.setClassNumber(classes);
            classification.computeJenks(false);

            final int[] indexes = classification.getIndex();

            //create palette
            final List<Color> colors = new ArrayList<Color>();
            try {
                final Palette palette = PALETTE_FACTORY.getPalette((String)paletteName.getValue(), classes);
                final IndexColorModel icm = (IndexColorModel) palette.getColorModel();

                for (int i=0; i<classes; i++) {
                    colors.add(new Color(icm.getRGB(i)));
                }

            } catch (FileNotFoundException ex) {
                LOGGER.log(Level.WARNING, "Palette not found.", ex);
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Palette not found.", ex);
            }

            colorMap.clear();
            colorMap.put(Double.NEGATIVE_INFINITY, new Color(0, 0, 0, 0));
            for (int i = 0; i < indexes.length; i++) {
                colorMap.put(pixelValues[indexes[i]-1], colors.get(i));
            }
            for (int i = 0; i < noData.length; i++) {
                colorMap.put(noData[i], new Color(0, 0, 0, 0));
            }
            /*
             * HACK byte -> no-data = 255 else no-data = Double.NaN
             * TODO find more elegant way to support no-data values.
             */
            if (dataType == DataBuffer.TYPE_BYTE) {
                colorMap.put(255.0, new Color(0, 0, 0, 0));
            }

            final ColorModel originColorModel = image.getColorModel();
            final ColorModel newColorModel = new CompatibleColorModel(originColorModel.getPixelSize(), new JenksCategorize(colorMap));

            /*
             * Gives the color model to the image layout and creates a new image using the Null
             * operation, which merely propagates its first source along the operation chain
             * unmodified (except for the ColorModel given in the layout in this case).
             */
            final ImageLayout layout = new ImageLayout().setColorModel(newColorModel);
            return new NullOpImage(image, layout, null, OpImage.OP_COMPUTE_BOUND);
        }

        return null;
    }

    @Override
    public Object accept(ExpressionVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

    @Override
    public Map<Double, Color> getColorMap() {
        return colorMap;
    }

    /**
     * Internal function used in CompatibleColorModel to recolor input image.
     */
    private class JenksCategorize implements Function {

        private TreeMap<Double,Color> values = new TreeMap<Double,Color>();

        private JenksCategorize(TreeMap<Double, Color> values) {
            this.values.putAll(values);//copy
        }

        @Override
        public String getName() {
            return "JenksNumber";
        }

        @Override
        public List<Expression> getParameters() {
            return null;
        }

        @Override
        public Literal getFallbackValue() {
            return DEFAULT_FALLBACK;
        }

        @Override
        public Object evaluate(Object object) {
            return evaluate(object, Object.class);
        }

        @Override
        public Object evaluate(final Object object, final Class c) {
            if (object instanceof Number) {

                Number value = (Number) object;
                return values.headMap(value.doubleValue(),false).lastEntry().getValue();
            }
            return null;
        }

        @Override
        public Object accept(ExpressionVisitor visitor, Object extraData) {
            return visitor.visit(this, extraData);
        }
    }

}
