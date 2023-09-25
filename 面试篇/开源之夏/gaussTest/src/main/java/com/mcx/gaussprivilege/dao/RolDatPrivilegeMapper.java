package com.mcx.gaussprivilege.dao;
import com.mcx.gaussprivilege.entity.RolDatPrivilege;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RolDatPrivilegeMapper {
//    @Select("SELECT b.rolname, a.datname, array_agg(a.pri_t) AS privileges" +
//            " FROM (SELECT datname, grantee, privilege_type AS pri_t FROM" +
//            " (SELECT datname, aclexplode(COALESCE(datacl, acldefault('d'::\"char\", datdba))).grantee AS grantee," +
//            " aclexplode(COALESCE(datacl, acldefault('d'::\"char\", datdba))).privilege_type AS privilege_type" +
//            " FROM pg_database WHERE datname NOT LIKE 'template%') subquery) a" +
//            " JOIN pg_roles b ON a.grantee = b.oid OR a.grantee = 0" +
//            " WHERE b.rolname NOT LIKE 'gs%' GROUP BY a.datname, b.rolname")
@Select("SELECT b.rolname, a.datname, string_agg(a.pri_t, ',') AS privileges FROM (SELECT datname, grantee, privilege_type AS pri_t FROM (SELECT datname, (aclexplode(COALESCE(datacl, acldefault('d'::\"char\", datdba)))).grantee AS grantee, (aclexplode(COALESCE(datacl, acldefault('d'::\"char\", datdba)))).privilege_type AS privilege_type FROM pg_database WHERE datname NOT LIKE 'template%') subquery) a JOIN pg_roles b ON a.grantee = b.oid OR a.grantee = 0 WHERE b.rolname NOT LIKE 'gs%' GROUP BY a.datname, b.rolname;")

List<RolDatPrivilege> getRolDatPrivileges();
}
