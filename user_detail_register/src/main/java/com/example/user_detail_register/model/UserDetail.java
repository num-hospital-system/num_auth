package com.example.user_detail_register.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_details")
public class UserDetail {
    
    @Id
    private String id;
    
    private String authUserId;      // Auth системийн user ID
    private String firstName;       // Нэр
    private String lastName;        // Овог
    private String registerNumber;  // Регистр
    private String gender;          // Хүйс (автоматаар тооцогдоно)
    private int age;                // Нас (автоматаар тооцогдоно)
    private int birthYear;          // Төрсөн он (автоматаар тооцогдоно)
    private String university;      // Аль сургууль
    private int courseYear;         // Хэддүгээр курс
    private String phoneNumber;     // Утас
    
    private Date createdAt;
    private Date updatedAt;
} 