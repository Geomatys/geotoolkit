/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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

import java.lang.annotation.Annotation;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;
import junit.framework.TestResult;

import org.junit.Test;
import org.opengis.filter.Filter;

import com.vividsolutions.jts.util.Assert;


/**
 * FilterToCQLTest
 *
 * Unit test for FilterToCQL
 *
 * @author Johann Sorel
 */
public class FilterToCQLTest extends TestCase {

    FilterToCQL toCQL;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        toCQL = new FilterToCQL();
    }

    public void testSample() throws Exception {
        Filter filter = CQL.toFilter(FilterCQLSample.LESS_FILTER_SAMPLE);

        String output = filter.accept( toCQL, null ).toString();
        assertNotNull( output );
        assertEquals( FilterCQLSample.LESS_FILTER_SAMPLE, output );
    }
    /* NOT (ATTR1 BETWEEN 10 AND 20) */
    public void testNotBetween() throws Exception {
        cqlTest( "NOT (ATTR1 BETWEEN 10 AND 20)" );
    }
    /* ((ATTR1 < 10 AND ATTR2 < 2) OR ATTR3 > 10) */
    public void testANDOR() throws Exception {
        cqlTest( "((ATTR1 < 10 AND ATTR2 < 2) OR ATTR3 > 10)" );
    }
    /** (ATTR1 > 10 OR ATTR2 < 2) */
    public void testOR() throws Exception {
        cqlTest( "(ATTR1 > 10 OR ATTR2 < 2)" );
    }
    protected void cqlTest( String cql ) throws Exception {
        Filter filter = CQL.toFilter(cql);
        assertNotNull( cql + " parse", filter );

        String output = filter.accept( toCQL, null ).toString();
        assertNotNull( cql + " encode", output );
        assertEquals( cql, cql, output );
    }
    /*
    public static Test suite(){
        TestSuite suite = new TestSuite();
        suite.addTestSuite(FilterToCQLTest.class );
        for( String cql : FilterSample.SAMPLES.keySet() ){
            suite.addTest( new CQLTest( cql ));
        }
        return suite;
    }
    */
    static class CQLTest2 extends Assert implements Test {
        String cql;
        CQLTest2( String cql ){
            this.cql = cql;
        }
        public int countTestCases() {
            return 1;
        }

        public void run(TestResult result) {
            result.startTest( (junit.framework.Test) this );
            try {
                Filter filter = CQL.toFilter( cql );

                FilterToCQL toCQL = new FilterToCQL();
                String output = filter.accept( toCQL, null ).toString();
                assertNotNull( output );
                assertEquals( cql, output );
            }
            catch (AssertionError fail){
                result.addFailure(
                        (junit.framework.Test) this, new AssertionFailedError( fail.getMessage() ));
            }
            catch (Throwable t ){
                result.addError( (junit.framework.Test) this, t );
            }
            finally {
                result.endTest((junit.framework.Test) this);
            }
        }
        @Override
        public String toString() {
            return cql;
        }
        public Class<? extends Throwable> expected() {
            return null;
        }
        public long timeout() {
            return 0;
        }
        public Class<? extends Annotation> annotationType() {
            return null;
        }

    }
}
