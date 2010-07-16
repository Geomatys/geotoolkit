/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.data.atom.model;

/**
 * <p>This interface maps atom link element.</p>
 *
 * <pre>
 * &lt;element name="link">
 *  &lt;complexType>
 *      &lt;attribute name="href" use="required"/>
 *      &lt;attribute name="rel"/>
 *      &lt;attribute name="type" type="atom:atomMediaType"/>
 *      &lt;attribute name="hreflang" type="atom:atomLanguageTag"/>
 *      &lt;attribute name="title"/>
 *      &lt;attribute name="length"/>
 *  &lt;/complexType>
 * &lt;/element>
 * </pre>
 *
 * @author Samuel Andrés
 */
public interface AtomLink {

    /**
     *
     * @return
     */
    public String getHref();

    /**
     *
     * @return
     */
    public String getRel();

    /**
     *
     * @return
     */
    public String getType();

    /**
     *
     * @return
     */
    public String getHreflang();

    /**
     *
     * @return
     */
    public String getTitle();

    /**
     * 
     * @return
     */
    public String getLength();

    /**
     *
     * @param href
     */
    public void setHref(final String href);

    /**
     *
     * @param rel
     */
    public void setRel(final String rel);

    /**
     *
     * @param type
     */
    public void setType(final String type);

    /**
     *
     * @param hreflang
     */
    public void setHreflang(final String hreflang);

    /**
     *
     * @param title
     */
    public void setTitle(final String title);

    /**
     *
     * @param length
     */
    public void setLength(final String length);
}
