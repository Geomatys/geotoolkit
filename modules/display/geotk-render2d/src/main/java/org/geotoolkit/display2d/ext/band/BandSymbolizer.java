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
package org.geotoolkit.display2d.ext.band;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.measure.quantity.Length;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.se.xml.v110.ParameterValueType;
import org.geotoolkit.se.xml.v110.SymbolizerType;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.opengis.filter.expression.Expression;
import org.opengis.style.ExtensionSymbolizer;
import org.opengis.style.StyleVisitor;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BandSymbolizerType")
@XmlRootElement(name="BandSymbolizer",namespace="http://geotoolkit.org")
public class BandSymbolizer extends SymbolizerType implements ExtensionSymbolizer{

    public static final String NAME = "Band";
    
    @XmlElementRef(name = "Channel", namespace = "http://www.opengis.net/se", type = JAXBElement.class)
    private List<Channel> channels;

    public List<Channel> getChannels() {
        if(channels==null){
            channels = new ArrayList<>();
        }
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    @Override
    public String getGeometryPropertyName() {
        return null;
    }
    
    @Override
    public Expression getGeometry() {
        return null;
    }
    
    @Override
    public Unit<Length> getUnitOfMeasure() {
        return NonSI.PIXEL;
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

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Channel {
        
        @XmlElement(name = "Band",namespace="http://geotoolkit.org")
        private String band;
        @XmlElement(name = "ColorSpaceComponent",namespace="http://geotoolkit.org")
        private String colorSpaceComponent;
        @XmlElement(name = "LowerBound",namespace="http://geotoolkit.org")
        private Bound lower;
        @XmlElement(name = "UpperBound",namespace="http://geotoolkit.org")
        private Bound upper;

        public Channel() {
        }

        public String getBand() {
            return band;
        }

        public void setBand(String band) {
            this.band = band;
        }

        public String getColorSpaceComponent() {
            return colorSpaceComponent;
        }

        public void setColorSpaceComponent(String colorSpaceComponant) {
            this.colorSpaceComponent = colorSpaceComponant;
        }
        
        public Bound getLower() {
            return lower;
        }

        public void setLower(Bound lower) {
            this.lower = lower;
        }

        public Bound getUpper() {
            return upper;
        }

        public void setUpper(Bound upper) {
            this.upper = upper;
        }

    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Bound {
        
        public static final String MODE_EXPRESSION = "EXPRESSION";
        public static final String MODE_PERCENT = "PERCENT";
        
        @XmlElement(name = "Mode",namespace="http://geotoolkit.org")
        private String mode;
        @XmlElement(name = "Value",namespace="http://geotoolkit.org")
        private ParameterValueType value;

        @XmlTransient
        private Expression valueExp;
        
        public Bound() {
            setMode(MODE_EXPRESSION);
            setValue(new DefaultLiteral(10));
        }

        /**
         * Channel mode : EXPRESSION or PERCENT
         */
        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }
        
        public Expression getValue() {
            if(valueExp==null && value!=null){
                valueExp = new StyleXmlIO().getTransformer110().visitExpression(value);
            }
            return valueExp;
        }

        public void setValue(Expression value) {
            this.valueExp = value;
            this.value = new StyleXmlIO().getTransformerXMLv110().visitExpression(value);
        }
        
    }
    
}
