package org.nakedobjects.viewer.skylark.example.border;
import org.nakedobjects.viewer.skylark.Canvas;
import org.nakedobjects.viewer.skylark.Click;
import org.nakedobjects.viewer.skylark.Color;
import org.nakedobjects.viewer.skylark.Drag;
import org.nakedobjects.viewer.skylark.DragStart;
import org.nakedobjects.viewer.skylark.Location;
import org.nakedobjects.viewer.skylark.Size;
import org.nakedobjects.viewer.skylark.ViewAxis;
import org.nakedobjects.viewer.skylark.core.AbstractView;


public class TestHeaderView extends AbstractView {

    private int requiredWidth;
    private int requiredHeight;

    public TestHeaderView(ViewAxis axis, int width, int height) {
        super(null, null, axis);
        setRequiredSize(new Size(width, height));
    }
    
    public void draw(Canvas canvas) {
        super.draw(canvas);       
        int width = getSize().getWidth();
        int height = getSize().getHeight();
        canvas.clearBackground(this, Color.GRAY);
        canvas.drawRectangle(0,0, width - 1, height - 1, Color.BLUE);
        canvas.drawLine(0, 0, width - 1, height - 1, Color.ORANGE);
        canvas.drawLine(width - 1, 0, 0, height - 1, Color.ORANGE);
    }
    
    public Size getRequiredSize() {
        return new Size(requiredWidth, requiredHeight);
    }
    
    public void setRequiredSize(Size size) {
        requiredHeight = size.getHeight();
        requiredWidth = size.getWidth();
        
        setSize(size);
    }

    public void firstClick(Click click) {
        debug("first click " + click);
        super.firstClick(click);
    }

    public void secondClick(Click click) {
        debug("second click " + click);
        super.secondClick(click);
    }
    
    public void mouseMoved(Location location) {
        debug("mouse moved " + location);
        super.mouseMoved(location);
    }
    
    private void debug(String str) {
        getViewManager().getSpy().addAction(str);
    }
    
    public Drag dragStart(DragStart drag) {
        debug("drag start " + drag);
        return super.dragStart(drag);
    }
}


/*
Naked Objects - a framework that exposes behaviourally complete
business objects directly to the user.
Copyright (C) 2000 - 2005  Naked Objects Group Ltd

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