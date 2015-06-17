package org.eclipse.sapphire.ui.cspext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.FileUtil;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

public class CspSapphireUIUtil {
    public static final String CSP_MODEL_FOLDER = ".settings"; // 模型的缓存目录，本工程下的.settings目录
    public static final String CSP_LAYOUT_POSTFIX = ".layout"; // 模型的布局文件后缀
    public static final String CSP_STATE_POSTFIX = ".state"; // 模型的状态文件后缀
    public static final String CSP_LAYOUT_SHARE_FILE = "/config/layout.xml"; // 模型布局共享标记

    /**
     * 获取模型的缓存
     * 
     * @param editorInput
     * @param postfix
     *            ：布局或状态
     * @return
     * @author tds
     * @createtime 2014年10月23日 上午11:35:41
     */
    private static File getModelFile(IEditorInput editorInput, String postfix) {
        if (editorInput instanceof FileEditorInput) {
            FileEditorInput fileEditorInput = (FileEditorInput) editorInput;
            IFile ifile = fileEditorInput.getFile();
            String projectPath = ifile.getProject().getLocation().toPortableString();
            File folder = new File(projectPath, CSP_MODEL_FOLDER);
            if (!folder.exists()) {
                try {
                    FileUtil.mkdirs(folder);
                } catch (IOException e) {
                    // do nothing
                }
            }
            File file = new File(folder, ifile.getName() + postfix);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // do nothing
                }
            }
            return file;
        }
        return null;
    }

    /**
     * 模型打开状态的缓存文件【展开/收缩等等】
     * 
     * @param editorInput
     * @return
     * @author tds
     * @createtime 2014年10月23日 上午11:34:06
     */
    public static File getStateFile(IEditorInput editorInput) {
        return getModelFile(editorInput, CSP_STATE_POSTFIX);
    }

    /**
     * 模型布局的缓存文件【节点/连线的位置、显示网格等等选项】
     * 
     * @param editorInput
     * @return
     * @author tds
     * @createtime 2014年10月23日 上午11:27:10
     */
    public static File getLayoutFile(IEditorInput editorInput) {
        return getModelFile(editorInput, CSP_LAYOUT_POSTFIX);
    }

    /**
     * 触发元素删除事件《包括节点和连线》
     * 
     * @param part
     * @author tds
     * @createtime 2015年6月10日 下午1:52:49
     */
    public static void handleDelete(ISapphirePart part) {
        if (part == null) {
            return;
        }
        Element et = part.getLocalModelElement();
        if (et.type().hasAnnotation(CspDelete.class)) {
            try {
                CspDelete anno = et.type().getAnnotation(CspDelete.class);
                CspDeleteHandler handler = anno.handler().newInstance();
                handler.handle(part);
            } catch (Exception e) {
                // do nothing
            }
        }
    }

    /**
     * Node是否配置了自定义的addAction
     * 
     * @param nodeTemplate
     * @return
     * @author tds
     * @createtime 2015年6月10日 下午2:54:55
     */
    public static CspNodeAddHandler getNodeAddAnno(DiagramNodeTemplate nodeTemplate) {
        if (nodeTemplate == null) {
            return null;
        }
        ElementType et = nodeTemplate.getModelProperty().getType();
        if (et.hasAnnotation(CspNodeAdd.class)) {
            try {
                CspNodeAdd anno = et.getAnnotation(CspNodeAdd.class);
                return anno.handler().newInstance();
            } catch (Exception e) {
                // do nothing
            }
        }
        return null;
    }

    /**
     * Shape是否配置了自定义的addAction
     * 
     * @param et
     * @return
     * @author tds
     * @createtime 2015年6月10日 下午3:05:38
     */
    public static CspShapeAddHandler getShapeAddAnno(ElementType et) {
        if (et.hasAnnotation(CspShapeAdd.class)) {
            try {
                CspShapeAdd anno = et.getAnnotation(CspShapeAdd.class);
                return anno.handler().newInstance();
            } catch (Exception e) {
                // do nothing
            }
        }
        return null;
    }

    /**
     * 选中一个节点或连线
     * 
     * @param pagePart
     * @param part
     * @author tds
     * @createtime 2014年12月22日 上午10:51:01
     */
    public static void selectPart(final SapphireDiagramEditorPagePart pagePart, final ISapphirePart part) {
        try {
            List<ISapphirePart> ps = new ArrayList<ISapphirePart>();
            ps.add(part);
            pagePart.setSelections(ps, true);
        } catch (Exception e) {
            // CspLogger.log(e);
        }
    }

    /**
     * 建模时对Node进行定位
     * 
     * @param pagePart
     * @param ele
     * @param focus
     *            定位后是否获取焦点并进入编辑状态
     * @author tds
     * @createtime 2014年11月14日 上午10:33:58
     */
    public static void setXY(final SapphireDiagramEditorPagePart pagePart, final org.eclipse.sapphire.Element ele,
            final boolean focus) {
        try {
            Point pt = pagePart.getMouseLocation();
            DiagramNodePart nodePart = pagePart.getDiagramNodePart(ele);
            nodePart.setNodeBounds(pt.getX(), pt.getY());
            if (focus) {
                pagePart.selectAndDirectEdit(nodePart);
            }
        } catch (Exception e) {
            // CspLogger.log(e);
        }
    }

    public static void setXY(final SapphireDiagramEditorPagePart pagePart, final org.eclipse.sapphire.Element ele) {
        setXY(pagePart, ele, false);
    }
}
