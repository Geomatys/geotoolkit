/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geotoolkit.filter.expression;

/**
 *
 * @author sorel
 */
public enum ExpressionType {
    /* Types implemented by ExpressionLiteral */
    /** Defines a literal expression with an undeclared type. */
    LITERAL_UNDECLARED,
    /** Defines a literal expression with a declared double type. */
    LITERAL_DOUBLE,
    /** Defines a literal expression with a declared integer type. */
    LITERAL_INTEGER,
    /** Defines a literal expression with a declared string type. */
    LITERAL_STRING,
    /** Defines a literal expression with a declared geometry type. */
    LITERAL_GEOMETRY,
    /** Defines a literal expression with a declared long type. */
    LITERAL_LONG,
    /* Types implemented by ExpressionMath. */
    /** Defines a math expression for adding. */
    MATH_ADD,
    /** Defines a math expression for subtracting. */
    MATH_SUBTRACT,
    /** Defines a math expression for multiplying. */
    MATH_MULTIPLY,
    /** Defines a math expression for dividing. */
    MATH_DIVIDE,
    /* Types implemented by ExpressionAttribute. */
    /** Defines an attribute expression with a declared double type. */
    ATTRIBUTE_DOUBLE,
    /** Defines an attribute expression with a declared integer type. */
    ATTRIBUTE_INTEGER,
    /** Defines an attribute expression with a declared string type. */
    ATTRIBUTE_STRING,
    /** Defines an attribute expression with a declared string type. */
    ATTRIBUTE_GEOMETRY,
    /** Defines an attribute expression with a declared string type. */
    ATTRIBUTE_UNDECLARED,
    /** Defines an attribute expression with a declared string type. */
    ATTRIBUTE,
    /** Defines a function expression */
    FUNCTION;
}
