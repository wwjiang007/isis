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

package org.nakedobjects.object;


import org.nakedobjects.object.control.About;
import org.nakedobjects.object.control.FieldAbout;
import org.nakedobjects.object.value.Money;
import org.nakedobjects.object.value.TextString;


public class ProductTestObject extends AbstractNakedObject {
    private static final long serialVersionUID = 1L;
    private final TextString name = new TextString();
    private final TextString description = new TextString();
    private final Money price = new Money();

    public About about() {
        return FieldAbout.READ_ONLY;
    }

    public TextString getDescription() {
        return description;
    }

    public TextString getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public Title title() {
        return name.title();
    }
}
