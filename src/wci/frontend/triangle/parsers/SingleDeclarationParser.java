package wci.frontend.triangle.parsers;

import java.util.ArrayList;
import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.triangle.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.DefinitionImpl;

import static wci.frontend.triangle.TriangleErrorCode.*;
import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.CONSTANT_VALUE;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_SYMTAB;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_PARMS;
import static wci.intermediate.symtabimpl.SymTabKeyImpl.ROUTINE_ICODE;

/**
 * <h1>PrimaryExpressionParser</h1>
 * 
 * <p>
 * Parse a Triangle PrimaryExpression.
 * </p>
 * 
 * <p>
 * For instructional purposes only. No warranties.
 * </p>
 */
public class SingleDeclarationParser extends TriangleParserTD {
	static final EnumSet<TriangleTokenType> FIRST_SET;
	static final EnumSet<TriangleTokenType> FOLLOW_SET;
	static final EnumSet<TriangleTokenType> FIRST_FOLLOW_SET;
	
	static {
		FIRST_SET = EnumSet.of(CONST, VAR, PROC, FUNC, TYPE);
		FOLLOW_SET = EnumSet.of(SEMICOLON, IN);
		FIRST_FOLLOW_SET = FIRST_SET.clone();
	    FIRST_FOLLOW_SET.addAll(FOLLOW_SET);
	}

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            the parent parser.
	 */
	public SingleDeclarationParser(TriangleParserTD parent) {
		super(parent);
	}

	/**
	 * Parse a Triangle SingleDeclarationParser.
	 * 
	 * Single-Declaration ::=   const Identifier ~ Expression 
	 *                        | var Identifier : Type-Denoter
	 *                        | proc Identifier (Formal-Parameter-Sequence) ~ Single-Command
	 *                        | func Identifier (Formal-Parameter-Sequence) : Type-Denoter ~ Expression
	 *                        | Type Identifier ~ Type-Denoter
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
		SingleCommandParser singleCommand = null;
		ExpressionParser expression = null;
		FormalParameterSequenceParser formalParameterSequence = null;
		TypeDenoterParser typeDenoter = null;
		EnumSet<TriangleTokenType> syncSet=null;
		TypeSpec identifierType = null;
		Token identifierToken = null;
		ICode routineICode = null;
		
		token = currentToken();
		
		switch((TriangleTokenType)token.getType()) {
		case CONST:
			identifierToken = nextToken();
		    syncSet = EnumSet.of(TILDE);
			syncSet.addAll(ExpressionParser.FIRST_FOLLOW_SET);
			synchronize(IDENTIFIER, MISSING_IDENTIFIER, syncSet);
			syncSet.remove(TILDE);
			token = synchronize(TILDE, MISSING_TILDE, syncSet);
			expression = new ExpressionParser(this);
			ICodeNode expressionNode = expression.parse(token);
			
			SymTabEntry constantId = symTabStack.lookupLocal(identifierToken.getText().toLowerCase());
			// Enter the new identifier into the symbol table
            // but don't set how it's defined yet.
            if (constantId == null) {
                constantId = symTabStack.enterLocal(identifierToken.getText().toLowerCase());
                constantId.setDefinition(DefinitionImpl.CONSTANT);
                constantId.setAttribute(CONSTANT_VALUE, expressionNode);
                constantId.appendLineNumber(identifierToken.getLineNumber());
                constantId.setTypeSpec(expressionNode.getTypeSpec());
            }
            else {
                errorHandler.flag(identifierToken, IDENTIFIER_REDEFINED, this);
                constantId = null;
            }
			break;
		case VAR:
			identifierToken = nextToken();
			syncSet = EnumSet.of(COLON);
			syncSet.addAll(TypeDenoterParser.FIRST_FOLLOW_SET);
			synchronize(IDENTIFIER, MISSING_IDENTIFIER, syncSet);

			syncSet.remove(COLON);
			token = synchronize(COLON, MISSING_COLON, syncSet);
			
			typeDenoter = new TypeDenoterParser(this);
			identifierType = typeDenoter.parse(token);
			
			SymTabEntry variableId = symTabStack.lookupLocal(identifierToken.getText().toLowerCase());
			// Enter the new identifier into the symbol table
            // but don't set how it's defined yet.
            if (variableId == null) {
            	variableId = symTabStack.enterLocal(identifierToken.getText().toLowerCase());
            	variableId.setDefinition(DefinitionImpl.VARIABLE);
            	variableId.appendLineNumber(identifierToken.getLineNumber());
            	variableId.setTypeSpec(identifierType);
            }
            else {
                errorHandler.flag(identifierToken, IDENTIFIER_REDEFINED, this);
            }
			break;
		case PROC:
			identifierToken = nextToken();
			routineICode = ICodeFactory.createICode();
			syncSet = EnumSet.of(LEFT_PAREN);
			syncSet.addAll(FormalParameterSequenceParser.FIRST_FOLLOW_SET);
			token = synchronize(IDENTIFIER, MISSING_IDENTIFIER, syncSet);
			syncSet.remove(LEFT_PAREN);
			token = synchronize(LEFT_PAREN, MISSING_LEFT_PAREN, syncSet);
			SymTabEntry procId = symTabStack.lookupLocal(identifierToken.getText().toLowerCase());
			// Enter the new identifier into the symbol table
            // but don't set how it's defined yet.
            if (procId == null) {
            	procId = symTabStack.enterLocal(identifierToken.getText().toLowerCase());
            	procId.setTypeSpec(null);
            	procId.setDefinition(DefinitionImpl.PROCEDURE);
            	procId.appendLineNumber(identifierToken.getLineNumber());
            	procId.setAttribute(ROUTINE_SYMTAB, symTabStack.push());
            	procId.setAttribute(ROUTINE_ICODE, routineICode);
            }else {
                errorHandler.flag(identifierToken, IDENTIFIER_REDEFINED, this);
            }
			
			formalParameterSequence = new FormalParameterSequenceParser(this);
			ArrayList<SymTabEntry> procParamList = formalParameterSequence.parse(token);
			syncSet = EnumSet.of(TILDE);
			syncSet.addAll(SingleCommandParser.FIRST_FOLLOW_SET);
			token = synchronize(RIGHT_PAREN, MISSING_RIGHT_PAREN, syncSet);
			syncSet.remove(TILDE);
			token = synchronize(TILDE, MISSING_TILDE, syncSet);
			singleCommand = new SingleCommandParser(this);
			routineICode.setRoot(singleCommand.parse(token));
            if (procId != null) {
            	procId.setAttribute(ROUTINE_PARMS,procParamList);
            }
			break;
		case FUNC:
			identifierToken = nextToken();
			routineICode = ICodeFactory.createICode();
			SymTabEntry funcId = symTabStack.lookupLocal(identifierToken.getText().toLowerCase());
			// Enter the new identifier into the symbol table
            // but don't set how it's defined yet.
            if (funcId == null) {
            	funcId = symTabStack.enterLocal(identifierToken.getText().toLowerCase());
            	funcId.setDefinition(DefinitionImpl.FUNCTION);
            	funcId.appendLineNumber(identifierToken.getLineNumber());
            	funcId.setAttribute(ROUTINE_SYMTAB, symTabStack.push());
            	funcId.setAttribute(ROUTINE_ICODE, routineICode);
            }
            else {
                errorHandler.flag(identifierToken, IDENTIFIER_REDEFINED, this);
            }
			syncSet = EnumSet.of(LEFT_PAREN);
			syncSet.addAll(FormalParameterSequenceParser.FIRST_FOLLOW_SET);
			token = synchronize(IDENTIFIER, MISSING_IDENTIFIER, syncSet);
			syncSet.remove(LEFT_PAREN);
			token = synchronize(LEFT_PAREN, MISSING_LEFT_PAREN, syncSet);
			formalParameterSequence = new FormalParameterSequenceParser(this);
			ArrayList<SymTabEntry> funcParamList = formalParameterSequence.parse(token);
			syncSet = EnumSet.of(COLON);
			syncSet.addAll(TypeDenoterParser.FIRST_FOLLOW_SET);
			synchronize(RIGHT_PAREN, MISSING_RIGHT_PAREN, syncSet);
			syncSet.remove(COLON);
			token = synchronize(COLON, MISSING_COLON, syncSet);
			typeDenoter = new TypeDenoterParser(this);
			TypeSpec funcType = typeDenoter.parse(token);
			syncSet = ExpressionParser.FIRST_FOLLOW_SET.clone();
			token = synchronize(TILDE, MISSING_TILDE, syncSet);
			expression = new ExpressionParser(this);
			routineICode.setRoot(expression.parse(token));
            if (funcId != null) {
            	funcId.setTypeSpec(funcType);
            	funcId.setAttribute(ROUTINE_PARMS, funcParamList);
            }
           	symTabStack.pop();
			break;
		case TYPE:
			identifierToken = nextToken();
			syncSet = EnumSet.of(TILDE);
			syncSet.addAll(TypeDenoterParser.FIRST_FOLLOW_SET);
			synchronize(IDENTIFIER, MISSING_IDENTIFIER, syncSet);
			syncSet.remove(TILDE);
			token = synchronize(TILDE, MISSING_TILDE, syncSet);
			typeDenoter = new TypeDenoterParser(this);
			TypeSpec typeType = typeDenoter.parse(token);
			SymTabEntry typeId = symTabStack.lookupLocal(identifierToken.getText().toLowerCase());
			// Enter the new identifier into the symbol table
            // but don't set how it's defined yet.
            if (typeId == null) {
            	typeId = symTabStack.enterLocal(identifierToken.getText().toLowerCase());
            	typeId.setDefinition(DefinitionImpl.TYPE);
            	typeId.appendLineNumber(identifierToken.getLineNumber());
            	typeId.setTypeSpec(typeType);
            }
            else {
                errorHandler.flag(token, IDENTIFIER_REDEFINED, this);
            }
			break;
		default:
			errorHandler.flag(token, MISSING_DECLARATION, this);
			break;
		}
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