package wci.frontend.triangle.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.triangle.*;

import static wci.frontend.triangle.TriangleTokenType.*;

/**
 * <h1>DeclarationParser</h1>
 * 
 * <p>
 * Parse a Triangle Declaration.
 * </p>
 * 
 * <p>
 * For instructional purposes only. No warranties.
 * </p>
 */
public class DeclarationParser extends TriangleParserTD {
	static final EnumSet<TriangleTokenType> FIRST_SET;
	static final EnumSet<TriangleTokenType> FOLLOW_SET;
	static final EnumSet<TriangleTokenType> FIRST_FOLLOW_SET;
	static {
		FIRST_SET = EnumSet.of(CONST, VAR, PROC, FUNC, TYPE);
		FOLLOW_SET = EnumSet.of(SEMICOLON, IN);
		FIRST_FOLLOW_SET= FIRST_SET.clone();
		FIRST_FOLLOW_SET.addAll(FOLLOW_SET);
	}

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent parser.
	 */
	public DeclarationParser(TriangleParserTD parent) {
		super(parent);
	}

	/**
	 * Parse a Triangle Declaration.
	 * 
	 * Declaration ::=   Single-Declaration
	 *                 | Declaration ; Single-Declaration
	 * 
	 * To be overridden by the specialized command parse subclasses.
	 * 
	 * @param token
	 *            the initial token.
	 * @return the root node of the generated parse tree.
	 * @throws Exception
	 *             if an error occurred.
	 */
	public void parse(Token token) throws Exception {
		TriangleTokenType tokenType=null;
		
		token = currentToken();		
		do{
			SingleDeclarationParser singleDeclaration = new SingleDeclarationParser(this);
			singleDeclaration.parse(token);
			synchronize(FOLLOW_SET);
			token = currentToken();
			tokenType = (TriangleTokenType)token.getType();
			if (tokenType == SEMICOLON){
				token = nextToken();
			}
		}while(tokenType == SEMICOLON);

	}
}