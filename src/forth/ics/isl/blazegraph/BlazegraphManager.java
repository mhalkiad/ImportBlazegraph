/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forth.ics.isl.blazegraph;

import forth.ics.isl.utils.ResponseStatus;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.query.TupleQuery;

import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.eclipse.rdf4j.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.eclipse.rdf4j.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.eclipse.rdf4j.query.resultio.text.csv.SPARQLResultsCSVWriter;
import org.eclipse.rdf4j.query.resultio.text.tsv.SPARQLResultsTSVWriter;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;



/**
 *
 * @author mhalkiad
 */

public class BlazegraphManager {
    
    private Repository repo;

    
    public void openConnectionToBlazegraph(String sparqlEndPoint) {
        
        repo = new SPARQLRepository(sparqlEndPoint);
        repo.initialize();
        
    }
    
    
    public void closeConnectionToBlazeGraph() {
        
        repo.getConnection().close();

    }
    
    
    
    
    private ByteArrayOutputStream outputStreamData(TupleQuery tupleQuery, String dataFormat)
    {
        ByteArrayOutputStream out = null;
 
            if(TupleQueryResultFormat.CSV.getMIMETypes().contains(dataFormat))
            {
                out = new ByteArrayOutputStream();
                tupleQuery.evaluate(new SPARQLResultsCSVWriter(out));
            }
            else if(TupleQueryResultFormat.JSON.getMIMETypes().contains(dataFormat))
            {
                out = new ByteArrayOutputStream();
                tupleQuery.evaluate(new SPARQLResultsJSONWriter(out));
            }
            else if(TupleQueryResultFormat.SPARQL.getMIMETypes().contains(dataFormat))
            {
                out = new ByteArrayOutputStream();
                tupleQuery.evaluate(new SPARQLResultsXMLWriter(out));
            }
            else if(TupleQueryResultFormat.TSV.getMIMETypes().contains(dataFormat))
            {
                out = new ByteArrayOutputStream();
                tupleQuery.evaluate(new SPARQLResultsTSVWriter(out));
            }
        return out;
    }
    
    
    public ResponseStatus importFile(InputStream file, RDFFormat format, String graph) {
      
        ResponseStatus responseStatus = null;
        
        try (RepositoryConnection con = repo.getConnection()) {

        con.begin();
        try {
            ValueFactory factory = SimpleValueFactory.getInstance();
            IRI graphIRI = factory.createIRI(graph);
            
            con.add(file, graph, format, graphIRI);
            //con.add(new BufferedReader(new InputStreamReader(file, StandardCharsets.UTF_8)), graph, format, graphIRI);
            con.commit();
            responseStatus = new ResponseStatus(200, "File imported successfully");
        } catch (RepositoryException e) {
            responseStatus = new ResponseStatus(404, "Unable to connect to repository");
            con.rollback();
        } catch (IOException ex) {
            responseStatus = new ResponseStatus(400, "Bad request");
        } catch (RDFParseException ex) {
            responseStatus = new ResponseStatus(500, "Bad format in file");
        }
        }     
        return responseStatus;
    }
   
}