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

package org.geotoolkit.naturesdi;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.opengis.metadata.identification.Keywords;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class NATSDI_TaxonomicClassification implements org.opengis.metadata.naturesdi.NATSDI_TaxonomicClassification {

    private NATSDI_RankNameCode taxonRankName;

    private Keywords taxonRankValue;

    public NATSDI_TaxonomicClassification() {

    }

    public NATSDI_TaxonomicClassification(NATSDI_RankNameCode taxonRankName, Keywords taxonRankValue) {
        this.taxonRankName  = taxonRankName;
        this.taxonRankValue = taxonRankValue;
    }

    /**
     * @return the taxonRankName
     */
    public NATSDI_RankNameCode getTaxonRankName() {
        return taxonRankName;
    }

    /**
     * @param taxonRankName the taxonRankName to set
     */
    public void setTaxonRankName(NATSDI_RankNameCode taxonRankName) {
        this.taxonRankName = taxonRankName;
    }

    /**
     * @return the taxonRankValue
     */
    public Keywords getTaxonRankValue() {
        return taxonRankValue;
    }

    /**
     * @param taxonRankValue the taxonRankValue to set
     */
    public void setTaxonRankValue(Keywords taxonRankValue) {
        this.taxonRankValue = taxonRankValue;
    }
}
