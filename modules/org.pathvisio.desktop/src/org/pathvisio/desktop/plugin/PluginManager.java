// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.pathvisio.desktop.plugin;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.pathvisio.core.debug.Logger;
import org.pathvisio.desktop.PvDesktop;

/**
 * This class loads and maintains a collection of plugins
 */
public class PluginManager {

	private PvDesktop pvDesktop;
	private List<PluginInfo> plugins;
	private RepositoryManager repositoryManager;
	private List<Plugin> startedPlugins;
	
	
	
	/**
	 * Create a plugin manager that loads plugins from the given locations
	 */
	public PluginManager(PvDesktop pvDesktop) {
		this.pvDesktop = pvDesktop;
		startedPlugins = new ArrayList<Plugin>();
		plugins = new ArrayList<PluginInfo>();
		repositoryManager = new RepositoryManager(pvDesktop);
		repositoryManager.loadRepositories();
		
		startPlugins();
	}
	
	/**
	 * 
	 */
	public void startPlugins() {
		try {
			ServiceReference[] refs = pvDesktop.getContext().getServiceReferences(Plugin.class.getName(), null);
			if(refs != null) {
				for(int i = 0; i < refs.length; i++) {
					Plugin plugin = (Plugin) pvDesktop.getContext().getService(refs[i]);
					if(!startedPlugins.contains(plugin)) {
						PluginInfo pi = new PluginInfo();
						pi.plugin = plugin.getClass();
						pi.param = "";
						pi.jar = refs[i].getBundle().getLocation();
						try {
							plugin.init(pvDesktop);
							startedPlugins.add(plugin);
						} catch (Exception ex) {
							pi.error = ex;
						}
						plugins.add(pi);	
					}
				}
			}
		} catch (InvalidSyntaxException e) {
			Logger.log.error("Couldn't load plugins.");
		}
	}

	/**
	 * Info about a plugin (active or non-active).
	 * Gives info about
	 * <li>from which jar it was loaded, if any
	 * <li>if there were any errors
	 * <li>which parameter caused it to be loaded
	 */
	public static class PluginInfo
	{
		public Class<? extends Plugin> plugin;
		public String jar; // may be null if it wasn't a jar
		public Throwable error; // null if there was no error
		public String param; // parameter that caused this plugin to be loaded
	}

	List<PluginInfo> info = new ArrayList<PluginInfo>();
	public List<PluginInfo> getPluginInfo()
	{
		return info;
	}

	public RepositoryManager getRepositoryManager() {
		return repositoryManager;
	}
}