package wci.frontend.triangle.parsers;

import static wci.frontend.triangle.TriangleErrorCode.UNEXPECTED_TOKEN;
import static wci.frontend.triangle.TriangleTokenType.SEMICOLON;
import static wci.frontend.triangle.TriangleTokenType.END_OF_FILE;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.PROGRAM;

import java.util.EnumSet;

import wci.frontend.Scanner;
import wci.frontend.Token;
import wci.frontend.triangle.TriangleParserTD;
import wci.frontend.triangle.TriangleTokenType;
import wci.intermediate.ICodeNode;
import wci.intermediate.ICodeFactory;

/**
 * <h1>ProgramParser</h1>
 * 
 * <p>
 * Parse a Triangle Program.
 * </p>
 * 
 * <p>
 * For instructional purposes only. No warranties.
 * </p>
 */

public class ProgramParser extends TriangleParserTD {

	public static EnumSet<TriangleTokenType> NOT_EOF_SET;

	static {
		NOT_EOF_SET = EnumSet.of(SEMICOLON, END_OF_FILE);
	}

	public ProgramParser(Scanner scanner) {
		super(scanner);
	}

	public ProgramParser(TriangleParserTD parser) {
		super(parser);
	}

	/**
	 * Parse a Triangle program.
	 * 
	 * Program ::= Command
	 * 
	 * To be overridden by the specialized command parse subclasses.
	 * 
	 * @param token
	 *            the initial token.
	 * @return the root node of the generated parse tree.
	 * @throws Exception
	 *             if an error occurred.
	 */
	public ICodeNode parse(Token token) throws Exception {
		ICodeNode programNode = ICodeFactory.createICodeNode(PROGRAM);

		// parse command
		token = currentToken();
		do {
			CommandParser cmd = new CommandParser(this);
			programNode.addChild(cmd.parse(token));
			token = currentToken();
			if ((TriangleTokenType) token.getType() != END_OF_FILE) {
				token = synchronize(END_OF_FILE, UNEXPECTED_TOKEN, NOT_EOF_SET);
				token = nextToken(); // if semicolon absorb it; otherwise will return EofToken
			}
		} while ((TriangleTokenType) token.getType() != END_OF_FILE);
		// Set the current line number as an attribute.
		// setLineNumber(statementNode, token);

		return programNode;
	}

}
