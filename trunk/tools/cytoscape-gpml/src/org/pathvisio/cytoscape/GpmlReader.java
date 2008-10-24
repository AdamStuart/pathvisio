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
package org.pathvisio.cytoscape;

import java.io.File;
import java.io.IOException;

import org.pathvisio.model.Pathway;

import cytoscape.data.readers.AbstractGraphReader;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.LayoutAdapter;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;

public class GpmlReader extends AbstractGraphReader {
	GpmlConverter converter;
	GpmlHandler gpmlHandler;
	
	public GpmlReader(String fileName, GpmlHandler gpmlHandler) {
		super(fileName);
		this.gpmlHandler = gpmlHandler;
	}

	public void read() throws IOException {
		try {
			Pathway pathway = new Pathway();
			pathway.readFromXml(new File(fileName), true);
			converter = new GpmlConverter(gpmlHandler, pathway);
		} catch(Exception ex) {
			ex.printStackTrace();
			throw new IOException(ex.getMessage());
		}
	}
	
	public CyLayoutAlgorithm getLayoutAlgorithm() {
		return new LayoutAdapter() {
			public void doLayout(CyNetworkView networkView, TaskMonitor monitor) {
				converter.layout(networkView);
			}
		};
	}
	
	public int[] getEdgeIndicesArray() {
		return converter.getEdgeIndicesArray();
	}
	
	public int[] getNodeIndicesArray() {
		return converter.getNodeIndicesArray();
	}
		
	public String getNetworkName() {
		String pwName = converter.getPathway().getMappInfo().getMapInfoName();
		return pwName == null ? super.getNetworkName() : pwName;
	}
}