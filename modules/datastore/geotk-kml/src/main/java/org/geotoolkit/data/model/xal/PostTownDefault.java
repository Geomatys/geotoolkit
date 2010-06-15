package org.geotoolkit.data.model.xal;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class PostTownDefault implements PostTown{

    private final List<GenericTypedGrPostal> addressLines;
    private final List<GenericTypedGrPostal> postTownNames;
    private final PostTownSuffix postTownSuffix;
    private final String type;

    /**
     *
     * @param addressLines
     * @param postTownNames
     * @param postTownSuffix
     * @param type
     */
    public PostTownDefault(List<GenericTypedGrPostal> addressLines,
            List<GenericTypedGrPostal> postTownNames, PostTownSuffix postTownSuffix, String type){
        this.addressLines = (addressLines == null) ? EMPTY_LIST : addressLines;
        this.postTownNames = (postTownNames == null) ? EMPTY_LIST : postTownNames;
        this.postTownSuffix = postTownSuffix;
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
    public List<GenericTypedGrPostal> getPostTownNames() {return this.postTownNames;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public PostTownSuffix getPostTownSuffix(){return this.postTownSuffix;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getType() {return this.type;}

}
