package top.hwlljy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import top.hwlljy.model.pojo.Report;

public interface ReportRepository extends JpaRepository<Report, String> {
}
