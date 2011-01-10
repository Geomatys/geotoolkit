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
package org.geotoolkit.wcs.xml.v100;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.EnvelopeEntry;
import org.geotoolkit.gml.xml.v311.DirectPositionType;
import org.geotoolkit.referencing.CRS;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.FactoryException;


/**
 * Envelope defines an extent using a pair of positions defining opposite corners in arbitrary dimensions. 
 * 
 * <p>Java class for LonLatEnvelopeBaseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LonLatEnvelopeBaseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.opengis.net/gml}EnvelopeType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/gml}pos" maxOccurs="2" minOccurs="2"/>
 *       &lt;/sequence>
 *       &lt;attribute name="srsName" type="{http://www.w3.org/2001/XMLSchema}anyURI" fixed="urn:ogc:def:crs:OGC:1.3:CRS84" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LonLatEnvelopeBaseType")
@XmlSeeAlso({
    LonLatEnvelopeType.class
})
public class LonLatEnvelopeBaseType extends EnvelopeEntry implements Envelope{
    
    LonLatEnvelopeBaseType(){
    }
    
    public LonLatEnvelopeBaseType(final List<DirectPositionType> pos, final String srsName) {
        super(pos, srsName);
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        try {
            return CRS.decode(getSrsName());
        } catch (NoSuchAuthorityCodeException ex) {
            Logger.getLogger(LonLatEnvelopeBaseType.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FactoryException ex) {
            Logger.getLogger(LonLatEnvelopeBaseType.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public int getDimension() {
        return getCoordinateReferenceSystem().getCoordinateSystem().getDimension();
    }

    @Override
    public double getMinimum(final int dimension) throws IndexOutOfBoundsException {
        return getPos().get(0).getOrdinate(dimension);
    }

    @Override
    public double getMaximum(final int dimension) throws IndexOutOfBoundsException {
        return getPos().get(1).getOrdinate(dimension);
    }

    @Override
    public double getMedian(final int dimension) throws IndexOutOfBoundsException {
        return (getMinimum(dimension) + getMaximum(dimension)) /2 ;
    }

    @Override
    public double getSpan(final int dimension) throws IndexOutOfBoundsException {
        return getMaximum(dimension) - getMinimum(dimension);
    }
}
