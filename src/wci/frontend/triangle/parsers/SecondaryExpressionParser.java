package wci.frontend.triangle.parsers;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import wci.frontend.*;
import wci.frontend.triangle.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.DefinitionImpl;
import wci.intermediate.symtabimpl.SymTabKeyImpl;

import static wci.frontend.triangle.TriangleErrorCode.*;
import static wci.frontend.triangle.TriangleTokenType.*;
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
	}

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent parser.
	 */
	public SecondaryExpressionParser(TriangleParserTD parent) {
		super(parent);
	}

	/**
	 * Parse a Triangle Secondary-Expression.
	 * 
	 * Secondary-Expression ::= Primary-Expression (Operator
	 * Primary-Expression)*
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
	@SuppressWarnings("unchecked")
	public ICodeNode parse(Token token) throws Exception {
		ICodeNode secondaryExpressionNode = null;
		ICodeNode leftOperand = null;
		ICodeNode rightOperand = null;
		
		token = currentToken();
		Token leftOperandToken = token;
		
		PrimaryExpressionParser primaryExpressionParser = new PrimaryExpressionParser(
				this);
		leftOperand = primaryExpressionParser.parse(token);
		
		token = currentToken();

		while ((TriangleTokenType) token.getType() == OPERATOR) {
			List<SymTabEntry> formalParamList = null;

			ICodeNode opNode = null;
			ICodeNode paramNode = null;

			opNode = ICodeFactory.createICodeNode(CALL);
			setLineNumber(opNode, token);
			opNode.setAttribute(ID, token.getText());
			paramNode = ICodeFactory.createICodeNode(PARAMETERS);
			opNode.addChild(paramNode);

			SymTabEntry operatorId = symTabStack.lookup(token.getText()
					.toLowerCase());
			if (operatorId != null) {
				if (operatorId.getDefinition() == DefinitionImpl.FUNCTION) {

					opNode.setTypeSpec(operatorId.getTypeSpec());
					formalParamList = (ArrayList<SymTabEntry>) operatorId
							.getAttribute(SymTabKeyImpl.ROUTINE_PARMS);
				} else {
					errorHandler.flag(token, OPERATOR_NOT_FUNCTION, this);
				}
			} else {
				errorHandler.flag(token, OPERATOR_NOT_DEFINED, this);
			}
			
			ICodeNode valueParm = ICodeFactory.createICodeNode(VALUE_PARM);
			valueParm.setTypeSpec(leftOperand.getTypeSpec());
			valueParm.addChild(leftOperand);
			paramNode.addChild(valueParm);
			
			token = nextToken(); // absorb operator
			Token rightOperandToken = token;
			
			primaryExpressionParser = new PrimaryExpressionParser(this);
			rightOperand = primaryExpressionParser.parse(token);
			valueParm = ICodeFactory.createICodeNode(VALUE_PARM);
			valueParm.setTypeSpec(rightOperand.getTypeSpec());
			valueParm.addChild(rightOperand);
			paramNode.addChild(valueParm);
			
			if (formalParamList.size() == 2){
				if (formalParamList.get(0).getDefinition() != DefinitionImpl.VALUE_PARM){
					errorHandler.flag(leftOperandToken, NOT_EXPECTING_VALUE_PARM, this);
				} else if (!formalParamList.get(0).getTypeSpec().equals(leftOperand.getTypeSpec())){
					errorHandler.flag(leftOperandToken, OPERAND_TYPE_MISMATCH, this);
				}
				if (formalParamList.get(1).getDefinition() != DefinitionImpl.VALUE_PARM){
					errorHandler.flag(rightOperandToken, NOT_EXPECTING_VALUE_PARM, this);
				} else if (!formalParamList.get(1).getTypeSpec().equals(rightOperand.getTypeSpec())){
					errorHandler.flag(rightOperandToken, OPERAND_TYPE_MISMATCH, this);
				}
			} else {
				errorHandler.flag(token, OPERAND_MISMATCH, this);
			}
			
			secondaryExpressionNode = opNode;
			
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