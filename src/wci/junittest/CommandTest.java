package wci.junittest;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.Test;
import org.junit.BeforeClass;

import wci.frontend.Source;
import wci.frontend.triangle.TriangleParserTD;
import wci.frontend.triangle.TriangleScanner;
import wci.frontend.triangle.parsers.ProgramParser;

public class CommandTest {

	private static TriangleParserTD parser;

	@BeforeClass
	public static void before() {
		parser = new TriangleParserTD(new TriangleScanner(null));
		parser.addMessageListener(new ParserMessageListener());
	}
		
	@Test
	public void testPrimaryExpression() {
		String[] code = {
				"x := 5+y/r*8; call(d, p); ",
				" := 5+y/r*8; call(d, p; ",
				"begin end; begin x := 5 end",
				" end; begin x : 5 end",
				" end; begin x := 5 ",
				"if (x == 5) then x := 5+G-y else x := 6; while y < 5 do y := y+1",
				"(x == 5) then x := 5+G-y else x := 6; y < 5 do y := y+1",
				"if  then x := 5+G-y else x := 6; while  do y := y+1",
				"if (x == 5) x := 5+G-y else x := 6; while y < 5 y := y+1",	
				"if (x == 5) then x := 5+G-y  x := 6; while y < 5 do y := y+1;",			
				};
		for (String s : code) {
			StringReader st = new StringReader(s);
			BufferedReader bf = new BufferedReader(st);

			try {
				Source source = new Source(bf);
				source.addMessageListener(new SourceMessageListener());
				parser = new TriangleParserTD(new TriangleScanner(source));
				ProgramParser cmd = new ProgramParser(parser);
				cmd.parse(parser.getScanner().nextToken());
				//assertEquals(pep.getErrorCount(),1);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
