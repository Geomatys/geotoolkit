

package org.geotoolkit.pending.demo.filter;

import org.geotoolkit.factory.FactoryFinder;
import org.apache.sis.feature.builder.FeatureTypeBuilder;
import org.geotoolkit.filter.function.javascript.JavaScriptFunctionFactory;
import org.geotoolkit.pending.demo.Demos;
import org.opengis.feature.Feature;
import org.opengis.feature.FeatureType;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Literal;


public class JavascriptFunctionDemo {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static void main(String[] args) {
        Demos.init();
        
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
        ftb.addAttribute(String.class).setName("family");
        ftb.addAttribute(Double.class).setName("depth");
        ftb.addAttribute(Integer.class).setName("age");
        ftb.addAttribute(Float.class).setName("weight");
        final FeatureType type = ftb.build();

        final Feature feature1 = type.newInstance();
        feature1.setPropertyValue("family","seashell");
        feature1.setPropertyValue("depth",46.58);
        feature1.setPropertyValue("age",31);
        feature1.setPropertyValue("weight",132.56);

        return feature1;
    }

}
