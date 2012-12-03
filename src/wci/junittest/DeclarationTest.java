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
import wci.frontend.triangle.parsers.SingleDeclarationParser;
import wci.intermediate.ICode;
import wci.intermediate.ICodeFactory;
import wci.intermediate.ICodeNode;
import wci.intermediate.SymTabEntry;
import wci.intermediate.SymTabStack;
import wci.intermediate.TypeSpec;
import wci.intermediate.symtabimpl.DefinitionImpl;
import wci.intermediate.symtabimpl.TrianglePredefined;
import wci.util.CrossReferencer;
import wci.util.ParseTreePrinter;

public class DeclarationTest {

	private static TriangleParserTD parser;

	@BeforeClass
	public static void before() {
		parser = new TriangleParserTD(new TriangleScanner(null));
		parser.addMessageListener(new ParserMessageListener());
	}
		
	@Test
	public void testPrimaryExpression() {
		String[] code = {
				/*"const x ~ 5+6",
				"var y : Integer",*/
				"proc foo(z:Boolean) ~ Begin z := false end",
				"func call(z:Boolean):Integer ~ 5",
				/*
				
				"const x ~ 5+6-c+d*4/r",
				" x ~ 5+6-c+d*4/r",
				"proc foo(z:Boolean) ~ Begin z := false end",
				"func bar(z:Boolean):Boolean ~ z",
				
				"const  ~ 5+6-c+d*4/r",
				"const x  5+6-c+d*4/r",
				"const x ~ ",
				"var x : Integer",
				" x : Integer",
				"var  : Integer",
				"var x  Integer",
				"var x : ",
				"proc call(z:Boolean) ~ Begin z := false end",
				"call(z:Boolean) ~ Begin z := false end",
				"proc (z:Boolean) ~ Begin z := false end",
				"proc call(z:Boolean)  Begin z := false end",
				"proc call(z:Boolean) ~ z := false",
				"func call(z:Boolean):Boolean ~ ^z",
				"func call(z:Boolean) Boolean ~ ^z",
				"func call(z:Boolean): ~ ^z",
				"func call(z:Boolean):Boolean ^z",
				"func call(z:Boolean) ^z",
				"const x ~ 5+6-c+d*4/r; var x : Integer; proc call(z:Boolean) ~ Begin z := false end; func call(z:Boolean):Boolean ~ ^z",
				"const  ~ 5+6-c+d*4/r; var x  Integer; proc call(z:Boolean)  Begin z := false end; func call(z:Boolean) Boolean  ^z"
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
				SingleDeclarationParser decleration = new SingleDeclarationParser(parser);
				decleration.parse(parser.getScanner().nextToken());
				xref.print(symTabStack);
				symTabStack.pop();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		symTabStack.pop();
	}
}
