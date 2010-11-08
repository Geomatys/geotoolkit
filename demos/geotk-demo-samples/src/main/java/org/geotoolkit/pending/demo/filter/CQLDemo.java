

package org.geotoolkit.pending.demo.filter;

import java.util.List;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.filter.text.cql2.CQL;
import org.geotoolkit.filter.text.cql2.CQLException;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

public class CQLDemo {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static void main(String[] args) throws CQLException {

        final String filterTxt = "name = sorel and age > 20";
        Filter filter = CQL.toFilter(filterTxt);
        System.out.println(filter);

        System.out.println(CQL.toFilter("ATTR1 < 10 AND ATTR2 < 2 OR ATTR3 > 10"));
        System.out.println(CQL.toFilter("NAME = 'New York' "));
        System.out.println(CQL.toFilter("NAME LIKE 'New%' "));
        System.out.println(CQL.toFilter("NAME IS NULL"));
        System.out.println(CQL.toFilter("DATE BEFORE 2006-11-30T01:30:00Z"));
        System.out.println(CQL.toFilter("NAME DOES-NOT-EXIST"));
        System.out.println(CQL.toFilter("QUANTITY BETWEEN 10 AND 20"));
        System.out.println(CQL.toFilter("CROSS(SHAPE, LINESTRING(1 2, 10 15))"));
        System.out.println(CQL.toFilter("BBOX(SHAPE, 10,20,30,40)"));

        System.out.println(CQL.toExpression("NAME"));
        System.out.println(CQL.toExpression("QUANTITY * 2"));
        System.out.println(CQL.toExpression("strConcat(NAME, 'suffix')"));
        List filters = CQL.toFilterList("NAME IS NULL;BBOX(SHAPE, 10,20,30,40);INCLUDE");

    }

}
