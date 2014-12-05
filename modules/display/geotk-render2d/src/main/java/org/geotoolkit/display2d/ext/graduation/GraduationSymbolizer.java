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
package org.geotoolkit.display2d.ext.graduation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.measure.quantity.Length;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.se.xml.v110.FontType;
import org.geotoolkit.se.xml.v110.ParameterValueType;
import org.geotoolkit.se.xml.v110.StrokeType;
import org.geotoolkit.se.xml.v110.SymbolizerType;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.geotoolkit.style.StyleConstants;
import org.geotoolkit.style.visitor.ListingPropertyVisitor;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.style.ExtensionSymbolizer;
import org.opengis.style.Font;
import org.opengis.style.Stroke;
import org.opengis.style.StyleVisitor;

/**
 * Draw graduation along a LineStrings or polygon boundary.
 * 
 * @author Johann Sorel (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GraduationSymbolizerType")
@XmlRootElement(name="GraduationSymbolizer",namespace="http://geotoolkit.org")
public class GraduationSymbolizer extends SymbolizerType implements ExtensionSymbolizer{
    
    public static final Literal SIDE_LEFT = new DefaultLiteral("LEFT");
    public static final Literal SIDE_RIGHT = new DefaultLiteral("RIGHT");
    public static final Literal SIDE_BOTH = new DefaultLiteral("BOTH");
    
    public static final Literal DIRECTION_FORWARD = new DefaultLiteral(false);
    public static final Literal DIRECTION_REVERSE = new DefaultLiteral(true);
    
    /**
     * Definition of each graduation.
     */
    @XmlElement(name = "Graduation",namespace="http://geotoolkit.org")
    private List<Graduation> graduations = new ArrayList<>();
    
    public GraduationSymbolizer(){}

    @Override
    public Unit<Length> getUnitOfMeasure() {
        return NonSI.PIXEL;
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
    public String getExtensionName() {
        return "graduation";
    }

    @Override
    public Map<String, Expression> getParameters() {

        final Map<String,Expression> config = new HashMap<>();

        final Set<String> properties = new HashSet<>();

        for (final Graduation graduation : graduations) {
            if (graduation.getOffset()!= null) {
                graduation.getOffset().accept(ListingPropertyVisitor.VISITOR, properties);
            }
            if (graduation.getReverse()!= null) {
                graduation.getReverse().accept(ListingPropertyVisitor.VISITOR, properties);
            }
            if (graduation.getSide()!= null) {
                graduation.getSide().accept(ListingPropertyVisitor.VISITOR, properties);
            }
            if (graduation.getStart()!= null) {
                graduation.getStart().accept(ListingPropertyVisitor.VISITOR, properties);
            }
            if (graduation.getStep()!= null) {
                graduation.getStep().accept(ListingPropertyVisitor.VISITOR, properties);
            }
            if (graduation.getUnit()!= null) {
                graduation.getUnit().accept(ListingPropertyVisitor.VISITOR, properties);
            }
            if (graduation.getFont()!= null) {
                graduation.getFont().accept(ListingPropertyVisitor.VISITOR, properties);
            }
            if (graduation.getStroke()!= null) {
                graduation.getStroke().accept(ListingPropertyVisitor.VISITOR, properties);
            }
        }

        int i=0;
        for(String str : properties){
            config.put(String.valueOf(i++), FactoryFinder.getFilterFactory(null).property(str));
        }
        return config;
    }

    public synchronized List<Graduation> getGraduations() {
        if(graduations==null){
            graduations = new ArrayList<>();
        }
        return graduations;
    }

    @Override
    public Object accept(StyleVisitor sv, Object o) {
        return sv.visit(this, o);
    }


    /**
     * Graduation definition including color, font, spacing, offset, ...
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Graduation {
        
        @XmlElement(name = "Size",namespace="http://geotoolkit.org")
        private ParameterValueType size;
        @XmlElement(name = "Start",namespace="http://geotoolkit.org")
        private ParameterValueType start;
        @XmlElement(name = "Reverse",namespace="http://geotoolkit.org")
        private ParameterValueType reverse;
        @XmlElement(name = "Side",namespace="http://geotoolkit.org")
        private ParameterValueType side;
        @XmlElement(name = "Offset",namespace="http://geotoolkit.org")
        private ParameterValueType offset;
        @XmlElement(name = "Step",namespace="http://geotoolkit.org")
        private ParameterValueType step;
        @XmlElement(name = "Unit",namespace="http://geotoolkit.org")
        private ParameterValueType unit;
        @XmlElement(name = "Format",namespace="http://geotoolkit.org")
        private ParameterValueType format;
        @XmlElement(name = "Font",namespace="http://geotoolkit.org")
        private FontType font;
        @XmlElement(name = "Stroke",namespace="http://geotoolkit.org")
        private StrokeType stroke;

        @XmlTransient
        private Expression sizeExp;
        @XmlTransient
        private Expression startExp;
        @XmlTransient
        private Expression reverseExp;
        @XmlTransient
        private Expression sideExp;
        @XmlTransient
        private Expression offsetExp;
        @XmlTransient
        private Expression stepExp;
        @XmlTransient
        private Expression unitExp;
        @XmlTransient
        private Expression formatExp;
        @XmlTransient
        private Font fontExp;
        @XmlTransient
        private Stroke strokeExp;

        public Graduation() {
            setSize(new DefaultLiteral(10));
            setStart(StyleConstants.LITERAL_ZERO_FLOAT);
            setReverse(DIRECTION_FORWARD);
            setSide(SIDE_RIGHT);
            setOffset(StyleConstants.LITERAL_ZERO_FLOAT);
            setStep(new DefaultLiteral(100));
            setUnit(new DefaultLiteral("km"));
            setFormat(new DefaultLiteral("###"));
            setFont(StyleConstants.DEFAULT_FONT);
            setStroke(StyleConstants.DEFAULT_STROKE);
        }

        /**
         * Graduation tick size/length in pixel
         */
        public Expression getSize() {
            if(sizeExp==null && size!=null){
                sizeExp = new StyleXmlIO().getTransformer110().visitExpression(size);
            }
            return sizeExp;
        }

        public void setSize(Expression value) {
            this.sizeExp = value;
            this.size = new StyleXmlIO().getTransformerXMLv110().visitExpression(value);
        }
        
        /**
         * Starting value of the graduation. 
         * normally graduation starts at 0 but another value may be used
         */
        public Expression getStart() {
            if(startExp==null && start!=null){
                startExp = new StyleXmlIO().getTransformer110().visitExpression(start);
            }
            return startExp;
        }

        public void setStart(Expression value) {
            this.startExp = value;
            this.start = new StyleXmlIO().getTransformerXMLv110().visitExpression(value);
        }
        
        /**
         * Graduate geometry in point iteration order or reverse order.
         */
        public Expression getReverse() {
            if(reverseExp==null && reverse!=null){
                reverseExp = new StyleXmlIO().getTransformer110().visitExpression(reverse);
            }
            return reverseExp;
        }

        public void setReverse(Expression value) {
            this.reverseExp = value;
            this.reverse = new StyleXmlIO().getTransformerXMLv110().visitExpression(value);
        }
        
        /**
         * Side of graduation, side in relation to the line segment direction.
         * Value can be : LEFT, RIGHT, BOTH
         */
        public Expression getSide() {
            if(sideExp==null && side!=null){
                sideExp = new StyleXmlIO().getTransformer110().visitExpression(side);
            }
            return sideExp;
        }

        public void setSide(Expression value) {
            this.sideExp = value;
            this.side = new StyleXmlIO().getTransformerXMLv110().visitExpression(value);
        }

        /**
         * Graduation offset from the geometry.
         * Position value are on the right side of the points iteration order.
         */
        public Expression getOffset() {
            if(offsetExp==null && offset!=null){
                offsetExp = new StyleXmlIO().getTransformer110().visitExpression(offset);
            }
            return offsetExp;
        }

        public void setOffset(Expression value) {
            this.offsetExp = value;
            this.offset = new StyleXmlIO().getTransformerXMLv110().visitExpression(value);
        }
        
        /**
         * Step between each tick of the graduation.
         */
        public Expression getStep() {
            if(stepExp==null && step!=null){
                stepExp = new StyleXmlIO().getTransformer110().visitExpression(step);
            }
            return stepExp;
        }

        public void setStep(Expression value) {
            this.stepExp = value;
            this.step = new StyleXmlIO().getTransformerXMLv110().visitExpression(value);
        }
        
        /**
         * Step between each tick of the graduation.
         */
        public Expression getUnit() {
            if(unitExp==null && unit!=null){
                unitExp = new StyleXmlIO().getTransformer110().visitExpression(unit);
            }
            return unitExp;
        }

        public void setUnit(Expression value) {
            this.unitExp = value;
            this.unit = new StyleXmlIO().getTransformerXMLv110().visitExpression(value);
        }
        
        /**
         * Graduation text format
         */
        public Expression getFormat() {
            if(formatExp==null && format!=null){
                formatExp = new StyleXmlIO().getTransformer110().visitExpression(format);
            }
            return formatExp;
        }

        public void setFormat(Expression value) {
            this.formatExp = value;
            this.format = new StyleXmlIO().getTransformerXMLv110().visitExpression(value);
        }
        
        /**
         * Tick text font.
         * Use null for no text.
         * Color is taken from the stroke
         */
        public Font getFont() {
            if(fontExp==null && font!=null){
                fontExp = new StyleXmlIO().getTransformer110().visit(font);
            }
            return fontExp;
        }

        public void setFont(Font value) {
            this.fontExp = value;
            this.font = new StyleXmlIO().getTransformerXMLv110().visit(fontExp, null);
        }
        
        /**
         * Tick stroke.
         */
        public Stroke getStroke() {
            if(strokeExp==null && stroke!=null){
                strokeExp = new StyleXmlIO().getTransformer110().visit(stroke);
            }
            return strokeExp;
        }

        public void setStroke(Stroke value) {
            this.strokeExp = value;
            this.stroke = new StyleXmlIO().getTransformerXMLv110().visit(strokeExp, null);
        }
        
    }
    
}
