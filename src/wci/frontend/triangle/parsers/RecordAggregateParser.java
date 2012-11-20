package wci.frontend.triangle.parsers;

import wci.frontend.*;
import wci.frontend.triangle.*;
import wci.intermediate.*;
import wci.intermediate.icodeimpl.ICodeNodeTypeImpl;

import java.util.ArrayList;
import java.util.EnumSet;

import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;
import static wci.frontend.triangle.TriangleErrorCode.*;

/**
 * <h1>RecordTypeDenoterParser</h1>
 *
 * <p>Parse a Triangle command statement.</p>
 *
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class RecordAggregateParser extends TriangleParserTD
{
	static final EnumSet<TriangleTokenType> FIRST_SET;
	static final EnumSet<TriangleTokenType> FOLLOW_SET;
    static final EnumSet<TriangleTokenType> FIRST_FOLLOW_SET;
    static final EnumSet<TriangleTokenType> TILDE_SET;
    static final EnumSet<TriangleTokenType> COMMA_SET;
    static {
    	FIRST_SET = EnumSet.of(IDENTIFIER);
    	FOLLOW_SET = EnumSet.of(RIGHT_BRACE);
    	
    	FIRST_FOLLOW_SET= FIRST_SET.clone();
    	FIRST_FOLLOW_SET.addAll(FOLLOW_SET);
    	
    	TILDE_SET = EnumSet.of(IDENTIFIER, CHAR, INTEGER, OPERATOR,LEFT_PAREN,LEFT_BRACE,
				LEFT_BRACKET,LET, TriangleTokenType.IF);
    	TILDE_SET.addAll(FOLLOW_SET);
    	
    	COMMA_SET = FIRST_FOLLOW_SET.clone();
    }
    
	/**
     * Constructor.
     * @param parent the parent parser.
     */
    public RecordAggregateParser(TriangleParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a Triangle Record-Aggregate.
     * 
     * Record-Aggregate ::= Expression | Expression , Array-Aggregate
     * 
     * To be overridden by the specialized command parse subclasses.
     * @param token the initial token.
     * @return the root node of the generated parse tree.
     * @throws Exception if an error occurred.
     */
    public ICodeNode parse(Token token) throws Exception {
    	ICodeNode recordAggregateNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.RECORD_CONSTANT);
    	TriangleTokenType type = null;
    	
    	token = currentToken();
    	
    	setLineNumber(recordAggregateNode,token);
    	
    	do {
    		Token identToken = token;
    		token = synchronize(IDENTIFIER, MISSING_IDENTIFIER, TILDE_SET);
    		token = synchronize(TILDE, MISSING_TILDE, TILDE_SET);
    		ExpressionParser expression = new ExpressionParser(this);
    		ICodeNode exprNode = expression.parse(token);
    		ICodeNode fieldNode = ICodeFactory.createICodeNode(ICodeNodeTypeImpl.FIELD);
    		recordAggregateNode.addChild(fieldNode);
    		fieldNode.setAttribute(ID, identToken.getText());
    		setLineNumber(fieldNode,identToken);
    		fieldNode.setTypeSpec(exprNode.getTypeSpec());
    		fieldNode.addChild(exprNode);
    		token = currentToken();
    		type = (TriangleTokenType)token.getType();
    		if (type == COMMA){
    			token = nextToken();
    		}
    	} while( type == COMMA);
    	
        return recordAggregateNode;
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
