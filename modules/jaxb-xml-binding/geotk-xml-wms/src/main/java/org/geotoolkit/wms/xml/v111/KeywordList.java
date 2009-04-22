/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.wms.xml.v111;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.wms.xml.AbstractKeywordList;


/**
 * <p>Java class for anonymous complex type.
 * 
 * 
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "keyword"
})
@XmlRootElement(name = "KeywordList")
public class KeywordList implements AbstractKeywordList{

    @XmlElement(name = "Keyword")
    private List<Keyword> keyword = new ArrayList<Keyword>();

     /**
     * An empty constructor used by JAXB.
     */
     KeywordList() {
     }

    /**
     * Build a new Keyword list.
     */
    public KeywordList(final List<Keyword> keyword) {
        this.keyword = keyword;
        
    }
    
    /**
     * Build a new Contact person primary object.
     */
    public KeywordList(final Keyword... keywords) {
        for (final Keyword element : keywords) {
            this.keyword.add(element);
        }
    }
    
    /**
     * Gets the value of the keyword property.
     */
    @Override
    public List<Keyword> getKeyword() {
       return Collections.unmodifiableList(keyword);
    }

}
