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

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultExtendedData implements ExtendedData {

    private List<Data> datas;
    private List<SchemaData> schemaDatas;
    private List<Object> anyOtherElements;

    /**
     *
     */
    public DefaultExtendedData() {
        this.datas = EMPTY_LIST;
        this.schemaDatas = EMPTY_LIST;
        this.anyOtherElements = EMPTY_LIST;
    }

    /**
     * 
     * @param datas
     * @param schemaDatas
     * @param anyOtherElements
     */
    public DefaultExtendedData(List<Data> datas,
            List<SchemaData> schemaDatas, List<Object> anyOtherElements) {
        this.datas = (datas == null) ? EMPTY_LIST : datas;
        this.schemaDatas = (schemaDatas == null) ? EMPTY_LIST : schemaDatas;
        this.anyOtherElements = (anyOtherElements == null) ? EMPTY_LIST : anyOtherElements;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Data> getDatas() {
        return this.datas;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SchemaData> getSchemaData() {
        return this.schemaDatas;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Object> getAnyOtherElements() {
        return this.anyOtherElements;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setDatas(List<Data> datas) {
        this.datas = datas;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setSchemaData(List<SchemaData> schemaDatas) {
        this.schemaDatas = schemaDatas;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public void setAnyOtherElements(List<Object> anyOtherElements) {
        this.anyOtherElements = anyOtherElements;
    }
}
