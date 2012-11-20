package wci.junittest;

import static org.junit.Assert.*;

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
		symTabStack.push();
		for (String s : code) {
			StringReader st = new StringReader(s);
			BufferedReader bf = new BufferedReader(st);

			try {
				Source source = new Source(bf);
				source.addMessageListener(new SourceMessageListener());
				parser = new TriangleParserTD(new TriangleScanner(source));
				setUpSymbolTable();
				VnameParser vname = new VnameParser(parser);
				vname.parse(parser.getScanner().nextToken());
				//assertEquals(pep.getErrorCount(),1);
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
