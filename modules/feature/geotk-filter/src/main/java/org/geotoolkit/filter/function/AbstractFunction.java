

package org.geotoolkit.filter.function;

import java.util.List;
import org.geotoolkit.filter.expression.AbstractExpression;
import org.geotoolkit.util.collection.UnmodifiableArrayList;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.ExpressionVisitor;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;

public abstract class AbstractFunction extends AbstractExpression implements Function {

    protected final String name;
    protected final List<Expression> parameters;
    protected final Literal fallback;

    public AbstractFunction(String name, Expression[] parameters, Literal fallback) {
        if(name == null){
            throw new NullPointerException("name can not be null");
        }
        this.name = name;
        this.parameters = UnmodifiableArrayList.wrap(parameters);
        this.fallback = fallback;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Expression> getParameters() {
        return parameters;
    }

    @Override
    public Literal getFallbackValue() {
        return fallback;
    }

    @Override
    public Object accept(ExpressionVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }

}
