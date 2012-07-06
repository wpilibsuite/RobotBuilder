/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

import java.util.LinkedList;

/**
 *
 * @author alex
 */
public class Validator {
    private String name, type;
    LinkedList<String> fields;
    //Set used;

    public Validator(String name, String type, LinkedList<String> fields) {
        this.name = name;
        this.type = type;
        this.fields = fields;
    }
    
    public Validator copy() {
        LinkedList<String> newFields = new LinkedList<String>();
        for (String item : fields) {
            newFields.add(item);
        }
        return new Validator(name, type, newFields);
    }
    
    /**
     * 
     * @return 
     */
    //public boolean claim(Map<String, String> fields) {
        
    //}
            
}
