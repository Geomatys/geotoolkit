package org.geotoolkit.data.model;

import java.util.List;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xal.GrPostal;
import org.geotoolkit.data.model.xal.PostalServiceElements;
import org.geotoolkit.data.model.xal.Xal;
import org.geotoolkit.data.model.xal.XalException;

/**
 *
 * @author Samuel Andrés
 */
public interface XalFactory {

    public Xal createXal(List<AddressDetails> addressDetails, String version);

    public AddressDetails createAddressDetails(PostalServiceElements postalServiceElements, Object localisation,
            String addressType, String currentStatus, String validFromDate, String validToDate,
            String usage, GrPostal grPostal, String AddressDetailsKey) throws XalException;
}
