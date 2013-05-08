package gui;

import model.Document;



/**
 * The input model for the results table of the GUI window. Provides values for
 * all IR models.
 * 
 * @author Eric hildebrand
 * 
 * Part of the Information Retrieval 2012 Basic IR models project.
 */
public class ResultsLine {
    
    private double fBoolSim = 0.0;
    private double fVectSim = 0.0;
    private double fProbSim = 0.0;
    
    private Document fBoolDoc = null;
    private Document fVectDoc = null;
    private Document fProbDoc = null;
    
    public ResultsLine( Document boolDoc, double boolSim, Document vectDoc, double vectSim, Document probDoc, double probSim ) {
        fBoolDoc = boolDoc;
        fBoolSim = boolSim;
        fVectDoc = vectDoc;
        fVectSim = vectSim;
        fProbDoc = probDoc;
        fProbSim = probSim;
    }
    
    public Document getBoolDoc( ) {
        return fBoolDoc;
    }
    
    public Document getVectDoc( ) {
        return fVectDoc;
    }
    
    public Document getProbDoc( ) {
        return fProbDoc;
    }
    
    public double getBoolSim( ) {
        return ( double ) Math.round( fBoolSim * 100000 ) / 100000;
    }
    
    public double getVectSim( ) {
        return ( double ) Math.round( fVectSim * 100000 ) / 100000;
        
    }
    
    public double getProbSim( ) {
        return ( double ) Math.round( fProbSim * 100000 ) / 100000;
    }
    
}
