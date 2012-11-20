package wci.frontend.triangle.parsers;

import wci.frontend.*;
import wci.frontend.triangle.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.DefinitionImpl;

import java.util.EnumSet;

import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.FUNC_PARM;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.PROC_PARM;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.VALUE_PARM;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.VAR_PARM;
import static wci.intermediate.symtabimpl.TrianglePredefined.undefinedType;
import static wci.frontend.triangle.TriangleErrorCode.MISSING_ACTUAL_PARAMETER;
import static wci.frontend.triangle.TriangleErrorCode.PROCEDURE_UNDEFINED;
import static wci.frontend.triangle.TriangleErrorCode.IDENTIFIER_NOT_PROCEDURE;
import static wci.frontend.triangle.TriangleErrorCode.IDENTIFIER_NOT_FUNCTION;
import static wci.frontend.triangle.TriangleErrorCode.FUNCTION_UNDEFINED;
import static wci.frontend.triangle.TriangleErrorCode.FORMAL_PARAM_NOT_VAR;
import static wci.frontend.triangle.TriangleErrorCode.PARAMETER_TYPE_MISMATCH;
import static wci.frontend.triangle.TriangleErrorCode.FORMAL_PARAM_NOT_PROC;
import static wci.frontend.triangle.TriangleErrorCode.FORMAL_PARAM_NOT_FUNC;
import static wci.frontend.triangle.TriangleErrorCode.FORMAL_PARAM_NOT_VALUE;
import static wci.frontend.triangle.TriangleErrorCode.PROC_PARM_MISMATCH;
import static wci.frontend.triangle.TriangleErrorCode.FUNC_PARM_MISMATCH;

/**
 * <h1>Actual Parameter Parser</h1>
 * 
 * <p>
 * Parse a Triangle Actual Parameter.
 * </p>
 * 
 * <p>
 * For instructional purposes only. No warranties.
 * </p>
 */
public class ActualParameterParser extends TriangleParserTD {
	static final EnumSet<TriangleTokenType> FIRST_SET;
	static final EnumSet<TriangleTokenType> FOLLOW_SET;
	static final EnumSet<TriangleTokenType> FIRST_FOLLOW_SET;
	static {
		FIRST_SET = EnumSet.of(IDENTIFIER, CHAR, INTEGER, OPERATOR, LEFT_PAREN,
				LEFT_BRACE, LEFT_BRACKET, LET, TriangleTokenType.IF, VAR, PROC,
				FUNC);
		FOLLOW_SET = EnumSet.of(COMMA, RIGHT_PAREN);
		FIRST_FOLLOW_SET = FIRST_SET.clone();
		FIRST_FOLLOW_SET.addAll(FOLLOW_SET);
	}

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent parser.
	 */
	public ActualParameterParser(TriangleParserTD parent) {
		super(parent);
	}

	/**
	 * Parse a Triangle Actual-Parameter.
	 * 
	 * Actual-Parameter ::= Expression | var V-name 
	 *                     | proc Identifier | func Identifier
	 * 
	 * To be overridden by the specialized command parse subclasses.
	 * 
	 * @param token
	 *            the initial token.
	 * @return the root node of the generated parse tree.
	 * @throws Exception
	 *             if an error occurred.
	 */
	public ICodeNode parse(Token token, SymTabEntry formalParam) throws Exception {
		ICodeNode actualParameterNode = null;
		Token identToken = null;
		
		token = currentToken();
		TriangleTokenType tokenType = (TriangleTokenType) token.getType();

		switch (tokenType) {
		case VAR:
			if (formalParam.getDefinition() != DefinitionImpl.VAR_PARM){
				errorHandler.flag(token, FORMAL_PARAM_NOT_VAR, this);
			}
			token = nextToken(); // absorb the var
			VnameParser vnameParser = new VnameParser(this);
			ICodeNode varNode = vnameParser.parse(token);
			actualParameterNode = ICodeFactory.createICodeNode(VAR_PARM);
			setLineNumber(actualParameterNode,token);
			actualParameterNode.addChild(varNode);
			if (varNode != null){
				actualParameterNode.setTypeSpec(varNode.getTypeSpec());
			} else {
				actualParameterNode.setTypeSpec(undefinedType);
			}
			if (formalParam == null || 
					!formalParam.getTypeSpec().equals(actualParameterNode.getTypeSpec())){
				errorHandler.flag(token, PARAMETER_TYPE_MISMATCH, this);
			}
			break;
		case PROC:
			if (formalParam.getDefinition() != DefinitionImpl.PROC_PARM){
				errorHandler.flag(token, FORMAL_PARAM_NOT_PROC, this);
			}
			identToken = nextToken(); // absorb the proc
			
			actualParameterNode = ICodeFactory.createICodeNode(PROC_PARM);
			actualParameterNode.setAttribute(ID, identToken.getText());
			setLineNumber(actualParameterNode,identToken);
			actualParameterNode.setTypeSpec(undefinedType);
			
			token = synchronize(IDENTIFIER,
					TriangleErrorCode.MISSING_PROC_IDENTIFIER, FOLLOW_SET);
			
			SymTabEntry procActualParam = symTabStack.lookup(identToken.getText().toLowerCase());
			if (procActualParam != null){
				if (procActualParam.getDefinition() != DefinitionImpl.PROCEDURE){
					errorHandler.flag(token, IDENTIFIER_NOT_PROCEDURE, this);
				} else {
					if (!procActualParam.equals(formalParam)){
						errorHandler.flag(token, PROC_PARM_MISMATCH, this);
					}
				}
			} else {
				errorHandler.flag(token, PROCEDURE_UNDEFINED, this);
			}
			break;
		case FUNC:
			if (formalParam.getDefinition() != DefinitionImpl.FUNC_PARM){
				errorHandler.flag(token, FORMAL_PARAM_NOT_FUNC, this);
			}
			identToken = nextToken(); // absorb the func
			actualParameterNode = ICodeFactory.createICodeNode(FUNC_PARM);
			actualParameterNode.setAttribute(ID, identToken.getText());
			setLineNumber(actualParameterNode,identToken);
			actualParameterNode.setTypeSpec(undefinedType);
			token = synchronize(IDENTIFIER,
					TriangleErrorCode.MISSING_FUNC_IDENTIFIER, FOLLOW_SET);
			SymTabEntry funcActualParam = symTabStack.lookup(identToken.getText().toLowerCase());
			if (funcActualParam != null){
				if (funcActualParam.getDefinition() != DefinitionImpl.FUNCTION){
					errorHandler.flag(token, IDENTIFIER_NOT_FUNCTION, this);
				} else {
					actualParameterNode.setTypeSpec(funcActualParam.getTypeSpec());
					if (!funcActualParam.equals(formalParam)){
						errorHandler.flag(token, FUNC_PARM_MISMATCH, this);
					}
				}
			} else {
				errorHandler.flag(token, FUNCTION_UNDEFINED, this);
			}
			break;
		default:
			if (ExpressionParser.FIRST_SET.contains(tokenType)){
				if (formalParam.getDefinition() != DefinitionImpl.VALUE_PARM){
					errorHandler.flag(token, FORMAL_PARAM_NOT_VALUE, this);
				}
				actualParameterNode = ICodeFactory.createICodeNode(VALUE_PARM);
				setLineNumber(actualParameterNode,token);
				ExpressionParser expressionParser = new ExpressionParser(this);
				ICodeNode exprNode = expressionParser.parse(token);
				actualParameterNode.addChild(exprNode);
				if (exprNode != null){
					actualParameterNode.setTypeSpec(exprNode.getTypeSpec());
				} else {
					actualParameterNode.setTypeSpec(undefinedType);
				}
				if (!formalParam.getTypeSpec().equals(actualParameterNode.getTypeSpec())){
					errorHandler.flag(token, PARAMETER_TYPE_MISMATCH, this);
				}
			}
			else{
				errorHandler.flag(token, MISSING_ACTUAL_PARAMETER, this);
			}
			break;
		}

		return actualParameterNode;
	}

	/**
	 * Set the current line number as a statement node attribute.
	 * 
	 * @param node
	 *            ICodeNode
	 * @param token
	 *            Token
	 */
	protected void setLineNumber(ICodeNode node, Token token) {
		if (node != null) {
			node.setAttribute(LINE, token.getLineNumber());
		}
	}

}
