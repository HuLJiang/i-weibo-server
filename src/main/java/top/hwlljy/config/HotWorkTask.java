package top.hwlljy.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.hwlljy.model.pojo.UserWork;
import top.hwlljy.repository.WorkRepository;

import java.util.Calendar;
import java.util.List;

@Component
@Slf4j
public class HotWorkTask {

    @Autowired
    private WorkRepository workRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void doTask() {
        log.info("---------每日热度计算---------");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,- 6);
        workRepository.updateHot(calendar.getTime());
        for(int i = 1;i <= 5;i ++) {
            Calendar start = Calendar.getInstance();
            start.add(Calendar.DAY_OF_MONTH,-i - 1);
            Calendar end = Calendar.getInstance();
            end.add(Calendar.DAY_OF_MONTH,-i);
            List<UserWork> list = workRepository.getUserWorkBetween(start.getTime(),end.getTime());
            int finalI = i;
            list.forEach(item -> item.setHot(item.getHot() / finalI));
            workRepository.saveAll(list);
        }
    }
}
