package org.plugin.task.editors;


import java.awt.FileDialog;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.*;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.ide.IDE;
import org.plugin.task.editors.Info;
/**
 * An example showing how to create a multi-page editor.
 * This example has 3 pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class MultiPageEditor extends MultiPageEditorPart implements IResourceChangeListener
{

	/** The text editor used in page 0. */
	private TextEditor editor;

	/**
	 * Creates a multi-page editor example.
	 */
	public MultiPageEditor() 
	{
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}
	/**
	 * Creates page wich allows you to fill processor data and save it
	 */
	void createPage() 
	{

		//ScrolledComposite scrolledComposite = new ScrolledComposite(getContainer(), SWT.V_SCROLL);
        //scrolledComposite.setExpandVertical(true);
        //TODO make page scrollable
		Composite mainComposite = new Composite(getContainer(), SWT.NONE);
		GridLayout mainLayout = new GridLayout();
		mainLayout.numColumns = 1;
		mainComposite.setLayout(mainLayout);
		
		Composite upperComposite = new Composite(mainComposite, SWT.NONE);
		upperComposite.setLayout(mainLayout);
		Label titleLabel = new Label(upperComposite, SWT.NONE);
		titleLabel.setText("Title:");
		Text titleTF = new Text(upperComposite, SWT.NONE);
		Label MemoryLabel = new Label(upperComposite, SWT.NONE);
		MemoryLabel.setText("Memory (name, origin, length):");
		
		Composite memoryComposite = new Composite(mainComposite, SWT.NONE);
		GridLayout memoryLayout = new GridLayout(3, false);
		memoryComposite.setLayout(memoryLayout);
		
		Composite plusComposite = new Composite(mainComposite, SWT.NONE);
		plusComposite.setLayout(mainLayout);
		Button plusButton = new Button(plusComposite, SWT.PUSH);
		GridData plusGD = new GridData(GridData.CENTER);
		plusGD.horizontalSpan = 1;
		plusButton.setLayoutData(plusGD);
		plusButton.setText("+");
		
		List<Text[]> memoryTFs = new ArrayList<Text[]>();
		Text name = new Text(memoryComposite, SWT.NONE);
		Text origin = new Text(memoryComposite, SWT.NONE);
		Text length = new Text(memoryComposite, SWT.NONE);
		memoryTFs.add(new Text[] { name, origin, length });
		
		Composite aliasComposite = new Composite(mainComposite, SWT.NONE); //TODO: Combos
		GridLayout aliasLayout = new GridLayout(2, false);
		aliasComposite.setLayout(aliasLayout);
		Label startupLabel = new Label(aliasComposite, SWT.NONE);
		startupLabel.setText("Startup:");
		Text startupText = new Text(aliasComposite, SWT.NONE);
		Label textLabel = new Label(aliasComposite, SWT.NONE);
		textLabel.setText("Text:");
		Text textText = new Text(aliasComposite, SWT.NONE);
		Label dataLabel = new Label(aliasComposite, SWT.NONE);
		dataLabel.setText("Data:");
		Text dataText = new Text(aliasComposite, SWT.NONE);
		Label sdataLabel = new Label(aliasComposite, SWT.NONE);
		sdataLabel.setText("Sdata:");
		Text sdataText = new Text(aliasComposite, SWT.NONE);
		
		Composite predefineCharComposite = new Composite(mainComposite, SWT.NONE);
		GridLayout predefinedCharLayout = new GridLayout(3, false);
		predefineCharComposite.setLayout(predefinedCharLayout);
		Label stackTopLabel = new Label(predefineCharComposite, SWT.NONE);
		stackTopLabel.setText("__end_heap:");
		Text stackTopText = new Text(predefineCharComposite, SWT.NONE);
		Label additionalLabel = new Label(predefineCharComposite, SWT.NONE);
		additionalLabel.setText("&& -4");
		Label endHeapLabel = new Label(predefineCharComposite, SWT.NONE);
		endHeapLabel.setText("__stack_top:");
		Text endHeapText = new Text(predefineCharComposite, SWT.NONE);
		
		Composite saveComposite = new Composite(mainComposite, SWT.NONE);
		saveComposite.setLayout(mainLayout);
		Button saveButton = new Button(saveComposite, SWT.NONE);
		GridData SaveGD = new GridData(GridData.BEGINNING);
		SaveGD.horizontalSpan = 1;
		saveButton.setLayoutData(SaveGD);
		saveButton.setText("Save file");

		Label messageLabel = new Label(saveComposite, SWT.NONE); //TODO delete err text in time
		
		plusButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent event) 
			{
	    		Text name = new Text(memoryComposite, SWT.NONE);
	    		Text origin = new Text(memoryComposite, SWT.NONE);
	    		Text length = new Text(memoryComposite, SWT.NONE);
	    		
	    		memoryComposite.requestLayout();
	    		memoryTFs.add(new Text[] { name, origin, length });
			}
		});
		
		saveButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent event) 
			{
				if (DataIsCorrect()) 
				{
					Info info = CreateInfo();
					try 
					{
						FillAndSaveFile(info);
					} 
					catch (Exception ex) 
					{
						messageLabel.setText(ex.getMessage());
						saveComposite.requestLayout();
						saveComposite.pack();
					}
				}
			}
			public Info CreateInfo()
			{
				Info info = new Info();
				info.setName(titleTF.getText());
				info.Memory = new HashMap<String, String[]>();
				for(Text[] memoryInfo: memoryTFs)
				{
					String name = 	((Text) memoryInfo[0]).getText();
					String[] originAndLength = new String[] {memoryInfo[1].getText(), memoryInfo[2].getText()};
					info.Memory.put(name, originAndLength);
				}
				info.Aliases = new String[] 
						{ startupText.getText(), textText.getText(), dataText.getText(), sdataText.getText()};
				info.setStackTop(stackTopText.getText());
				info.setEndHeap(endHeapText.getText());
				return info;
			}
			public boolean DataIsCorrect() 
			{
				//TODO
				return true;		
			}
			public void FillAndSaveFile(Info info) 
			{
				List<String> result = new ArrayList<String>();
				//List<String> aliases = new ArrayList<String>();
				result.add("/* " + info.getName() + " */");
				result.add("");
				result.add("MEMORY\r\n{");
				for(Entry<String, String[]> memoryElement: info.Memory.entrySet())
				{
					result.add("\t" + memoryElement.getKey() + " : ORIGIN = " + memoryElement.getValue()[0] + ", LENGTH = " + memoryElement.getValue()[1]);
				}
				result.add("}");
				result.add("");
				result.add("REGION_ALIAS(\"startup\", " + info.Aliases[0] + ")");
				result.add("REGION_ALIAS(\"text\", " + info.Aliases[1] + ")");
				result.add("REGION_ALIAS(\"data\", " + info.Aliases[2] + ")");
				result.add("REGION_ALIAS(\"sdata\", " + info.Aliases[3] + ")");
				result.add("");
				result.add("PROVIDE (__stack_top = (" + info.getStackTop() + " & -4) );");
				result.add("PROVIDE (__end_heap = (" + info.getStackTop() + ") );");
				SaveFile(result);
	    	}
			
			public void SaveFile(List<String> result) 
			{
				FileDialog fileDialog = new FileDialog(new JFrame(), "Save file", FileDialog.SAVE);
				fileDialog.setDirectory("C:\\");
				fileDialog.setVisible(true);
				// TODO File filter (save *.x files)
				List<String> lines = result;
				Path file = Paths.get(fileDialog.getDirectory(), fileDialog.getFile());
				try 
				{
					Files.write(file, lines, Charset.forName("Unicode"));
				} catch (IOException ex) {
					messageLabel.setText(ex.getMessage());
					saveComposite.requestLayout();
	    			saveComposite.pack();
				}
			}
		});
		int index = addPage(mainComposite);
		setPageText(index, "Properties");
	}
	/** 
	 * Gets memory names for alias comboboxes (for now: no comboboxes so no method)
	 
	private String[] GetMemories(List<Text[]> memoryTFs)
	{
		String[] result = new String[memoryTFs.size()];
		int i = 0;
		for(Text[] memoryInfo: memoryTFs)
		{
			result[i] = memoryInfo[0].getText();
		}
		return result;
	}
	*/
	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() 
	{
		createPage();
	}
	/**
	 * The <code>MultiPageEditorPart</code> implementation of this 
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() 
	{
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}
	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) 
	{
		getEditor(0).doSave(monitor);
	}
	/**
	 * Saves the multi-page editor's document as another file.
	 * Also updates the text for page 0's tab, and updates this multi-page editor's input
	 * to correspond to the nested editor's.
	 */
	public void doSaveAs() 
	{
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}
	/* (non-Javadoc)
	 * Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) 
	{
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}
	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
		throws PartInitException 
	{
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}
	/* (non-Javadoc)
	 * Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() 
	{
		return true;
	}
	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	protected void pageChange(int newPageIndex) 
	{
		super.pageChange(newPageIndex);
	}
	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event)
	{
		if(event.getType() == IResourceChangeEvent.PRE_CLOSE)
		{
			Display.getDefault().asyncExec(new Runnable()
			{
				public void run()
				{
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i<pages.length; i++)
					{
						if(((FileEditorInput)editor.getEditorInput()).getFile().getProject().equals(event.getResource()))
						{
							IEditorPart editorPart = pages[i].findEditor(editor.getEditorInput());
							pages[i].closeEditor(editorPart,true);
						}
					}
				}            
			});
		}
	}
}
