package org.geotoolkit.data.model.kml;

/**
 * <p>This interface maps SnippetType type.</p>
 *
 * <br />&lt;complexType name="SnippetType" final="#all">
 * <br />&lt;simpleContent>
 * <br />&lt;extension base="string">
 * <br />&lt;attribute name="maxLines" type="int" use="optional" default="2"/>
 * <br />&lt;/extension>
 * <br />&lt;/simpleContent>
 * <br />&lt;/complexType>
 *
 * @author Samuel Andrés
 */
public interface Snippet {

    /**
     *
     * @return
     */
    public int getMaxLines();

    /**
     * 
     * @return
     */
    public String getContent();

}
