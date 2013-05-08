package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import common.Helpers;



/**
 * A representation of a Disjunctive Normal Form.
 * 
 * @author Eric Hildebrand
 * 
 *         Part of the Information Retrieval 2012 Basic IR models project.
 */
public class DisjunctiveNormalForm implements Iterable<Map<String,Boolean>> {
    
    private Set<Map<String,Boolean>> fDisjunctiveTerms = null; // { {A:true,B:false,C:true}, {A:false,B:true,C:true}, ... }
    
    protected DisjunctiveNormalForm( ) {
        throw new UnsupportedOperationException( );
    }
    
    /**
     * Creates a dnf model from a formula. The formula is supposed to be already
     * in a dnf format. Negations are marked with -
     * 
     * @param formula
     *            e.g. ( A & -B & C ) | ( -A & B & -C )
     * @param stem
     *            Optionally stems terms.
     */
    public DisjunctiveNormalForm( String formula, boolean stem ) {
        fDisjunctiveTerms = new HashSet<Map<String,Boolean>>( );
        for ( String conjunction : formula.split( "[^\\-\\w\\d]*\\|[^\\-\\w\\d]*" ) ) {
            Map<String,Boolean> terms = new HashMap<String,Boolean>( );
            boolean contradiction = false;
            for ( String term : conjunction.replaceAll( "[\\(\\)]", " " ).trim( ).toLowerCase( ).split( "[^\\-\\w\\d]*&[^\\-\\w\\d]*" ) ) {
                if ( term.startsWith( "-" ) ) {
                    term = term.substring( 1 );
                    if ( stem ) {
                        term = Helpers.stem( term );
                    }
                    if ( terms.containsKey( term ) && terms.get( term ) ) {
                        contradiction = true;
                        break;
                    }
                    terms.put( term, false );
                } else {
                    if ( stem ) {
                        term = Helpers.stem( term );
                    }
                    if ( terms.containsKey( term ) && !terms.get( term ) ) {
                        contradiction = true;
                        break;
                    }
                    terms.put( term, true );
                }
            }
            if ( !contradiction ) {
                fDisjunctiveTerms.add( terms );
            }
        }
    }
    
    @Override
    public Iterator<Map<String,Boolean>> iterator( ) {
        return fDisjunctiveTerms.iterator( );
    }
    
    @Override
    public String toString( ) {
        String result = "";
        for ( Map<String,Boolean> conjunction : fDisjunctiveTerms ) {
            result += "(";
            for ( String term : conjunction.keySet( ) ) {
                result += " ";
                if ( !conjunction.get( term ) ) {
                    result += "-";
                }
                result += term;
            }
            result += " ) ";
        }
        return result;
    }
    
}
