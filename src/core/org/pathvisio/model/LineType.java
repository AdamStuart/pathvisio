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
package org.pathvisio.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LineType 
{
	private static Map<String, LineType> mappMappings = new HashMap<String, LineType>();
	private static Map<String, LineType> nameMappings = new HashMap<String, LineType>();
	private static List<LineType> values = new ArrayList<LineType>();

	public static final LineType LINE = new LineType("Line", "Line");
	public static final LineType ARROW = new LineType("Arrow", "Arrow");
	public static final LineType TBAR = new LineType("TBar", "TBar");
	public static final LineType RECEPTOR = new LineType("Receptor", "Receptor");
	public static final LineType LIGAND_SQUARE = new LineType("LigandSquare","LigandSq");
	public static final LineType RECEPTOR_SQUARE = new LineType("ReceptorSquare", "ReceptorSq");
	public static final LineType LIGAND_ROUND = new LineType("LigandRound", "LigandRd");
	public static final LineType RECEPTOR_ROUND = new LineType("ReceptorRound", "ReceptorRd");

	/**
	   mappName may be null for new shapes that don't have a .mapp
	   equivalent.
	 */
	private LineType (String name, String mappName)
	{
		if (name == null) { throw new NullPointerException(); }
		
		this.mappName = mappName; 
		this.name = name;

		if (mappName != null)
		{
			mappMappings.put (mappName, this);
		}
		nameMappings.put (name, this);

		// assign an integer value
		value = values.size();
		// and add it tot hte array list.
		values.add (this);
	}

	/**
	   Create an object and add it to the list.

	   For extending the enum.
	 */
	public static LineType create (String name, String mappName)
	{
		if (nameMappings.containsKey (name))
		{
			return nameMappings.get (name);
		}
		else
		{
			return new LineType (name, mappName);
		}
	}
	
	private String mappName;
	private String name;

	private int value;
	
	public String getMappName() { return mappName; }
	
	/** @deprecated, use getName instead. */
	public String getGpmlName() { return getName(); }
	
	public String getName() { return name; }

	/**
	   @deprecated
	 */
	static LineType fromGpmlName(String value)
	{
		return fromName (value);
	}

	public static LineType fromName(String value)
	{
		return nameMappings.get (value);
	}

	/**
	   The ordinal value of this. Not guaranteed to be the same between
	   application runs, only to be used for temporary lookup such as
	   in comboboxes.
	   If you need something that is stable, use the name instead.

	   //TODO: we should make change comboboxes so they don't need
	   integer values anymore
	   and get rid of this.
	 */
	public int getOrdinal () { return value; }

	public static LineType fromOrdinal (int value)
	{
		return values.get (value);
	}

	static public String[] getNames()
	{
		String[] result = new String [values.size()];

		for (int i = 0; i < values.size(); ++i)
		{
			result[i] = values.get(i).getName();
		}
		return result;
	}

	static public LineType[] getValues()
	{
		return values.toArray (new LineType[0]);
	}

	public String toString()
	{
		return name;
	}
}
