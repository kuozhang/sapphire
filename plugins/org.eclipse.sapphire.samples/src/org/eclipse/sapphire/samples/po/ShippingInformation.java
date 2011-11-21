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

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.DependsOn;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.samples.contacts.internal.CityNamePossibleValuesService;
import org.eclipse.sapphire.samples.contacts.internal.StateCodePossibleValuesService;
import org.eclipse.sapphire.samples.contacts.internal.ZipCodePossibleValuesService;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

@GenerateImpl

public interface ShippingInformation extends IModelElement
{
    ModelElementType TYPE = new ModelElementType( ShippingInformation.class );
    
    // *** Name ***
    
    @Label( standard = "name" )
    @Required
    @DefaultValue( text = "${ Parent().BillingInformation.Name }" )
    
    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );
    
    Value<String> getName();
    void setName( String value );
    
    // *** Organization ***
    
    @Label( standard = "organization" )
    @DefaultValue( text = "${ Parent().BillingInformation.Organization }" )
    
    ValueProperty PROP_ORGANIZATION = new ValueProperty( TYPE, "Organization" );
    
    Value<String> getOrganization();
    void setOrganization( String value );
    
    // *** Street ***

    @Label( standard = "street" )
    @Required
    @DefaultValue( text = "${ Parent().BillingInformation.Street }" )

    ValueProperty PROP_STREET = new ValueProperty( TYPE, "Street" );

    Value<String> getStreet();
    void setStreet( String street );
    
    // *** City ***

    @Label( standard = "city" )
    @Required
    @Service( impl = CityNamePossibleValuesService.class )
    @DependsOn( { "ZipCode", "State" } )
    @DefaultValue( text = "${ Parent().BillingInformation.City }" )

    ValueProperty PROP_CITY = new ValueProperty( TYPE, "City" );

    Value<String> getCity();
    void setCity( String city );

    // *** State ***

    @Label( standard = "state" )
    @Required
    @Service( impl = StateCodePossibleValuesService.class )
    @DependsOn( { "ZipCode", "City" } )
    @DefaultValue( text = "${ Parent().BillingInformation.State }" )

    ValueProperty PROP_STATE = new ValueProperty( TYPE, "State" );

    Value<String> getState();
    void setState( String state );

    // *** ZipCode ***

    @Label( standard = "ZIP code" )
    @Required
    @Service( impl = ZipCodePossibleValuesService.class )
    @DependsOn( { "State", "City" } )
    @DefaultValue( text = "${ Parent().BillingInformation.ZipCode }" )

    ValueProperty PROP_ZIP_CODE = new ValueProperty( TYPE, "ZipCode" );

    Value<String> getZipCode();
    void setZipCode( String zipCode );

}
