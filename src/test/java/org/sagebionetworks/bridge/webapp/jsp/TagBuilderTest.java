package org.sagebionetworks.bridge.webapp.jsp;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TagBuilderTest {
	
	TagBuilder tb;
	
	@Before
	public void init() {
		tb = new TagBuilder();
	}
	
	@Test
    public void startTag() throws Exception {
        tb.startTag("div");
        assertEquals("<div />", tb.toString());
    }
    
	@Test
    public void addAttribute() throws Exception {
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
	public void addStyleClasses() {
		tb.startTag("div");
		tb.addStyleClass("foo", "bar");
		
		assertEquals("<div class=\"foo bar\" />", tb.toString());
	}
	
	@Test
	public void addNullStyleClasses() {
		tb.startTag("div");
		tb.addStyleClass();
		
		assertEquals("<div />", tb.toString());
	}
	
	
	@Test
    public void appendInt() throws Exception {
        tb.startTag("div");
        tb.append(3);
        assertEquals("<div>3</div>", tb.toString());
    }

	@Test
    public void appendString() throws Exception {
        tb.startTag("div");
        tb.append("Red Dwarf");
        assertEquals("<div>Red Dwarf</div>", tb.toString());
    }

	@Test
    public void appendLong() throws Exception {
        tb.startTag("div");
        tb.append(12L);
        assertEquals("<div>12</div>", tb.toString());
    }
    
	@Test
    public void appendStringBuilder() throws Exception {
        tb.startTag("div");
        StringBuilder sb = new StringBuilder("Red ");
        sb.append("Dwarf");
        tb.append(sb);
        assertEquals("<div>Red Dwarf</div>", tb.toString());
    }
    
	@Test
    public void appendBoolean() throws Exception {
        tb.startTag("div");
        tb.append(true);
        tb.append(false);
        assertEquals("<div>truefalse</div>", tb.toString());
    }
    
	@Test
    public void appendChar() throws Exception {
        tb.startTag("div");
        tb.append('R');
        tb.append('e');
        tb.append('d');
        assertEquals("<div>Red</div>", tb.toString());
    }
    
	@Test
    public void appendCharArray() throws Exception {
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
        tb.startTag("div");
        StringBuffer sb = new StringBuffer("Red");
        sb.append(" Dwarf");
        tb.append(sb.subSequence(0, 3));
        assertEquals("<div>Red</div>", tb.toString());
    }
    
	@Test
    public void appendDouble() throws Exception {
        tb.startTag("div");
        tb.append(2.01);
        assertEquals("<div>2.01</div>", tb.toString());
    }
    
	@Test
    public void appendFloat() throws Exception {
        tb.startTag("div");
        tb.append(2.01F);
        assertEquals("<div>2.01</div>", tb.toString());
    }
    
	@Test
    public void endTag() throws Exception {
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
    }

	@Test
    public void length() throws Exception {
        tb = new TagBuilder();
        tb.startTag("div");
        tb.startTag("tag");
        tb.append("Red Dwarf");
        
        // Should show the correct length accounting for end tags
        assertEquals("<div><tag>Red Dwarf</tag></div>".length(), tb.length());

        // But... we should still be able to add content at our current location
        tb.fullTag("b", "test");
        // tb.startTag("b").append("test").endTag("b");
        
        assertEquals("<div><tag>Red Dwarf<b>test</b></tag></div>", tb.toString());
        tb = new TagBuilder();
        assertEquals(0, tb.length());
    }

	@Test
	public void tagsCanBeNested() throws Exception {
		// Real world example: <a><span class="foo">Icon</span> Test</a>
		tb.startTag("a");
		tb.startTag("span");
		tb.addAttribute("class", "foo");
		tb.append("Icon");
		tb.endTag("span");
		tb.append(" ");
		tb.append("Test");
		tb.endTag("a");
		
		assertEquals("<a><span class=\"foo\">Icon</span> Test</a>", tb.toString());
	}
	
	@Test
    public void toStringEmpty() throws Exception {
        assertEquals("", tb.toString());
    }
}
