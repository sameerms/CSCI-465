package wci.junittest;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.Test;
import org.junit.BeforeClass;

import wci.frontend.Source;
import wci.frontend.triangle.TriangleParserTD;
import wci.frontend.triangle.TriangleScanner;
import wci.frontend.triangle.parsers.DeclarationParser;;

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
				"const x ~ 5+6-c+d*4/r",
				" x ~ 5+6-c+d*4/r",
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
				};
		for (String s : code) {
			StringReader st = new StringReader(s);
			BufferedReader bf = new BufferedReader(st);

			try {
				Source source = new Source(bf);
				source.addMessageListener(new SourceMessageListener());
				parser = new TriangleParserTD(new TriangleScanner(source));
				DeclarationParser declaration = new DeclarationParser(parser);
				declaration.parse(parser.getScanner().nextToken());
				//assertEquals(pep.getErrorCount(),1);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
