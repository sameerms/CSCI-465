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
import wci.frontend.triangle.parsers.VnameParser;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.DefinitionImpl;
import wci.intermediate.symtabimpl.SymTabKeyImpl;
import wci.intermediate.symtabimpl.TrianglePredefined;
import wci.intermediate.typeimpl.TypeFormImpl;
import wci.util.ParseTreePrinter;

public class VnameTest {

	private static TriangleParserTD parser;

	@BeforeClass
	public static void before() {
		parser = new TriangleParserTD(new TriangleScanner(null));
		parser.addMessageListener(new ParserMessageListener());
	}
		
	@Test
	public void testPrimaryExpression() {
		String[] code = {
				"x",
				"x.y",
				"x.y.z",
				"x.z",
				"x.z[5]",
				"x[5]",
				/*
				"x[c-d+e]",
				"x[5].y.z[67]",
				"x.y[45-g].z.h[565-a+b]",
				"x.",
				"x.y.",
				"x.[6]",
				"x[6].y[]"
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
				setUpSymbolTable();
				VnameParser vname = new VnameParser(parser);
				ICodeNode node = vname.parse(parser.getScanner().nextToken());
				iCode.setRoot(node);
				treePrinter.print(symTabStack);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
		symTabStack.pop();
	}
	
	private void setUpSymbolTable(){
		SymTabStack symTabStack = parser.getSymTabStack();
		SymTabEntry identId = symTabStack.enterLocal("x");
		identId.setDefinition(DefinitionImpl.VARIABLE);
		SymTab table = symTabStack.push();
		SymTabEntry fieldId = symTabStack.enterLocal("y");
		fieldId.setDefinition(DefinitionImpl.FIELD);
		fieldId.setTypeSpec(TypeFactory.createType(TypeFormImpl.SCALAR));
		SymTabEntry arrayId = symTabStack.enterLocal("z");
		arrayId.setDefinition(DefinitionImpl.FIELD);
		arrayId.setTypeSpec(TypeFactory.createArrayType(10, TrianglePredefined.integerType));
		identId.setTypeSpec(TypeFactory.createRecordType(table, null));
		symTabStack.pop();
	}
}
