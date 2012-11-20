package wci.intermediate;

import java.util.List;

import wci.intermediate.typeimpl.*;
import static wci.intermediate.symtabimpl.TrianglePredefined.integerType;
import static wci.intermediate.typeimpl.TypeFormImpl.SUBRANGE;
import static wci.intermediate.typeimpl.TypeKeyImpl.ARRAY_ELEMENT_COUNT;
import static wci.intermediate.typeimpl.TypeKeyImpl.ARRAY_ELEMENT_TYPE;
import static wci.intermediate.typeimpl.TypeKeyImpl.ARRAY_INDEX_TYPE;
import static wci.intermediate.typeimpl.TypeKeyImpl.SUBRANGE_BASE_TYPE;
import static wci.intermediate.typeimpl.TypeKeyImpl.SUBRANGE_MAX_VALUE;
import static wci.intermediate.typeimpl.TypeKeyImpl.SUBRANGE_MIN_VALUE;


/**
 * <h1>TypeFactory</h1>
 *
 * <p>A factory for creating type specifications.</p>
 *
 * <p>Copyright (c) 2009 by Ronald Mak</p>
 * <p>For instructional purposes only.  No warranties.</p>
 */
public class TypeFactory
{
    /**
     * Create a type specification of a given form.
     * @param form the form.
     * @return the type specification.
     */
    public static TypeSpec createType(TypeForm form)
    {
        return new TypeSpecImpl(form);
    }

    /**
     * Create a type specification of Record form.
     * @param symbotTable the symbol table containing the field list.
     * @param fieldList A list of symbol table entries of the fields.
     * @return the type specification.
     */
    public static TypeSpec createRecordType(SymTab symbolTable, List<SymTabEntry> fieldList)
    {
        TypeSpec recordType = new TypeSpecImpl(TypeFormImpl.RECORD);
        recordType.setAttribute(TypeKeyImpl.RECORD_SYMTAB, symbolTable);
        recordType.setAttribute(TypeKeyImpl.RECORD_FIELD_LIST, fieldList);
        return recordType;
    }
    
    /**
     * Create a type specification of Array form.
     * @param symbotTable the symbol table containing the field list.
     * @param fieldList A list of symbol table entries of the fields.
     * @return the type specification.
     */
    public static TypeSpec createArrayType(int size, TypeSpec elementType)
    {
    	TypeSpec arrayType = TypeFactory.createType(TypeFormImpl.ARRAY);
		
		TypeSpec indexType = new TypeSpecImpl(SUBRANGE);
        indexType.setAttribute(SUBRANGE_BASE_TYPE, integerType);
        indexType.setAttribute(SUBRANGE_MIN_VALUE, 0);
        indexType.setAttribute(SUBRANGE_MAX_VALUE, size-1);

        arrayType.setAttribute(ARRAY_INDEX_TYPE, indexType);
        arrayType.setAttribute(ARRAY_ELEMENT_TYPE, elementType);
        arrayType.setAttribute(ARRAY_ELEMENT_COUNT, size);

        return arrayType;
    }
    
    /**
     * Create a string type specification.
     * @param value the string value.
     * @return the type specification.
     */
    public static TypeSpec createStringType(String value)
    {
        return new TypeSpecImpl(value);
    }
    
    
}
