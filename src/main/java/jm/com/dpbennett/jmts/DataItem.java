/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts;

import javax.faces.model.SelectItem;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author dbennett
 */
@XmlRootElement
public class DataItem extends SortableSelectItem {

    public DataItem() {
        super("", "");
    }

    public DataItem(Object value, String label) {
        super(value, label);
    }

    @Override
    public String getLabel() {
        return super.getLabel(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setLabel(String label) {
        super.setLabel(label); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getValue() {
        return super.getValue(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setValue(Object value) {
        super.setValue(value); //To change body of generated methods, choose Tools | Templates.
    }

}
