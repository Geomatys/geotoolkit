package org.geotoolkit.data.model.xal;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPremise implements Premise{

    private final List<GenericTypedGrPostal> addressLines;
    private final List<PremiseName> premiseNames;
    private PremiseLocation premiseLocation;
    private List<PremiseNumber> premiseNumbers;
    private PremiseNumberRange premiseNumberRange;
    private final List<PremiseNumberPrefix> premiseNumberPrefixes;
    private final List<PremiseNumberSuffix> premiseNumberSuffixes;
    private final List<BuildingName> buildingNames;
    private List<SubPremise> subPremises;
    private Firm firm;
    private final MailStop mailStop;
    private final PostalCode postalCode;
    private final Premise premise;
    private final String type;
    private final String premiseDependency;
    private final String premiseDependencyType;
    private final String premiseThoroughfareConnector;

    /**
     * 
     * @param addressLines
     * @param premiseNames
     * @param localisation
     * @param premiseNumberPrefixes
     * @param premiseNumberSuffixes
     * @param buildingNames
     * @param sub
     * @param mailStop
     * @param postalCode
     * @param premise
     * @param type
     * @param premiseDependency
     * @param premiseDependencyType
     * @param premiseThoroughfareConnector
     * @throws XalException
     */
    public DefaultPremise(List<GenericTypedGrPostal> addressLines, List<PremiseName> premiseNames,
            Object location,
            List<PremiseNumberPrefix> premiseNumberPrefixes,
            List<PremiseNumberSuffix> premiseNumberSuffixes,
            List<BuildingName> buildingNames,
            Object sub,
            MailStop mailStop, PostalCode postalCode, Premise premise,
            String type, String premiseDependency, String premiseDependencyType,
            String premiseThoroughfareConnector) throws XalException{
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.premiseNames = (premiseNames == null) ? EMPTY_LIST : premiseNames;
        if (location instanceof PremiseLocation){
            premiseLocation = (PremiseLocation) location;
        } else if (location instanceof List){
            try {
                premiseNumbers = (List<PremiseNumber>) location;
            } catch (ClassCastException e){
                throw new XalException("This kind of location ("+location.getClass()+") is not allowed here : "+this.getClass());
            }
        } else if (location instanceof PremiseNumberRange){
            premiseNumberRange = (PremiseNumberRange) location;
        } else if (location != null){
            throw new XalException("This kind of location ("+location.getClass()+") is not allowed here : "+this.getClass());
        }
        this.premiseNumberPrefixes = (premiseNumberPrefixes == null) ? EMPTY_LIST : premiseNumberPrefixes;
        this.premiseNumberSuffixes = (premiseNumberSuffixes == null) ? EMPTY_LIST : premiseNumberSuffixes;
        this.buildingNames = (buildingNames == null) ? EMPTY_LIST : buildingNames;
        if (sub instanceof List){
            try {
                subPremises =  (List<SubPremise>) sub;
            } catch (ClassCastException e){
                throw new XalException("This kind of class ("+sub.getClass()+") is not allowed here : "+this.getClass());
            }
        } else if (sub instanceof Firm){
            firm =  (Firm) sub;
        } else if (sub != null){
            throw new XalException("This kind of class ("+sub.getClass()+") is not allowed here : "+this.getClass());
        }
        this.mailStop = mailStop;
        this.postalCode = postalCode;
        this.premise = premise;
        this.type = type;
        this.premiseDependency = premiseDependency;
        this.premiseDependencyType = premiseDependencyType;
        this.premiseThoroughfareConnector = premiseThoroughfareConnector;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<GenericTypedGrPostal> getAddressLines() {return this.addressLines;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<PremiseName> getPremiseNames() {return this.premiseNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PremiseLocation getPremiseLocation() {return this.premiseLocation;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<PremiseNumber> getPremiseNumbers() {return this.premiseNumbers;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PremiseNumberRange getPremiseNumberRange() {return this.premiseNumberRange;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<PremiseNumberPrefix> getPremiseNumberPrefix() {return this.premiseNumberPrefixes;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<PremiseNumberSuffix> getPremiseNumberSuffix() {return this.premiseNumberSuffixes;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<BuildingName> getBuildingNames() {return this.buildingNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<SubPremise> getSubPremises() {return this.subPremises;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Firm getFirm() {return this.firm;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public MailStop getMailStop() {return this.mailStop;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalCode getPostalCode() {return this.postalCode;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Premise getPremise() {return this.premise;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getType() {return this.type;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getPremiseDependency() {return this.premiseDependency;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getPremiseDependencyType() {return this.premiseDependencyType;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getPremiseThoroughfareConnector() {return this.premiseThoroughfareConnector;}

}
