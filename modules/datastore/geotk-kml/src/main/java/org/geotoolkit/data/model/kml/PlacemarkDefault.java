package org.geotoolkit.data.model.kml;

import java.util.List;
import org.geotoolkit.data.model.atom.AtomPersonConstruct;
import org.geotoolkit.data.model.atom.AtomLink;
import org.geotoolkit.data.model.xal.AddressDetails;
import org.geotoolkit.data.model.xsd.SimpleType;

/**
 *
 * @author Samuel Andrés
 */
public class PlacemarkDefault extends AbstractFeatureDefault implements Placemark {

    private AbstractGeometry abstractGeometry;
    private List<SimpleType> placemarkSimpleExtension;
    private List<AbstractObject> placemarkObjectExtension;


    public PlacemarkDefault(List<SimpleType> objectSimpleExtensions,
            IdAttributes idAttributes,
            String name,
            boolean visibility,
            boolean open,
            AtomPersonConstruct author,
            AtomLink link,
            String address,
            AddressDetails addressDetails,
            String phoneNumber, String snippet,
            String description, AbstractView view,
            AbstractTimePrimitive timePrimitive,
            String styleUrl, List<AbstractStyleSelector> styleSelector,
            Region region, ExtendedData extendedData,
            List<SimpleType> abstractFeatureSimpleExtensions,
            List<AbstractObject> abstractFeatureObjectExtensions,
            AbstractGeometry abstractGeometry,
            List<SimpleType> placemarkSimpleExtensions,
            List<AbstractObject> placemarkObjectExtension){

        super(objectSimpleExtensions, idAttributes,
            name, visibility, open, author, link, address, addressDetails,
            phoneNumber, snippet, description, view, timePrimitive,
            styleUrl, styleSelector, region, extendedData,
            abstractFeatureSimpleExtensions,
            abstractFeatureObjectExtensions);

        this.abstractGeometry = abstractGeometry;
        this.placemarkSimpleExtension = placemarkSimpleExtensions;
        this.placemarkObjectExtension = placemarkObjectExtension;
    }


    @Override
    public AbstractGeometry getAbstractGeometry() {
        return this.abstractGeometry;
    }

    @Override
    public List<SimpleType> getPlacemarkSimpleExtensions() {
        return this.placemarkSimpleExtension;
    }

    @Override
    public List<AbstractObject> getPlacemarkObjectExtensions() {
        return this.placemarkObjectExtension;
    }

    @Override
    public String toString(){
        String resultat = super.toString();
        resultat += "Placemark : ";
        //resultat += "\n\t"+abstractGeometry;
        return resultat;
    }
}
