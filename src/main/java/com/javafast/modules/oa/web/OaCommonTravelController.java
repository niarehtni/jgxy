/**
 * Copyright 2015-2020
 */
package com.javafast.modules.oa.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;

import com.google.common.collect.Lists;
import com.javafast.common.utils.DateUtils;
import com.javafast.common.utils.MyBeanUtils;
import com.javafast.common.config.Global;
import com.javafast.common.persistence.Page;
import com.javafast.common.web.BaseController;
import com.javafast.common.utils.StringUtils;
import com.javafast.common.utils.excel.ExportExcel;
import com.javafast.common.utils.excel.ImportExcel;
import com.javafast.modules.oa.entity.OaCommonAudit;
import com.javafast.modules.oa.entity.OaCommonTravel;
import com.javafast.modules.oa.service.OaCommonAuditService;
import com.javafast.modules.oa.service.OaCommonTravelService;

/**
 * 差旅单Controller
 * @author javafast
 * @version 2017-08-25
 */
@Controller
@RequestMapping(value = "${adminPath}/oa/oaCommonTravel")
public class OaCommonTravelController extends BaseController {

	@Autowired
	private OaCommonTravelService oaCommonTravelService;
	
	@Autowired
	private OaCommonAuditService oaCommonAuditService;
	
	@ModelAttribute
	public OaCommonTravel get(@RequestParam(required=false) String id) {
		OaCommonTravel entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = oaCommonTravelService.get(id);
		}
		if (entity == null){
			entity = new OaCommonTravel();
		}
		return entity;
	}
	
	/**
	 * 差旅单列表页面
	 */
	@RequestMapping(value = {"list", ""})
	public String list(OaCommonTravel oaCommonTravel, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<OaCommonTravel> page = oaCommonTravelService.findPage(new Page<OaCommonTravel>(request, response), oaCommonTravel); 
		model.addAttribute("page", page);
		return "modules/oa/oaCommonTravelList";
	}

	/**
	 * 编辑差旅单表单页面
	 */
	@RequestMapping(value = "form")
	public String form(OaCommonTravel oaCommonTravel, Model model) {
		model.addAttribute("oaCommonTravel", oaCommonTravel);
		return "modules/oa/oaCommonTravelForm";
	}
	
	/**
	 * 查看差旅单页面
	 */
	@RequestMapping(value = "view")
	public String view(OaCommonTravel oaCommonTravel, Model model) {
		model.addAttribute("oaCommonTravel", oaCommonTravel);
		return "modules/oa/oaCommonTravelView";
	}

	/**
	 * 提交差旅单
	 */
	@RequestMapping(value = "save")
	public String save(OaCommonTravel oaCommonTravel, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
		if (!beanValidator(model, oaCommonTravel)){
			return form(oaCommonTravel, model);
		}
		
		try{
		
			if(!oaCommonTravel.getIsNewRecord()){//编辑表单提交				
				OaCommonTravel t = oaCommonTravelService.get(oaCommonTravel.getId());//从数据库取出记录的值
				MyBeanUtils.copyBeanNotNull2Bean(oaCommonTravel, t);//将编辑表单中的非NULL值覆盖数据库记录中的值
				oaCommonTravelService.save(t);//提交
			}else{//新增表单提交
				oaCommonTravelService.save(oaCommonTravel);//提交
				
				//获取审批主体
				OaCommonAudit oaCommonAudit = oaCommonAuditService.get(oaCommonTravel.getId());
				
				//通知下一审批人
				oaCommonAuditService.sendMsgToAuditUser(oaCommonAudit);
				
			}
			addMessage(redirectAttributes, "提交差旅单成功");
		}catch(Exception e){			
			e.printStackTrace();
			addMessage(redirectAttributes, "提交差旅单失败");
		}finally{
			return "redirect:"+Global.getAdminPath()+"/oa/oaCommonAudit/?repage";
		}
	}
	
	/**
	 * 删除差旅单
	 */
	@RequestMapping(value = "delete")
	public String delete(OaCommonTravel oaCommonTravel, RedirectAttributes redirectAttributes) {
		oaCommonTravelService.delete(oaCommonTravel);
		addMessage(redirectAttributes, "删除差旅单成功");
		return "redirect:"+Global.getAdminPath()+"/oa/oaCommonAudit/?repage";
	}
	
	/**
	 * 批量删除差旅单
	 */
	@RequestMapping(value = "deleteAll")
	public String deleteAll(String ids, RedirectAttributes redirectAttributes) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			oaCommonTravelService.delete(oaCommonTravelService.get(id));
		}
		addMessage(redirectAttributes, "删除差旅单成功");
		return "redirect:"+Global.getAdminPath()+"/oa/oaCommonTravel/?repage";
	}
	
	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("oa:oaCommonAudit:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public String exportFile(OaCommonTravel oaCommonTravel, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "差旅单"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<OaCommonTravel> page = oaCommonTravelService.findPage(new Page<OaCommonTravel>(request, response, -1), oaCommonTravel);
    		new ExportExcel("差旅单", OaCommonTravel.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导出差旅单记录失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/oaCommonTravel/?repage";
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oa:oaCommonAudit:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<OaCommonTravel> list = ei.getDataList(OaCommonTravel.class);
			for (OaCommonTravel oaCommonTravel : list){
				try{
					oaCommonTravelService.save(oaCommonTravel);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条差旅单记录。");
			}
			addMessage(redirectAttributes, "已成功导入 "+successNum+" 条差旅单记录"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入差旅单失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/oaCommonTravel/?repage";
    }
	
	/**
	 * 下载导入差旅单数据模板
	 */
	@RequiresPermissions("oa:oaCommonAudit:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "差旅单数据导入模板.xlsx";
    		List<OaCommonTravel> list = Lists.newArrayList(); 
    		new ExportExcel("差旅单数据", OaCommonTravel.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "导入模板下载失败！失败信息："+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/oa/oaCommonTravel/?repage";
    }
	
	/**
	 * 差旅单列表选择器
	 */
	@RequestMapping(value = "selectList")
	public String selectList(OaCommonTravel oaCommonTravel, HttpServletRequest request, HttpServletResponse response, Model model) {		
        list(oaCommonTravel, request, response, model);
        return "modules/oa/oaCommonTravelSelectList";
	}
	
}