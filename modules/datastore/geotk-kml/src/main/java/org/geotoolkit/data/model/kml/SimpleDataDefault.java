package org.geotoolkit.data.model.kml;

/**
 *
 * @author Samuel Andrés
 */
public class SimpleDataDefault implements SimpleData {

    private String name;
    private String content;

    public SimpleDataDefault(String name, String content){
        this.name = name;
        this.content = content;
    }

    @Override
    public String getName() {return this.name;}

    @Override
    public String getContent() {return this.content;}

}
