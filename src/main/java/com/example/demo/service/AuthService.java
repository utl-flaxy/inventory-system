package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // ユーザー登録
    public User register(RegisterRequest request) {

        System.out.println("register開始");

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("ユーザー名は既に使用されています");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();

        System.out.println("register完了");

        return userRepository.save(user);
    }

    // ログイン
    public LoginResponse login(LoginRequest request) {

        System.out.println("① login開始");

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("ユーザーが存在しません"));

        System.out.println("② user取得");

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            System.out.println("パスワードNG");

            throw new RuntimeException("パスワードが違います");
        }

        System.out.println("③ パスワードOK");

        // JWT生成
        String token = jwtUtil.generateToken(user.getUsername());

        System.out.println("④ token生成");

        LoginResponse response = new LoginResponse(token);

        System.out.println("⑤ response生成");

        return response;
    }
}