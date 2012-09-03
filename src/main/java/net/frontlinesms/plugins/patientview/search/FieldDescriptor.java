package net.frontlinesms.plugins.patientview.search;

import net.frontlinesms.plugins.patientview.search.simplesearch.SimpleSearchDataType;
import net.frontlinesms.plugins.patientview.search.simplesearch.SimpleSearchEntity;
import net.frontlinesms.ui.i18n.InternationalisationUtils;

public class FieldDescriptor {

	private SimpleSearchEntity parentEntity;
	private String displayName;
	private String databaseName;
	private SimpleSearchDataType dataType;
	
	public FieldDescriptor(SimpleSearchEntity parentEntity, String displayName, String databaseName, SimpleSearchDataType dataType) {
		super();
		this.parentEntity = parentEntity;
		this.displayName = displayName;
		this.databaseName = databaseName;
		this.dataType = dataType;
	}
	
	public SimpleSearchEntity getParentEntity() {
		return parentEntity;
	}
	public void setParentEntity(SimpleSearchEntity parentEntity) {
		this.parentEntity = parentEntity;
	}
	public String getDisplayName() {
		return InternationalisationUtils.getI18nString(displayName);
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public SimpleSearchDataType getDataType() {
		return dataType;
	}
	public void setDataType(SimpleSearchDataType dataType) {
		this.dataType = dataType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result
				+ ((databaseName == null) ? 0 : databaseName.hashCode());
		result = prime * result
				+ ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result
				+ ((parentEntity == null) ? 0 : parentEntity.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldDescriptor other = (FieldDescriptor) obj;
		if (dataType != other.dataType)
			return false;
		if (databaseName == null) {
			if (other.databaseName != null)
				return false;
		} else if (!databaseName.equals(other.databaseName))
			return false;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (parentEntity != other.parentEntity)
			return false;
		return true;
	}	
}
