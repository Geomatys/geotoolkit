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
package org.geotoolkit.gml.xml.v311;

import java.util.Set;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.complex.Composite;
import org.opengis.geometry.primitive.OrientablePrimitive;
import org.opengis.geometry.primitive.Primitive;


/**
 * This is the abstract root type of the geometric primitives. A geometric primitive is a geometric object that is not
 *          decomposed further into other primitives in the system. All primitives are oriented in the direction implied by the sequence of their
 *          coordinate tuples.
 *
 * <p>Java class for AbstractGeometricPrimitiveType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AbstractGeometricPrimitiveType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml}AbstractGeometryType">
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractGeometricPrimitiveType")
@XmlSeeAlso({
    PointType.class,
    AbstractSolidType.class,
    AbstractCurveType.class,
    AbstractSurfaceType.class
})
public abstract class AbstractGeometricPrimitiveType extends AbstractGeometryType implements Primitive {

    public AbstractGeometricPrimitiveType() {
    }

    public AbstractGeometricPrimitiveType(final String srsName) {
        super(srsName);
    }

    public AbstractGeometricPrimitiveType(final String id, final String srsName) {
        super(id, srsName);
    }

    @Override
    public Set<Primitive> getContainedPrimitives() {
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public Set<Primitive> getContainingPrimitives() {
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public Set<Complex> getComplexes() {
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public Composite getComposite() {
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public OrientablePrimitive[] getProxy() {
        // TODO calculate this for each subtype of geometry
        return null;
    }

    @Override
    public AbstractGeometricPrimitiveType clone() {
        // TODO calculate this for each subtype of geometry
        return null;
    }
}
