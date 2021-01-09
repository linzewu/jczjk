package com.xs.jczjk.manager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xs.jczjk.util.Base64Utils;

@Service
public class UploadZJOfHuNan {
	
	Logger logger = Logger.getLogger(UploadZJOfHuNan.class);
	
	@Autowired
    private RestTemplate restTemplate;
	
	@Value("${jcz.url}")
	private String url;
	
	@Value("${jcz.companyid}")
	private String companyId;
	
	@Value("${jcz.source}")
	private String source;
	
	@Value("${jcz.companyname}")
	private String companyname;
	
	
	
	/**
	 * 获取token
	 * @return
	 */
	public String getAccessToken() {
		HttpHeaders headers = new HttpHeaders();
		JSONObject jObject = new JSONObject();
		jObject.put("username", "czjdc19");
		jObject.put("password", "bG0eB3cD");	
		
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> requestBody = new HashMap();
		requestBody.put("CompanyId", companyId);
		requestBody.put("Source", source);
		requestBody.put("IPCType", "getAccessToken");
		requestBody.put("IPCType.value", jObject.toJSONString());
		HttpEntity<Map<String, String>> request = new HttpEntity<Map<String, String>>(requestBody, headers);
	    return restTemplate.postForObject(url+"/restapi/detecting/get_access_token", request, String.class);
	}
	
	/**
	 * 6.2	检验检测机构基本信息交换与共享接口
	 * @return
	 */
	public String shareDetectionStationInfo(Map<String,String> param,String token) {
		HttpHeaders headers = new HttpHeaders();
		JSONObject jObject = new JSONObject();
		JSONObject detectionStation = new JSONObject();
		JSONObject stationInfo = new JSONObject();
		stationInfo.put("dsId", companyId);//检验检测机构唯一编码，见附录7.1detectRecord
		stationInfo.put("dsName",companyname);//检验检测机构名称detectRecord
		stationInfo.put("dsType", "150");//检验检测机构企业性 质 , 参 照 
		stationInfo.put("createStationDate", "20180927");//建站时间： YYYYMMDD
		stationInfo.put("orgnumber", "91431000MA4PYYNYX5");//统一社会信息代码
		stationInfo.put("ma", "2020102598021");//标定/校准证书编号
		stationInfo.put("maExpDate", "20211020");//标定/校准证书编号到期时间： YYYYMMDD
		stationInfo.put("orgId", source);//归属组织
		stationInfo.put("createDate", "20180927");//创建时间： YYYYMMDD
		stationInfo.put("address", "郴州市北湖区石盖塘街道万寿村四组");//地址
//		stationInfo.put("email", "");//企业邮箱
//		stationInfo.put("industryid", "");//道路经营许可证号
		stationInfo.put("isOnline", "是");//是否在线
		stationInfo.put("principal", "肖洪");//负责人
		stationInfo.put("tel", "15364283179");//负责人联系电话
//		stationInfo.put("introduce ", "");//介绍
//		stationInfo.put("elseInvestDes", "");//其他投资说明
//		stationInfo.put("staffNumber", "");//人员数量
//		stationInfo.put("equipmentNumber", "");//设备数量
//		stationInfo.put("fax ", "");//传真
//		stationInfo.put("website ", "");//网址
//		stationInfo.put("post ", "");//邮编
//		stationInfo.put("longitude ", "");//检验检测机构所处位置经度，十进制
//		stationInfo.put("latitude ", "");//检验检测机构所处位置纬度，十进制
//		stationInfo.put("servicePromise", "");//服务承诺
//		stationInfo.put("chargeStandard", "");//收费标准
//		stationInfo.put("beianNo", "");//备案号
//		stationInfo.put("note ", "");//备注
//		stationInfo.put("staffList", "");//员工基本信息
//		stationInfo.put("staffId", "");//员工编号
//		stationInfo.put("staffName", "");//员工姓名
//		stationInfo.put("idCardNumber", "");//证件号码
//		stationInfo.put("telphone", "");//联系电话
//		stationInfo.put("address ", "");//联系地址
//		stationInfo.put("postion", "");//职务
//		stationInfo.put("deviceList", "");//检测设备信息
//		stationInfo.put("equipmentName", "");//设备名称
//		stationInfo.put("equipmentId", "");//设备编号
//		stationInfo.put("equipmentType", "");//设备型号
//		stationInfo.put("equipmentFunction", "");//设备用途
//		stationInfo.put("equipmentParam", "");//设备参数
//		stationInfo.put("createDate", "");//创建时间： YYYYMMDD
//		stationInfo.put("detectAbility", "");//检测能力信息
//		stationInfo.put("detectCapYear", "");//年检测能力，单位：台次/年
//		stationInfo.put("detectCapMon", "");//月检测能力，单位：台次/月
//		stationInfo.put("detectCapDay", "");//日检测能力，单位：台次/日
//		stationInfo.put("updateDate", "");//统 计 更 新 时 间 ：YYYYMMDD
		
		detectionStation.put("stationInfo", stationInfo);
		jObject.put("detectionStation", detectionStation);
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> requestBody = new HashMap();
		requestBody.put("CompanyId", companyId);
		requestBody.put("Source", source);
		requestBody.put("IPCType", "shareDetectionStationInfo");
		requestBody.put("IPCType.value", jObject.toJSONString());
		HttpEntity<Map<String, String>> request = new HttpEntity<Map<String, String>>(requestBody, headers);
	    String result =  restTemplate.postForObject(url+"/restapi/detecting/put_data;token="+token, request, String.class);
	    
	    //{"status":"检验检测机构不存在，检验检测机构编码[431000019]!","code":"0","data":"{}"}
	    return result;
	}
	
	/**
	 * 6.7	获取机动车信息及检测流水号信息接口
	 * @return
	 */
	public String getVehicleInfoAndDetectSn(Map<String,String> param,String token,JSONArray bpsArr,String zhqz) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject jObject = new JSONObject();
		jObject.put("dsId", companyId);
		jObject.put("vehicleNo", param.get("hphm"));
		jObject.put("plateColorCode", getParamByTypeAndName(bpsArr,zhqz+"cpys",String.valueOf(param.get("cpys"))));
		jObject.put("vinNo", param.get("clsbdh"));
		Map<String, String> requestBody = new HashMap();
		requestBody.put("CompanyId", companyId);
		requestBody.put("Source", source);
		requestBody.put("IPCType", "getVehicleInfoAndDetectSn");
		requestBody.put("IPCType.value", jObject.toJSONString());
		
		System.out.println(requestBody);
		HttpEntity<Map<String, String>> request = new HttpEntity<Map<String, String>>(requestBody, headers);
	    String result =  restTemplate.postForObject(url+"/restapi/detecting/get_data;token="+token, request, String.class);
	    
	    //{"status":"检验检测机构不存在，检验检测机构编码[431000019]!","code":"0","data":"{}"}
	    return result;
	}
	
	/**
	 * 6.3	机动车基本信息交换与共享接口
	 * @param param
	 * @param token
	 * @return
	 */
	public String shareVehicleInfo(Map<String,String> param,String token) {		
		HttpHeaders headers = new HttpHeaders();
		JSONObject jObject = new JSONObject();
		jObject.put("dsId", companyId);
		jObject.put("vehicleNo", param.get("hphm"));//车牌号码
		jObject.put("vehicleBodyColor", param.get("csys"));//车身颜色,见附录7.2
		jObject.put("plateColorCode", param.get("cpys"));//车牌颜色代码,见附录7.3
		jObject.put("vinNo", param.get("clsbdh"));//车辆识别代码， 填写17位VIN号码
		jObject.put("vehicleBrandModel", param.get(""));//厂牌型号，参照JT/T 697.7
		jObject.put("registDate", param.get(""));//注册日期，车辆行驶证初次领证日期:YYYYMMDD
		jObject.put("vehicleType", param.get("clxh"));//车辆类型， 参照GA 24.4
		jObject.put("overallSize", param.get("cwkc")+"×"+param.get("cwkk")+"×"+param.get("cwkg"));//外廓尺寸，格式：长×宽×高单位： mm
		jObject.put("axleAmount", StringUtils.isEmpty(param.get("zs"))?"0":param.get("zs"));//车辆轴数， 单位：轴
		jObject.put("steeringAxleAmount", param.get(""));//转向轴数， 单位：轴
		jObject.put("vehicleWeight", param.get(""));//整备质量，单位： kg
		jObject.put("approveWeight", param.get(""));//核定载质量，单位： kg
		jObject.put("totalWeight", param.get(""));//总质量，单位： kg
		jObject.put("ownerName", param.get(""));//所属业户名称
		jObject.put("trailerVehicleNo", param.get(""));//挂车牌照号码
		jObject.put("transCertificateCode", param.get(""));//道路运输证号，新车可为空
		jObject.put("transCertificateFirstDate", param.get(""));//道路运输证初领日期:YYYYMMDD
		jObject.put("engineNo", param.get("fdjh"));//发动机号码
		jObject.put("engineModel", param.get(""));//发动机型号
		jObject.put("chassisNo", param.get(""));//底盘号码
		jObject.put("productionDate", param.get(""));//出厂日期:YYYYMMDD
		jObject.put("busTypeLevel", param.get(""));//客车类型与等级，参照JT/T 697.7
		jObject.put("brakeModel", param.get(""));//制动方式，参照GB/T 26765
		jObject.put("parkType", param.get(""));//驻车类型，规定值：手刹、脚刹、电子驻车
		jObject.put("driveType", param.get(""));//驱动型式格式， 如： 4×2后驱
		jObject.put("driveAxleAmount", param.get(""));//车辆驱动轴数， 单位：轴
		jObject.put("fuelType", param.get(""));//燃油类别，参照JT/T697.7
		jObject.put("lampSystem", param.get(""));//前照灯制，规定值：二、四
		jObject.put("vehicleSuspensi", param.get(""));//车辆悬架形式，参照JT/T 697.7
		jObject.put("onForm", param.get(""));//
		jObject.put("ratifiedLoadCap", param.get(""));//核定满载人员数
		jObject.put("acity", param.get(""));//
		jObject.put("seatCount", param.get(""));//座位（铺）数， 单位：位，客车必填，货车非必填
		jObject.put("travelMileage", param.get(""));//行驶里程
		jObject.put("farLightCanAdju", param.get(""));//远光束能否单独调整，参照 GB/T
		jObject.put("st", param.get(""));//26765
		jObject.put("parkAxle", param.get(""));//驻车轴，用数字表示，作用在多轴时，各驻车轴数用“,”分开
		jObject.put("maxDesignSpeed", param.get(""));//最大设计车速
		jObject.put("isTurbo", param.get(""));//是否涡轮增压
		jObject.put("isAbs", param.get(""));//是否ABS制动
		jObject.put("engineCylinder", param.get(""));//发动机缸数
		
		
		headers.setContentType(MediaType.APPLICATION_JSON);
		Map<String, String> requestBody = new HashMap();
		requestBody.put("CompanyId", companyId);
		requestBody.put("Source", source);
		requestBody.put("IPCType", "getVehicleInfoAndDetectSn");
		requestBody.put("IPCType.value", jObject.toJSONString());
		HttpEntity<Map<String, String>> request = new HttpEntity<Map<String, String>>(requestBody, headers);
	    String result =  restTemplate.postForObject(url+"/restapi/detecting/get_data;token="+token, request, String.class);
		
		
		return result;		
	}
	
	/**
	 * 6.8	仪器设备结果组合信息交换与共享接口
	 * @param param
	 * @param token
	 * @return
	 */
	public String shareDetectInfo(Map<String,Object> param,String token,String detectSn,JSONArray bpsArr,String zhqz) {
		
		SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");
		
		HttpHeaders headers = new HttpHeaders();
		JSONObject jObject = new JSONObject();
		//System.out.println("数据1："+param);
		JSONObject detectRecord = new JSONObject();
		detectRecord.put("dsId", companyId);//检验检测机构唯一编码，见附录7.1detectRecord
		detectRecord.put("dsName",companyname);//检验检测机构名称detectRecord
		detectRecord.put("detectSn",param.get("zjlsh"));//检测流水号,见附录7.9detectRecord
		detectRecord.put("detectType",getParamByTypeAndName(bpsArr,zhqz+"jcxz",String.valueOf(param.get("jcxz"))));//检测类别,见附录7.8detectRecord
		detectRecord.put("detectDate",param.get("uplinedate"));//检测日期:YYYY-MM-DDhh:mm:ssdetectRecord
		detectRecord.put("client",param.get("zjwtr"));//委托人detectRecord
		detectRecord.put("vehicleNo",param.get("hphm"));//车牌号码detectRecord
		detectRecord.put("plateColorCode",getParamByTypeAndName(bpsArr,zhqz+"cpys",String.valueOf(param.get("cpys"))));//车牌颜色代码，见附录7.3detectRecord
		detectRecord.put("vinNo",param.get("clsbdh"));//车辆识别代码detectRecord
		detectRecord.put("vehicleType",param.get("cllx"));//车辆类型：参照GA24.4detectRecord
		detectRecord.put("engineNo",param.get("fdjh"));//发动机号码detectRecord
		detectRecord.put("travelMileage",StringUtils.isEmpty(param.get("lcbds"))?"0":param.get("lcbds").toString());//行驶总里程，单位kmdetectRecord
		detectRecord.put("fuelType",param.get("rlzl"));//燃油类别，参照JT/T697.7detectRecord
		detectRecord.put("steeringAxleAmount",param.get(""));//转向轴数，单位：轴detectRecord
		detectRecord.put("detectLine",getJcxdh(param.get("jcxdh").toString()));//检测线别：规定值：大写英文字母detectRecord
		detectRecord.put("busiType",getParamByTypeAndName(bpsArr,zhqz+"zjywlx",String.valueOf(param.get("zjywlx"))));//业务类型：规定值：申请、在用detectRecord
		detectRecord.put("transCertificateCode",param.get("dlyxzh"));//道路运输证号detectRecord
		detectRecord.put("trailerVehicleNo",param.get(""));//挂车牌照号码detectRecord
		detectRecord.put("trailerVehicleType",param.get(""));//挂车类型，参照GB/T3730.2detectRecord
		
		if(param.get("ccdjrq")!=null) {
			long t =(long) param.get("ccdjrq");
			Date ccdjrq = new Date(t);
			detectRecord.put("productionDate",sdf.format(ccdjrq));//出厂日期:YYYYMMDDdetectRecord
		}
		
		if(param.get("ccrq")!=null) {
			long t =(long) param.get("ccrq");
			Date ccrq = new Date(t);
			detectRecord.put("registDate",sdf.format(ccrq));//注册日期:YYYYMMDDdetectRecord
		}
		
		
		
		detectRecord.put("vehicleBrandModel",param.get("clxh"));//车辆型号：参照JT/T697.7detectRecord
		detectRecord.put("vehicleBodyColor",param.get("csys"));//车身颜色，见附录7.3.2detectRecord
		detectRecord.put("driveType",param.get("qdxs"));//驱动型式，如：4×2后驱detectRecord
		detectRecord.put("vehicleSuspensionForm",param.get(""));//车辆悬架形式，参照JT/T697.7detectRecord
		detectRecord.put("trailerVehicleAxleAmount",param.get(""));//挂车轴数，单位：轴detectRecord
		if(param.get("gl")!=null) {
			detectRecord.put("compressIgnitEnginePower",param.get("gl").toString());
		}
		//压燃式发动机额定功率，单位：kWdetectRecord
		detectRecord.put("ratedTorque",StringUtils.isEmpty(param.get("ednj"))?"0":param.get("ednj").toString());//点燃式额定扭矩detectRecord
		detectRecord.put("ratedSpeedOfIgnit",StringUtils.isEmpty(param.get("edzs"))?"0":param.get("edzs").toString());//点燃式额定转速detectRecord
		
		if(param.get("ltlx")!=null) {
			detectRecord.put("driveWheelModel",param.get("ltlx").toString());//驱动轮轮胎规格型号detectRecord
		}
		
		
		detectRecord.put("totalWeight",StringUtils.isEmpty(param.get("zzl"))?"0":param.get("zzl").toString());//总质量，单位：kgdetectRecord
		detectRecord.put("vehicleHeight",param.get("cwkg"));//车高，单位：mmdetectRecord
		
		detectRecord.put("frontTrack",StringUtils.isEmpty(param.get("qlj"))?"0":param.get("qlj").toString());//前轮距，单位：mmdetectRecord
		
		detectRecord.put("vehicleLength",param.get("cwkc"));//客车车长，单位：mmdetectRecord
		detectRecord.put("busTypeLevel",param.get("kcdj"));//客车类型与等级，参照JT/T697.7detectRecord
		if(param.get("hccsxs")!=null) {
			detectRecord.put("truckBodyType",param.get("hccsxs").toString());//货车车身型式detectRecord
		}
		
		detectRecord.put("driveAxleAmount",param.get(""));//驱动轴数，单位：轴detectRecord
		detectRecord.put("driveAxleLoadMass",StringUtils.isEmpty(param.get("qdzkzzl"))?"0":param.get("qdzkzzl"));//驱动轴空载质量，单位:kgdetectRecord
		detectRecord.put("totalWeightOfTractor",StringUtils.isEmpty(param.get("qycmzzl"))?"0":param.get("qycmzzl"));//牵引车满载总质量（最大允许总质量），单位:kgdetectRecord
		detectRecord.put("shaftForm",param.get("bzzxs"));//并装轴形式，如：并装双轴、并装三轴等detectRecord
		detectRecord.put("lampSystem",getQzdz(param.get("qzdz")));//前照灯制，规定值：二、四detectRecord
		detectRecord.put("seatCount",StringUtils.isEmpty(param.get("kczws"))?"0":param.get("kczws"));//座位（铺）数，单位：位，客车必填，货车非必填detectRecord
		detectRecord.put("mainVehicleAxleAmount",StringUtils.isEmpty(param.get("zs"))?"0":param.get("zs").toString());//单车（主车）轴数detectRecord
		detectRecord.put("overallSize",param.get("cwkc")+"×"+param.get("cwkk")+"×"+param.get("cwkg"));//单车外廓尺寸，格式：长×宽×高，单位：mmdetectRecord
		detectRecord.put("overallSizeTrailer",param.get(""));//挂车外廓尺寸，格式：长×宽×高，单位：mmdetectRecord
		detectRecord.put("farLightCanAdjust","否");//远光束能否单独调整detectRecord
		detectRecord.put("parkAxle",param.get("zczw"));//驻车轴，用数字表示，作用在多轴时，各驻车轴数用“,”分开detectRecord
		detectRecord.put("carriageSsideboardHeight",StringUtils.isEmpty(param.get("cxlbgd"))?"0":param.get("cxlbgd"));//单车车厢栏板高度，单位：mmdetectRecord
		detectRecord.put("ssideboardHeightTrailer",param.get(""));//挂车车厢栏板高度，单位：mmdetectRecord
		detectRecord.put("detectTotalCount",param.get("jycs").toString());//总检次数，单位：次detectRecord
		
		//燃料经济性
		JSONObject fuelEconomy = new JSONObject();
		if(!StringUtils.isEmpty(param.get("dlx"))) {
			Map dlx = (Map)param.get("dlx");
			
			
			JSONObject power = new JSONObject();
			
			
			power.put("standardPower",StringUtils.isEmpty(dlx.get("dlx_dbgl"))?"0":dlx.get("dlx_dbgl"));//达标功率，单位：kWpower
			power.put("ratedSpeed",StringUtils.isEmpty(dlx.get("dlx_edcs"))?"0":dlx.get("dlx_edcs"));//额定车速，单位：km/hpower
			power.put("loadingForce",StringUtils.isEmpty(dlx.get("dlx_jzl"))?"0":dlx.get("dlx_jzl"));//加载力，单位：Npower
			power.put("steadySpeed",StringUtils.isEmpty(dlx.get("dlx_wdcs"))?"0":dlx.get("dlx_wdcs"));//稳定车速，单位：km/hpower
			power.put("evaluate",getpd(param.get("dlx_pd").toString()));//判定：见附录7.11power
			detectRecord.put("power",power);//动力性节点detectRecord
			//动力性
			//basicinfo.put("dlx", dlx.get("dlx_pd"));
			//basicinfo.put("rljjx", dlx.get("yh_pd"));
			fuelEconomy.put("measuredValue", StringUtils.isEmpty(dlx.get("yh_scz"))?"0":dlx.get("yh_scz"));//实测值fuelEconomy
			if(param.get("yh_pd")!=null&&!"0".equals(param.get("yh_pd"))) {
				fuelEconomy.put("speedFuelPerHundredKm", param.get("yhxz"));//等速百公里油耗标准限值fuelEconomy
				fuelEconomy.put("evaluate",getpd(param.get("yh_pd").toString()));//判定：见附录7.11fuelEconomy
			}
		}
		
		
		
	
		detectRecord.put("fuelEconomy", fuelEconomy);
		//制动性
		JSONObject brake = new JSONObject();
		//原始数据
		JSONArray initData = new JSONArray();//原始数据brake
		
		JSONArray singleAxle = new JSONArray();//单轴brake
		
		//一轴
		if(!StringUtils.isEmpty(param.get("b1"))) {
			Map b1 = (Map)param.get("b1");
			JSONObject yz = new JSONObject();
			yz.put("axleSerialNo",1);//轴序号(第几轴)，规定值：1、2、3、4、5、6initData
			yz.put("leftHorizontalLoadWheel",b1.get("zlh"));//水平称重轮荷（左轮），单位：daNinitData
			yz.put("rightHorizontalLoadWheel",b1.get("ylh"));//水平称重轮荷（右轮），单位：daNinitData
			yz.put("axleLoadOfCompoundTable",b1.get("zjzh"));//复合台称重轴荷，单位：daNinitData
			//yz.put("leftDynamicWheelLoad",param.get(""));//动态轮荷（左轮），单位：daNinitData
			//yz.put("rightDynamicWheelLoad",param.get(""));//动态轮荷（右轮），单位：daNinitData
			yz.put("leftDriveBrakeForce",b1.get("zzdl"));//行车制动力（左轮），单位：daNinitData
			yz.put("rightDriveBrakeForce",b1.get("yzdl"));//行车制动力（右轮），单位：daNinitData
			
			////驻车制动力
			if(!StringUtils.isEmpty(param.get("b0_1"))) {
				Map b0_1 = (Map)param.get("b0_1");
				yz.put("leftParkBrakeForce", b0_1.get("zzdl"));//驻车制动力（左轮），单位：daNinitData
				yz.put("rightParkBrakeForce", b0_1.get("yzdl"));//驻车制动力（右轮），单位：daNinitData
			}
			initData.add(yz);
			
			JSONObject yzAxle = new JSONObject();
			yzAxle.put("axleSerialNo",1);//轴序号(第几轴)，规定值：1、2、3、4、5、6singleAxle
			yzAxle.put("axleBrakeRate",b1.get("kzxczdl"));//轴制动率，单位：%singleAxle
			yzAxle.put("brakeUnbalanceRate",b1.get("kzbphl"));//制动不平衡率，单位：%singleAxle
			yzAxle.put("leftMaxProcessDiff",b1.get("zzdlcd"));//过程差最大点（左轮），单位：daNsingleAxle
			yzAxle.put("rightMaxProcessDiff",b1.get("yzdlcd"));//过程差最大点（右轮），单位：daNsingleAxle
			yzAxle.put("leftRetardingForce",b1.get("zzzlf"));//车轮阻滞率（左轮），单位：%singleAxle
			yzAxle.put("rightRetardingForce",b1.get("yzzlf"));//车轮阻滞率（右轮），单位：%singleAxle
			yzAxle.put("evaluate",b1.get("zpd"));//判定，见附录7.11singleAxle
			singleAxle.add(yzAxle);
		}
		//二轴
		if(!StringUtils.isEmpty(param.get("b2"))) {
			Map b2 = (Map)param.get("b2");
			JSONObject ez = new JSONObject();
			ez.put("axleSerialNo",2);//轴序号(第几轴)，规定值：1、2、3、4、5、6initData
			ez.put("leftHorizontalLoadWheel",b2.get("zlh"));//水平称重轮荷（左轮），单位：daNinitData
			ez.put("rightHorizontalLoadWheel",b2.get("ylh"));//水平称重轮荷（右轮），单位：daNinitData
			ez.put("axleLoadOfCompoundTable",b2.get("zjzh"));//复合台称重轴荷，单位：daNinitData
			//yz.put("leftDynamicWheelLoad",param.get(""));//动态轮荷（左轮），单位：daNinitData
			//yz.put("rightDynamicWheelLoad",param.get(""));//动态轮荷（右轮），单位：daNinitData
			ez.put("leftDriveBrakeForce",b2.get("zzdl"));//行车制动力（左轮），单位：daNinitData
			ez.put("rightDriveBrakeForce",b2.get("yzdl"));//行车制动力（右轮），单位：daNinitData
			
			if(!StringUtils.isEmpty(param.get("b0_2"))) {
				Map b0_2 = (Map)param.get("b0_2");
				ez.put("leftParkBrakeForce", b0_2.get("zzdl"));
				ez.put("rightParkBrakeForce", b0_2.get("yzdl"));
			}
			initData.add(ez);
			
			JSONObject yzAxle = new JSONObject();
			yzAxle.put("axleSerialNo",2);//轴序号(第几轴)，规定值：1、2、3、4、5、6singleAxle
			yzAxle.put("axleBrakeRate",b2.get("kzxczdl"));//轴制动率，单位：%singleAxle
			yzAxle.put("brakeUnbalanceRate",b2.get("kzbphl"));//制动不平衡率，单位：%singleAxle
			yzAxle.put("leftMaxProcessDiff",b2.get("zzdlcd"));//过程差最大点（左轮），单位：daNsingleAxle
			yzAxle.put("rightMaxProcessDiff",b2.get("yzdlcd"));//过程差最大点（右轮），单位：daNsingleAxle
			yzAxle.put("leftRetardingForce",b2.get("zzzlf"));//车轮阻滞率（左轮），单位：%singleAxle
			yzAxle.put("rightRetardingForce",b2.get("yzzlf"));//车轮阻滞率（右轮），单位：%singleAxle
			yzAxle.put("evaluate",b2.get("zpd"));//判定，见附录7.11singleAxle
			singleAxle.add(yzAxle);
		}
		//三轴
		if(!StringUtils.isEmpty(param.get("b3"))) {
			Map b3 = (Map)param.get("b3");
			JSONObject sz = new JSONObject();
			sz.put("axleSerialNo",3);//轴序号(第几轴)，规定值：1、2、3、4、5、6initData
			sz.put("leftHorizontalLoadWheel",b3.get("zlh"));//水平称重轮荷（左轮），单位：daNinitData
			sz.put("rightHorizontalLoadWheel",b3.get("ylh"));//水平称重轮荷（右轮），单位：daNinitData
			sz.put("axleLoadOfCompoundTable",b3.get("zjzh"));//复合台称重轴荷，单位：daNinitData
			//yz.put("leftDynamicWheelLoad",param.get(""));//动态轮荷（左轮），单位：daNinitData
			//yz.put("rightDynamicWheelLoad",param.get(""));//动态轮荷（右轮），单位：daNinitData
			sz.put("leftDriveBrakeForce",b3.get("zzdl"));//行车制动力（左轮），单位：daNinitData
			sz.put("rightDriveBrakeForce",b3.get("yzdl"));//行车制动力（右轮），单位：daNinitData
			
			if(!StringUtils.isEmpty(param.get("b0_3"))) {
				Map b0_3 = (Map)param.get("b0_3");
				sz.put("leftParkBrakeForce", b0_3.get("zzdl"));
				sz.put("rightParkBrakeForce", b0_3.get("yzdl"));
			}
			initData.add(sz);
			
			JSONObject yzAxle = new JSONObject();
			yzAxle.put("axleSerialNo",3);//轴序号(第几轴)，规定值：1、2、3、4、5、6singleAxle
			yzAxle.put("axleBrakeRate",b3.get("kzxczdl"));//轴制动率，单位：%singleAxle
			yzAxle.put("brakeUnbalanceRate",b3.get("kzbphl"));//制动不平衡率，单位：%singleAxle
			yzAxle.put("leftMaxProcessDiff",b3.get("zzdlcd"));//过程差最大点（左轮），单位：daNsingleAxle
			yzAxle.put("rightMaxProcessDiff",b3.get("yzdlcd"));//过程差最大点（右轮），单位：daNsingleAxle
			yzAxle.put("leftRetardingForce",b3.get("zzzlf"));//车轮阻滞率（左轮），单位：%singleAxle
			yzAxle.put("rightRetardingForce",b3.get("yzzlf"));//车轮阻滞率（右轮），单位：%singleAxle
			yzAxle.put("evaluate",b3.get("zpd"));//判定，见附录7.11singleAxle
			singleAxle.add(yzAxle);
		}
		//四轴
		if(!StringUtils.isEmpty(param.get("b4"))) {
			Map b4 = (Map)param.get("b4");
			JSONObject sz = new JSONObject();
			sz.put("axleSerialNo",4);//轴序号(第几轴)，规定值：1、2、3、4、5、6initData
			sz.put("leftHorizontalLoadWheel",b4.get("zlh"));//水平称重轮荷（左轮），单位：daNinitData
			sz.put("rightHorizontalLoadWheel",b4.get("ylh"));//水平称重轮荷（右轮），单位：daNinitData
			sz.put("axleLoadOfCompoundTable",b4.get("zjzh"));//复合台称重轴荷，单位：daNinitData
			//yz.put("leftDynamicWheelLoad",param.get(""));//动态轮荷（左轮），单位：daNinitData
			//yz.put("rightDynamicWheelLoad",param.get(""));//动态轮荷（右轮），单位：daNinitData
			sz.put("leftDriveBrakeForce",b4.get("zzdl"));//行车制动力（左轮），单位：daNinitData
			sz.put("rightDriveBrakeForce",b4.get("yzdl"));//行车制动力（右轮），单位：daNinitData
			
			if(!StringUtils.isEmpty(param.get("b0_4"))) {
				Map b0_4 = (Map)param.get("b0_4");
				sz.put("leftParkBrakeForce", b0_4.get("zzdl"));
				sz.put("rightParkBrakeForce", b0_4.get("yzdl"));
			}
			initData.add(sz);
			
			JSONObject yzAxle = new JSONObject();
			yzAxle.put("axleSerialNo",4);//轴序号(第几轴)，规定值：1、2、3、4、5、6singleAxle
			yzAxle.put("axleBrakeRate",b4.get("kzxczdl"));//轴制动率，单位：%singleAxle
			yzAxle.put("brakeUnbalanceRate",b4.get("kzbphl"));//制动不平衡率，单位：%singleAxle
			yzAxle.put("leftMaxProcessDiff",b4.get("zzdlcd"));//过程差最大点（左轮），单位：daNsingleAxle
			yzAxle.put("rightMaxProcessDiff",b4.get("yzdlcd"));//过程差最大点（右轮），单位：daNsingleAxle
			yzAxle.put("leftRetardingForce",b4.get("zzzlf"));//车轮阻滞率（左轮），单位：%singleAxle
			yzAxle.put("rightRetardingForce",b4.get("yzzlf"));//车轮阻滞率（右轮），单位：%singleAxle
			yzAxle.put("evaluate",b4.get("zpd"));//判定，见附录7.11singleAxle
			singleAxle.add(yzAxle);
		}		
		brake.put("initData", initData);
		brake.put("singleAxle", singleAxle);
		//整车
		JSONObject wholeVehicle = new JSONObject();
		//单车
		JSONObject singleVehicle = new JSONObject();
		//整车
		if(!StringUtils.isEmpty(param.get("other"))) {
			Map other = (Map)param.get("other");
			singleVehicle.put("horizontalWeight", other.get("jczczbzl"));//水平称重，单位：daNsingleVehicle
			singleVehicle.put("wholeVehicleBrakeRate", other.get("zczdl"));//整车制动率，单位：%singleVehicle
			//制动性台架检验整车项目综合“判定” 1通过2 不通过
			//basicinfo.put("jcxmzh", other.get("zcpd"));	
			singleVehicle.put("evaluate", other.get("zcpd"));	//判定：见附录7.11singleVehicle
			
		}
		if(!StringUtils.isEmpty(param.get("par"))) {
			Map par = (Map)param.get("par");
			singleVehicle.put("parkBrakeRate", par.get("tczdl"));//驻车制动率，单位：%singleVehicle
		}
		
		wholeVehicle.put("singleVehicle", singleVehicle);
		
		brake.put("wholeVehicle", wholeVehicle);
		
		
		
//		jObject.put("vehicleSerial1",param.get(""));//汽车列车项目第1部分wholeVehicle
//		jObject.put("horizontalWeight",param.get(""));//水平称重，单位：daNvehicleSerial1
//		jObject.put("vehicleBrakeRateTractor",param.get(""));//整车制动率（牵），单位：%vehicleSerial1
//		jObject.put("vehicleBrakeRateTrailer",param.get(""));//整车制动率（挂），单位：%vehicleSerial1
//		jObject.put("parkBrakeRate",param.get(""));//驻车制动率，单位：%vehicleSerial1
//		jObject.put("brakeCoordinateTime",param.get(""));//制动协调时间，单位：svehicleSerial1
//		jObject.put("evaluate",param.get(""));//判定：见附录7.11vehicleSerial1
//		jObject.put("brakeSeqTime",param.get(""));//制动时序（时间）wholeVehicle
//		jObject.put("axle1",param.get(""));//轴1，单位：sbrakeSeqTime
//		jObject.put("Axle2",param.get(""));//轴2，单位：sbrakeSeqTime
//		jObject.put("Axle3",param.get(""));//轴3，单位：sbrakeSeqTime
//		jObject.put("Axle4",param.get(""));//轴4，单位：sbrakeSeqTime
//		jObject.put("Axle5",param.get(""));//轴5，单位：sbrakeSeqTime
//		jObject.put("Axle6",param.get(""));//轴6，单位：sbrakeSeqTime
//		jObject.put("evaluate",param.get(""));//判定：见附录7.11brakeSeqTime
//		jObject.put("brakeSeq",param.get(""));//制动时序（时序）wholeVehicle
//		jObject.put("axle1",param.get(""));//轴1，规定值:1、2、3、4、5、6brakeSeq
//		jObject.put("axle2",param.get(""));//轴2，规定值：1、2、3、4、5、6brakeSeq
//		jObject.put("axle3",param.get(""));//轴3，规定值：1、2、3、4、5、6brakeSeq
//		jObject.put("axle4",param.get(""));//轴4，规定值：1、2、3、4、5、6brakeSeq
//		jObject.put("axle5",param.get(""));//轴5，规定值：1、2、3、4、5、6brakeSeq
//		jObject.put("axle6",param.get(""));//轴6，规定值：1、2、3、4、5、6brakeSeq
//		jObject.put("evaluate",param.get(""));//判定，见附录7.11brakeSeq
//		jObject.put("vehicleSerial2",param.get(""));//汽车列车项目第2部分wholeVehicle
//		jObject.put("brakeRateTractorTrain",param.get(""));//整车制动率比%(牵引车/列车)，单位：%vehicleSerial2
//		jObject.put("brakeRateTrailerTrain",param.get(""));//整车制动率比%(挂车/列车)，单位：%vehicleSerial2
//		jObject.put("evaluate",param.get(""));//判定，见附录7.11vehicleSerial2
		
		//路试
//		if(!StringUtils.isEmpty(param.get("lsy"))) {
//			Map lsy = (Map)param.get("lsy");
//			JSONObject roadTest = new JSONObject();
//			
//			JSONObject driveBrake = new JSONObject();			
//			driveBrake.put("initialVelocity",lsy.get("zdcsd"));//初速度，单位：km/hdriveBrake
//			//driveBrake.put("laneWidth",param.get(""));//试车道宽度，单位：mdriveBrake
//			driveBrake.put("brakeDistance",lsy.get("xckzzdjl"));//制动距离，单位：mdriveBrake
//			driveBrake.put("mfdd",lsy.get("xckzmfdd"));//MFDD，单位：m/s2driveBrake
//			driveBrake.put("brakeStability",lsy.get("zdwdx"));//制动稳定性，规定值：稳定、不稳定driveBrake
//			driveBrake.put("brakeCoordinateTime",lsy.get("zdxtsj"));//汽车列车制动协调时间，driveBrake
//			driveBrake.put("evaluate",lsy.get("lsjg"));//判定，见附录7.11driveBrake
//			
//			roadTest.put("driveBrake", driveBrake);
//			//驻车制动
//			JSONObject parkBrake = new JSONObject();
//			parkBrake.put("parkSlope",lsy.get("zcpd"));//驻车坡度，单位：%parkBrake
//			parkBrake.put("parkResult",lsy.get("lszczdpd"));//不少于5min坡道驻车情况，规定值：溜坡、不溜坡parkBrake
//			parkBrake.put("evaluate",lsy.get("lszczdpd"));//判定，见附录7.11
//			
//			roadTest.put("parkBrake", parkBrake);
//			brake.put("roadTest", roadTest);
//			
//		}
		
		//排放性
		JSONObject emission = new JSONObject();  //detectRecord
		
		//汽油车
		JSONObject gasolineVehicle = new JSONObject();
		if(!StringUtils.isEmpty(param.get("sds"))) {
			Map sds = (Map)param.get("sds");
			gasolineVehicle.put("highCo", sds.get("cogclz"));//CO_高怠速，单位：%gasolineVehicle
			gasolineVehicle.put("highHc", sds.get("hcgclz"));//HC_高怠速，单位：10-6gasolineVehicle
			gasolineVehicle.put("highL", sds.get("kqxs"));//λ_高怠速gasolineVehicle
			gasolineVehicle.put("lowCo", sds.get("codclz"));//CO_怠速，单位：%gasolineVehicle
			gasolineVehicle.put("lowHc", sds.get("hcdclz"));//HC_怠速，单位：10-6gasolineVehicle
		}
		
		if(!StringUtils.isEmpty(param.get("wt"))) {
			Map wt = (Map)param.get("wt");
			gasolineVehicle.put("co5025", wt.get("clzco25"));//CO_5025，单位：%gasolineVehicle
			gasolineVehicle.put("hc5025", wt.get("clzhc25"));//HC_5025，单位：10-6gasolineVehicle
			gasolineVehicle.put("no5025", wt.get("clzno25"));//NO_5025，单位：10-6gasolineVehicle
			gasolineVehicle.put("co2540", wt.get("clzco40"));//CO_2540，单位：%gasolineVehicle
			gasolineVehicle.put("hc2540", wt.get("clzhc40"));//HC_2540，单位：10-6gasolineVehicle
			gasolineVehicle.put("no2540", wt.get("clzno40"));//NO_2540，单位：10-6gasolineVehicle
		}
		
//		gasolineVehicle.put("vmasCo",param.get(""));//CO_简易瞬态工况，单位：g/kmgasolineVehicle
//		gasolineVehicle.put("vmasHc",param.get(""));//HC_简易瞬态工况，单位：g/kmgasolineVehicle
//		gasolineVehicle.put("vmasNo",param.get(""));//NO_简易瞬态工况，单位：g/kmgasolineVehicle
//		gasolineVehicle.put("vmasHcNo",param.get(""));//HC_NO_简易瞬态工况，单位：g/kmgasolineVehicle
//		gasolineVehicle.put("evaluate",param.get(""));//判定，见附录7.11emission
		emission.put("gasolineVehicle", gasolineVehicle);
		
		
		
		
		//柴油车
		JSONObject dieselVehicle = new JSONObject();
		if(!StringUtils.isEmpty(param.get("yd"))) {
			Map yd = (Map)param.get("yd");
			
			dieselVehicle.put("ratio1",yd.get("d2clz"));//光吸收系数1，单位：m-1dieselVehicle
			dieselVehicle.put("ratio2",yd.get("d3clz"));//光吸收系数2，单位：m-1dieselVehicle
			dieselVehicle.put("ratio3",yd.get("d4clz"));//光吸收系数3，单位：m-1dieselVehicle
			dieselVehicle.put("ratioBalance",yd.get("ydpjz"));//光吸收系数平均，单位：m-1dieselVehicle
		}
		emission.put("dieselVehicle",dieselVehicle);
		
		
		
//		jObject.put("smoke1",param.get(""));//滤纸烟度1，单位：BSUdieselVehicle
//		jObject.put("smoke2",param.get(""));//滤纸烟度2，单位：BSUdieselVehicle
//		jObject.put("smoke3",param.get(""));//滤纸烟度3，单位：BSUdieselVehicle
//		jObject.put("smokeBalance",param.get(""));//滤纸烟度平均，单位：BSUdieselVehicle
//		jObject.put("ratio100",param.get(""));//光吸收系数100%，单位：m-1dieselVehicle
//		jObject.put("ratio90",param.get(""));//光吸收系数90%，单位：m-1dieselVehicle
//		jObject.put("ratio80",param.get(""));//光吸收系数80%，单位：m-1dieselVehicle
//		jObject.put("smokeBalance",param.get(""));//滤纸烟度平均dieselVehicle
//		jObject.put("maxWheelSidePower",param.get(""));//实测最大轮边功率，单位：kWdieselVehicle
//		jObject.put("evaluate",param.get(""));//判定，见附录7.11detectRecord
		
		//悬架
		JSONArray suspension  = new JSONArray(); 
		if(!StringUtils.isEmpty(param.get("xj1"))) {
			Map xj1 = (Map)param.get("xj1");
			JSONObject suspension_qz  = new JSONObject(); 
			suspension_qz.put("axleType","前轴");//轴类型，规定值:前轴、后轴suspension
			suspension_qz.put("efficiencyOfLeftAxle",xj1.get("zxsl"));//左吸收率，单位：%suspension
			suspension_qz.put("efficiencyOfRightAxle",xj1.get("yxsl"));//右吸收率，单位：%suspension
			suspension_qz.put("absorbRateDifOfAxle",xj1.get("zyc"));//左右差，单位：%suspension
			suspension_qz.put("evaluate",xj1.get("zpd"));//判定，见附录7.11detectRecord
			suspension.add(suspension_qz);
		}
		if(!StringUtils.isEmpty(param.get("xj2"))) {
			Map xj2 = (Map)param.get("xj2");
			
			JSONObject suspension_hz  = new JSONObject(); 
			suspension_hz.put("axleType","后轴");//轴类型，规定值:前轴、后轴suspension
			suspension_hz.put("efficiencyOfLeftAxle",xj2.get("zxsl"));//左吸收率，单位：%suspension
			suspension_hz.put("efficiencyOfRightAxle",xj2.get("yxsl"));//右吸收率，单位：%suspension
			suspension_hz.put("absorbRateDifOfAxle",xj2.get("zyc"));//左右差，单位：%suspension
			suspension_hz.put("evaluate",xj2.get("zpd"));//判定，见附录7.11detectRecord
			suspension.add(suspension_hz);
		}
		
		//前照灯
		JSONArray mainLamp = new JSONArray();
		
		//灯光
		JSONObject zwyg = new JSONObject();
		zwyg.put("lampType","1");//灯类型，见附录7.12
		if(!StringUtils.isEmpty(param.get("h1_j"))) {
			Map h1_j = (Map)param.get("h1_j");
						
			zwyg.put("farLightLampHight",h1_j.get("dg"));//灯高（远光），单位：mmmainLamp			
			
			zwyg.put("nearLightVOffset",h1_j.get("czpy"));//近光偏移（垂直），单位：HmainLamp
			zwyg.put("nearLightHOffset",h1_j.get("sppc"));//近光偏移（水平），单位：mm/10mmainLamp
			//zwyg.put("evaluate",param.get(""));//判定，见附录7.11detectRecord
			
		}
		
		if(!StringUtils.isEmpty(param.get("h1_y"))) {
			Map h1_y = (Map)param.get("h1_y");
			
			
			zwyg.put("nearLightLampHight",h1_y.get("dg"));//灯高（近光），单位：mmmainLamp
			zwyg.put("farLightStrong",h1_y.get("gq"));//远光光强，单位：cdmainLamp
			zwyg.put("farLightVOffset",h1_y.get("czpy"));//远光偏移（垂直），单位：HmainLamp
			zwyg.put("farLightHOffset",h1_y.get("sppc"));//远光偏移（水平），单位：mm/10mmainLamp
		}
		mainLamp.add(zwyg);
		
	
//		if(!StringUtils.isEmpty(param.get("h2_j"))) {
//			JSONObject znyg = new JSONObject();
//			znyg.put("lampType","2");//灯类型，见附录7.12
//			Map h2_j = (Map)param.get("h2_j");
//						
//			znyg.put("farLightLampHight",h2_j.get("dg"));//灯高（远光），单位：mmmainLamp			
//			
//			znyg.put("nearLightVOffset",h2_j.get("czpy"));//近光偏移（垂直），单位：HmainLamp
//			znyg.put("nearLightHOffset",h2_j.get("sppc"));//近光偏移（水平），单位：mm/10mmainLamp
//		}
//		if(!StringUtils.isEmpty(param.get("h2_y"))) {
//			Map h2_y = (Map)param.get("h2_y");
//			
//			znyg.put("nearLightLampHight",h2_y.get("dg"));//灯高（近光），单位：mmmainLamp
//			znyg.put("farLightStrong",h2_y.get("gq"));//远光光强，单位：cdmainLamp
//			znyg.put("farLightVOffset",h2_y.get("czpy"));//远光偏移（垂直），单位：HmainLamp
//			znyg.put("farLightHOffset",h2_y.get("sppc"));//远光偏移（水平），单位：mm/10mmainLamp
//		}
//		mainLamp.add(znyg);
//		
//		JSONObject ywyg = new JSONObject();	
//		ywyg.put("lampType","3");//灯类型，见附录7.12
//		if(!StringUtils.isEmpty(param.get("h3_j"))) {
//			Map h3_j = (Map)param.get("h3_j");
//			
//			ywyg.put("farLightLampHight",h3_j.get("dg"));//灯高（远光），单位：mmmainLamp			
//			
//			ywyg.put("nearLightVOffset",h3_j.get("czpy"));//近光偏移（垂直），单位：HmainLamp
//			ywyg.put("nearLightHOffset",h3_j.get("sppc"));//近光偏移（水平），单位：mm/10mmainLamp
//		}
//		if(!StringUtils.isEmpty(param.get("h3_y"))) {
//			Map h3_y = (Map)param.get("h3_y");
//			
//			ywyg.put("nearLightLampHight",h3_y.get("dg"));//灯高（近光），单位：mmmainLamp
//			ywyg.put("farLightStrong",h3_y.get("gq"));//远光光强，单位：cdmainLamp
//			ywyg.put("farLightVOffset",h3_y.get("czpy"));//远光偏移（垂直），单位：HmainLamp
//			ywyg.put("farLightHOffset",h3_y.get("sppc"));//远光偏移（水平），单位：mm/10mmainLamp
//		}
//		mainLamp.add(ywyg);
		
		JSONObject ynyg = new JSONObject();	
		ynyg.put("lampType","4");//灯类型，见附录7.12
		if(!StringUtils.isEmpty(param.get("h4_j"))) {
			Map h4_j = (Map)param.get("h4_j");
			
			ynyg.put("farLightLampHight",h4_j.get("dg"));//灯高（远光），单位：mmmainLamp			
			
			ynyg.put("nearLightVOffset",h4_j.get("czpy"));//近光偏移（垂直），单位：HmainLamp
			ynyg.put("nearLightHOffset",h4_j.get("sppc"));//近光偏移（水平），单位：mm/10mmainLamp
		}
		if(!StringUtils.isEmpty(param.get("h4_y"))) {
			Map h4_y = (Map)param.get("h4_y");
			
			ynyg.put("nearLightLampHight",h4_y.get("dg"));//灯高（近光），单位：mmmainLamp
			ynyg.put("farLightStrong",h4_y.get("gq"));//远光光强，单位：cdmainLamp
			ynyg.put("farLightVOffset",h4_y.get("czpy"));//远光偏移（垂直），单位：HmainLamp
			ynyg.put("farLightHOffset",h4_y.get("sppc"));//远光偏移（水平），单位：mm/10mmainLamp
		}	
		mainLamp.add(ynyg);	
		
//		jObject.put("singleItem",param.get(""));//单项检测singleItem
//		jObject.put("itemCode",param.get(""));//单项检测项目编码,见附录7.13
//		jObject.put("image",param.get(""));//图片资料image
//		jObject.put("imageUrl",param.get(""));//图片链接地址image
//		jObject.put("imageType",param.get(""));//检验工位照片类型,见附录7.14
//		jObject.put("video",param.get(""));//检验工位视频video
//		jObject.put("videoUrl",param.get(""));//视频链接地址video
//		jObject.put("videoType",param.get(""));//检验工位视频代码，值域见附录7.15
//		jObject.put("detectReport",param.get(""));//检验报告单detectReport
//		jObject.put("detectResult",param.get(""));//检验结论：一级、二级、三级或合格、不合格detectReport
//		jObject.put("note",param.get(""));//备注detectReport
//		jObject.put("tractorInfo",param.get(""));//单车（牵引车）基本信息tractorInfo
//		jObject.put("vehicleNo",param.get(""));//号牌号码tractorInfo
//		jObject.put("vinNo",param.get(""));//车辆识别代码tractorInfo
//		jObject.put("engineNo",param.get(""));//发动机号码tractorInfo
//		jObject.put("client",param.get(""));//委托人tractorInfo
//		jObject.put("vehicleBrandModel",param.get(""));//厂牌型号，参照JT/T697.7tractorInfo
//		jObject.put("administrativeAera",param.get(""));//行政区划代码，参照GB/T2260tractorInfo
//		jObject.put("vehicleType",param.get(""));//车辆类型：参照GA24.4tractorInfo
//		jObject.put("transCertificateCode",param.get(""));//道路运输证号tractorInfo
//		jObject.put("registDate",param.get(""));//注册登记日期，车辆行驶证初领日期:YYYYMMDDtractorInfo
//		jObject.put("productionDate",param.get(""));//出厂年月:YYYYMMDDtractorInfo
//		jObject.put("driveLicense",param.get(""));//机动车行驶证号tractorInfo
//		jObject.put("vehicleBodyColor",param.get(""));//车身颜色,见附录7.2detectReport
//		jObject.put("trailerInfo",param.get(""));//挂车基本信息trailerInfo
//		jObject.put("vehicleNo",param.get(""));//号牌号码trailerInfo
//		jObject.put("client",param.get(""));//委托人trailerInfo
//		jObject.put("vehicleType",param.get(""));//车辆类型：参照GA24.4trailerInfo
//		jObject.put("vehicleBrandModel",param.get(""));//厂牌型号，参照JT/T697.7trailerInfo
//		jObject.put("registDate",param.get(""));//注册登记日期:YYYYMMDDtrailerInfo
//		jObject.put("productionDate",param.get(""));//出厂日期:YYYYMMDDtrailerInfo
//		jObject.put("vinNo",param.get(""));//车辆识别代码trailerInfo
//		jObject.put("driveLicense",param.get(""));//车辆行驶证号detectReport
//		jObject.put("manualTestResult",param.get(""));//人工检验结果manualTestResul
//		jObject.put("detectCls",param.get(""));//人工检验项目代码，见附录7.16t
//		jObject.put("evaluate",param.get(""));//判定：见附录7.11manualTestResul
//		jObject.put("unqualifiedItem",param.get(""));//不符合项目t
//		jObject.put("performanceItem",param.get(""));//性能检测manualTestResul
//		jObject.put("itemCode",param.get(""));//性能检测项目编码，见附7.17t
//		jObject.put("detectData",param.get(""));//检测数据，各项格式参照GB18565detectReport
//		jObject.put("standardValue",param.get(""));//标准限值，各项格式参照GB18565performanceItem
//		jObject.put("evaluate",param.get(""));//判定：见附录7.11performanceItem
		JSONObject detectReport = new JSONObject();
		
		if(param.get("zjjl")!=null) {
			String zjjl=(String)param.get("zjjl");
			if(zjjl.equals("壹级车")) {
				zjjl="一级";
			}else if(zjjl.equals("贰级车")) {
				zjjl="二级";
			}
			detectReport.put("detectResult", zjjl);
		}
		
		
		
		JSONObject trailerInfo = new JSONObject();
		trailerInfo.put("vehicleNo", "无");
		
		detectReport.put("trailerInfo", trailerInfo);
		
		
		JSONObject  tractorInfo = new JSONObject();
		
		tractorInfo.put("vehicleNo", param.get("hphm"));
		tractorInfo.put("vinNo", param.get("clsbdh"));
		tractorInfo.put("engineNo", param.get("fdjh"));
		tractorInfo.put("client", param.get("syr"));
		
		tractorInfo.put("client", param.get("syr"));
		
		tractorInfo.put("vehicleBrandModel",param.get("clpp1")+"/"+param.get("clxh"));
		tractorInfo.put("administrativeAera", "");
		
		tractorInfo.put("vehicleType", param.get("cllx"));
		tractorInfo.put("transCertificateCode", param.get("dlyszh"));
		
		
		
		
		
		if(param.get("ccdjrq")!=null) {
			long t =(long) param.get("ccdjrq");
			Date ccdjrq = new Date(t);
			tractorInfo.put("registDate", sdf.format(ccdjrq));
			
		}
		
		if(param.get("ccrq")!=null) {
			long t =(long) param.get("ccrq");
			Date ccrq = new Date(t);
			tractorInfo.put("productionDate", sdf.format(ccrq));
			
		}
		
		tractorInfo.put("driveLicense", "");
		
		
		tractorInfo.put("vehicleBodyColor", param.get("cpys"));
		detectReport.put("tractorInfo", tractorInfo);
		detectReport.put("note", "123");
		
		
		//detectReport.put("manualTestResult", );
		
		JSONArray manualTestResult = new JSONArray();
		
		for(int i=1;i<=6;i++) {
			JSONObject mt =new JSONObject();
			mt.put("detectCls", String.valueOf(i));
			mt.put("evaluate", "0");
			mt.put("unqualifiedItem", "无");
			manualTestResult.add(mt);
		}
		detectReport.put("manualTestResult", manualTestResult);
		
		
		List reports = (List)(param.get("reports"));
		
		String jsonTemp = JSON.toJSONString(param.get("reports"));
		
		JSONArray reportsArray =JSONArray.parseArray(jsonTemp);
		
		System.out.println("reports:="+reportsArray);
		
		JSONArray performanceItem =new JSONArray();
		
		for(int i=0;i<reportsArray.size();i++) {
			JSONObject jo = reportsArray.getJSONObject(i);
			System.out.println(jo.getString("yqjyxm").indexOf("驱动轮"));
			System.out.println(jo.getString("yqjyxm").indexOf("经济性"));
			System.out.println(jo.getString("yqjyxm").indexOf("喇叭"));
			if(jo.getString("yqjyxm").indexOf("驱动轮")>=0) {
				JSONObject item =new JSONObject();
				item.put("itemCode", "power");
				item.put("detectData", jo.getString("yqjyjg"));
				item.put("standardValue", jo.getString("yqbzxz"));
				item.put("evaluate", getpd(jo.getString("yqjgpd")));
				performanceItem.add(item);
			}
			if(jo.getString("yqjyxm").indexOf("经济性")>=0) {
				JSONObject item =new JSONObject();
				item.put("itemCode", "economy");
				item.put("detectData", jo.getString("yqjyjg"));
				item.put("standardValue", jo.getString("yqbzxz"));
				item.put("evaluate", getpd(jo.getString("yqjgpd")));
				performanceItem.add(item);
			}
			if(jo.getString("yqjyxm").indexOf("喇叭")>=0) {
				JSONObject item =new JSONObject();
				item.put("itemCode", "horn_sound_pressure_level");
				item.put("detectData", jo.getString("yqjyjg"));
				item.put("standardValue", jo.getString("yqbzxz"));
				item.put("evaluate", getpd(jo.getString("yqjgpd")));
				performanceItem.add(item);
			}
		}
		
		if(param.get("s1")!=null) {
			Map s1 = (Map) param.get("s1");
			JSONObject item =new JSONObject();
			item.put("itemCode", "speed_meter");
			item.put("detectData", s1.get("speed").toString());
			item.put("evaluate", getpd(s1.get("sdpd").toString()));
			item.put("standardValue", "32.8~40");
			performanceItem.add(item);
		}
		
		detectReport.put("performanceItem", performanceItem);
		
		
		
		JSONArray singleItem = new JSONArray();
		
		if(param.get("s1")!=null) {
			Map s1 = (Map) param.get("s1");
			JSONObject item =new JSONObject();
			item.put("itemCode", "measured_speed");
			item.put("detectResult", s1.get("speed").toString());
			item.put("evaluate", getpd(s1.get("sdpd").toString()));
			singleItem.add(item);
			JSONObject item1 =new JSONObject();
			item1.put("itemCode", "speed_meter");
			item1.put("detectResult", s1.get("speed").toString());
			item1.put("evaluate", getpd(s1.get("sdpd").toString()));
			
			singleItem.add(item1);
			
		}
		
		if(param.get("dlx")!=null) {
			Map dlx = (Map) param.get("dlx");
			JSONObject item =new JSONObject();
			item.put("itemCode", "constant_speed_fuel_consumption_per_hundred_kilometers");
			item.put("detectResult", dlx.get("yh_scz").toString());
			item.put("evaluate", getpd(dlx.get("yh_pd").toString()));
			singleItem.add(item);
		}
		
		if(param.get("sjj")!=null) {
			Map sjj = (Map) param.get("sjj");
			JSONObject item =new JSONObject();
			item.put("itemCode", "horn_sound");
			item.put("detectResult", sjj.get("fb").toString());
			item.put("evaluate", getpd(sjj.get("zpd").toString()));
			
			JSONObject item1 =new JSONObject();
			item1.put("itemCode", "horn_sound_pressure_level");
			item1.put("detectResult", sjj.get("fb").toString());
			item1.put("evaluate", getpd(sjj.get("zpd").toString()));
			
			singleItem.add(item);
			singleItem.add(item1);
		}
		
		detectRecord.put("singleItem", singleItem);
		
		//detectRecord.put(key, value)
		
		
		//detectRecord.put("mainLamp", mainLamp);
		detectRecord.put("suspension", suspension);	
	//	detectRecord.put("emission", emission);
		//detectRecord.put("brake", brake);
		//
		jObject.put("detectRecord", detectRecord);
		
		jObject.put("detectReport", detectReport);
		
		jObject.put("image", new JSONArray());
		jObject.put("video", new JSONArray());
		
		headers.setContentType(MediaType.APPLICATION_JSON); 
		Map<String, String> requestBody = new HashMap();
		requestBody.put("CompanyId", companyId);
		requestBody.put("Source", source);
		requestBody.put("IPCType", "shareDetectInfo");
		requestBody.put("IPCType.value", jObject.toJSONString());
		logger.info("param:"+requestBody);
		HttpEntity<Map<String, String>> request = new HttpEntity<Map<String, String>>(requestBody, headers);
	    String result =  restTemplate.postForObject(url+"/restapi/detecting/put_data;token="+token, request, String.class);	
	    logger.info("result:"+result);
		return result;		
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

	
	private String getpd(String pd) {
		
		if("1级".equals(pd)) {
			return "1";
		}else if("2级".equals(pd)) {
			return "2";
		}else if("合格".equals(pd)) {
			return "0";
		}else if("不合格".equals(pd)) {
			return "-1";
		}else if("○".equals(pd)) {
			return "0";
		}else if("X".equals(pd)||"x".equals(pd)) {
			return "-1";
		}else if("1".equals(pd)) {
			return "0";
		}else if("2".equals(pd)) {
			return "-1";
		}
		return pd;
	}
	
	
	private String getJcxdh(String jcxdh) {
		if("1".equals(jcxdh)) {
			return "A";
		}else if("2".equals(jcxdh)) {
			return "B";
		}
		else if("3".equals(jcxdh)) {
			return "C";
		}else if("4".equals(jcxdh)) {
			return "D";
		}else if("5".equals(jcxdh)) {
			return "E";
		}
		
		return jcxdh;
	}
	
	
	
	public void uploadIamge(Map param,String path,String imgType,String token,JSONArray bpsArr,String zhqz) {
		
		String imageType =getImageType(imgType);
		
		if(imageType==null) {
			return ;
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject jObject = new JSONObject();
		jObject.put("dsId", companyId);
		jObject.put("vehicleNo", param.get("hphm"));
		jObject.put("detectSn",param.get("zjlsh"));//检测流水号,见附录7.9detectRecord
		jObject.put("plateColorCode", getParamByTypeAndName(bpsArr,zhqz+"cpys",String.valueOf(param.get("cpys"))));
		jObject.put("vinNo", param.get("clsbdh"));
		jObject.put("imageType", imageType);
		
		
		String base64Image = Base64Utils.ImageToBase64ByLocal(path);
		jObject.put("base64Image", base64Image);
		
		//logger.info("base64Image="+base64Image);
		
		JSONObject requestBody = new JSONObject();
		requestBody.put("CompanyId", companyId);
		requestBody.put("Source", source);
		 
		String method="shareReportImage";
		
		String root = "reportImage";
		
		if(imgType.equals("zd")||imgType.equals("dg")||imgType.equals("dl")) {
			method="sharePrintImage";
			root="printImage";
		}
		System.out.println(method);
		requestBody.put("IPCType", method);
		requestBody.put("IPCType.value","{\""+root+"\":["+ jObject.toJSONString()+"]\r\n}");
		
		//logger.info(requestBody);
		HttpEntity<JSONObject> request = new HttpEntity<JSONObject>(requestBody, headers);
	    String result =  restTemplate.postForObject(url+"/restapi/detecting/put_data;token="+token, request, String.class);
	    
	    System.out.println(result);
	    
		
	}
	
	
	public String getImageType(String type) {
		
		if(type.equals("zd")){
			return "1";
		}else if(type.equals("dg")) {
			return "2";
		}else if(type.equals("dl")) {
			return "3";
		}else if(type.equals("xsz")) {
			return "3";
		}else if(type.equals("ajbg")) {
			return "1";
		}else if(type.equals("jybzzp")) {
			return "2";
		}
		return null;
		
	}
	
	

}
