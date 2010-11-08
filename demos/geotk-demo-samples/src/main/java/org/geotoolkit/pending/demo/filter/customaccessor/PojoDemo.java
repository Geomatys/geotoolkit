

package org.geotoolkit.pending.demo.filter.customaccessor;

import java.util.Date;
import org.geotoolkit.factory.FactoryFinder;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.Expression;

public class PojoDemo {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static void main(String[] args) {

        final Pojo myPojo = new Pojo("squid", 1200, new Date());

        Expression exp = FF.property("family");
        System.out.println(exp.evaluate(myPojo));

        exp = FF.property("depth");
        System.out.println(exp.evaluate(myPojo));

        exp = FF.property("birth");
        System.out.println(exp.evaluate(myPojo));

    }

}
