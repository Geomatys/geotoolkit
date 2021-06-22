/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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

package org.geotoolkit.filter.binding;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.geotoolkit.filter.DefaultPropertyName;
import org.geotoolkit.filter.FilterTestConstants;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.geotoolkit.filter.FilterTestConstants.*;
import org.junit.Ignore;
import org.opengis.feature.Attribute;
import org.opengis.feature.Feature;
import org.opengis.filter.ValueReference;


/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class XPathBindingTest extends org.geotoolkit.test.TestBase {

    @Ignore
    @Test
    public void testJaxenXPath() throws JaxenException {
        List lst = null;
        String exp;

        // flat attribut test //////////////////////////////////////////////////

        exp = "/{http://test.com}attString";
        XPath xpath = JaxenFeatureXPath.create(exp);
        lst = xpath.selectNodes(CX_FEATURE);

        assertEquals(2, lst.size());
        assertEquals("toto1", ((Attribute)lst.get(0)).getValue());
        assertEquals("toto2", ((Attribute)lst.get(1)).getValue());

        // sub path attribut ///////////////////////////////////////////////////

        exp = "/{http://test.com}attCpx/{http://test.com}attString";
        xpath = JaxenFeatureXPath.create(exp);
        lst = xpath.selectNodes(CX_FEATURE);

        assertEquals(2, lst.size());
        assertEquals("toto19", ((Attribute)lst.get(0)).getValue());
        assertEquals("toto41", ((Attribute)lst.get(1)).getValue());

        //access on multiple different namespaces
        exp = "/attCpx/attString";
        xpath = JaxenFeatureXPath.create(exp);
        lst = xpath.selectNodes(CX_FEATURE);

        assertEquals(7, lst.size());
        assertEquals("toto19", ((Attribute)lst.get(0)).getValue());
        assertEquals("marcel1", ((Attribute)lst.get(1)).getValue());
        assertEquals("marcel5", ((Attribute)lst.get(2)).getValue());
        assertEquals("toto41", ((Attribute)lst.get(3)).getValue());
        assertEquals("marcel2", ((Attribute)lst.get(4)).getValue());
        assertEquals("marcel3", ((Attribute)lst.get(5)).getValue());
        assertEquals("marcel5", ((Attribute)lst.get(6)).getValue());

        // sub path attribut ///////////////////////////////////////////////////

        exp = "/{http://test.com}attCpx[{http://test2.com}attString='marcel2']";
        xpath = JaxenFeatureXPath.create(exp);
        lst = xpath.selectNodes(CX_FEATURE);
        assertEquals(1, lst.size());
        Iterator<Feature> ite = ((Collection)CX_FEATURE.getPropertyValue("attCpx")).iterator();
        ite.next();
        assertEquals(ite.next(), lst.get(0));

        //same without namespace
        exp = "/attCpx[attString='marcel2']";
        xpath = JaxenFeatureXPath.create(exp);
        lst = xpath.selectNodes(CX_FEATURE);
        assertEquals(1, lst.size());
        ite = ((Collection)CX_FEATURE.getPropertyValue("attCpx")).iterator();
        ite.next();
        assertEquals(ite.next(), lst.get(0));

        // numerique value filter test /////////////////////////////////////////
        exp = "/attCpx[attDouble=45]";
        xpath = JaxenFeatureXPath.create(exp);
        lst = xpath.selectNodes(CX_FEATURE);
        assertEquals(2, lst.size());
    }

    @Test
    public void testDoubleSlashXPath(){

        final String exp = "//gml:testString";
        final ValueReference pn = new DefaultPropertyName(exp);
        final Object result = pn.apply(FilterTestConstants.FEATURE_1);
        assertEquals("test string data", result);
    }

    @Test
    public void testStarXPath(){
        final String exp = "*[10]";
        final ValueReference pn = new DefaultPropertyName(exp);
        final Object result = pn.apply(FilterTestConstants.FEATURE_1);
        assertEquals("test string data", result);
    }
}
