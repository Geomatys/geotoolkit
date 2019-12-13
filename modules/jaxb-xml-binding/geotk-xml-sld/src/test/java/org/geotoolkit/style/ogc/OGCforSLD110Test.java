/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.style.ogc;

import javax.xml.bind.JAXBException;
import org.geotoolkit.sld.xml.StyleXmlIO;
import org.junit.Test;
import org.opengis.filter.Filter;
import org.opengis.filter.Or;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Literal;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;
import org.opengis.filter.spatial.BBOX;
import org.geotoolkit.geometry.BoundingBox;
import org.opengis.util.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.referencing.CommonCRS;
import static org.junit.Assert.*;

/**
 * Test class for Filter and Expression jaxb marshelling and unmarshelling.
 *
 * @author Johann Sorel (Geomatys)
 * @module
 */
public class OGCforSLD110Test {

    @Test
    public void testCustom1() throws JAXBException, FactoryException{

        StyleXmlIO util = new StyleXmlIO();
        final Filter filter = util.readFilter(OGCforSLD110Test.class.getResource("/org/geotoolkit/test/filter/filterbbox.xml"), org.geotoolkit.sld.xml.Specification.Filter.V_1_1_0);

        assertTrue(filter instanceof Or);

        final Or or = (Or) filter;
        final Filter f1 = or.getChildren().get(0);
        final Filter f2 = or.getChildren().get(1);
        assertTrue(f1 instanceof PropertyIsEqualTo);
        assertTrue(f2 instanceof BBOX);

        final PropertyIsEqualTo ff1 = (PropertyIsEqualTo) f1;
        final BBOX ff2 = (BBOX) f2;

        assertTrue(ff1.getExpression1() instanceof PropertyName);
        assertTrue(ff1.getExpression2() instanceof Literal);
        assertTrue(ff2.getExpression1() instanceof PropertyName);
        assertTrue(ff2.getExpression2() instanceof Literal);

        assertEquals("sf:str4Property", ((PropertyName)ff1.getExpression1()).getPropertyName());
        assertEquals("abc3", ((Literal)ff1.getExpression2()).getValue());
        assertEquals("sf:attribut.Géométrie", ((PropertyName)ff2.getExpression1()).getPropertyName());

        final BoundingBox geom = (BoundingBox) ((Literal)ff2.getExpression2()).getValue();
        assertEquals(34d,geom.getMinX(),1e-7);
        assertEquals(40d,geom.getMaxX(),1e-7);
        assertEquals(15d,geom.getMinY(),1e-7);
        assertEquals(19d,geom.getMaxY(),1e-7);
        final CoordinateReferenceSystem crs = geom.getCoordinateReferenceSystem();
        assertEquals(CommonCRS.WGS84.geographic(), crs);

    }

    @Test
    public void testCustom2() throws JAXBException, FactoryException {
        StyleXmlIO util = new StyleXmlIO();
        final SortBy sort = util.readSortBy(OGCforSLD110Test.class.getResource("/org/geotoolkit/test/filter/sortby.xml"), org.geotoolkit.sld.xml.Specification.Filter.V_1_1_0);

        assertEquals("sf:str4Property", sort.getPropertyName().getPropertyName());
        assertEquals(SortOrder.ASCENDING, sort.getSortOrder());
    }
}
