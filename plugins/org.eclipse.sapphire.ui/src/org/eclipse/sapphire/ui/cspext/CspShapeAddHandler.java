package org.eclipse.sapphire.ui.cspext;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.ShapeFactoryPart;

public abstract class CspShapeAddHandler {
    public abstract Object handle(Presentation context, DiagramNodePart nodePart, ShapeFactoryPart factory,
            ElementType type, String CspParams);

}
