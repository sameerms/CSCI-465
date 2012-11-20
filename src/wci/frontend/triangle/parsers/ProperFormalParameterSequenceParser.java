package wci.frontend.triangle.parsers;

import wci.frontend.*;
import wci.frontend.triangle.*;
import wci.intermediate.*;

import java.util.ArrayList;
import java.util.EnumSet;

import static wci.frontend.triangle.TriangleTokenType.*;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.*;

/**
 * <h1>ProperFormalParameterSequenceParser</h1>
 *
 * <p>Parse a Triangle Proper-Formal-Parameter-Sequence.</p>
 *
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class ProperFormalParameterSequenceParser extends TriangleParserTD
{
	static final EnumSet<TriangleTokenType> FIRST_SET;
	static final EnumSet<TriangleTokenType> FOLLOW_SET;
    static final EnumSet<TriangleTokenType> FIRST_FOLLOW_SET;
    static final EnumSet<TriangleTokenType> COMMA_SET;
    static {
    	FIRST_SET = EnumSet.of(IDENTIFIER, VAR, PROC, FUNC, RIGHT_PAREN);
    	FOLLOW_SET = EnumSet.of(RIGHT_PAREN);
    	FIRST_FOLLOW_SET = FIRST_SET.clone();
    	FIRST_FOLLOW_SET.addAll(FOLLOW_SET);
    	COMMA_SET = FIRST_FOLLOW_SET.clone();
    }
    
	/**
     * Constructor.
     * @param parent the parent parser.
     */
    public ProperFormalParameterSequenceParser(TriangleParserTD parent)
    {
        super(parent);
    }

    /**
     * Parse a Triangle Proper-Formal-Parameter-Sequence.
     * 
     * Proper-Formal-Parameter-Sequence ::=  Formal-Parameter 
     *                                     | Formal-Parameter, Proper-Formal-Parameter-Sequence
     * 
     * To be overridden by the specialized command parse subclasses.
     * @param token the initial token.
     * @return The list of symbol table entries of the parameters.
     * @throws Exception if an error occurred.
     */
    public ArrayList<SymTabEntry> parse(Token token) throws Exception {
    	ArrayList<SymTabEntry> properFormalParameterSequenceList = new ArrayList<SymTabEntry>();
        TriangleTokenType type = null;
        
        do{
        	FormalParameterParser formalParameter = new FormalParameterParser(this);
        	properFormalParameterSequenceList.add(formalParameter.parse(token));
        	token = currentToken();
        	if ((type = (TriangleTokenType)token.getType()) == COMMA){
        		token = nextToken(); // absorb comma
        	}
        }while( type == COMMA);
        
        return properFormalParameterSequenceList;
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
