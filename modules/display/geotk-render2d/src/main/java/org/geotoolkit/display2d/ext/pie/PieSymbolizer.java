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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.measure.quantity.Length;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.sis.util.logging.Logging;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.ogc.xml.v110.FilterType;
import org.geotoolkit.se.xml.v110.FillType;
import org.geotoolkit.se.xml.v110.ParameterValueType;
import org.geotoolkit.se.xml.v110.StrokeType;
import org.geotoolkit.se.xml.v110.SymbolizerType;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.geotoolkit.style.visitor.ListingPropertyVisitor;
import org.opengis.filter.Filter;
import org.opengis.filter.expression.Expression;
import org.opengis.style.ExtensionSymbolizer;
import org.opengis.style.Fill;
import org.opengis.style.Stroke;
import org.opengis.style.StyleVisitor;
import org.opengis.util.FactoryException;

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

    private static final Logger LOGGER = Logging.getLogger(PieSymbolizer.class);

    @XmlElement(name = "Group",namespace="http://geotoolkit.org")
    private List<Group> groups;

    public PieSymbolizer(){
        this(null);
    }

    public PieSymbolizer(List<Group> groups){
        if(groups == null) {
            groups = new ArrayList<Group>();
        }
        this.groups = groups;
    }

    @Override
    public Unit<Length> getUnitOfMeasure() {
        return NonSI.PIXEL;
    }

    @Override
    public String getGeometryPropertyName() {
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

        for(Group g : groups){
            if(g.getFilter() != null){
                g.getFilter().accept(ListingPropertyVisitor.VISITOR, properties);
            }
            if(g.getFill() != null){
                g.getFill().accept(ListingPropertyVisitor.VISITOR, properties);
            }
            if(g.getStroke() != null){
                g.getStroke().accept(ListingPropertyVisitor.VISITOR, properties);
            }
            if(g.getText() != null){
                g.getText().accept(ListingPropertyVisitor.VISITOR, properties);
            }
        }

        int i=0;
        for(String str : properties){
            config.put(String.valueOf(i++), FactoryFinder.getFilterFactory(null).property(str));
        }
        return config;
    }

    public List<Group> getGroups() {
        return groups;
    }



    @Override
    public Object accept(StyleVisitor sv, Object o) {
        return sv.visit(this, o);
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Group{

        //jaxb binding
        @XmlElement(name = "Filter",namespace="http://geotoolkit.org")
        private FilterType filterType;
        @XmlElement(name = "Fill",namespace="http://geotoolkit.org")
        protected FillType fillType;
        @XmlElement(name = "Stroke",namespace="http://geotoolkit.org")
        protected StrokeType strokeType;
        @XmlElement(name = "Text",namespace="http://geotoolkit.org")
        private ParameterValueType textColor;

        //real style objects
        @XmlTransient
        private Filter filter;
        @XmlTransient
        private Stroke stroke;
        @XmlTransient
        private Fill fill;
        @XmlTransient
        private Expression text;

        public Group() {
        }

        public Group(Filter filter, Stroke stroke , Fill fill, Expression text) {
            setFilter(filter);
            setFill(fill);
            setStroke(stroke);
            setText(text);
        }

        public FilterType getFilterType() {
            return filterType;
        }

        public void setFilterType(FilterType filterType) {
            this.filterType = filterType;
            this.filter = null;
        }

        public FillType getFillType() {
            return fillType;
        }

        public void setFillType(FillType fill) {
            this.fillType = fill;
            this.fill = null;
        }

        public StrokeType getStrokeType() {
            return strokeType;
        }

        public void setStrokeType(StrokeType stroke) {
            this.strokeType = stroke;
            this.stroke = null;
        }

        public ParameterValueType getTextType() {
            return textColor;
        }

        public void setTextType(ParameterValueType textColor) {
            this.textColor = textColor;
            this.text = null;
        }

        public Filter getFilter() {
            if(filter != null){
                return filter;
            }else if(filterType != null){
                final StyleXmlIO util = new StyleXmlIO();
                try {
                    filter = util.getTransformer110().visitFilter(filterType);
                    return filter;
                } catch (FactoryException ex) {
                    LOGGER.log(Level.WARNING, ex.getMessage(), ex);
                }
            }
            return Filter.EXCLUDE;
        }

        public void setFilter(Filter filter) {
            this.filter = filter;
            final StyleXmlIO util = new StyleXmlIO();
            this.filterType = util.getTransformerXMLv110().visit(filter);
        }

        public Stroke getStroke() {
            if(stroke != null){
                return stroke;
            }else if(strokeType != null){
                final StyleXmlIO util = new StyleXmlIO();
                stroke = util.getTransformer110().visit(strokeType);
                return stroke;
            }
            return null;
        }

        public void setStroke(Stroke stroke) {
            this.stroke = stroke;
            final StyleXmlIO util = new StyleXmlIO();
            this.strokeType = util.getTransformerXMLv110().visit(stroke,null);
        }

        public Fill getFill() {
            if(fill != null){
                return fill;
            }else if(fillType != null){
                final StyleXmlIO util = new StyleXmlIO();
                fill = util.getTransformer110().visit(fillType);
                return fill;
            }
            return null;
        }

        public void setFill(Fill fill) {
            this.fill = fill;
            final StyleXmlIO util = new StyleXmlIO();
            this.fillType = util.getTransformerXMLv110().visit(fill,null);
        }

        public Expression getText() {
            if(text != null){
                return text;
            }else if(textColor != null){
                final StyleXmlIO util = new StyleXmlIO();
                text = util.getTransformer110().visitExpression(textColor);
                return text;
            }
            return null;
        }

        public void setText(Expression text) {
            this.text = text;
            final StyleXmlIO util = new StyleXmlIO();
            this.textColor = util.getTransformerXMLv110().visitExpression(text);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Group other = (Group) obj;
            if (this.filter != other.filter && (this.filter == null || !this.filter.equals(other.filter))) {
                return false;
            }
            if (this.stroke != other.stroke && (this.stroke == null || !this.stroke.equals(other.stroke))) {
                return false;
            }
            if (this.fill != other.fill && (this.fill == null || !this.fill.equals(other.fill))) {
                return false;
            }
            if (this.text != other.text && (this.text == null || !this.text.equals(other.text))) {
                return false;
            }
            return true;
        }
    }

}