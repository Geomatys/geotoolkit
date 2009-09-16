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
package org.geotoolkit.sampling.xml.v100;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 *
 * @version $Id:
 * @author Guilhem Legal
 */
@XmlRegistry
public class ObjectFactory {
    
    private static final QName _SamplingPoint_QNAME = new QName("http://www.opengis.net/sampling/1.0", "SamplingPoint");
    private static final QName _SamplingSurface_QNAME = new QName("http://www.opengis.net/sampling/1.0", "SamplingSurface");
    private static final QName _SamplingCurve_QNAME = new QName("http://www.opengis.net/sampling/1.0", "SamplingCurve");
    private static final QName _Specimen_QNAME = new QName("http://www.opengis.net/sampling/1.0", "Specimen");
    private static final QName _SpatiallyExtensiveSamplingFeature_QNAME = new QName("http://www.opengis.net/sampling/1.0", "SpatiallyExtensiveSamplingFeature");
    private static final QName _SamplingSolid_QNAME = new QName("http://www.opengis.net/sampling/1.0", "SamplingSolid");
    private static final QName _LocatedSpecimen_QNAME = new QName("http://www.opengis.net/sampling/1.0", "LocatedSpecimen");
    private static final QName _SurveyProcedure_QNAME = new QName("http://www.opengis.net/sampling/1.0", "SurveyProcedure");
    private static final QName _SamplingFeature_QNAME = new QName("http://www.opengis.net/sampling/1.0", "SamplingFeature");
    private static final QName _SamplingFeatureRelation_QNAME = new QName("http://www.opengis.net/sampling/1.0", "SamplingFeatureRelation");
    private static final QName _SamplingFeatureCollection_QNAME = new QName("http://www.opengis.net/sampling/1.0", "SamplingFeatureCollection");
    
    /**
     * Create an instance of {@link SamplingPointEntry }
     * 
     */
    public SamplingPointEntry createSamplingPointEntry() {
        return new SamplingPointEntry();
    }
    
    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ObservationEntry }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sampling/1.0", name = "SamplingPoint", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractFeature")
    public JAXBElement<SamplingPointEntry> createSamplingPoint(SamplingPointEntry value) {
        return new JAXBElement<SamplingPointEntry>(_SamplingPoint_QNAME, SamplingPointEntry.class, null, value);
    }



    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.opengis.sampling._1
     *
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SamplingFeatureCollectionPropertyType }
     *
     */
    public SamplingFeatureCollectionPropertyType createSamplingFeatureCollectionPropertyType() {
        return new SamplingFeatureCollectionPropertyType();
    }

    /**
     * Create an instance of {@link SpecimenType.Size }
     *
     */
    public SpecimenType.Size createSpecimenTypeSize() {
        return new SpecimenType.Size();
    }

    /**
     * Create an instance of {@link LocatedSpecimenType }
     *
     */
    public LocatedSpecimenType createLocatedSpecimenType() {
        return new LocatedSpecimenType();
    }

    /**
     * Create an instance of {@link SamplingFeatureRelationType }
     *
     */
    public SamplingFeatureRelationEntry createSamplingFeatureRelationType() {
        return new SamplingFeatureRelationEntry();
    }

    /**
     * Create an instance of {@link AnyOrReferenceType }
     *
     */
    public AnyOrReferenceType createAnyOrReferenceType() {
        return new AnyOrReferenceType();
    }

    /**
     * Create an instance of {@link LocatedSpecimenPropertyType }
     *
     */
    public LocatedSpecimenPropertyType createLocatedSpecimenPropertyType() {
        return new LocatedSpecimenPropertyType();
    }

    /**
     * Create an instance of {@link SamplingFeatureType }
     *
     */
    public SamplingFeatureEntry createSamplingFeatureType() {
        return new SamplingFeatureEntry();
    }

    /**
     * Create an instance of {@link SamplingCurveType }
     *
     */
    public SamplingCurveType createSamplingCurveType() {
        return new SamplingCurveType();
    }

    /**
     * Create an instance of {@link SamplingPointType }
     *
     */
    public SamplingPointEntry createSamplingPointType() {
        return new SamplingPointEntry();
    }

    /**
     * Create an instance of {@link SpecimenPropertyType }
     *
     */
    public SpecimenPropertyType createSpecimenPropertyType() {
        return new SpecimenPropertyType();
    }

    /**
     * Create an instance of {@link SamplingFeaturePropertyType }
     *
     */
    public SamplingFeaturePropertyType createSamplingFeaturePropertyType() {
        return new SamplingFeaturePropertyType();
    }

    /**
     * Create an instance of {@link SamplingSurfaceType }
     *
     */
    public SamplingSurfaceType createSamplingSurfaceType() {
        return new SamplingSurfaceType();
    }

    /**
     * Create an instance of {@link SamplingCurvePropertyType }
     *
     */
    public SamplingCurvePropertyType createSamplingCurvePropertyType() {
        return new SamplingCurvePropertyType();
    }

    /**
     * Create an instance of {@link SamplingFeatureRelationPropertyType }
     *
     */
    public SamplingFeatureRelationPropertyType createSamplingFeatureRelationPropertyType() {
        return new SamplingFeatureRelationPropertyType();
    }

    /**
     * Create an instance of {@link SpatiallyExtensiveSamplingFeatureType }
     *
     */
    public SpatiallyExtensiveSamplingFeatureType createSpatiallyExtensiveSamplingFeatureType() {
        return new SpatiallyExtensiveSamplingFeatureType();
    }

    /**
     * Create an instance of {@link SamplingSurfacePropertyType }
     *
     */
    public SamplingSurfacePropertyType createSamplingSurfacePropertyType() {
        return new SamplingSurfacePropertyType();
    }

    /**
     * Create an instance of {@link SpatiallyExtensiveSamplingFeaturePropertyType }
     *
     */
    public SpatiallyExtensiveSamplingFeaturePropertyType createSpatiallyExtensiveSamplingFeaturePropertyType() {
        return new SpatiallyExtensiveSamplingFeaturePropertyType();
    }

    /**
     * Create an instance of {@link SamplingSolidType }
     *
     */
    public SamplingSolidType createSamplingSolidType() {
        return new SamplingSolidType();
    }

    /**
     * Create an instance of {@link SamplingSolidPropertyType }
     *
     */
    public SamplingSolidPropertyType createSamplingSolidPropertyType() {
        return new SamplingSolidPropertyType();
    }

    /**
     * Create an instance of {@link SurveyProcedureType }
     *
     */
    public SurveyProcedureType createSurveyProcedureType() {
        return new SurveyProcedureType();
    }

    /**
     * Create an instance of {@link SamplingFeatureCollectionType }
     *
     */
    public SamplingFeatureCollectionType createSamplingFeatureCollectionType() {
        return new SamplingFeatureCollectionType();
    }

    /**
     * Create an instance of {@link SpecimenType }
     *
     */
    public SpecimenType createSpecimenType() {
        return new SpecimenType();
    }

    /**
     * Create an instance of {@link SamplingPointPropertyType }
     *
     */
    public SamplingPointPropertyType createSamplingPointPropertyType() {
        return new SamplingPointPropertyType();
    }

    /**
     * Create an instance of {@link SurveyProcedurePropertyType }
     *
     */
    public SurveyProcedurePropertyType createSurveyProcedurePropertyType() {
        return new SurveyProcedurePropertyType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SamplingSurfaceType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sampling/1.0", name = "SamplingSurface", substitutionHeadNamespace = "http://www.opengis.net/sampling/1.0", substitutionHeadName = "SpatiallyExtensiveSamplingFeature")
    public JAXBElement<SamplingSurfaceType> createSamplingSurface(SamplingSurfaceType value) {
        return new JAXBElement<SamplingSurfaceType>(_SamplingSurface_QNAME, SamplingSurfaceType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SamplingCurveType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sampling/1.0", name = "SamplingCurve", substitutionHeadNamespace = "http://www.opengis.net/sampling/1.0", substitutionHeadName = "SpatiallyExtensiveSamplingFeature")
    public JAXBElement<SamplingCurveType> createSamplingCurve(SamplingCurveType value) {
        return new JAXBElement<SamplingCurveType>(_SamplingCurve_QNAME, SamplingCurveType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SpecimenType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sampling/1.0", name = "Specimen", substitutionHeadNamespace = "http://www.opengis.net/sampling/1.0", substitutionHeadName = "SamplingFeature")
    public JAXBElement<SpecimenType> createSpecimen(SpecimenType value) {
        return new JAXBElement<SpecimenType>(_Specimen_QNAME, SpecimenType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SpatiallyExtensiveSamplingFeatureType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sampling/1.0", name = "SpatiallyExtensiveSamplingFeature", substitutionHeadNamespace = "http://www.opengis.net/sampling/1.0", substitutionHeadName = "SamplingFeature")
    public JAXBElement<SpatiallyExtensiveSamplingFeatureType> createSpatiallyExtensiveSamplingFeature(SpatiallyExtensiveSamplingFeatureType value) {
        return new JAXBElement<SpatiallyExtensiveSamplingFeatureType>(_SpatiallyExtensiveSamplingFeature_QNAME, SpatiallyExtensiveSamplingFeatureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SamplingSolidType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sampling/1.0", name = "SamplingSolid", substitutionHeadNamespace = "http://www.opengis.net/sampling/1.0", substitutionHeadName = "SpatiallyExtensiveSamplingFeature")
    public JAXBElement<SamplingSolidType> createSamplingSolid(SamplingSolidType value) {
        return new JAXBElement<SamplingSolidType>(_SamplingSolid_QNAME, SamplingSolidType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LocatedSpecimenType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sampling/1.0", name = "LocatedSpecimen", substitutionHeadNamespace = "http://www.opengis.net/sampling/1.0", substitutionHeadName = "Specimen")
    public JAXBElement<LocatedSpecimenType> createLocatedSpecimen(LocatedSpecimenType value) {
        return new JAXBElement<LocatedSpecimenType>(_LocatedSpecimen_QNAME, LocatedSpecimenType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SurveyProcedureType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sampling/1.0", name = "SurveyProcedure", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractFeature")
    public JAXBElement<SurveyProcedureType> createSurveyProcedure(SurveyProcedureType value) {
        return new JAXBElement<SurveyProcedureType>(_SurveyProcedure_QNAME, SurveyProcedureType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SamplingFeatureType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sampling/1.0", name = "SamplingFeature", substitutionHeadNamespace = "http://www.opengis.net/gml", substitutionHeadName = "AbstractFeature")
    public JAXBElement<SamplingFeatureEntry> createSamplingFeature(SamplingFeatureEntry value) {
        return new JAXBElement<SamplingFeatureEntry>(_SamplingFeature_QNAME, SamplingFeatureEntry.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SamplingFeatureRelationType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sampling/1.0", name = "SamplingFeatureRelation")
    public JAXBElement<SamplingFeatureRelationEntry> createSamplingFeatureRelation(SamplingFeatureRelationEntry value) {
        return new JAXBElement<SamplingFeatureRelationEntry>(_SamplingFeatureRelation_QNAME, SamplingFeatureRelationEntry.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SamplingFeatureCollectionType }{@code >}}
     *
     */
    @XmlElementDecl(namespace = "http://www.opengis.net/sampling/1.0", name = "SamplingFeatureCollection", substitutionHeadNamespace = "http://www.opengis.net/sampling/1.0", substitutionHeadName = "SamplingFeature")
    public JAXBElement<SamplingFeatureCollectionType> createSamplingFeatureCollection(SamplingFeatureCollectionType value) {
        return new JAXBElement<SamplingFeatureCollectionType>(_SamplingFeatureCollection_QNAME, SamplingFeatureCollectionType.class, null, value);
    }

}
