/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts;

public class MainViewTab {

    private Boolean showButton1;
    private Boolean showButton2;
    private String tabName;
    private Boolean rendered;

    public MainViewTab(Boolean showButton1, Boolean showButton2, String tabName, Boolean rendered) {
        this.showButton1 = showButton1;
        this.showButton2 = showButton2;
        this.tabName = tabName;
        this.rendered = rendered;
    }

    public Boolean getRendered() {
        return rendered;
    }

    public void setRendered(Boolean rendered) {
        this.rendered = rendered;
    }

    public Boolean getShowButton1() {
        return showButton1;
    }

    public void setShowButton1(Boolean showButton1) {
        this.showButton1 = showButton1;
    }

    public Boolean getShowButton2() {
        return showButton2;
    }

    public void setShowButton2(Boolean showButton2) {
        this.showButton2 = showButton2;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

}
