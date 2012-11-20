package wci.frontend.triangle.parsers;

import java.util.ArrayList;
import java.util.EnumSet;

import wci.frontend.*;
import wci.frontend.triangle.*;
import wci.intermediate.*;

import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.frontend.triangle.TriangleErrorCode.MISSING_IDENTIFIER;
import static wci.frontend.triangle.TriangleErrorCode.MISSING_TYPE_DEFINITION;
import static wci.frontend.triangle.TriangleErrorCode.INVALID_TYPE;

/**
 * <h1>TypeDenoterParser</h1>
 *
 * <p>Parse a Triangle Type Denoter.</p>
 *
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class TypeDenoterParser extends TriangleParserTD
{
	static final EnumSet<TriangleTokenType> FIRST_SET;
	static final EnumSet<TriangleTokenType> FOLLOW_SET;
    static final EnumSet<TriangleTokenType> FIRST_FOLLOW_SET;
    static final EnumSet<TriangleTokenType> INTEGER_SET;
    static {
    	FIRST_SET = EnumSet.of(IDENTIFIER,ARRAY,TriangleTokenType.RECORD);
    	FOLLOW_SET = EnumSet.of(COMMA, RIGHT_PAREN, TILDE, END, SEMICOLON, IN);
    	FIRST_FOLLOW_SET= FIRST_SET.clone();
    	FIRST_FOLLOW_SET.addAll(FOLLOW_SET);
    	INTEGER_SET = EnumSet.of(OF);
    	INTEGER_SET.addAll(FIRST_SET);
    }
    
	/**
     * Constructor.
     * @param parent the parent parser.
     */
    public TypeDenoterParser(TriangleParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a Triangle Type-Denoter.
     * 
     * Type-Denoter ::= Identifier | array Integer-Literal of Type-Denoter | Record Record-Type-Denoter end
     * 
     * To be overridden by the specialized command parse subclasses.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public TypeSpec parse(Token token) throws Exception {
    	TypeSpec typeDenoterType = null;
    	token = currentToken();
    	
    	switch((TriangleTokenType)token.getType()) {
    	case IDENTIFIER:
    		String name = token.getText().toLowerCase();
    		SymTabEntry typeId = symTabStack.lookup(name);
			// If type is not in symbol table, then an error occurred.
            if (typeId != null) {
                typeDenoterType = typeId.getTypeSpec();
            } else {
                errorHandler.flag(token, INVALID_TYPE, this);
            }
    		token = synchronize(IDENTIFIER, MISSING_IDENTIFIER, FOLLOW_SET);
    		break;
    	case ARRAY:
    		token = nextToken();
    		int arraySize = 0;
    		if (token.getType() == INTEGER){
    			arraySize = (Integer)token.getValue();
    		}
    		synchronize(INTEGER, TriangleErrorCode.MISSING_CONSTANT, INTEGER_SET);
    		token = synchronize(OF, TriangleErrorCode.MISSING_OF, FIRST_FOLLOW_SET);
    		TypeDenoterParser typeDenoter = new TypeDenoterParser(this);
    		typeDenoterType = typeDenoter.parse(token);
    		typeDenoterType = TypeFactory.createArrayType(arraySize, typeDenoterType);
    		break;
    	case RECORD:
    		token = nextToken();
    		symTabStack.push();
    		RecordTypeDenoterParser recordTypeDenoter = new RecordTypeDenoterParser(this);
    		ArrayList<SymTabEntry> recordTypeDeonterList = recordTypeDenoter.parse(token);
    		token = synchronize(END, TriangleErrorCode.MISSING_END, FOLLOW_SET);
    		typeDenoterType = TypeFactory.createRecordType(symTabStack.pop(),recordTypeDeonterList);
    		break;
    	default:
    		errorHandler.flag(token, MISSING_TYPE_DEFINITION, this);
    	}
    	
        return typeDenoterType;
    }

    /**
     * Set the current line number as a statement node attribute.
     * @param node ICodeNode
     * @param token Token
     */
    protected void setLineNumber(ICodeNode node, Token token)
    {
        if (node != null) {
            node.setAttribute(LINE, token.getLineNumber());
        }
    }

    }
