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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Status.CompositeStatusFactory;
import org.eclipse.sapphire.ui.def.FormEditorPageDef;
import org.eclipse.sapphire.ui.def.FormEditorSectionDef;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

/**
 * @author <a href="mailto:rcernich@redhat.com">Rob Cernich</a>
 * @author <a href="mailto:konstantin.komissarchik@oracle.com">Konstantin Komissarchik</a> 
 */

public class StandardFormEditorPagePart extends SapphireEditorPagePart {

    private Status status = Status.createOkStatus();
    private List<SapphireSection> sectionParts;

    @Override
    protected void init() {
        super.init();
        
        final SapphirePartListener listener = new SapphirePartListener() {

            @Override
            public void handleEvent(SapphirePartEvent event) {
                StandardFormEditorPagePart.this.notifyListeners(event);
            }

            @Override
            public void handleValidateStateChange(Status oldValidateState, Status newValidationState) {
                StandardFormEditorPagePart.this.updateValidationState();
            }

            @Override
            public void handleStructureChangedEvent(SapphirePartEvent event) {
                StandardFormEditorPagePart.this.notifyStructureChangedEventListeners(event);
            }

            @Override
            public void handleFocusReceivedEvent(SapphirePartEvent event) {
                StandardFormEditorPagePart.this.notifyFocusRecievedEventListeners();
            }
        };

        ModelElementList<FormEditorSectionDef> sectionDefs = getEditorDefinition().getSections();
        this.sectionParts = new ArrayList<SapphireSection>(sectionDefs.size());
        for (FormEditorSectionDef sectionDef : sectionDefs) {
            SapphireSection sectionPart = new SapphireSection();
            sectionPart.init(this, getModelElement(), sectionDef, Collections.<String, String>emptyMap());
            sectionPart.addListener(listener);
            this.sectionParts.add(sectionPart);
        }
    }

    protected FormEditorPageDef getEditorDefinition() {
        return (FormEditorPageDef) getDefinition();
    }

    @Override
    protected Status computeValidationState() {
        CompositeStatusFactory factory = Status.factoryForComposite();
        for (SapphireSection section : this.sectionParts) {
            factory.merge(section.getValidationState());
        }
        this.status = factory.create();
        return this.status;
    }

    @Override
    public void render(SapphireRenderingContext context) {
        context = new SapphireRenderingContext(this, context, context.getComposite());
        for (SapphireSection section : this.sectionParts) {
            section.render(context);
        }
        // HACK: patch up layout data
        int sectionIndex = 0;
        int sectionCount = this.sectionParts.size();
        for (Control control : context.getComposite().getChildren()) {
            if (control instanceof Section) {
                if (sectionIndex < sectionCount) {
                    FormEditorSectionDef sectionDef = (FormEditorSectionDef) this.sectionParts.get(
                            sectionIndex).getDefinition();
                    try {
                        control.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, sectionDef
                                .getScaleVertically().getContent() ? TableWrapData.FILL_GRAB : TableWrapData.FILL,
                                sectionDef.getRowSpan().getContent(), sectionDef.getColumnSpan().getContent()));
                    } catch (Exception e) {
                    }
                    ++sectionIndex;
                } else {
                    break;
                }
            }
        }
    }

    @Override
    public void dispose() {
        for (SapphireSection section : this.sectionParts) {
            try {
                section.dispose();
            } catch (Exception e) {
            }
        }
        this.sectionParts.clear();
        super.dispose();
    }

}
