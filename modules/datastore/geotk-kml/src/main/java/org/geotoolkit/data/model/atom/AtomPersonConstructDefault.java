package org.geotoolkit.data.model.atom;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class AtomPersonConstructDefault implements AtomPersonConstruct{

    private List<String> names;
    private List<String> uris;
    private List<String> emails;

    public AtomPersonConstructDefault(List<String> names, List<String> uris, List<String> emails){
        this.names = names;
        this.uris = uris;
        this.emails = emails;
    }

    @Override
    public List<String> getNames() {return this.names;}

    @Override
    public List<String> getUris() {return this.uris;}

    @Override
    public List<String> getEmails() {return this.emails;}

}
