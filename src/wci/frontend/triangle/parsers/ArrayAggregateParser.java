package wci.frontend.triangle.parsers;

import wci.frontend.*;
import wci.frontend.triangle.*;
import wci.intermediate.*;
import wci.intermediate.symtabimpl.TrianglePredefined;

import java.util.EnumSet;

import static wci.frontend.triangle.TriangleErrorCode.ELEMENT_TYPE_MISMATCH;
import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.intermediate.icodeimpl.ICodeNodeTypeImpl.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>ArrayAggregateParser</h1>
 *
 * <p>Parse a Triangle Array-Aggregate.</p>
 *
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class ArrayAggregateParser extends TriangleParserTD
{
	static final EnumSet<TriangleTokenType> FIRST_SET;
	static final EnumSet<TriangleTokenType> FOLLOW_SET;
    static final EnumSet<TriangleTokenType> FIRST_FOLLOW_SET;
    static final EnumSet<TriangleTokenType> COMMA_SET;
    static {
    	FIRST_SET = EnumSet.of(IDENTIFIER, CHAR, INTEGER, OPERATOR,LEFT_PAREN,LEFT_BRACE,
				LEFT_BRACKET,LET, TriangleTokenType.IF);
    	FOLLOW_SET = EnumSet.of(RIGHT_BRACKET);
    	FIRST_FOLLOW_SET= FIRST_SET.clone();
    	FIRST_FOLLOW_SET.addAll(FOLLOW_SET);
    	
    	COMMA_SET = FIRST_FOLLOW_SET.clone();
    }
    
	/**
     * Constructor.
     * @param parent the parent parser.
     */
    public ArrayAggregateParser(TriangleParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a Triangle Array-Aggregate.
     * 
     * Array-Aggregate ::= Expression | Expression , Array-Aggregate
     * 
     * To be overridden by the specialized command parse subclasses.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token) throws Exception {
    	TriangleTokenType type = null;
    	ICodeNode prevNode = null;
    	int size = 0;
    	
    	token = currentToken();
    	ICodeNode arrayAggregateNode = ICodeFactory.createICodeNode(ARRAY_CONSTANT);
    	arrayAggregateNode.setTypeSpec(TrianglePredefined.undefinedType);
    	setLineNumber(arrayAggregateNode, token);
    	
    	do {
    		ExpressionParser expression = new ExpressionParser(this);
    		ICodeNode exprNode = expression.parse(token);
    		arrayAggregateNode.addChild(exprNode);
    		if (prevNode != null && exprNode != null && !prevNode.getTypeSpec().equals(exprNode.getTypeSpec())){
    			 errorHandler.flag(token, ELEMENT_TYPE_MISMATCH, this);
    		}
    		prevNode = exprNode;
    		size++;
    		token = currentToken();
    		type = (TriangleTokenType)token.getType();
    		if (type == COMMA){
    			token = nextToken();
    		}
    	} while( type == COMMA);

    	arrayAggregateNode.setTypeSpec(TypeFactory.createArrayType(size, prevNode.getTypeSpec()));
    	
        return arrayAggregateNode;
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
