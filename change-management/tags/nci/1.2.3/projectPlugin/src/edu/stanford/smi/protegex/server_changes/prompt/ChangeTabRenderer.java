package edu.stanford.smi.protegex.server_changes.prompt;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.DefaultRenderer;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.server_changes.model.generated.Ontology_Component;


public class ChangeTabRenderer extends DefaultRenderer {
    private static final long serialVersionUID = -6563461006874742252L;
    
    private KnowledgeBase old_kb, new_kb;
    
    private Ontology_Component frame;

    private boolean _underline = false;
    private boolean _strikeOut = false;
    
    public ChangeTabRenderer(KnowledgeBase old_kb, KnowledgeBase new_kb){
        super();
        this.old_kb = old_kb;
        this.new_kb = new_kb;
    }

    public Color getTextColor() {
        _underline = false;
        _strikeOut = false;
    
        switch (frame.getStatus()) {
        case CREATED_AND_DELETED:
            return Color.ORANGE;
        case CREATED:
            return Color.BLUE;
        case DELETED:
            return Color.RED;
        default:
            return Color.BLACK;
        }
    }
        
    public Font getFont () {
        Font result = super.getFont ();
        if (frame == null) return result;
        
        _underline = false;
        _strikeOut = false;

        switch(frame.getStatus()) {
        case CREATED_AND_DELETED:
            result = result.deriveFont(Font.ITALIC);
            break;
        case CREATED:
            _underline = true;
            break;
        case DELETED:
            _strikeOut = true;
            break;
        case CHANGED:
            result = result.deriveFont(Font.BOLD);
            break;
        default:
            break;

        }
        return result;
    }
    


    public void load(Object value) {
        if(value instanceof Ontology_Component) {
            frame = (Ontology_Component) value;
            value = calculateValueString(frame);
        } else {
            frame = null;
        }
        super.load(value);
    }

    private String calculateValueString(Ontology_Component frame) {
        String name = null;
        switch (frame.getStatus()) {
        case CHANGED:
        case UNCHANGED:
        case CREATED:
            name = frame.getCurrentName();
            if (new_kb != null) {
                try {
                    name = new_kb.getFrame(name).getBrowserText();
                }
                catch (Throwable t) {
                    Log.getLogger().warning("Could not get browser text for new frame " + name + ", " + t);
                }
            }
            break;
        case DELETED:
            name = frame.getInitialName();
            if (old_kb != null) {
                try {
                    name = old_kb.getFrame(name).getBrowserText();
                }
                catch(Throwable t) {
                    Log.getLogger().warning("Could not get browser text for old frame " + name + ", " + t);
                }
            }
            break;
        }
        switch (frame.getStatus()) {
        case UNCHANGED:
            return "Unchanged: " + name;
        case CHANGED:
            return "Modified: " + name;
        case CREATED:
            return "New: " + name;
        case DELETED:
            return "Deleted: " + name;
        case CREATED_AND_DELETED:
            return "Created and Deleted Object";
        default:
            throw new RuntimeException("Developer missed a case");
        }
    }

    protected void paintString(Graphics graphics, String text, Point position, Color color, Dimension size) {
     if (color != null) {
         graphics.setColor(color);
     }

     graphics.setFont(getFont());
     int y = (size.height + _fontMetrics.getAscent())/2 -2; // -2 is a bizarre fudge factor that makes it look better!
     graphics.drawString(text, position.x, y);
     drawLine (graphics, position.x, (_fontMetrics.getHeight())/2, position.x + _fontMetrics.stringWidth(text), (_fontMetrics.getHeight())/2);
     position.x += _fontMetrics.stringWidth(text);
   }

   private void drawLine (Graphics g, int x1, int y1, int x2, int y2) { 
     if (frame == null) return;
     if (_strikeOut)
       g.drawLine(x1, y1+1, x2, y2+1);
     if (_underline) {
       g.drawLine(x1, y1*2, x2, y2*2);
     }
   }

        
}
