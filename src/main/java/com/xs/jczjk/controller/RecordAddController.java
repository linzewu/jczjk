package com.xs.jczjk.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xs.jczjk.manager.UploadZJOfHuNan;
import com.xs.jczjk.util.FtpUtils;
import com.xs.jczjk.util.QrCodeUtil;

@Controller
public class RecordAddController {
	
	@Value("${jcz.url}")
	private String url;
	
	@Value("${jcz.username}")
	private String username;
	@Value("${jcz.password}")
	private String password;
	
	@Value("${veh.bps.visiturl}")
	private String vehUrl;
	
	
	@Value("${image.save.url}")
	private String imageUrl;
	
	@Value("${ftp.photo.hostname}")
	private String hostname;
	@Value("${ftp.photo.username}")
	private String ftpUsername;
	@Value("${ftp.photo.password}")
	private String ftpPassword;
	
	@Value("${jcz.jcdw}")
	private String jcdw;
	@Value("${jcz.jcdwdz}")
	private String jcdwdz;
	@Value("${jcz.yzbm}")
	private String yzbm;
	@Value("${jcz.lxdh}")
	private String lxdh;
	@Value("${jcz.jczjyxkzh}")
	private String jczjyxkzh;
	
	@Value("${jcz.local:js}")
	private String local;
	
	@Autowired
	private UploadZJOfHuNan uploadZJOfHuNan;
	
	@Autowired
    private RestTemplate restTemplate;
	
	Logger logger = Logger.getLogger(RecordAddController.class);
	
	
	
	@RequestMapping("/recordadd")  
    @ResponseBody  
    public String recordadd(@RequestBody Map params) {
		
		if(local.equals("js")) {
			 return recordaddJS(params);  
		}
		
		if(local.equals("hn")) {
			return recordaddHn(params);
		}
		
		
		return null;
       
    }
	
	/**
	 * 湖南接口上传
	 * @param params
	 * @return
	 */
	public String recordaddHn(Map params) {
		String str = restTemplate.getForEntity(vehUrl, String.class).getBody();
		String[] strArr = str.split(";");
		JSONArray jsonArr = new JSONArray();
		if(strArr.length>0) {
			String bpsStr = strArr[0].substring(strArr[0].indexOf("=")+1);
			jsonArr = JSONArray.parseArray(bpsStr);
		}
		//字典转化前缀
		JSONArray zhqz = getParamByType(jsonArr,"zhqz");
		String qz = "";
		if(zhqz.size() > 0) {
			qz = zhqz.getJSONObject(0).getString("paramValue");
		}
		//////
		//获取token
		String tokenResult = this.uploadZJOfHuNan.getAccessToken();
		JSONObject jsonObject = JSONObject.parseObject(tokenResult);
		String token = "";
		if("1".equals(jsonObject.get("code"))) {
			token = jsonObject.getString("access_token");
		}else {
			return tokenResult;
		}
		
		//获取机动车信息及检测流水号信息接口
		JSONObject vehInfoJson = null;
		String lshResult = this.uploadZJOfHuNan.getVehicleInfoAndDetectSn(params,token,jsonArr, qz);
		JSONObject lshJson = JSONObject.parseObject(lshResult);
		
		if("1".equals(lshJson.get("code"))) {
			vehInfoJson = lshJson.getJSONObject("data");
			//综检流水号
			String detectSn = vehInfoJson.getString("detectSn");
			params.put("zjlsh", detectSn);
			
			//上传接口数据
			return uploadZJOfHuNan.shareDetectInfo(params, token, detectSn, jsonArr, qz);
		}else {
			return tokenResult;
		}
		
		
	}
	
	@RequestMapping("/getToken")  
	public @ResponseBody String getToken() {
		return this.uploadZJOfHuNan.getAccessToken();
	}
	
	/**
	 * 获取机动车信息及检测流水号信息接口
	 * @param param
	 * @return
	 */
	@RequestMapping("/getVehicleInfoAndDetectSn")  
	public @ResponseBody String getVehicleInfoAndDetectSn(@RequestBody Map<String,String> param) {
		//获取token
		String tokenResult = this.uploadZJOfHuNan.getAccessToken();
		JSONObject jsonObject = JSONObject.parseObject(tokenResult);
		String token = "";
		if("1".equals(jsonObject.get("code"))) {
			token = jsonObject.getString("access_token");
		}else {
			return tokenResult;
		}
		
		String str = restTemplate.getForEntity(vehUrl, String.class).getBody();
		String[] strArr = str.split(";");
		JSONArray jsonArr = new JSONArray();
		if(strArr.length>0) {
			String bpsStr = strArr[0].substring(strArr[0].indexOf("=")+1);
			jsonArr = JSONArray.parseArray(bpsStr);
		}
		//字典转化前缀
		JSONArray zhqz = getParamByType(jsonArr,"zhqz");
		String qz = "";
		if(zhqz.size() > 0) {
			qz = zhqz.getJSONObject(0).getString("paramValue");
		}
		
		//获取机动车信息及检测流水号信息接口
		JSONObject vehInfoJson = null;
		String lshResult = this.uploadZJOfHuNan.getVehicleInfoAndDetectSn(param,token,jsonArr, qz);
		JSONObject lshJson = JSONObject.parseObject(lshResult);
		
		if("1".equals(lshJson.get("code"))) {
			vehInfoJson = lshJson.getJSONObject("data");
			//综检流水号
			String detectSn = vehInfoJson.getString("detectSn");
			return detectSn;
		}else {
			return tokenResult;
		}
		
		
	}
	
	/**
	 * 检验检测机构基本信息交换与共享接口
	 * @param param
	 * @return
	 */
	@RequestMapping("/shareDetectionStationInfo")  
	public @ResponseBody String shareDetectionStationInfo() {
		//获取token
		String tokenResult = this.uploadZJOfHuNan.getAccessToken();
		JSONObject jsonObject = JSONObject.parseObject(tokenResult);
		String token = "";
		if("1".equals(jsonObject.get("code"))) {
			token = jsonObject.getString("access_token");
		}else {
			return tokenResult;
		}
		
		String lshResult = this.uploadZJOfHuNan.shareDetectionStationInfo(null,token);
		
		return lshResult;
	}
	
	
	public String recordaddJS(Map params) {
		String str = restTemplate.getForEntity(vehUrl, String.class).getBody();
		String[] strArr = str.split(";");
		JSONArray jsonArr = new JSONArray();
		if(strArr.length>0) {
			String bpsStr = strArr[0].substring(strArr[0].indexOf("=")+1);
			jsonArr = JSONArray.parseArray(bpsStr);
		}
		//字典转化前缀
		JSONArray zhqz = getParamByType(jsonArr,"zhqz");
		String qz = "";
		if(zhqz.size() > 0) {
			qz = zhqz.getJSONObject(0).getString("paramValue");
		}
		Map userParam = new HashMap();
		userParam.put("username", username);
		userParam.put("password", password);
		System.out.println(url+"  "+userParam);
		String result = restTemplate.postForEntity(url+"lcipgetaccesstoken", userParam, String.class).getBody();
		System.out.println("result:"+result);
		JSONObject resultObj = JSONObject.parseObject(result);
		if("1".equals(resultObj.getString("code"))) {
			String access_token = resultObj.getString("access_token");
			JSONObject basicinfo = new JSONObject();
			setAddParam(params, basicinfo,jsonArr,qz);
			Map recordParam = new HashMap();
			recordParam.put("access_token", access_token);
			recordParam.put("basicinfo",basicinfo);
			System.out.println("recordParam:"+recordParam); 
			result = restTemplate.postForEntity(url+"recordadd", recordParam, String.class).getBody();
			JSONObject resultObj1 = JSONObject.parseObject(result);
			//生成二维码
			if("1".equals(resultObj1.getString("code"))) {
				QrCodeUtil.createQrCode(resultObj1.getString("twocodeurl"), imageUrl+basicinfo.getString("jldbh")+".jpg");
			}
			System.out.println(result+" "+params.toString());
		}
		return result;
	}

	private JSONArray getParamByType(JSONArray jsonArr,String type) {
		JSONArray retArr = new JSONArray();
		for(int i=0;i<jsonArr.size();i++) {
			JSONObject jobject = jsonArr.getJSONObject(i);
			if(type.equals(jobject.get("type"))) {
				retArr.add(jobject);
			}
		}
		return retArr;
	}
	
	private String getParamByTypeAndName(JSONArray jsonArr,String type,String paramName) {
		JSONArray retArr = new JSONArray();
		for(int i=0;i<jsonArr.size();i++) {
			JSONObject jobject = jsonArr.getJSONObject(i);
			if(type.equals(jobject.get("type")) && paramName.equals(jobject.get("paramName"))) {
				return jobject.getString("paramValue");
			}
		}
		return "";
	}
	
	private String getParamByTypeAndValue(JSONArray jsonArr,String type,String paramValue) {
		JSONArray retArr = new JSONArray();
		for(int i=0;i<jsonArr.size();i++) {
			JSONObject jobject = jsonArr.getJSONObject(i);
			if(type.equals(jobject.get("type")) && paramValue.equals(jobject.get("paramValue"))) {
				return jobject.getString("paramName");
			}
		}
		return "";
	}

	private void setAddParam(Map params, JSONObject basicinfo,JSONArray bpsArr,String zhqz) {
		basicinfo.put("jylb", getParamByTypeAndName(bpsArr,zhqz+"jcxz",String.valueOf(params.get("jcxz"))));
		//车辆单位
		basicinfo.put("cldw", params.get("syr"));
		basicinfo.put("dlyszh", params.get("dlyxzh"));
		basicinfo.put("ywlb", getParamByTypeAndName(bpsArr,zhqz+"zjywlx",String.valueOf(params.get("zjywlx"))));
		basicinfo.put("jcxb", params.get("jcxdh"));
		
		basicinfo.put("jyrq", StringUtils.isEmpty(params.get("uplinedate")) ? "1900-01-01 00:00:00":params.get("uplinedate"));
		//记录单编号
		basicinfo.put("jldbh", params.get("bgdbh"));
		System.out.println("号牌种类=============："+params.get("hpzl"));
		if("16".equals(params.get("hpzl"))||"17".equals(params.get("hpzl"))) {
			basicinfo.put("hphm", params.get("hphm")+"学");
		}else {
			basicinfo.put("hphm", params.get("hphm"));
		}
		
		
		
		String hpys = getParamByTypeAndName(bpsArr,zhqz+"cpys",String.valueOf(params.get("cpys")));
		System.out.println(String.valueOf(params.get("cpys")));
		basicinfo.put("hpys", hpys.equals("")?"2":hpys);
		basicinfo.put("hpxh", params.get("clxh"));
		basicinfo.put("gcqychp", params.get("qychphm"));
		basicinfo.put("clccrq", StringUtils.isEmpty(params.get("ccrq"))?"1900-01-01 00:00:00":formatDate(params.get("ccrq").toString()));
		basicinfo.put("ccdjrq", StringUtils.isEmpty(params.get("ccdjrq"))? "1900-01-01 00:00:00":formatDate(params.get("ccdjrq").toString()));
		basicinfo.put("hpzl", params.get("hpzl"));
		basicinfo.put("clyt", params.get("zjclyt"));
		basicinfo.put("fdjxh", params.get("fdjxh"));
		basicinfo.put("fdjh", params.get("fdjh"));
		basicinfo.put("vinh", params.get("clsbdh"));
		basicinfo.put("rllb", getParamByTypeAndName(bpsArr,zhqz+"rlzl",String.valueOf(params.get("rlzl"))));
		basicinfo.put("pfhzz", StringUtils.isEmpty(params.get("pfhzz"))?"0":params.get("pfhzz"));
		basicinfo.put("qlj", StringUtils.isEmpty(params.get("qlj"))?"0":params.get("qlj"));
		basicinfo.put("zxzxs", getParamByTypeAndName(bpsArr,zhqz+"zxzxjxs",String.valueOf(params.get("zxzxjxs"))));
		basicinfo.put("bzzxs", params.get("bzzxs"));
		basicinfo.put("fdjedgl", StringUtils.isEmpty(params.get("gl"))?"0":params.get("gl"));
		basicinfo.put("zws", StringUtils.isEmpty(params.get("kczws"))?"0":params.get("kczws"));
		basicinfo.put("lxdj", params.get("kcdj"));
		basicinfo.put("qdxs", params.get("qdxs"));//取baseparamName
		//驱动轴位置
		basicinfo.put("qdzwz", getParamByTypeAndValue(bpsArr,"qdxs",String.valueOf(params.get("qdxs"))));
		basicinfo.put("edzj", StringUtils.isEmpty(params.get("ednj"))?"0":params.get("ednj"));
		basicinfo.put("edzs", StringUtils.isEmpty(params.get("edzs"))?"0":params.get("edzs"));
		basicinfo.put("hccsxs", params.get("hccsxs"));
		basicinfo.put("zkzzl", StringUtils.isEmpty(params.get("qdzkzzl"))?"0":params.get("qdzkzzl"));
		basicinfo.put("ltggxh", (StringUtils.isEmpty(params.get("ltlx"))? "0":params.get("ltlx"))+"/"+(StringUtils.isEmpty(params.get("ltxh"))?"0":params.get("ltxh") ));
		basicinfo.put("mzzzl", StringUtils.isEmpty(params.get("qycmzzl"))?"0":params.get("qycmzzl"));
		basicinfo.put("zzl", StringUtils.isEmpty(params.get("zzl"))?"0":params.get("zzl"));
		basicinfo.put("zbzl", StringUtils.isEmpty(params.get("zbzl"))?"0":params.get("zbzl"));
		basicinfo.put("qzdzs", getQzdz(params.get("qzdz")));
		basicinfo.put("istz", "2");
		basicinfo.put("dczs", StringUtils.isEmpty(params.get("zs"))?"0":params.get("zs"));
		basicinfo.put("qczwz", params.get("zczw"));
		basicinfo.put("wlcc", params.get("cwkc")+"×"+params.get("cwkk")+"×"+params.get("cwkg"));
		basicinfo.put("cslbgd", StringUtils.isEmpty(params.get("cxlbgd"))?"0":params.get("cxlbgd"));
		System.out.println("basicinfo:"+basicinfo);
		if(!StringUtils.isEmpty(params.get("dlx"))) {
			Map dlx = (Map)params.get("dlx");
			basicinfo.put("dbgl", StringUtils.isEmpty(dlx.get("dlx_dbgl"))?"0":dlx.get("dlx_dbgl"));
			basicinfo.put("edcs", StringUtils.isEmpty(dlx.get("dlx_edcs"))?"0":dlx.get("dlx_edcs"));
			basicinfo.put("jzl", StringUtils.isEmpty(dlx.get("dlx_jzl"))?"0":dlx.get("dlx_jzl"));
			basicinfo.put("wdcs", StringUtils.isEmpty(dlx.get("dlx_wdcs"))?"0":dlx.get("dlx_wdcs"));
			basicinfo.put("glyhscz", StringUtils.isEmpty(dlx.get("yh_scz"))?"0":dlx.get("yh_scz"));
			//动力性
			//basicinfo.put("dlx", dlx.get("dlx_pd"));
			//basicinfo.put("rljjx", dlx.get("yh_pd"));
		}
		//动力性
		basicinfo.put("dlx", params.get("dlx_pd"));
		basicinfo.put("rljjx", params.get("yh_pd"));
		
		basicinfo.put("glyhxz", params.get("yhxz"));
		
		if(!StringUtils.isEmpty(params.get("b1"))) {
			Map b1 = (Map)params.get("b1");
			basicinfo.put("yyspcz", b1.get("ylh"));
			basicinfo.put("yzspcz", b1.get("zlh"));
			basicinfo.put("yzfhtzh", b1.get("zjzh"));
			basicinfo.put("yzxczdl", b1.get("zzdl"));
			basicinfo.put("yyxczdl", b1.get("yzdl"));
			basicinfo.put("yzzdl", b1.get("kzxczdl"));
			basicinfo.put("yzdbphl", b1.get("kzbphl"));
			basicinfo.put("yzgcczd", b1.get("zzdlcd"));
			basicinfo.put("yygcczd", b1.get("yzdlcd"));
			basicinfo.put("yzclzzl", b1.get("zzzlf"));
			basicinfo.put("yyclzzl", b1.get("yzzlf"));
			//一轴制动性台架检验综合“判定” 1通过2 不通过
			basicinfo.put("ytjjyzh", b1.get("zpd"));
		}
		if(!StringUtils.isEmpty(params.get("b2"))) {
			Map b2 = (Map)params.get("b2");
			basicinfo.put("ezspcz", b2.get("zlh"));
			basicinfo.put("eyspcz", b2.get("ylh"));
			basicinfo.put("ezfhtzh", b2.get("zjzh"));
			basicinfo.put("ezxczdl", b2.get("zzdl"));
			basicinfo.put("eyxczdl", b2.get("yzdl"));
			basicinfo.put("ezzdl", b2.get("kzxczdl"));
			basicinfo.put("ezdbphl", b2.get("kzbphl"));
			basicinfo.put("ezgcczd", b2.get("zzdlcd"));
			basicinfo.put("eygcczd", b2.get("yzdlcd"));
			basicinfo.put("ezclzzl", b2.get("zzzlf"));
			basicinfo.put("eyclzzl", b2.get("yzzlf"));
			//二轴制动性台架检验综合“判定” 1通过2 不通过
			basicinfo.put("etjjyzh", b2.get("zpd"));
		}
		if(!StringUtils.isEmpty(params.get("b3"))) {
			Map b3 = (Map)params.get("b3");
			basicinfo.put("szspcz", b3.get("zlh"));
			basicinfo.put("syspcz", b3.get("ylh"));
			basicinfo.put("szfhtzh", b3.get("zjzh"));
			basicinfo.put("szxczdl", b3.get("zzdl"));
			basicinfo.put("syxczdl", b3.get("yzdl"));
			basicinfo.put("szzdl", b3.get("kzxczdl"));
			basicinfo.put("szdbphl", b3.get("kzbphl"));
			basicinfo.put("szgcczd", b3.get("zzdlcd"));
			basicinfo.put("sygcczd", b3.get("yzdlcd"));
			basicinfo.put("szclzzl", b3.get("zzzlf"));
			basicinfo.put("syclzzl", b3.get("yzzlf"));
			//三轴制动性台架检验综合“判定” 1通过2 不通过
			basicinfo.put("stjjyzh", b3.get("zpd"));
		}
		if(!StringUtils.isEmpty(params.get("b4"))) {
			Map b4 = (Map)params.get("b4");
			basicinfo.put("sizspcz", b4.get("zlh"));
			basicinfo.put("siyspcz", b4.get("ylh"));
			basicinfo.put("sizfhtzh", b4.get("zjzh"));
			basicinfo.put("sizxczdl", b4.get("zzdl"));
			basicinfo.put("siyxczdl", b4.get("yzdl"));
			basicinfo.put("sizzdl", b4.get("kzxczdl"));
			basicinfo.put("sizdbphl", b4.get("kzbphl"));
			basicinfo.put("sizgcczd", b4.get("zzdlcd"));
			basicinfo.put("siygcczd", b4.get("yzdlcd"));
			basicinfo.put("sizclzzl", b4.get("zzzlf"));
			basicinfo.put("siyclzzl", b4.get("yzzlf"));
			//四轴制动性台架检验综合“判定” 1通过2 不通过
			basicinfo.put("sitjjyzh", b4.get("zpd"));
		}
		
		////驻车制动力
		if(!StringUtils.isEmpty(params.get("b0_1"))) {
			Map b0_1 = (Map)params.get("b0_1");
			basicinfo.put("yzzczdl", b0_1.get("zzdl"));
			basicinfo.put("yyzczdl", b0_1.get("yzdl"));
		}
		if(!StringUtils.isEmpty(params.get("b0_2"))) {
			Map b0_2 = (Map)params.get("b0_2");
			basicinfo.put("ezzczdl", b0_2.get("zzdl"));
			basicinfo.put("eyzczdl", b0_2.get("yzdl"));
		}
		if(!StringUtils.isEmpty(params.get("b0_3"))) {
			Map b0_3 = (Map)params.get("b0_3");
			basicinfo.put("szzczdl", b0_3.get("zzdl"));
			basicinfo.put("syzczdl", b0_3.get("yzdl"));
			
		}
		if(!StringUtils.isEmpty(params.get("b0_4"))) {
			Map b0_4 = (Map)params.get("b0_4");
			basicinfo.put("sizzczdl", b0_4.get("zzdl"));
			basicinfo.put("siyzczdl", b0_4.get("yzdl"));
		}
		
		
//		//一轴左轮“动态轮荷(kg)
//		basicinfo.put("yzdtlh", "1");
//		//一轴右轮“动态轮荷(kg)
//		basicinfo.put("yydtlh", "1");
//		//二轴左轮“动态轮荷(kg)”
//		basicinfo.put("ezdtlh", "1");
//		//二轴右轮“动态轮荷(kg)”
//		basicinfo.put("eydtlh", "1");
		
		//整车
		if(!StringUtils.isEmpty(params.get("other"))) {
			Map other = (Map)params.get("other");
			basicinfo.put("spcz", other.get("jczczbzl"));
			basicinfo.put("zczdl", other.get("zczdl"));
			//制动性台架检验整车项目综合“判定” 1通过2 不通过
			basicinfo.put("jcxmzh", other.get("zcpd"));	
			
		}
		
		if(!StringUtils.isEmpty(params.get("par"))) {
			Map par = (Map)params.get("par");
			basicinfo.put("zzczdl", par.get("tczdl"));
		}
		
		
		//路试
		if(!StringUtils.isEmpty(params.get("lsy"))) {
			Map lsy = (Map)params.get("lsy");
			basicinfo.put("csd", lsy.get("zdcsd"));
//			//试车道宽度
//			basicinfo.put("scdkd", "1");
			basicinfo.put("zdjl", lsy.get("xckzzdjl"));
			basicinfo.put("mfdd", lsy.get("xckzmfdd"));
			basicinfo.put("zdwdx", lsy.get("zdwdx"));
			basicinfo.put("zdxtsj", lsy.get("zdxtsj"));
			basicinfo.put("xmzhpd", lsy.get("lsjg"));
			
			basicinfo.put("zcpd", lsy.get("zcpd"));
			basicinfo.put("pdzcqk", lsy.get("lszczdpd"));
//			//驻车制动路试“理论牵引力(N)”
//			basicinfo.put("llqyl", "1");
//			//驻车制动路试“实际牵引力(N)”
//			basicinfo.put("sjqyl", "1");
			basicinfo.put("lsxmzhpd", lsy.get("lszczdpd"));
		}
		
		
		if(!StringUtils.isEmpty(params.get("sds"))) {
			Map sds = (Map)params.get("sds");
			basicinfo.put("gco", sds.get("cogclz"));
			basicinfo.put("ghc", sds.get("hcgclz"));
			basicinfo.put("gzs", sds.get("kqxs"));
			basicinfo.put("dco", sds.get("codclz"));
			basicinfo.put("dhc", sds.get("hcdclz"));
		}
		if(!StringUtils.isEmpty(params.get("wt"))) {
			Map wt = (Map)params.get("wt");
			basicinfo.put("i5025co", wt.get("clzco25"));
			basicinfo.put("i5025hc", wt.get("clzhc25"));
			basicinfo.put("i5025no", wt.get("clzno25"));
			basicinfo.put("i2540co", wt.get("clzco40"));
			basicinfo.put("i2540hc", wt.get("clzhc40"));
			basicinfo.put("i2540no", wt.get("clzno40"));
		}
		
		
		
		
		basicinfo.put("pfxzgpd", params.get("pfx1pd"));
		
		if(!StringUtils.isEmpty(params.get("yd"))) {
			Map yd = (Map)params.get("yd");
			basicinfo.put("ygxsxs", yd.get("d2clz"));
			basicinfo.put("egxsxs", yd.get("d3clz"));
			basicinfo.put("sgxsxs", yd.get("d4clz"));
			//压燃式汽车自由加速法光吸收系数检测结果
			basicinfo.put("gxsxsjcjg", yd.get("ydpjz"));			
		}
		basicinfo.put("yrszgpd", params.get("pfx2pd"));
		
		//悬架
		
		if(!StringUtils.isEmpty(params.get("xj1"))) {
			Map xj1 = (Map)params.get("xj1");
			basicinfo.put("qzxjxsl", xj1.get("zxsl"));
			basicinfo.put("qyxjxsl", xj1.get("yxsl"));
			basicinfo.put("qzzyc", xj1.get("zyc"));
			basicinfo.put("qzzhpd", xj1.get("zpd"));
		}
		if(!StringUtils.isEmpty(params.get("xj2"))) {
			Map xj2 = (Map)params.get("xj2");
			basicinfo.put("hzxjxsl", xj2.get("zxsl"));
			basicinfo.put("hyxjxsl", xj2.get("yxsl"));
			basicinfo.put("hzzyc", xj2.get("zyc"));
			basicinfo.put("hzzhpd", xj2.get("zpd"));
		}
		
		if(!StringUtils.isEmpty(params.get("s1"))) {
			Map s1 = (Map)params.get("s1");
			basicinfo.put("csbsjcs", s1.get("speed"));
			//车速表项目“判定”1 通过 2 不通过  
			basicinfo.put("csbxmpd", s1.get("sdpd"));
		}
		//侧滑量
		if(!StringUtils.isEmpty(params.get("a1"))) {
			Map a1 = (Map)params.get("a1");
			basicinfo.put("ychl", a1.get("sideslip"));
			basicinfo.put("chlzhpd", a1.get("chpd"));
		}
		if(!StringUtils.isEmpty(params.get("a2"))) {
			Map a2 = (Map)params.get("a2");
			basicinfo.put("echl", a2.get("sideslip"));
		}
		//喇叭
		if(!StringUtils.isEmpty(params.get("sjj"))) {
			Map sjj = (Map)params.get("sjj");
			basicinfo.put("lbsyj", sjj.get("fb"));
			basicinfo.put("lbsyjpd", sjj.get("zpd"));
		}
		
		//灯光
		if(!StringUtils.isEmpty(params.get("h1_j"))) {
			Map h1_j = (Map)params.get("h1_j");
			basicinfo.put("zwygdg", h1_j.get("dg"));
			basicinfo.put("zwjgczpy", h1_j.get("czpy"));
			basicinfo.put("zwjgsppy", h1_j.get("sppc"));
		}
		
		if(!StringUtils.isEmpty(params.get("h1_y"))) {
			Map h1_y = (Map)params.get("h1_y");
			basicinfo.put("zwjgdg", h1_y.get("dg"));
			basicinfo.put("zwyggq", h1_y.get("gq"));
			basicinfo.put("zwygczpy", h1_y.get("czpy"));
			basicinfo.put("zwygsppy", h1_y.get("sppc"));
		}
		
		if(!StringUtils.isEmpty(params.get("h2_j"))) {
			Map h2_j = (Map)params.get("h2_j");
			basicinfo.put("znygdg", h2_j.get("dg"));
			basicinfo.put("znjgczpy", h2_j.get("czpy"));
			basicinfo.put("znjgsppy", h2_j.get("sppc"));
		}
		if(!StringUtils.isEmpty(params.get("h2_y"))) {
			Map h2_y = (Map)params.get("h2_y");
			basicinfo.put("znjgdg", h2_y.get("dg"));
			basicinfo.put("znyggc", h2_y.get("gq"));
			basicinfo.put("znygczpy", h2_y.get("czpy"));
			basicinfo.put("znygsppy", h2_y.get("sppc"));
		}
		
		if(!StringUtils.isEmpty(params.get("h3_j"))) {
			Map h3_j = (Map)params.get("h3_j");
			basicinfo.put("ynygdg", h3_j.get("dg"));
			basicinfo.put("ynjgczpy", h3_j.get("czpy"));
			basicinfo.put("ynjgsppy", h3_j.get("sppc"));
		}
		if(!StringUtils.isEmpty(params.get("h3_y"))) {
			Map h3_y = (Map)params.get("h3_y");
			basicinfo.put("ynjgdg", h3_y.get("dg"));
			basicinfo.put("ynyggq", h3_y.get("gq"));
			basicinfo.put("ynygczpy", h3_y.get("czpy"));
			basicinfo.put("ynygsppy", h3_y.get("sppc"));
		}
		if(!StringUtils.isEmpty(params.get("h4_j"))) {
			Map h4_j = (Map)params.get("h4_j");
			basicinfo.put("ywygdg", h4_j.get("dg"));
			basicinfo.put("ywjgczpy", h4_j.get("czpy"));
			basicinfo.put("ywjgsppy", h4_j.get("sppc"));
		}
		if(!StringUtils.isEmpty(params.get("h4_y"))) {
			Map h4_y = (Map)params.get("h4_y");
			basicinfo.put("ywjgdg", h4_y.get("dg"));
			basicinfo.put("ywyggq", h4_y.get("gq"));
			basicinfo.put("ywygczpy", h4_y.get("czpy"));
			basicinfo.put("ywygsppy", h4_y.get("sppc"));
		}		
		
		//“前照灯”项目综合“判定”1 通过 2 不通过
//		basicinfo.put("qzdzhpd", "1");
		//复检情况
//		basicinfo.put("fjqk", "1");
		basicinfo.put("dlyxm", params.get("dly"));
		basicinfo.put("ycyxm", params.get("ycy"));
		//上方检视员姓名（灯光检测检测人员）
		basicinfo.put("sfjsyxm", params.get("wjy"));
		//下方检视员姓名
		basicinfo.put("xfjsyxm", params.get("dpjyy"));
		//排放检测员姓名（汽油（柴油）车尾气排放检测人员）
		basicinfo.put("pfjcyxm", params.get("ycy"));
		basicinfo.put("hjwd", params.get("hjwd"));
		basicinfo.put("xdsd", params.get("hjsd"));
		basicinfo.put("dqyl", params.get("dqy"));
		//检验单位
		basicinfo.put("jcdw", jcdw);
		//检验单位的“地址”
		basicinfo.put("jcdwdz", jcdwdz);
		//邮政编码
		basicinfo.put("yzbm", yzbm);
		//联系电话
		basicinfo.put("lxdh", lxdh);
		//检测站经营许可证号
		basicinfo.put("jczjyxkzh", jczjyxkzh);
		//检测开始时间
		basicinfo.put("jckssj", StringUtils.isEmpty(params.get("dlsj"))?"1900-01-01 00:00:00":formatDate(params.get("dlsj").toString()));
		//检测结束时间
		
		
		//检测结论
		basicinfo.put("jcjl", params.get("zjjl"));
		//合规性状态 1待检查 2通过 3不通过
		basicinfo.put("stat", "1");
		//登录时间
		basicinfo.put("dlsj", StringUtils.isEmpty(params.get("dlsj"))?"1900-01-01 00:00:00":formatDate(params.get("dlsj").toString()));
		//人工检验不合格项对应编号
		//basicinfo.put("bhgbh", "1");
		//汽油（柴油）车尾气排放数据开始时间
	//	basicinfo.put("qcsbkssj", "1900-01-01 00:00:00");
		//汽油（柴油）车尾气排放数据结束时间
	//	basicinfo.put("qcsbjssj", "1900-01-01 00:00:00");
		//柴油检测数据（K值）
//		basicinfo.put("kdata", "");
		//制动检测设备动作开始时间
		basicinfo.put("zdjckssj", StringUtils.isEmpty(params.get("zdjckssj"))?"1900-01-01 00:00:00":params.get("zdjckssj"));//B
		//制动检测设备动作结束时间
		basicinfo.put("zdjcjssj", StringUtils.isEmpty(params.get("zdjcjssj"))?"1900-01-01 00:00:00":params.get("zdjcjssj"));
		//灯光检测设备动作开始时间
		basicinfo.put("dgjckssj", StringUtils.isEmpty(params.get("dgjckssj"))?"1900-01-01 00:00:00":params.get("dgjckssj"));//H
		//灯光检测设备动作结束时间
		basicinfo.put("dgjcjssj", StringUtils.isEmpty(params.get("dgjcjssj"))?"1900-01-01 00:00:00":params.get("dgjcjssj"));
		
		String jcjssj=null; 
		
		if(!StringUtils.isEmpty(StringUtils.isEmpty(params.get("dgjcjssj")))) {
			basicinfo.put("jcjssj",StringUtils.isEmpty(params.get("dgjcjssj"))?"1900-01-01 00:00:00":params.get("dgjcjssj"));
		}
		
		
		if(!StringUtils.isEmpty(params.get("dlx"))) {
			Map dlx = (Map)params.get("dlx");
			//碳平衡油耗仪检测数据设备开始时间
			basicinfo.put("tphyhkssj", StringUtils.isEmpty(dlx.get("kssj"))?"1900-01-01 00:00:00":dlx.get("kssj"));//Map dlx = (Map)params.get("dlx");
			//碳平衡油耗仪检测数据设备结束时间
			basicinfo.put("tphyhjssj", StringUtils.isEmpty(dlx.get("jssj"))?"1900-01-01 00:00:00":dlx.get("jssj"));
			//动力性检测数据设备开始时间
			basicinfo.put("dlxjckssj", StringUtils.isEmpty(dlx.get("kssj"))?"1900-01-01 00:00:00":dlx.get("kssj"));
			//动力性检测数据设备结束时间
			basicinfo.put("dlxjcjssj", StringUtils.isEmpty(dlx.get("jssj"))?"1900-01-01 00:00:00":dlx.get("jssj"));
			if(!StringUtils.isEmpty(dlx.get("jssj"))) {
				basicinfo.put("jcjssj",StringUtils.isEmpty(dlx.get("jssj"))?"1900-01-01 00:00:00":dlx.get("jssj"));
				basicinfo.put("jyrq", StringUtils.isEmpty(dlx.get("jssj")) ? "1900-01-01 00:00:00":dlx.get("jssj"));
			}
		}
		//性能检测不合格项对应编号
//		basicinfo.put("xnbhgbh", "1");
		//车辆技术等级评定结论
		System.out.println(params.get("zjjl"));
		basicinfo.put("cljspdjl", params.get("zjjl"));
		//外检编号
//		basicinfo.put("wjbh", "1");
		//性能检测报告
//		basicinfo.put("xnjcbg", "1");
		//车辆类型
		basicinfo.put("vehicleType", params.get("cllx"));
		//行驶总里程
		basicinfo.put("travelMileage", params.get("lcbds"));
		//驻车轴
		basicinfo.put("parkAxle", params.get("zczw"));
		//车辆悬架形式
//		basicinfo.put("vehicleSuspensionForm", "1");
		//技术等级有效期限期至
		basicinfo.put("techLevelEndDate", params.get("techLevelEndDate"));
		//二维检测日期 (如检测类别为2.二级维护竣工质量检验此项不能为空)
//		basicinfo.put("twoMainCheckDate", "1900-01-01 00:00:00");
		//二维有效期限期至(如检测类别为2.二级维护竣工质量检验此项不能为空)
//		basicinfo.put("twoMainEndDate", "1900-01-01 00:00:00");
		//车身颜色
		basicinfo.put("vehicleBodyColor", params.get("csys"));


	} 
	
	public String formatDate(String mills) {
		Date date = new Date();
		date.setTime(Long.valueOf(mills));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
	
	public String getQzdz(Object qzdz) {
		if(!StringUtils.isEmpty(qzdz)) {
			if("01".equals(qzdz) || "02".equals(qzdz)) {
				return "四";
			}else {
				return "二";
			}
		}
		return "";
	}
	
	public String setJylb(Object jylb) {
		if(!StringUtils.isEmpty(jylb)) {
			if("二级维护".equals(jylb)) {
				return "2";
			}else if("等级评定".equals(jylb)) {
				return "1";
			}
		}
		return "";
	}
	

	@RequestMapping("/uploadPhoto")  
    @ResponseBody  
    public String uploadPhoto(@RequestBody Map params) throws IOException {
		
		logger.info("上传照片："+params);
		
		if(local.equals("hn")) {
			uploadHn(params);
		
		}else {
			FtpUtils ftpUtils = new FtpUtils();
			Iterator it = params.keySet().iterator();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(new Date());
			String[] dateArr = date.split("-");
			while(it.hasNext()) {
				String key = it.next().toString();
				if(!"otherInfo".equals(key)) {
					FTPClient ftpClient = initFtpClient();
					String value = String.valueOf(params.get(key));
					Map otherInfo = (Map) params.get("otherInfo");
					
					System.out.println("address:"+"/"+dateArr[0]+"/"+dateArr[1]+"/"+otherInfo.get("bgdbh"));
					boolean uploadFile = ftpUtils.uploadFile("/"+dateArr[0]+"/"+dateArr[1]+"/"+params.get("bgdbh"), value, imageUrl+value, ftpClient);
				}
			}
		}
		
		
		
		return "success";
	}
	
	
	public String uploadHn(Map imgMap) {
		
		Map params = (Map) imgMap.get("otherInfo");
		
		String str = restTemplate.getForEntity(vehUrl, String.class).getBody();
		String[] strArr = str.split(";");
		JSONArray jsonArr = new JSONArray();
		if(strArr.length>0) {
			String bpsStr = strArr[0].substring(strArr[0].indexOf("=")+1);
			jsonArr = JSONArray.parseArray(bpsStr);
		}
		//字典转化前缀
		JSONArray zhqz = getParamByType(jsonArr,"zhqz");
		String qz = "";
		if(zhqz.size() > 0) {
			qz = zhqz.getJSONObject(0).getString("paramValue");
		}
		//////
		//获取token
		String tokenResult = this.uploadZJOfHuNan.getAccessToken();
		JSONObject jsonObject = JSONObject.parseObject(tokenResult);
		String token = "";
		if("1".equals(jsonObject.get("code"))) {
			token = jsonObject.getString("access_token");
		}else {
			return tokenResult;
		}
		
		//获取机动车信息及检测流水号信息接口
		JSONObject vehInfoJson = null;
		String lshResult = this.uploadZJOfHuNan.getVehicleInfoAndDetectSn(params,token,jsonArr, qz);
		JSONObject lshJson = JSONObject.parseObject(lshResult);
		
		if("1".equals(lshJson.get("code"))) {
			vehInfoJson = lshJson.getJSONObject("data");
			//综检流水号
			String detectSn = vehInfoJson.getString("detectSn");
			params.put("zjlsh", detectSn);
		}
		
		Iterator it = imgMap.keySet().iterator();
		
		while(it.hasNext()) {
			String key = it.next().toString();
			if(!"otherInfo".equals(key)) {
				String value = String.valueOf(imgMap.get(key));
				logger.info("key="+key);
				logger.info("params.get(key)="+imgMap.get(key));
				String path = imageUrl+value;
				uploadZJOfHuNan.uploadIamge(params,path,key,token, jsonArr, qz);
			}
		}
		
		return "success";
	}
	
	public FTPClient initFtpClient() throws IOException {
		
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding("utf-8");
        try {
            System.out.println("connecting...ftp服务器:"+hostname+":"+21); 
            ftpClient.connect(hostname, 21); //连接ftp服务器
            ftpClient.login(ftpUsername, ftpPassword); //登录ftp服务器
            int replyCode = ftpClient.getReplyCode(); //是否成功登录服务器
            if(!FTPReply.isPositiveCompletion(replyCode)){
                System.out.println("connect failed...ftp服务器:"+hostname+":"+21); 
            }
            System.out.println("connect successfu...ftp服务器:"+hostname+":"+21); 
        }catch (MalformedURLException e) { 
           e.printStackTrace(); 
        }catch (IOException e) { 
           e.printStackTrace(); 
        } 
        return ftpClient;
    }
}
