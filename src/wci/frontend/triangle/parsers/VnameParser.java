package wci.frontend.triangle.parsers;

import wci.frontend.*;
import wci.frontend.triangle.*;
import wci.intermediate.*;

import java.util.EnumSet;

import static wci.frontend.triangle.TriangleErrorCode.*;
import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.typeimpl.TypeFormImpl.RECORD;
import static wci.intermediate.typeimpl.TypeFormImpl.ARRAY;
import static wci.intermediate.typeimpl.TypeKeyImpl.RECORD_SYMTAB;
import static wci.intermediate.typeimpl.TypeKeyImpl.ARRAY_ELEMENT_TYPE;
import static wci.intermediate.symtabimpl.TrianglePredefined.integerType;
import static wci.intermediate.symtabimpl.TrianglePredefined.undefinedType;
import static wci.intermediate.symtabimpl.DefinitionImpl.CONSTANT;
/**
 * <h1>VnameParser</h1>
 * 
 * <p>
 * Parse a Triangle Vname.
 * </p>
 * 
 * <p>
 * For instructional purposes only. No warranties.
 * </p>
 */
public class VnameParser extends TriangleParserTD {
	static final EnumSet<TriangleTokenType> FIRST_SET;
	static final EnumSet<TriangleTokenType> FOLLOW_SET;
	static final EnumSet<TriangleTokenType> FIRST_FOLLOW_SET;
	static {
		FIRST_SET = EnumSet.of(IDENTIFIER);
		FOLLOW_SET = EnumSet.of(DOT, LEFT_BRACKET, COLON_EQUALS, THEN, ELSE,
				DO, COMMA, RIGHT_PAREN, RIGHT_BRACKET, SEMICOLON, END,
				IDENTIFIER, BEGIN, LET, TriangleTokenType.IF, WHILE, IN, CHAR, INTEGER, OPERATOR,
				LEFT_PAREN, LEFT_BRACE);
		FIRST_FOLLOW_SET = FIRST_SET.clone();
		FIRST_FOLLOW_SET.addAll(FOLLOW_SET);
	}

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent parser.
	 */
	public VnameParser(TriangleParserTD parent) {
		super(parent);
	}

	/**
	 * Parse a Triangle Vname.
	 * 
	 * Vname ::= identifier( .identifier | [Expression] )*
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
		ICodeNode vnameNode = null;
		token = currentToken();
		Token identToken = token;
		
		vnameNode = ICodeFactory.createICodeNode(VARIABLE);
		setLineNumber(vnameNode,token);
		vnameNode.setAttribute(ID, token.getText());
		vnameNode.setTypeSpec(undefinedType);
		
		SymTabEntry identId = symTabStack.lookup(token.getText().toLowerCase());
        if (identId == null) {
            errorHandler.flag(token, IDENTIFIER_UNDEFINED, this);
        } else if (identId.getDefinition() == CONSTANT){
        	errorHandler.flag(token, IDENTIFIER_CONSTANT, this);
        } else {
        	vnameNode.setTypeSpec(identId.getTypeSpec());
        }
		
        token = synchronize(IDENTIFIER, TriangleErrorCode.MISSING_IDENTIFIER, FIRST_FOLLOW_SET);
		TriangleTokenType tokenType = (TriangleTokenType) token.getType();
		while (tokenType == DOT || tokenType == LEFT_BRACKET) {
			if (tokenType == DOT) {
				token = nextToken();
				ICodeNode fieldNode = ICodeFactory.createICodeNode(FIELD);
			    fieldNode.setAttribute(ID, token.getText());
			    setLineNumber(fieldNode,token);
				 vnameNode.addChild(fieldNode);
				if (identId != null && token.getType() == IDENTIFIER){
					TypeSpec identType = identId.getTypeSpec();
					if (identType.getForm() == RECORD){
						symTabStack.push((SymTab)identType.getAttribute(RECORD_SYMTAB));
						identId = symTabStack.lookupLocal(token.getText().toLowerCase());
						fieldNode.setTypeSpec(identId.getTypeSpec());
						symTabStack.pop();
					} else {
						errorHandler.flag(identToken, NOT_A_RECORD, this);
					}
					
					vnameNode.setTypeSpec(identId.getTypeSpec());
					identToken = token;
				}
				token = synchronize(IDENTIFIER,
						TriangleErrorCode.MISSING_IDENTIFIER, FIRST_FOLLOW_SET);
			} else if (tokenType == LEFT_BRACKET) {
				token = nextToken();
				ExpressionParser expression = new ExpressionParser(this);
				ICodeNode exprNode = expression.parse(token);
				TypeSpec exprType = exprNode != null ? exprNode.getTypeSpec() : undefinedType;
				
				if (identId != null){
					TypeSpec identType = identId.getTypeSpec();
					if (identType.getForm() == ARRAY){
						vnameNode.setTypeSpec((TypeSpec)identId.getTypeSpec().getAttribute(ARRAY_ELEMENT_TYPE));
					} else {
						errorHandler.flag(identToken, NOT_AN_ARRAY, this);
					}
				}
				
				if (!exprType.equals(integerType)){
					errorHandler.flag(token, INVALID_INDEX_TYPE, this);
				}
				
				ICodeNode subscriptNode = ICodeFactory.createICodeNode(SUBSCRIPTS);
				setLineNumber(subscriptNode,token);
				subscriptNode.addChild(exprNode);
				subscriptNode.setTypeSpec(exprType);
				vnameNode.addChild(subscriptNode);
				token = synchronize(RIGHT_BRACKET,TriangleErrorCode.MISSING_RIGHT_BRACKET,
						FIRST_FOLLOW_SET);
			}
			tokenType = (TriangleTokenType) token.getType();
		}
		return vnameNode;
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
