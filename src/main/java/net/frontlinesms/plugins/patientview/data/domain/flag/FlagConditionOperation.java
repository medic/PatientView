package net.frontlinesms.plugins.patientview.data.domain.flag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.frontlinesms.plugins.patientview.data.domain.framework.DataType;

public enum FlagConditionOperation {

	EQUAL("=",new DataType[]{DataType.TEXT_FIELD,DataType.TEXT_AREA,DataType.NUMERIC_TEXT_FIELD},false),
	NOT_EQUAL("!=",new DataType[]{DataType.TEXT_FIELD,DataType.TEXT_AREA,DataType.NUMERIC_TEXT_FIELD},false),
	GREATER_THAN(">",new DataType[]{DataType.NUMERIC_TEXT_FIELD},false),
	GREATER_THAN_EQUAL_TO(">=",new DataType[]{DataType.NUMERIC_TEXT_FIELD},false),
	LESS_THAN("<",new DataType[]{DataType.NUMERIC_TEXT_FIELD},false),
	LESS_THAN_EQUAL_TO("<=",new DataType[]{DataType.NUMERIC_TEXT_FIELD},false),
	TRUE("is true",new DataType[]{DataType.CHECK_BOX,DataType.POSITIVENEGATIVE,DataType.TRUEFALSE, DataType.YESNO},true),
	NOT_TRUE("is false",new DataType[]{DataType.CHECK_BOX,DataType.POSITIVENEGATIVE,DataType.TRUEFALSE, DataType.YESNO},true),
	CONTAINS("contains",new DataType[]{DataType.TEXT_FIELD,DataType.TEXT_AREA},false);
	
	public final String label;
	private DataType[] validTypes;
	public final boolean isUnary;
	private static Set<DataType> operableTypes;
	
	static{
		operableTypes = new HashSet<DataType>();
		for(FlagConditionOperation op: FlagConditionOperation.values()){
			for(DataType dt: op.validTypes){
				operableTypes.add(dt);
			}
		}
	}

	FlagConditionOperation(String label, DataType[] validTypes, boolean isUnary){
		this.label = label;
		this.validTypes = validTypes;
		this.isUnary = isUnary;
	}
	
	public static List<FlagConditionOperation> getOperationsForDataType(DataType dt){
		List<FlagConditionOperation> results = new ArrayList<FlagConditionOperation>();
		for(FlagConditionOperation fco: FlagConditionOperation.values()){
			for(DataType type: fco.validTypes){
				if(dt == type){
					results.add(fco);
					continue;
				}
			}
		}
		return results;
	}
	
	public static boolean isOperable(DataType dt){
		return operableTypes.contains(dt);
	}
}