package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class SubPremiseDefault implements SubPremise {

    private final List<GenericTypedGrPostal> addressLines;
    private final List<SubPremiseName> subPremiseNames;
    private SubPremiseLocation subPremiseLocation;
    private List<SubPremiseNumber> subPremiseNumbers;
    private final List<SubPremiseNumberPrefix> subPremiseNumberPrefixes;
    private final List<SubPremiseNumberSuffix> subPremiseNumberSuffixes;
    private final List<BuildingName> buildingNames;
    private final Firm firm;
    private final MailStop mailStop;
    private final PostalCode postalCode;
    private final SubPremise subPremise;
    private final String type;

    public SubPremiseDefault(List<GenericTypedGrPostal> addressLines, 
            List<SubPremiseName> subPremiseNames, Object location,
            List<SubPremiseNumberPrefix> subPremiseNumberPrefixes,
            List<SubPremiseNumberSuffix> subPremiseNumberSuffixes, 
            List<BuildingName> buildingNames, Firm firm, MailStop mailStop,
            PostalCode postalCode, SubPremise subPremise, String type) throws XalException{
        this.addressLines = addressLines;
        this.subPremiseNames = subPremiseNames;
        if (location instanceof SubPremiseLocation){
            this.subPremiseLocation = (SubPremiseLocation) location;
        } else if (location instanceof List){
            try {
                this.subPremiseNumbers = (List<SubPremiseNumber>) location;
            } catch (ClassCastException e){
                throw new XalException("This kind of location ("+location.getClass()+") is not allowed here : "+this.getClass());
            }
        } else if (location != null){
            throw new XalException("This kind of location ("+location.getClass()+") is not allowed here : "+this.getClass());
        }
        this.subPremiseNumberPrefixes = subPremiseNumberPrefixes;
        this.subPremiseNumberSuffixes = subPremiseNumberSuffixes;
        this.buildingNames = buildingNames;
        this.firm = firm;
        this.mailStop = mailStop;
        this.postalCode = postalCode;
        this.subPremise = subPremise;
        this.type = type;
    }

    @Override
    public List<GenericTypedGrPostal> getAddressLines() {return this.addressLines;}

    @Override
    public List<SubPremiseName> getSubPremiseNames() {return this.subPremiseNames;}

    @Override
    public SubPremiseLocation getSubPremiseLocation() {return this.subPremiseLocation;}

    @Override
    public List<SubPremiseNumber> getSubPremiseNumbers() {return this.subPremiseNumbers;}

    @Override
    public List<SubPremiseNumberPrefix> getSubPremiseNumberPrefixes() {return this.subPremiseNumberPrefixes;}

    @Override
    public List<SubPremiseNumberSuffix> getSubPremiseNumberSuffixes() {return this.subPremiseNumberSuffixes;}

    @Override
    public List<BuildingName> getBuildingNames() {return this.buildingNames;}

    @Override
    public Firm getFirm() {return this.firm;}

    @Override
    public MailStop getMailStop() {return this.mailStop;}

    @Override
    public PostalCode getPostalCode() {return this.postalCode;}

    @Override
    public SubPremise getSubPremise() {return this.subPremise;}

    @Override
    public String getType() {return this.type;}

}
