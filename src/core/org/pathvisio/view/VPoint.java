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

import org.pathvisio.model.PathwayElement.MPoint;

public class VPoint extends VPathwayElement {
	Handle handle;
	Set<Line> lines;
	Set<MPoint> mPoints;
	
	VPoint(VPathway canvas) {
		super(canvas);
		mPoints = new HashSet<MPoint>();
		lines = new HashSet<Line>();
		handle = new Handle(Handle.DIRECTION_FREE, this, canvas);
	}
	
	public int getDrawingOrder() {
		return VPathway.DRAW_ORDER_LINE;
	}
	
	protected void addMPoint(MPoint p) {
		mPoints.add(p);
	}
	
	protected void removeMPoint(MPoint p) {
		mPoints.remove(p);
	}
	
	protected void addLine(Line l) {
		lines.add(l);
	}
	
	protected void removeLine(Line l) {
		lines.remove(l);
		//Remove this VPoint when it links to no lines no more
		if(lines.size() == 0) {
			destroy();
		}
	}
	
	protected Set<Line> getLines() { return lines; }
	
	protected void link(Graphics g) {
		if(lines.contains(g)) return; //Prevent linking to self
		String id = g.getGmmlData().getGraphId();
		if(id == null) id = g.getGmmlData().setGeneratedGraphId();
		for(MPoint p : mPoints) p.setGraphRef(id);
	}
	
	protected void link(VPoint p) {
		if(p == this) return; //Already linked
		for(MPoint mp : p.mPoints) {
			mPoints.add(mp);
		}
		for(Line l : p.lines) {
			l.swapPoint(p, this);
			addLine(l);
		}
		p.lines.clear();
		p.destroy();
	}
	
	protected double getVX() { return vFromM(getMPoint().getX()); }
	protected double getVY() { return vFromM(getMPoint().getY()); }
	
	protected void setVLocation(double vx, double vy) {
		for(MPoint p : mPoints) {
			p.setX(mFromV(vx));
			p.setY(mFromV(vy));
		}
	}
	
	protected void vMoveBy(double dx, double dy) {
		for(MPoint p : mPoints) {
			p.moveBy(mFromV(dx), mFromV(dy));
		}
	}
	
	protected void setHandleLocation() {
		MPoint mp = getMPoint();
		handle.setMLocation(mp.getX(), mp.getY());
	}
	
	private MPoint getMPoint() {
		for(MPoint p : mPoints) return p;
		return null;
	}
	
	protected void adjustToHandle(Handle h, double vnewx, double vnewy)
	{
		double mcx = mFromV (vnewx);
		double mcy = mFromV (vnewy);
		for(MPoint p : mPoints)
		{
			p.setX(mcx);
			p.setY(mcy);
		}
	}
	
	protected Handle getHandle() {
		return handle;
	}
	
	protected Handle[] getHandles() {
		return new Handle[] { handle };
	}
	
	protected void doDraw(Graphics2D g2d) {
		// TODO Auto-generated method stub
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
