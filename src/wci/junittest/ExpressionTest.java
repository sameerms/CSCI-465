package wci.junittest;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.Test;
import org.junit.BeforeClass;

import wci.frontend.Source;
import wci.frontend.triangle.TriangleParserTD;
import wci.frontend.triangle.TriangleScanner;
import wci.frontend.triangle.parsers.ExpressionParser;

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
				"[5,-6,7+9,10+c-f",*/
				"let  x ~ 5+6-c+d*4/r; var x  Integer; proc call(z:Boolean)  Begin z := false end; func call(z:Boolean):Boolean ~ ^z x:= 5 ",
				};
		for (String s : code) {
			StringReader st = new StringReader(s);
			BufferedReader bf = new BufferedReader(st);

			try {
				Source source = new Source(bf);
				source.addMessageListener(new SourceMessageListener());
				parser = new TriangleParserTD(new TriangleScanner(source));
				ExpressionParser exp = new ExpressionParser(parser);
				exp.parse(parser.getScanner().nextToken());
				//assertEquals(pep.getErrorCount(),1);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
