/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.samples.po;

import java.util.Date;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl

public interface PurchaseOrder extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( PurchaseOrder.class );
    
    // *** Id ***
    
    @Label( standard = "ID" )
    @Required
    
    ValueProperty PROP_ID = new ValueProperty( TYPE, "Id" );
    
    Value<String> getId();
    void setId( String value );
    
    // *** Customer ***
    
    @Label( standard = "customer" )
    @Required
    
    ValueProperty PROP_CUSTOMER = new ValueProperty( TYPE, "Customer" );
    
    Value<String> getCustomer();
    void setCustomer( String value );
    
    // *** InitialQuoteDate ***
    
    @Type( base = Date.class )
    @Label( standard = "initial quote date" )
    
    ValueProperty PROP_INITIAL_QUOTE_DATE = new ValueProperty( TYPE, "InitialQuoteDate" );
    
    Value<Date> getInitialQuoteDate();
    void setInitialQuoteDate( String value );
    void setInitialQuoteDate( Date value );
    
    // *** OrderDate ***
    
    @Type( base = Date.class )
    @Label( standard = "order date" )
    
    ValueProperty PROP_ORDER_DATE = new ValueProperty( TYPE, "OrderDate" );
    
    Value<Date> getOrderDate();
    void setOrderDate( String value );
    void setOrderDate( Date value );
    
    // *** FulfillmentDate ***
    
    @Type( base = Date.class )
    @Label( standard = "fulfillment date" )
    
    ValueProperty PROP_FULFILLMENT_DATE = new ValueProperty( TYPE, "FulfillmentDate" );
    
    Value<Date> getFulfillmentDate();
    void setFulfillmentDate( String value );
    void setFulfillmentDate( Date value );
    
    // *** BillingInformation ***
    
    @Type( base = BillingInformation.class )
    @Label( standard = "billing information" )
    
    ImpliedElementProperty PROP_BILLING_INFORMATION = new ImpliedElementProperty( TYPE, "BillingInformation" );
    
    BillingInformation getBillingInformation();
    
    // *** ShippingInformation ***
    
    @Type( base = ShippingInformation.class )
    @Label( standard = "shipping information" )
    
    ImpliedElementProperty PROP_SHIPPING_INFORMATION = new ImpliedElementProperty( TYPE, "ShippingInformation" );
    
    ShippingInformation getShippingInformation();
    
    // *** Entries ***
    
    @Type( base = PurchaseOrderEntry.class )
    @Label( standard = "entries" )
    @CountConstraint( min = 1 )
    
    ListProperty PROP_ENTRIES = new ListProperty( TYPE, "Entries" );
    
    ModelElementList<PurchaseOrderEntry> getEntries();
    
}
