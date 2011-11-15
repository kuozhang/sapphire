/******************************************************************************
 * Copyright (c) 2011 Red Hat and Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Rob Cernich - initial implementation
 *    Konstantin Komissarchik - initial implementation review and related changes
 ******************************************************************************/

package org.eclipse.sapphire.ui;

import java.util.Collections;

import org.eclipse.sapphire.modeling.CapitalizationType;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.ui.def.ISapphireDocumentation;
import org.eclipse.sapphire.ui.def.ISapphireDocumentationDef;
import org.eclipse.sapphire.ui.def.ISapphireDocumentationRef;
import org.eclipse.sapphire.ui.def.FormEditorPageDef;
import org.eclipse.sapphire.ui.internal.TableWrapLayoutUtil;
import org.eclipse.sapphire.ui.swt.renderer.SapphireToolBarManagerActionPresentation;
import org.eclipse.sapphire.ui.util.SapphireHelpSystem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author <a href="mailto:rcernich@redhat.com">Rob Cernich</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a> 
 */

public class StandardFormEditorPage extends SapphireEditorFormPage {

    /**
     * Helper method for initializing the FormEditor page and page part.
     * 
     * @param editor the container
     * @param rootModelElement the root model element
     * @param definition the page definition
     * @return a new FormPage that has been associated with its ISapphirePart,
     *         which has been associated with the FormEditor.
     */
    public static StandardFormEditorPage createFormEditorPage(final SapphireEditor editor,
            final IModelElement rootModelElement, final FormEditorPageDef definition) {
        StandardFormEditorPagePart editorPagePart = new StandardFormEditorPagePart();
        editorPagePart.init(editor, rootModelElement, definition, Collections.<String, String> emptyMap());
        return new StandardFormEditorPage(editor, editorPagePart);
    }

    public StandardFormEditorPage(final SapphireEditor editor, final StandardFormEditorPagePart editorPagePart) {
        super(editor, editorPagePart);
        setPartName(getPart().getDefinition().getPageName().getLocalizedText(CapitalizationType.TITLE_STYLE, false));
    }

    @Override
    public String getId() {
        return getPart().getDefinition().getId().getContent();
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        final FormToolkit toolkit = managedForm.getToolkit();
        toolkit.decorateFormHeading(managedForm.getForm().getForm());
        managedForm.getForm().setText(
                getDefinition().getPageHeaderText().getLocalizedText(CapitalizationType.TITLE_STYLE, false));

        TableWrapLayout layout = TableWrapLayoutUtil.twlayout(getDefinition().getNumColumns().getContent(), 10, 10, 10,
                10);
        layout.horizontalSpacing = 10;
        layout.verticalSpacing = 10;
        managedForm.getForm().getBody().setLayout(layout);
        getPart().render(new FormPageRenderingContext(getEditor(), managedForm));

        final ISapphireDocumentation doc = getDefinition().getDocumentation().element();

        if (doc != null) {
            ISapphireDocumentationDef docdef = null;

            if (doc instanceof ISapphireDocumentationDef) {
                docdef = (ISapphireDocumentationDef) doc;
            } else {
                docdef = ((ISapphireDocumentationRef) doc).resolve();
            }

            if (docdef != null) {
                SapphireHelpSystem.setHelp(managedForm.getForm().getBody(), docdef);
            }
        }

        final SapphireActionGroup actions = getPart().getActions(SapphireActionSystem.CONTEXT_EDITOR_PAGE);
        final SapphireToolBarManagerActionPresentation actionPresentation = new SapphireToolBarManagerActionPresentation(
                getPart(), getSite().getShell(), actions);
        actionPresentation.setToolBarManager(managedForm.getForm().getToolBarManager());
        actionPresentation.render();
    }

    public FormEditorPageDef getDefinition() {
        return (FormEditorPageDef) getPart().getDefinition();
    }

    private static final class FormPageRenderingContext extends SapphireRenderingContext {
        private final FormToolkit toolkit;

        public FormPageRenderingContext(final ISapphirePart part, final IManagedForm managedForm) {
            super(part, managedForm.getForm().getBody());
            this.toolkit = managedForm.getToolkit();
        }

        @Override
        public void adapt(Control control) {
            if (control instanceof Composite) {
                this.toolkit.adapt((Composite) control);
            } else if (control instanceof Label) {
                this.toolkit.adapt(control, false, false);
            } else {
                this.toolkit.adapt(control, true, true);
            }
        }
    }
}
