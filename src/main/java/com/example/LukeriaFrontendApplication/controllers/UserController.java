package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.UserClient;
import com.example.LukeriaFrontendApplication.dtos.UserDTO;
import com.example.LukeriaFrontendApplication.models.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {
    private static final String REDIRECTTXT = "redirect:/user/show";
    private static final String SESSION_TOKEN="sessionToken";

    private final UserClient userClient;

    @GetMapping("/show")
    public String index(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        List<UserDTO> users = userClient.getAllUsers(token);
        model.addAttribute("users", users);
        return "User/show";
    }

    @GetMapping("/create")
    String createUser(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "User/create";
    }

    @PostMapping("/submit")
    public ModelAndView submitUser(@ModelAttribute("user") UserDTO user, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        userClient.createUser(user, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/editUser/{id}")
    String editUser(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        UserDTO existingCarton = userClient.getUserById(id, token);
        model.addAttribute("user", existingCarton);
        return "User/edit";
    }

    @GetMapping("/edit/{id}")
    ModelAndView editSubmitUser(@PathVariable(name = "id") Long id, UserDTO userDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        userClient.updateUser(id, userDTO, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/delete/{id}")
    ModelAndView deleteClientById(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        userClient.deleteUserById(id, token);
        return new ModelAndView(REDIRECTTXT);
    }

}
