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
package org.geotoolkit.csw.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBException;

import org.geotoolkit.ows.xml.v100.ExceptionReport;

import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.xml.MarshallerPool;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class CSWClassesContext {

    private static MarshallerPool instance = null;

    protected CSWClassesContext() {}

    public static MarshallerPool getMarshallerPool() throws JAXBException {
        if (instance == null) {
            instance = new MarshallerPool(getAllClasses());
        }
        return instance;
    }
    
    /**
     * List of classes for the french profile of metadata.
     */
    public static final List<Class> FRA_CLASSES = new ArrayList<Class>();

    static {
        FRA_CLASSES.addAll(Arrays.asList(
                org.geotoolkit.metadata.fra.FRA_Constraints.class,
                org.geotoolkit.metadata.fra.FRA_DataIdentification.class,
                org.geotoolkit.metadata.fra.FRA_DirectReferenceSystem.class,
                org.geotoolkit.metadata.fra.FRA_IndirectReferenceSystem.class,
                org.geotoolkit.metadata.fra.FRA_LegalConstraints.class,
                org.geotoolkit.metadata.fra.FRA_SecurityConstraints.class));
    }

    /**
     * Return the list of all the marshallable classes.
     */
    public static List<Class> getAllClassesList() {
        final List<Class> classeList = new ArrayList<Class>();

        //ISO 19115 class
        classeList.add(DefaultMetadata.class);

        //ISO 19115 French profile class
        classeList.addAll(FRA_CLASSES);

        // Inspire classes
        classeList.add(org.geotoolkit.inspire.xml.ObjectFactory.class);

        // xsd classes classes
        classeList.add(org.geotoolkit.xsd.xml.v2001.ObjectFactory.class);

        //CSW 2.0.2 classes
        classeList.addAll(Arrays.asList(org.geotoolkit.csw.xml.v202.ObjectFactory.class,
                                        ExceptionReport.class,
                                        org.geotoolkit.ows.xml.v110.ExceptionReport.class, // TODO remove
                                        org.geotoolkit.dublincore.xml.v2.terms.ObjectFactory.class));

        //CSW 2.0.0 classes
        classeList.addAll(Arrays.asList(org.geotoolkit.csw.xml.v200.ObjectFactory.class,
                                        org.geotoolkit.dublincore.xml.v1.terms.ObjectFactory.class));

        // GML base factory
        classeList.add(org.geotoolkit.internal.jaxb.geometry.ObjectFactory.class);

        // we add the extensions classes
        classeList.add(org.geotoolkit.service.ServiceIdentificationImpl.class);
        classeList.addAll(Arrays.asList(org.geotoolkit.feature.catalog.AssociationRoleImpl.class,
                                        org.geotoolkit.feature.catalog.BindingImpl.class,
                                        org.geotoolkit.feature.catalog.BoundFeatureAttributeImpl.class,
                                        org.geotoolkit.feature.catalog.ConstraintImpl.class,
                                        org.geotoolkit.feature.catalog.DefinitionReferenceImpl.class,
                                        org.geotoolkit.feature.catalog.DefinitionSourceImpl.class,
                                        org.geotoolkit.feature.catalog.FeatureAssociationImpl.class,
                                        org.geotoolkit.feature.catalog.FeatureAttributeImpl.class,
                                        org.geotoolkit.feature.catalog.FeatureCatalogueImpl.class,
                                        org.geotoolkit.feature.catalog.FeatureOperationImpl.class,
                                        org.geotoolkit.feature.catalog.FeatureTypeImpl.class,
                                        org.geotoolkit.feature.catalog.InheritanceRelationImpl.class,
                                        org.geotoolkit.feature.catalog.ListedValueImpl.class,
                                        org.geotoolkit.feature.catalog.PropertyTypeImpl.class,
                                        org.geotoolkit.util.Multiplicity.class));

        return classeList;
    }

    /**
     * Returns the marshallable classes in an array.
     */
    public static Class[] getAllClasses() {
        List<Class> classes =getAllClassesList();
        return classes.toArray(new Class[classes.size()]);
    }

}
