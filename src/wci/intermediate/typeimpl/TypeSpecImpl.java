package wci.intermediate.typeimpl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import wci.intermediate.*;
import wci.intermediate.symtabimpl.DefinitionImpl;

import static wci.intermediate.typeimpl.TypeFormImpl.ARRAY;
import static wci.intermediate.typeimpl.TypeFormImpl.SUBRANGE;
import static wci.intermediate.typeimpl.TypeKeyImpl.*;
import static wci.intermediate.symtabimpl.TrianglePredefined.integerType;
import static wci.intermediate.symtabimpl.TrianglePredefined.charType;

/**
 * <h1>TypeSpecImpl</h1>
 *
 * <p>A Pascal type specification implementation.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class TypeSpecImpl
    extends HashMap<TypeKey, Object>
    implements TypeSpec
{
    private TypeForm form;           // type form
    private SymTabEntry identifier;  // type identifier

    /**
     * Constructor.
     * @param form the type form.
     */
    public TypeSpecImpl(TypeForm form)
    {
        this.form = form;
        this.identifier = null;
    }

    /**
     * Constructor.
     * @param value a string value.
     */
    public TypeSpecImpl(String value)
    {
        this.form = ARRAY;

        TypeSpec indexType = new TypeSpecImpl(SUBRANGE);
        indexType.setAttribute(SUBRANGE_BASE_TYPE, integerType);
        indexType.setAttribute(SUBRANGE_MIN_VALUE, 1);
        indexType.setAttribute(SUBRANGE_MAX_VALUE, value.length());

        setAttribute(ARRAY_INDEX_TYPE, indexType);
        setAttribute(ARRAY_ELEMENT_TYPE, charType);
        setAttribute(ARRAY_ELEMENT_COUNT, value.length());
    }

    /**
     * Getter
     * @return the type form.
     */
    public TypeForm getForm()
    {
        return form;
    }

    /**
     * Setter.
     * @param identifier the type identifier (symbol table entry).
     */
    public void setIdentifier(SymTabEntry identifier)
    {
        this.identifier = identifier;
    }

    /**
     * Getter.
     * @return the type identifier (symbol table entry).
     */
    public SymTabEntry getIdentifier()
    {
        return identifier;
    }

    /**
     * Set an attribute of the specification.
     * @param key the attribute key.
     * @param value the attribute value.
     */
    public void setAttribute(TypeKey key, Object value)
    {
        this.put(key, value);
    }

    /**
     * Get the value of an attribute of the specification.
     * @param key the attribute key.
     * @return the attribute value.
     */
    public Object getAttribute(TypeKey key)
    {
        return this.get(key);
    }

    /**
     * @return true if this is a Pascal string type.
     */
    public boolean isPascalString()
    {
        if (form == ARRAY) {
            TypeSpec elmtType  = (TypeSpec) getAttribute(ARRAY_ELEMENT_TYPE);
            TypeSpec indexType = (TypeSpec) getAttribute(ARRAY_INDEX_TYPE);

            return (elmtType.baseType()  == charType) &&
                   (indexType.baseType() == integerType);
        }
        else {
            return false;
        }
    }

    /**
     * @return the base type of this type.
     */
    public TypeSpec baseType()
    {
        return form == SUBRANGE ? (TypeSpec) getAttribute(SUBRANGE_BASE_TYPE)
                                : this;
    }
    
    /**
     * Returns a true if this TypeSpec is structurally equivalent with obj.
     * 
     * @param obj the TypeSpec instance to compare this TypeSpec instance to
     * @return true if this TypeSpec is structurally equivalent with obj, otherwise false.
     */
    @SuppressWarnings("unchecked")
	@Override
    public boolean equals(Object obj)
    {
    	if (this == obj){
    		return true;
    	}
    	if (obj == null){
    		return false;
    	}
    	if (!(obj instanceof TypeSpec)){
    		return false;
    	}
    	TypeSpec otherType = (TypeSpec)obj;
    	if (this.getForm() != otherType.getForm()){
    		return false;
    	}
    	
    	boolean isEqual =true;
    	
    	if (this.getForm()==TypeFormImpl.SCALAR) {
    		isEqual = false;
    	} else if (this.getForm()==TypeFormImpl.ARRAY) {
    		TypeSpec thisIndexType = (TypeSpec)this.getAttribute(ARRAY_INDEX_TYPE);
    		TypeSpec otherIndexType = (TypeSpec)otherType.getAttribute(ARRAY_INDEX_TYPE);
    		TypeSpec thisElementType = (TypeSpec)this.getAttribute(ARRAY_ELEMENT_TYPE);
    		TypeSpec otherElementType = (TypeSpec)otherType.getAttribute(ARRAY_ELEMENT_TYPE);
    		int thisElementCount = (Integer)this.getAttribute(ARRAY_ELEMENT_COUNT);
    		int otherElementCount = (Integer)otherType.getAttribute(ARRAY_ELEMENT_COUNT);
    		isEqual =  thisElementCount==otherElementCount && 
    				      thisElementType.equals(otherElementType) && 
    				          thisIndexType.equals(otherIndexType);
    	} else if (this.getForm()==TypeFormImpl.RECORD) {
    		List<SymTabEntry> thisFields = (List<SymTabEntry>)this.getAttribute(TypeKeyImpl.RECORD_FIELD_LIST);
    		List<SymTabEntry> otherFields = (List<SymTabEntry>)otherType.getAttribute(TypeKeyImpl.RECORD_FIELD_LIST);
    		Iterator<SymTabEntry> thisIter = thisFields.iterator();
    		Iterator<SymTabEntry> otherIter = otherFields.iterator();
    		while(isEqual && thisIter.hasNext() && otherIter.hasNext()){
    			SymTabEntry thisEntry = thisIter.next();
    			SymTabEntry otherEntry = otherIter.next();
    			if(!thisEntry.getTypeSpec().equals(otherEntry.getTypeSpec())){
    				isEqual = false;
    			}
    		}
    		isEqual = isEqual && !thisIter.hasNext() && !otherIter.hasNext();
    	}
    	
    	return isEqual;
    }
}
