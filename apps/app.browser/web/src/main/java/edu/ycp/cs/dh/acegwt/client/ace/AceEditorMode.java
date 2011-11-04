// Copyright (c) 2011 David H. Hovemeyer <david.hovemeyer@gmail.com>
// 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package edu.ycp.cs.dh.acegwt.client.ace;

/**
 * Enumeration for ACE editor modes.
 * Note that the corresponding .js file must be loaded
 * before a mode can be set.
 */
public enum AceEditorMode {
	/** C/C++. */
	C_CPP("c_cpp"),
	/** Clojure. */
	CLOJURE("clojure"),
	/** Coffee. */
	COFFEE("coffee"),
	/** ColdFusion. */
	COLDFUSION("coldfusion"),
	/** C#. */
	CSHARP("csharp"),
	/** CSS. */
	CSS("css"),
	/** Groovy. */
	GROOVY("groovy"),
	/** HTML. */
	HTML("html"),
	/** JAVA. */
	JAVA("java"),
	/** Javascript. */
	JAVASCRIPT("javascript"),
	/** JSON. */
	JSON("json"),
	/** LaTeX. */
	LATEX("latex"),
	/** Lua. */
	LUA("lua"),
	/** Markdown. */
	MARKDOWN("markdown"),
	/** OCaml. */
	OCAML("ocaml"),
	/** Perl. */
	PERL("perl"),
	/** PHP. */
	PHP("php"),
	/** PowerShell. */
	POWERSHELL("powershell"),
	/** Python. */
	PYTHON("python"),
	/** Ruby. */
	RUBY("ruby"),
	/** Scad. */
	SCAD("scad"),
	/** Scala. */
	SCALA("scala"),
	/** SCSS. */
	SCSS("scss"),
	/** SQL. */
	SQL("sql"),
	/** SVG. */
	SVG("svg"),
	/** Textile. */
	TEXTILE("textile"),
	/** XML. */
	XML("xml");
	
	private final String name;
	
	private AceEditorMode(String name) {
		this.name = name;
	}
	
	/**
	 * @return mode name (e.g., "java" for Java mode)
	 */
	public String getName() {
		return name;
	}
	
	public static AceEditorMode fromPath(String path) {
		int dotIndex = path.lastIndexOf('.');
		int slashIndex = path.lastIndexOf('/');
		// If no dot, or dot is before a slash
		if (dotIndex == -1 || dotIndex < slashIndex) {
			return null;
		} else {
			String extension = path.substring(dotIndex + 1);
			for (AceEditorMode mode : AceEditorMode.values()) {
				if (extension.equals(mode.getName())) {
					return mode;
				}
			}
			// Special cases not in names
			if (extension.equals("c") || extension.equals("h") || extension.equals("cpp") || extension.equals("hpp"))
				return AceEditorMode.C_CPP;
			if (extension.equals("clj"))
				return AceEditorMode.CLOJURE;
			if (extension.equals("cs"))
				return AceEditorMode.CSHARP;
			if (extension.equals("js"))
				return AceEditorMode.JAVASCRIPT;
			if (extension.equals("md"))
				return AceEditorMode.MARKDOWN;
			if (extension.equals("pl"))
				return AceEditorMode.PERL;
			if (extension.equals("py"))
				return AceEditorMode.PYTHON;
			if (extension.equals("rb"))
				return AceEditorMode.RUBY;
			
			// In any other case
			return null;
		}
	}
}
