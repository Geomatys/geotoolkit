/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.observation.xml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311modified.AbstractTimeGeometricPrimitiveType;
import org.geotoolkit.sampling.xml.v100.SamplingFeatureEntry;
import org.geotoolkit.swe.xml.v101.PhenomenonEntry;
import org.geotoolkit.metadata.iso.DefaultMetaData;
import org.opengis.observation.Measurement;
import org.opengis.observation.Measure;

/**
 * Implémentation d'une entrée représentant une {@linkplain Measurement mesure}.
 *
 * @version $Id: MeasurementEntry.java 1559 2009-04-23 14:42:42Z glegal $
 * @author Antoine Hnawia
 * @author Martin Desruisseaux
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Measurement")
@XmlRootElement(name = "Measurement")
public class MeasurementEntry extends ObservationEntry implements Measurement {
    /**
     * Pour compatibilités entre les enregistrements binaires de différentes versions.
     */
    private static final long serialVersionUID = 6700527485309897974L;
    
    
    /**
     * constructeur vide utilisé par JAXB.
     */
    protected MeasurementEntry() {}
    
    /**
     * Crée une nouvelle mesure.
     *
     * @param station           La station d'observation (par exemple une position de pêche).
     * @param observedProperty  Ce que l'on observe (température, quantité pêchée, <cite>etc.</cite>).
     * @param process           La procedure effectuée sur cette operation.
     * @param quality
     * @param result            Le resultat de l'observation, ici une measure.
     * @param samplingTime
     * @param observationMetadata
     * @param resultDefinition
     * @param procedureTime
     * @param procedureParameter
     */
    public MeasurementEntry(final String name,
            final String                 definition,
            final SamplingFeatureEntry   station,
            final PhenomenonEntry        observedProperty,
            final ProcessEntry           procedure,
            final ElementEntry           quality,
            final MeasureEntry           result,
            final AbstractTimeGeometricPrimitiveType    samplingTime,
            final DefaultMetaData        observationMetadata,
            final AbstractTimeGeometricPrimitiveType    procedureTime,
            final Object                 procedureParameter) {
        super(name, definition, station, observedProperty, procedure, quality, result,
                samplingTime, observationMetadata, procedureTime, procedureParameter);
    }
    
    /**
     * Crée une nouvelle mesure  reduite adapté a BRGM.
     *
     * @param station     La station d'observation (par exemple une position de pêche).
     * @param observable  Ce que l'on observe (température, quantité pêchée, <cite>etc.</cite>).
     * @param value       La valeur mesurée.
     * @param error       Estimation de l'erreur sur la valeur mesurée, ou {@link Float#NaN NaN}
     *                    si l'erreur est inconnue ou ne s'applique pas.
     */
    public MeasurementEntry(final String name,
            final String definition,
            final SamplingFeatureEntry station,
            final PhenomenonEntry      observedProperty,
            final ProcessEntry         procedure,
            //final ElementEntry         quality,
            final MeasureEntry         result,
            final AbstractTimeGeometricPrimitiveType  samplingTime) {
        super(name, definition, station, observedProperty, procedure, result,
                samplingTime);
        
    }
    
    @Override
    public Measure getResult() {
       return (Measure)super.getResult();
    }
    
    
    
    /**
     * Vérifie si cette entré est identique à l'objet spécifié.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof MeasurementEntry && super.equals(object)) {
            final MeasurementEntry that = (MeasurementEntry) object;
            return this.getResult().equals(that.getResult());
        }
        return false;
    }

}
