package org.geotoolkit.data.model.xal;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class FirmDefault implements Firm {

    private final List<GenericTypedGrPostal> addressLines;
    private final List<GenericTypedGrPostal> firmNames;
    private final List<Department> departments;
    private final MailStop mailStop;
    private final PostalCode postalCode;
    private final String type;

    /**
     *
     * @param addressLines
     * @param firmNames
     * @param departments
     * @param mailStop
     * @param postalCode
     * @param type
     */
    public FirmDefault(List<GenericTypedGrPostal> addressLines, List<GenericTypedGrPostal> firmNames,
            List<Department> departments, MailStop mailStop, PostalCode postalCode, String type){
        this.addressLines = addressLines;
        this.firmNames = firmNames;
        this.departments = departments;
        this.mailStop = mailStop;
        this.postalCode = postalCode;
        this.type = type;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<GenericTypedGrPostal> getAddressLines() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<GenericTypedGrPostal> getFirmNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Department> getDepartments() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public MailStop getMailStop() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostalCode getPostalCode() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
