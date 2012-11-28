package wci.intermediate.symtabimpl;

import wci.intermediate.Definition;

/**
 * <h1>DefinitionImpl</h1>
 *
 * <p>How a Pascal symbol table entry is defined.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public enum DefinitionImpl implements Definition
{
    CONSTANT, ENUMERATION_CONSTANT("enumeration constant"),
    TYPE, VARIABLE(true), FIELD("record field",true),
    VALUE_PARM("value parameter"), VAR_PARM("VAR parameter",true),
    PROGRAM_PARM("program parameter"),
    FUNC_PARM("function parameter"),
    PROC_PARM("procedure parameter"),
    PROGRAM, PROCEDURE, FUNCTION,
    UNDEFINED;

    private String text;
    private boolean isAssignable;
    
    /**
     * Constructor.
     */
    DefinitionImpl()
    {
        this.text = this.toString().toLowerCase();
        isAssignable = false;
    }

    /**
     * Constructor.
     */
    DefinitionImpl(boolean isAssignable)
    {
        this.text = this.toString().toLowerCase();
        this.isAssignable = isAssignable;
    }
    
    /**
     * Constructor.
     * @param text the text for the definition code.
     */
    DefinitionImpl(String text)
    {
        this.text = text;
        isAssignable = false;
    }
    
    DefinitionImpl(String text, boolean isAssignable)
    {
        this.text = text;
        this.isAssignable = isAssignable;
    }

    /**
     * Getter.
     * @return the text of the definition code.
     */
    public String getText()
    {
        return text;
    }
    
    /**
     * Getter.
     * @return whether this definition is assignable a value.
     */
    public boolean isAssignable()
    {
    	return isAssignable;
    }
}
