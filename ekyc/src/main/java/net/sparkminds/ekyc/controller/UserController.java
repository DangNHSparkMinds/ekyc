package net.sparkminds.ekyc.controller;

import lombok.RequiredArgsConstructor;
import net.sparkminds.ekyc.service.UserService;
import net.sparkminds.ekyc.service.dto.response.UserDetailsResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/public/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/details")
    public ResponseEntity<?> getUserDetails() {
        UserDetailsResponseDto responseDTO = userService.getUserDetails();
        return ResponseEntity.ok(responseDTO);
    }
}
