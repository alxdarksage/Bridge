package org.sagebionetworks.bridge.webapp.jsp;

import static org.junit.Assert.*;
import org.junit.Test;

public class TagBuilderTest {
	
	@Test
    public void startTag() throws Exception {
        TagBuilder tb = new TagBuilder();
        tb.startTag("div");
        assertEquals("<div />", tb.toString());
    }
    
	@Test
    public void addAttribute() throws Exception {
        TagBuilder tb = new TagBuilder();
        tb.startTag("div");
        tb.addAttribute("foo", "boo");
        assertEquals("<div foo=\"boo\" />", tb.toString());

        try {
            tb.addAttribute(null, "Barnard Star");
            fail("Should not be able to add an attribute with a null name");
        } catch(Exception e) {
            ;
        }
        try {
            tb.addAttribute(null, null);
            fail("Should not be able to add an attribute with a null name");
        } catch(Exception e) {
            ;
        }
        // But this is just ignored...
        tb.addAttribute("Barnard Star", null);
        assertEquals("<div foo=\"boo\" />", tb.toString());
        
        
        // And this is ignored too...
        tb.endTag("div");
        tb.addAttribute("barnard", null);
        assertEquals("<div foo=\"boo\" />", tb.toString());
    }

	@Test
    public void appendInt() throws Exception {
        TagBuilder tb = new TagBuilder();
        tb.startTag("div");
        tb.append(3);
        assertEquals("<div>3</div>", tb.toString());
    }

	@Test
    public void appendString() throws Exception {
        TagBuilder tb = new TagBuilder();
        tb.startTag("div");
        tb.append("Red Dwarf");
        assertEquals("<div>Red Dwarf</div>", tb.toString());
    }

	@Test
    public void appendLong() throws Exception {
        TagBuilder tb = new TagBuilder();
        tb.startTag("div");
        tb.append(12L);
        assertEquals("<div>12</div>", tb.toString());
    }
    
	@Test
    public void appendStringBuilder() throws Exception {
        TagBuilder tb = new TagBuilder();
        tb.startTag("div");
        StringBuilder sb = new StringBuilder("Red ");
        sb.append("Dwarf");
        tb.append(sb);
        assertEquals("<div>Red Dwarf</div>", tb.toString());
    }
    
	@Test
    public void appendBoolean() throws Exception {
        TagBuilder tb = new TagBuilder();
        tb.startTag("div");
        tb.append(true);
        tb.append(false);
        assertEquals("<div>truefalse</div>", tb.toString());
    }
    
	@Test
    public void appendChar() throws Exception {
        TagBuilder tb = new TagBuilder();
        tb.startTag("div");
        tb.append('R');
        tb.append('e');
        tb.append('d');
        assertEquals("<div>Red</div>", tb.toString());
    }
    
	@Test
    public void appendCharArray() throws Exception {
        TagBuilder tb = new TagBuilder();
        tb.startTag("div");
        char[] c = new char[3];
        c[0] = 'R';
        c[1] = 'e';
        c[2] = 'd';
        tb.append(c);
        assertEquals("<div>Red</div>", tb.toString());
    }
    
	@Test
    public void appendCharArrayWithOffset() throws Exception {
        TagBuilder tb = new TagBuilder();
        tb.startTag("div");
        char[] c = new char[3];
        c[0] = 'R';
        c[1] = 'e';
        c[2] = 'd';
        tb.append(c, 1, 2);
        assertEquals("<div>ed</div>", tb.toString());
    }
    
	@Test
    public void appendCharSequence() throws Exception {
        TagBuilder tb = new TagBuilder();
        tb.startTag("div");
        StringBuffer sb = new StringBuffer("Red");
        sb.append(" Dwarf");
        tb.append(sb.subSequence(0, 3));
        assertEquals("<div>Red</div>", tb.toString());
    }
    
	@Test
    public void appendDouble() throws Exception {
        TagBuilder tb = new TagBuilder();
        tb.startTag("div");
        tb.append(2.01);
        assertEquals("<div>2.01</div>", tb.toString());
    }
    
	@Test
    public void appendFloat() throws Exception {
        TagBuilder tb = new TagBuilder();
        tb.startTag("div");
        tb.append(2.01F);
        assertEquals("<div>2.01</div>", tb.toString());
    }
    
	@Test
    public void endTag() throws Exception {
        TagBuilder tb = new TagBuilder();
        tb.startTag("div");
        tb.append("Red Dwarf");
        tb.endTag("div");
        assertEquals("<div>Red Dwarf</div>", tb.toString());
        
        // Now a more complicated nesting, should also be closed.
        tb = new TagBuilder();
        tb.startTag("div");
        tb.startTag("tag");
        tb.append("Red Dwarf");
        assertEquals("<div><tag>Red Dwarf</tag></div>", tb.toString());
        
        tb = new TagBuilder();
        try {
            tb.startTag("div");
            tb.startTag("tag");
            tb.append("Red Dwarf");
            tb.endTag("div");
            tb.endTag("tag");
            fail("Failed to throw runtime exception over mismatched tags.");
        } catch(Exception e) {
            ;
        }
    }

	@Test
    public void length() throws Exception {
        TagBuilder tb = new TagBuilder();
        tb = new TagBuilder();
        tb.startTag("div");
        tb.startTag("tag");
        tb.append("Red Dwarf");
        
        // Should show the correct length accounting for end tags
        assertEquals("<div><tag>Red Dwarf</tag></div>".length(), tb.length());

        // But... we should still be able to add content at our current location
        tb.startTag("b");
        tb.append("test");
        tb.endTag("b");
        
        System.out.println("|"+tb.toString()+"|");
        assertEquals("<div><tag>Red Dwarf<b>test</b></tag></div>", tb.toString());
        tb = new TagBuilder();
        assertEquals(tb.length(), 0);
    }

	@Test
    public void toStringEmpty() throws Exception {
        TagBuilder tb = new TagBuilder();
        assertEquals("", tb.toString());
    }
}
