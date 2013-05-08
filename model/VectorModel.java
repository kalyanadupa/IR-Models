package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import common.Helpers;



/**
 * An implementation of the vector space IR model.
 * 
 * @author Eric Hildebrand
 * 
 *         Part of the Information Retrieval 2012 Basic IR models project.
 */
public class VectorModel extends AbstractModel {
    
    private boolean fTfidf = false;
    
    protected VectorModel( ) {
        throw new UnsupportedOperationException( );
    }
    
    protected VectorModel( Document[] documents ) {
        throw new UnsupportedOperationException( );
    }
    
    /**
     * Creates a new vector space model.
     * 
     * @param documents
     *            The base corpus.
     * @param tfidf
     *            Optionally uses tf/idf weights instead of simple term
     *            frequencies.
     */
    public VectorModel( Document[] documents, boolean tfidf ) {
        fTfidf = tfidf;
        fDocuments = documents;
        if ( fTfidf ) {
            for ( Document document : fDocuments ) {
                for ( String term : document.getTerms( ) ) {
                    Helpers.setTfIdfWeight( document, term, documents );
                }
            }
        }
    }
    
    /**
     * Calculates the cosine similarity between two documents.
     * 
     * @param document
     *            A document.
     * @param query
     *            Another document.
     * @return The cosine similarity of the two document vectors.
     */
    private double getCosineSimilarity( Document document, Document query ) {
        Set<String> terms = new HashSet<String>( );
        for ( String term : document.getTerms( ) ) {
            terms.add( term );
        }
        for ( String term : query.getTerms( ) ) {
            terms.add( term );
        }
        double numerator = 0.0;
        double denominator1 = 0.0;
        double denominator2 = 0.0;
        for ( String term : terms ) {
            if ( fTfidf ) {
                numerator += document.getWeight( term ) * query.getWeight( term );
                denominator1 += document.getWeight( term ) * document.getWeight( term );
                denominator2 += query.getWeight( term ) * query.getWeight( term );
            } else {
                numerator += document.getFrequency( term ) * query.getFrequency( term );
                denominator1 += document.getFrequency( term ) * document.getFrequency( term );
                denominator2 += query.getFrequency( term ) * query.getFrequency( term );
            }
        }
        if ( denominator1 == 0 || denominator2 == 0 ) {
            return 0;
        }
        return numerator / ( Math.sqrt( denominator1 ) * Math.sqrt( denominator2 ) );
    }
    
    @Override
    public SortedMap<Document,Double> getDocuments( String queryString, boolean stem ) {
        
        Document query = new Document( "query", queryString, stem );
        if ( fTfidf ) {
            Document[] documents = new Document[fDocuments.length + 1];
            System.arraycopy( fDocuments, 0, documents, 0, fDocuments.length );
            documents[documents.length - 1] = query;
            for ( String term : query.getTerms( ) ) {
                Helpers.setTfIdfWeight( query, term, documents );
            }
        }
        Map<Document,Double> map = new HashMap<Document,Double>( );
        SortedMap<Document,Double> result = new TreeMap<Document,Double>( new ValueComparator( map ) );
        
        for ( Document document : fDocuments ) {
            map.put( document, getCosineSimilarity( document, query ) );
        }
        result.putAll( map );
        return result;
    }
    
}
