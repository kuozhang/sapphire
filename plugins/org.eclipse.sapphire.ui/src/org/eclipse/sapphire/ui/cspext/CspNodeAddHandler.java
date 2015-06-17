package org.eclipse.sapphire.ui.cspext;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;

public abstract class CspNodeAddHandler {
    public abstract Object handle(Presentation context, DiagramNodeTemplate nodeTemplate);
}
