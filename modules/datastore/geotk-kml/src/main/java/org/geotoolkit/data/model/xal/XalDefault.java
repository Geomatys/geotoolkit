package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class XalDefault implements Xal{

    private List<AddressDetails> addressDetails;
    private String version;

    public XalDefault(List<AddressDetails> addressDetails, String version){
        this.addressDetails = addressDetails;
        this.version = version;
    }

    @Override
    public List<AddressDetails> getAddressDetails() {return this.addressDetails;}

    @Override
    public String getVersion() {return this.version;}

}
