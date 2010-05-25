package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public class IdAttributesDefault implements IdAttributes{

    private String id;
    private String targetId;

    public IdAttributesDefault(String id, String targetId){
        this.id = id;
        this.targetId = targetId;
    }

    @Override
    public String getId(){return this.id;}
    
    @Override
    public String getTargetId(){return this.targetId;}
}
