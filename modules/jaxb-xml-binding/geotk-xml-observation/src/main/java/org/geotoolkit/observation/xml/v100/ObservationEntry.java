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

// jaxb import
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

// openGis dependencies
import org.geotoolkit.gml.xml.v311.AbstractFeatureEntry;
import org.opengis.observation.Process;
import org.opengis.metadata.quality.Element;
import org.opengis.metadata.Metadata;
import org.opengis.observation.Observation;
import org.opengis.observation.Phenomenon;
import org.opengis.observation.sampling.SamplingFeature;

// GeotoolKit dependencies
import org.geotoolkit.gml.xml.v311.AbstractTimeGeometricPrimitiveType;
import org.geotoolkit.gml.xml.v311.FeatureCollectionType;
import org.geotoolkit.gml.xml.v311.FeaturePropertyType;
import org.geotoolkit.gml.xml.v311.ReferenceEntry;
import org.geotoolkit.gml.xml.v311.TimePeriodType;
import org.geotoolkit.gml.xml.v311.TimePositionType;
import org.geotoolkit.internal.sql.table.Entry;
import org.geotoolkit.sampling.xml.v100.SamplingFeatureEntry;
import org.geotoolkit.sampling.xml.v100.SamplingPointEntry;
import org.geotoolkit.swe.xml.v101.AnyResultEntry;
import org.geotoolkit.swe.xml.v101.DataArrayEntry;
import org.geotoolkit.swe.xml.v101.DataArrayPropertyType;
import org.geotoolkit.swe.xml.v101.PhenomenonEntry;
import org.geotoolkit.swe.xml.v101.PhenomenonPropertyType;
import org.geotoolkit.swe.xml.v101.TimeGeometricPrimitivePropertyType;
import org.geotoolkit.util.Utilities;
import org.geotoolkit.metadata.iso.DefaultMetadata;
import org.geotoolkit.sampling.xml.v100.SamplingCurveType;
import org.geotoolkit.sampling.xml.v100.SamplingSolidType;
import org.geotoolkit.sampling.xml.v100.SamplingSurfaceType;
import org.geotoolkit.util.logging.Logging;


/**
 * Implémentation d'une entrée représentant une {@linkplain Observation observation}.
 *
 * @version $Id: ObservationEntry.java 1559 2009-04-23 14:42:42Z glegal $
 * @author Martin Desruisseaux
 * @author Antoine Hnawia
 * @author Guilhem Legal
 * @module pending
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
    "result"
})
@XmlRootElement(name = "Observation")
@XmlSeeAlso({ MeasurementEntry.class})
public class ObservationEntry implements Observation, Entry {
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
    private static final Logger LOGGER = Logging.getLogger("observationEntry");
    
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
    private ProcessEntry procedure;
    
     /**
     * Référence vers la {@linkplain Distribution distribution} associée à cet observable.
     
    @XmlTransient
    private Distribution distribution;*/
    
    /**
     * La qualité de la donnée. Peut être nul si cette information n'est pas disponible.
     */
    @XmlTransient
    private ElementEntry resultQuality;
    
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
    public ObservationEntry() {}

     /**
     * Build a clone of an observation
     */
    public ObservationEntry(ObservationEntry observation) {
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
            this.result              = observation.result;
        }
        this.resultQuality       = observation.resultQuality;
        this.samplingTime        = observation.samplingTime;
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
    public ObservationEntry(final String               name,
                            final String               definition,
                            final SamplingFeatureEntry featureOfInterest, 
                            final PhenomenonEntry      observedProperty,
                            final ProcessEntry         procedure,
                            final ElementEntry         quality,
                            final Object               result,
                            final AbstractTimeGeometricPrimitiveType  samplingTime,
                            final DefaultMetadata      observationMetadata,
                            final AbstractTimeGeometricPrimitiveType  procedureTime,
                            final Object               procedureParameter) 
    {
        this.name                = name;
        this.definition          = definition;
        if (featureOfInterest instanceof SamplingPointEntry) {
            this.featureOfInterest   = new FeaturePropertyType(SAMPLING_FACTORY.createSamplingPoint((SamplingPointEntry)featureOfInterest));
        } else if (featureOfInterest instanceof SamplingCurveType) {
            this.featureOfInterest   = new FeaturePropertyType(SAMPLING_FACTORY.createSamplingCurve((SamplingCurveType)featureOfInterest));
        } else if (featureOfInterest instanceof SamplingSolidType) {
            this.featureOfInterest   = new FeaturePropertyType(SAMPLING_FACTORY.createSamplingSolid((SamplingSolidType)featureOfInterest));
        } else if (featureOfInterest instanceof SamplingSurfaceType) {
            this.featureOfInterest   = new FeaturePropertyType(SAMPLING_FACTORY.createSamplingSurface((SamplingSurfaceType)featureOfInterest));
        }
        if (observedProperty != null) {
            this.observedProperty    = new PhenomenonPropertyType(observedProperty);
        }
        this.procedure           = procedure;
        this.resultQuality       = quality;
        this.result              = OM_FACTORY.createResult(result);
        this.observationMetadata = observationMetadata;
        this.procedureParameter  = procedureParameter; 
        this.samplingTime        = new TimeGeometricPrimitivePropertyType(samplingTime);
        this.procedureTime       = new TimeGeometricPrimitivePropertyType(procedureTime);
    }
    
    /**
     * Build a new observation.
     * 
     * 
     * @param featureOfInterest The observation station.
     * @param observedProperty  The observed phenomenon.
     * @param procedure         The associated procedure.
     */
    public ObservationEntry(final String                name,
                            final String                definition,
                            final SamplingFeatureEntry  featureOfInterest, 
                            final PhenomenonEntry       observedProperty,
                            final ProcessEntry          procedure,
                            final Object                result,
                            final AbstractTimeGeometricPrimitiveType   samplingTime)
    {
        this.name                = name;
        this.definition          = definition;
        if (featureOfInterest instanceof SamplingPointEntry) {
            this.featureOfInterest   = new FeaturePropertyType(SAMPLING_FACTORY.createSamplingPoint((SamplingPointEntry)featureOfInterest));
        } else if (featureOfInterest instanceof SamplingCurveType) {
            this.featureOfInterest   = new FeaturePropertyType(SAMPLING_FACTORY.createSamplingCurve((SamplingCurveType)featureOfInterest));
        } else if (featureOfInterest instanceof SamplingSolidType) {
            this.featureOfInterest   = new FeaturePropertyType(SAMPLING_FACTORY.createSamplingSolid((SamplingSolidType)featureOfInterest));
        } else if (featureOfInterest instanceof SamplingSurfaceType) {
            this.featureOfInterest   = new FeaturePropertyType(SAMPLING_FACTORY.createSamplingSurface((SamplingSurfaceType)featureOfInterest));
        }
        this.observedProperty    = new PhenomenonPropertyType(observedProperty);
        this.procedure           = procedure;
        this.resultQuality       = null;      
        this.result              = OM_FACTORY.createResult(result);
        this.observationMetadata = null;
        this.procedureTime       = null;
        this.procedureParameter  = null;
        this.samplingTime        = new TimeGeometricPrimitivePropertyType(samplingTime);
    }

    /**
     * Build a new observation.
     *
     *
     * @param featureOfInterest The observation station.
     * @param observedProperty  The observed phenomenon.
     * @param procedure         The associated procedure.
     */
    public ObservationEntry(final String                name,
                            final String                definition,
                            final FeaturePropertyType   featureOfInterest,
                            final PhenomenonEntry       observedProperty,
                            final ProcessEntry          procedure,
                            final Object                result,
                            final AbstractTimeGeometricPrimitiveType   samplingTime)
    {
        this.name                = name;
        this.definition          = definition;
        this.featureOfInterest   = featureOfInterest;
        if (observedProperty != null) {
            this.observedProperty    = new PhenomenonPropertyType(observedProperty);
        }
        this.procedure           = procedure;
        this.resultQuality       = null;
        this.result              = OM_FACTORY.createResult(result);
        this.observationMetadata = null;
        this.procedureTime       = null;
        this.procedureParameter  = null;
        this.samplingTime        = new TimeGeometricPrimitivePropertyType(samplingTime);
    }

    /**
     * Construit un nouveau template temporaire d'observation a partir d'un template fournit en argument.
     * On y rajoute un samplingTime et un id temporaire. 
     */
    public ObservationEntry getTemporaryTemplate(String temporaryName, AbstractTimeGeometricPrimitiveType time) {
        if (time == null) { 
            TimePositionType begin = new  TimePositionType("1900-01-01T00:00:00");
            time = new TimePeriodType(begin);
        }
        PhenomenonEntry pheno = null;
        if (this.observedProperty != null) {
            pheno = this.observedProperty.getPhenomenon();
        }
        //debugging purpose
        Object res = null;

        if (this.result != null) {
            res = this.result.getValue();
            if (this.result.getValue() instanceof DataArrayPropertyType) {
                DataArrayEntry d = ((DataArrayPropertyType)this.result.getValue()).getDataArray();
                d.setElementCount(0);
                d.setValues("");
            }
        }
        return new ObservationEntry(temporaryName,
                                    this.definition,
                                    this.featureOfInterest,
                                    pheno,
                                    this.procedure,
                                    res,
                                    time);
        
    }
    
    /**
     */
    public void setName(String name) {
        this.name  = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }

    public String getIdentifier() {
        return name;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated
    public SamplingFeature getFeatureOfInterest() {
        if (featureOfInterest != null) {
            if (featureOfInterest.getAbstractFeature() instanceof SamplingFeature) {
                return (SamplingFeature)featureOfInterest.getAbstractFeature();
            } else {
                LOGGER.warning("information lost getFeatureOfInterest() is deprecated use getPropertyFeatureOfInterest() instead");
            }
        }
        return null;
    }

    public void setFeatureOfInterest(AbstractFeatureEntry featureOfInterest) {
        if (featureOfInterest != null) {
            if (featureOfInterest instanceof SamplingPointEntry) {
                this.featureOfInterest = new FeaturePropertyType(SAMPLING_FACTORY.createSamplingPoint((SamplingPointEntry) featureOfInterest));
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
    
    public FeaturePropertyType getPropertyFeatureOfInterest(){
        return featureOfInterest;
    }

    public void setPropertyFeatureOfInterest(FeaturePropertyType featureOfInterest){
        this.featureOfInterest = featureOfInterest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phenomenon getObservedProperty() {
        if (observedProperty != null) {
            return observedProperty.getPhenomenon();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setObservedProperty(PhenomenonEntry observedProperty) {
        if (observedProperty != null) {
            this.observedProperty = new PhenomenonPropertyType(observedProperty);
        }
    }
    
    public PhenomenonPropertyType getPropertyObservedProperty() {
       return observedProperty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Process getProcedure() {
        return procedure;
    }
    
    /**
     * fixe le capteur qui a effectué cette observation.
     */
    public void setProcedure(ProcessEntry process) {
        this.procedure = process;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element getQuality() {
        return resultQuality;
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
    public void setResult(Object result) {
        if (!(result instanceof ReferenceEntry) && !(result instanceof AnyResultEntry) &&
            !(result instanceof DataArrayPropertyType) && !(result instanceof MeasureEntry)) {
            throw new IllegalArgumentException("this type " + result.getClass().getSimpleName() +
                                           " is not allowed in result");
        }
        this.result = OM_FACTORY.createResult(result);
    }

    /**
     * Update the result of the obervation by setting the specified String value and numbers of result.
     *
     * @param values A datablock of values.
     * @param nbResult the number of result in the datablock.
     * @throws IllegalArgumentException if the resulat of the observation is not a DataArray.
     */
    public void updateDataArrayResult(String values, int nbResult) {
        if (getResult() instanceof DataArrayPropertyType) {
            DataArrayEntry array = ((DataArrayPropertyType)getResult()).getDataArray();
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
        if (samplingTime != null)
            return samplingTime.getTimeGeometricPrimitive();
        return null;
        
    }
    
    /**
     * {@inheritDoc}
     */
    public void setSamplingTime(AbstractTimeGeometricPrimitiveType value) {
        this.samplingTime = new TimeGeometricPrimitivePropertyType(value);
    }

    /**
     * {@inheritDoc}
     */
    public void extendSamplingTime(String newEndBound) {
        if (samplingTime != null && samplingTime.getTimeGeometricPrimitive() instanceof TimePeriodType) {
            ((TimePeriodType)samplingTime.getTimeGeometricPrimitive()).setEndPosition(new TimePositionType(newEndBound));
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
        if (procedureTime != null)
            return procedureTime.getTimeGeometricPrimitive();
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
     * Return true if the observation match the specified template.
     */ 
    public boolean matchTemplate(ObservationEntry template) {
        
        boolean obsProperty = false;
        if (this.observedProperty != null && template.observedProperty != null) {
            obsProperty = Utilities.equals(this.observedProperty.getPhenomenon(),    template.observedProperty.getPhenomenon());
            if (!obsProperty) {
                LOGGER.info("\ncomparing observed property:\nTHIS     => " +  this.observedProperty.getPhenomenon() +
                            "\nTEMPLATE => "                               + template.observedProperty.getPhenomenon() + '\n');
            }
        } else {
            obsProperty = this.observedProperty == null && template.observedProperty == null;
        }
        
        boolean obsFoi = false;
        if (this.featureOfInterest != null && template.featureOfInterest != null) {
            obsFoi = Utilities.equals(this.featureOfInterest.getAbstractFeature(),    template.featureOfInterest.getAbstractFeature());
            if (!obsFoi) {
                LOGGER.info("\ncomparing feature of interest:\nTHIS    => "+  this.featureOfInterest.getAbstractFeature() +
                            "\nTEMPLATE => " + template.featureOfInterest.getAbstractFeature() + '\n');
            }
        } else {
            obsFoi = this.featureOfInterest == null && template.featureOfInterest == null;
        }
        
        boolean match = obsFoi                                                                   &&
                        Utilities.equals(this.procedure,           template.procedure)           &&
                        Utilities.equals(this.resultQuality,       template.resultQuality)       && 
                        Utilities.equals(this.observationMetadata, template.observationMetadata) &&
                        Utilities.equals(this.procedureTime,       template.procedureTime)       &&
                        Utilities.equals(this.procedureParameter,  template.procedureParameter)  &&
                        obsProperty;
        if (!match) {
            LOGGER.severe("error matching template report:" + '\n' +
                   "FOI  =>" + obsFoi                                                                   + '\n' +
                   "PROC =>" + Utilities.equals(this.procedure,           template.procedure)           + '\n' +
                   "QUAL =>" + Utilities.equals(this.resultQuality,       template.resultQuality)       + '\n' + 
                   "META =>" + Utilities.equals(this.observationMetadata, template.observationMetadata) + '\n' +
                   "PTI  =>" + Utilities.equals(this.procedureTime,       template.procedureTime)       + '\n' +
                   "PPAM =>" + Utilities.equals(this.procedureParameter,  template.procedureParameter)  + '\n' +
                   "PHEN =>" + obsProperty);
        }
        return match;
        
             
               
        
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
        if (object instanceof ObservationEntry) {
            final ObservationEntry that = (ObservationEntry) object;

            boolean res = false;
            if (this.result == null && that.result == null) {
                res = true;
            } else if (this.result != null && that.result != null) {
                res = Utilities.equals(this.result.getValue(), that.result.getValue());
            }
            return Utilities.equals(this.featureOfInterest,   that.featureOfInterest)   &&
                   Utilities.equals(this.observedProperty,    that.observedProperty)    &&
                   Utilities.equals(this.procedure,           that.procedure)           &&
                   Utilities.equals(this.resultQuality,       that.resultQuality)       && 
                   res                                                                  &&
                   Utilities.equals(this.samplingTime,        that.samplingTime)        &&
                   Utilities.equals(this.observationMetadata, that.observationMetadata) &&
                   Utilities.equals(this.procedureTime,       that.procedureTime)       &&
                   Utilities.equals(this.procedureParameter,  that.procedureParameter);
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
        s.append("name = ").append(name);
        if (definition != null)
            s.append("definition = ").append(definition);
        s.append(lineSeparator);
        if (samplingTime != null)
            s.append("samplingTime = ").append(samplingTime.toString()).append(lineSeparator);
       if (procedure != null)
            s.append("procedure = ").append(procedure.toString()).append(lineSeparator);
        else
            s.append("procedure is null!").append(lineSeparator);
        
        if (observedProperty != null)
            s.append("observedProperty = ").append(observedProperty.toString()).append(lineSeparator);
        else s.append("observed property is null!").append(lineSeparator);
        if (featureOfInterest != null)
            s.append("feature Of Interest = ").append(featureOfInterest.toString()).append(lineSeparator); 
        else
            s.append("feature Of Interest is null!").append(lineSeparator);
        if (result != null)       
            s.append(" result = ").append(result.getValue()).append(lineSeparator);
        return s.toString();
    }

    

}
