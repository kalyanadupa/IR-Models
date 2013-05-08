package model;

import java.util.HashMap;
import java.util.Map;

import common.Helpers;



/**
 * A representation for a document. 
 * 
 * Term "vectors" are realized with mappings from terms to frequencies or weights,
 * respectively. Missing terms are considered to have frequency and weight 0.
 * Frequencies are abolute, weights could be relative term frequencies, 
 * tf/idf or any other weighting functions.
 * 
 * @author Eric Hildebrand
 * 
 *         Part of the Information Retrieval 2012 Basic IR models project.
 */
public class Document {
    
    private int fLength = 0; // Number of terms
    private int fMaxFreq = 0;
    private String fName = null;
    private Map<String,Integer> fFreqs = null; // The term frequency "vector"
    private Map<String,Double> fWeights = null; // The term weight "vector"
    
    protected Document( ) {
        throw new UnsupportedOperationException( );
    }
    
    /**
     * Creates a new document.
     * 
     * @param name
     *            An identifier for the document.
     * @param text
     *            The text included in the document.
     * @param stem
     *            Optionally stems terms.
     */
    public Document( String name, String text, boolean stem ) {
        fName = name;
        String[] terms = text.replaceAll( "[^\\w\\d\\s]+", " " ).trim( ).toLowerCase( ).split( "\\s+" ); // Preprocessing
        if ( stem ) {
            for ( int i = 0; i < terms.length; i++ ) {
                terms[i] = Helpers.stem( terms[i] );
            }
        }
        fFreqs = new HashMap<String,Integer>( );
        for ( String term : terms ) {
            fLength++;
            if ( !fFreqs.containsKey( term ) ) {
                fFreqs.put( term, 0 ); // Init term frequency
            }
            int freq = fFreqs.get( term ) + 1;
            fFreqs.put( term, freq ); // Update term frequency
            if ( freq > fMaxFreq ) {
                fMaxFreq = freq; // Update maximum frequency
            }
        }
    }
    
    public int getLength( ) {
        return fLength;
    }
    
    public String getName( ) {
        return fName;
    }
    
    public String[] getTerms( ) {
        return fFreqs.keySet( ).toArray( new String[fFreqs.keySet( ).size( )] );
    }
    
    public int getMaxFreq( ) {
        return fMaxFreq;
    }
    
    /**
     * Returns the (absolute) frequency for a given term in the document.
     * If the term is not present in the frequency mapping, 0 is returned.
     * 
     * @param term
     *            A term
     * @return The frequency of the term in the document of 0, if not present.
     */
    public int getFrequency( String term ) {
        if ( term != null && fFreqs != null && fFreqs.containsKey( term ) ) {
            return fFreqs.get( term );
        }
        return 0;
    }
    
    /**
     * Updates the weight for a given term
     * 
     * @param term
     *            A term.
     * @param weight
     *            The new value for the term weight.
     */
    public void setWeight( String term, double weight ) {
        if ( fWeights == null ) {
            fWeights = new HashMap<String,Double>( );
        }
        fWeights.put( term, weight );
    }
    
    /**
     * Returns the weight for a given term in the document. If the term is
     * not present in the weight mapping, 0 is returned.
     * 
     * @param term
     *            A term
     * @return The weight of the term in the document of 0, if not present.
     */
    public Double getWeight( String term ) {
        if ( term != null && fWeights != null && fWeights.containsKey( term ) ) {
            return fWeights.get( term );
        }
        return 0.0;
    }
    
    @Override
    public boolean equals( Object o ) {
        if ( o instanceof Document ) {
            Document document = ( Document ) o;
            return fName.equals( document );
        }
        return false;
    }
    
    @Override
    public int hashCode( ) {
        return fName.hashCode( );
    }
}
