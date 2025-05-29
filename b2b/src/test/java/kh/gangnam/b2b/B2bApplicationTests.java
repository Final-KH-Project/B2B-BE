package kh.gangnam.b2b;

import kh.gangnam.b2b.entity.auth.Employee;
import kh.gangnam.b2b.repository.AlarmRepository;
import kh.gangnam.b2b.repository.EmployeeRepository;
import kh.gangnam.b2b.service.AlarmService;
import kh.gangnam.b2b.service.ServiceImpl.AlarmServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class B2bApplicationTests {

    @Autowired
    AlarmService alarmService;
    @Autowired
    AlarmRepository alarmRepository;
    //@Test
    void 알람등록테스트(){
        alarmService.save(5l);
    }

    //@Test
    void 읽지않은알람(){
        int result=alarmRepository.countByEmployee_employeeIdAndIsReadFalse(4l);
        System.out.println(">>>>:"+result);
    }
}
