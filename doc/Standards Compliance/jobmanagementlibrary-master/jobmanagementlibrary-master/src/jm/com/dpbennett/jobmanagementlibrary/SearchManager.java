/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jobmanagementlibrary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.model.SelectItem;
import jm.com.dpbennett.entity.DatePeriod;
import jm.com.dpbennett.utils.BusinessEntityUtils;
import jm.com.dpbennett.utils.SearchParameters;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author dbennett
 */
public class SearchManager implements SearchManagement, Serializable {

    private HashMap searhParameters = new HashMap();
    private String currentSearchParameterKey;
    private int mainTabViewActiveIndex;
    private int legalMetTabViewActiveIndex;

    /**
     * Creates a new instance of SearchManager
     */
    public SearchManager() {

        ArrayList jobSearchTypes = new ArrayList();
        ArrayList jobSearchDateFields = new ArrayList();

        // add items
        jobSearchTypes.add(new SelectItem("General", "General"));
        jobSearchTypes.add(new SelectItem("My jobs", "My jobs"));
        jobSearchTypes.add(new SelectItem("My department's jobs", "My department's jobs"));

        jobSearchDateFields.add(new SelectItem("dateSubmitted", "Date submitted"));
        jobSearchDateFields.add(new SelectItem("dateOfCompletion", "Date completed"));
        jobSearchDateFields.add(new SelectItem("expectedDateOfCompletion", "Exp'ted date of completion"));
        jobSearchDateFields.add(new SelectItem("dateSamplesCollected", "Date sample(s) collected"));
        jobSearchDateFields.add(new SelectItem("dateDocumentCollected", "Date document(s) collected"));

        searhParameters.put("Job Search", new SearchParameters("Job Search", jobSearchTypes, jobSearchDateFields,  "dateSubmitted", "General", new DatePeriod("This month", "month", null, null, false, false, true), ""));
        searhParameters.put("Service Request Search", new SearchParameters("Service Request Search", jobSearchTypes, jobSearchDateFields,  "dateSubmitted", "General", new DatePeriod("This month", "month", null, null, false, false, true), ""));


        ArrayList legalMetSearchTypes = new ArrayList();
        ArrayList legalMetSearchDateFields = new ArrayList();

        legalMetSearchTypes.add(new SelectItem("General", "General"));

        legalMetSearchDateFields.add(new SelectItem("dateIssued", "Date certified"));
        legalMetSearchDateFields.add(new SelectItem("expiryDate", "Expiry date"));
        
        searhParameters.put("Legal Metrology Search", new SearchParameters("Legal Metrology Search", legalMetSearchTypes, legalMetSearchDateFields, "dateIssued", "General", 
                new DatePeriod("Custom", "custom", 
                BusinessEntityUtils.createDate(2000, 0, 1), new Date(), false, false, false), ""/*, false, false*/));
    }

    public int getMainTabViewActiveIndex() {
        return mainTabViewActiveIndex;
    }

    public void setMainTabViewActiveIndex(int mainTabViewActiveIndex) {
        this.mainTabViewActiveIndex = mainTabViewActiveIndex;
    }

    public int getLegalMetTabViewActiveIndex() {
        return legalMetTabViewActiveIndex;
    }

    public void setLegalMetTabViewActiveIndex(int legalMetTabViewActiveIndex) {
        this.legalMetTabViewActiveIndex = legalMetTabViewActiveIndex;
    }

//    public JobManagerUser getUser() {
//        return JMTSApp.getUser();
//    }

    @Override
    public String getCurrentSearchParameterKey() {
        return currentSearchParameterKey;
    }

    @Override
    public void setCurrentSearchParameterKey(String currentSearchParameterKey) {
        this.currentSearchParameterKey = currentSearchParameterKey;
    }

    @Override
    public SearchParameters getCurrentSearchParameters() {
        return (SearchParameters) searhParameters.get(currentSearchParameterKey);
    }

    public void updateDateField() {
        doSearch();
    }

    public void updateSearchType() {
        doSearch();
    }

    public void changeSearchDatePeriod() {
        getCurrentSearchParameters().getDatePeriod().initDatePeriod();
        //doJobSearch();
        doSearch();
    }

    public void handleStartDateSelect(SelectEvent event) {
        //dateSearchPeriod.setStartDate(event.getDate());
        getCurrentSearchParameters().getDatePeriod().setStartDate((Date)event.getObject());
        doSearch();
    }

    public void handleEndDateSelect(SelectEvent event) {
        //dateSearchPeriod.setEndDate(event.getDate());
        getCurrentSearchParameters().getDatePeriod().setEndDate((Date)event.getObject());
        doSearch();
    }

    /**
     * For future implementation if necessary
     *
     * @param query
     * @return
     */
    public List<String> completeSearchText(String query) {
        List<String> suggestions = new ArrayList<String>();

        return suggestions;
    }

    public void doSearch() {
        RequestContext context = RequestContext.getCurrentInstance();

        if (currentSearchParameterKey.equals("Job Search")) {
            JobManager jm = JMTSApp.findBean("jobManager");
            if (jm != null) {
                System.out.println("doing job search");
                jm.doJobSearch(getCurrentSearchParameters());
                context.update("mainTabViewForm:mainTabView:jobsDatabaseTable");
            }
        } else if (currentSearchParameterKey.equals("Service Request Search")) {
            ServiceManager sm = JMTSApp.findBean("serviceManager");
            if (sm != null) {
                sm.doServiceRequestSearch(getCurrentSearchParameters());
                context.update("mainTabViewForm:mainTabView:serviceRequestsDatabaseTable");
            }
        } else if (currentSearchParameterKey.equals("Legal Metrology Search")) {
            System.out.println("Legal Met Search: " + mainTabViewActiveIndex + ", " + legalMetTabViewActiveIndex);
            LegalMetrologyManager lmm = JMTSApp.findBean("legalMetrologyManager");
            if (legalMetTabViewActiveIndex == 0) { // petrol stations tab?   
                if (lmm != null) {
                    lmm.doPetrolStationSearch(getCurrentSearchParameters());
                    context.update("mainTabViewForm:mainTabView:legalMetTabView:petrolStationsDatabaseTable");
                }
            } else if (legalMetTabViewActiveIndex == 1) { // scales tab?   
                if (lmm != null) {
                    lmm.doScaleSearch(getCurrentSearchParameters());
                    context.update("mainTabViewForm:mainTabView:legalMetTabView:scalesDatabaseTable");
                }
            }
        }
    }
}
