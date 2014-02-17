

package org.geotoolkit.pending.demo.filter;

import java.util.List;
import org.geotoolkit.cql.CQL;
import org.geotoolkit.cql.CQLException;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.pending.demo.Demos;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;

public class CQLDemo {

    private static final FilterFactory FF = FactoryFinder.getFilterFactory(null);

    public static void main(String[] args) throws CQLException {
        Demos.init();
        
        final String filterTxt = "name = sorel and age > 20";
        Filter filter = CQL.parseFilter(filterTxt);
        System.out.println(filter);

        System.out.println(CQL.parseFilter("ATTR1 < 10 AND ATTR2 < 2 OR ATTR3 > 10"));
        System.out.println(CQL.parseFilter("NAME = 'New York' "));
        System.out.println(CQL.parseFilter("NAME LIKE 'New%' "));
        System.out.println(CQL.parseFilter("NAME IS NULL"));
        System.out.println(CQL.parseFilter("DATE BEFORE 2006-11-30T01:30:00Z"));
        System.out.println(CQL.parseFilter("NAME DOES-NOT-EXIST"));
        System.out.println(CQL.parseFilter("QUANTITY BETWEEN 10 AND 20"));
        System.out.println(CQL.parseFilter("CROSS(SHAPE, LINESTRING(1 2, 10 15))"));
        System.out.println(CQL.parseFilter("BBOX(SHAPE, 10,20,30,40)"));

        System.out.println(CQL.parseExpression("NAME"));
        System.out.println(CQL.parseExpression("QUANTITY * 2"));
        System.out.println(CQL.parseExpression("strConcat(NAME, 'suffix')"));
        
        String cqlfilter = CQL.write(filter);
        String cqlexpression = CQL.write(FF.literal("hello"));

    }

}
