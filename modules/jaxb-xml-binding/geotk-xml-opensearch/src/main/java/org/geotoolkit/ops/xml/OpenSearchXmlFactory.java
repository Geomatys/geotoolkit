/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.ops.xml;

import org.geotoolkit.ops.xml.v110.ObjectFactory;
import org.w3._2005.atom.FeedType;
import org.w3._2005.atom.PersonType;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class OpenSearchXmlFactory {

    public static FeedType buildFeed(String id, String title, PersonType author, String source, Long totalResults, Long startIndex, Long itemsPerPage) {
        FeedType feed = new FeedType(id, title, author, source);
        final ObjectFactory factory = new ObjectFactory();
        feed.getPagingAttributes().add(factory.createTotalResults(totalResults));
        feed.getPagingAttributes().add(factory.createStartIndex(startIndex));
        feed.getPagingAttributes().add(factory.createItemsPerPage(itemsPerPage));
        return feed;
    }

    public static FeedType completeFeed(FeedType feed, Long totalResults, Long startIndex, Long itemsPerPage) {
        final ObjectFactory factory = new ObjectFactory();
        feed.getPagingAttributes().add(factory.createTotalResults(totalResults));
        feed.getPagingAttributes().add(factory.createStartIndex(startIndex));
        feed.getPagingAttributes().add(factory.createItemsPerPage(itemsPerPage));
        return feed;
    }
}
