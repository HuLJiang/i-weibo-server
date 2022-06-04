package top.hwlljy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.hwlljy.model.pojo.UserRole;


public interface UserRoleRepository extends JpaRepository<UserRole, String> {
}
