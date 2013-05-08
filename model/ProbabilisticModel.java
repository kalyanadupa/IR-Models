package model;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;



/**
 * An implementation of the Okapi BM25 IR model.
 * 
 * @author Eric Hildebrand
 * 
 *         Part of the Information Retrieval 2012 Basic IR models project.
 */
public class ProbabilisticModel extends AbstractModel {
    
    private double fAvgDocLen = 0.0;
    
    public ProbabilisticModel( Document[] documents ) {
        super( documents );
        for ( Document document : fDocuments ) {
            fAvgDocLen += document.getLength( );
        }
        fAvgDocLen /= fDocuments.length;
        
    }
    
    @Override
    public SortedMap<Document,Double> getDocuments( String queryString, boolean stem ) {
        return getDocuments( queryString, 1.2, 0.75, stem );
    }
    
    public SortedMap<Document,Double> getDocuments( String queryString, double k, double b, boolean stem ) {
        
        Document query = new Document( "query", queryString, stem );
        Map<Document,Double> map = new HashMap<Document,Double>( );
        SortedMap<Document,Double> result = new TreeMap<Document,Double>( new ValueComparator( map ) );
        
        for ( Document document : fDocuments ) {
            double score = 0.0;
            for ( String term : query.getTerms( ) ) {
                int n = 0;
                for ( Document doc : fDocuments ) {
                    if ( doc.getFrequency( term ) > 0 ) {
                        n++;
                    }
                }
                double idf = Math.log( ( fDocuments.length - n + 0.5 ) / ( n + 0.5 ) ) / Math.log( 2 );
                if ( idf < 0 ) {
                    idf = 0.05;
                }
                double numerator = idf * document.getFrequency( term ) * ( k + 1 );
                double denominator = document.getFrequency( term ) + k * ( 1 - b + b * ( document.getLength( ) / fAvgDocLen ) );
                score += numerator / denominator;
            }
            map.put( document, score );
        }
        result.putAll( map );
        return result;
    }
    
}
