package org.geotoolkit.data.xal.model;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPostBox implements PostBox {

    private final List<GenericTypedGrPostal> addressLines;
    private final PostBoxNumber postBoxNumber;
    private final PostBoxNumberPrefix postBoxNumberPrefix;
    private final PostBoxNumberSuffix postBoxNumberSuffix;
    private final PostBoxNumberExtension postBoxNumberExtension;
    private final Firm firm;
    private final PostalCode postalCode;
    private final String type;
    private final String indicator;

    /**
     *
     * @param addressLines
     * @param postBoxNumber
     * @param postBoxNumberPrefix
     * @param postBoxNumberSuffix
     * @param postBoxNumberExtension
     * @param firm
     * @param postalCode
     * @param type
     * @param indicator
     */
    public DefaultPostBox(List<GenericTypedGrPostal> addressLines, PostBoxNumber postBoxNumber,
            PostBoxNumberPrefix postBoxNumberPrefix, PostBoxNumberSuffix postBoxNumberSuffix,
            PostBoxNumberExtension postBoxNumberExtension, Firm firm,
            PostalCode postalCode, String type, String indicator){
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.postBoxNumber = postBoxNumber;
        this.postBoxNumberPrefix = postBoxNumberPrefix;
        this.postBoxNumberSuffix = postBoxNumberSuffix;
        this.postBoxNumberExtension = postBoxNumberExtension;
        this.firm = firm;
        this.postalCode = postalCode;
        this.type = type;
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
    public PostBoxNumber getPostBoxNumber() {return this.postBoxNumber;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBoxNumberPrefix getPostBoxNumberPrefix() {return this.postBoxNumberPrefix;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBoxNumberSuffix getPostBoxNumberSuffix() {return this.postBoxNumberSuffix;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostBoxNumberExtension getPostBoxNumberExtension() {return this.postBoxNumberExtension;}

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
    public String getIndicator() {return this.indicator;}

}
