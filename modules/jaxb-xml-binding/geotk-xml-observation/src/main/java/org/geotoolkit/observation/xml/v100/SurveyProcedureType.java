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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotoolkit.gml.xml.v311.AbstractTimeGeometricPrimitiveType;
import org.geotoolkit.internal.sql.table.Entry;
import org.geotoolkit.metadata.iso.citation.DefaultResponsibleParty;
import org.geotoolkit.util.Utilities;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.observation.Measure;
import org.opengis.observation.sampling.SurveyProcedure;
import org.opengis.referencing.datum.Datum;
import org.opengis.temporal.TemporalObject;
import org.opengis.util.GenericName;

/**
 *Implémentation d'une entrée représentant une {@linkplain SurveyProcedure SurveyProcedure}.
 *
 * @version $Id:
 * @author Guilhem Legal
 * @module pending
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SurveyProcedure")
public class SurveyProcedureType implements SurveyProcedure, Entry {
    
    
    /**
     */
    @XmlTransient
    private DefaultResponsibleParty operator;

    @XmlTransient
    private String name;
    
    // JAXB issue  private Datum elevationDatum;
    
    private ProcessType elevationMethod;
    
    private MeasureType elevationAccuracy;
    
    // JAXB issue  private Datum geodeticDatum;
    
    private ProcessType positionMethod;
    
    private MeasureType positionAccuracy;
    
    // JAXB ISSUE private GenericNameType projection;
    
    private AbstractTimeGeometricPrimitiveType surveyTime;
    
    /**
     * Constructeur utilisé par JAXB
     */
    private SurveyProcedureType() {}
    
    /** Creates a new instance of SurveyProcedureType */
    public SurveyProcedureType( final String name,
            final DefaultResponsibleParty operator,
            final Datum elevationDatum,
            final ProcessType elevationMethod,
            final MeasureType elevationAccuracy,
            final Datum geodeticDatum,
            final ProcessType positionMethod,
            final MeasureType positionAccuracy,
            final GenericName projection,
            final AbstractTimeGeometricPrimitiveType surveyTime) 
    {
        this.name  = name;
        this.operator = operator;
       // JAXB issue  this.elevationDatum = elevationDatum;
        this.elevationMethod = elevationMethod;
        this.elevationAccuracy = elevationAccuracy;
        // JAXB issue this.geodeticDatum = geodeticDatum;
        this.positionMethod = positionMethod;
        this.positionAccuracy = positionAccuracy; 
         // JAXB issue this.projection = projection;
        this.surveyTime = surveyTime;
                
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return name;
    }
    
    public ResponsibleParty getOperator() {
        return operator;
    }
        
    public Datum getElevationDatum() {
        throw new UnsupportedOperationException("Not supported yet.");
        //return elevationDatum;
    }
    
    public org.opengis.observation.Process getElevationMethod() {
        return elevationMethod;
    }
    
    public Measure getElevationAccuracy() {
        throw new UnsupportedOperationException("Not supported yet.");
        //return elevationAccuracy;
    }
    
    public Datum getGeodeticDatum() {
        throw new UnsupportedOperationException("Not supported yet.");
        //return geodeticDatum;
    }
    
    public org.opengis.observation.Process getPositionMethod() {
    return positionMethod;
    }
    
    public Measure getPositionAccuracy() {
        throw new UnsupportedOperationException("Not supported yet.");
        //return positionAccuracy;
    }
    
    public GenericName getProjection() {
        throw new UnsupportedOperationException("Not supported yet.");
        //return projection;
    }
    
    public TemporalObject getSurveyTime() {
        return (TemporalObject) surveyTime;
    }
    
     /**
     * Retourne le code numérique identifiant cette entrée.
     */
    @Override
    public int hashCode() {
        return getName().hashCode();
    }
    
    /**
     * Vérifie que cette procedure est identique à l'objet spécifié
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof SurveyProcedureType && super.equals(object)) {
            final SurveyProcedureType that = (SurveyProcedureType) object;
            return Utilities.equals(this.operator,          that.operator)   &&
                   //Utilities.equals(this.elevationDatum,    that.elevationDatum)   && 
                   Utilities.equals(this.elevationMethod,   that.elevationMethod) &&
                   Utilities.equals(this.elevationAccuracy, that.elevationAccuracy) &&
                   Utilities.equals(this.positionAccuracy,  that.positionAccuracy) &&
                   Utilities.equals(this.positionMethod,    that.positionMethod) &&
                   //Utilities.equals(this.projection,        that.projection) &&
                   Utilities.equals(this.surveyTime,        that.surveyTime);
        }
        return false;
    }
}
