/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2020, Geomatys
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
package org.w3._2005.atom;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.geotoolkit.ops.xml.OpenSearchMarshallerPool;
import org.geotoolkit.ops.xml.OpenSearchXmlFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class XmlBindingTest {

    @Test
    public void ComponentMarshalingTest() throws Exception {

        FeedType feed = new FeedType();
        feed.addId(new IdType("test-001"));
        feed.addEntry(new EntryType());
        OpenSearchXmlFactory.completeFeed(feed, 100L, 1L, 10L);
        feed.addLink(new LinkType("http://hh.com", "collection", "test/xml"));

        Marshaller marshaller = OpenSearchMarshallerPool.getInstance().acquireMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        marshaller.marshal(feed, sw);

        String result = sw.toString();
        System.out.println("result:" + result);

        Unmarshaller um = OpenSearchMarshallerPool.getInstance().acquireUnmarshaller();

        Object umarshalled = um.unmarshal(new StringReader(result));
        if (umarshalled instanceof JAXBElement) {
            umarshalled = ((JAXBElement)umarshalled).getValue();
        }

        Assert.assertTrue(umarshalled instanceof FeedType);

        FeedType f = (FeedType) umarshalled;

        Assert.assertEquals(f.getTotalResults(), new Integer(100));
    }

}
