package com.hao.community;

import com.hao.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter() {
        String text= " 这里可以博※可以※嫖※娼※可以吸※毒可以开※票也可以※";
        text = sensitiveFilter.filter(text);
        System.out.println(Integer.parseInt("※"));
        System.out.println(text);
    }
}
