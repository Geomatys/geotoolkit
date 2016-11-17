/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2016, Geomatys
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
package org.geotoolkit.wps.xml;

import java.util.List;
import org.geotoolkit.ows.xml.AbstractCodeType;
import org.geotoolkit.ows.xml.RequestBase;

/**
 *
 * @author guilhem (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public interface Execute extends RequestBase {

    /**
     * Returns process identifier, never {@code null}.
     */
    AbstractCodeType getIdentifier();

    /**
     * Sets process identifiers to use.
     */
    void setIdentifier(String identifiers);
    
    List<? extends Input> getInput();
    
    List<? extends OutputDefinition> getOutput();
    
    String getLanguage();

    /**
     * Returns OutputLineage state.
     */
    boolean isLineage();

    /**
     * Sets OutputLineage state.
     */
    void setLineage(boolean outLineage);
    
    boolean isRawOutput();
    
    boolean isDocumentOutput();
    
    boolean isStatus();

    boolean isStoreExecuteResponse();
    
}
