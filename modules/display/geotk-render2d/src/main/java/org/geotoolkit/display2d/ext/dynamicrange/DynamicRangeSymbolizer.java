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
package org.geotoolkit.display2d.ext.dynamicrange;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.measure.quantity.Length;
import javax.measure.Unit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.se.xml.v110.ParameterValueType;
import org.geotoolkit.se.xml.v110.SymbolizerType;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.opengis.feature.FeatureType;
import org.opengis.filter.expression.Expression;
import org.opengis.style.ExtensionSymbolizer;
import org.opengis.style.StyleVisitor;
import org.apache.sis.measure.Units;
import org.geotoolkit.display2d.GO2Utilities;
import org.opengis.filter.expression.PropertyName;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DynamicRangeSymbolizerType")
@XmlRootElement(name="DynamicRangeSymbolizer",namespace="http://geotoolkit.org")
public class DynamicRangeSymbolizer extends SymbolizerType implements ExtensionSymbolizer{

    public static final String NAME = "DynamicRange";
    public static final String PROPERTY_MIN = "min";
    public static final String PROPERTY_MAX = "max";
    public static final String PROPERTY_MEAN = "mean";
    public static final String PROPERTY_STD = "std";
    public static final String PROPERTY_HISTO = "histo";
    public static final String PROPERTY_HISTO_MIN = "histo_min";
    public static final String PROPERTY_HISTO_MAX = "histo_max";

    @XmlElement(name = "Channel",namespace="http://geotoolkit.org")
    private List<DRChannel> channels;
    @XmlElement(name = "Geometry")
    protected String geometry;
    

    public List<DRChannel> getChannels() {
        if(channels==null){
            channels = new ArrayList<>();
        }
        return channels;
    }

    public void setChannels(List<DRChannel> channels) {
        this.channels = channels;
    }

    public void setGeometry(final String value) {
        this.geometry = value;
    }

    @Override
    public Expression getGeometry() {
        return geometry==null ? null : GO2Utilities.FILTER_FACTORY.property(geometry);
    }

    @Override
    public Unit<Length> getUnitOfMeasure() {
        return Units.POINT;
    }

    @Override
    public String getExtensionName() {
        return NAME;
    }

    @Override
    public Map<String, Expression> getParameters() {
        return Collections.EMPTY_MAP;
    }

    @Override
    public Object accept(StyleVisitor sv, Object o) {
        return sv.visit(this, o);
    }

    @Override
    public String getGeometryPropertyName() {
        return geometry;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DRChannel {

        public static final String BAND_RED = "R";
        public static final String BAND_GREEN = "G";
        public static final String BAND_BLUE = "B";
        public static final String BAND_ALPHA = "A";

        @XmlElement(name = "Band",namespace="http://geotoolkit.org")
        private String band;
        @XmlElement(name = "ColorSpaceComponent",namespace="http://geotoolkit.org")
        private String colorSpaceComponent;
        @XmlElement(name = "LowerBound",namespace="http://geotoolkit.org")
        private DRBound lower;
        @XmlElement(name = "UpperBound",namespace="http://geotoolkit.org")
        private DRBound upper;

        public DRChannel() {
            band = "";
            colorSpaceComponent = "";
            lower = new DRBound();
            upper = new DRBound();
        }

        public String getBand() {
            return band;
        }

        public void setBand(String band) {
            ArgumentChecks.ensureNonNull("band", band);
            this.band = band;
        }

        public String getColorSpaceComponent() {
            return colorSpaceComponent;
        }

        public void setColorSpaceComponent(String colorSpaceComponant) {
            ArgumentChecks.ensureNonNull("colorSpaceComponant", colorSpaceComponant);
            this.colorSpaceComponent = colorSpaceComponant;
        }

        public DRBound getLower() {
            return lower;
        }

        public void setLower(DRBound lower) {
            ArgumentChecks.ensureNonNull("lower", lower);
            this.lower = lower;
        }

        public DRBound getUpper() {
            return upper;
        }

        public void setUpper(DRBound upper) {
            ArgumentChecks.ensureNonNull("upper", upper);
            this.upper = upper;
        }

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DRBound {

        public static final String MODE_EXPRESSION = "EXPRESSION";
        public static final String MODE_PERCENT = "PERCENT";

        @XmlElement(name = "Mode",namespace="http://geotoolkit.org")
        private String mode;
        @XmlElement(name = "Value",namespace="http://geotoolkit.org")
        private ParameterValueType value;

        @XmlTransient
        private Expression valueExp;

        public DRBound() {
            setMode(MODE_EXPRESSION);
        }

        /**
         * Channel mode : EXPRESSION or PERCENT
         */
        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            ArgumentChecks.ensureNonNull("mode", mode);
            this.mode = mode;
        }

        public Expression getValue() {
            if(valueExp==null && value!=null){
                valueExp = new StyleXmlIO().getTransformer110().visitExpression(value);
            }
            if(valueExp==null){
                valueExp = new DefaultLiteral<>(10);
            }
            return valueExp;
        }

        public void setValue(Expression value) {
            ArgumentChecks.ensureNonNull("value", value);
            this.valueExp = value;
            this.value = new StyleXmlIO().getTransformerXMLv110().visitExpression(value);
        }

    }

    public static FeatureType buildBandType(){
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("coverage");
        ftb.addAttribute(double.class).setName(PROPERTY_MIN);
        ftb.addAttribute(double.class).setName(PROPERTY_MAX);
        ftb.addAttribute(double.class).setName(PROPERTY_MEAN);
        ftb.addAttribute(double.class).setName(PROPERTY_STD);
        ftb.addAttribute(long[].class).setName(PROPERTY_HISTO);
        ftb.addAttribute(double.class).setName(PROPERTY_HISTO_MIN);
        ftb.addAttribute(double.class).setName(PROPERTY_HISTO_MAX);
        return ftb.build();
    }

}
