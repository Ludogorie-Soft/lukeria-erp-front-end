package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.UserClient;
import com.example.LukeriaFrontendApplication.dtos.UserDTO;
import com.example.LukeriaFrontendApplication.models.User;
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
    private final UserClient userClient;
    private static final String REDIRECTTXT = "redirect:/user/show";


    @GetMapping("/show")
    public String index(Model model) {
        List<UserDTO> users = userClient.getAllUsers();
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
    public ModelAndView submitUser(@ModelAttribute("user") User user) {
        userClient.createUser(user);
        return new ModelAndView(REDIRECTTXT);
    }

    @GetMapping("/editUser/{id}")
    String editUser(@PathVariable(name = "id") Long id, Model model) {
        UserDTO existingCarton = userClient.getUserById(id);
        model.addAttribute("user", existingCarton);
        return "User/edit";
    }
    @GetMapping("/edit/{id}")
    ModelAndView editSubmitUser(@PathVariable(name = "id") Long id, UserDTO userDTO) {
        userClient.updateUser(id, userDTO);
        return new ModelAndView(REDIRECTTXT);
    }
    @PostMapping("/delete/{id}")
    ModelAndView deleteClientById(@PathVariable("id") Long id, Model model) {
        userClient.deleteUserById(id);
        return new ModelAndView(REDIRECTTXT);
    }

}
