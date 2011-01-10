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
package org.geotoolkit.wmts.xml.v100;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.ows.xml.v110.ContentsBaseType;
import org.geotoolkit.ows.xml.v110.DatasetDescriptionSummaryBaseType;


/**
 * <p>Java class for ContentsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ContentsType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ows/1.1}ContentsBaseType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/wmts/1.0}TileMatrixSet" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ContentsType", propOrder = {
    "tileMatrixSet"
})
public class ContentsType extends ContentsBaseType {

    @XmlElement(name = "TileMatrixSet")
    private List<TileMatrixSet> tileMatrixSet;

    /**
     * A description of the geometry of a tile fragmentation Gets the value of the tileMatrixSet property.
     * 
     * Objects of the following type(s) are allowed in the list
     * {@link TileMatrixSet }
     * 
     * 
     */
    public List<TileMatrixSet> getTileMatrixSet() {
        if (tileMatrixSet == null) {
            tileMatrixSet = new ArrayList<TileMatrixSet>();
        }
        return this.tileMatrixSet;
    }

    /**
     * Returns the {@link TileMatrixSet} with the matching identifier, or {@code null}
     * if the given argument is null or if no one matches.
     *
     * @param indentifier The identifier of a matrix set.
     * @return A {@link TileMatrixSet} with the matching identifier, or {@code null}
     *         if none.
     */
    public TileMatrixSet getTileMatrixSetByIdentifier(final String indentifier) {
        if (tileMatrixSet == null || indentifier == null) {
            return null;
        }
        for (TileMatrixSet matrix : tileMatrixSet) {
            if (indentifier.equalsIgnoreCase(matrix.getIdentifier().getValue())) {
                return matrix;
            }
        }
        return null;
    }

    public void setTileMatrixSet(final List<TileMatrixSet> tms) {
        this.tileMatrixSet = tms;
    }

    public List<LayerType> getLayers() {
        final List<LayerType> layers = new ArrayList<LayerType>();
        for (JAXBElement<? extends DatasetDescriptionSummaryBaseType> elem : getDatasetDescriptionSummary()) {
            final Object candidate = elem.getValue();
            if (candidate instanceof LayerType) {
                layers.add((LayerType)candidate);
            }
        }
        return layers;
    }

    public void setLayers(final List<LayerType> layers) {
        final ObjectFactory factory = new ObjectFactory();
        this.datasetDescriptionSummary = new ArrayList<JAXBElement<? extends DatasetDescriptionSummaryBaseType>>(layers.size());
        for (LayerType elem : layers) {
            this.datasetDescriptionSummary.add(factory.createLayer(elem));
        }
    }

}
