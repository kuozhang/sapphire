package org.eclipse.sapphire.ui.swt.gef;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ui.cspext.CspSapphireUIUtil;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class CspSearchContribution extends ControlContribution {
    private SapphireDiagramEditor editor = null;
    private DiagramNodePart cnode = null;

    public CspSearchContribution(String id, SapphireDiagramEditor editor) {
        super(id);
        this.editor = editor;
    }

    @Override
    protected Control createControl(Composite parent) {
        Text text = new Text(parent, SWT.BORDER);
        text.setMessage("输入关键字后，回车，可以快速定位节点");
        text.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.keyCode == SWT.CR) {
                    String txt = ((Text) evt.widget).getText();
                    locateNode(txt);
                }
            }

            @Override
            public void keyReleased(KeyEvent evt) {
                // cnode = null;
            }
        });

        return text;
    }

    private void locateNode(String txt) {
        if (txt == null || txt.trim().isEmpty()) {
            return;
        }
        txt = txt.toLowerCase();

        if (editor == null) {
            return;
        }

        SapphireDiagramEditorPagePart pagePart = editor.getPart();
        if (pagePart == null) {
            return;
        }

        LinkedHashMap<String, DiagramNodePart> nodes = new LinkedHashMap<String, DiagramNodePart>();
        for (DiagramNodePart nodePart : pagePart.getNodes()) {
            Element ele = nodePart.getLocalModelElement();
            Property prop = ele.property("name"); // TODO 名称字段不是name时?
            String name = ((Value<?>) prop).text(false);
            if (name != null && !name.trim().isEmpty() && name.toLowerCase().startsWith(txt)) {
                nodes.put(name, nodePart);
            }
        }

        DiagramNodePart node = null;
        if (cnode != null) {
            boolean find = false;
            for (Entry<String, DiagramNodePart> entry : nodes.entrySet()) {
                if (find) {
                    node = entry.getValue();
                    break;
                }
                if (cnode == entry.getValue()) {
                    find = true;
                }
            }
        }
        if (node == null) {
            for (Entry<String, DiagramNodePart> entry : nodes.entrySet()) {
                node = entry.getValue();
                break;
            }
        }
        if (node != null) {
            CspSapphireUIUtil.selectPart(pagePart, node);
            cnode = node;
        }
    }
}
