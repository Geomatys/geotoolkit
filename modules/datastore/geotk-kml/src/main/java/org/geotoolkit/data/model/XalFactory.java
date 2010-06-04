package org.geotoolkit.data.model;

import java.util.List;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xal.AddressIdentifier;
import org.geotoolkit.data.model.xal.AddressIdentifierDefault;
import org.geotoolkit.data.model.xal.AddressLines;
import org.geotoolkit.data.model.xal.GenericTypedGrPostal;
import org.geotoolkit.data.model.xal.GrPostal;
import org.geotoolkit.data.model.xal.PostalServiceElements;
import org.geotoolkit.data.model.xal.SortingCode;
import org.geotoolkit.data.model.xal.Xal;
import org.geotoolkit.data.model.xal.XalException;

/**
 *
 * @author Samuel Andrés
 */
public interface XalFactory {

    /**
     *
     * @param addressDetails
     * @param version
     * @return
     */
    public Xal createXal(List<AddressDetails> addressDetails, String version);

    /**
     *
     * @param postalServiceElements
     * @param localisation
     * @param addressType
     * @param currentStatus
     * @param validFromDate
     * @param validToDate
     * @param usage
     * @param grPostal
     * @param AddressDetailsKey
     * @return
     * @throws XalException
     */
    public AddressDetails createAddressDetails(PostalServiceElements postalServiceElements, Object localisation,
            String addressType, String currentStatus, String validFromDate, String validToDate,
            String usage, GrPostal grPostal, String AddressDetailsKey) throws XalException;

    /**
     *
     * @param addressLines
     * @return
     */
    public AddressLines createAddressLines(List<GenericTypedGrPostal> addressLines);

    /**
     * 
     * @param type
     * @param grPostal
     * @param Content
     * @return
     */
    public GenericTypedGrPostal createGenericTypedGrPostal(String type, GrPostal grPostal, String Content);

    /**
     * 
     * @param code
     * @return
     */
    public GrPostal createGrPostal(String code);

    /**
     * 
     * @param addressIdentifiers
     * @param endorsementLineCode
     * @param keyLineCode
     * @param barCode
     * @param sortingCode
     * @param addressLatitude
     * @param addressLatitudeDirection
     * @param addressLongitude
     * @param addressLongitudeDirection
     * @param supplementaryPostalServiceData
     * @param type
     * @return
     */
    public PostalServiceElements createPostalServiceElements(List<AddressIdentifier> addressIdentifiers, GenericTypedGrPostal endorsementLineCode,
            GenericTypedGrPostal keyLineCode, GenericTypedGrPostal barCode, SortingCode sortingCode, GenericTypedGrPostal addressLatitude,
            GenericTypedGrPostal addressLatitudeDirection, GenericTypedGrPostal addressLongitude, GenericTypedGrPostal addressLongitudeDirection,
            List<GenericTypedGrPostal> supplementaryPostalServiceData, String type);

    /**
     * 
     * @param type
     * @param grPostal
     * @return
     */
    public SortingCode createSortingCode(String type, GrPostal grPostal);

    /**
     *
     * @param content
     * @param identifierType
     * @param type
     * @param grPostal
     * @return
     */
    public AddressIdentifier createAddressIdentifier(String content, String identifierType, String type, GrPostal grPostal);
}
