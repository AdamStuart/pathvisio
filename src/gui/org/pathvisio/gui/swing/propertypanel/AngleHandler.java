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
package org.pathvisio.gui.swing.propertypanel;

import java.text.NumberFormat;

import org.pathvisio.model.StaticPropertyType;

/**
 * This class knows how to handle angles.
 * Extends NumberHandler to take advantage of its number validation ability.
 *
 * @author Mark Woon
 */
public class AngleHandler extends NumberHandler {

	public AngleHandler() {
		super(StaticPropertyType.ANGLE, Double.class, NumberFormat.getNumberInstance());
	}


	@Override
	Object formatValue(Object value) {
		return (Double)value * 180.0 / Math.PI;
	}

	@Override
	public Object getCellEditorValue() {
		return Double.parseDouble(getTextField().getText()) * Math.PI / 180;
	}
}