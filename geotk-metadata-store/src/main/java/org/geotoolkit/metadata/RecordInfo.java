/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2019, Geomatys
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
package org.geotoolkit.metadata;

import org.w3c.dom.Node;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class RecordInfo {
    public String identifier;
    public Node node;
    public MetadataType originalFormat;
    public MetadataType actualFormat;

    public RecordInfo(String identifier, Node node, MetadataType originalFormat, MetadataType actualFormat) {
        this.identifier = identifier;
        this.node = node;
        this.originalFormat = originalFormat;
        this.actualFormat = actualFormat;
    }

}
