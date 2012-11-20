package wci.junittest;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.Test;
import org.junit.BeforeClass;

import wci.frontend.Source;
import wci.frontend.triangle.TriangleParserTD;
import wci.frontend.triangle.TriangleScanner;
import wci.frontend.triangle.parsers.FormalParameterSequenceParser;;

public class FormalParameterTest {

	private static TriangleParserTD parser;

	@BeforeClass
	public static void before() {
		parser = new TriangleParserTD(new TriangleScanner(null));
		parser.addMessageListener(new ParserMessageListener());
	}
		
	@Test
	public void testPrimaryExpression() {
		String[] code = {
				/*"",
				"x:Integer",
				"var x:Integer, y:Character, var z:Boolean",*/
				"var x:Integer, proc h(x:Boolean, var z:Integer), func br(x:Boolean, var Z:Character):Integer",
				"x Integer",
				":Integer",
				"x:",
				"var :Integer, y Character, var z:Boolean",
				"var x:, y:Character, var :Boolean",
				"var x:Integer, :Character, var :Boolean",
				"var x:Integer proc h(x:Boolean, var z:Integer),  br(x:Boolean, var Z:Character):Integer",
				"var x:Integer, proc h(x Boolean, var :Integer), func br(x:Boolean, var Z:Character):Integer"
				};
		for (String s : code) {
			StringReader st = new StringReader(s);
			BufferedReader bf = new BufferedReader(st);

			try {
				Source source = new Source(bf);
				source.addMessageListener(new SourceMessageListener());
				parser = new TriangleParserTD(new TriangleScanner(source));
				FormalParameterSequenceParser fps = new FormalParameterSequenceParser(parser);
				fps.parse(parser.getScanner().nextToken());
				//assertEquals(pep.getErrorCount(),1);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
