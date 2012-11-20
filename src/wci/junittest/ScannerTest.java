package wci.junittest;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.Test;

import wci.frontend.EofToken;
import wci.frontend.Source;
import wci.frontend.triangle.TriangleScanner;
import wci.frontend.triangle.TriangleToken;

import static wci.frontend.triangle.TriangleTokenType.END_OF_FILE;
public class ScannerTest {

	@Test
	public void test() {
		String[] code = {
				"(-34)",
				"-5",
				"++7",
				":"};
		for (String s : code) {
			StringReader st = new StringReader(s);
			BufferedReader bf = new BufferedReader(st);

			try {
				Source source = new Source(bf);
				source.addMessageListener(new SourceMessageListener());
				TriangleScanner scanner = new TriangleScanner(source);
				TriangleToken token = (TriangleToken)scanner.nextToken();
				while(token.getType() != END_OF_FILE){
				System.out.println(token.getText()+" "+token.getType().toString());
				token = (TriangleToken)scanner.nextToken();
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

}
