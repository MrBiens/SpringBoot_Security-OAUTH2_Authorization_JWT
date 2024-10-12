package com.vn.sbit.idenfity_service.service;

import com.vn.sbit.idenfity_service.EnumRoles.Role;
import com.vn.sbit.idenfity_service.dto.request.UserCreationRequest;
import com.vn.sbit.idenfity_service.dto.request.UserUpdateRequest;
import com.vn.sbit.idenfity_service.dto.response.UserResponse;
import com.vn.sbit.idenfity_service.entity.User;
import com.vn.sbit.idenfity_service.exception.AppException;
import com.vn.sbit.idenfity_service.exception.ErrorCode;
import com.vn.sbit.idenfity_service.mapper.UserMapper;
import com.vn.sbit.idenfity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor   //sẽ tự động Injection dependency mà không ần @Autowired
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
//@FielDefaults(level=AccessLevel.PRIVATE, makeFilnal = true) -- những attribute nào không khai báo sẽ mặc định là private final NameAttribute;
public class UserService {
     UserRepository userRepository;

     UserMapper userMapper;

     PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request){
        if(userRepository.existsByUserName(request.getUserName())
        ) throw new AppException(ErrorCode.USER_EXISTED);

//        Builder - tham số nhất định ( thay thế constructor)
//        UserCreationRequest userCreationRequest = UserCreationRequest.builder().userName("").firstName("").lastName("").build();
        User user = userMapper.toUser(request);

        user.setPassWord(passwordEncoder.encode(request.getPassWord()));

        HashSet<String> roles= new HashSet<>();//Hash hơn Set ở điểm có sẵn các method và dùng hàm băm nên nhanh hơn.
        roles.add(Role.USER.name());
        user.setRoles(roles);

        return userMapper.toUserResponse(userRepository.save(user));
    }


    public List<UserResponse> getUsers() throws Exception {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();

        //   List<User> users = userRepository.findAll();
        //        List<UserResponse> userResponses = users.stream()
        //                .map(user -> userMapper.toUserResponse(user))
        //                .toList();
        //        return userResponses;
    }
    public UserResponse getUser(String id){
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(()->new  RuntimeException("User not found")
        ));
    }

    public UserResponse updateUser(String userId,UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(()->new  RuntimeException("User not found"));
        userMapper.updateUser(user,request);
        return  userMapper.toUserResponse(userRepository.save(user));
    }
    public void deleteUser(String userId){
        userRepository.deleteById(userId);

    }
}
