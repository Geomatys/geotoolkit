/*
 *    Geotoolkit.org - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2004-2010, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2009-2010, Geomatys
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
 *
 *    This package contains documentation from OpenGIS specifications.
 *    OpenGIS consortium's work is fully acknowledged here.
 */

package org.geotoolkit.geotnetcab;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java element interface 
 * generated in the org.mdweb_project.files.xsd package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.mdweb_project.files.xsd
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GNCAccessType }
     * 
     */
    public GNC_Access createGNCAccessType() {
        return new GNC_Access();
    }


    /**
     * Create an instance of {@link GNCMaterialResourceType }
     * 
     */
    public GNC_MaterialResource createGNCMaterialResourceType() {
        return new GNC_MaterialResource();
    }

    /**
     * Create an instance of {@link GNCAccessConstraintsType }
     * 
     */
    public GNC_AccessConstraints createGNCAccessConstraintsType() {
        return new GNC_AccessConstraints();
    }

    
    /**
     * Create an instance of {@link GNCResourceType }
     * 
     */
    public GNC_Resource createGNCResourceType() {
        return new GNC_Resource();
    }

    /**
     * Create an instance of {@link GNCDocumentType }
     * 
     */
    public GNC_Document createGNCDocumentType() {
        return new GNC_Document();
    }

    /**
     * Create an instance of {@link GNCEOProductType }
     * 
     */
    public GNC_EOProduct createGNCEOProductType() {
        return new GNC_EOProduct();
    }

    /**
     * Create an instance of {@link GNCUserRestrictionType }
     * 
     */
    public GNC_UserRestriction createGNCUserRestrictionType() {
        return new GNC_UserRestriction();
    }

    /**
     * Create an instance of {@link GNCRelationTypeType }
     * 
     */
    public GNC_RelationType createGNCRelationTypeType() {
        return new GNC_RelationType();
    }

    /**
     * Create an instance of {@link GNCSoftwareType }
     * 
     */
    public GNC_Software createGNCSoftwareType() {
        return new GNC_Software();
    }

    /**
     * Create an instance of {@link GNCOrganisationEntitieType }
     * 
     */
    public GNC_Organisation createGNCOrganisationType() {
        return new GNC_Organisation();
    }

    /**
     * Create an instance of {@link GNCServiceType }
     * 
     */
    public GNC_Service createGNCServiceType() {
        return new GNC_Service();
    }

    /**
     * Create an instance of {@link GNCTrainingType }
     * 
     */
    public GNC_Training createGNCTrainingType() {
        return new GNC_Training();
    }

    /**
     * Create an instance of {@link GNCReferenceType }
     * 
     */
    public GNC_Reference createGNCReferenceType() {
        return new GNC_Reference();
    }

    /**
     * Create an instance of {@link GNCProductType }
     * 
     */
    public GNC_Product createGNCProductType() {
        return new GNC_Product();
    }

}
