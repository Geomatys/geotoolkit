package org.geotoolkit.data.model.atom;

/**
 *
 * @author Samuel Andrés
 */
public class AtomLinkDefault implements AtomLink{

    private String href;
    private String rel;
    private String type;
    private String hreflang;
    private String title;
    private String length;

    public AtomLinkDefault(String href, String rel, String type, String hreflang, String title, String length){
        this.href = href;
        this.rel = rel;
        this.type = type;
        this.hreflang = hreflang;
        this.title = title;
        this.length = length;
    }
            

    @Override
    public String getHref() {return this.href;}

    @Override
    public String getRel() {return this.rel;}

    @Override
    public String getType() {return this.type;}

    @Override
    public String getHreflang() {return this.hreflang;}

    @Override
    public String getTitle() {return this.title;}

    @Override
    public String getLength() {return this.length;}

}
