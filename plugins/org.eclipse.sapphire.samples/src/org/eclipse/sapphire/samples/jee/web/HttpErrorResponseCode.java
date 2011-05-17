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

package org.eclipse.sapphire.samples.jee.web;

import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * <p>Enumeration of all HTTP error response codes (4xx and 5xx).</p>
 * 
 * <p>Based on <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">HTTP 1.1 specification (RFC 2616)</a>.</p>
 * 
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public enum HttpErrorResponseCode
{
    @Label( standard = "400 Bad Request" )
    @EnumSerialization( primary = "400" )
    
    HTTP_400,

    @Label( standard = "401 Unauthorized" )
    @EnumSerialization( primary = "401" )
    
    HTTP_401,

    @Label( standard = "402 Payment Required" )
    @EnumSerialization( primary = "402" )
    
    HTTP_402,

    @Label( standard = "403 Forbidden" )
    @EnumSerialization( primary = "403" )
    
    HTTP_403,

    @Label( standard = "404 Not Found" )
    @EnumSerialization( primary = "404" )
    
    HTTP_404,

    @Label( standard = "405 Method Not Allowed" )
    @EnumSerialization( primary = "405" )
    
    HTTP_405,

    @Label( standard = "406 Not Acceptable" )
    @EnumSerialization( primary = "406" )
    
    HTTP_406,

    @Label( standard = "407 Proxy Authentication Required" )
    @EnumSerialization( primary = "407" )
    
    HTTP_407,

    @Label( standard = "408 Request Timeout" )
    @EnumSerialization( primary = "408" )
    
    HTTP_408,

    @Label( standard = "409 Conflict" )
    @EnumSerialization( primary = "409" )
    
    HTTP_409,

    @Label( standard = "410 Gone" )
    @EnumSerialization( primary = "410" )
    
    HTTP_410,

    @Label( standard = "411 Length Required" )
    @EnumSerialization( primary = "411" )
    
    HTTP_411,

    @Label( standard = "412 Precondition Failed" )
    @EnumSerialization( primary = "412" )
    
    HTTP_412,

    @Label( standard = "413 Request Entity Too Large" )
    @EnumSerialization( primary = "413" )
    
    HTTP_413,

    @Label( standard = "414 Request-URI Too Long" )
    @EnumSerialization( primary = "414" )
    
    HTTP_414,

    @Label( standard = "415 Unsupported Media Type" )
    @EnumSerialization( primary = "415" )
    
    HTTP_415,

    @Label( standard = "416 Requested Range Not Satisfiable" )
    @EnumSerialization( primary = "416" )
    
    HTTP_416,

    @Label( standard = "417 Expectation Failed" )
    @EnumSerialization( primary = "417" )
    
    HTTP_417,

    @Label( standard = "500 Internal Server Error" )
    @EnumSerialization( primary = "500" )
    
    HTTP_500,

    @Label( standard = "501 Not Implemented" )
    @EnumSerialization( primary = "501" )
    
    HTTP_501,

    @Label( standard = "502 Bad Gateway" )
    @EnumSerialization( primary = "502" )
    
    HTTP_502,

    @Label( standard = "503 Service Unavailable" )
    @EnumSerialization( primary = "503" )
    
    HTTP_503,

    @Label( standard = "504 Gateway Timeout" )
    @EnumSerialization( primary = "504" )
    
    HTTP_504,

    @Label( standard = "505 HTTP Version Not Supported" )
    @EnumSerialization( primary = "505" )
    
    HTTP_505
}
