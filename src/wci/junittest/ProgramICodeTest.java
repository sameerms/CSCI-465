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
				"while //(x >= 5) do \n begin \n x := x - 1 \n end"*/
				"let var x:integer in x := 5"
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
