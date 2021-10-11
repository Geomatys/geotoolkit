

package org.geotoolkit.pending.demo.filter;

import java.util.Collection;
import java.util.Iterator;
import org.apache.sis.internal.filter.FunctionRegister;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.storage.FeatureSet;
import org.apache.sis.util.Classes;
import org.geotoolkit.filter.FilterUtilities;
import org.geotoolkit.filter.function.Functions;
import org.geotoolkit.filter.function.math.MathFunctionFactory;
import org.geotoolkit.pending.demo.Demos;
import org.geotoolkit.util.StringUtilities;
import org.opengis.feature.Feature;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Expression;


public class ExpressionDemo {

    private static final FilterFactory FF = FilterUtilities.FF;

    public static void main(String[] args) throws DataStoreException {
        Demos.init();

        final FeatureSet collection = FilterDemo.createSampleCollection();

        testExpression(collection, mathExpression());
        testExpression(collection, functionExpression());

    }

    private static void testExpression(FeatureSet collection, Expression exp) throws DataStoreException{
        System.out.println("\n==============================================================\n");
        System.out.println(exp);
        System.out.println('\n');

        final Iterator<Feature> ite = collection.features(false).iterator();
        while(ite.hasNext()){
            final Feature candidate = ite.next();
            System.out.println(exp.apply(candidate));
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
        final Collection<FunctionRegister> factories = Functions.getFactories();
        for(FunctionRegister ff : factories){
            System.out.println(Classes.getShortClassName(ff));
            System.out.println(StringUtilities.toStringTree(ff.getNames()));
        }
        final Expression function = Functions.function(MathFunctionFactory.COS, null, FF.property("age"));
        return function;
    }
}
