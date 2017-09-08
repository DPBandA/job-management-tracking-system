/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts;

import java.text.Collator;
import javax.faces.model.SelectItem;

/**
 *
 * @author dbennett
 */
public class SortableSelectItem extends SelectItem implements Comparable {

    public SortableSelectItem() {
    }

    public SortableSelectItem(Object value, String label) {
        super(value, label);
    }

    @Override
    public int compareTo(Object o) {
        return Collator.getInstance().compare(this.getLabel(), ((SortableSelectItem) o).getLabel());
    }
}
