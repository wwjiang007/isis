package org.nakedobjects.object;

import org.nakedobjects.object.collection.InstanceCollection;
import org.nakedobjects.object.control.ActionAbout;
import org.nakedobjects.object.control.FieldAbout;
import org.nakedobjects.object.value.TextString;


public class FastFinder extends AbstractNakedObject {
	private final TextString term = new TextString();
	private NakedClass nakedClass;
	
	public String getIconName() {
		return "Find";
	}

	public TextString getTerm() {
		return term;
	}
	
	public void aboutActionFind(ActionAbout about) {
		about.unusableOnCondition(term.isEmpty(), "Search term needed");
	}
	
	public NakedObject actionFind() {
		InstanceCollection instances = InstanceCollection.findInstances(nakedClass, term.stringValue());
		if(instances.size() == 1) { 
			return (NakedObject) instances.elements().nextElement();
		} else {
			return instances;
		}
	}
	
	public void aboutFromClass(FieldAbout about) {
		about.unmodifiable();
	}
	
	public NakedClass getFromClass() {
		return nakedClass;
	}
	
	public void setFromClass(NakedClass nakedClass) {
		this.nakedClass = nakedClass;
	}
	
	public boolean isFinder() {
		return false;
	}
	
	public Title title() {
		return new Title("Search for '").concat(term).concat("'");
	}

}
