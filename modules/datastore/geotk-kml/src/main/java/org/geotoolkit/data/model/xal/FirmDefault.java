package org.geotoolkit.data.model.xal;

import java.util.List;
import static java.util.Collections.*;

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
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.firmNames = (firmNames == null) ? EMPTY_LIST : firmNames;
        this.departments = (departments == null) ? EMPTY_LIST : departments;
        this.mailStop = mailStop;
        this.postalCode = postalCode;
        this.type = type;
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
    public List<GenericTypedGrPostal> getFirmNames() {return this.firmNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public List<Department> getDepartments() {return this.departments;}

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
    public String getType() {return this.type;}

}
