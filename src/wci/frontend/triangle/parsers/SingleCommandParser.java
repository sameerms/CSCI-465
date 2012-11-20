package wci.frontend.triangle.parsers;

import java.util.ArrayList;
import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.triangle.*;
import wci.intermediate.*;
import wci.intermediate.icodeimpl.ICodeNodeTypeImpl;
import wci.intermediate.symtabimpl.DefinitionImpl;
import wci.intermediate.symtabimpl.SymTabKeyImpl;
import wci.intermediate.typeimpl.TypeChecker;

import static wci.frontend.triangle.TriangleErrorCode.*;
import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;

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
public class SingleCommandParser extends TriangleParserTD {
	static final EnumSet<TriangleTokenType> FIRST_SET;
	static final EnumSet<TriangleTokenType> FOLLOW_SET;
	static final EnumSet<TriangleTokenType> FIRST_FOLLOW_SET; 
	static final EnumSet<TriangleTokenType> COLON_EQUAL_SET;
	static {
		
		FIRST_SET = EnumSet.of(IDENTIFIER, LET, TriangleTokenType.IF, BEGIN, WHILE, 
				IN, SEMICOLON, END, END_OF_FILE);
		FOLLOW_SET = EnumSet.of(ELSE, IN, SEMICOLON, IDENTIFIER, LET, 
				TriangleTokenType.IF, BEGIN, WHILE, END_OF_FILE);
		FIRST_FOLLOW_SET = FIRST_SET.clone();
		FIRST_FOLLOW_SET.addAll(FOLLOW_SET);
		COLON_EQUAL_SET = EnumSet.of(IDENTIFIER, CHAR, INTEGER, OPERATOR, LEFT_PAREN,
				LEFT_BRACE, LEFT_BRACKET, LET, TriangleTokenType.IF);
		COLON_EQUAL_SET.addAll(FIRST_FOLLOW_SET);
	}

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent parser.
	 */
	public SingleCommandParser(TriangleParserTD parent) {
		super(parent);
	}

	/**
	 * Parse a Triangle single-command.
	 * 
	 * Single-Command ::= 
	 *                    | V-name := Expression 
	 *                    | Identifier (Actual-Parameter-Sequence) 
	 *                    | begin Command End 
	 *                    | let Declaration in Single-Command 
	 *                    | if Expression then Single_Command else Single-Command 
	 *                    | while Expression do Single-Command
	 *  
	 * To be overridden by the specialized command parse subclasses.
	 * 
	 * @param token
	 *            the initial token.
	 * @return the root node of the generated parse tree.
	 * @throws Exception
	 *             if an error occurred.
	 */
	@SuppressWarnings("unchecked")
	public ICodeNode parse(Token token) throws Exception {
		ICodeNode singleCommandNode = null;
		ICodeNode exprNode = null;
		SingleCommandParser singleCommandParser = null;
		ExpressionParser expression = null;

		token = currentToken();
		TriangleTokenType tokenType = (TriangleTokenType) token.getType();

		switch (tokenType) {
		case IDENTIFIER:
			// Need to distinguish between assignment statement or procedure call
			// Peek at the next token.
			token = peekToken();
			if ((TriangleTokenType) (token.getType()) == LEFT_PAREN) { // procedure call
				token = currentToken(); // back to identifier token
				singleCommandNode = ICodeFactory.createICodeNode(CALL);
				singleCommandNode.setAttribute(ID, token.getText());
				setLineNumber(singleCommandNode,token);
				ICodeNode paramNode = ICodeFactory.createICodeNode(PARAMETERS);
				singleCommandNode.addChild(paramNode);
				ArrayList<SymTabEntry> formalParameterSeq = null;
				SymTabEntry procId = symTabStack.lookup(token.getText().toLowerCase());
				if (singleCommandNode != null) {
					if (procId.getDefinition() != DefinitionImpl.PROCEDURE) {
						errorHandler.flag(token, IDENTIFIER_NOT_PROCEDURE, this);
					}
					formalParameterSeq = (ArrayList<SymTabEntry>) procId.getAttribute(SymTabKeyImpl.ROUTINE_PARMS);
					singleCommandNode.setTypeSpec(procId.getTypeSpec());
				}
				token = nextToken(); // absorb the Identifier
				token = nextToken(); // absorb left parenthesis
				ActualParameterSequenceParser actualParameterSeq = new ActualParameterSequenceParser(this);
				paramNode.addChild(actualParameterSeq.parse(token,formalParameterSeq));
				token = synchronize(RIGHT_PAREN, MISSING_RIGHT_PAREN,FOLLOW_SET);
			} else { // assignment statement
				singleCommandNode = ICodeFactory.createICodeNode(ASSIGN);
				setLineNumber(singleCommandNode, token);
				VnameParser vname = new VnameParser(this);
				ICodeNode lhs = vname.parse(token);
				singleCommandNode.addChild(lhs);
				token = synchronize(COLON_EQUALS, MISSING_COLON_EQUALS,COLON_EQUAL_SET);
				expression = new ExpressionParser(this);
				ICodeNode rhs = expression.parse(token);
				singleCommandNode.addChild(rhs);
				if (!TypeChecker.isAssignmentCompatible(lhs, rhs)){
					errorHandler.flag(token, ASSIGNMENT_NOT_TYPE_COMPATIBLE, this);
				}
			}
			break;
		case BEGIN:
			token = nextToken();
			CommandParser commandParser = new CommandParser(this);
			singleCommandNode = commandParser.parse(token);
			token = synchronize(END, MISSING_END,FOLLOW_SET);
			break;
		case LET:
			singleCommandNode = ICodeFactory.createICodeNode(BLOCK);
			setLineNumber(singleCommandNode,token);
			singleCommandNode.setAttribute(SYMBOL_TABLE, symTabStack.push());
			token = nextToken();
			DeclarationParser declaration = new DeclarationParser(this);
			declaration.parse(token);
			token = synchronize(IN, MISSING_IN,FIRST_FOLLOW_SET);
			singleCommandParser = new SingleCommandParser(this);
			singleCommandParser.parse(token);
			symTabStack.pop();
			break;
		case IF:
			singleCommandNode = ICodeFactory.createICodeNode(IF_STATEMENT);
			setLineNumber(singleCommandNode, token);
			token = nextToken();
			expression = new ExpressionParser(this);
			exprNode = expression.parse(token);
			singleCommandNode.addChild(exprNode);
			if (!TypeChecker.isExpressionBoolean(exprNode)){
				errorHandler.flag(token, EXPECTING_BOOLEAN, this);
			}
			token = synchronize(THEN, MISSING_THEN,FIRST_FOLLOW_SET);
			singleCommandParser = new SingleCommandParser(this);
			singleCommandNode.addChild(singleCommandParser.parse(token));
			token = synchronize(ELSE, MISSING_ELSE,FIRST_FOLLOW_SET);
			singleCommandParser = new SingleCommandParser(this);
			singleCommandNode.addChild(singleCommandParser.parse(token));
			break;
		case WHILE:
			singleCommandNode = ICodeFactory.createICodeNode(LOOP);
			setLineNumber(singleCommandNode, token);
			ICodeNode testNode = ICodeFactory.createICodeNode(TEST);
			setLineNumber(testNode,token);
			singleCommandNode.addChild(testNode);
			token = nextToken();
			expression = new ExpressionParser(this);
			exprNode = expression.parse(token);
			testNode.addChild(exprNode);
			if (!TypeChecker.isExpressionBoolean(exprNode)){
				errorHandler.flag(token, EXPECTING_BOOLEAN, this);
			}
			singleCommandParser = new SingleCommandParser(this);
			singleCommandNode.addChild(singleCommandParser.parse(token));
			break;
		default:
			// no op
		}
		// Set the current line number as an attribute.
		// setLineNumber(statementNode, token);

		return singleCommandNode;
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