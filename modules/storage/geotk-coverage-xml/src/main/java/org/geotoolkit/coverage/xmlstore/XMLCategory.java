/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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

package org.geotoolkit.coverage.xmlstore;

import java.awt.Color;
import java.awt.image.DataBuffer;

import org.apache.sis.util.Numbers;
import org.opengis.metadata.content.TransferFunctionType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.sis.measure.NumberRange;
import org.geotoolkit.coverage.Category;
import org.apache.sis.util.ObjectConverters;
import org.opengis.referencing.operation.MathTransform1D;
import org.apache.sis.referencing.operation.transform.TransferFunction;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlRootElement(name="Category")
@XmlAccessorType(XmlAccessType.FIELD)
public class XMLCategory {

    /** y=C0+C1*x */
    public static final String FUNCTION_LINEAR = "linear";
    /** y=10^(C0+C1*x) */
    public static final String FUNCTION_EXPONENTIAL = "exponential";

    @XmlElement(name="name")
    public String name;
    @XmlElement(name="lower")
    public double lower;
    @XmlElement(name="upper")
    public double upper;
    @XmlElement(name="c0")
    public double c0;
    @XmlElement(name="c1")
    public double c1;
    @XmlElement(name="function")
    public String function;
    @XmlElement(name="colors")
    public String[] colors;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLower() {
        return lower;
    }

    public void setLower(double lower) {
        this.lower = lower;
    }

    public double getUpper() {
        return upper;
    }

    public void setUpper(double upper) {
        this.upper = upper;
    }

    public double getC0() {
        return c0;
    }

    public void setC0(double c0) {
        this.c0 = c0;
    }

    public double getC1() {
        return c1;
    }

    public void setC1(double c1) {
        this.c1 = c1;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    /**
     * Build Category from its internal unmarshalling attributs.
     * 
     * @return Expected {@link Category} build from its unmarshalled attributs.
     * @throws IllegalArgumentException if internal transfer function is not known.
     * @param dataType
     */
    public Category buildCategory(int dataType) {
        MathTransform1D sampleToGeophysics = null;
        //-- if function != null means categories own a quantitative unit. 
        //-- For example it is not NOData category but exits more other reason.
        if (function != null) {
            final TransferFunction f = new TransferFunction();
            if (FUNCTION_LINEAR.equals(function)) {
                f.setType(TransferFunctionType.LINEAR);
            } else if (FUNCTION_EXPONENTIAL.equals(function)) {
                f.setType(TransferFunctionType.EXPONENTIAL);
            } else {
                throw new IllegalArgumentException("Unsupported transform : "+function);
            }
            f.setScale(c1);
            f.setOffset(c0);
            sampleToGeophysics = f.getTransform();
        }

        Class typeClass = null;
        switch (dataType) {
            case DataBuffer.TYPE_BYTE :   //fall through
            case DataBuffer.TYPE_USHORT : //fall through
            case DataBuffer.TYPE_SHORT :  //fall through
            case DataBuffer.TYPE_INT : typeClass = Integer.class ; break;
            case DataBuffer.TYPE_DOUBLE : typeClass= Double.class ; break;
            case DataBuffer.TYPE_FLOAT : typeClass = Float.class ; break;
            case DataBuffer.TYPE_UNDEFINED : //fall through
            default : throw new IllegalArgumentException("Undefined SampleDimension DataType.");
        }

        final NumberRange range = getTypedRangeNumber(typeClass, lower, true, upper, true);

        final Color[] cols = new Color[colors.length];
        for(int i = 0; i < cols.length; i++) {
            cols[i] = ObjectConverters.convert(colors[i], Color.class);
        }
        final Category cat;
        if (Double.isNaN(lower) || lower == upper) {
            cat = new Category(name, cols[0], range);
        } else {
            cat = new Category(name, cols, range, sampleToGeophysics);//-- sampletogeophysic may be null. 
        }
        return cat;
    }

    /**
     * Copy informations from given category.
     * @param category
     */
    public void fill(final Category category){
        final Color[] cols = category.getColors();
        colors = new String[cols.length];
        for(int i=0;i<cols.length;i++){
            colors[i] = toString(cols[i]);
        }
        name = category.getName().toString();
        final NumberRange range = category.getRange();
        lower = range.getMinDouble(true);
        upper = range.getMaxDouble(true);

        final MathTransform1D trs = category.getSampleToGeophysics();
        if (trs != null) { //-- if category != NODATA. trs == null for nodata is expected comportement
            final TransferFunction f = new TransferFunction();
            f.setTransform(trs);
            if (TransferFunctionType.LINEAR.equals(f.getType())) {
                function = FUNCTION_LINEAR;
                c0 = f.getOffset();
                c1 = f.getScale();
            } else if (TransferFunctionType.EXPONENTIAL.equals(f.getType())) {
                function = FUNCTION_EXPONENTIAL;
                c0 = f.getOffset();
                c1 = f.getScale();
            }else{
                throw new IllegalArgumentException("Unsupported 1D transform : "+trs);
            }
        }
            
        category.getRange();
    }

    /**
     * Color to hexadecimal.
     *
     * @param color
     * @return color in hexadecimal form
     */
    private static String toString(final Color color) {
        if (color == null) {
            return null;
        }

        String redCode = Integer.toHexString(color.getRed());
        String greenCode = Integer.toHexString(color.getGreen());
        String blueCode = Integer.toHexString(color.getBlue());
        if (redCode.length() == 1)      redCode = "0" + redCode;
        if (greenCode.length() == 1)    greenCode = "0" + greenCode;
        if (blueCode.length() == 1)     blueCode = "0" + blueCode;

        final String colorCode;
        int alpha = color.getAlpha();
        if(alpha != 255){
            String alphaCode = Integer.toHexString(alpha);
            if (alphaCode.length() == 1) alphaCode = "0" + alphaCode;
            colorCode = "#" + alphaCode + redCode + greenCode + blueCode;
        }else{
            colorCode = "#" + redCode + greenCode + blueCode;
        }
        return colorCode.toUpperCase();
    }

    /**
     * Returns an appropriate {@link NumberRange} from given parameters.
     *
     * @param <T> type of internal data.
     * @param type type of internal data.
     * @param min minimum range value.
     * @param isMinIncluded {@code true} if minimum value is considered as include into range interval else false (exclusive).
     * @param max maximum range value.
     * @param isMaxIncluded {@code true} if maximum value is considered as include into range interval else false (exclusive).
     * @return appropriate range value casted in expected type.
     */
    private static <T extends Number & Comparable<T>> NumberRange<T> getTypedRangeNumber(final Class<T> type,
                                                                                         final double min, final boolean isMinIncluded,
                                                                                         final double max, final boolean isMaxIncluded)
    {
        return new NumberRange(type, Numbers.cast(min, type), isMinIncluded,
                Numbers.cast(max, type), isMaxIncluded);
    }

}
