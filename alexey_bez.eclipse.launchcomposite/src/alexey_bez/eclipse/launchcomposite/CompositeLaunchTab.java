package alexey_bez.eclipse.launchcomposite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;


public class CompositeLaunchTab extends AbstractLaunchConfigurationTab {

	private ListViewer viewer;
	
	@Override
	public String getName() {
		return "NAME";
	}
	
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			DebugPlugin debugPlugin = DebugPlugin.getDefault();
			ILaunchManager launchManager = debugPlugin.getLaunchManager();
			List<String> children = configuration.getAttribute(CompositeConfigurationLaunchDelegate.LAUNCH_LIST, Collections.emptyList());
			List<ILaunchConfiguration> childConfigurations = new ArrayList<>();
			for(String child : children) {
				childConfigurations.add(launchManager.getLaunchConfiguration(child));
			}
			viewer.setSelection(new StructuredSelection(childConfigurations));
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
		List<String> children = new ArrayList<>();
		for(Object obj : selection.toArray()) {
			ILaunchConfiguration config = (ILaunchConfiguration) obj;
			try {
				children.add(config.getMemento());
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		configuration.setAttribute(CompositeConfigurationLaunchDelegate.LAUNCH_LIST, children);
	} 
	
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(CompositeConfigurationLaunchDelegate.LAUNCH_LIST, Collections.emptyList());
	}

	@Override
	public void createControl(Composite parent) {
		viewer = new ListViewer(parent);
        viewer.setContentProvider(ArrayContentProvider.getInstance());
        viewer.setLabelProvider(new LaunchLabelProvider());
        viewer.setInput(listConfigurations());
        
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				setDirty(true);
				scheduleUpdateJob();
			}
		});
        
        setControl(viewer.getControl());
	}

	private ILaunchConfiguration[] listConfigurations() {
		DebugPlugin debugPlugin = DebugPlugin.getDefault();
		ILaunchManager manager = debugPlugin.getLaunchManager();
		try {
			return manager.getLaunchConfigurations();
		} catch (CoreException e) {
			e.printStackTrace();
			return new ILaunchConfiguration[0];
		}
	}
	
	private class LaunchLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			ILaunchConfiguration config = (ILaunchConfiguration)element;
			try {
				ILaunchConfigurationType type = config.getType();
				return type.getName() + "/" + config.getName();
			} catch (CoreException e) {
				e.printStackTrace();
				return "ERROR";
			}
		}
	}
}
