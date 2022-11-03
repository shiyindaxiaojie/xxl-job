package com.xxl.job.admin.controller;

import com.xxl.job.admin.core.model.XxlJobGroup;
import com.xxl.job.admin.core.model.XxlJobInfo;
import com.xxl.job.admin.core.route.ExecutorRouteStrategyEnum;
import com.xxl.job.admin.core.thread.JobTriggerPoolHelper;
import com.xxl.job.admin.core.trigger.TriggerTypeEnum;
import com.xxl.job.admin.dao.XxlJobGroupDao;
import com.xxl.job.admin.service.XxlJobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * index controller
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/jobinfo")
public class JobInfoController {

	private static final Logger log = LoggerFactory.getLogger(JobInfoController.class);

	@Resource
	private XxlJobGroupDao xxlJobGroupDao;
	@Resource
	private XxlJobService xxlJobService;
	
	@RequestMapping
	public String index(Model model, @RequestParam(required = false, defaultValue = "-1") int jobGroup) {

		// 枚举-字典
		model.addAttribute("ExecutorRouteStrategyEnum", ExecutorRouteStrategyEnum.values());	// 路由策略-列表
		model.addAttribute("GlueTypeEnum", GlueTypeEnum.values());								// Glue类型-字典
		model.addAttribute("ExecutorBlockStrategyEnum", ExecutorBlockStrategyEnum.values());	// 阻塞处理策略-字典

		// 任务组
		List<XxlJobGroup> jobGroupList =  xxlJobGroupDao.findAll();
		model.addAttribute("JobGroupList", jobGroupList);
		model.addAttribute("jobGroup", jobGroup);

		return "jobinfo/jobinfo.index";
	}
	
	@RequestMapping("/pageList")
	@ResponseBody
	public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,  
			@RequestParam(required = false, defaultValue = "10") int length,
			int jobGroup, String jobDesc, String executorHandler, String filterTime) {
		
		return xxlJobService.pageList(start, length, jobGroup, jobDesc, executorHandler, filterTime);
	}
	
	@RequestMapping("/add")
	@ResponseBody
	public ReturnT<String> add(XxlJobInfo jobInfo) {
		return xxlJobService.add(jobInfo);
	}
	
	@RequestMapping("/update")
	@ResponseBody
	public ReturnT<String> update(XxlJobInfo jobInfo) {
		return xxlJobService.update(jobInfo);
	}
	
	@RequestMapping("/remove")
	@ResponseBody
	public ReturnT<String> remove(int id) {
		return xxlJobService.remove(id);
	}
	
	@RequestMapping("/pause")
	@ResponseBody
	public ReturnT<String> pause(int id) {
		return xxlJobService.pause(id);
	}
	
	@RequestMapping("/resume")
	@ResponseBody
	public ReturnT<String> resume(int id) {
		return xxlJobService.resume(id);
	}
	
	@RequestMapping("/trigger")
	@ResponseBody
	//@PermessionLimit(limit = false)
	public ReturnT<String> triggerJob(int id, String executorParam) {
		// force cover job param
		if (executorParam == null) {
			executorParam = "";
		}

		JobTriggerPoolHelper.trigger(id, TriggerTypeEnum.MANUAL, -1, null, executorParam);
		return ReturnT.SUCCESS;
	}

	@RequestMapping("/addOrUpdate")
	@ResponseBody
	public ReturnT<String> addOrUpdate(XxlJobInfo jobInfo) {
		ReturnT<XxlJobInfo> returnT = xxlJobService.existsByGroupAndHandler(jobInfo.getJobGroup(),
				jobInfo.getExecutorHandler());

		int jobId = 0;
		XxlJobInfo xxlJobInfoFromDB = returnT.getContent();
		if (xxlJobInfoFromDB != null) {
			jobId = xxlJobInfoFromDB.getId();
			log.info("Found executorHandler '{}' from groupId '{}', return jobId '{}'",
					jobInfo.getExecutorHandler(), jobInfo.getJobGroup(), jobId);
		} else {
			ReturnT<String> jobInfoReturnT = xxlJobService.add(jobInfo);
			if (jobInfoReturnT.getCode() == ReturnT.SUCCESS_CODE) {
				log.info("Save executorHandler '{}' for groupId '{}', return jobId '{}'",
						jobInfo.getExecutorHandler(), jobInfo.getJobGroup(), jobInfo.getId());
			} else {
				log.info("Save executorHandler '{}' for groupId '{}', failed",
						jobInfo.getExecutorHandler(), jobInfo.getJobGroup());
			}
		}
		return jobId != 0? new ReturnT<>(String.valueOf(jobId)) : ReturnT.FAIL;
	}
}
