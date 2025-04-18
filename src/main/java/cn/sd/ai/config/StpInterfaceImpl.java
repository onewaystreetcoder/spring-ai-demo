//package cn.sd.ai.config;
//
//import cn.dev33.satoken.stp.StpInterface;
//import cn.dev33.satoken.stp.StpUtil;
//import cn.hutool.core.collection.ListUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class StpInterfaceImpl implements StpInterface {
//
//    private static final List<String> PERMISSIONS = ListUtil.list(false, "chat", "embedding:add");
//
//    /**
//     * 返回一个账号所拥有的权限码集合
//     */
//    @Override
//    public List<String> getPermissionList(Object loginId, String loginType) {
//        if (loginType.equals(StpUtil.TYPE)) {
//            return PERMISSIONS;
//        }
//        return null;
//    }
//
//    /**
//     * 返回一个账号所拥有的角色标识集合
//     */
//    @Override
//    public List<String> getRoleList(Object loginId, String loginType) {
//        return null;
//    }
//}
