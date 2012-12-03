package wci.junittest;

import static org.junit.Assert.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_ICODE;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.Test;
import org.junit.BeforeClass;

import wci.frontend.Source;
import wci.frontend.triangle.TriangleParserTD;
import wci.frontend.triangle.TriangleScanner;
import wci.frontend.triangle.parsers.ProgramParser;

import wci.intermediate.*;
import wci.util.*;

public class ProgramICodeTest {

	private static TriangleParserTD parser;

	@BeforeClass
	public static void before() {
		parser = new TriangleParserTD(new TriangleScanner(null));
		parser.addMessageListener(new ParserMessageListener());
	}
		
	@Test
	public void testProgram() {
		String[] code = {
				/*"x := -5;\n x := x + (z - 5) / y"
				"if (x <= -5) then x := x + 5 else x := x + 1",
				"let const d ~ 5; type int ~ Integer; var x:int; var y:integer in x := y",
				"let const m ~ 5; \n"+
				"var x: integer; \n"+
				"proc foo (var y : integer) ~ \n"+
				"  x := y + m \n"+
				"in \n"+
				"begin \n"+
				"while \\(x >= 5) do \n begin \n x := x - m \n end; \n"+
				"x := x/5 * x \\/ true;\n"+
				"foo(proc foo);\n"+
				"end;"
				*/
				//"let var x:integer in x := x *2"
				"let\n"+
				"type int ~ integer;"+
				"type Info ~ Record\n"+
				"  name : array 3 of char, \n"+
				"  salary : int \n"+
				"end; \n"+
				"var salInfo : Info\n"+
				"in \n"+
				"Begin\n"+
				"salInfo := {name ~ ['W','e','s'], salary ~ 25000};\n"+
				"salInfo.name := ['B','A']; \n"+
				"end"
				};
		for (String s : code) {
			StringReader st = new StringReader(s);
			BufferedReader bf = new BufferedReader(st);

			boolean xref = true;
			boolean intermediate = true;
			
			try {
				Source source = new Source(bf);
				source.addMessageListener(new SourceMessageListener());
				parser = new TriangleParserTD(new TriangleScanner(source));
				parser.parse();
				if (parser.getErrorCount() == 0) {
	                SymTabStack symTabStack = parser.getSymTabStack();

	                SymTabEntry programId = symTabStack.getProgramId();
	                ICode iCode = (ICode) programId.getAttribute(ROUTINE_ICODE);

	                if (xref) {
	                    CrossReferencer crossReferencer = new CrossReferencer();
	                    crossReferencer.print(symTabStack);
	                }

	                if (intermediate) {
	                    ParseTreePrinter treePrinter =
	                                         new ParseTreePrinter(System.out);
	                    treePrinter.print(symTabStack);
	                }
	                //backend.process(iCode, symTabStack);
	            }
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
