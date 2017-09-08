/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts.utils;

/**
 *
 * @author dbennett
 */
public interface DialogActionHandler {

    public void handleDialogOkButtonClick();

    public void handleDialogYesButtonClick();

    public void handleDialogNoButtonClick();
    
     public void handleDialogCancelButtonClick();
    
    public DialogActionHandler initDialogActionHandlerId(String id);
    
    public String getDialogActionHandlerId();
}
