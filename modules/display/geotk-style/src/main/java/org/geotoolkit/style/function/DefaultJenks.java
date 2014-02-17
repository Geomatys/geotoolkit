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
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.filter.AbstractExpression;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.image.classification.Classification;
import org.geotoolkit.image.io.Palette;
import org.geotoolkit.image.io.PaletteFactory;
import static org.geotoolkit.style.StyleConstants.*;
import org.apache.sis.util.logging.Logging;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Literal;

/**
 *
 * @author Quentin Boileau (Geomatys).
 */
public class DefaultJenks extends AbstractExpression implements Jenks {

    private static final PaletteFactory PALETTE_FACTORY = PaletteFactory.getDefault();
    private static final Logger LOGGER = Logging.getLogger(DefaultJenks.class);
    
    private Literal classNumber;
    private Literal paletteName;
    private Literal fallback;

    private Map<Double, Color> colorMap;
    
    public DefaultJenks() {
    }

    public DefaultJenks(final Literal classNumber, final Literal paletteName, final Literal fallback) {
        this.classNumber = (classNumber == null) ? new DefaultLiteral(10) : classNumber;
        this.paletteName = (paletteName == null) ? new DefaultLiteral("rainbow") : paletteName;
        this.fallback = (fallback == null) ? DEFAULT_FALLBACK : fallback;
        colorMap = new HashMap<Double, Color>();
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
    public String getName() {
        return "Jenks";
    }

    @Override
    public List<Expression> getParameters() {
        final List<Expression> params = new ArrayList<Expression>();
        params.add(classNumber);
        params.add(paletteName);
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

            //TODO use a simple list to get all distinct pixel values and sort this list before
            //convert it into an array and give it to classification algorithm
            final Map<Double, Double> valuesStats = new TreeMap<Double, Double>();
            
            int[] pixel = new int[numBands];
            Double key = Double.NaN;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    data.getPixel(x, y, pixel);
                    key = Double.valueOf(pixel[0]);
                    if (valuesStats.containsKey(key)) {
                        valuesStats.put(key, valuesStats.get(key) + 1);
                    } else {
                        valuesStats.put(key, 1.0);
                    }
                }
            }
            
            //prevent classification errors if requested classes is superior to computable classe number.
            final int computableClasses = valuesStats.size();
            if (classes > computableClasses) {
                classes = computableClasses;
                LOGGER.log(Level.WARNING, "Not enough distinct data to compute the requested number of class. Jenks will be computed for {0} classes.", classes);
            }
            
            final double[] pixelValues = new double[valuesStats.size()];
            final double[] pixelOccurs = new double[valuesStats.size()];
            
            int index = 0;
            for (final Map.Entry<Double, Double> pix : valuesStats.entrySet()) {
                pixelValues[index] = pix.getKey().doubleValue(); 
                pixelOccurs[index] = pix.getValue().doubleValue();
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
            
            
            colorMap = new HashMap<Double, Color>();
            int lastindex = 0;
            for (int i = 0; i < indexes.length; i++) {
                int start = lastindex;
                int end = indexes[i];
                if (start < 0 || end < 0) continue;
                for (int j = start; j < end; j++) {
                    colorMap.put(Double.valueOf(pixelValues[j]), colors.get(i));
                }
                lastindex = indexes[i];
            }
            
            /*
             * HACK byte -> no-data = 255 else no-data = Double.NaN 
             * TODO find more elegent way to support no-data values.
             */
            if (dataType == DataBuffer.TYPE_BYTE) {
                colorMap.put(255.0, new Color(0, 0, 0, 0));
            } else {
                colorMap.put(Double.NaN, new Color(0, 0, 0, 0));
            }
            
            final BufferedImage bufferedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            
            key = Double.NaN;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    data.getPixel(x, y, pixel);
                    key = Double.valueOf(pixel[0]);
                    final Color pixelColor = colorMap.get(key);
                    bufferedImg.setRGB(x, y, pixelColor.getRGB());
                }
            }
            
            final RenderedImage outputImage = bufferedImg;
            return outputImage;
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
    
}
