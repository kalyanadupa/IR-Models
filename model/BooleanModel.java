package model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;



/**
 * A simple implementation of the boolean IR model.
 * 
 * @author Eric Hildebrand
 * 
 *         Part of the Information Retrieval 2012 Basic IR models project.
 */
public class BooleanModel extends AbstractModel {
    
    public BooleanModel( Document[] documents ) {
        super( documents );
    }
    
    @Override
    public SortedMap<Document,Double> getDocuments( String queryString, boolean stem ) {
        
        DisjunctiveNormalForm dnf = new DisjunctiveNormalForm( queryString, stem );
        HashMap<Document,Double> map = new HashMap<Document,Double>( );
        Comparator<Document> comparator = new ValueComparator( map );
        SortedMap<Document,Double> result = new TreeMap<Document,Double>( comparator );
        
        // Initialize all documents with similarity of 0
        // For each document, check if any conjunction of the query's dnf is satisfied
        // If so, update the documents similarity to 1
        for ( Document document : fDocuments ) {
            map.put( document, 0.0 );
            for ( Map<String,Boolean> conjunction : dnf ) {
                boolean match = true;
                for ( String term : conjunction.keySet( ) ) {
                    if ( ( conjunction.get( term ) && document.getFrequency( term ) == 0 ) || ( !conjunction.get( term ) && document.getFrequency( term ) > 0 ) ) {
                        match = false;
                        break; // Leave term loop
                    }
                }
                if ( match ) { // Conjunction satisfied
                    map.put( document, 1.0 );
                    break; // Leave conjunction loop
                }
            }
        }
        result.putAll( map );
        return result;
    }
    
}
