/* 
 *  controlP5 is a processing gui library.
 * 
 * Copyright (C)  2006-2012 by Andreas Schlegel
 * Copyright (C)  2015 by Jeremy Laviole
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 * 
 * 
 */
package fr.inria.controlP5.gui.controllers;

import fr.inria.controlP5.Hacks;
import fr.inria.controlP5.events.ControlEvent;
import fr.inria.controlP5.events.ControlListener;
import fr.inria.controlP5.ControlP5;
import fr.inria.controlP5.ControlP5Constants;
import fr.inria.controlP5.gui.Controller;
import fr.inria.controlP5.gui.controllers.MultiListInterface;
import fr.inria.controlP5.gui.group.Tab;
import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

/**
 * A Multilist is a multi-menu-tree controller. see the example for more information and how to use.
 * 
 * @example controllers/ControlP5multiList
 * 
 * @nosuperclasses Controller Controller
 */
public class MultiList extends Controller<MultiList> implements MultiListInterface, ControlListener {

	/*
	 * TODO reflection does not work properly. TODO add an option to remove
	 * MultiListButtons
	 */

	protected Tab _myTab;

	protected boolean isVisible = true;

	private int cnt;

	protected boolean isOccupied;

	protected boolean isUpdateLocation = false;

	protected MultiListInterface mostRecent;

	protected int[] _myRect = new int[4];

	protected int _myDirection = ControlP5Constants.RIGHT;

	public int closeDelay = 30;

	protected int _myDefaultButtonHeight = 10;

	protected boolean isUpperCase = true;


	/**
	 * Convenience constructor to extend MultiList.
	 * 
	 * @example use/ControlP5extendController
	 * @param theControlP5
	 * @param theName
	 */
	public MultiList(ControlP5 theControlP5, String theName) {
		this(theControlP5, theControlP5.getDefaultTab(), theName, 0, 0, 99, 19);
		theControlP5.register(theControlP5.getObjectForIntrospection(), theName, this);
	}


	public MultiList(ControlP5 theControlP5, Tab theParent, String theName, int theX, int theY, int theWidth, int theHeight) {
		super(theControlP5, theParent, theName, theX, theY, theWidth, 0);
		_myDefaultButtonHeight = theHeight;
		setup();
	}


	public MultiList toUpperCase(boolean theValue) {
		isUpperCase = theValue;
		for (Controller c : getSubelements()) {
			c.getCaptionLabel().toUpperCase(isUpperCase);
		}
		return this;
	}


	public void setup() {
		mostRecent = this;
		isVisible = true;
		updateRect(position.x, position.y, width, _myDefaultButtonHeight);
	}


	protected void updateRect(float theX, float theY, float theW, float theH) {
		_myRect = new int[] { (int) theX, (int) theY, (int) theW, (int) theH };
	}


	public int getDirection() {
		return _myDirection;
	}


	/**
	 * TODO does not work.
	 * 
	 * @param theDirection
	 */
	void setDirection(int theDirection) {
		_myDirection = (theDirection == LEFT) ? LEFT : RIGHT;
		for (int i = 0; i < getSubelements().size(); i++) {
			((MultiListButton) getSubelements().get(i)).setDirection(_myDirection);
		}
	}


	/**
	 * @param theX float
	 * @param theY float
	 */
	public void updateLocation(float theX, float theY) {
		position.x += theX;
		position.y += theY;
		updateRect(position.x, position.y, width, _myDefaultButtonHeight);
		for (int i = 0; i < getSubelements().size(); i++) {
			((MultiListInterface) getSubelements().get(i)).updateLocation(theX, theY);
		}

	}


	/**
	 * removes the multilist.
	 */
	public void remove() {
		super.remove();
		for (int i = 0; i < getSubelements().size(); i++) {
			getSubelements().get(i).removeListener(this);
			getSubelements().get(i).remove();
		}
	}


	/**
	 * adds multilist buttons to the multilist.
	 * 
	 * @param theName String
	 * @param theValue int
	 * @return MultiListButton
	 */
	public MultiListButton add(String theName, int theValue) {
		int x = (int) position.x;
		int yy = 0;
		for (Controller<?> c : getSubelements()) {
			yy += c.getHeight() + 1;
		}
		int y = (int) position.y + yy;//(_myDefaultButtonHeight + 1) * _myChildren.size();
		MultiListButton b = new MultiListButton(cp5, theName, theValue, x, y, width, _myDefaultButtonHeight, this, this);
		b.toUpperCase(isUpperCase);
		b.isMoveable = false;
		cp5.register(null, "", b);
		b.addListener(this);
		getSubelements().add(b);
		b.show();
		updateRect(position.x, position.y, width, (_myDefaultButtonHeight + 1) * getSubelements().size());
		return b;
	}


	/**
	 * @param theEvent
	 */
	@Override public void controlEvent(ControlEvent theEvent) {
		if (theEvent.getController() instanceof MultiListButton) {
			_myValue = theEvent.getController().getValue();
			ControlEvent myEvent = new ControlEvent(this);
			cp5.getControlBroadcaster().broadcast(myEvent, ControlP5Constants.FLOAT);
		}
	}

	/**
	 * 
	 * @param theApplet
	 * @return boolean
	 */
	public boolean update(PApplet theApplet) {
		if (!isOccupied) {
			cnt++;
			if (cnt == closeDelay) {
				close();
			}
		}

		if (isUpdateLocation) {
			updateLocation((controlWindow.getPointerX() - controlWindow.getPointerPrevX()), (controlWindow.getPointerY() - controlWindow.getPointerPrevY()));
			isUpdateLocation = theApplet.mousePressed;
		}

		if (isOccupied) {
			if (theApplet.keyPressed && theApplet.mousePressed) {
				if (theApplet.keyCode == PApplet.ALT) {
					isUpdateLocation = true;
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * 
	 * @param theFlag boolean
	 */
	public void occupied(boolean theFlag) {
		isOccupied = theFlag;
		cnt = 0;
	}


	/**
	 * @return boolean
	 */
	public boolean observe() {
		return Hacks.inside(_myRect, controlWindow.getPointerX(), controlWindow.getPointerY());
	}


	/**
	 * @param theInterface MultiListInterface
	 */
	public void close(MultiListInterface theInterface) {
		for (int i = 0; i < getSubelements().size(); i++) {
			if (theInterface != (MultiListInterface) getSubelements().get(i)) {
				((MultiListInterface) getSubelements().get(i)).close();
			}
		}

	}


	/**
	 * {@inheritDoc}
	 */
	@Override public void close() {
		for (int i = 0; i < getSubelements().size(); i++) {
			((MultiListInterface) getSubelements().get(i)).close();
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override public void open() {
		for (int i = 0; i < getSubelements().size(); i++) {
			((MultiListInterface) getSubelements().get(i)).open();
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override public MultiList setValue(float theValue) {
		return this;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override public MultiList update() {
		return setValue(_myValue);
	}


	@Deprecated public List<MultiListButton> subelements() {
		System.out.println("controlP5.MultiList.subelements() is deprecated since 0.7.6, use getSubelements().\nFor convenience an empty List is returned here.");
		return new ArrayList<MultiListButton>();
	}


	@Deprecated public List<MultiListButton> getChildren() {
		System.out.println("controlP5.MultiList.getChildren() is deprecated since 0.7.6, use getSubelements().\nFor convenience an empty List is returned here.");
		return new ArrayList<MultiListButton>();
	}

}
