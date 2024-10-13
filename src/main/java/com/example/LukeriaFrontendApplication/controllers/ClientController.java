package com.example.LukeriaFrontendApplication.controllers;

import com.example.LukeriaFrontendApplication.config.ClientClient;
import com.example.LukeriaFrontendApplication.config.ClientUserClient;
import com.example.LukeriaFrontendApplication.config.UserClient;
import com.example.LukeriaFrontendApplication.dtos.ClientDTO;
import com.example.LukeriaFrontendApplication.dtos.ClientUserDTO;
import com.example.LukeriaFrontendApplication.dtos.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/client")
public class ClientController {
    private static final String CLIENT = "client";
    private static final String SESSION_TOKEN = "sessionToken";

    private static final String REDIRECTTXT = "redirect:/client/show";
    private final ClientClient clientClient;
    private final UserClient userClient;
    private final ClientUserClient clientUserClient;

    @GetMapping("/show")
    public String index(Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);

        List<ClientDTO> clients = clientClient.getAllClients(token);
        List<UserDTO> users = userClient.getAllUsers(token);
        List<ClientUserDTO> clientUsers = clientUserClient.getAllClientUsers(token);

        Map<Long, Long> clientUserMap = new HashMap<>();
        for (ClientUserDTO clientUser : clientUsers) {
            clientUserMap.put(clientUser.getUserId(), clientUser.getClientId());
        }

        for (ClientDTO clientDTO : clients) {
            Long associatedUserId = clientUserMap.entrySet()
                    .stream()
                    .filter(entry -> entry.getValue().equals(clientDTO.getId()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            clientDTO.setCustomer(associatedUserId);
        }

        model.addAttribute("deleteMessageBoolean", true);
        model.addAttribute("clients", clients);
        model.addAttribute("users", users);

        return "Client/show";
    }

    @GetMapping("/show/{id}")
    public String getClientById(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        ClientDTO client = clientClient.getClientById(id, token);

        List<ClientUserDTO> userClientDtoList = clientUserClient.getAllClientUsers(token);

        Long userId = userClientDtoList.stream()
                .filter(clientUserDTO -> clientUserDTO.getClientId().equals(client.getId()))
                .map(ClientUserDTO::getUserId)
                .findFirst().orElse(null);

        if (userId != null) {
            UserDTO userDTO = userClient.getUserById(userId, token);
            model.addAttribute("user", userDTO);
        }

        model.addAttribute(CLIENT, client);

        return "Client/showById";
    }


    @GetMapping("/create")
    String createClient(Model model) {
        ClientDTO client = new ClientDTO();
        model.addAttribute(CLIENT, client);
        return "Client/create";
    }

    @GetMapping("/editClient/{id}")
    String editClient(@PathVariable(name = "id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        ClientDTO existingClient = clientClient.getClientById(id, token);
        model.addAttribute(CLIENT, existingClient);
        return "Client/edit";
    }

    @PostMapping("/submit")
    public ModelAndView submitClient(@ModelAttribute("client") ClientDTO clientDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        if (Objects.equals(clientDTO.getBusinessName(), "")) {
            clientDTO.setBusinessName(clientDTO.getEnglishBusinessName());
            clientDTO.setAddress(clientDTO.getEnglishAddress());
            clientDTO.setMol(clientDTO.getEnglishMol());
        } else {
            clientDTO.setHasIdNumDDS(true);
        }
        clientClient.createClient(clientDTO, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/edit/{id}")
    ModelAndView editClient(@PathVariable(name = "id") Long id, ClientDTO clientDTO, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        clientClient.updateClient(id, clientDTO, token);
        return new ModelAndView(REDIRECTTXT);
    }

    @PostMapping("/delete/{id}")
    ModelAndView deleteClientById(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        clientClient.deleteClientById(id, token);
        return new ModelAndView(REDIRECTTXT);
    }
    @PostMapping("/deleteByUserAndClient/{userID}/{clientId}")
    ModelAndView deleteClientUser(@PathVariable Long userID, @PathVariable Long clientId,  Model model, HttpServletRequest request) {
        String token = (String) request.getSession().getAttribute(SESSION_TOKEN);
        clientUserClient.deleteClientUser(userID,clientId,token);
        ClientDTO client = clientClient.getClientById(clientId, token);

        List<ClientUserDTO> userClientDtoList = clientUserClient.getAllClientUsers(token);

        Long userId = userClientDtoList.stream()
                .filter(clientUserDTO -> clientUserDTO.getClientId().equals(client.getId()))
                .map(ClientUserDTO::getUserId)
                .findFirst().orElse(null);

        if (userId != null) {
            UserDTO userDTO = userClient.getUserById(userId, token);
            model.addAttribute("user", userDTO);
        }

        model.addAttribute(CLIENT, client);

        return new ModelAndView("redirect:/client/show/"+clientId);
    }

}

