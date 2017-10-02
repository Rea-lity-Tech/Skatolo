/* 
 *  skatolo is a processing gui library.
 * 
 * Copyright (C)  2017 by RealityTechSASU
 * Copyright (C)  2015-2016 by Jeremy Laviole
 * Copyright (C)  2006-2012 by Andreas Schlegel
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
package tech.lity.rea.skatolo.events;

import tech.lity.rea.skatolo.Skatolo;
import tech.lity.rea.skatolo.SkatoloConstants;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.security.AccessControlException;

/**
 * The ControllerPlug is used to do all the reflection procedures to link a controller to a variable or function inside your main
 * application.
 * 
 * @example use/skatoloplugTo
 */
public class ControllerPlug {

	private Object _myObject;
	private String _myName;
	private Method _myMethod;
	private Field _myField;

	private int _myType = SkatoloConstants.INVALID;
	
        private Class<?> _myParameterClass;
	
        private int _myParameterType = -1;
	private Object _myValue = null;

        private Class<?>[] _myAcceptClassList;
	private Class<?> _myEventMethodParameter = ControlEvent.class;

	public ControllerPlug(final Object theObject, final String theName, final int theType, final int theParameterType, Class<?>[] theAcceptClassList) {
		set(theObject, theName, theType, theParameterType, theAcceptClassList);
	}

	ControllerPlug(Class<?> param, final Object theObject, final String theName, final int theType, final int theParameterType) {
		setEventMethodParameter(param);
		set(theObject, theName, theType, theParameterType, null);
	}

	void setEventMethodParameter(Class<?> theClass) {
		_myEventMethodParameter = theClass;
	}

	protected void set(Object theObject) {
		set(theObject, getName(), getType(), getParameterType(), getAcceptClassList());
	}

	public void set(final Object theObject, final String theName, final int theType, final int theParameterType, final Class<?>[] theAcceptClassList) {
		_myObject = theObject;
		_myName = theName;
		_myType = theType;
		_myParameterType = theParameterType;
		_myAcceptClassList = theAcceptClassList;
		Class<?> myClass = theObject.getClass();

		/* check for methods */
		if (_myType == SkatoloConstants.METHOD) {
			try {
				Method[] myMethods = myClass.getDeclaredMethods();
				for (int i = 0; i < myMethods.length; i++) {
					if ((myMethods[i].getName()).equals(theName)) {
						if (myMethods[i].getParameterTypes().length == 1) {
							for (int j = 0; j < _myAcceptClassList.length; j++) {
								if (myMethods[i].getParameterTypes()[0] == _myAcceptClassList[j]) {
									_myParameterClass = myMethods[i].getParameterTypes()[0];
									break;
								}
							}
						} else if (myMethods[i].getParameterTypes().length == 0) {
							_myParameterClass = null;
							break;
						}
						break;
					}
				}
				Class<?>[] myArgs = (_myParameterClass == null) ? new Class[] {} : new Class[] { _myParameterClass };
				_myMethod = myClass.getDeclaredMethod(_myName, myArgs);
				_myMethod.setAccessible(true);
			} catch (SecurityException e) {
				printSecurityWarning(e);
			} catch (NoSuchMethodException e) {
				if (_myParameterClass != CallbackEvent.class) {
					Skatolo.logger().warning(" plug() failed. If function " + theName + " does exist, make it public. " + e);
				}
			}


			/* check for controlEvent */
		} else if (_myType == SkatoloConstants.EVENT) {
			try {

				_myMethod = _myObject.getClass().getMethod(_myName, new Class[] { _myEventMethodParameter });
				_myMethod.setAccessible(true);
				_myParameterClass = _myEventMethodParameter;
			} catch (SecurityException e) {
				printSecurityWarning(e);
			} catch (NoSuchMethodException e) {
				if (_myEventMethodParameter != CallbackEvent.class) {
					Skatolo.logger().warning(" plug() failed " + _myParameterClass + ". If function " + theName + " does exist, make it public. " + e);
				}
			}
			/* check for fields */
		} else if (_myType == SkatoloConstants.FIELD) {

			Field[] myFields = ControlBroadcaster.getFieldsFor(myClass);

			for (int i = 0; i < myFields.length; i++) {
				if (myFields[i].getName().equals(_myName)) {
					_myParameterClass = myFields[i].getType();
				}
			}
			if (_myParameterClass != null) {
				/**
				 * note. when running in applet mode. for some reason setAccessible(true) works for methods but not for fields.
				 * theAccessControlException is thrown. therefore, make fields in your code public.
				 */

				try {
					_myField = myClass.getDeclaredField(_myName);
					try {
						_myField.setAccessible(true);
					} catch (java.security.AccessControlException e) {
						printSecurityWarning(e);
					}
					try {
						_myValue = (_myField.get(theObject));
					} catch (Exception ex) {
						printSecurityWarning(ex);
					}
				} catch (NoSuchFieldException e) {
					Skatolo.logger().warning(e.toString());
				}
			}
		}
	}

	private void printSecurityWarning(Exception e) {
		// AccessControlException required for applets.
		if (e.getClass().equals(AccessControlException.class)) {
			Skatolo.isApplet = true;
			Skatolo.logger().warning("You are probably running in applet mode.\n" + "make sure fields and methods in your code are public.\n" + e);
		}
	}

	public Object getValue() {
		return _myValue;
	}

	public Object getObject() {
		return _myObject;
	}

	public String getName() {
		return _myName;
	}

	protected int getType() {
		return _myType;
	}

	protected int getParameterType() {
		return _myParameterType;
	}

	protected Class<?>[] getAcceptClassList() {
		return _myAcceptClassList;
	}

	public Class<?> getClassType() {
		return _myParameterClass;
	}

	protected boolean checkType(int theType) {
		return _myType == theType;
	}

	protected boolean checkName(String theName) {
		return (_myName.equals(theName));
	}

	private Object get(float theValue) {
		if (_myParameterClass == float.class) {
			return new Float(theValue);
		} else if (_myParameterClass == int.class) {
			return new Integer((int) theValue);
		} else if (_myParameterClass == boolean.class) {
			return (theValue > 0.5) ? new Boolean(true) : new Boolean(false);
		} else {
			return null;
		}
	}

	protected Object getFieldParameter(float theValue) {
		return get(theValue);
	}

	protected Object[] getMethodParameter(float theValue) {
		return new Object[] { get(theValue) };
	}

	protected Method getMethod() {
		return _myMethod;
	}

	protected Field getField() {
		return _myField;
	}

	static public boolean checkPlug(Object theObject, String thePlugName, Class<?>[] theArgs) {
		try {
			theObject.getClass().getDeclaredMethod(thePlugName, theArgs);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}