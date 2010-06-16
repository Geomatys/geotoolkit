package org.geotoolkit.data.model.xal;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultXal implements Xal{

    private final List<AddressDetails> addressDetails;
    private final String version;

    public DefaultXal(List<AddressDetails> addressDetails, String version){
        this.addressDetails = (addressDetails == null) ? EMPTY_LIST : addressDetails;
        this.version = version;
    }

    @Override
    public List<AddressDetails> getAddressDetails() {return this.addressDetails;}

    @Override
    public String getVersion() {return this.version;}

}
