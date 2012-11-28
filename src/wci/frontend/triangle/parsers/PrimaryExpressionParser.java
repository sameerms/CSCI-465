package wci.frontend.triangle.parsers;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;

import wci.frontend.*;
import wci.frontend.triangle.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.DefinitionImpl;
import wci.intermediate.symtabimpl.SymTabKeyImpl;
import wci.intermediate.symtabimpl.TrianglePredefined;

import static wci.frontend.triangle.TriangleErrorCode.*;
import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.intermediate.symtabimpl.TrianglePredefined.integerType;
import static wci.intermediate.symtabimpl.TrianglePredefined.charType;

/**
 * <h1>PrimaryExpressionParser</h1>
 * 
 * <p>
 * Parse a Triangle Primary-Expression.
 * </p>
 * 
 * <p>
 * For instructional purposes only. No warranties.
 * </p>
 */
public class PrimaryExpressionParser extends TriangleParserTD {
	// Map relational operator tokens to node types.
	private static final HashMap<String, ICodeNodeType> REL_OPS_MAP;

	static final EnumSet<TriangleTokenType> FIRST_SET;
	static final EnumSet<TriangleTokenType> FOLLOW_SET;
	static final EnumSet<TriangleTokenType> FIRST_FOLLOW_SET;
	static {
		FIRST_SET = EnumSet.of(INTEGER, CHAR, IDENTIFIER, OPERATOR, LEFT_PAREN,
				LEFT_BRACE, LEFT_BRACKET);
		FOLLOW_SET = EnumSet.of(THEN, ELSE, DO, COMMA, RIGHT_BRACKET,
				RIGHT_PAREN, SEMICOLON, END, IDENTIFIER, BEGIN, END, LET,
				TriangleTokenType.IF, WHILE, IN, CHAR, INTEGER, OPERATOR,
				RIGHT_PAREN, RIGHT_BRACE, RIGHT_BRACKET, END_OF_FILE);
		FIRST_FOLLOW_SET = FIRST_SET.clone();
		FIRST_FOLLOW_SET.addAll(FOLLOW_SET);
		REL_OPS_MAP = new HashMap<String, ICodeNodeType>();
		REL_OPS_MAP.put("-", NEGATE);
		REL_OPS_MAP.put("//", NOT);
	}

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent parser.
	 */
	public PrimaryExpressionParser(TriangleParserTD parent) {
		super(parent);
	}

	/**
	 * Parse a Triangle Primary-ExpressionParser.
	 * 
	 * Primary-Expression ::= Integer-Literal | Character-Literal | V-name |
	 * Identifier ( Actual-Parameter-Sequence ) | Operator Primary-Expression |
	 * ( Expression ) | { Record-Aggregate } | [ Array-Aggregate ]
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
		ICodeNode primaryExpressionNode = null;
		TypeSpec defaultType = TrianglePredefined.undefinedType;

		token = currentToken();
		TriangleTokenType tokenType = (TriangleTokenType) token.getType();

		switch (tokenType) {
		case INTEGER:
			Integer constantValue = Integer.valueOf(token.getText());
			primaryExpressionNode = ICodeFactory
					.createICodeNode(INTEGER_CONSTANT);
			primaryExpressionNode.setAttribute(VALUE, constantValue);
			setLineNumber(primaryExpressionNode, token);
			primaryExpressionNode.setTypeSpec(integerType);
			token = synchronize(INTEGER, TriangleErrorCode.MISSING_CONSTANT,
					FOLLOW_SET);
			break;
		case CHAR:
			Character charValue = Character.valueOf(token.getText().trim()
					.charAt(0));
			primaryExpressionNode = ICodeFactory.createICodeNode(CHAR_CONSTANT);
			primaryExpressionNode.setAttribute(VALUE, charValue);
			setLineNumber(primaryExpressionNode, token);
			primaryExpressionNode.setTypeSpec(charType);
			token = synchronize(CHAR,
					TriangleErrorCode.MISSING_CHARACTER_LITERAL, FOLLOW_SET);
			break;
		case IDENTIFIER:
			// Need to distinguish between v-name statement or function call
			// Peek at next token
			token = peekToken();
			if ((TriangleTokenType) (token.getType()) == LEFT_PAREN) { // function
																		// call
				token = currentToken();
				ArrayList<SymTabEntry> formalParameterSeq = null;
				primaryExpressionNode = ICodeFactory.createICodeNode(CALL);
				setLineNumber(primaryExpressionNode, token);
				primaryExpressionNode.setAttribute(ID, token.getText());
				primaryExpressionNode.setTypeSpec(defaultType);
				SymTabEntry funcId = symTabStack.lookup(token.getText()
						.toLowerCase());
				if (funcId != null) {
					if (funcId.getDefinition() != DefinitionImpl.FUNCTION) {
						errorHandler.flag(token, IDENTIFIER_NOT_FUNCTION, this);
					}
					formalParameterSeq = (ArrayList<SymTabEntry>) funcId
							.getAttribute(SymTabKeyImpl.ROUTINE_PARMS);
					primaryExpressionNode.setTypeSpec(funcId.getTypeSpec());
				}
				token = nextToken(); // absorb identifier
				token = nextToken(); // absorb left parenthesis
				ActualParameterSequenceParser actualParameterSeq = new ActualParameterSequenceParser(
						this);
				primaryExpressionNode.addChild(actualParameterSeq.parse(token,
						formalParameterSeq));
				token = synchronize(RIGHT_PAREN, MISSING_RIGHT_PAREN,
						FOLLOW_SET);
			} else { // v-name statement
				VnameParser vname = new VnameParser(this);
				primaryExpressionNode = vname.parse(token);
			}
			break;
		case LEFT_PAREN:
			token = nextToken();
			ExpressionParser expression = new ExpressionParser(this);
			primaryExpressionNode = expression.parse(token);
			token = synchronize(RIGHT_PAREN, MISSING_RIGHT_PAREN, FOLLOW_SET);
			break;

		case LEFT_BRACE:
			token = nextToken();
			RecordAggregateParser recordAggregate = new RecordAggregateParser(
					this);
			primaryExpressionNode = recordAggregate.parse(token);
			token = synchronize(RIGHT_BRACE, MISSING_RIGHT_BRACE, FOLLOW_SET);
			break;
		case LEFT_BRACKET:
			token = nextToken();
			ArrayAggregateParser arrayAggregate = new ArrayAggregateParser(this);
			primaryExpressionNode = arrayAggregate.parse(token);
			token = synchronize(RIGHT_BRACKET, MISSING_RIGHT_BRACKET,
					FOLLOW_SET);
			break;
		case OPERATOR:
			if (token.getText().equals("-")){
				primaryExpressionNode = generateSpecialNegateOperator(token);
			} else {
				primaryExpressionNode = generateUnaryOperator(token);
			}
			break;
		default:
			errorHandler.flag(token, MISSING_EXPRESSION, this);
			primaryExpressionNode = ICodeFactory.createICodeNode(NO_OP);
			primaryExpressionNode.setTypeSpec(defaultType);
			break;
		}

		return primaryExpressionNode;
	}
	
	protected ICodeNode generateSpecialNegateOperator(Token token) throws Exception
	{
		// deal with special case of minus (-)
		ICodeNode primaryExpressionNode = ICodeFactory.createICodeNode(NEGATE);
		primaryExpressionNode.setTypeSpec(TrianglePredefined.undefinedType);
		setLineNumber(primaryExpressionNode, token);
		primaryExpressionNode.setAttribute(ID, token.getText());
		token = nextToken();
		PrimaryExpressionParser primaryExpressionParser = new PrimaryExpressionParser(
				this);
		ICodeNode operandNode = primaryExpressionParser.parse(token);
		primaryExpressionNode.addChild(operandNode);
		if (operandNode.equals(integerType)){
			errorHandler.flag(token, OPERAND_TYPE_MISMATCH, this);
		}
		return primaryExpressionNode;
	}
	
	protected ICodeNode generateUnaryOperator(Token token) throws Exception
	{
		token = currentToken();
		ICodeNode primaryExpressionNode = ICodeFactory.createICodeNode(CALL);
		primaryExpressionNode.setTypeSpec(TrianglePredefined.undefinedType);
		ICodeNode paramNode = ICodeFactory.createICodeNode(PARAMETERS);
		primaryExpressionNode.addChild(paramNode);
		setLineNumber(primaryExpressionNode, token);
		primaryExpressionNode.setAttribute(ID, token.getText());
		SymTabEntry operatorId = symTabStack.lookup(token.getText()
				.toLowerCase());
		if (operatorId != null) {
			if (operatorId.getDefinition() == DefinitionImpl.FUNCTION) {
				primaryExpressionNode.setTypeSpec(operatorId.getTypeSpec());
			} else {
				errorHandler.flag(token, OPERATOR_NOT_FUNCTION, this);
			}
		} else {
			errorHandler.flag(token, OPERATOR_NOT_DEFINED, this);
		}
		token = nextToken();
		PrimaryExpressionParser primaryExpressionParser = new PrimaryExpressionParser(
				this);
		ICodeNode operandNode = primaryExpressionParser.parse(token);
		paramNode.addChild(operandNode);
		if (operatorId != null) {
			@SuppressWarnings("unchecked")
			ArrayList<SymTabEntry> formalParamSeq = (ArrayList<SymTabEntry>) operatorId
					.getAttribute(SymTabKeyImpl.ROUTINE_PARMS);
			if (formalParamSeq.size() != 1) {
				errorHandler.flag(token, NUMBER_ACTUAL_FORMAL_NO_MATCH,
						this);
			} else {
				if (!formalParamSeq.get(0).getTypeSpec()
						.equals(operandNode.getTypeSpec())) {
					errorHandler.flag(token, OPERAND_TYPE_MISMATCH, this);
				}
			}
		}
		
		return primaryExpressionNode;
		
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