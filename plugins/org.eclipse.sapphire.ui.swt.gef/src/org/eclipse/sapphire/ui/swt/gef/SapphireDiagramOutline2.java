package org.eclipse.sapphire.ui.swt.gef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.draw2d.parts.Thumbnail;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ui.cspext.CspSapphireUIUtil;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.swt.gef.parts.SapphireDiagramEditorPageEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class SapphireDiagramOutline2 extends Page implements IContentOutlinePage, ISelectionChangedListener {

    private SashForm sash;
    private Canvas canvas;
    private Thumbnail thumbnail;
    private FilteredTree tree;

    // 用来切换 缩略图和树 的按钮
    // private Action canvAction;
    // private Action treeAction;

    private ScalableFreeformRootEditPart rootEditPart;
    private Element element;

    // private static final String KEY_CANVAS = "canvas";
    // private static final String KEY_TREE = "tree";

    /**
     * Creates a new OverviewOutlinePage instance.
     * 
     * @param rootEditPart
     *            the root edit part to show the overview from
     * @param element
     */
    public SapphireDiagramOutline2(ScalableFreeformRootEditPart rootEditPart, Element element) {
        super();
        this.rootEditPart = rootEditPart;
        this.element = element;
    }

    private Map<String, Image> outLineImages = null;

    public void setOutLineImages(Map<String, Image> imgs) {
        this.outLineImages = imgs;
    }

    public Map<String, Image> getOutLineImages() {
        return this.outLineImages;
    }

    @Override
    public void selectionChanged(SelectionChangedEvent evt) {
        IStructuredSelection sel = (IStructuredSelection) evt.getSelection();
        if (!sel.isEmpty()) {
            for (Iterator<?> i = sel.iterator(); i.hasNext();) {
                Object obj = i.next();
                if ((obj instanceof Node)) {
                    locateNode((Node) obj);
                    break;
                }
            }
        }
    }

    /**
     * 定位节点
     * 
     * @param node
     * @author tds
     * @createtime 2014年11月11日 下午2:53:04
     */
    private void locateNode(Node node) {
        try {
            SapphireDiagramEditorPageEditPart pageEditPart = (SapphireDiagramEditorPageEditPart) rootEditPart
                    .getContents();
            if (pageEditPart == null) {
                return;
            }
            SapphireDiagramEditor editor = pageEditPart.getConfigurationManager().getDiagramEditor();
            if (editor == null) {
                return;
            }
            SapphireDiagramEditorPagePart pagePart = editor.getPart();
            if (pagePart == null) {
                return;
            }
            for (DiagramNodePart nodePart : pagePart.getNodes()) {
                Element ele = nodePart.getLocalModelElement();
                Property prop = ele.property("name"); // TODO 名称字段不是name时?
                String name = ((Value<?>) prop).text(false);
                if (name != null && name.equals(node.name)) {
                    CspSapphireUIUtil.selectPart(pagePart, nodePart);
                    break;
                }
            }
        } catch (Exception e) {
            // do nothing
        }
    }

    /**
	 * 
	 */
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
    }

    @Override
    public void removeSelectionChangedListener(ISelectionChangedListener arg0) {

    }

    @Override
    public void createControl(Composite parent) {
        sash = new SashForm(parent, SWT.VERTICAL);

        canvas = new Canvas(sash, SWT.NONE);
        LightweightSystem lws = new LightweightSystem(canvas);

        thumbnail = new ScrollableThumbnail((Viewport) rootEditPart.getFigure());
        // thumbnail.setBorder(new MarginBorder(3));
        thumbnail.setSource(rootEditPart.getLayer(LayerConstants.PRINTABLE_LAYERS));
        lws.setContents(thumbnail);
        // overview.setVisible(false);

        // 初始化tree
        this.tree = new FilteredTree(sash, SWT.SINGLE, new CPatternFilter(), true);
        // this.tree.setVisible(false);
        this.tree.setQuickSelectionMode(false);
        // this.tree.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

        TreeViewer treeViewer = this.tree.getViewer();
        treeViewer.setLabelProvider(new LabelProvider(this.outLineImages));
        treeViewer.setContentProvider(new ContentProvider());
        treeViewer.setInput(getData(element));
        treeViewer.addSelectionChangedListener(this);
        if (hasAtMostOneView(this.tree.getViewer())) {
            Text filterText = this.tree.getFilterControl();
            if (filterText != null) {
                filterText.setEnabled(false);
            }
        }
        treeViewer.expandAll();

        /**
         * 不加按钮了 IActionBars bar = this.getSite().getActionBars(); canvAction = new SwitchAction(KEY_CANVAS);
         * bar.getToolBarManager().add(canvAction); treeAction = new SwitchAction(KEY_TREE);
         * bar.getToolBarManager().add(treeAction); bar.updateActionBars();
         */

        sash.setWeights(new int[] { 3, 7 });
    }

    /**
     * 刷新tree
     * 
     * @param ele
     * @author tds
     * @createtime 2014年11月11日 下午2:33:35
     */
    public void update(Element ele) {
        TreeViewer treeViewer = this.tree.getViewer();
        treeViewer.setInput(getData(element));
        treeViewer.expandAll();
        // treeViewer.refresh();
    }

    private Tree getData(Element ele) {
        // Element ele = ((DiagramModel)
        // rootEditPart.getContents().getModel()).getNodes().get(0).getModelPart().getLocalModelElement();
        Tree t = new Tree();
        t.datas = new ArrayList<PNode>();
        String cn = ele.type().getQualifiedName();
        if (cn.equals("com.chanjet.csp.ide.project.ui.entity.Schema")) {
            PNode p = new PNode();
            p.name = "EO";
            p.type = "eo";
            p.nodes = new ArrayList<Node>();
            t.datas.add(p);

            ElementList<?> entities = (ElementList<?>) ele.property("Entities");
            for (Element eo : entities) {
                Node n = new Node();
                n.name = ((Value<?>) eo.property("name")).text(false);
                if (n.name == null) {
                    continue;
                }
                n.label = ((Value<?>) eo.property("label")).text(false);
                n.type = "eo";
                p.nodes.add(n);
            }

            p = new PNode();
            p.name = "Enum";
            p.type = "enum";
            p.nodes = new ArrayList<Node>();
            t.datas.add(p);

            ElementList<?> enums = (ElementList<?>) ele.property("CSPEnums");
            for (Element en : enums) {
                Node n = new Node();
                n.name = ((Value<?>) en.property("name")).text(false);
                if (n.name == null) {
                    continue;
                }
                n.type = "enum";
                p.nodes.add(n);
            }
        } else if (cn.equals("com.chanjet.csp.ide.project.ui.bo.CspBO")) {
            PNode p = new PNode();
            p.name = "BO";
            p.type = "bo";
            p.nodes = new ArrayList<Node>();
            t.datas.add(p);

            ElementList<?> bos = (ElementList<?>) ele.property("BusinessObjects");
            for (Element bo : bos) {
                Node n = new Node();
                n.name = ((Value<?>) bo.property("name")).text(false);
                if (n.name == null) {
                    continue;
                }
                n.label = ((Value<?>) bo.property("label")).text(false);
                n.type = "bo";
                p.nodes.add(n);
            }

            p = new PNode();
            p.name = "VBO";
            p.type = "vbo";
            p.nodes = new ArrayList<Node>();
            t.datas.add(p);
            ElementList<?> vbos = (ElementList<?>) ele.property("VirtualBOs");
            for (Element vbo : vbos) {
                Node n = new Node();
                n.name = ((Value<?>) vbo.property("name")).text(false);
                if (n.name == null) {
                    continue;
                }
                n.label = ((Value<?>) vbo.property("label")).text(false);
                n.type = "vbo";
                p.nodes.add(n);
            }

            p = new PNode();
            p.name = "Picker";
            p.type = "picker";
            p.nodes = new ArrayList<Node>();
            t.datas.add(p);
            ElementList<?> pickers = (ElementList<?>) ele.property("Pickers");
            for (Element picker : pickers) {
                Node n = new Node();
                n.name = ((Value<?>) picker.property("name")).text(false);
                n.label = ((Value<?>) picker.property("label")).text(false);
                n.type = "picker";
                p.nodes.add(n);
            }

            p = new PNode();
            p.name = "Query";
            p.type = "query";
            p.nodes = new ArrayList<Node>();
            t.datas.add(p);
            ElementList<?> queries = (ElementList<?>) ele.property("Queries");
            for (Element query : queries) {
                Node n = new Node();
                n.name = ((Value<?>) query.property("name")).text(false);
                n.label = ((Value<?>) query.property("label")).text(false);
                n.type = "query";
                p.nodes.add(n);
            }
        }

        return t;
    }

    private boolean hasAtMostOneView(TreeViewer tree) {
        ITreeContentProvider contentProvider = (ITreeContentProvider) tree.getContentProvider();
        Object[] children = contentProvider.getElements(tree.getInput());
        if (children.length <= 1) {
            if (children.length == 0) {
                return true;
            }
            return !contentProvider.hasChildren(children[0]);
        }
        return false;
    }

    @Override
    public void dispose() {
        if (null != thumbnail) {
            thumbnail.deactivate();
        }
        if (null != tree) {
            tree.dispose();
        }

        getSite().getActionBars().updateActionBars();

        super.dispose();
    }

    /**
	 * 
	 */
    public Control getControl() {
        return sash;
    }

    /**
	 * 
	 */
    public ISelection getSelection() {
        return StructuredSelection.EMPTY;
    }

    /**
	 * 
	 */
    public void setFocus() {
        if (getControl() != null)
            getControl().setFocus();
    }

    /**
	 * 
	 */
    public void setSelection(ISelection selection) {
    }

    // private ViewerComparator lexComparator = new ViewerComparator(new
    // LexComparator());

    // private class SwitchAction extends Action {
    // private Control ctrl;
    //
    // public SwitchAction(String key) {
    // super();
    // this.setId("com.chanjet.csp.ide.outline.SwitchAction." + key);
    // this.setText("切换");
    // this.setToolTipText("显示/隐藏：" + (key.equals(KEY_CANVAS) ? "缩略图" : "节点树"));
    // this.setImageDescriptor(Activator.getDefault().getImageDescriptorByKey(
    // key.equals(KEY_CANVAS) ? "CSP_THUMBNAIL" : "CSP_TREE"));
    // this.setDisabledImageDescriptor(Activator.getDefault().getImageDescriptorByKey("CSP_LOGO"));
    //
    // this.setEnabled(true);
    // this.valueChange(false);
    //
    // ctrl = key.equals(KEY_CANVAS) ? canvas : tree;
    // }
    //
    // @Override
    // public void run() {
    // this.valueChange(isChecked());
    // }
    //
    // private void valueChange(final boolean on) {
    // this.setChecked(on);
    // if (ctrl != null && !ctrl.isDisposed()) {
    // ctrl.setVisible(!on);
    // }
    // // overview.setVisible(!on);
    // // tree.setVisible(on);
    //
    // }
    // }

    private class Tree {
        public List<PNode> datas;
    }

    private class BNode {
        public String type;
        public String name;
    }

    private class PNode extends BNode {
        public List<Node> nodes;
    }

    private class Node extends BNode {
        public String label;
    }

    private class CPatternFilter extends PatternFilter {
        public boolean isElementSelectable(Object element) {
            return element instanceof Node;
        }

        protected boolean isLeafMatch(Viewer viewer, Object element) {
            if ((element instanceof PNode)) {
                return false;
            }
            if ((element instanceof Node)) {
                Node node = (Node) element;
                if (wordMatches(node.name) || wordMatches(node.label)) {
                    return true;
                }
            }
            return false;
        }
    }

    private class ContentProvider implements ITreeContentProvider {
        private Map<Object, Object[]> childMap = new HashMap<Object, Object[]>();

        public void dispose() {
            this.childMap.clear();
        }

        public Object[] getChildren(Object element) {
            Object[] children = (Object[]) this.childMap.get(element);
            if (children == null) {
                children = createChildren(element);
                this.childMap.put(element, children);
            }
            return children;
        }

        private Object[] createChildren(Object element) {
            if ((element instanceof Tree)) {
                Tree tree = (Tree) element;
                List<PNode> pnodes = tree.datas;
                return pnodes.toArray(new PNode[pnodes.size()]);
            }
            if ((element instanceof PNode)) {
                List<Node> nodes = ((PNode) element).nodes;
                return nodes.toArray(new Node[nodes.size()]);
            }
            return new Object[0];
        }

        public Object[] getElements(Object element) {
            return getChildren(element);
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            if (element instanceof Tree || element instanceof PNode) {
                return true;
            }
            return false;
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            this.childMap.clear();
        }
    }

    private class LabelProvider extends ColumnLabelProvider {
        Map<String, Image> images = new HashMap<String, Image>();

        public LabelProvider(Map<String, Image> images) {
            super();
            this.images = images;
        }

        protected void initialize(ColumnViewer viewer, ViewerColumn column) {
            super.initialize(viewer, column);
        }

        public Image getImage(Object element) {
            if ((element instanceof BNode)) {
                BNode node = (BNode) element;
                if (images.containsKey(node.type)) {
                    return images.get(node.type);
                }
            }
            return null;
        }

        @Override
        public void dispose() {
            super.dispose();
            // 回收图片资源
            // for (Image image : images.values()) {
            // if (image != null && !image.isDisposed()) {
            // image.dispose();
            // }
            // }
            images.clear();
        }

        public String getText(Object element) {
            String str = "##";
            if ((element instanceof PNode)) {
                PNode pnode = (PNode) element;
                return pnode.name;
            } else if ((element instanceof Node)) {
                Node node = (Node) element;
                str = node.name;
                if (node.label != null && !node.label.isEmpty()) {
                    str += "(" + node.label + ")";
                }
            }
            return str;
        }

        public Color getBackground(Object element) {
            return null;
        }

        public Color getForeground(Object element) {
            return null;
        }
    }

}
