package org.geotoolkit.data.model.kml;

import java.util.List;

/**
 *
 * @author Samuel Andrés
 */
public class CoordinatesDefault implements Coordinates{

    private List<Coordinate> coordinates;

    public CoordinatesDefault(List<Coordinate> coordinates){
        this.coordinates = coordinates;
    }

    @Override
    public List<Coordinate> getCoordinates() {return this.coordinates;}

    /**
     *
     * @param i the index of the Coordinate to return.
     * @return The Coordinate at the index i.
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
     * Get the XML schema list of coordinates.
     * @return The String of coordinates values formated as a XML schema list.
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
