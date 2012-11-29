package wci.frontend.triangle.parsers;

import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.triangle.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.TrianglePredefined;
import wci.intermediate.typeimpl.TypeChecker;

import static wci.frontend.triangle.TriangleErrorCode.*;
import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.frontend.triangle.TriangleErrorCode.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>SingleCommandParser</h1>
 * 
 * <p>
 * Parse a Triangle single-command.
 * </p>
 * 
 * <p>
 * For instructional purposes only. No warranties.
 * </p>
 */
public class ExpressionParser extends TriangleParserTD {
	static final EnumSet<TriangleTokenType> FIRST_SET;
	static final EnumSet<TriangleTokenType> FOLLOW_SET;
	static final EnumSet<TriangleTokenType> FIRST_FOLLOW_SET;

	static {
		FIRST_SET = EnumSet.of(IDENTIFIER, CHAR, INTEGER, OPERATOR, LEFT_PAREN,
				LEFT_BRACE, LEFT_BRACKET, LET, TriangleTokenType.IF);
		FOLLOW_SET = EnumSet.of(THEN, ELSE, DO, COMMA, RIGHT_BRACKET,
				RIGHT_PAREN, SEMICOLON, END, IDENTIFIER, BEGIN, END, LET,
				TriangleTokenType.IF, WHILE, IN, CHAR, INTEGER, OPERATOR,
				RIGHT_PAREN, RIGHT_BRACE, RIGHT_BRACKET, END_OF_FILE);
		FIRST_FOLLOW_SET = FIRST_SET.clone();
		FIRST_FOLLOW_SET.addAll(FOLLOW_SET);
	}

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent parser.
	 */
	public ExpressionParser(TriangleParserTD parent) {
		super(parent);
	}

	/**
	 * Parse a Triangle single-command.
	 * 
	 * Expression ::= Secondary-Expression | let Declaration in Expression | if
	 * Expression then Expression else Expression
	 * 
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
		ICodeNode expressionNode = null;
		SecondaryExpressionParser secondaryExpression = null;
		DeclarationParser declaration = null;
		ExpressionParser expression = null;
		
		token = currentToken();
		
		switch ((TriangleTokenType) token.getType()) {
		case LET:
			expressionNode = ICodeFactory.createICodeNode(EXPRESSION_BLOCK);
			setLineNumber(expressionNode,token);
			expressionNode.setAttribute(SYMBOL_TABLE, symTabStack.push());
			token = nextToken();
			declaration = new DeclarationParser(this);
			declaration.parse(token);
			token = synchronize(IN, MISSING_IN, ExpressionParser.FIRST_FOLLOW_SET);
			expression = new ExpressionParser(this);
			ICodeNode exprNode = expression.parse(token);
			expressionNode.addChild(exprNode);
			expressionNode.setTypeSpec(exprNode.getTypeSpec());
			symTabStack.pop();
			break;
		case IF:
			expressionNode = ICodeFactory.createICodeNode(IF_EXPRESSION);
			setLineNumber(expressionNode, token);
			token = nextToken();
			expression = new ExpressionParser(this);
			ICodeNode testNode = expression.parse(token);
			expressionNode.addChild(testNode);
			if (!testNode.getTypeSpec().equals(TrianglePredefined.booleanType)){
				errorHandler.flag(token, EXPECTING_BOOLEAN, this);
			}
			token = synchronize(THEN, MISSING_THEN, ExpressionParser.FIRST_FOLLOW_SET);
			expression = new ExpressionParser(this);
			ICodeNode thenExprNode = expression.parse(token);
			expressionNode.addChild(thenExprNode);
			// Assume if expression has type of then expression
			expressionNode.setTypeSpec(thenExprNode.getTypeSpec());
			token = synchronize(ELSE, MISSING_ELSE, ExpressionParser.FIRST_FOLLOW_SET);
			expression = new ExpressionParser(this);
			ICodeNode elseExprNode = expression.parse(token);
			expressionNode.addChild(elseExprNode);
			if (!thenExprNode.getTypeSpec().equals(elseExprNode.getTypeSpec())){
				errorHandler.flag(token, ElSE_TYPE_MISMATCH, this);
			}
			break;
		default:
			secondaryExpression = new SecondaryExpressionParser(this);
			expressionNode = secondaryExpression.parse(token);
			break;
		}
		// Set the current line number as an attribute.
		// setLineNumber(statementNode, token);

		return expressionNode;
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