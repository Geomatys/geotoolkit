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
package org.geotoolkit.xal.model;

/**
 * <p>This interface maps CountryNameCode type.</p>
 *
 * <p>A country code according to the specified scheme.</p>
 *
 * <pre>
 * &lt;xs:complexType mixed="true">
 *  &lt;xs:attribute name="Scheme">...
 *  &lt;/xs:attribute>
 *  &lt;xs:attributeGroup ref="grPostal"/>
 *  &lt;xs:anyAttribute namespace="##other"/>
 * &lt;/xs:complexType>
 * </pre>
 *
 * @author Samuel Andrés
 * @module
 */
public interface CountryNameCode {

    /**
     *
     * @return
     */
    String getContent();

    /**
     * <p>Country code scheme possible values, but not limited to:
     * iso.3166-2, iso.3166-3 for two and three character country codes.</p>
     *
     * @return
     */
    String getScheme();

    /**
     *
     * @return
     */
    GrPostal getGrPostal();

    /**
     *
     * @param content
     */
    void setContent(String content);

    /**
     *
     * @param scheme
     */
    void setScheme(String scheme);

    /**
     *
     * @param grPostal
     */
    void setGrPostal(GrPostal grPostal);

}
