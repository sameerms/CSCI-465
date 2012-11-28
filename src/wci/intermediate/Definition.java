package wci.intermediate;

/**
 * <h1>Definition</h1>
 *
 * <p>The interface for how a symbol table entry is defined.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public interface Definition
{
    /**
     * Getter.
     * @return the text of the definition.
     */
    public String getText();
    
    /**
     * Getter.
     * @return whether this definition is assignable a value.
     */
    public boolean isAssignable();
}
