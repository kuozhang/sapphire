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

package org.eclipse.sapphire.ui;

import static org.eclipse.sapphire.ui.renderers.swt.SwtRendererUtil.toImageDescriptor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.modeling.ImageData;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.renderers.swt.SwtRendererUtil;
import org.eclipse.swt.graphics.Image;

/**
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 */

public final class SapphireImageCache
{
    public static final ImageDescriptor OVERLAY_ERROR
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/overlays/error.gif" ) );
    
    public static final ImageDescriptor OVERLAY_WARNING
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/overlays/warning.png" ) );
    
    public static final ImageDescriptor DECORATOR_ASSIST
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/decorators/assist.png" ) );
    
    public static final ImageDescriptor DECORATOR_ASSIST_FAINT
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/decorators/assist-faint.png" ) );
    
    public static final ImageDescriptor DECORATOR_BLANK
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/decorators/blank.png" ) );
    
    public static final ImageDescriptor ACTION_ADD 
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/actions/add.png" ) );
    
    public static final ImageDescriptor ACTION_EDIT 
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/actions/edit.png" ) );
    
    public static final ImageDescriptor ACTION_DELETE 
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/actions/delete.png" ) );
    
    public static final ImageDescriptor ACTION_MOVE_UP 
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/actions/move-up.png" ) );
    
    public static final ImageDescriptor ACTION_MOVE_DOWN 
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/actions/move-down.png" ) );
    
    public static final ImageDescriptor ACTION_MOVE_LEFT 
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/actions/move-left.png" ) );
    
    public static final ImageDescriptor ACTION_MOVE_RIGHT 
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/actions/move-right.png" ) );
    
    public static final ImageDescriptor ACTION_EXPAND_ALL 
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/actions/expand-all.png" ) );
    
    public static final ImageDescriptor ACTION_COLLAPSE_ALL 
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/actions/collapse-all.png" ) );
    
    public static final ImageDescriptor ACTION_RESTORE_DEFAULTS 
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/actions/restore-defaults.png" ) );
    
    public static final ImageDescriptor ACTION_BROWSE
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/actions/browse.png" ) );
    
    public static final ImageDescriptor ACTION_BROWSE_MINI
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/actions/browse-mini.png" ) );
    
    public static final ImageDescriptor ACTION_SELECT_ALL
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/actions/select-all.png" ) );
    
    public static final ImageDescriptor ACTION_DESELECT_ALL
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/actions/deselect-all.png" ) );
    
    public static final ImageDescriptor ACTION_SHOW_IN_SOURCE
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/actions/show-in-source.png" ) );
    
    public static final ImageDescriptor ACTION_DEFAULT
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/actions/default.png" ) );
    
    public static final ImageDescriptor ACTION_HIDE_OUTLINE
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/actions/hide-outline.png" ) );
    
    public static final ImageDescriptor OBJECT_FILE
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/objects/file.png" ) );
    
    public static final ImageDescriptor OBJECT_FOLDER
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/objects/folder.png" ) );
    
    public static final ImageDescriptor OBJECT_PACKAGE
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/objects/package.png" ) );
    
    public static final ImageData IMG_OBJECT_CONTAINER_NODE
        = ImageData.createFromClassLoader( SapphireImageCache.class, "images/objects/container-node.png" );
    
    public static final ImageDescriptor OBJECT_CONTAINER_NODE
        = SwtRendererUtil.toImageDescriptor( IMG_OBJECT_CONTAINER_NODE );

    public static final ImageData IMG_OBJECT_LEAF_NODE
        = ImageData.createFromClassLoader( SapphireImageCache.class, "images/objects/leaf-node.png" );

    public static final ImageDescriptor OBJECT_LEAF_NODE
        = SwtRendererUtil.toImageDescriptor( IMG_OBJECT_LEAF_NODE );
    
    public static final ImageDescriptor OBJECT_CHECK_ON
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/objects/check-on.gif" ) );
    
    public static final ImageDescriptor OBJECT_CHECK_OFF
        = SwtRendererUtil.toImageDescriptor( ImageData.createFromClassLoader( SapphireImageCache.class, "images/objects/check-off.gif" ) );
    
    private final Map<ImageDescriptor,ImageHandle> imageDescToImageHandle = new HashMap<ImageDescriptor,ImageHandle>();
    
    private final Map<org.eclipse.sapphire.modeling.ImageData,ImageDescriptor> imageDataToImageDesc 
        = new HashMap<org.eclipse.sapphire.modeling.ImageData,ImageDescriptor>();
    
    public Image getImage( final ImageDescriptor imageDescriptor )
    {
        return getImage( imageDescriptor, Status.Severity.OK );
    }

    public Image getImage( final ImageDescriptor imageDescriptor,
                           final Status.Severity problemSeverity )
    {
        ImageHandle imageHandle = this.imageDescToImageHandle.get( imageDescriptor );
        
        if( imageHandle == null )
        {
            imageHandle = new ImageHandle( imageDescriptor );
            this.imageDescToImageHandle.put( imageDescriptor, imageHandle );
        }
        
        return imageHandle.getImage( problemSeverity );
    }

    public ImageDescriptor getImageDescriptor( final ModelElementType type )
    {
        return toImageDescriptor( type.image() );
    }
    
    public Image getImage( final ModelElementType type )
    {
        return getImage( type.image() );
    }
    
    public Image getImage( final org.eclipse.sapphire.modeling.ImageData imageData )
    {
        return getImage( imageData, Status.Severity.OK );
    }
    
    public Image getImage( final org.eclipse.sapphire.modeling.ImageData imageData,
                           final Status.Severity severity )
    {
        if( imageData != null )
        {
            ImageDescriptor imageDescriptor = this.imageDataToImageDesc.get( imageData );
            
            if( imageDescriptor == null )
            {
                imageDescriptor = toImageDescriptor( imageData );
                this.imageDataToImageDesc.put( imageData, imageDescriptor );
            }
            
            return getImage( imageDescriptor, severity );
        }
        
        return null;
    }

    public void dispose()
    {
        for( ImageHandle imageHandle : this.imageDescToImageHandle.values() )
        {
            imageHandle.dispose();
        }
    }
    
    private static final class ImageHandle
    {
        private final ImageDescriptor baseImageDescriptor;
        private Image baseImage;
        private Image warningImage;
        private Image errorImage;
        
        public ImageHandle( final ImageDescriptor baseImageDescriptor )
        {
            this.baseImageDescriptor = baseImageDescriptor;
        }
        
        public Image getImage( final Status.Severity severity )
        {
            if( this.baseImageDescriptor == null )
            {
                return null;
            }
            
            if( severity == Status.Severity.ERROR )
            {
                if( this.errorImage == null )
                {
                    final ImageDescriptor desc = new ProblemOverlayImageDescriptor( this.baseImageDescriptor, Status.Severity.ERROR );
                    this.errorImage = desc.createImage();
                }
                
                return this.errorImage;
            }
            else if( severity == Status.Severity.WARNING )
            {
                if( this.warningImage == null )
                {
                    final ImageDescriptor desc = new ProblemOverlayImageDescriptor( this.baseImageDescriptor, Status.Severity.WARNING );
                    this.warningImage = desc.createImage();
                }
                
                return this.warningImage;
            }
            else
            {
                if( this.baseImage == null )
                {
                    this.baseImage = this.baseImageDescriptor.createImage();
                }
                
                return this.baseImage;
            }
        }
        
        public void dispose()
        {
            if( this.baseImage != null )
            {
                this.baseImage.dispose();
            }
            
            if( this.errorImage != null )
            {
                this.errorImage.dispose();
            }
            
            if( this.warningImage != null )
            {
                this.warningImage.dispose();
            }
        }
    }

}
