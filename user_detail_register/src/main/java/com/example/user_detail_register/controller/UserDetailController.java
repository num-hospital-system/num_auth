package com.example.user_detail_register.controller;

import com.example.user_detail_register.dto.UserDetailDto;
import com.example.user_detail_register.model.UserDetail;
import com.example.user_detail_register.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/user-details")
@RequiredArgsConstructor
public class UserDetailController {

    private final UserDetailService userDetailService;

    @PostMapping
    public ResponseEntity<?> createUserDetail(@RequestBody UserDetailDto userDetailDto) {
        try {
            // DTO-г модель рүү хөрвүүлэх
            UserDetail userDetail = UserDetail.builder()
                    .sisiId(userDetailDto.getSisiId())
                    .firstName(userDetailDto.getFirstName())
                    .lastName(userDetailDto.getLastName())
                    .registerNumber(userDetailDto.getRegisterNumber())
                    .university(userDetailDto.getUniversity())
                    .courseYear(userDetailDto.getCourseYear())
                    .phoneNumber(userDetailDto.getPhoneNumber())
                    .build();
            
            // Хэрэглэгчийн мэдээлэл үүсгэх
            UserDetail createdUserDetail = userDetailService.createUserDetail(userDetail);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUserDetail);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping("/sisi-user/{sisiId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getUserBySisiId(@PathVariable String sisiId) {
        Optional<UserDetail> userDetail = userDetailService.getUserDetailBySisiId(sisiId);
        
        if (userDetail.isPresent()) {
            return ResponseEntity.ok(userDetail.get());
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Хэрэглэгч олдсонгүй");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping("/register/{registerNumber}")
    public ResponseEntity<?> getUserByRegisterNumber(@PathVariable String registerNumber) {
        Optional<UserDetail> userDetail = userDetailService.getUserDetailByRegisterNumber(registerNumber);
        
        if (userDetail.isPresent()) {
            return ResponseEntity.ok(userDetail.get());
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Хэрэглэгч олдсонгүй");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserDetail(@PathVariable String id, @RequestBody UserDetailDto userDetailDto) {
        try {
            // DTO-г модель рүү хөрвүүлэх
            UserDetail userDetail = UserDetail.builder()
                    .firstName(userDetailDto.getFirstName())
                    .lastName(userDetailDto.getLastName())
                    .registerNumber(userDetailDto.getRegisterNumber())
                    .university(userDetailDto.getUniversity())
                    .courseYear(userDetailDto.getCourseYear())
                    .phoneNumber(userDetailDto.getPhoneNumber())
                    .build();
            
            UserDetail updatedUserDetail = userDetailService.updateUserDetail(id, userDetail);
            return ResponseEntity.ok(updatedUserDetail);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
} 