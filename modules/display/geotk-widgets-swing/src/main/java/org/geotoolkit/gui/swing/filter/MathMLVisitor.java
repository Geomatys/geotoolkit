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

package org.geotoolkit.gui.swing.filter;

import java.awt.image.BufferedImage;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import net.sourceforge.jeuclid.LayoutContext.Parameter;
import net.sourceforge.jeuclid.MutableLayoutContext;
import net.sourceforge.jeuclid.context.LayoutContextImpl;
import net.sourceforge.jeuclid.converter.Converter;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.mathml.xml.MathMLUtilities;
import org.geotoolkit.mathml.xml.Mfenced;
import org.geotoolkit.mathml.xml.Mfrac;
import org.geotoolkit.mathml.xml.Mi;
import org.geotoolkit.mathml.xml.Mn;
import org.geotoolkit.mathml.xml.Mo;
import org.geotoolkit.mathml.xml.Mrow;
import org.geotoolkit.mathml.xml.ObjectFactory;
import org.opengis.filter.expression.Add;
import org.opengis.filter.expression.Divide;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.Multiply;
import org.opengis.filter.expression.NilExpression;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.expression.Subtract;
import org.w3c.dom.Document;

/**
 * Convert and expression in MathML
 * @author Johann Sorel (Geomatys)
 */
public final class MathMLVisitor implements ExpressionVisitor {

    public static final MathMLVisitor INSTANCE = new MathMLVisitor();
    private static final ObjectFactory MLFactory = new ObjectFactory();
    private static final String MAGENTA = "#FF00FF";
    private static final String GREEN = "#009600";
    private static final String BLUE = "#0000FF";

    private MathMLVisitor(){}

    @Override
    public Object visit(NilExpression expression, Object extraData) {
        final String strVal = "NIL";
        final Mi lit = new Mi();
        lit.getContent().add(strVal);
        return MLFactory.createMi(lit);
    }

    @Override
    public Object visit(Add expression, Object extraData) {
        final Expression exp1 = expression.getExpression1();
        final Expression exp2 = expression.getExpression2();
        final Mrow row = new Mrow();
        final Mo operator = new Mo();
        operator.getContent().add(" + ");

        row.getMathExpression().add(fence(exp1.accept(this, extraData),exp1));
        row.getMathExpression().add(MLFactory.createMo(operator));
        row.getMathExpression().add(fence(exp2.accept(this, extraData),exp2));
        return MLFactory.createMrow(row);
    }

    @Override
    public Object visit(Divide expression, Object extraData) {
        final Mfrac row = new Mfrac();
        row.getContent().add(expression.getExpression1().accept(this, extraData));
        row.getContent().add(expression.getExpression2().accept(this, extraData));
        return MLFactory.createMfrac(row);
    }

    @Override
    public Object visit(Function expression, Object extraData) {
        final Mrow row = new Mrow();

        final Mi lit = new Mi();
        //lit.setMathvariant("bold");
        lit.setMathcolor(MAGENTA);
        lit.getContent().add(expression.getName());
        row.getMathExpression().add(MLFactory.createMi(lit));

        final Mfenced fence = new Mfenced();
        for(Expression sub : expression.getParameters()){
            fence.getMathExpression().add(sub.accept(this, extraData));
        }
        row.getMathExpression().add(MLFactory.createMfenced(fence));

        return MLFactory.createMrow(row);
    }

    @Override
    public Object visit(Literal expression, Object extraData) {
        final Object obj = expression.getValue();

        if(obj instanceof Number){
            final Mn lit = new Mn();
            lit.setMathcolor(GREEN);
            lit.getContent().add(obj.toString());
            return MLFactory.createMn(lit);
        }else{
            final String strVal = String.valueOf(obj);
            final Mi lit = new Mi();
            lit.setMathcolor(GREEN);
            lit.getContent().add(strVal);
            return MLFactory.createMi(lit);
        }

    }

    @Override
    public Object visit(Multiply expression, Object extraData) {
        final Expression exp1 = expression.getExpression1();
        final Expression exp2 = expression.getExpression2();
        final Mrow row = new Mrow();
        final Mo operator = new Mo();
        operator.getContent().add(" × ");

        row.getMathExpression().add(fence(exp1.accept(this, extraData),exp1));
        row.getMathExpression().add(MLFactory.createMo(operator));
        row.getMathExpression().add(fence(exp2.accept(this, extraData),exp2));
        return MLFactory.createMrow(row);
    }

    @Override
    public Object visit(PropertyName expression, Object extraData) {
        final String strVal = String.valueOf(expression.getPropertyName());
        final Mi prop = new Mi();
        prop.setMathvariant("bold");
        prop.setMathcolor(BLUE);
        prop.getContent().add(strVal);
        return MLFactory.createMi(prop);
    }

    @Override
    public Object visit(Subtract expression, Object extraData) {
        final Expression exp1 = expression.getExpression1();
        final Expression exp2 = expression.getExpression2();
        final Mrow row = new Mrow();
        final Mo operator = new Mo();
        operator.getContent().add(" − ");

        row.getMathExpression().add(fence(exp1.accept(this, extraData),exp1));
        row.getMathExpression().add(MLFactory.createMo(operator));
        row.getMathExpression().add(fence(exp2.accept(this, extraData),exp2));
        return MLFactory.createMrow(row);
    }

    private Object fence(Object element, Expression exp){
        if(exp instanceof Literal || exp instanceof PropertyName || exp instanceof Function){
            return element;
        }else{
            final Mfenced fence = new Mfenced();
            fence.getMathExpression().add(element);
            return MLFactory.createMfenced(fence);
        }
    }

    public static Document toMathML(Expression exp) throws Exception{

        final Object ele = exp.accept(INSTANCE, null);

        final org.geotoolkit.mathml.xml.Math doc = new org.geotoolkit.mathml.xml.Math();
        doc.getMathExpression().add(ele);

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document dom = builder.newDocument();

        final MarshallerPool pool = MathMLUtilities.getMarshallerPool();
        final Marshaller marshaller = pool.acquireMarshaller();
        marshaller.marshal(doc, dom);
        pool.recycle(marshaller);

        return dom;
    }

    public static BufferedImage toImage(Document doc) throws Exception {
        final MutableLayoutContext params = new LayoutContextImpl(LayoutContextImpl.getDefaultLayoutContext());
        params.setParameter(Parameter.MATHSIZE, 25f);
        return Converter.getConverter().render(doc, params);
    }


}
