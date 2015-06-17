package org.eclipse.sapphire.ui.diagram.shape.def;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.ui.forms.ActuatorDef;

@Label(standard = "cspactuator")
@Image(path = "ActuatorDef.png")
@XmlBinding(path = "cspactuator")
public interface CspActuatorDef extends TextDef, ActuatorDef {
	ElementType TYPE = new ElementType(CspActuatorDef.class);
}
