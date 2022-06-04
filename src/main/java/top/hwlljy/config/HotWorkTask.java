package top.hwlljy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.hwlljy.model.pojo.UserWork;
import top.hwlljy.repository.WorkRepository;

import java.util.Calendar;
import java.util.List;

@Component
public class HotWorkTask {

    @Autowired
    private WorkRepository workRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void doTask() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-5);
        workRepository.updateHot(calendar.getTime());
        for(int i = 1;i < 3;i ++) {
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
