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
package org.geotoolkit.observation.xml.v100;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.sis.xml.bind.metadata.DQ_Element;
import org.apache.sis.metadata.iso.DefaultIdentifier;

import org.geotoolkit.gml.xml.v311.*;
import org.opengis.metadata.quality.Element;
import org.opengis.metadata.Metadata;
import org.opengis.observation.Observation;
import org.opengis.observation.Phenomenon;
import org.opengis.observation.sampling.SamplingFeature;

import org.geotoolkit.internal.sql.Entry;
import org.geotoolkit.sampling.xml.v100.SamplingFeatureType;
import org.geotoolkit.sampling.xml.v100.SamplingPointType;
import org.geotoolkit.swe.xml.v101.AnyResultType;
import org.geotoolkit.swe.xml.v101.DataArrayType;
import org.geotoolkit.swe.xml.v101.DataArrayPropertyType;
import org.geotoolkit.swe.xml.v101.PhenomenonType;
import org.geotoolkit.swe.xml.v101.PhenomenonPropertyType;
import org.geotoolkit.swe.xml.v101.TimeGeometricPrimitivePropertyType;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.geotoolkit.observation.xml.AbstractObservation;
import org.geotoolkit.sampling.xml.v100.SamplingCurveType;
import org.geotoolkit.sampling.xml.v100.SamplingSolidType;
import org.geotoolkit.sampling.xml.v100.SamplingSurfaceType;
import org.geotoolkit.gml.xml.AbstractGeometry;
import org.geotoolkit.gml.xml.BoundingShape;
import org.opengis.metadata.Identifier;
import org.opengis.temporal.Period;
import org.opengis.temporal.TemporalPrimitive;


/**
 * Implémentation d'une entrée représentant une {@linkplain Observation observation}.
 *
 * @author Martin Desruisseaux
 * @author Antoine Hnawia
 * @author Guilhem Legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Observation", propOrder = {
    "name",
    "samplingTime",
    "procedure",
    "procedureParameter",
    "procedureTime",
    "observedProperty",
    "featureOfInterest",
    "resultQuality",
    "result"
})
@XmlRootElement(name = "Observation")
@XmlSeeAlso({ MeasurementType.class})
public class ObservationType implements Entry, AbstractObservation {
    /**
     * Pour compatibilités entre les enregistrements binaires de différentes versions.
     */
    private static final long serialVersionUID = 3269639171560208276L;

    protected static final org.geotoolkit.sampling.xml.v100.ObjectFactory SAMPLING_FACTORY = new org.geotoolkit.sampling.xml.v100.ObjectFactory();

    protected static final org.geotoolkit.gml.xml.v311.ObjectFactory GML_FACTORY = new org.geotoolkit.gml.xml.v311.ObjectFactory();

    protected static final ObjectFactory OM_FACTORY = new ObjectFactory();

    /**
     * A logger (debugging purpose)
     */
    protected static final Logger LOGGER = Logger.getLogger("org.geotoolkit.observation.xml.v100");

    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    private String id;

    /**
     *The observation name
     */
    @XmlElement(namespace = "http://www.opengis.net/gml")
    private String name;

    /**
     * La description de l'observation
     */
    @XmlTransient
    private String definition;

    /**
     * La station à laquelle a été pris cet échantillon.
     */
    @XmlElement(required = true)
    private FeaturePropertyType featureOfInterest;

    /**
     * Référence vers le {@linkplain Phenomenon phénomène} observé.
     */
    @XmlElement(required = true)
    private PhenomenonPropertyType observedProperty;

    /**
     * Référence vers la {@linkplain Procedure procédure} associée à cet observable.
     */
    @XmlElement(required = true)
    private ProcessType procedure;

     /**
     * Référence vers la {@linkplain Distribution distribution} associée à cet observable.

    @XmlTransient
    private Distribution distribution;*/

    @XmlJavaTypeAdapter(DQ_Element.class)
    private Element resultQuality;

    /**
     * le resultat de l'observation de n'importe quel type
     */
    @XmlElementRef(name= "result", namespace="http://www.opengis.net/om/1.0")
    private JAXBElement<Object> result;

    /**
     *
     */
     @XmlElement
     private TimeGeometricPrimitivePropertyType samplingTime;


     /**
      *
      */
     @XmlTransient
     private DefaultMetadata observationMetadata;

    /**
     *
     */
     @XmlElement
    private TimeGeometricPrimitivePropertyType procedureTime;


    /**
     *
     */
    @XmlElement
    private Object procedureParameter;

    /**
     * Build An empty observation (used for JAXB serialization)
     */
    public ObservationType() {}

     /**
     * Build a clone of an observation
     */
    public ObservationType(final ObservationType observation) {
        this.definition          = observation.definition;
        this.featureOfInterest   = observation.featureOfInterest;
        this.name                = observation.name;
        this.observationMetadata = observation.observationMetadata;
        this.observedProperty    = observation.observedProperty;
        this.procedure           = observation.procedure;
        this.procedureParameter  = observation.procedureParameter;
        this.procedureTime       = observation.procedureTime;
        if (observation.result != null && observation.result.getValue() instanceof DataArrayPropertyType) {
            this.result = OM_FACTORY.createResult(new DataArrayPropertyType((DataArrayPropertyType)observation.result.getValue()));
        } else {
            this.result          = observation.result;
        }
        this.resultQuality       = observation.resultQuality;
        this.samplingTime        = observation.samplingTime;
    }

    public ObservationType(final String                name,
                            final String               definition,
                            final SamplingFeatureType  featureOfInterest,
                            final PhenomenonType       observedProperty,
                            final String               procedure,
                            final Object               result,
                            final AbstractTimeGeometricPrimitiveType   samplingTime)
    {
        this(name, definition, buildFeatureProperty(featureOfInterest), new PhenomenonPropertyType(observedProperty), new ProcessType(procedure), null, result, samplingTime, null, null, null);
    }

    public ObservationType(final String                name,
                           final String                definition,
                           final FeaturePropertyType   featureOfInterest,
                           final PhenomenonType        observedProperty,
                           final String                procedure,
                           final Object                result,
                           final AbstractTimeGeometricPrimitiveType   samplingTime)
    {
        this(name, definition, featureOfInterest, new PhenomenonPropertyType(observedProperty), new ProcessType(procedure),null,  result, samplingTime, null, null, null);
    }

    public ObservationType(final String                  name,
                            final String                 definition,
                            final FeaturePropertyType    featureOfInterest,
                            final PhenomenonPropertyType observedProperty,
                            final String                 procedure,
                            final Object                 result,
                            final AbstractTimeGeometricPrimitiveType   samplingTime)
    {
        this(name, definition, featureOfInterest, observedProperty, new ProcessType(procedure), null, result, samplingTime, null, null, null);
    }

    /**
     * Construit une observation.
     *
     *
     * @param featureOfInterest La station d'observation (par exemple une position de pêche).
     * @param observedProperty  Le phénomène observé.
     * @param procedure         La procédure associée.
     * @param resultQuality    La qualité de la donnée, ou {@code null} si inconnue.
     */
    public ObservationType(final String                name,
                            final String               definition,
                            final FeaturePropertyType  featureOfInterest,
                            final PhenomenonPropertyType observedProperty,
                            final ProcessType          procedure,
                            final Element              quality,
                            final Object               result,
                            final AbstractTimeGeometricPrimitiveType  samplingTime,
                            final DefaultMetadata      observationMetadata,
                            final AbstractTimeGeometricPrimitiveType  procedureTime,
                            final Object               procedureParameter)
    {
        this.name                = name;
        this.definition          = definition;
        this.featureOfInterest   = featureOfInterest;
        this.observedProperty    = observedProperty;
        this.procedure           = procedure;
        this.resultQuality       = quality;
        this.result              = OM_FACTORY.createResult(result);
        this.observationMetadata = observationMetadata;
        this.procedureParameter  = procedureParameter;
        if (samplingTime != null) {
            this.samplingTime        = new TimeGeometricPrimitivePropertyType(samplingTime);
        }
        if (procedureTime != null) {
            this.procedureTime    = new TimeGeometricPrimitivePropertyType(procedureTime);
        }
    }

    private static FeaturePropertyType buildFeatureProperty(final SamplingFeatureType featureOfInterest) {
        if (featureOfInterest instanceof SamplingPointType) {
            return new FeaturePropertyType(SAMPLING_FACTORY.createSamplingPoint((SamplingPointType)featureOfInterest));
        } else if (featureOfInterest instanceof SamplingCurveType) {
            return new FeaturePropertyType(SAMPLING_FACTORY.createSamplingCurve((SamplingCurveType)featureOfInterest));
        } else if (featureOfInterest instanceof SamplingSolidType) {
            return new FeaturePropertyType(SAMPLING_FACTORY.createSamplingSolid((SamplingSolidType)featureOfInterest));
        } else if (featureOfInterest instanceof SamplingSurfaceType) {
            return new FeaturePropertyType(SAMPLING_FACTORY.createSamplingSurface((SamplingSurfaceType)featureOfInterest));
        }
        return null;
    }

    /**
     * Construit un nouveau template temporaire d'observation a partir d'un template fournit en argument.
     * On y rajoute un samplingTime et un id temporaire.
     */
    @Override
    public ObservationType getTemporaryTemplate(final String temporaryName, TemporalPrimitive time) {
        if (time == null) {
            TimePositionType begin = new  TimePositionType("1900-01-01T00:00:00");
            time = new TimePeriodType(begin);
        }
        //debugging purpose
        Object res = null;

        if (this.result != null) {
            res = this.result.getValue();
            if (this.result.getValue() instanceof DataArrayPropertyType dap) {
                DataArrayType d = dap.getDataArray();
                d.setElementCount(0);
                d.setValues("");
            }
        }
        return new ObservationType(temporaryName,
                                    this.definition,
                                    this.featureOfInterest,
                                    this.observedProperty,
                                    this.procedure.getHref(),
                                    res,
                                    (AbstractTimeGeometricPrimitiveType)time);

    }

    @Override
    public String getObservationType() {
        return "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Observation";
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
       this.id = id;
    }

    @Override
    public void setName(final Identifier name) {
        if (name != null) {
            this.name  = name.getCode();
        } else {
            this.name  = null;
        }
    }

    @Override
    public Identifier getName() {
        if (name != null) {
            return new DefaultIdentifier(this.name);
        }
        return null;
    }

    @Override
    public String getIdentifier() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public SamplingFeatureType getFeatureOfInterest() {
        if (featureOfInterest != null) {
            if (featureOfInterest.getAbstractFeature() instanceof SamplingFeature) {
                return (SamplingFeatureType)featureOfInterest.getAbstractFeature();
            } else {
                LOGGER.warning("information lost getFeatureOfInterest() is deprecated use getPropertyFeatureOfInterest() instead");
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFullFeatureOfInterest(final SamplingFeature foi) {
        if (foi instanceof AbstractFeatureType aft) {
            this.setFeatureOfInterest(aft);
        } else if (observedProperty != null) {
            throw new IllegalArgumentException("Unexpected phenomenon implementation. expecting Sampling 1.0.0");
        }
    }

    public void setFeatureOfInterest(final AbstractFeatureType featureOfInterest) {
        if (featureOfInterest != null) {
            if (featureOfInterest instanceof SamplingPointType) {
                this.featureOfInterest = new FeaturePropertyType(SAMPLING_FACTORY.createSamplingPoint((SamplingPointType) featureOfInterest));
            } else if (featureOfInterest instanceof SamplingCurveType) {
                this.featureOfInterest = new FeaturePropertyType(SAMPLING_FACTORY.createSamplingCurve((SamplingCurveType) featureOfInterest));
            } else if (featureOfInterest instanceof SamplingSolidType) {
                this.featureOfInterest = new FeaturePropertyType(SAMPLING_FACTORY.createSamplingSolid((SamplingSolidType) featureOfInterest));
            } else if (featureOfInterest instanceof SamplingSurfaceType) {
                this.featureOfInterest = new FeaturePropertyType(SAMPLING_FACTORY.createSamplingSurface((SamplingSurfaceType) featureOfInterest));
            } else if (featureOfInterest instanceof FeatureCollectionType) {
                this.featureOfInterest = new FeaturePropertyType(GML_FACTORY.createFeatureCollection((FeatureCollectionType) featureOfInterest));
            }
        }
    }

    @Override
    public FeaturePropertyType getPropertyFeatureOfInterest(){
        return featureOfInterest;
    }

    public void setPropertyFeatureOfInterest(final FeaturePropertyType featureOfInterest){
        this.featureOfInterest = featureOfInterest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhenomenonType getObservedProperty() {
        if (observedProperty != null) {
            return observedProperty.getPhenomenon();
        } else {
            return null;
        }
    }

    public void setObservedProperty(final PhenomenonType observedProperty) {
        if (observedProperty != null) {
            this.observedProperty = new PhenomenonPropertyType(observedProperty);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFullObservedProperty(final Phenomenon observedProperty) {
        if (observedProperty instanceof PhenomenonType phen) {
            this.observedProperty = new PhenomenonPropertyType(phen);
        } else if (observedProperty != null) {
            throw new IllegalArgumentException("Unexpected phenomenon implementation. expecting SWE 1.0.0");
        }
    }

    public void setPropertyObservedProperty(final PhenomenonPropertyType observedProperty) {
        this.observedProperty = observedProperty;
    }

    @Override
    public PhenomenonPropertyType getPropertyObservedProperty() {
       return observedProperty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProcessType getProcedure() {
        return procedure;
    }

    @Override
    public void setProcedure(final String procedureID) {
        if (procedureID != null) {
            this.procedure = new ProcessType(procedureID);
        }
    }

    /**
     * fixe le capteur qui a effectué cette observation.
     */
    public void setProcedure(final ProcessType process) {
        this.procedure = process;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element getQuality() {
        return resultQuality;
    }

    public void setQuality(final Element resultQuality) {
        this.resultQuality = resultQuality;
    }

    @Override
    public List<Element> getResultQuality() {
        if (resultQuality != null) {
            return Arrays.asList(resultQuality);
        }
        return new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getResult() {
        if (result != null) {
            return result.getValue();
        }
        return null;
    }

    /**
     * Set the result of the observation.
     */
    @Override
    public void setResult(final Object result) {
        if (!(result instanceof ReferenceType) && !(result instanceof AnyResultType) &&
            !(result instanceof DataArrayPropertyType) && !(result instanceof MeasureType)) {
            throw new IllegalArgumentException("this type " + result.getClass().getSimpleName() +
                                           " is not allowed in result");
        }
        this.result = OM_FACTORY.createResult(result);
    }

    /**
     * Update the result of the obervation by setting the specified String value and numbers of result.
     * and the last date of measure.
     *
     * @param values A datablock of values.
     * @param nbResult the number of result in the datablock.
      * @param lastDate The last date of measure. If this parameter is null, the last date will be kept.
     * @throws IllegalArgumentException if the resulat of the observation is not a DataArray.
     */
    public void updateDataArrayResult(final String values, final int nbResult, final Date lastDate) {
        if (lastDate != null) {
            extendSamplingTime(lastDate);
        }
        if (getResult() instanceof DataArrayPropertyType) {
            DataArrayType array = ((DataArrayPropertyType)getResult()).getDataArray();
            array.updateArray(values, nbResult);
        } else {
            throw new IllegalArgumentException("The result is not a data array.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractTimeGeometricPrimitiveType getSamplingTime() {
        if (samplingTime != null) {
            return samplingTime.getTimeGeometricPrimitive();
        }
        return null;

    }

    @Override
    public void emptySamplingTime() {
        this.samplingTime = null;
    }

    /**
     * {@inheritDoc}
     */
    public void setSamplingTime(final AbstractTimeGeometricPrimitiveType value) {
        this.samplingTime = new TimeGeometricPrimitivePropertyType(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void extendSamplingTime(final Date newEndBound) {
        if (newEndBound != null) {
            if (samplingTime != null && samplingTime.getTimeGeometricPrimitive() instanceof TimePeriodType) {
                ((TimePeriodType)samplingTime.getTimeGeometricPrimitive()).setEndPosition(new TimePositionType(newEndBound.toInstant()));
            } else if (samplingTime != null && samplingTime.getTimeGeometricPrimitive() instanceof TimeInstantType) {
                final TimeInstantType instant = (TimeInstantType) samplingTime.getTimeGeometricPrimitive();
                if (!newEndBound.equals(instant.getTimePosition().getValue())) {
                    final TimePeriodType period = new TimePeriodType(instant.getId(), instant.getTimePosition().getPosition(), newEndBound.toInstant());
                    samplingTime.setTimeGeometricPrimitive(period);
                }
            } else if (samplingTime == null) {
                samplingTime = new TimeGeometricPrimitivePropertyType(new TimeInstantType(new TimePositionType(newEndBound.toInstant())));
            }
        }
    }

    @Override
    public void setSamplingTimePeriod(final Period period) {
        if (period instanceof TimePeriodType p) {
            this.samplingTime = new TimeGeometricPrimitivePropertyType(p);
        } else if (period != null) {
            final TimePeriodType pt = new TimePeriodType(null, period.getBeginning().getPosition(), period.getEnding().getPosition());
            this.samplingTime = new TimeGeometricPrimitivePropertyType(pt);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Metadata getObservationMetadata() {
        return observationMetadata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractTimeGeometricPrimitiveType getProcedureTime() {
        if (procedureTime != null) {
            return procedureTime.getTimeGeometricPrimitive();
        }
        return null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getProcedureParameter() {
        return procedureParameter;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getDefinition() {
        return definition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BoundingShape getBoundedBy() {
        return null;
    }

    @Override
    public void extendBoundingShape(AbstractGeometry newGeom) {
        // not bounds in this version
    }

    /**
     * Retourne un code représentant cette observation.
     */
    @Override
    public final int hashCode() {
        return featureOfInterest.hashCode() ^ observedProperty.hashCode() ^ result.hashCode();
    }

    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ObservationType) {
            final ObservationType that = (ObservationType) object;

            boolean res = false;
            if (this.result == null && that.result == null) {
                res = true;
            } else if (this.result != null && that.result != null) {
                res = Objects.equals(this.result.getValue(), that.result.getValue());
            }
            return Objects.equals(this.id,                  that.id)                  &&
                   Objects.equals(this.name,                that.name)                &&
                   Objects.equals(this.featureOfInterest,   that.featureOfInterest)   &&
                   Objects.equals(this.observedProperty,    that.observedProperty)    &&
                   Objects.equals(this.procedure,           that.procedure)           &&
                   Objects.equals(this.resultQuality,       that.resultQuality)       &&
                   res                                                                &&
                   Objects.equals(this.samplingTime,        that.samplingTime)        &&
                   Objects.equals(this.observationMetadata, that.observationMetadata) &&
                   Objects.equals(this.procedureTime,       that.procedureTime)       &&
                   Objects.equals(this.procedureParameter,  that.procedureParameter);
        }
        return false;
    }

    /**
     * Verifie si l'observation est complete.
     */
    public boolean isComplete() {
        //TODO appeler les isCOmplete des attributs
        return (procedure != null) && (observedProperty != null) && (featureOfInterest != null) && (result != null);
    }

    /**
     * Retourne une chaine de charactere representant l'observation.
     */
    @Override
    public String toString() {
        StringBuilder s    = new StringBuilder("[").append(this.getClass().getSimpleName()).append(']');
        char lineSeparator = '\n';
        s.append(lineSeparator);
        s.append("id = ").append(id).append(lineSeparator);
        s.append("name = ").append(name).append(lineSeparator);
        if (definition != null) {
            s.append("definition = ").append(definition).append(lineSeparator);
        }
        s.append(lineSeparator);
        if (samplingTime != null) {
            s.append("samplingTime = ").append(samplingTime.toString()).append(lineSeparator);
        }
        if (procedure != null) {
            s.append("procedure = ").append(procedure.toString()).append(lineSeparator);
        } else {
            s.append("procedure is null!").append(lineSeparator);
        }
        if (observedProperty != null) {
            s.append("observedProperty = ").append(observedProperty.toString()).append(lineSeparator);
        } else {
            s.append("observed property is null!").append(lineSeparator);
        }
        if (featureOfInterest != null) {
            s.append("feature Of Interest = ").append(featureOfInterest.toString()).append(lineSeparator);
        } else {
            s.append("feature Of Interest is null!").append(lineSeparator);
        }
        if (result != null) {
            s.append(" result = ").append(result.getValue()).append(lineSeparator);
        }
        return s.toString();
    }
}
