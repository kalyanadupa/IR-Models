package common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import model.Document;



/**
 * Provides static methods used by various modules.
 * 
 * @author Eric Hildebrand
 * 
 *         Part of the Information Retrieval 2012 Basic IR models project.
 */
public class Helpers {
    
    /**
     * Stems a given word.
     * 
     * @param term
     *            A word.
     * @return The stemmed form of the word.
     */
    public static String stem( String term ) {
        Stemmer stemmer = new Stemmer( );
        char[] letters = term.toLowerCase( ).toCharArray( );
        for ( char letter : letters ) {
            stemmer.add( letter );
        }
        stemmer.stem( );
        return stemmer.toString( );
    }
    
    /**
     * Reads and returns the text contained in a given file.
     * 
     * @param path
     *            The path to a text file.
     * @return The text contained in the file.
     */
    public static String readFile( String path ) {
        StringBuilder res = new StringBuilder( );
        try {
            BufferedReader in = new BufferedReader( new FileReader( path ) );
            String line = null;
            while ( ( line = in.readLine( ) ) != null ) {
                res.append( new String( line.getBytes( ), "UTF-8" ) + "\n" );
            }
            in.close( );
        } catch ( IOException e ) {
            e.printStackTrace( );
        }
        return res.toString( );
    }
    
    /**
     * Updates the weight of a given document for a given term.
     * 
     * @param document
     *            The document whose weight should be updated.
     * @param term
     *            The term whose weight should be updated.
     * @param documents
     *            All documents relevant for updating the term weight.
     */
    public static void setTfIdfWeight( Document document, String term, Document[] documents ) {
        double tf = document.getMaxFreq( ) == 0 ? 0.0 : ( double ) document.getFrequency( term ) / document.getMaxFreq( );
        if ( Double.isNaN( tf ) ) {
            tf = 0.0;
        }
        int n = 0;
        for ( Document doc : documents ) {
            if ( doc.getFrequency( term ) > 0 ) {
                n++;
            }
        }
        double idf = Math.log( ( double ) documents.length / n ) / Math.log( 2 );
        if ( Double.isNaN( idf ) ) {
            idf = 0.0;
        }
        document.setWeight( term, tf * idf );
    }
    
}
