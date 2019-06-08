package com.javafast.modules.oa.dao;

import org.hibernate.validator.constraints.Length;
import com.javafast.modules.sys.entity.User;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;
import com.javafast.common.persistence.CrudDao;
import com.javafast.common.persistence.annotation.MyBatisDao;
import com.javafast.modules.oa.entity.OaCommonAuditRecord;

/**
 * 审批流程DAO接口
 */
@MyBatisDao
public interface OaCommonAuditRecordDao extends CrudDao<OaCommonAuditRecord> {
	
	public List<OaCommonAuditRecord> findNoAuditList(OaCommonAuditRecord oaCommonAuditRecord);
}