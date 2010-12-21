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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotoolkit.ows.xml.v100.ExceptionReport;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.util.FileUtilities;
import org.geotoolkit.util.StringUtilities;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class CSWClassesContext {

    private static final Logger LOGGER = Logger.getLogger("org.geotoolkit.csw.xml");

    protected CSWClassesContext() {}

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

        //ISO 19115-2 class
        classeList.add(org.geotoolkit.metadata.imagery.DefaultMetadata.class);
        classeList.add(org.geotoolkit.metadata.imagery.DefaultCoverageDescription.class);
        classeList.add(org.geotoolkit.metadata.imagery.DefaultSource.class);
        classeList.add(org.geotoolkit.metadata.imagery.DefaultProcessStep.class);
        classeList.add(org.geotoolkit.metadata.imagery.DefaultGeorectified.class);
        classeList.add(org.geotoolkit.metadata.imagery.DefaultGeoreferenceable.class);
        classeList.add(org.geotoolkit.metadata.imagery.DefaultBand.class);
        classeList.add(org.geotoolkit.metadata.imagery.DefaultImageDescription.class);

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



        try {
            final InputStream stream        = CSWClassesContext.class.getResourceAsStream("extra-classes");
            final String s                  = FileUtilities.getStringFromStream(stream);
            final List<String> extraClasses = StringUtilities.toStringList(s, '\n');
            for (String extraClassName : extraClasses) {
                try {
                    Class extraClass = Class.forName(extraClassName);
                    classeList.add(extraClass);
                } catch (ClassNotFoundException ex) {
                    LOGGER.log(Level.INFO, "unable to find extra class:" + extraClassName, ex);
                }

            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "IO exception while getting extra-classes file", ex);
        }

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
