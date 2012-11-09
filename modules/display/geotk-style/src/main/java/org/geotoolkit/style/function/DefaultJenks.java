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
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.filter.AbstractExpression;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.image.classification.Classification;
import org.geotoolkit.image.io.Palette;
import org.geotoolkit.image.io.PaletteFactory;
import static org.geotoolkit.style.StyleConstants.*;
import org.geotoolkit.util.logging.Logging;

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

    private Map<Integer, Color> colorMap;
    
    public DefaultJenks() {
    }

    public DefaultJenks(final Literal classNumber, final Literal paletteName, final Literal fallback) {
        this.classNumber = (classNumber == null) ? new DefaultLiteral(10) : classNumber;
        this.paletteName = (paletteName == null) ? new DefaultLiteral("rainbow") : paletteName;
        this.fallback = (fallback == null) ? DEFAULT_FALLBACK : fallback;
        colorMap = new HashMap<Integer, Color>();
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
            final Raster data = image.getData();
            int classes = (Integer) this.classNumber.getValue();
            final int numBands = data.getNumBands();
            final int width = data.getWidth();
            final int height = data.getHeight();
            
            final double[] doubleValues = new double[256];     
            Arrays.fill(doubleValues, 0.0);
            
            int[] pixel = new int[numBands];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    data.getPixel(x, y, pixel);
                    
                    if (pixel[0] != 255) {
                        doubleValues[pixel[0]] = doubleValues[pixel[0]] + 1.0;
                    }
                }
            }
            
            //prevent classification errors if requested classes is superior to computable classe number.
            final int computableClasses = getMaxClassNbre(doubleValues);
            if (classes > computableClasses) {
                classes = computableClasses;
                LOGGER.log(Level.WARNING, "Not enough distinct data to compute the requested number of class. Jenks will be computed for {0} classes.", classes);
            }
            
            //compute classes
            final Classification classification = new Classification();
            classification.setData(doubleValues);
            classification.setClassNumber(classes);
            classification.computeJenks();

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
            
            
            colorMap = new HashMap<Integer, Color>();
            int lastindex = 0;
            for (int i = 0; i < indexes.length; i++) {
                int start = lastindex;
                int end = indexes[i];
                for (int j = start; j < end; j++) {
                    colorMap.put(j, colors.get(i));
                }
                lastindex = indexes[i];
            }
            colorMap.put(255, new Color(0, 0, 0, 0));//no data = transparent
            
            final BufferedImage bufferedImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    data.getPixel(x, y, pixel);
                    
                    final Color pixelColor = colorMap.get(pixel[0]);
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
    public Map<Integer, Color> getColorMap() {
        return colorMap;
    }
    
    /**
     * Compute the maximum number of classes from data arrays.
     * 
     * @param data
     * @return maximum number of classes.
     */
    private int getMaxClassNbre(double[] data) {
        
        final List<Double> valuesList = new ArrayList<Double>();
        
        Double dbl;
        for (double dataValue : data) {
            dbl = Double.valueOf(dataValue);
            if (!valuesList.contains(dbl)) {
                valuesList.add(dbl);
            }
        }
        
        return valuesList.size();
        
    }
}
