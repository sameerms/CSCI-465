package wci.junittest;

import static org.junit.Assert.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_ICODE;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_SYMTAB;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.Test;
import org.junit.BeforeClass;

import wci.frontend.Source;
import wci.frontend.triangle.TriangleParserTD;
import wci.frontend.triangle.TriangleScanner;
import wci.frontend.triangle.parsers.ExpressionParser;
import wci.intermediate.ICode;
import wci.intermediate.ICodeFactory;
import wci.intermediate.ICodeNode;
import wci.intermediate.SymTabEntry;
import wci.intermediate.SymTabStack;
import wci.intermediate.symtabimpl.DefinitionImpl;
import wci.intermediate.symtabimpl.TrianglePredefined;
import wci.util.ParseTreePrinter;

public class ExpressionTest {

	private static TriangleParserTD parser;

	@BeforeClass
	public static void before() {
		parser = new TriangleParserTD(new TriangleScanner(null));
		parser.addMessageListener(new ParserMessageListener());
	}
		
	@Test
	public void testPrimaryExpression() {
		String[] code = {
				"5 + 6",
				"\\true"
				/*"-5",
				"'v'",
				"-34",
				"--34",
				"call()",
				"call(",
				"(-34)",
				"(-34",
				"(-34+v-f+10)",
				"x",
				"{}",  // test Record-Aggregate
				"{d~5}",
				"{~5}",
				"{d 5}",
				"{d~}",
				"{d~5,Z~4}",
				"{d 5,Z~4}",
				"{d~5 Z~4}",
				"{d~5,}",
				"{d~5,Z}",
				"[]",		// test Array-Aggregate
				"[5,-6,7+9,10+c-f]",
				"[,-6,7+9,10+c-f]",
				"[5,,7+9,10+c-f]",
				"[5,-6,7+9,10c-f]",
				"[5,-6,7+9,10+c-f",
				"let  x ~ 5+6-c+d*4/r; var x  Integer; proc call(z:Boolean)  Begin z := false end; func call(z:Boolean):Boolean ~ ^z x:= 5 ",
				*/
				};
		SymTabStack symTabStack = parser.getSymTabStack();
		TrianglePredefined.initialize(symTabStack);
		ICode iCode = ICodeFactory.createICode();
		// Create a dummy program identifier symbol table entry.
        SymTabEntry routineId = symTabStack.enterLocal("DummyProgramName".toLowerCase());
        routineId.setDefinition(DefinitionImpl.PROGRAM);
        symTabStack.setProgramId(routineId);

        // Push a new symbol table onto the symbol table stack and set
        // the routine's symbol table and intermediate code.
        routineId.setAttribute(ROUTINE_SYMTAB, symTabStack.push());
		routineId.setAttribute(ROUTINE_ICODE, iCode);
		ParseTreePrinter treePrinter = new ParseTreePrinter(System.out);

		for (String s : code) {
			StringReader st = new StringReader(s);
			BufferedReader bf = new BufferedReader(st);

			try {
				Source source = new Source(bf);
				source.addMessageListener(new SourceMessageListener());
				parser = new TriangleParserTD(new TriangleScanner(source));
				ExpressionParser exp = new ExpressionParser(parser);
				ICodeNode node = exp.parse(parser.getScanner().nextToken());
				iCode.setRoot(node);
				treePrinter.print(symTabStack);
				//assertEquals(pep.getErrorCount(),1);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
