

package org.geotoolkit.pending.demo.filter;

import java.util.Collection;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.filter.function.FunctionFactory;
import org.geotoolkit.filter.function.Functions;
import org.geotoolkit.filter.function.math.MathFunctionFactory;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.util.converter.Classes;
import org.opengis.feature.Feature;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;


public class ExpressionDemo {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static void main(String[] args) {

        final FeatureCollection collection = FilterDemo.createSampleCollection();

        testExpression(collection, mathExpression());
        testExpression(collection, functionExpression());

    }

    private static void testExpression(FeatureCollection collection, Expression exp){
        System.out.println("\n==============================================================\n");
        System.out.println(exp);
        System.out.println('\n');

        final FeatureIterator ite = collection.iterator();
        try{
            while(ite.hasNext()){
                final Feature candidate = ite.next();
                System.out.println(exp.evaluate(candidate));
            }
        }finally{
            ite.close();
        }
    }

    private static Expression mathExpression(){
        final Expression multi = FF.multiply(FF.property("age"), FF.literal(3));
        final Expression add = FF.add(multi, FF.literal(10));
        return add;
    }

    private static Expression functionExpression(){

        //display all available functions
        System.out.println("\n==============================================================\n");
        final Collection<FunctionFactory> factories = Functions.getFactories();
        for(FunctionFactory ff : factories){
            System.out.println(Classes.getShortClassName(ff));
            System.out.println(StringUtilities.toStringTree((Object[])ff.getNames()));
        }

        final Function function = Functions.function(MathFunctionFactory.COS, null, FF.property("age"));
        return function;
    }

}
