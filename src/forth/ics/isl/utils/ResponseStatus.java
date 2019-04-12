/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forth.ics.isl.utils;


/**
 *
 * @author mhalkiad
 */

public class ResponseStatus {
    
    private String response;
    private int status;
    
    public ResponseStatus() {}
    
    public ResponseStatus(int status, String response) {
        this.status = status;
        this.response = response;
    }
    
    public String getResponse() {
        return response;
    }
    
    
    public int getStatus() {
        return status;
    }
    
    
    public void setResponse(String response) {
        this.response = response;
    }
    
    
    public void setStatus(int status) {
        this.status = status;
    }
    
          
    
    
    
}
