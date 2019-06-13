package forth.ics.isl.service;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.QueryParam;

import forth.ics.isl.blazegraph.*;
import forth.ics.isl.utils.PropertiesManager;
import forth.ics.isl.utils.ResponseStatus;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import java.net.URL;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;


/**
 *
 * @author mhalkiad
 */

@Path("/import")
public class ImportBlazegraph {
    
    private PropertiesManager propertiesManager = PropertiesManager.getPropertiesManager();
    
    
    @POST
    @Path("/import")
    //@Consumes({"text/plain", "application/rdf+xml", "application/x-turtle", "text/rdf+n3"})
    public Response importToBlazegraph(InputStream file, 
                                       @QueryParam("data-url") String dataURL,
                                       @QueryParam("service-url") String serviceURL,
                                       @QueryParam("content-type") String contentType,
                                       @QueryParam("namespace") String namespace,
                                       @DefaultValue("") @QueryParam("graph") String graph) throws MalformedURLException, IOException {


        BlazegraphManager manager = new BlazegraphManager();

        if(serviceURL == null)
            serviceURL = propertiesManager.getTripleStoreUrl();
        
        if(namespace == null)
            namespace = propertiesManager.getTripleStoreNamespace();
              
        manager.openConnectionToBlazegraph(serviceURL + "/namespace/" + namespace + "/sparql");
        
        RDFFormat format = Rio.getParserFormatForMIMEType(contentType).get();
        
        System.out.println("serviceURL:" + serviceURL);
        System.out.println("File: " + file);
        System.out.println("dataURL:" + dataURL);
        
        if(!dataURL.startsWith("http://"))
            dataURL = "http://" + dataURL;
        if(file != null)
            file = new URL(dataURL).openStream();
               
        System.out.println("FileNew: " + file);
        System.out.println("dataURLNew:" + dataURL);
        
        ResponseStatus responseStatus = manager.importFile(file, format, graph);

        manager.closeConnectionToBlazeGraph();

        // Adding Access-Control-Allow-Origin to the header in order to resolve the CORS issue between modern browsers and server
        return Response.status(responseStatus.getStatus()).entity(responseStatus.getResponse()).header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Header", "X-Requested-With").build();
        
    }
    
    
    @GET
    @Path("/helloWorld")
    public Response getHelloWorld() {
        return Response.status(200).entity("Hello World page!!!").header("Access-Control-Allow-Origin", "*").header("Access-Control-Allow-Header", "X-Requested-With").build();
    }
    
    
    
    
    
}