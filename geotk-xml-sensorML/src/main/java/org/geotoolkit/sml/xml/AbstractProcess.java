/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008 - 2009, Geomatys
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
package org.geotoolkit.sml.xml;

import java.util.List;

/**
 *
 * @author Guilhem Legal
 * @module
 */
public interface AbstractProcess extends AbstractSML {

    public List<? extends AbstractClassification> getClassification();

    public List<? extends AbstractIdentification> getIdentification();

    public AbstractValidTime getValidTime();

    public void setValidTime(AbstractValidTime validTime);

    public List<? extends AbstractKeywords> getKeywords();

    public List<? extends AbstractContact> getContact();

    public List<? extends AbstractDocumentation> getDocumentation();

    public List<? extends AbstractHistory> getHistory();

    public List<? extends AbstractLegalConstraint> getLegalConstraint();

    public List<? extends AbstractCapabilities> getCapabilities();

    public List<? extends AbstractCharacteristics> getCharacteristics();

    public AbstractSecurityConstraint getSecurityConstraint();
}
