/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2012-2013, Geomatys
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
package org.geotoolkit.processing.chain.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
@XmlSeeAlso({ElementProcess.class,ElementCondition.class,ElementManual.class})
public class Element extends Positionable {
        
    /** Placeholder for process flow start node */
    public static final Element BEGIN = new Element(Integer.MIN_VALUE);
    /** Placeholder for process flow end node */
    public static final Element END = new Element(Integer.MAX_VALUE);
    
    @XmlAttribute(name="id")
    protected Integer id;

    public Element() {
    }

    public Element(Integer id) {
        this.id = id;
    }

    public Element(Integer id, int x, int y) {
        super(x, y);
        this.id = id;
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Element copy(){
        return new Element(id);
    }
    
}
