/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/

package org.eclipse.sapphire.modeling.validators;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.annotations.ModelPropertyValidator;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class IPAddressValidator extends ModelPropertyValidator<Value<?>> {

    @Override
    public IStatus validate(Value<?> value) {
        String address = value.getText();
        int index;

        if ((address != null) && !(address.equals(""))) {
            for (int j = 0; j < 4; j++) {
                index = address.indexOf(".");
                if (index == -1 && j < 3) {
                    final String label = value.getProperty().getLabel(true, CapitalizationType.NO_CAPS, false);
                    return createErrorStatus(Resources.bind(Resources.ipAddressError, ((Value<?>) value).getText(), label));
                }

                if (j == 3) {
                    index = address.length();
                }

                for (int i = 0; i < index; i++) {
                    if (!(Character.isDigit(address.charAt(i)))) {
                        final String label = value.getProperty().getLabel(true, CapitalizationType.NO_CAPS, false);
                        return createErrorStatus(Resources.bind(Resources.ipAddressError, ((Value<?>) value).getText(), label));
                    }
                }
                if (j < 3) {
                    address = address.substring(index + 1);
                }
            }
        }
        return Status.OK_STATUS;
    }
    
    private static final class Resources
    
        extends NLS
        
    {
        public static String ipAddressError;
    
        static
        {
            initializeMessages( IPAddressValidator.class.getName(), Resources.class );
        }
    }


}
