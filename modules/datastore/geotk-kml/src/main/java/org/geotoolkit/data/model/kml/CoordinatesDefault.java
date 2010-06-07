package org.geotoolkit.data.model.kml;

import java.util.List;
import static java.util.Collections.*;

/**
 *
 * @author Samuel Andrés
 */
public class CoordinatesDefault implements Coordinates{

    private final List<Coordinate> coordinates;

    /**
     * 
     * @param coordinates
     */
    public CoordinatesDefault(List<Coordinate> coordinates){
        this.coordinates = (coordinates == null) ? EMPTY_LIST : coordinates;
    }

    @Override
    public List<Coordinate> getCoordinates() {return this.coordinates;}

    /**
     *
     * @{@inheritDoc }
     */
    @Override
    public Coordinate getCoordinate(int i) {
        Coordinate resultat = null;
        if (i < this.coordinates.size()){
            resultat = this.coordinates.get(i);
        }
        return resultat;
    }

    /**
     * 
     * @{@inheritDoc }
     */
    @Override
    public String getCoordinatesString(){
        String resultat = "";
        int position = 0;
        for(Coordinate coordinate : this.coordinates){
            position += 1;
            if(position == this.coordinates.size()){
                resultat += coordinate.getCoordinateString();
            } else {
                resultat += coordinate.getCoordinateString()+" ";
            }
        }
        return resultat;
    }

    @Override
    public String toString(){
        String resultat = "Coordinates : ";
        for(Coordinate c : this.coordinates){
            resultat += "\n\t"+c.toString();
        }
        return resultat;
    }

}
