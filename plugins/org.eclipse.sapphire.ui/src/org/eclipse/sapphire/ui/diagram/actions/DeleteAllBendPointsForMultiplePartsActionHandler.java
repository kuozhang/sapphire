/******************************************************************************
 * Copyright (c) 2013 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 *    Konstantin Komissarchik - [376266] Diagram delete all connection bend points action should be available in multi-select mode
 *    Ling Hao - [383924]  Flexible diagram node shapes
 ******************************************************************************/

package org.eclipse.sapphire.ui.diagram.actions;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireEditorPagePart.SelectionChangedEvent;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.ui.diagram.ConnectionService;
import org.eclipse.sapphire.ui.diagram.ConnectionServiceEvent;
import org.eclipse.sapphire.ui.diagram.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;

/**
 * @author <a href="mailto:shenxue.zhou@oracle.com">Shenxue Zhou</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a>
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class DeleteAllBendPointsForMultiplePartsActionHandler extends SapphireActionHandler 
{
	@Override
	public void init(SapphireAction action, ActionHandlerDef def) 
	{
		super.init(action, def);

        SapphireDiagramEditorPagePart part = (SapphireDiagramEditorPagePart) getPart();
		part.attach(new Listener() 
		{
			@Override
			public void handle(final Event e) 
			{
                if( e instanceof SelectionChangedEvent ) 
                {
                    broadcast( new EnablementChangedEvent() );
                }
			}
		});
		part.service(ConnectionService.class).attach( new FilteredListener<ConnectionServiceEvent>()
		{
			@Override
			protected void handleTypedEvent(ConnectionServiceEvent event) 
			{
				switch(event.getConnectionEventType()) 
				{
		    	case ConnectionAddBendpoint:
	                broadcast( new EnablementChangedEvent() );
		    		break;
		    	case ConnectionRemoveBendpoint:
	                broadcast( new EnablementChangedEvent() );
		    		break;
		    	default:
		    		break;
				}
			}
		});
	}

    @Override
    public boolean isEnabled()
    {
        final SapphireDiagramEditorPagePart page = (SapphireDiagramEditorPagePart) getPart();
        
        for( ISapphirePart selectedPart : page.getSelections() )
        {
            if( selectedPart instanceof DiagramConnectionPart )
            {
                if( ! ( (DiagramConnectionPart) selectedPart ).getBendpoints().isEmpty() )
                {
                    return true;
                }
            }
        }
        
        return false;
    }

    @Override
    protected Object run( final Presentation context) 
    {
        final SapphireDiagramEditorPagePart page = (SapphireDiagramEditorPagePart) getPart();
        
        for( ISapphirePart selectedPart : page.getSelections() )
        {
            if( selectedPart instanceof DiagramConnectionPart )
            {
                ( (DiagramConnectionPart) selectedPart ).removeAllBendpoints();
            }
        }
        
        return null;
    }

}
