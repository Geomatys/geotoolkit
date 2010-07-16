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
import org.geotoolkit.data.kml.xsd.SimpleType;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultSchemaData extends DefaultAbstractObject implements SchemaData {

    public URI schemaURL;
    public List<SimpleData> simpleDatas;
    public List<Object> schemaDataExtensions;

    /**
     * 
     */
    public DefaultSchemaData(){
        this.simpleDatas = EMPTY_LIST;
        this.schemaDataExtensions = EMPTY_LIST;
    }

    /**
     * 
     * @param objectSimpleExtensions
     * @param idAttributes
     * @param schemaURL
     * @param simpleDatas
     * @param schemaDataExtensions
     */
    public DefaultSchemaData(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes, URI schemaURL,
            List<SimpleData> simpleDatas, List<Object> schemaDataExtensions) {
        super(objectSimpleExtensions, idAttributes);
        this.schemaURL = schemaURL;
        this.simpleDatas = (simpleDatas == null) ? EMPTY_LIST : simpleDatas;
        this.schemaDataExtensions = (schemaDataExtensions == null) ? EMPTY_LIST : schemaDataExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SimpleData> getSimpleDatas() {
        return this.simpleDatas;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getSchemaDataExtensions() {
        return this.schemaDataExtensions;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public URI getSchemaURL() {
        return this.schemaURL;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSchemaURL(URI schemaURL) {
        this.schemaURL = schemaURL;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSimpleDatas(List<SimpleData> simpleDatas) {
        this.simpleDatas = simpleDatas;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSchemaDataExtensions(List<Object> schemaDataExtensions) {
        this.schemaDataExtensions = schemaDataExtensions;
    }
}
