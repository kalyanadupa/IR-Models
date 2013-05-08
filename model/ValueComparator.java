package model;

import java.util.Comparator;
import java.util.Map;



/**
 * From http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
 * 
 * Part of the Information Retrieval 2012 Basic IR models project.
 */
class ValueComparator implements Comparator<Document> {
    
    Map<Document,Double> fBase;
    
    public ValueComparator( Map<Document,Double> base ) {
        fBase = base;
    }
    
    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare( Document a, Document b ) {
        if ( fBase.get( a ) >= fBase.get( b ) ) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}