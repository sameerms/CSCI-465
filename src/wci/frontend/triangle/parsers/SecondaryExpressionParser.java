package wci.frontend.triangle.parsers;

import java.util.EnumSet;
import java.util.HashMap;

import wci.frontend.*;
import wci.frontend.triangle.*;
import wci.intermediate.*;

import static wci.frontend.triangle.TriangleErrorCode.*;
import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.frontend.triangle.TriangleErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>SecondaryExpressionParser</h1>
 * 
 * <p>
 * Parse a Triangle Secondary-Expression.
 * </p>
 * 
 * <p>
 * For instructional purposes only. No warranties.
 * </p>
 */
public class SecondaryExpressionParser extends TriangleParserTD {
	// Map relational operator tokens to node types.
	private static final HashMap<String, ICodeNodeType>	REL_OPS_MAP;
	
	static final EnumSet<TriangleTokenType> FIRST_SET;
	static final EnumSet<TriangleTokenType> FOLLOW_SET;
	static final EnumSet<TriangleTokenType> FIRST_FOLLOW_SET; 
	static {
		FIRST_SET = EnumSet.of(INTEGER,CHAR,IDENTIFIER,OPERATOR,LEFT_PAREN, LEFT_BRACE, LEFT_BRACKET);
		FOLLOW_SET = EnumSet.of(THEN, ELSE, DO, COMMA, RIGHT_BRACKET, RIGHT_PAREN, SEMICOLON, END,
				IDENTIFIER, BEGIN, END, LET, TriangleTokenType.IF, WHILE, IN, CHAR, INTEGER,
				OPERATOR, RIGHT_PAREN, RIGHT_BRACE, RIGHT_BRACKET, END_OF_FILE);
		FIRST_FOLLOW_SET= FIRST_SET.clone();
		FIRST_FOLLOW_SET.addAll(FOLLOW_SET);
		REL_OPS_MAP = new HashMap<String, ICodeNodeType>();
		REL_OPS_MAP.put("+", ADD);
		REL_OPS_MAP.put("-", SUBTRACT);
		REL_OPS_MAP.put("\\/", OR);
		REL_OPS_MAP.put("*", MULTIPLY);
		REL_OPS_MAP.put("/",INTEGER_DIVIDE);
		REL_OPS_MAP.put("//",MOD);
		REL_OPS_MAP.put("/\\", AND);
		REL_OPS_MAP.put("=", EQ);
		REL_OPS_MAP.put("//=", NE);
		REL_OPS_MAP.put("<", LT);
		REL_OPS_MAP.put("<=", LE);
		REL_OPS_MAP.put(">", GT);
		REL_OPS_MAP.put(">=", GE);
	}

	/**
	 * Constructor.
	 * 
	 * @param parent  the parent parser.
	 */
	public SecondaryExpressionParser(TriangleParserTD parent) {
		super(parent);
	}

	/**
	 * Parse a Triangle Secondary-Expression.
	 * 
	 * Secondary-Expression ::=   Primary-Expression (Operator Primary-Expression)*
	 * 
	 * 
	 * To be overridden by the specialized command parse subclasses.
	 * 
	 * @param token  the initial token.
	 * @return the root node of the generated parse tree.
	 * @throws Exception  if an error occurred.
	 */
	public ICodeNode parse(Token token) throws Exception {
		ICodeNode secondaryExpressionNode = null;
		
		token = currentToken();
		
		PrimaryExpressionParser primaryExpressionParser = new PrimaryExpressionParser(this);
		secondaryExpressionNode = primaryExpressionParser.parse(token);
		token = currentToken();
		
		while((TriangleTokenType)token.getType() == OPERATOR){
			ICodeNodeType opNodeType = REL_OPS_MAP.get(token.getText());
			ICodeNode tempNode = secondaryExpressionNode;
			secondaryExpressionNode = ICodeFactory.createICodeNode(opNodeType);
			secondaryExpressionNode.addChild(tempNode);
			setLineNumber(secondaryExpressionNode,token);
			token = nextToken(); // absorb operator
			primaryExpressionParser = new PrimaryExpressionParser(this);
			secondaryExpressionNode.addChild(primaryExpressionParser.parse(token));
			token = currentToken();
		}
		
		// Set the current line number as an attribute.
		// setLineNumber(statementNode, token);

		return secondaryExpressionNode;
	}

	/**
	 * Set the current line number as a statement node attribute.
	 * 
	 * @param node
	 *            ICodeNode
	 * @param token
	 *            the initial token.
	 */
	protected void setLineNumber(ICodeNode node, Token token) {
		if (node != null) {
			node.setAttribute(LINE, token.getLineNumber());
		}
	}
}