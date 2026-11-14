package ru.practicum.microbank.transfer.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.practicum.microbank.transfer.dto.TransferRequest;

@RestController
@RequestMapping("/")
@Slf4j
public class TransferController {

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER') && hasAuthority('transfer.access')")
    public String transfer(@RequestBody TransferRequest request) {
        return "OK";
    }
}
