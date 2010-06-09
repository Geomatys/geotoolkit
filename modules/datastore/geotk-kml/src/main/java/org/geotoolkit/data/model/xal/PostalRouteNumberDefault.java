package org.geotoolkit.data.model.xal;

/**
 *
 * @author Samuel Andrés
 */
public class PostalRouteNumberDefault implements PostalRouteNumber {

    private final GrPostal grPostal;
    private final String content;

    /**
     * 
     * @param grPostal
     * @param content
     */
    public PostalRouteNumberDefault(GrPostal grPostal, String content){
        this.grPostal = grPostal;
        this.content = content;
    }

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public String getContent() {return this.content;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public GrPostal getGrPostal() {return this.grPostal;}

}
