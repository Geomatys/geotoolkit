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
package org.geotoolkit.citygml.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311modified.AbstractGMLEntry;
import org.geotoolkit.gml.xml.v311modified.GeometryPropertyType;
import org.geotoolkit.gml.xml.v311modified.PointPropertyType;


/**
 *  Type for the implicit representation of a geometry.
 * An implicit geometry is a geometric object, where the shape is stored only once as a prototypical geometry,
 * e.g. a tree or other vegetation object, a traffic light or a traffic sign.
 * This prototypic geometry object is re-used or referenced many times, wherever the corresponding feature occurs in the 3D city model.
 * Each occurrence is represented by a link to the prototypic shape geometry (in a local cartesian coordinate system),
 * by a transforma-tion matrix that is multiplied with each 3D coordinate tuple of the prototype,
 * and by an anchor point denoting the base point of the object in the world coordinate reference system.
 * In order to determine the absolute coordinates of an implicit geometry, the anchor point coordinates have to be added to the matrix multiplication results.
 * The transformation matrix accounts for the intended rotation, scaling, and local translation of the prototype.
 * It is a 4x4 matrix that is multiplied with the prototype coordinates using homogeneous coordinates, i.e. (x,y,z,1).
 * This way even a projection might be modelled by the transformation matrix.
 * The concept of implicit geometries is an enhancement of the geometry model of GML3.
 * 
 * <p>Java class for ImplicitGeometryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ImplicitGeometryType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGMLType">
 *       &lt;sequence>
 *         &lt;element name="mimeType" type="{http://www.opengis.net/citygml/1.0}MimeTypeType" minOccurs="0"/>
 *         &lt;element name="transformationMatrix" type="{http://www.opengis.net/citygml/1.0}TransformationMatrix4x4Type" minOccurs="0"/>
 *         &lt;element name="libraryObject" type="{http://www.w3.org/2001/XMLSchema}anyURI" minOccurs="0"/>
 *         &lt;element name="relativeGMLGeometry" type="{http://www.opengis.net/gml}GeometryPropertyType" minOccurs="0"/>
 *         &lt;element name="referencePoint" type="{http://www.opengis.net/gml}PointPropertyType"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImplicitGeometryType", propOrder = {
    "mimeType",
    "transformationMatrix",
    "libraryObject",
    "relativeGMLGeometry",
    "referencePoint"
})
public class ImplicitGeometryType extends AbstractGMLEntry {

    private String mimeType;
    @XmlList
    @XmlElement(type = Double.class)
    private List<Double> transformationMatrix;
    @XmlSchemaType(name = "anyURI")
    private String libraryObject;
    private GeometryPropertyType relativeGMLGeometry;
    @XmlElement(required = true)
    private PointPropertyType referencePoint;

    /**
     * Gets the value of the mimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }

    /**
     * Gets the value of the transformationMatrix property.
     */
    public List<Double> getTransformationMatrix() {
        if (transformationMatrix == null) {
            transformationMatrix = new ArrayList<Double>();
        }
        return this.transformationMatrix;
    }

    /**
     * Gets the value of the libraryObject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLibraryObject() {
        return libraryObject;
    }

    /**
     * Sets the value of the libraryObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLibraryObject(String value) {
        this.libraryObject = value;
    }

    /**
     * Gets the value of the relativeGMLGeometry property.
     * 
     * @return
     *     possible object is
     *     {@link GeometryPropertyType }
     *     
     */
    public GeometryPropertyType getRelativeGMLGeometry() {
        return relativeGMLGeometry;
    }

    /**
     * Sets the value of the relativeGMLGeometry property.
     * 
     * @param value
     *     allowed object is
     *     {@link GeometryPropertyType }
     *     
     */
    public void setRelativeGMLGeometry(GeometryPropertyType value) {
        this.relativeGMLGeometry = value;
    }

    /**
     * Gets the value of the referencePoint property.
     * 
     * @return
     *     possible object is
     *     {@link PointPropertyType }
     *     
     */
    public PointPropertyType getReferencePoint() {
        return referencePoint;
    }

    /**
     * Sets the value of the referencePoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link PointPropertyType }
     *     
     */
    public void setReferencePoint(PointPropertyType value) {
        this.referencePoint = value;
    }

}
