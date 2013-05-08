package model;

import java.util.SortedMap;



/**
 * An abstract super class for all IR models.
 * 
 * @author Eric Hildebrand
 * 
 *         Part of the Information Retrieval 2012 Basic IR models project.
 */
public abstract class AbstractModel {
    
    protected Document[] fDocuments = null;
    
    public AbstractModel( ) {
        super( );
    }
    
    public AbstractModel( Document[] documents ) {
        fDocuments = documents;
    }
    
    /**
     * Returns all documents for a query, ranked by their similarity. Must be
     * implemented by all inherited classes.
     * 
     * @param queryString
     *            The seach query.
     * @param stem
     *            Use stemming?
     * @return A mapping from documents to similarity values. The documents
     *         should be ranked by similarity using model.ValueComparator.
     */
    public abstract SortedMap<Document,Double> getDocuments( String queryString, boolean stem );
    
}
