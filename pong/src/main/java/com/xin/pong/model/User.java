package com.xin.pong.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private Long id;
    private String username;

    public User(String username) {
        this.username = username;
    }
}
