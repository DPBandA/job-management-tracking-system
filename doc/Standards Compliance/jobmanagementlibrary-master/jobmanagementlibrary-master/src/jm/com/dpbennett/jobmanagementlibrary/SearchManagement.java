/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jobmanagementlibrary;

import jm.com.dpbennett.utils.SearchParameters;

/**
 *
 * @author dbennett
 */
public interface SearchManagement {

    public SearchParameters getCurrentSearchParameters();

    public String getCurrentSearchParameterKey();

    public void setCurrentSearchParameterKey(String currentSearchParameterKey);
}
