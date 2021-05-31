/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2009, Geomatys
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

package org.geotoolkit.display2d.ext.pattern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.apache.sis.measure.Units;
import org.geotoolkit.se.xml.v110.ParameterValueType;
import org.geotoolkit.se.xml.v110.SymbolizerType;
import org.geotoolkit.se.xml.v110.ThreshholdsBelongToType;
import org.geotoolkit.se.xml.vext.RangeType;
import org.geotoolkit.sld.xml.GTtoSLD110Transformer;
import org.geotoolkit.sld.xml.SLD110toGTTransformer;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.geotoolkit.style.function.ThreshholdsBelongTo;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.style.ExtensionSymbolizer;
import org.opengis.style.StyleVisitor;
import org.opengis.style.Symbolizer;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PatternSymbolizerType")
@XmlRootElement(name="PatternSymbolizer",namespace="http://geotoolkit.org")
public final class PatternSymbolizer extends SymbolizerType implements ExtensionSymbolizer{

    public static final String NAME = "Pattern";

    private static final class ExpComparator implements Comparator<Expression>{

        @Override
        public int compare(final Expression t, final Expression t1) {
            final Literal left = (Literal) t;
            final Literal right = (Literal) t1;

            if(left == null || left.getValue() == null){
                return -1;
            }else if(right == null || right.getValue() == null){
                return +1;
            }else{
                final Number leftN = left.evaluate(null,Number.class);
                final Number righN = right.evaluate(null,Number.class);
                final double res = leftN.doubleValue() - righN.doubleValue();
                if(res < 0){
                    return -1;
                }else if(res > 0){
                    return +1;
                }else{
                    return 0;
                }
            }
        }

    }


    @XmlElement(name = "Channel")
    protected ParameterValueType channelType;
    @XmlElementRef(name = "Range", namespace = "http://www.opengis.net/se", type = JAXBElement.class)
    protected List<JAXBElement<RangeType>> range;
    @XmlAttribute(name = "ThreshholdsBelongToType")
    protected ThreshholdsBelongToType threshholdsBelongTo;

    @XmlTransient
    private Expression channel;
    @XmlTransient
    private ThreshholdsBelongTo belongTo;
    @XmlTransient
    private Map<Expression, List<Symbolizer>> thredholds;

    public PatternSymbolizer() {
    }

    public PatternSymbolizer(final Expression channel, final Map<Expression,List <Symbolizer>> ranges, final ThreshholdsBelongTo belong) {
        setChannel(channel);
        setBelongTo(belong);
        setRanges(ranges);
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

    /**
     * Graduation tick size/length in pixel
     */
    public Expression getChannel() {
        if (channel == null && channelType != null) {
            channel = new StyleXmlIO().getTransformer110().visitExpression(channelType);
        }
        return channel;
    }

    public void setChannel(Expression value) {
        this.channel = value;
        this.channelType = new StyleXmlIO().getTransformerXMLv110().visitExpression(value);
    }

    public ThreshholdsBelongTo getBelongTo(){
        if (belongTo == null && threshholdsBelongTo != null) {
            switch (threshholdsBelongTo) {
                case PRECEDING : belongTo = ThreshholdsBelongTo.PRECEDING; break;
                case SUCCEEDING : belongTo = ThreshholdsBelongTo.SUCCEEDING; break;
            }
        }
        return belongTo;
    }

    public void setBelongTo(ThreshholdsBelongTo value) {
        this.belongTo = value;
        switch (value) {
            case PRECEDING : threshholdsBelongTo = ThreshholdsBelongToType.PRECEDING; break;
            case SUCCEEDING : threshholdsBelongTo = ThreshholdsBelongToType.SUCCEEDING; break;
        }
    }

    public Map<Expression, List<Symbolizer>> getRanges() {
        if (thredholds == null && range != null) {
            thredholds = new TreeMap<>(new ExpComparator());

            final SLD110toGTTransformer io = new StyleXmlIO().getTransformer110();
            final Map<Expression, List<Symbolizer>> visitRanges = io.visitRanges(range);
            thredholds.putAll(visitRanges);
        }

        return thredholds;
    }

    public void setRanges(Map<Expression, List<Symbolizer>> range) {
        this.thredholds = new TreeMap<>(new ExpComparator());
        if (range != null) this.thredholds.putAll(range);

        final GTtoSLD110Transformer io = new StyleXmlIO().getTransformerXMLv110();
        final List<JAXBElement<RangeType>> ranges = new ArrayList<>();
        for (Entry<Expression,List<Symbolizer>> entry : this.thredholds.entrySet()) {
            final JAXBElement<RangeType> rt = io.visitRange(entry.getKey(), entry.getValue());
            ranges.add(rt);
        }

        this.range = ranges;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Pattern symbolizer \n");

        for(Map.Entry<Expression,List<Symbolizer>> entry : thredholds.entrySet()){
            System.out.println(" - " + entry.getKey());
            for(Symbolizer s : entry.getValue()){
                System.out.println(" - - " + s);
            }
        }

        return sb.toString();
    }

}
