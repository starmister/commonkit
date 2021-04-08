//package com.songwy.utils;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.plg.base.common.util.Assert;
//import com.plg.base.common.util.PropertiesUtil;
//import com.plg.base.common.util.StringUtils;
//import com.plg.qiyeui.provider.bean.bo.GetApproveContractListBO;
//import com.plg.qiyeui.provider.bean.bo.GetApproveInfoBo;
//import com.plg.qiyeui.provider.bean.bo.GetContractBO;
//import com.plg.qiyeui.provider.bean.dto.appRove.ApproveInfoDTO;
//import com.plg.qiyeui.provider.bean.dto.appRove.GetApproveContractListDTO;
//import com.plg.qiyeui.provider.bean.dto.debtCommit.GetDebtContractListDTO;
//import com.plg.qiyeui.provider.bean.dto.loanExtend.ExtContractListDTO;
//import com.plg.qiyeui.provider.bean.dto.outerApi.LoanByLoanUuidsDTO;
//import com.plg.qiyeui.provider.bean.dto.phpLoan.ContractListDTO;
//import com.plg.qiyeui.provider.bean.dto.phpLoan.PhpLoanContractDTO;
//import com.plg.qiyeui.provider.bean.dto.qiyeFriend.GetContractListDTO;
//import com.plg.qiyeui.provider.bean.model.Product;
//import com.plg.qiyeui.provider.common.constants.ContractSceneConstants;
//import com.plg.qiyeui.provider.common.utils.CommonUtils;
//import com.plg.qiyeui.provider.dao.ProductDao;
//import com.plg.qiyeui.provider.service.remote.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//
//@Service
//public class GetFriendWishPayLendStaffContractListStrategy implements IGetContactListStrategy{
//
//    private static final String CONTRACT_ROUTER_URL = PropertiesUtil.getStringValue("outerapi.host");
//    @Autowired
//    private ProductDao productDao;
//    @Autowired
//    private PhpLoanRemoteService phpLoanRemoteService;
//
//    @Autowired
//    OpuiRemoteSerivce opuiRemoteSerivce;
//    @Autowired
//    private QiyeLoanRemoteService qiyeLoanRemoteService;
//    @Autowired
//    private OuterApiRemoteService outerApiRemoteService;
//
//    @Autowired
//    private DebtCommitRemoteService debtCommitRemoteService;
//
//    @Autowired
//    private AppRoveRemoteService appRoveRemoteService;
//    @Autowired
//    private LoanExtendRemoteService loanExtendRemoteService;
//    @Override
//    public List<GetContractListDTO.GetContract> getContractList(GetContractBO getContractBO) throws Exception {
//        String memberID = getContractBO.getMemberID();
//        String productID = getContractBO.getProductID();
//        String relativePayLandryID = getContractBO.getRelativePayLandryID()==null ? "0":getContractBO.getRelativePayLandryID();
//        List<GetContractListDTO.GetContract> getContractDTOList = new ArrayList();
//        //查询product表获取company_id
//        Product product = productDao.selectByProductId(productID);
//        Assert.notNull(product,"该产品不存在！");
//        String orderID = relativePayLandryID;
//        //phploan系统等一系列协议
//        TreeMap<String,String> contractParams = new TreeMap<>();
//        contractParams.put("memberID",memberID);
//        contractParams.put("productID",productID);
//        contractParams.put("orderID",orderID);
//        contractParams.put("tradeType",0+"");
//        List<ContractListDTO> contractListDTOList = phpLoanRemoteService.getContractList(contractParams);
//        if(contractListDTOList!=null){
//            for(ContractListDTO contractListDTO: contractListDTOList){
//                PhpLoanContractDTO phpLoanContractDTO = phpLoanRemoteService.getContract(memberID,contractListDTO.getContractNumber(),memberID,contractListDTO.getContractRole());
//                GetContractListDTO.GetContract getContractDTO = new GetContractListDTO.GetContract();
//                //包装参数
//                phpLoanContractDTO.getTemplateData().put("templateName",phpLoanContractDTO.getTemplateName());
//                phpLoanContractDTO.getTemplateData().put("templateVersion",phpLoanContractDTO.getTemplateVersion());
//                phpLoanContractDTO.getTemplateData().put("templateCode",phpLoanContractDTO.getTemplateName());
//                phpLoanContractDTO.getTemplateData().put("scene", "app-view");
//                getContractDTO.setTitle(phpLoanContractDTO.getTitle());
//                getContractDTO.setSubTitle("");
//                getContractDTO.setContractNumber(phpLoanContractDTO.getContractNumber()==null?"":phpLoanContractDTO.getContractNumber());
//                getContractDTO.setTime(phpLoanContractDTO.getTemplateVersion()==null?"":phpLoanContractDTO.getTemplateVersion());
//                getContractDTO.setContentUrl(CONTRACT_ROUTER_URL +"/"+ ContractSceneConstants.PREVIEW_URL + "?" + CommonUtils.httpBuildQuery(phpLoanContractDTO.getTemplateData()));
//                getContractDTOList.add(getContractDTO);
//            }
//        }
//
//        //展期协议
//        JSONArray extUuidDTO = qiyeLoanRemoteService.getExtInfoByLoanUUID(orderID,memberID,1);
//        // 是否是新展期
//        Boolean hasNext = getNewExtForSourceProductID(productID);
//        ExtContractListDTO extContractListDTO = null;
//        if(hasNext && extUuidDTO!=null&&extUuidDTO.size()>0){
//            for(int i=0;i<extUuidDTO.size();i++){
//                if(StringUtils.isNotEmpty(extUuidDTO.getJSONObject(i).getString("ext_uuid"))){
//                    extContractListDTO = loanExtendRemoteService.fQueryComExtContract(memberID,extUuidDTO.getJSONObject(i).getString("ext_uuid"),0);
//                }
//
//            }
//
//        }else if(extUuidDTO!=null&&extUuidDTO.size()>0){
//            for(int i=0;i<extUuidDTO.size();i++){
//                if(StringUtils.isNotEmpty(extUuidDTO.getJSONObject(i).getString("ext_uuid"))){
//                    extContractListDTO = loanExtendRemoteService.queryComExtContract(memberID,extUuidDTO.getJSONObject(i).getString("ext_uuid"),0);
//                }
//
//            }
//        }
//        if(extContractListDTO!=null&&extContractListDTO.getContractList().size()>0){
//            for(ExtContractListDTO.Contract contract : extContractListDTO.getContractList()){
//                contract.getTemplateData().put("templateCode",contract.getTemplateName());
//                contract.getTemplateData().put("scene","app-view");
//                GetContractListDTO.GetContract getContractDTO = new GetContractListDTO.GetContract();
//                getContractDTO.setTitle(contract.getName());
//                getContractDTO.setSubTitle("");
//                getContractDTO.setContractNumber(contract.getContractNumber()==null?"":contract.getContractNumber());
//                getContractDTO.setTime(contract.getTemplateVersion()==null?"":contract.getTemplateVersion());
//                getContractDTO.setContentUrl(CONTRACT_ROUTER_URL +"/"+ ContractSceneConstants.PREVIEW_URL + "?" + CommonUtils.httpBuildQuery(contract.getTemplateData()));
//                getContractDTOList.add(getContractDTO);
//            }
//        }
//
//        //销账协议
//        JSONObject getLoan = outerApiRemoteService.getLoanInfoJSONObjectByLoanUuids(Arrays.asList(orderID));
//        if(getLoan!=null){
//            LoanByLoanUuidsDTO transProductInfoDTO = JSONObject.parseObject(JSON.toJSONString(getLoan.get(orderID)), LoanByLoanUuidsDTO.class);
//            String debtUuid = transProductInfoDTO.getCurrentDebtUuid();
//            GetDebtContractListDTO getDebtContractListDTO = debtCommitRemoteService.getContractListById(debtUuid,"1",memberID);
//            if(getDebtContractListDTO!=null&&getDebtContractListDTO.getGetDebtContractList().size()>0){
//                for(GetDebtContractListDTO.GetDebtContract getDebtContract : getDebtContractListDTO.getGetDebtContractList()){
//                    JSONObject jsonObject = debtCommitRemoteService.getContractInfoByCancelId(getDebtContract.getCancelUuid(),null);
//                    if(jsonObject!=null){
//                        GetContractListDTO.GetContract getContractDTO = new GetContractListDTO.GetContract();
//                        //包装参数
//                        Map<String ,String> data = jsonObject.toJavaObject(Map.class);
//                        data.put("templateCode",jsonObject.getString("contract_code")==null?"":jsonObject.getString("contract_code"));
//                        data.put("scene","app-view");
//                        getContractDTO.setTitle(getDebtContract.getName());
//                        getContractDTO.setSubTitle("");
//                        getContractDTO.setContractNumber(jsonObject.getString("contract_code")==null?"":jsonObject.getString("contract_code"));
//                        getContractDTO.setTime(getDebtContract.getCreateTime());
//                        getContractDTO.setContentUrl(CONTRACT_ROUTER_URL+"/" + ContractSceneConstants.PREVIEW_URL + "?" + CommonUtils.httpBuildQuery(data));
//                        getContractDTOList.add(getContractDTO);
//                    }
//
//                }
//            }
//        }
//        //还款保障协议
//        GetApproveInfoBo getApproveInfoBo = new GetApproveInfoBo();
//        getApproveInfoBo.setQueryType(1);
//        getApproveInfoBo.setLoanUuid(getContractBO.getRelativePayLandryID());
//        List<ApproveInfoDTO.InfoItem> infoItemList = appRoveRemoteService.getRoveInfoLoanType(getApproveInfoBo);
//        GetApproveContractListBO getApproveContractListBO = new GetApproveContractListBO();
//        getApproveContractListBO.setDebtorID(memberID);
//        getApproveContractListBO.setRole("1");
//        getApproveContractListBO.setBizID(infoItemList!=null?infoItemList.get(0).getBizInfo().getBizId():"");
//        getApproveContractListBO.setBizType("0");
//        getApproveContractListBO.setPreviewType("1");
//        List<GetApproveContractListDTO.GetApproveContract> getApproveContractList = appRoveRemoteService.getApproveCreateContractList(getApproveContractListBO);
//        if(getApproveContractList!=null||getApproveContractList.size()>0){
//            for(GetApproveContractListDTO.GetApproveContract getApproveContract : getApproveContractList){
//                GetContractListDTO.GetContract getContractDTO = new GetContractListDTO.GetContract();
//                //包装参数
//                getApproveContract.getTemplateData().put("templateCode",getApproveContract.getTemplateId());
//                getApproveContract.getTemplateData().put("scene","app-view");
//                getContractDTO.setTitle(getApproveContract.getName());
//                getContractDTO.setSubTitle("");
//                getContractDTO.setContractNumber(getApproveContract.getTemplateId());
//                getContractDTO.setTime(getApproveContract.getCreateTime());
//                getContractDTO.setContentUrl(CONTRACT_ROUTER_URL +"/"+ ContractSceneConstants.PREVIEW_URL + "?" + CommonUtils.httpBuildQuery(getApproveContract.getTemplateData()));
//                getContractDTOList.add(getContractDTO);
//            }
//        }
//        return getContractDTOList;
//    }
//
//    //根据原始标ID，获取是否是新展期
//    private Boolean getNewExtForSourceProductID(String sourceProductID){
//        if(StringUtils.isEmpty(sourceProductID)){
//            return false;
//        }
//        Product product = productDao.selectByProductId(sourceProductID);
//        if(product==null){
//            return false;
//        }
//        if(product.getIsExtCombineProduct()==1||product.getIsExtSubProduct()==1){
//            return true;
//        }
//        return false;
//    }
//}
