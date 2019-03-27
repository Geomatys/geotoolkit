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
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.util.logging.Logging;
import org.geotoolkit.ows.xml.v100.ExceptionReport;


/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class CSWClassesContext {

    private static final Logger LOGGER = Logging.getLogger("org.geotoolkit.csw.xml");

    protected CSWClassesContext() {}

    /**
     * List of classes for the french profile of metadata.
     */
    public static final List<Class> FRA_CLASSES = new ArrayList<>();

    static {
        FRA_CLASSES.addAll(Arrays.asList(
                org.apache.sis.internal.profile.fra.Constraints.class,
                org.apache.sis.internal.profile.fra.DataIdentification.class,
                org.apache.sis.internal.profile.fra.DirectReferenceSystem.class,
                org.apache.sis.internal.profile.fra.IndirectReferenceSystem.class,
                org.apache.sis.internal.profile.fra.LegalConstraints.class,
                org.apache.sis.internal.profile.fra.SecurityConstraints.class));
    }

    /**
     * Return the list of all the marshallable classes.
     */
    public static List<Class> getAllClassesList() {
        final List<Class> classeList = new ArrayList<>();

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

        //CSW 3.0.0 classes
        classeList.addAll(Arrays.asList(org.geotoolkit.csw.xml.v300.ObjectFactory.class,
                                        org.geotoolkit.ows.xml.v200.ExceptionReport.class));

        //CSW 2.0.0 classes
        classeList.addAll(Arrays.asList(org.geotoolkit.csw.xml.v200.ObjectFactory.class,
                                        org.geotoolkit.dublincore.xml.v1.terms.ObjectFactory.class));

        // GML base factory
        classeList.add(org.apache.sis.internal.jaxb.geometry.ObjectFactory.class);
        classeList.add(org.geotoolkit.gml.xml.v311.ObjectFactory.class);
        classeList.add(org.geotoolkit.gml.xml.v321.ObjectFactory.class);

        // vertical CRS
        try {
            Class vcrsClass = Class.forName("org.apache.sis.referencing.crs.DefaultVerticalCRS");
            classeList.add(vcrsClass);
        } catch (ClassNotFoundException ex) {}

        // we add the extensions classes
        classeList.add(org.apache.sis.metadata.iso.identification.DefaultServiceIdentification.class);
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
                                        org.apache.sis.internal.jaxb.gco.Multiplicity.class));



        final List<String> extraClasses = new ArrayList<>();
        final Iterator<CSWClassesFactory> ite = ServiceLoader.load(CSWClassesFactory.class).iterator();
        while (ite.hasNext()) {
            final CSWClassesFactory currentFactory = ite.next();
            extraClasses.addAll(currentFactory.getExtraClasses());
        }
        for (String extraClassName : extraClasses) {
            try {
                Class extraClass = Class.forName(extraClassName);
                classeList.add(extraClass);
            } catch (ClassNotFoundException ex) {
                LOGGER.log(Level.FINER, "unable to find extra class:" + extraClassName, ex);
            }

        }
        return classeList;
    }

    public static List<Class> getCSWClassesList() {
        final List<Class> classeList = new ArrayList<>();

        //CSW 3.0.0 classes
        classeList.addAll(Arrays.asList(org.geotoolkit.csw.xml.v300.ObjectFactory.class,
                                        org.geotoolkit.ows.xml.v200.ExceptionReport.class));

        //CSW 2.0.2 classes
        classeList.addAll(Arrays.asList(org.geotoolkit.csw.xml.v202.LimitedObjectFactory.class,
                                        ExceptionReport.class,
                                        org.geotoolkit.ows.xml.v110.ExceptionReport.class, // TODO remove
                                        org.geotoolkit.dublincore.xml.v2.terms.ObjectFactory.class));

        //CSW 2.0.0 classes
        classeList.addAll(Arrays.asList(org.geotoolkit.csw.xml.v200.ObjectFactory.class,
                                        org.geotoolkit.dublincore.xml.v1.terms.ObjectFactory.class));

        // GML base factory
        classeList.add(org.apache.sis.internal.jaxb.geometry.ObjectFactory.class);
        return classeList;
    }

    /**
     * Returns the marshallable classes in an array.
     */
    public static Class[] getAllClasses() {
        final List<Class> classes = getAllClassesList();
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Returns the marshallable classes in an array.
     */
    public static Class[] getCSWClasses() {
        final List<Class> classes = getCSWClassesList();
        return classes.toArray(new Class[classes.size()]);
    }

}
