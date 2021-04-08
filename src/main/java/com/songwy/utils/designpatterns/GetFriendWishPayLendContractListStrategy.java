//package com.songwy.utils;
//
//import com.alibaba.fastjson.JSONObject;
//import com.plg.base.common.logs.CommonLogger;
//import com.plg.base.common.logs.CommonLoggerFactory;
//import com.plg.base.common.util.PropertiesUtil;
//import com.plg.qiyeui.provider.bean.bo.GetContractBO;
//import com.plg.qiyeui.provider.bean.dto.phpLoan.PreviewContractDTO;
//import com.plg.qiyeui.provider.bean.dto.phpQyPass.CompanyDetailInfoDTO;
//import com.plg.qiyeui.provider.bean.dto.qiyeFriend.GetContractListDTO;
//import com.plg.qiyeui.provider.bean.dto.qiyeFriend.GetFriendContractDTO;
//import com.plg.qiyeui.provider.bean.model.Product;
//import com.plg.qiyeui.provider.common.constants.ContractSceneConstants;
//import com.plg.qiyeui.provider.common.myenum.service.PhpOpuiDataIdEnum;
//import com.plg.qiyeui.provider.common.utils.CommonUtils;
//import com.plg.qiyeui.provider.common.utils.DateUtil;
//import com.plg.qiyeui.provider.dao.ProductDao;
//import com.plg.qiyeui.provider.service.remote.JieTiaoRemoteService;
//import com.plg.qiyeui.provider.service.remote.OpuiRemoteSerivce;
//import com.plg.qiyeui.provider.service.remote.PhpLoanRemoteService;
//import com.plg.qiyeui.provider.service.remote.PhpQyPassRemoteService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//import static com.plg.qiyeui.provider.service.remote.JieTiaoRemoteService.JIETIAO_BUSINESSTYPE;
//@Service
//public class GetFriendWishPayLendContractListStrategy implements IGetContactListStrategy {
//    private static final CommonLogger log = CommonLoggerFactory.getLogger(GetFriendWishPayLendContractListStrategy.class);
//    private static final String CONTRACT_ROUTER_URL = PropertiesUtil.getStringValue("outerapi.host");
//    @Autowired
//    private ProductDao productDao;
//    @Autowired
//    private PhpLoanRemoteService phpLoanRemoteService;
//    @Autowired
//    private JieTiaoRemoteService jieTiaoRemoteService;
//    @Autowired
//    private PhpQyPassRemoteService phpQyPassRemoteService;
//    @Autowired
//    OpuiRemoteSerivce opuiRemoteSerivce;
//    @Override
//    public List<GetContractListDTO.GetContract> getContractList(GetContractBO getContractBO) throws Exception {
//
//        String memberID = getContractBO.getMemberID();
//        List<GetContractListDTO.GetContract> getContractDTOList = new ArrayList();
//        TreeMap<String, String> params = new TreeMap<>();
//        params.put("type","qyinnerLend");
//        params.put("memberID",getContractBO.getUserID());
//        params.put("productID",getContractBO.getProductID());
//        params.put("loanAmount",getContractBO.getAmount());
//        //查询product表获取company_id
//        Product product = productDao.selectByProductId(getContractBO.getProductID());
//        List<PreviewContractDTO> previewContractList = phpLoanRemoteService.previewContractList(params);
//        if(previewContractList!=null){
//            for(PreviewContractDTO previewContractDTO : previewContractList){
//                GetContractListDTO.GetContract getContractDTO = new GetContractListDTO.GetContract();
//                getContractDTO.setTitle(previewContractDTO.getName());
//                getContractDTO.setSubTitle("");
//                getContractDTO.setTime(previewContractDTO.getTemplateVersion());
//                getContractDTO.setContractNumber("");
//                previewContractDTO.getTemplateData().put("templateCode", previewContractDTO.getTemplateName());
//                getContractDTO.setContentUrl(CONTRACT_ROUTER_URL+"/" + ContractSceneConstants.PREVIEW_URL + "?" + CommonUtils.httpBuildQuery(previewContractDTO.getTemplateData()));
//                getContractDTOList.add(getContractDTO);
//            }
//        }
//        //借条系统 《亲友打借条协议》《亲友投资工具授权确认书》《风险告知提示确认书（债务人）》《还款保障协议》
//
//        if(product!=null){
//            GetFriendContractDTO getFriendContractDTO = jieTiaoRemoteService.getContractPreviewList(getContractBO,JIETIAO_BUSINESSTYPE, product.getEndTime());
//            if(getFriendContractDTO!=null){
//                for(GetFriendContractDTO.Contract contract : getFriendContractDTO.getAgreementList()){
//                    GetContractListDTO.GetContract getContractDTO = new GetContractListDTO.GetContract();
//                    getContractDTO.setTitle(contract.getTitle());
//                    getContractDTO.setSubTitle(contract.getSubTitle());
//                    getContractDTO.setTime(contract.getTime());
//                    getContractDTO.setContractNumber(contract.getContractNumber());
//                    getContractDTO.setContentUrl(contract.getContentUrl());
//                    getContractDTOList.add(getContractDTO);
//                }
//            }
//            //获取《员工投资工具授权确认书》 企业版
//            //查询qiyepass接口，获取企业信息。
//            CompanyDetailInfoDTO companyDetailInfoDTO = phpQyPassRemoteService.getDetailInfo(memberID,product.getCompanyId(),0);
//
//            if(companyDetailInfoDTO!=null){
//                JSONObject jsonObject = opuiRemoteSerivce.dataGetContract(memberID, PhpOpuiDataIdEnum.ID_CONTRACT_STAFF_BORROW_FEES.key,new Date().toString());
//                log.info("获取《员工投资工具授权确认书》 企业版 {}",jsonObject);
//                if(jsonObject!=null){
//                    GetContractListDTO.GetContract getContractDTO = new GetContractListDTO.GetContract();
//                    //包装参数
//                    Map<String ,String> data = new HashMap<>();
//                    data.put("jiafangName",companyDetailInfoDTO.getCompanyName());
//                    data.put("date", DateUtil.formatContractYmd(new Date()));
//                    data.put("templateCode",jsonObject.getString("templateCode")==null?"":jsonObject.getString("templateCode"));
//                    data.put("contractNo","");
//                    data.put("idNo",companyDetailInfoDTO.getOperating().getCompanyIdNo());
//                    data.put("scene","app-preview");
//
//                    getContractDTO.setTitle(jsonObject.getString("contractName")==null?"":jsonObject.getString("contractName"));
//                    getContractDTO.setSubTitle("");
//                    getContractDTO.setContractNumber("");
//                    getContractDTO.setTime("");
//                    getContractDTO.setContentUrl(CONTRACT_ROUTER_URL+"/" + ContractSceneConstants.PREVIEW_URL + "?" + CommonUtils.httpBuildQuery(data));
//                    getContractDTOList.add(getContractDTO);
//                }
//            }
//        }
//
//        return getContractDTOList;
//    }
//}
