/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2008-2010, Geomatys
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
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public interface AbstractSecurity {

    public List<String> getFGIsourceOpen() ;

    //public List<String> getFGIsourceprivate();

    public List<String> getSARIdentifier();

    public List<String> getSCIcontrols();

    public String getClassificationReason();

    public void setClassificationReason(String value);

    public String getClassifiedBy() ;

    public void setClassifiedBy(String value);

    public XMLGregorianCalendar getDateOfExemptedSource();

    public void setDateOfExemptedSource(XMLGregorianCalendar value);

    public XMLGregorianCalendar getDeclassDate();

    public void setDeclassDate(XMLGregorianCalendar value);

    public String getDeclassEvent();

    public void setDeclassEvent(String value);

    public List<String> getDeclassException();

    public Boolean isDeclassManualReview();

    public void setDeclassManualReview(Boolean value);

    public String getDerivedFrom();

    public void setDerivedFrom(String value);

    public List<String> getDisseminationControls();

    public List<String> getNonICmarkings();

    public List<String> getOwnerProducer();

    public List<String> getReleasableTo();

    public List<String> getTypeOfExemptedSource();
}
