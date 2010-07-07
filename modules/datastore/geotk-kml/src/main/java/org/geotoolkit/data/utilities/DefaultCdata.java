package org.geotoolkit.data.utilities;

/**
 *
 * @author Samuel Andrés
 */
public class DefaultCdata implements Cdata{

    private String CDATA;

    public DefaultCdata(String cdata){
        this.CDATA = cdata;
    }

    @Override
    public String toString(){
        return this.CDATA;
    }

    @Override
    public boolean equals(Object object){
        if (object instanceof Cdata)
            return CDATA.equals(((Cdata) object).toString());
        else
            return false;
    }
}
