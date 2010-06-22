package org.geotoolkit.data.xal.model;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultPostalRouteNumber implements PostalRouteNumber {

    private final GrPostal grPostal;
    private final String content;

    /**
     * 
     * @param grPostal
     * @param content
     */
    public DefaultPostalRouteNumber(GrPostal grPostal, String content){
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
