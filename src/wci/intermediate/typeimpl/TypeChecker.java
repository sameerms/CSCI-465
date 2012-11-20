package wci.intermediate.typeimpl;

import java.util.ArrayList;

import wci.intermediate.ICodeNode;
import wci.intermediate.SymTabEntry;
import wci.intermediate.TypeSpec;
import wci.intermediate.SymTabStack;
import wci.frontend.triangle.TriangleParserTD;
import wci.intermediate.icodeimpl.ICodeNodeTypeImpl;
import wci.intermediate.symtabimpl.DefinitionImpl;
import wci.intermediate.symtabimpl.SymTabKeyImpl;
import static wci.intermediate.symtabimpl.TrianglePredefined.booleanType;
import static wci.intermediate.icodeimpl.ICodeKeyImpl.ID;

public class TypeChecker {

	@SuppressWarnings("unchecked")
	public static boolean isParameterCompatible(ICodeNode actualParam,
			SymTabEntry formalParameter, TriangleParserTD parser) {

		if (actualParam == null || formalParameter == null) {
			return false;
		}

		if (formalParameter.getDefinition() == DefinitionImpl.VAR_PARM
				&& actualParam.getType() == ICodeNodeTypeImpl.VAR_PARM) {

			if (actualParam.getTypeSpec().equals(formalParameter.getTypeSpec())) {
				return true;
			}
		} else if (formalParameter.getDefinition() == DefinitionImpl.VALUE_PARM
				&& actualParam.getType() == ICodeNodeTypeImpl.VALUE_PARM) {

			if (actualParam.getTypeSpec().equals(formalParameter.getTypeSpec())) {
				return true;
			}
		} else if (formalParameter.getDefinition() == DefinitionImpl.PROC_PARM
					|| formalParameter.getDefinition() == DefinitionImpl.FUNC_PARM) {
			if (actualParam.getChildren() != null && actualParam.getChildren().size() > 0){
				actualParam = actualParam.getChildren().get(0);
				SymTabStack symTabStack = parser.getSymTabStack();	 
				SymTabEntry actualParamId = 
						symTabStack.lookup((String) actualParam.getAttribute(ID));
				boolean areCompatible = areParametersCompatible(actualParamId,formalParameter);
				return areCompatible && actualParamId.getTypeSpec().equals(formalParameter.getTypeSpec());
			}
		} 
		return false;
	}

	@SuppressWarnings("unchecked")
	public static boolean areParametersCompatible(SymTabEntry actualId,
			SymTabEntry formalId) {
		ArrayList<SymTabEntry> paramList1 = 
				(ArrayList<SymTabEntry>)actualId.getAttribute(SymTabKeyImpl.ROUTINE_PARMS);
		ArrayList<SymTabEntry> paramList2 = 
				(ArrayList<SymTabEntry>)formalId.getAttribute(SymTabKeyImpl.ROUTINE_PARMS);
		int size = paramList1.size() < paramList2.size() ? paramList1.size(): paramList2.size();
		for(int index = 0; index < size; index++){
		
		}
		return false;
	}

	public static boolean isExpressionBoolean(ICodeNode exprNode) {
		return exprNode.getTypeSpec().equals(booleanType);
	}

	public static boolean isAssignmentCompatible(ICodeNode lhs, ICodeNode rhs) {
		return lhs.getTypeSpec().equals(rhs.getTypeSpec());
	}

}
