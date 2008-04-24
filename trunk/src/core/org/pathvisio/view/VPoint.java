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
package org.pathvisio.view;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.HashSet;
import java.util.Set;

import org.pathvisio.model.GraphLink.GraphIdContainer;
import org.pathvisio.model.PathwayElement.MPoint;
import org.pathvisio.preferences.GlobalPreference;
import org.pathvisio.view.LinAlg.Point;

public class VPoint extends VPathwayElement {
	Handle handle;
	Line line;
	MPoint mPoint;
	
	VPoint(VPathway canvas, MPoint mPoint, Line line) {
		super(canvas);
		this.mPoint = mPoint;
		this.line = line;
		handle = new Handle(Handle.DIRECTION_FREE, this, canvas);
	}
	
	protected void link(GraphIdContainer g) {		
		String id = g.getGraphId();
		if(id == null) id = g.setGeneratedGraphId();
		mPoint.setGraphRef(id);
	}

	protected double getVX() { return vFromM(getMPoint().getX()); }
	protected double getVY() { return vFromM(getMPoint().getY()); }
	
	protected void setVLocation(double vx, double vy) {
		mPoint.setX(mFromV(vx));
		mPoint.setY(mFromV(vy));
	}
	
	protected void vMoveBy(double dx, double dy) {
		mPoint.moveBy(mFromV(dx), mFromV(dy));
	}
	
	protected void setHandleLocation() {
		MPoint mp = getMPoint();
		handle.setMLocation(mp.getX(), mp.getY());
	}
	
	public MPoint getMPoint() {
		return mPoint;
	}
	
	public Line getLine() {
		return line;
	}
	
	protected void adjustToHandle(Handle h, double vnewx, double vnewy)
	{
		double mcx = mFromV (vnewx);
		double mcy = mFromV (vnewy);

		if (GlobalPreference.getValueBoolean(GlobalPreference.SNAP_TO_ANGLE) ||
			canvas.isSnapToAngle())
		{
			// get global preference and convert to radians.
			double lineSnapStep = GlobalPreference.getValueInt(
				GlobalPreference.SNAP_TO_ANGLE_STEP) * Math.PI / 180;
			VPoint p1 = line.getStart();
			VPoint p2 = line.getEnd();
			double basex, basey;
			// base is the static point the line rotates about.
			// it is equal to the OTHER point, the one we're not moving.
			if (p1 == this)
			{
				basex = p2.getMPoint().getX();
				basey = p2.getMPoint().getY();
			}
			else
			{
				basex = p1.getMPoint().getX();
				basey = p1.getMPoint().getY();
			}
			// calculate rotation and round it off
			double rotation = Math.atan2(basey - mcy, basex - mcx);
			rotation = Math.round (rotation / lineSnapStep) * lineSnapStep; 
			// project point mcx, mcy on a line with the desired angle.
			Point yr = new Point (Math.cos (rotation), Math.sin (rotation));
			Point prj = LinAlg.project(new Point (basex, basey), new Point(mcx, mcy), yr);
			mcx = prj.x;
			mcy = prj.y;
		}
		
		mPoint.setX(mcx);
		mPoint.setY(mcy);
	}
	
	protected Handle getHandle() {
		return handle;
	}
	
	protected Handle[] getHandles() {
		return new Handle[] { handle };
	}
	
	protected void doDraw(Graphics2D g2d) {
	}

	protected Shape getVOutline() {
		return handle.getVOutline();
	}

	protected void destroy() {
		//Check if we can legally destroy this point
		//TK: Disable this for now, until we implemented poly-lines
//		if(lines.size() > 0) 
//			throw new RuntimeException("VPoint cannot be destroyed: still linked to " + lines);

		super.destroy();
	}
}
