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

import java.util.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

// GeotoolKit dependencies
import org.geotoolkit.gml.xml.v311.AbstractFeatureType;
import org.geotoolkit.gml.xml.v311.FeaturePropertyType;
import org.geotoolkit.observation.xml.v100.ObservationType;
import org.geotoolkit.observation.xml.v100.SurveyProcedureType;
import org.geotoolkit.sampling.xml.SamplingFeature;
import org.apache.sis.util.ComparisonMode;
import org.opengis.geometry.Geometry;

// openGis dependencies
import org.opengis.observation.AnyFeature;
import org.opengis.observation.Observation;
import org.opengis.observation.sampling.SamplingFeatureRelation;

/**
 * Implémentation d'une entrée représentant une {@link SamplingFeature station}.
 *
 * @version $Id: SamplingFeatureType.java 1530 2009-04-17 09:10:58Z cedricbr $
 * @author Antoine Hnawia
 * @author Martin Desruisseaux
 *
 * @todo L'implémentation actuelle n'est pas <cite>serializable</cite> du fait qu'elle nécessite
 *       une connexion à la base de données. Une version future devrait rétablir la connexion au
 *       moment de la <cite>deserialization</cite>.
 * @module
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SamplingFeature", namespace="http://www.opengis.net/sampling/1.0")
@XmlRootElement(name = "SamplingFeature")
@XmlSeeAlso({ SamplingPointType.class})
public class SamplingFeatureType extends AbstractFeatureType implements SamplingFeature {
    /**
     * Pour compatibilités entre les enregistrements binaires de différentes versions.
     */
    private static final long serialVersionUID = 8822736167506306189L;

    /**
     *
     */
    private List<SamplingFeatureRelationType> relatedSamplingFeature;

    /**
     * Les Observations
     */
    private List<ObservationType> relatedObservation;

    /**
     * Les features designé
     */
    @XmlElement(required = true)
    private List<FeaturePropertyType> sampledFeature = new ArrayList<>();

    /**
     * Connexion vers la table des "survey details"
     * Optionnel peut etre {@code null}
     */
    private SurveyProcedureType surveyDetail;


    /**
     * Constructeur vide utilisé par JAXB.
     */
    protected SamplingFeatureType(){}

    /**
     *
     * Construit une entrée pour l'identifiant de station spécifié.
     * adapté au modele de BRGM.
     *
     *
     * @param id  L'identifiant numérique de la station.
     * @param name        Le nom de la station.
     * @param description Une description de la station.
     * @param sampledFeature
     */
    public SamplingFeatureType(   final String               id,
                                   final String               name,
                                   final String               description,
                                   final FeaturePropertyType sampledFeature)
    {
        super(id, name, description);
        this.sampledFeature         = new ArrayList<>();
        if (sampledFeature != null) {
            this.sampledFeature.add(sampledFeature);
        }

    }

    public SamplingFeatureType(   final String                 id,
                                   final String                 name,
                                   final String                 description,
                                   final List<SamplingFeatureRelationType> relatedSamplingFeature,
                                   final List<ObservationType> relatedObservation,
                                   final List<FeaturePropertyType> sampledFeature,
                                   final SurveyProcedureType   surveyDetail)
    {
        super(id, name, description);
        this.surveyDetail           = surveyDetail;
        this.relatedSamplingFeature = relatedSamplingFeature;
        this.sampledFeature         = sampledFeature;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<SamplingFeatureRelation> getRelatedSamplingFeature() {
        return new ArrayList<SamplingFeatureRelation>(relatedSamplingFeature);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<Observation> getRelatedObservation() {
        return new ArrayList<Observation>(relatedObservation);
    }

     /**
     * {@inheritDoc}
     */
    public synchronized List<FeaturePropertyType> getSampledFeatures() {
        return sampledFeature;
    }

    @Override
    public List<AnyFeature> getSampledFeature() {
        return new ArrayList<>();// TODO
    }

    @Override
    public SurveyProcedureType getSurveyDetail() {
        return this.surveyDetail;
    }

    /**
     * Vérifie que cette station est identique à l'objet spécifié
     */
    @Override
    public boolean equals(final Object object, final ComparisonMode mode) {
        if (object == this) {
            return true;
        }

        if (object instanceof SamplingFeatureType && super.equals(object, mode)) {
            final SamplingFeatureType that = (SamplingFeatureType) object;
            return Objects.equals(this.surveyDetail,           that.surveyDetail)   &&
                   Objects.equals(this.relatedObservation,     that.relatedObservation) &&
                   Objects.equals(this.relatedSamplingFeature, that.relatedSamplingFeature) &&
                   Objects.equals(this.sampledFeature,         that.sampledFeature);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.relatedSamplingFeature != null ? this.relatedSamplingFeature.hashCode() : 0);
        hash = 23 * hash + (this.relatedObservation != null ? this.relatedObservation.hashCode() : 0);
        hash = 23 * hash + (this.sampledFeature != null ? this.sampledFeature.hashCode() : 0);
        hash = 23 * hash + (this.surveyDetail != null ? this.surveyDetail.hashCode() : 0);
        return hash;
    }

   /**
     * Retourne une chaine de charactere representant la station.
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        if (sampledFeature != null) {
            Iterator i =  sampledFeature.iterator();
            String sampledFeatures = "";
            while (i.hasNext()) {
                sampledFeatures += i.next() + " ";
            }
            s.append("sampledFeature = ").append(sampledFeatures);
        }
        return s.toString();
    }

    @Override
    public Geometry getGeometry() {
       return null; // no geometry in that class
    }
}
