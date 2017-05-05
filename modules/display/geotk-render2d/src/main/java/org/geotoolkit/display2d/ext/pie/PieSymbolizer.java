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
package org.geotoolkit.display2d.ext.pie;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.se.xml.v110.ParameterValueType;
import org.geotoolkit.se.xml.v110.SymbolizerType;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.geotoolkit.style.visitor.ListingPropertyVisitor;
import org.opengis.filter.expression.Expression;
import org.opengis.style.ExtensionSymbolizer;
import org.opengis.style.StyleVisitor;
import org.apache.sis.measure.Units;

import javax.measure.quantity.Length;
import javax.measure.Unit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Pie symbolizer.
 *
 * @author Johann Sorel (Geomays)
 * @author Cédric Briançon (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PieSymbolizerType")
@XmlRootElement(name="PieSymbolizer", namespace="http://geotoolkit.org")
public final class PieSymbolizer extends SymbolizerType implements ExtensionSymbolizer {
    /**
     * Stores the matching between a quarter property name and the chosen color.
     */
    @XmlElement(name = "ColorQuarter",namespace="http://geotoolkit.org")
    private List<ColorQuarter> colorQuarters = new ArrayList<>();

    /**
     * Size of the pie, in pixels. If not specified, 100px
     */
    @XmlElement(name = "Size",namespace="http://geotoolkit.org")
    private ParameterValueType size;

    /**
     * Property name for identifying geometries. Used for the first regrouping of data.
     */
    @XmlElement(name = "Group",namespace="http://geotoolkit.org")
    private ParameterValueType group;

    /**
     * Property name on which to split data into quarters. Used in a second time,
     * to split pie into quarters.
     */
    @XmlElement(name = "Quarter",namespace="http://geotoolkit.org")
    private ParameterValueType quarter;

    /**
     * Property name containing values into the data
     */
    @XmlElement(name = "Value",namespace="http://geotoolkit.org")
    private ParameterValueType value;

    @XmlTransient
    private Expression pieSize;

    @XmlTransient
    private Expression groupExp;

    @XmlTransient
    private Expression quarterExp;

    @XmlTransient
    private Expression valueExp;

    public PieSymbolizer(){}

    @Override
    public Unit<Length> getUnitOfMeasure() {
        return Units.POINT;
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
        return "pie";
    }


    @Override
    public Map<String, Expression> getParameters() {

        final Map<String,Expression> config = new HashMap<>();

        final Set<String> properties = new HashSet<>();

        for (final ColorQuarter colorQuarter : colorQuarters) {
            if (colorQuarter.getColor() != null) {
                colorQuarter.getColor().accept(ListingPropertyVisitor.VISITOR, properties);
            }
            if (colorQuarter.getQuarter() != null) {
                colorQuarter.getQuarter().accept(ListingPropertyVisitor.VISITOR, properties);
            }
        }

        if(getSize() != null){
            getSize().accept(ListingPropertyVisitor.VISITOR, properties);
        }
        if(getGroup() != null){
            getGroup().accept(ListingPropertyVisitor.VISITOR, properties);
        }
        if(getQuarter() != null){
            getQuarter().accept(ListingPropertyVisitor.VISITOR, properties);
        }
        if(getValue() != null){
            getValue().accept(ListingPropertyVisitor.VISITOR, properties);
        }

        int i=0;
        for(String str : properties){
            config.put(String.valueOf(i++), FactoryFinder.getFilterFactory(null).property(str));
        }
        return config;
    }

    public List<ColorQuarter> getColorQuarters() {
        return colorQuarters;
    }

    public Expression getSize() {
        if(pieSize != null){
            return pieSize;
        }else if(size != null){
            final StyleXmlIO util = new StyleXmlIO();
            pieSize = util.getTransformer110().visitExpression(size);
            return pieSize;
        }
        return null;
    }

    public void setSize(Expression pieSize) {
        this.pieSize = pieSize;
        final StyleXmlIO util = new StyleXmlIO();
        this.size = util.getTransformerXMLv110().visitExpression(pieSize);
    }

    public Expression getGroup() {
        if(groupExp != null){
            return groupExp;
        }else if(group != null){
            final StyleXmlIO util = new StyleXmlIO();
            groupExp = util.getTransformer110().visitExpression(group);
            return groupExp;
        }
        return null;
    }

    public void setGroup(Expression groupExp) {
        this.groupExp = groupExp;
        final StyleXmlIO util = new StyleXmlIO();
        this.group = util.getTransformerXMLv110().visitExpression(groupExp);
    }

    public Expression getQuarter() {
        if(quarterExp != null){
            return quarterExp;
        }else if(quarter != null){
            final StyleXmlIO util = new StyleXmlIO();
            quarterExp = util.getTransformer110().visitExpression(quarter);
            return quarterExp;
        }
        return null;
    }

    public void setQuarter(Expression quarterExp) {
        this.quarterExp = quarterExp;
        final StyleXmlIO util = new StyleXmlIO();
        this.quarter = util.getTransformerXMLv110().visitExpression(quarterExp);
    }

    public Expression getValue() {
        if(valueExp != null){
            return valueExp;
        }else if(value != null){
            final StyleXmlIO util = new StyleXmlIO();
            valueExp = util.getTransformer110().visitExpression(value);
            return valueExp;
        }
        return null;
    }

    public void setValue(Expression valueExp) {
        this.valueExp = valueExp;
        final StyleXmlIO util = new StyleXmlIO();
        this.value = util.getTransformerXMLv110().visitExpression(valueExp);
    }

    @Override
    public Object accept(StyleVisitor sv, Object o) {
        return sv.visit(this, o);
    }


    /**
     * Mapping between a quarter name and its color.
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ColorQuarter {
        /**
         * Quarter property name.
         */
        @XmlElement(name = "Quarter",namespace="http://geotoolkit.org")
        private ParameterValueType quarter;

        /**
         * Color assigned to this quarter.
         */
        @XmlElement(name = "Color",namespace="http://geotoolkit.org")
        private ParameterValueType color;

        @XmlTransient
        private Expression quarterExp;

        @XmlTransient
        private Expression colorExp;

        public Expression getQuarter() {
            if(quarterExp != null){
                return quarterExp;
            }else if(quarter != null){
                final StyleXmlIO util = new StyleXmlIO();
                quarterExp = util.getTransformer110().visitExpression(quarter);
                return quarterExp;
            }
            return null;
        }

        public void setQuarter(Expression quarterExp) {
            this.quarterExp = quarterExp;
            final StyleXmlIO util = new StyleXmlIO();
            this.quarter = util.getTransformerXMLv110().visitExpression(quarterExp);
        }

        public Expression getColor() {
            if(colorExp != null){
                return colorExp;
            }else if(color != null){
                final StyleXmlIO util = new StyleXmlIO();
                colorExp = util.getTransformer110().visitExpression(color);
                return colorExp;
            }
            return null;
        }

        public void setColor(Expression colorExp) {
            this.colorExp = colorExp;
            final StyleXmlIO util = new StyleXmlIO();
            this.color = util.getTransformerXMLv110().visitExpression(colorExp);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ColorQuarter other = (ColorQuarter) obj;
            if (this.quarter != other.quarter && (this.quarter == null || !this.quarter.equals(other.quarter))) {
                return false;
            }
            if (this.color != other.color && (this.color == null || !this.color.equals(other.color))) {
                return false;
            }
            return true;
        }
    }
}
