/******************************************************************************
 * Copyright (c) 2012 Oracle and Liferay
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 *    Shenxue Zhou - [365019] SapphireDiagramEditor does not work on non-workspace files 
 *    Gregory Amerson - [371576] Support non-local files in SapphireDiagramEditor
 ******************************************************************************/

package org.eclipse.sapphire.ui.gef.diagram.editor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.StatusException;
import org.eclipse.sapphire.modeling.util.internal.FileUtil;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 */

public class SapphireDiagramEditorFactory 
{
    public static final String SAPPHIRE_DIAGRAM_TYPE = "sapphireDiagram";
        
    public static SapphireDiagramEditorInput createEditorInput(IEditorInput input)
    		throws StatusException, CoreException, IOException
    {
    	return createEditorInput(input, null, false);
    }
    
    public static SapphireDiagramEditorInput createEditorInput(IEditorInput input, String diagramPageId, boolean sideBySideLayoutFile)
    	throws StatusException, CoreException, IOException
    {
    	if (input instanceof FileEditorInput)
    	{
    		FileEditorInput fileEditorInput = (FileEditorInput)input;
    		IFile ifile = fileEditorInput.getFile();
    		return createEditorInput(ifile, diagramPageId, sideBySideLayoutFile);
    	}
    	else if (input instanceof FileStoreEditorInput)
    	{
    		FileStoreEditorInput fileStoreInput = (FileStoreEditorInput)input;
    		return createEditorInput(fileStoreInput.getURI(), diagramPageId, sideBySideLayoutFile);
    	}
		else if ( input instanceof IStorageEditorInput )
		{
			IStorageEditorInput storageEditorInput = (IStorageEditorInput) input;
			return createEditorInput( storageEditorInput.getStorage(), diagramPageId, sideBySideLayoutFile );
		}
		else
		{
			// Could not determine the layout file folder, we'll not persist the layout and 
			// let auto layout do the work.
			return createNoLayoutEditorInput();
		}
    }
    
    public static SapphireDiagramEditorInput createEditorInput(IFile file)
            throws StatusException, CoreException
    {
        return createEditorInput(file, null, false);
    }

    public static SapphireDiagramEditorInput createEditorInput(IFile file, String diagramPageId, boolean sideBySideLayoutFile) 
        throws StatusException, CoreException
    {
        if (file == null)
            return null;
        
        IProject project = file.getProject();
        IPath inputFilePath = file.getProjectRelativePath().removeLastSegments(1);        
        
        String fileName;
        String inputFileName = file.getName();
        if (inputFileName.endsWith(".xml"))
        {
            fileName = inputFileName.substring(0, inputFileName.indexOf(".xml"));
        }
        else
        {
            fileName = inputFileName;
        }
        if (diagramPageId != null)
        {
            fileName += "_" + diagramPageId;
        }
        
        // compute layout folder path
        
        IFolder layoutFolder;
        if (!sideBySideLayoutFile)
        {
            IFolder diagramSettingRootFolder = project.getFolder(".settings/org.eclipse.sapphire.ui.diagram/");                    
            IPath diagramSettingFolderPath = diagramSettingRootFolder.getProjectRelativePath().append(inputFilePath);
            IFolder diagramSettingFolder = project.getFolder(diagramSettingFolderPath);
            if (!diagramSettingFolder.exists())
            {
                FileUtil.mkdirs(diagramSettingFolder.getLocation().toFile());
                diagramSettingFolder.refreshLocal(IResource.DEPTH_ONE, null);
            }
            layoutFolder = diagramSettingFolder;
        }
        else
        {
            layoutFolder = (IFolder)file.getParent();
        }
        
        // create diagram layout file if it doesn't exist
        boolean existingLayout = true;
        IFile layoutFile = layoutFolder.getFile(fileName + ".layout");
        if (!layoutFile.exists())
        {
            layoutFile.create(new ByteArrayInputStream(new byte[0]), true, null);
            existingLayout = false;
        }

        final SapphireDiagramEditorInput diagramEditorInput = SapphireDiagramEditorInput.createEditorInput(SAPPHIRE_DIAGRAM_TYPE);
        diagramEditorInput.setLayoutFile(layoutFile.getLocation().toFile());
        diagramEditorInput.setNoExistingLayout(!existingLayout);
        return diagramEditorInput;
    }

    public static SapphireDiagramEditorInput createEditorInput(URI uri, String diagramPageId, boolean sideBySideLayoutFile) 
            throws StatusException, CoreException, IOException
    {
    	IFileStore store = EFS.getStore(uri);
    	File localFile = store.toLocalFile(EFS.NONE, null);
		//if no local file is available, obtain a cached file
		if (localFile == null)
			localFile = store.toLocalFile(EFS.CACHE, null);
		if (localFile == null)
			throw new IllegalArgumentException();
		
        String fileName;
        String inputFileName = localFile.getName();
        if (inputFileName.endsWith(".xml"))
        {
            fileName = inputFileName.substring(0, inputFileName.indexOf(".xml"));
        }
        else
        {
            fileName = inputFileName;
        }
        if (diagramPageId != null)
        {
            fileName += "_" + diagramPageId;
        }
        
        // compute layout folder path
        File layoutFolder;
        if (!sideBySideLayoutFile)
        {
	        String parentPath = localFile.getParentFile().getCanonicalPath();
	        int index = parentPath.indexOf(':');
	        if (index != 0)
	        {
	        	parentPath = parentPath.substring(0, index) + parentPath.substring(index + 1);
	        }
	        File file = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
	        file = new File(file, ".metadata/.plugins/org.eclipse.sapphire.ui.gef.diagram.editor/layout");
	        layoutFolder = new File(file, parentPath);
	        if (!layoutFolder.exists())
	        {
	        	FileUtil.mkdirs(layoutFolder);
	        }	        
        }
        else
        {
        	layoutFolder = localFile.getParentFile();
        }
		return createEditorInputFromPath( fileName, layoutFolder );
	}

	public static SapphireDiagramEditorInput createEditorInput(
		IStorage storage, String diagramPageId, boolean sideBySideLayoutFile ) throws StatusException, CoreException,
		IOException
	{
		IPath storagePath = storage.getFullPath();
		if (storagePath == null)
		{
			return createNoLayoutEditorInput();
		}
		String fileName = storagePath.lastSegment();

		String parentPath = storagePath.toOSString();
		int index = parentPath.indexOf( ':' );
		if ( index > 0 )
		{
			parentPath = parentPath.substring( 0, index ) + parentPath.substring( index + 1 );
		}
        File file = ResourcesPlugin.getWorkspace().getRoot().getLocation().toFile();
        file = new File(file, ".metadata/.plugins/org.eclipse.sapphire.ui.gef.diagram.editor/layout");
        File layoutFolder = new File(file, parentPath);
        if (!layoutFolder.exists())
        {
        	FileUtil.mkdirs(layoutFolder);
        }

		return createEditorInputFromPath( fileName, layoutFolder );
	}

	private static SapphireDiagramEditorInput createEditorInputFromPath( String fileName, File layoutFolder )
		throws StatusException, IOException
	{
        // create diagram layout file if it doesn't exist
        boolean existingLayout = true;
        File layoutFile = new File(layoutFolder, fileName + ".layout");
        if (!layoutFile.exists())
        {
            layoutFile.createNewFile();
            existingLayout = false;
        }

        final SapphireDiagramEditorInput diagramEditorInput = SapphireDiagramEditorInput.createEditorInput(SAPPHIRE_DIAGRAM_TYPE);
        diagramEditorInput.setLayoutFile(layoutFile);
        diagramEditorInput.setNoExistingLayout(!existingLayout);
        return diagramEditorInput;

    }
        
	private static SapphireDiagramEditorInput createNoLayoutEditorInput()
	{
        final SapphireDiagramEditorInput diagramEditorInput = SapphireDiagramEditorInput.createEditorInput(SAPPHIRE_DIAGRAM_TYPE);
        diagramEditorInput.setNoExistingLayout(true);
        return diagramEditorInput;		
	}
}
