package org.geotoolkit.data.kml.model;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultIdAttributes implements IdAttributes{

    private final String id;
    private final String targetId;

    /**
     *
     * @param id
     * @param targetId
     */
    public DefaultIdAttributes(String id, String targetId){
        this.id = id;
        this.targetId = targetId;
    }

    /**
     *
     * @{@inheritDoc}
     */
    @Override
    public String getId(){return this.id;}

    /**
     *
     * @{@inheritDoc}
     */
    @Override
    public String getTargetId(){return this.targetId;}
}
