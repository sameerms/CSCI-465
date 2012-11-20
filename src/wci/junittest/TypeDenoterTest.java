package wci.junittest;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.Test;
import org.junit.BeforeClass;

import wci.frontend.Source;
import wci.frontend.triangle.TriangleParserTD;
import wci.frontend.triangle.TriangleScanner;
import wci.frontend.triangle.parsers.*;

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
				"record y:Integer, m: Month, d:Integer end",
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
				"record y:Integer, m: Month, d:Integer "};
		for (String s : code) {
			StringReader st = new StringReader(s);
			BufferedReader bf = new BufferedReader(st);

			try {
				Source source = new Source(bf);
				source.addMessageListener(new SourceMessageListener());
				parser = new TriangleParserTD(new TriangleScanner(source));
				TypeDenoterParser td = new TypeDenoterParser(parser);
				td.parse(parser.getScanner().nextToken());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

}
