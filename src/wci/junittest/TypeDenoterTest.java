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
import wci.frontend.triangle.parsers.*;
import wci.intermediate.ICode;
import wci.intermediate.ICodeFactory;
import wci.intermediate.SymTabEntry;
import wci.intermediate.SymTabStack;
import wci.intermediate.TypeSpec;
import wci.intermediate.symtabimpl.DefinitionImpl;
import wci.intermediate.symtabimpl.TrianglePredefined;
import wci.util.CrossReferencer;

public class TypeDenoterTest {

	private static TriangleParserTD parser;

	@BeforeClass
	public static void before() {
		parser = new TriangleParserTD(new TriangleScanner(null));
		parser.addMessageListener(new ParserMessageListener());
	}

	@Test
	public void testTypeDenoter() {
		String[] code = {
				"Boolean",
				"array 80 of Char",
				"record y:Integer, m: Integer, d:Integer end",
				"record y:Integer, m: Month, d:Integer end",
				"array 80 of ",
				"array  of Char",
				/*
				"record size:Integer, entry : array 100 of record x:array 20 of Char,y:Integer end end",
				" 80 of Char",
				"array  of Char",
				"array 80 Char",
				"array 80 of ",
				" y:Integer, m: Month, d:Integer end",
				"record :Integer, m: Month, d:Integer end",
				"record y Integer, m: Month, d:Integer end",
				"record yInteger, m: Month, d:Integer end",
				"record y:Integer m: Month, d:Integer end",
				"record y: : Month, d:Integer end",
				"record y:Integer, m: Month, d:end",
				"record y:Integer, m: Month, d:Integer "
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
		routineId.setAttribute(ROUTINE_ICODE, iCode);
		
		CrossReferencer xref = new CrossReferencer();
		
		for (String s : code) {
			StringReader st = new StringReader(s);
			BufferedReader bf = new BufferedReader(st);
			
			try {
				Source source = new Source(bf);
				source.addMessageListener(new SourceMessageListener());
				parser = new TriangleParserTD(new TriangleScanner(source));
				routineId.setAttribute(ROUTINE_SYMTAB, symTabStack.push());
				TypeDenoterParser typeDenoter = new TypeDenoterParser(parser);
				TypeSpec type = typeDenoter.parse(parser.getScanner().nextToken());
				SymTabEntry entry = symTabStack.enterLocal(s.replace(' ','*'));
				entry.setDefinition(DefinitionImpl.VARIABLE);
				entry.appendLineNumber(parser.currentToken().getLineNumber());
				entry.setTypeSpec(type);
				xref.print(symTabStack);
				symTabStack.pop();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

}
