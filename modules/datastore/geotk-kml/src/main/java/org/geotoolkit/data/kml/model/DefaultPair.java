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
package org.geotoolkit.data.kml.model;

import java.net.URI;
import java.util.List;
import org.geotoolkit.data.kml.xsd.SimpleTypeContainer;
import static org.geotoolkit.data.kml.xml.KmlConstants.*;

/**
 *
 * @author Samuel Andrés
 * @module pending
 */
public class DefaultPair extends DefaultAbstractObject implements Pair {

    private StyleState key;
    private URI styleUrl;
    private AbstractStyleSelector styleSelector;

    /**
     * 
     */
    public DefaultPair() {
        this.key = DEF_STYLE_STATE;
    }

    /**
     *
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param key
     * @param styleUrl
     * @param styleSelector
     * @param pairSimpleExtensions
     * @param pairObjectExtensions
     */
    public DefaultPair(List<SimpleTypeContainer> objectSimpleExtensions,
            IdAttributes idAttributes,
            StyleState key, URI styleUrl, AbstractStyleSelector styleSelector,
            List<SimpleTypeContainer> pairSimpleExtensions,
            List<Object> pairObjectExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.key = key;
        this.styleUrl = styleUrl;
        this.styleSelector = styleSelector;
        if (pairSimpleExtensions != null) {
            this.extensions().simples(Extensions.Names.PAIR).addAll(pairSimpleExtensions);
        }
        if (pairObjectExtensions != null) {
            this.extensions().complexes(Extensions.Names.PAIR).addAll(pairObjectExtensions);
        }
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public StyleState getKey() {
        return this.key;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public URI getStyleUrl() {
        return this.styleUrl;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public AbstractStyleSelector getAbstractStyleSelector() {
        return this.styleSelector;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setKey(StyleState key) {
        this.key = key;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setStyleUrl(URI styleUrl) {
        this.styleUrl = styleUrl;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAbstractStyleSelector(AbstractStyleSelector styleSelector) {
        this.styleSelector = styleSelector;
    }
}
