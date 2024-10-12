package com.vn.sbit.idenfity_service.configuration;

import com.vn.sbit.idenfity_service.EnumRoles.Role;
import com.vn.sbit.idenfity_service.entity.User;
import com.vn.sbit.idenfity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
@RequiredArgsConstructor // auto autowired
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j//để sử dụng log
public class ApplicationInitConfig {
    //@RequiredArgsConstructor
    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
           if(userRepository.findByUserName("admin").isEmpty()){
               var roles=new HashSet<String>(); // vì property in User Set<String>
               roles.add(Role.ADMIN.name());
               User user = User.builder()
                       .userName("admin")
                       .passWord(passwordEncoder.encode("admin123"))
                       .roles(roles)
                       .build();

               userRepository.save(user);
               log.warn("Admin user has been created with default pass word: admin123 ,please change it");
           }
        };
    }
}
