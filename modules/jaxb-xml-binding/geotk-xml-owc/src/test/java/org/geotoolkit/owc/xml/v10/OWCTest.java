/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2014, Geomatys
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
package org.geotoolkit.owc.xml.v10;

import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.georss.xml.v100.WhereType;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.gml.xml.v311.EnvelopeType;
import org.junit.Test;
import static org.junit.Assert.*;
import org.w3._2005.atom.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;
import java.util.List;

public class OWCTest extends org.geotoolkit.test.TestBase {
    private static final org.w3._2005.atom.ObjectFactory OBJ_ATOM_FACT = new org.w3._2005.atom.ObjectFactory();
    private static final org.geotoolkit.owc.xml.v10.ObjectFactory OBJ_OWC_FACT = new org.geotoolkit.owc.xml.v10.ObjectFactory();
    private static final org.geotoolkit.georss.xml.v100.ObjectFactory OBJ_GEORSS_FACT = new org.geotoolkit.georss.xml.v100.ObjectFactory();

    private static final String EXP_RESULT =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"+
            "<ns2:feed xmlns:ns2=\"http://www.w3.org/2005/Atom\" xmlns:ns4=\"http://www.georss.org/georss\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:owc=\"http://www.opengis.net/owc/1.0\" xmlns:gml=\"http://www.opengis.net/gml\">\n"+
            "  <ns2:id>Test id</ns2:id>\n"+
            "  <ns2:title>Test</ns2:title>\n"+
            "  <ns4:where>\n"+
            "    <gml:Envelope srsName=\"CRS:84\">\n"+
            "      <gml:lowerCorner>-180.0 -90.0</gml:lowerCorner>\n"+
            "      <gml:upperCorner>180.0 90.0</gml:upperCorner>\n"+
            "    </gml:Envelope>\n"+
            "  </ns4:where>\n"+
            "  <ns2:entry>\n"+
            "    <ns2:id>Web Map Service Layer</ns2:id>\n"+
            "    <ns2:title>Test</ns2:title>\n"+
            "    <ns2:content type=\"html\"/>\n"+
            "    <ns2:category term=\"true\" scheme=\"http://www.opengis.net/spec/owc/active\"/>\n"+
            "    <owc:offering code=\"http://www.opengis.net/spec/owc-atom/1.0/req/wms\">\n"+
            "      <owc:operation method=\"GET\" code=\"GetCapabilities\" href=\"http://myhost.com/constellation/WS/wms/test?REQUEST=GetCapabilities&amp;SERVICE=WMS\"/>\n"+
            "      <owc:operation method=\"GET\" code=\"GetMap\" href=\"http://myhost.com/constellation/WS/wms/test?REQUEST=GetMap&amp;SERVICE=WMS&amp;FORMAT=image/png&amp;TRANSPARENT=true&amp;WIDTH=1024&amp;HEIGHT=768&amp;CRS=CRS:84&amp;BBOX=-5,40,15,60&amp;LAYERS=testlayer&amp;STYLES=default\"/>\n"+
            "    </owc:offering>\n"+
            "  </ns2:entry>\n"+
            "</ns2:feed>\n";

    @Test
    public void owcMarshallTest() throws JAXBException {

        final FeedType feed = new FeedType();
        final List<Object> entriesToSet = feed.getAuthorOrCategoryOrContributor();
        final IdType idFeed = new IdType();
        idFeed.setValue("Test id");
        entriesToSet.add(OBJ_ATOM_FACT.createEntryTypeId(idFeed));
        final TextType title = new TextType();
        title.getContent().add("Test");
        entriesToSet.add(OBJ_ATOM_FACT.createEntryTypeTitle(title));

        final String layerName = "testlayer";
        final String url = "http://myhost.com/constellation/WS/wms/test";

        final DirectPositionType lowerCorner = new DirectPositionType(-180.0, -90.0);
        final DirectPositionType upperCorner = new DirectPositionType(180.0, 90.0);
        final EnvelopeType envelope = new EnvelopeType(null, lowerCorner, upperCorner, "CRS:84");
        final WhereType where = new WhereType();
        where.setEnvelope(envelope);
        entriesToSet.add(OBJ_GEORSS_FACT.createWhere(where));

        final EntryType newEntry = new EntryType();
        final List<Object> entryThings = newEntry.getAuthorOrCategoryOrContent();
        final IdType idNewEntry = new IdType();
        idNewEntry.setValue("Web Map Service Layer");
        entryThings.add(OBJ_ATOM_FACT.createEntryTypeId(idNewEntry));
        final TextType titleNewEntry = new TextType();
        titleNewEntry.getContent().add(layerName);
        entryThings.add(OBJ_ATOM_FACT.createEntryTypeTitle(title));
        final org.w3._2005.atom.ContentType content = new org.w3._2005.atom.ContentType();
        content.setType("html");
        entryThings.add(OBJ_ATOM_FACT.createEntryTypeContent(content));
        final CategoryType category = new CategoryType();
        category.setScheme("http://www.opengis.net/spec/owc/active");
        category.setTerm("true");
        entryThings.add(OBJ_ATOM_FACT.createEntryTypeCategory(category));

        final OfferingType offering = new OfferingType();
        offering.setCode("http://www.opengis.net/spec/owc-atom/1.0/req/wms");

        final OperationType opCaps = new OperationType();
        opCaps.setCode("GetCapabilities");
        opCaps.setMethod(MethodCodeType.GET);
        final StringBuilder capsUrl = new StringBuilder();
        capsUrl.append(url).append("?REQUEST=GetCapabilities&SERVICE=WMS");
        opCaps.setHref(capsUrl.toString());
        offering.getOperationOrContentOrStyleSet().add(OBJ_OWC_FACT.createOfferingTypeOperation(opCaps));

        final OperationType opGetMap = new OperationType();
        opGetMap.setCode("GetMap");
        opGetMap.setMethod(MethodCodeType.GET);
        final String defStyle = "default";
        final StringBuilder getMapUrl = new StringBuilder();
        getMapUrl.append(url).append("?REQUEST=GetMap&SERVICE=WMS&FORMAT=image/png&TRANSPARENT=true&WIDTH=1024&HEIGHT=768&CRS=CRS:84&BBOX=")
                .append("-5,40,15,60").append("&LAYERS=").append(layerName)
                .append("&STYLES=").append(defStyle);
        opGetMap.setHref(getMapUrl.toString());
        offering.getOperationOrContentOrStyleSet().add(OBJ_OWC_FACT.createOfferingTypeOperation(opGetMap));

        entryThings.add(OBJ_OWC_FACT.createOffering(offering));

        entriesToSet.add(OBJ_ATOM_FACT.createEntry(newEntry));


        final JAXBContext jaxbCtxt = JAXBContext.newInstance("org.geotoolkit.owc.xml.v10:org.w3._2005.atom:org.geotoolkit.georss.xml.v100:org.geotoolkit.gml.xml.v311");
        final MarshallerPool pool = new MarshallerPool(jaxbCtxt, null);
        final Marshaller marsh = pool.acquireMarshaller();
        final StringWriter sw = new StringWriter();
        marsh.marshal(feed, sw);
        pool.recycle(marsh);
        assertEquals(EXP_RESULT, sw.toString());
    }
}


