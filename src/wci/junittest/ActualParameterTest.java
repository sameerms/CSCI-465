package wci.junittest;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.Test;
import org.junit.BeforeClass;

import wci.frontend.Source;
import wci.frontend.triangle.TriangleParserTD;
import wci.frontend.triangle.TriangleScanner;
import wci.frontend.triangle.parsers.ActualParameterSequenceParser;;

public class ActualParameterTest {

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
				"x,(y),-56+85*c/y",
				"var x.y.z[6],var z[y-7*g].y.x, var x",
				"proc x,proc y, proc z, func h, func g",
				"x,(y) -56+85*c/y",*/
				"x,(y),",
				"proc x,proc y,  , func h, func g",
				};
		for (String s : code) {
			StringReader st = new StringReader(s);
			BufferedReader bf = new BufferedReader(st);

			try {
				Source source = new Source(bf);
				source.addMessageListener(new SourceMessageListener());
				parser = new TriangleParserTD(new TriangleScanner(source));
				ActualParameterSequenceParser aps = new ActualParameterSequenceParser(parser);
				//aps.parse(parser.getScanner().nextToken());
				//assertEquals(pep.getErrorCount(),1);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
