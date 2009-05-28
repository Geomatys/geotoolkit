/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotoolkit.filter.binaryexpression;

import org.junit.Test;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;

import static org.junit.Assert.*;
import static org.geotoolkit.filter.FilterTestConstants.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class BinaryExpressionTest {

    
    public BinaryExpressionTest() {
    }

    @Test
    public void testAdd() {

        final double correctResult = 151;
        final Literal combineLiteral = FF.literal(50);

        PropertyName property;
        Expression exp;
        double result;

        property = FF.property("testShort");
        exp = FF.add(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

        property = FF.property("testInteger");
        exp = FF.add(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

        property = FF.property("testLong");
        exp = FF.add(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

        property = FF.property("testFloat");
        exp = FF.add(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

        property = FF.property("testDouble");
        exp = FF.add(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

    }

    @Test
    public void testDivide() {

        final double correctResult = 25.25;
        final Literal combineLiteral = FF.literal(4);

        PropertyName property;
        Expression exp;
        double result;

        property = FF.property("testShort");
        exp = FF.divide(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

        property = FF.property("testInteger");
        exp = FF.divide(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

        property = FF.property("testLong");
        exp = FF.divide(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

        property = FF.property("testFloat");
        exp = FF.divide(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

        property = FF.property("testDouble");
        exp = FF.divide(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

    }

    @Test
    public void testMultiply() {

        final double correctResult = 303;
        final Literal combineLiteral = FF.literal(3);

        PropertyName property;
        Expression exp;
        double result;

        property = FF.property("testShort");
        exp = FF.multiply(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

        property = FF.property("testInteger");
        exp = FF.multiply(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

        property = FF.property("testLong");
        exp = FF.multiply(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

        property = FF.property("testFloat");
        exp = FF.multiply(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

        property = FF.property("testDouble");
        exp = FF.multiply(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

    }

    @Test
    public void testSubtract() {

        final double correctResult = 31;
        final Literal combineLiteral = FF.literal(70);

        PropertyName property;
        Expression exp;
        double result;

        property = FF.property("testShort");
        exp = FF.subtract(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

        property = FF.property("testInteger");
        exp = FF.subtract(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

        property = FF.property("testLong");
        exp = FF.subtract(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

        property = FF.property("testFloat");
        exp = FF.subtract(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

        property = FF.property("testDouble");
        exp = FF.subtract(property,combineLiteral);
        result = exp.evaluate(FEATURE_1, Double.class);
        assertEquals(result, correctResult, 0.0000001d);

    }


}
