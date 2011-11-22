/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.filter.text.cql2;

import com.vividsolutions.jts.geom.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotoolkit.filter.DefaultLiteral;
import org.geotoolkit.filter.DefaultPropertyName;
import org.geotoolkit.filter.text.commons.CompilerUtil;
import org.geotoolkit.filter.text.commons.Language;
import org.geotoolkit.util.logging.Logging;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.Crosses;
import org.opengis.filter.spatial.Disjoint;
import org.opengis.filter.spatial.Equals;
import org.opengis.filter.spatial.Intersects;
import org.opengis.filter.spatial.Overlaps;
import org.opengis.filter.spatial.Touches;
import org.opengis.filter.spatial.Within;


/**
 * Test Geo Operations.
 * <p>
 *
 * <pre>
 *   &lt;routine invocation &gt; ::=
 *           &lt;geoop name &gt; &lt;georoutine argument list &gt;[*]
 *       |   &lt;relgeoop name &gt; &lt;relgeoop argument list &gt;
 *       |   &lt;routine name &gt; &lt;argument list &gt;
 *   &lt;geoop name &gt; ::=
 *           EQUAL | DISJOINT | INTERSECT | TOUCH | CROSS | [*]
 *           WITHIN | CONTAINS |OVERLAP | RELATE [*]
 *   That rule is extended with bbox for convenience.
 *   &lt;bbox argument list &gt;::=
 *       &quot;(&quot;  &lt;attribute &gt; &quot;,&quot; &lt;min X &gt; &quot;,&quot; &lt;min Y &gt; &quot;,&quot; &lt;max X &gt; &quot;,&quot; &lt;max Y &gt;[&quot;,&quot;  &lt;srs &gt;] &quot;)&quot;
 *       &lt;min X &gt; ::=  &lt;signed numerical literal &gt;
 *       &lt;min Y &gt; ::=  &lt;signed numerical literal &gt;
 *       &lt;max X &gt; ::=  &lt;signed numerical literal &gt;
 *       &lt;max Y &gt; ::=  &lt;signed numerical literal &gt;
 *       &lt;srs &gt; ::=
 * </pre>
 *
 * </p>
 *
 * @author Mauricio Pazos (Axios Engineering)
 * @module pending
 * @since 2.6
 */
public class CQLGeoOperationTest {

    protected Language language;

    protected static final Logger LOGGER = Logging.getLogger(CQLGeoOperationTest.class);
    
    /**
     * New instance of CQLTemporalPredicateTest
     */
    public CQLGeoOperationTest(){
        language = Language.CQL;
    }

    @Test
    public void disjoint() throws CQLException{


        Filter resultFilter = CompilerUtil.parseFilter(language, "DISJOINT(ATTR1, POINT(1 2))");

        Assert.assertTrue("Disjoint was expected", resultFilter instanceof Disjoint);
    }

    @Test
    public void Intersects() throws CQLException {
        Filter resultFilter = CompilerUtil.parseFilter(language,"INTERSECT(ATTR1, POINT(1 2))");

        Assert.assertTrue("Intersects was expected", resultFilter instanceof Intersects);

        //test bug GEOT-1980
        CompilerUtil.parseFilter(language,"INTERSECT(GEOLOC, POINT(615358 312185))");

        Assert.assertTrue("Intersects was expected", resultFilter instanceof Intersects);
    }

    @Test
    public void touches() throws CQLException{
        Filter resultFilter = CompilerUtil.parseFilter(language,"TOUCH(ATTR1, POINT(1 2))");

        Assert.assertTrue("Touches was expected", resultFilter instanceof Touches);
    }

    public void crosses() throws CQLException {
        Filter resultFilter = CompilerUtil.parseFilter(language,"CROSS(ATTR1, POINT(1 2))");

        Assert.assertTrue("Crosses was expected", resultFilter instanceof Crosses);

    }
    @Test
    public void contains() throws CQLException      {
        Filter resultFilter = CompilerUtil.parseFilter(language,"CONTAINS(ATTR1, POINT(1 2))");

        Assert.assertTrue("Contains was expected", resultFilter instanceof Contains);

    }
    
    @Test
    public void containsPT3D() throws CQLException      {
        Filter resultFilter = CompilerUtil.parseFilter(language,"CONTAINS(ATTR1, POINT3D(1 2 3))");

        Assert.assertTrue("Contains was expected", resultFilter instanceof Contains);
        Contains cfilter = (Contains) resultFilter;
        Assert.assertEquals(new DefaultPropertyName("ATTR1"), cfilter.getExpression1());
        
        Assert.assertTrue("Literal was expected but was "+ cfilter.getExpression2().getClass().getName(), cfilter.getExpression2() instanceof DefaultLiteral);
        DefaultLiteral lit = (DefaultLiteral) cfilter.getExpression2();
        Assert.assertTrue("Point was expected but was "+ lit.getValue().getClass().getName(), lit.getValue() instanceof Point);
        Point p = (Point) lit.getValue();
        Assert.assertEquals(1, p.getX(), 0);
        Assert.assertEquals(2, p.getY(), 0);
        Assert.assertEquals(3, p.getCoordinate().z, 0);
    }

    @Test
    public void overlaps() throws Exception {
        Filter resultFilter;


        resultFilter = CompilerUtil.parseFilter(language,"OVERLAP(ATTR1, POINT(1 2))");

        Assert.assertTrue("Overlaps was expected", resultFilter instanceof Overlaps);


    }
    @Test
    public void equals() throws CQLException{
        // EQUALS
        Filter resultFilter = CompilerUtil.parseFilter(language,"EQUAL(ATTR1, POINT(1 2))");

        Assert.assertTrue("not an instance of Equals", resultFilter instanceof Equals);

        resultFilter = CompilerUtil.parseFilter(language,"WITHIN(ATTR1, POLYGON((1 2, 1 10, 5 10, 1 2)) )");

        Assert.assertTrue("Within was expected", resultFilter instanceof Within);

    }

    @Test
    public void bbox() throws CQLException{

        Filter resultFilter;

        // BBOX
        resultFilter = CompilerUtil.parseFilter(language,"BBOX(ATTR1, 10.0,20.0,30.0,40.0)");
        Assert.assertTrue("BBox was expected", resultFilter instanceof BBOX);
        BBOX bboxFilter = (BBOX) resultFilter;
        Assert.assertEquals(bboxFilter.getMinX(), 10.0, 0.1);
        Assert.assertEquals(bboxFilter.getMinY(), 20.0, 0.1);
        Assert.assertEquals(bboxFilter.getMaxX(), 30.0, 0.1);
        Assert.assertEquals(bboxFilter.getMaxY(), 40.0, 0.1);
        Assert.assertEquals(null, bboxFilter.getSRS());

        // BBOX using EPSG
        resultFilter = CompilerUtil.parseFilter(language,"BBOX(ATTR1, 10.0,20.0,30.0,40.0, 'EPSG:4326')");
        Assert.assertTrue("BBox was expected", resultFilter instanceof BBOX);
        bboxFilter = (BBOX) resultFilter;
        Assert.assertEquals("EPSG:4326", bboxFilter.getSRS());

    }

}
