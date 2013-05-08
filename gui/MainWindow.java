package gui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import model.BooleanModel;
import model.Document;
import model.ProbabilisticModel;
import model.VectorModel;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import common.Helpers;



/**
 * Handles the graphical user interface.
 * 
 * @author Eric Hildebrand
 * 
 *         Part of the Information Retrieval 2012 Basic IR models project.
 */
public class MainWindow extends ApplicationWindow {
    
    private Table fTblModels = null;
    private Text fTxtCorpus = null;
    private Text fTxtQuery = null;
    
    private Document[] fDocuments = null;
    
    private List<ResultsLine> results = null;
    private BooleanModel fBooleanModel = null;
    private VectorModel fVectorModel = null;
    private ProbabilisticModel fProbabilisticModel = null;
    private TableViewer fTvwModels;
    private Button fBtnStem;
    private Button fBtnTfidf;
    private Text fTxtK;
    private Text fTxtB;
    
    /**
     * Create the application window.
     */
    public MainWindow( ) {
        super( null );
    }
    
    /**
     * Create contents of the application window.
     * 
     * @param parent
     */
    @Override
    protected Control createContents( Composite parent ) {
        Composite container = new Composite( parent, SWT.NONE );
        container.setLayout( new GridLayout( 8, false ) );
        
        Label lblCorpus = new Label( container, SWT.NONE );
        lblCorpus.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
        lblCorpus.setText( "Corpus" );
        
        fTxtCorpus = new Text( container, SWT.BORDER );
        fTxtCorpus.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 6, 1 ) );
        
        Button btnBrowse = new Button( container, SWT.NONE );
        btnBrowse.addSelectionListener( new SelectionAdapter( ) {
            
            @Override
            public void widgetSelected( SelectionEvent e ) {
                DirectoryDialog dirDlg = new DirectoryDialog( getShell( ) );
                String dir = dirDlg.open( );
                if ( dir != null ) {
                    fTxtCorpus.setText( dir );
                    Collection<Document> documents = new LinkedList<Document>( );
                    for ( File file : new File( dir ).listFiles( ) ) {
                        if ( file.isFile( ) ) {
                            documents.add( new Document( file.getName( ), Helpers.readFile( file.getAbsolutePath( ) ), fBtnStem.getSelection( ) ) );
                        }
                    }
                    fDocuments = documents.toArray( new Document[documents.size( )] );
                    fBooleanModel = new BooleanModel( fDocuments );
                    updateVectorModel( );
                    fProbabilisticModel = new ProbabilisticModel( fDocuments );
                }
            }
        } );
        btnBrowse.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false, 1, 1 ) );
        btnBrowse.setText( "&Browse..." );
        
        Label lblQuery = new Label( container, SWT.NONE );
        lblQuery.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
        lblQuery.setText( "Query" );
        
        fTxtQuery = new Text( container, SWT.BORDER );
        fTxtQuery.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, true, false, 6, 1 ) );
        
        Button btnSearch = new Button( container, SWT.NONE );
        btnSearch.addSelectionListener( new SelectionAdapter( ) {
            
            @SuppressWarnings("unchecked")
            @Override
            public void widgetSelected( SelectionEvent e ) {
                if ( fBooleanModel == null ) {
                    MessageDialog.openError( getShell( ), "No corpus selected", "You have to select a corpus first." );
                    return;
                }
                if ( fTxtQuery.getText( ).trim( ).length( ) == 0 ) {
                    MessageDialog.openError( getShell( ), "Empty query", "Your query does not contain any text." );
                    return;
                }
                
                // Retrieve results
                results = new ArrayList<ResultsLine>( );
                
                SortedMap<Document,Double> booleanResult = fBooleanModel.getDocuments( fTxtQuery.getText( ), fBtnStem.getSelection( ) );
                SortedMap<Document,Double> vectorResult = fVectorModel.getDocuments( fTxtQuery.getText( ), fBtnStem.getSelection( ) );
                SortedMap<Document,Double> probabilisticResult = fProbabilisticModel.getDocuments( fTxtQuery.getText( ), Double.valueOf( fTxtK.getText( ) ), Double.valueOf( fTxtB.getText( ) ), fBtnStem.getSelection( ) );
                
                Object[] booleanDocuments = booleanResult.entrySet( ).toArray( );
                Object[] vectorDocuments = vectorResult.entrySet( ).toArray( );
                Object[] probabilisticDocuments = probabilisticResult.entrySet( ).toArray( );
                for ( int i = 0; i < fDocuments.length; i++ ) {
                    results.add( new ResultsLine( ( ( Entry<Document,Double> ) booleanDocuments[i] ).getKey( ), ( ( Entry<Document,Double> ) booleanDocuments[i] ).getValue( ), ( ( Entry<Document,Double> ) vectorDocuments[i] ).getKey( ), ( ( Entry<Document,Double> ) vectorDocuments[i] ).getValue( ), ( ( Entry<Document,Double> ) probabilisticDocuments[i] ).getKey( ), ( ( Entry<Document,Double> ) probabilisticDocuments[i] ).getValue( ) ) );
                }
                fTvwModels.setInput( results );
            }
        } );
        btnSearch.setLayoutData( new GridData( SWT.FILL, SWT.CENTER, false, false, 1, 1 ) );
        btnSearch.setText( "Search" );
        
        fBtnStem = new Button( container, SWT.CHECK );
        fBtnStem.addSelectionListener( new SelectionAdapter( ) {
            
            @Override
            public void widgetSelected( SelectionEvent e ) {
                Collection<Document> documents = new LinkedList<Document>( );
                for ( File file : new File( fTxtCorpus.getText( ) ).listFiles( ) ) {
                    if ( file.isFile( ) ) {
                        documents.add( new Document( file.getName( ), Helpers.readFile( file.getAbsolutePath( ) ), fBtnStem.getSelection( ) ) );
                    }
                }
                fDocuments = documents.toArray( new Document[documents.size( )] );
                fBooleanModel = new BooleanModel( fDocuments );
                updateVectorModel( );
                fProbabilisticModel = new ProbabilisticModel( fDocuments );
            }
        } );
        fBtnStem.setLayoutData( new GridData( SWT.LEFT, SWT.CENTER, false, false, 2, 1 ) );
        fBtnStem.setToolTipText( "Use porter stemmer algorithm for documents and queries" );
        fBtnStem.setSelection( true );
        fBtnStem.setText( "&Stemmer" );
        
        fBtnTfidf = new Button( container, SWT.CHECK );
        fBtnTfidf.addSelectionListener( new SelectionAdapter( ) {
            
            @Override
            public void widgetSelected( SelectionEvent e ) {
                updateVectorModel( );
            }
        } );
        fBtnTfidf.setSelection( true );
        fBtnTfidf.setText( "&Tf-idf" );
        
        Label lblK = new Label( container, SWT.NONE );
        lblK.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
        lblK.setText( "k:" );
        
        fTxtK = new Text( container, SWT.BORDER );
        GridData gd_txtK = new GridData( SWT.LEFT, SWT.CENTER, false, false, 1, 1 );
        gd_txtK.widthHint = 30;
        fTxtK.setLayoutData( gd_txtK );
        fTxtK.setText( "1.2" );
        
        Label lblB = new Label( container, SWT.NONE );
        lblB.setLayoutData( new GridData( SWT.RIGHT, SWT.CENTER, false, false, 1, 1 ) );
        lblB.setText( "b:" );
        
        fTxtB = new Text( container, SWT.BORDER );
        GridData gd_txtB = new GridData( SWT.LEFT, SWT.CENTER, false, false, 1, 1 );
        gd_txtB.widthHint = 30;
        fTxtB.setLayoutData( gd_txtB );
        fTxtB.setText( "0.75" );
        new Label( container, SWT.NONE );
        
        fTvwModels = new TableViewer( container, SWT.BORDER | SWT.FULL_SELECTION );
        fTvwModels.setContentProvider( new ArrayContentProvider( ) );
        fTblModels = fTvwModels.getTable( );
        fTblModels.setHeaderVisible( true );
        fTblModels.setLinesVisible( true );
        fTblModels.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 8, 8 ) );
        
        TableViewerColumn tvcBoolMod = new TableViewerColumn( fTvwModels, SWT.CENTER );
        tvcBoolMod.setLabelProvider( new ColumnLabelProvider( ) {
            
            @Override
            public String getText( Object element ) {
                if ( element instanceof ResultsLine ) {
                    Document boolDoc = ( ( ResultsLine ) element ).getBoolDoc( );
                    return boolDoc == null ? null : boolDoc.getName( ) + ": " + ( ( ResultsLine ) element ).getBoolSim( );
                }
                return null;
            }
        } );
        TableColumn tclBoolMod = tvcBoolMod.getColumn( );
        tclBoolMod.setResizable( false );
        tclBoolMod.setWidth( 239 );
        tclBoolMod.setText( "Boolean Model" );
        
        TableViewerColumn tvcVectMod = new TableViewerColumn( fTvwModels, SWT.CENTER );
        tvcVectMod.setLabelProvider( new ColumnLabelProvider( ) {
            
            @Override
            public String getText( Object element ) {
                if ( element instanceof ResultsLine ) {
                    Document vectDoc = ( ( ResultsLine ) element ).getVectDoc( );
                    return vectDoc == null ? null : vectDoc.getName( ) + ": " + ( ( ResultsLine ) element ).getVectSim( );
                }
                return null;
            }
        } );
        TableColumn tclVectMod = tvcVectMod.getColumn( );
        tclVectMod.setResizable( false );
        tclVectMod.setWidth( 242 );
        tclVectMod.setText( "Vector Model" );
        
        TableViewerColumn tvcProbMod = new TableViewerColumn( fTvwModels, SWT.CENTER );
        tvcProbMod.setLabelProvider( new ColumnLabelProvider( ) {
            
            @Override
            public String getText( Object element ) {
                if ( element instanceof ResultsLine ) {
                    Document probDoc = ( ( ResultsLine ) element ).getProbDoc( );
                    return probDoc == null ? null : probDoc.getName( ) + ": " + ( ( ResultsLine ) element ).getProbSim( );
                }
                return null;
            }
        } );
        TableColumn tclProbMod = tvcProbMod.getColumn( );
        tclProbMod.setResizable( false );
        tclProbMod.setWidth( 266 );
        tclProbMod.setText( "Probabilistic Model (BM25)" );
        
        return container;
    }
    
    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main( String args[] ) {
        try {
            MainWindow window = new MainWindow( );
            window.setBlockOnOpen( true );
            window.open( );
            Display.getCurrent( ).dispose( );
        } catch ( Exception e ) {
            e.printStackTrace( );
        }
    }
    
    /**
     * Configure the shell.
     * 
     * @param newShell
     */
    @Override
    protected void configureShell( Shell newShell ) {
        super.configureShell( newShell );
        newShell.setText( "IR Models" );
    }
    
    /**
     * Return the initial size of the window.
     */
    @Override
    protected Point getInitialSize( ) {
        return new Point( 800, 600 );
    }
    
    private void updateVectorModel( ) {
        if ( fDocuments != null && fDocuments.length > 0 ) {
            fVectorModel = new VectorModel( fDocuments, fBtnTfidf.getSelection( ) );
        }
    }
}
