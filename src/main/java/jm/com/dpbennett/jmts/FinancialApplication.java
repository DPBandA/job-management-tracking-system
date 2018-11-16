/*
Job Management & Tracking System (JMTS) 
Copyright (C) 2017  D P Bennett & Associates Limited

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

Email: info@dpbennett.com.jm
 */
package jm.com.dpbennett.jmts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.Supplier;
import jm.com.dpbennett.wal.utils.DateUtils;
import jm.com.dpbennett.wal.utils.FinancialUtils;

/**
 *
 * @author Desmond Bennett
 */
@Named(value = "FinApp")
@ApplicationScoped
@Singleton
public class FinancialApplication implements Serializable {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    @PersistenceUnit(unitName = "AccPacPU")
    private EntityManagerFactory EMF2;

    /**
     * Creates a new instance of Application
     */
    public FinancialApplication() {
        
    }
    
    public List<Supplier> completeActiveSupplier(String query) {
        try {
            return Supplier.findActiveSuppliersByAnyPartOfName(getEntityManager1(), query);

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List getCostCodeList() {
        return FinancialUtils.getCostCodeList();
    }

    public EntityManager getEntityManager1() {
        return EMF1.createEntityManager();
    }

    public EntityManager getEntityManager2() {
        return EMF2.createEntityManager();
    }

    /**
     * Returns the discount type that can be applied to a payment/amount
     *
     * @return
     */
    public List getDiscountTypes() {

        return FinancialUtils.getDiscountTypes();
    }

    /**
     *
     * @return
     */
    public List getPaymentTypes() {
        return FinancialUtils.getPaymentTypes();
    }

    /**
     *
     * @return
     */
    public List getPaymentPurposes() {
        return FinancialUtils.getPaymentPurposes();
    }

    @SuppressWarnings("unchecked")
    public static <T> T findBean(String beanName) {
        FacesContext context = FacesContext.getCurrentInstance();
        return (T) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
    }

    
}
