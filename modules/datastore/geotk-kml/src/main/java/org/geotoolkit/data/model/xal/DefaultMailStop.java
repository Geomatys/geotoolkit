package org.geotoolkit.data.model.xal;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultMailStop implements MailStop {

    private final List<GenericTypedGrPostal> addressLines;
    private final List<GenericTypedGrPostal> mailStopNames;
    private final MailStopNumber mailStopNumber;
    private final String type;

    /**
     *
     * @param addressLines
     * @param mailStopNames
     * @param mailStopNumber
     * @param type
     */
    public DefaultMailStop(List<GenericTypedGrPostal> addressLines, List<GenericTypedGrPostal> mailStopNames,
            MailStopNumber mailStopNumber, String type){
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.mailStopNames = (mailStopNames == null) ? EMPTY_LIST : mailStopNames;
        this.mailStopNumber = mailStopNumber;
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
    public List<GenericTypedGrPostal> getMailStopNames() {return this.mailStopNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public MailStopNumber getMailStopNumber() {return this.mailStopNumber;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getType() {return this.type;}

}
