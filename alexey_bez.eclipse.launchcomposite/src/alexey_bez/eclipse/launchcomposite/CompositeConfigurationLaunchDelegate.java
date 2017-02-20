package alexey_bez.eclipse.launchcomposite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

public class CompositeConfigurationLaunchDelegate implements ILaunchConfigurationDelegate {

	public static final String LAUNCH_LIST = "alexey_bez.eclipse.launchcomposite.configuration.launch_list"; 
	
	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		List<String> launchList = configuration.getAttribute(LAUNCH_LIST, new ArrayList<String>());
		
		DebugPlugin debugPlugin = DebugPlugin.getDefault();
		ILaunchManager launchManager = debugPlugin.getLaunchManager();
				
		for(String id : launchList) {
			ILaunchConfiguration child = launchManager.getLaunchConfiguration(id);
			if(child == null)
				throw new CoreException(new Status(IStatus.ERROR, "alexey_bez.eclipse.launchcomposite", "Unable to find launch configuration: " + id));
			child.launch(mode,  monitor);
		}
	}
	
}
