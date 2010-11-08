

package org.geotoolkit.pending.demo.filter;

import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.filter.function.javascript.JavaScriptFunctionFactory;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;


public class JavascriptFunctionDemo {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static void main(String[] args) {

        final Feature feature = aFeature();

        final String mathematicText =
                "x = $depth - 6 * $age;     " +
                "y = Math.cos($weight);     " +
                "z = 0;                     " +
                "if(x<0) z = 10;            " +
                "else z = y/x;              " +
                "z;                         ";

        final Literal formula = FF.literal(mathematicText);
        final Expression exp = FF.function(JavaScriptFunctionFactory.JAVASCRIPT, formula);

        final Object result = exp.evaluate(feature);
        System.out.println("JavaScript result = " + result);



    }

    private static Feature aFeature(){

        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        ftb.setName("marine-life");
        ftb.add("family", String.class);
        ftb.add("depth", Double.class);
        ftb.add("age", Integer.class);
        ftb.add("weight", Float.class);
        final FeatureType type = ftb.buildFeatureType();

        final Feature feature1 = FeatureUtilities.defaultFeature(type, "id-1");
        feature1.getProperty("family").setValue("seashell");
        feature1.getProperty("depth").setValue(46.58);
        feature1.getProperty("age").setValue(31);
        feature1.getProperty("weight").setValue(132.56);

        return feature1;
    }

}
