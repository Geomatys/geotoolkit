

package org.geotoolkit.filter.function;

import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

public interface FunctionFactory {

    String getName();

    Function createFunction(Literal fallback, Expression ... parameters);

}
