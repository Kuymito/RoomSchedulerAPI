package org.example.roomschedulerapi.classroomscheduler.config;

 import jakarta.annotation.PostConstruct;
 import org.apache.ibatis.session.SqlSessionFactory;
 import org.example.roomschedulerapi.classroomscheduler.typehandler.StringListTypeHandler;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 @Configuration
 public class MyBatisConfig {
     @Autowired
     private SqlSessionFactory sqlSessionFactory;

     @PostConstruct
     public void registerTypeHandlers() {
         sqlSessionFactory.getConfiguration().getTypeHandlerRegistry().register(StringListTypeHandler.class);
     }
 }
