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
package org.geotoolkit.csw;

import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.csw.v202.GetRecords202;
import org.geotoolkit.csw.xml.ElementSetType;
import org.geotoolkit.csw.xml.ResultType;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testing class for GetRecords requests, in version 2.0.2.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class GetRecordsTest {
    public GetRecordsTest() {}

    /**
     * Ensures the {@link GetRecords202#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testGetRecords202() {
        final GetRecords202 getRecords202 = new GetRecords202("http://test.com",null);
        getRecords202.setElementSetName(ElementSetType.FULL);
        getRecords202.setConstraintLanguage("CQL_TEXT");
        getRecords202.setConstraintLanguageVersion("1.1.0");
        getRecords202.setTypeNames("csw:Record");
        getRecords202.setOutputFormat("xml");
        getRecords202.setResultType(ResultType.RESULTS);
        getRecords202.setConstraint("prop LIKE 'value'");
        final URL url;
        try {
            url = getRecords202.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("CONSTRAINTLANGUAGE=CQL_TEXT"));
        assertTrue(sUrl.contains("CONSTRAINT_LANGUAGE_VERSION=1.1.0"));
        assertTrue(sUrl.contains("OUTPUTFORMAT=xml"));
        assertTrue(sUrl.contains("ELEMENTSETNAME="+ ElementSetType.FULL.value()));
        assertTrue(sUrl.contains("RESULTTYPE="+ ResultType.RESULTS.value()));
        assertTrue(sUrl.contains("CONSTRAINT=prop%20LIKE%20'value'"));
    }
}
