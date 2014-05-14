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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
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

    private double[] noData;
    private Map<Double, Color> colorMap;
    
    public DefaultJenks() {
    }

    public DefaultJenks(final Literal classNumber, final Literal paletteName, final Literal fallback, List<Literal> noDataLiteral) {
        this.classNumber = (classNumber == null) ? new DefaultLiteral(10) : classNumber;
        this.paletteName = (paletteName == null) ? new DefaultLiteral("rainbow") : paletteName;
        this.fallback = (fallback == null) ? DEFAULT_FALLBACK : fallback;
        colorMap = new HashMap<Double, Color>();
        
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
            
            //set no-data value as transparent
            for (int i = 0; i < noData.length; i++) {
                colorMap.put(noData[i], new Color(0, 0, 0, 0));
            }
             
            /*
             * HACK byte -> no-data = 255 else no-data = Double.NaN 
             * TODO find more elegent way to support no-data values.
             */
            if (dataType == DataBuffer.TYPE_BYTE) {
                colorMap.put(255.0, new Color(0, 0, 0, 0));
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
