/*
    Naked Objects - a framework that exposes behaviourally complete
    business objects directly to the user.
    Copyright (C) 2000 - 2003  Naked Objects Group Ltd

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

    The authors can be contacted via www.nakedobjects.org (the
    registered address of Naked Objects Group is Kingsway House, 123 Goldworth
    Road, Woking GU21 1NR, UK).
*/

package org.nakedobjects.object.reflect;


import junit.framework.TestSuite;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import org.nakedobjects.object.InvalidEntryException;
import org.nakedobjects.object.MockObjectManager;
import org.nakedobjects.object.NakedClass;
import org.nakedobjects.object.NakedObject;
import org.nakedobjects.object.NakedObjectManager;
import org.nakedobjects.object.NakedObjectTestCase;
import org.nakedobjects.object.ObjectStoreException;
import org.nakedobjects.object.Person;
import org.nakedobjects.object.value.Money;
import org.nakedobjects.object.value.TextString;
import org.nakedobjects.security.SecurityContext;


public class ValueTests extends NakedObjectTestCase {
    private static final String SALARY_FIELD = "Salary";
	private static final String NAME_FIELD = "Name";
	private Person object;
    
    private Value nameField, salaryField;
    
    public ValueTests(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(new TestSuite(ValueTests.class));
    }

    public void setUp()  throws ObjectStoreException {
    	LogManager.getLoggerRepository().setThreshold(Level.OFF);

    	MockObjectManager manager = MockObjectManager.setup();
    	manager.setupAddClass(NakedObject.class);
    	manager.setupAddClass(Person.class);
    	
        object = new Person();
        
        NakedClass c = NakedObjectManager.getInstance().getNakedClass(Person.class.getName());
        
        nameField = (Value) c.getField(NAME_FIELD);
        salaryField = (Value) c.getField(SALARY_FIELD);
    }

    public void testType() {
    	assertEquals(TextString.class, nameField.getType());
    	assertEquals(Money.class, salaryField.getType());
    }
    	
    public void testSetGet() throws InvalidEntryException {
     	nameField.set(object, "Fred");
     	salaryField.set(object, "20.41");
     	
     	assertEquals("Fred", object.getName());
     	assertEquals(20.41, object.getSalary().doubleValue(), 0.001);
    }     	
    
    public void testInitGet() {
    	nameField.initData(object, "Joe");
    	salaryField.initData(object, "18.83");
    
    	assertEquals("Joe", object.getName());
    	assertEquals(18.83, object.getSalary().doubleValue(), 0.001);
    }
    
    public void testName() {
    	assertEquals(NAME_FIELD, nameField.getName());
    	assertEquals(SALARY_FIELD, salaryField.getName());
    }
    
    public void testLabel() {
    	SecurityContext context = new SecurityContext();
    	assertEquals(NAME_FIELD, nameField.getLabel(context, object));
    	assertEquals(SALARY_FIELD, salaryField.getLabel(context, object));
    }
    
    public void testAbout() {
    	assertFalse(nameField.hasAbout());
    	assertTrue(salaryField.hasAbout());

    	assertNotNull(salaryField.getAbout(new SecurityContext(), object));
    }
}
