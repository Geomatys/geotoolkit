package org.geotoolkit.data.model.xal;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultDependentLocality implements DependentLocality {

    private final List<GenericTypedGrPostal> addressLines;
    private final List<GenericTypedGrPostal> dependentLocalityNames;
    private final DependentLocalityNumber dependentLocalityNumber;
    private PostBox postBox;
    private LargeMailUser largeMailUser;
    private PostOffice postOffice;
    private PostalRoute postalRoute;
    private final Thoroughfare thoroughfare;
    private final Premise premise;
    private final DependentLocality dependentLocality;
    private final PostalCode postalCode;
    private final String type;
    private final String usageType;
    private final String connector;
    private final String indicator;

    /**
     *
     * @param addressLines
     * @param dependentLocalityNames
     * @param dependentLocalityNumber
     * @param localisation
     * @param thoroughfare
     * @param premise
     * @param dependentLocality
     * @param postalCode
     * @param type
     * @param usageType
     * @param connector
     * @param indicator
     * @throws XalException
     */
    public DefaultDependentLocality(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> dependentLocalityNames,
            DependentLocalityNumber dependentLocalityNumber,
            Object localisation, Thoroughfare thoroughfare, Premise premise,
            DependentLocality dependentLocality, PostalCode postalCode,
            String type, String usageType, String connector, String indicator) throws XalException{
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.dependentLocalityNames = (dependentLocalityNames == null) ? EMPTY_LIST : dependentLocalityNames;
        this.dependentLocalityNumber = dependentLocalityNumber;
        if (localisation instanceof PostBox){
            this.postBox = (PostBox) localisation;
        } else if (localisation instanceof LargeMailUser){
            this.largeMailUser = (LargeMailUser) localisation;
        } else if (localisation instanceof PostOffice){
            this.postOffice = (PostOffice) localisation;
        } else if (localisation instanceof PostalRoute){
            this.postalRoute = (PostalRoute) localisation;
        } else if ( localisation != null){
            throw new XalException("This kind of localisation is not allowed here."+localisation.getClass());
        }
        this.thoroughfare = thoroughfare;
        this.premise = premise;
        this.dependentLocality = dependentLocality;
        this.postalCode = postalCode;
        this.type = type;
        this.usageType = usageType;
        this.connector = connector;
        this.indicator = indicator;
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
    public List<GenericTypedGrPostal> getDependentLocalityNames() {return this.dependentLocalityNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public DependentLocalityNumber getDependentLocalityNumber() {return this.dependentLocalityNumber;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBox getPostBox() {return this.postBox;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public LargeMailUser getLargeMailUser() {return this.largeMailUser;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostOffice getPostOffice() {return this.postOffice;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalRoute getPostalRoute() {return this.postalRoute;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Thoroughfare getThoroughfare() {return this.thoroughfare;}

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
    public DependentLocality getDependentLocality() {return this.dependentLocality;}

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
    public String getType() {return this.type;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getUsageType() {return this.usageType;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getConnector() {return this.connector;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getIndicator() {return this.indicator;}

}
