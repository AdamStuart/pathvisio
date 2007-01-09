// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2007 BiGCaT Bioinformatics
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
package visualization.colorset;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import visualization.VisualizationManager;
import visualization.VisualizationManager.VisualizationEvent;

public class ColorSetManager {
	public final static String XML_ELEMENT = "color-sets";

	private static List<ColorSet> colorSets = new ArrayList<ColorSet>();

	/**
	 * Gets the {@link ColorSet}s used for the currently loaded Expression data
	 */
	public static List<ColorSet> getColorSets() { return colorSets; }

	public static boolean nameExists(String name) {
		for(ColorSet cs : colorSets) 
			if(cs.getName().equalsIgnoreCase(name)) return true;
		return false;
	}

	public static String getNewName() {
		String prefix = "color set";
		int i = 1;
		String name = prefix;
		while(nameExists(name)) name = prefix + "-" + i++;
		return name;
	}

	public static void newColorSet(String name) {
		if(name == null) name = getNewName();
		addColorSet(new ColorSet(name));
		
	}
	
	public static void addColorSet(ColorSet cs) {
		colorSets.add(cs);
		VisualizationManager.fireVisualizationEvent(
				new VisualizationEvent(null, VisualizationEvent.COLORSET_ADDED));
	}

	/**
	 * Removes this {@link ColorSet}
	 * @param cs Colorset to remove
	 */
	public static void removeColorSet(ColorSet cs) {
		if(colorSets.contains(cs)) {
			colorSets.remove(cs);
			VisualizationManager.fireVisualizationEvent(
					new VisualizationEvent(null, VisualizationEvent.COLORSET_REMOVED));
		}
	}
	
	/**
	 * Clears all color-set information
	 */
	public static void clearColorSets() {
		colorSets.clear();
		VisualizationManager.fireVisualizationEvent(
				new VisualizationEvent(null, VisualizationEvent.COLORSET_REMOVED));
	}
	
	public static ColorSet getColorSet(int index) {
		if(index >= 0 && index < colorSets.size())
			return colorSets.get(index);
		else return null;
	}

	/**
	 * Removes this {@link ColorSet}
	 * @param i index of ColorSet to remove
	 */
	public static void removeColorSet(int i) {
		if(i > -1 && i < colorSets.size()) {
			removeColorSet(colorSets.get(i));
		}
	}

	/**
	 * Gets the names of all {@link ColorSet}s used 
	 * @return
	 */
	public static String[] getColorSetNames()
	{
		String[] colorSetNames = new String[colorSets.size()];
		for(int i = 0; i < colorSetNames.length; i++)
		{
			colorSetNames[i] = ((ColorSet)colorSets.get(i)).getName();
		}
		return colorSetNames;
	}

	public static Element getXML() {
		Element cse = new Element(XML_ELEMENT);
				
		for(ColorSet cs : colorSets) cse.addContent(cs.toXML());
		
		return cse;
	}

	public static void fromXML(Element xml) {
		clearColorSets();
		
		if(xml == null) return;

		for(Object o : xml.getChildren(ColorSet.XML_ELEMENT)) {
			addColorSet(ColorSet.fromXML((Element) o));				
		}
	}

	static Document parseInput(InputStream in) throws JDOMException, IOException {
		SAXBuilder parser = new SAXBuilder();
		return parser.build(in);
	}
}
