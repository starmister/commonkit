//package com.songwy.utils;
//
//import com.plg.qiyeui.provider.common.constants.ContractSceneConstants;
//import org.springframework.beans.BeansException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.stereotype.Component;
//
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class GetContractListStrategyFactory implements ApplicationContextAware {
//    private static Map<String,IGetContactListStrategy> getContactListStrategyMap;
//
//    public static IGetContactListStrategy getGetContactListStrategy(String businessType) {
//        return getContactListStrategyMap.get(businessType);
//    }
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        getContactListStrategyMap = new HashMap<>();
//        getContactListStrategyMap.put(ContractSceneConstants.FRIEND_WISH_PAY_LEND,applicationContext.getBean(GetFriendWishPayLendContractListStrategy.class));
//        getContactListStrategyMap.put(ContractSceneConstants.FRIEND_WISH_PAY_LEND_STAFF,applicationContext.getBean(GetFriendWishPayLendStaffContractListStrategy.class));
//
//    }
//
//
//}
