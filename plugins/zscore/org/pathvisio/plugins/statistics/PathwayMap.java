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
package org.pathvisio.plugins.statistics;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bridgedb.Xref;
import org.pathvisio.data.XrefWithSymbol;
import org.pathvisio.debug.Logger;
import org.pathvisio.util.FileUtils;
import org.pathvisio.util.PathwayParser;
import org.pathvisio.util.PathwayParser.ParseException;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A collection of pathways, 
 * parsed quickly using PathwayParser
 */
public class PathwayMap 
{
	public static class PathwayInfo
	{
		Set<Xref> srcRefs;
		String name;
		File file;
	}

	List<PathwayInfo> pathways;	
	
	PathwayMap(File pwDir)
	{
		try
		{
			xmlReader = XMLReaderFactory.createXMLReader();
		}
		catch (SAXException e)
		{
			Logger.log.error("Problem while searching pathways", e);
			throw new IllegalStateException(); // TODO: more info in exception
		}
		
		doGetSrcRefs(pwDir);
	}
	
	private XMLReader xmlReader = null;

	private void doGetSrcRefs(File pwDir)
	{
		pathways = new ArrayList<PathwayInfo>();
		
		for (File file : FileUtils.getFiles(pwDir, "gpml", true))
		{
			try
			{
				PathwayParser pwyParser = new PathwayParser(file, xmlReader);
				
				Logger.log.info ("Reading references from " + pwyParser.getName());
				
				Set<Xref> srcRefs = new HashSet<Xref>();
				for (XrefWithSymbol x : pwyParser.getGenes()) srcRefs.add (x.asXref());
				
				PathwayInfo pi = new PathwayInfo();
				pi.name = pwyParser.getName();
				pi.srcRefs = srcRefs;
				pi.file = file;
				pathways.add (pi);
			}
			catch (ParseException ex)
			{
				// ignore files that are not valid gpml, just skip
			}
		}
	}
	

	public Set<Xref> getSrcRefs()
	{
		Set<Xref> result = new HashSet<Xref>();
		for (PathwayInfo pi : pathways)
		{
			result.addAll (pi.srcRefs);
		}
		return result;
	}
}
