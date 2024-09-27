package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.ClientClient;
import com.example.LukeriaFrontendApplication.config.ClientUserClient;
import com.example.LukeriaFrontendApplication.config.UserClient;
import com.example.LukeriaFrontendApplication.dtos.ClientDTO;
import com.example.LukeriaFrontendApplication.dtos.ClientUserDTO;
import com.example.LukeriaFrontendApplication.dtos.ClientUserHelperDTO;
import com.example.LukeriaFrontendApplication.dtos.UserDTO;
import com.example.LukeriaFrontendApplication.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/client-user")
public class ClientUserController {
    private static final String SESSION_TOKEN = "sessionToken";
    private static final String REDIRECTTXT = "redirect:/client-user/show";
    private final ClientUserClient clientUserClient;
    private final ClientClient clientClient;
    private final UserClient userClient;

    @GetMapping("/show")
    public String index(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        List<ClientUserHelperDTO>allClientUsers=new ArrayList<>();
        List<ClientUserDTO> clientUsers = clientUserClient.getAllClientUsers(token);
        for (ClientUserDTO clientUser: clientUsers) {
            ClientDTO client = clientClient.getClientById(clientUser.getClientId(), token);
            UserDTO user = userClient.getUserById(clientUser.getUserId(), token);
            allClientUsers.add(new ClientUserHelperDTO(clientUser.getId(), client, user));
        }
        model.addAttribute("clientUsers", allClientUsers);
        return "ClientUser/show";
    }

    @GetMapping("/show/{id}")
    public String getClientUserById(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        ClientUserDTO clientUser = clientUserClient.getClientUser(id, token);
        model.addAttribute("clientUser", clientUser);
        return "ClientUser/showById";
    }

    @GetMapping("/create")
    public String createClientUser(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        ClientUserDTO clientUser = new ClientUserDTO();
        List<ClientDTO> clients = clientClient.getAllClients(token);
        List<UserDTO> users = userClient.getAllUsers(token);

        List<UserDTO> customers = users.stream()
                .filter(user -> user.getRole() == Role.CUSTOMER)
                .collect(Collectors.toList());

        model.addAttribute("clients", clients);
        model.addAttribute("clientUser", clientUser);
        model.addAttribute("customers", customers);

        return "ClientUser/create";
    }


    @GetMapping("/edit/{id}")
    public String editClientUser(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        List<ClientDTO> clients = clientClient.getAllClients(token);
        List<UserDTO> users = userClient.getAllUsers(token);
        ClientUserDTO existingClientUser = clientUserClient.getClientUser(id, token);
        List<UserDTO> customers = users.stream()
                .filter(user -> user.getRole() == Role.CUSTOMER)
                .collect(Collectors.toList());

        model.addAttribute("clients", clients);
        model.addAttribute("customers", customers);
        model.addAttribute("clientUser", existingClientUser);
        return "ClientUser/edit";
    }

    @PostMapping("/submit")
    public ModelAndView submitClientUser(@ModelAttribute("clientUser") ClientUserDTO clientUserDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        clientUserClient.createClientUser(clientUserDTO, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/edit/{id}")
    public ModelAndView editClientUser(@PathVariable(name = "id") Long id, ClientUserDTO clientUserDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        clientUserClient.updateClientUser(id, clientUserDTO, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/delete/{id}")
    public ModelAndView deleteClientUserById(@PathVariable("id") Long id, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        clientUserClient.deleteClientUser(id, token);
        return new ModelAndView(REDIRECTTXT);
    }
}
